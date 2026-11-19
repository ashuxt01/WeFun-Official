const {
  initializeTestEnvironment,
  assertFails,
  assertSucceeds,
} = require("@firebase/rules-unit-testing");
const { test, before, after, beforeEach } = require("node:test");
const fs = require("node:fs");

let testEnv;
const PROJECT_ID = process.env.GCP_PROJECT || "demo-no-project";
const ALICE_UID = "alice_123";
const BOB_UID = "bob_456";
const CHARLIE_UID = "charlie_789";

const [emulatorHost, emulatorPortStr] = (
  process.env.FIRESTORE_EMULATOR_HOST || "127.0.0.1:8085"
).split(":");
const emulatorPort = parseInt(emulatorPortStr, 10);

function serverTs() {
  return new Date(Date.now() - 1000);
}

before(async () => {
  const rules = fs.readFileSync("./firestore.rules", "utf8");
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules,
      host: emulatorHost,
      port: emulatorPort,
    },
  });
});

after(async () => {
  if (testEnv) {
    await testEnv.cleanup();
  }
});

beforeEach(async () => {
  if (testEnv) {
    await testEnv.clearFirestore();
  }
});

test("Unauthenticated user: cannot read or write users or voice_rooms", async () => {
  const unauthDb = testEnv.unauthenticatedContext().firestore();
  await assertFails(unauthDb.collection("users").get());
  await assertFails(unauthDb.collection("users").doc(ALICE_UID).get());
  await assertFails(unauthDb.collection("voice_rooms").get());
  await assertFails(
    unauthDb.collection("voice_rooms").doc("room_1").set({
      ownerId: ALICE_UID,
      memberUids: [ALICE_UID],
      title: "Hack",
      category: "Friends",
      roomLevel: 1,
      maxSeats: 8,
      isPartnerSeatEnabled: false,
      createdAt: serverTs(),
      updatedAt: serverTs(),
    })
  );
});

test("Users collection: owner can create and read own profile; cross-user PII access fails", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      wefunId: "WF-100001",
      username: "alice",
      nickname: "Alice",
      email: "alice@example.com",
      coins: 500,
      diamonds: 0,
      vipLevel: 1,
      userLevel: 5,
      createdAt: serverTs(),
      updatedAt: serverTs(),
    })
  );

  await assertSucceeds(aliceDb.collection("users").doc(ALICE_UID).get());
  await assertFails(bobDb.collection("users").doc(ALICE_UID).get());
  await assertFails(bobDb.collection("users").get());
  await assertFails(
    bobDb.collection("users").doc(ALICE_UID).update({
      nickname: "Hacked",
      updatedAt: serverTs(),
    })
  );
});

test("Users collection: rejects shadow fields and immutable createdAt/userId tampering", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();

  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      wefunId: "WF-100001",
      username: "alice",
      nickname: "Alice",
      isVerified: true,
      createdAt: serverTs(),
      updatedAt: serverTs(),
    })
  );

  await assertSucceeds(
    aliceDb.collection("users").doc(ALICE_UID).set({
      userId: ALICE_UID,
      wefunId: "WF-100001",
      username: "alice",
      nickname: "Alice",
      createdAt: serverTs(),
      updatedAt: serverTs(),
    })
  );

  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      userId: BOB_UID,
      updatedAt: serverTs(),
    })
  );

  await assertFails(
    aliceDb.collection("users").doc(ALICE_UID).update({
      createdAt: serverTs(),
      updatedAt: serverTs(),
    })
  );
});

test("Voice rooms: enforces 8-seat cap for Levels 1-6 and unlocks 14 seats at Level 7", async () => {
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();

  // Level 1 room with 10 or 14 seats must fail
  await assertFails(
    aliceDb.collection("voice_rooms").doc("room_bad_cap").set({
      ownerId: ALICE_UID,
      memberUids: [ALICE_UID],
      title: "Level 1 Illegal 14",
      category: "Friends",
      roomLevel: 1,
      maxSeats: 14,
      isPartnerSeatEnabled: false,
      createdAt: serverTs(),
      updatedAt: serverTs(),
    })
  );

  // Level 1 room with 8 seats and Partner OFF succeeds
  await assertSucceeds(
    aliceDb.collection("voice_rooms").doc("room_lv1").set({
      ownerId: ALICE_UID,
      memberUids: [ALICE_UID, BOB_UID],
      title: "Midnight Lounge",
      category: "Friends",
      roomLevel: 1,
      maxSeats: 8,
      isPartnerSeatEnabled: false,
      createdAt: serverTs(),
      updatedAt: serverTs(),
    })
  );

  // Owner upgrades room to Level 7 with 14 seats and enables Partner Seat
  await assertSucceeds(
    aliceDb.collection("voice_rooms").doc("room_lv1").update({
      roomLevel: 7,
      maxSeats: 14,
      isPartnerSeatEnabled: true,
      updatedAt: serverTs(),
    })
  );

  // Attempting to exceed 14 seats fails
  await assertFails(
    aliceDb.collection("voice_rooms").doc("room_lv1").update({
      maxSeats: 16,
      updatedAt: serverTs(),
    })
  );
});

test("Voice rooms: query alignment, member permissions, and non-member isolation", async () => {
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await context.firestore().collection("voice_rooms").doc("room_shared").set({
      ownerId: ALICE_UID,
      memberUids: [ALICE_UID, BOB_UID],
      title: "Alice & Bob Stage",
      category: "Music",
      roomLevel: 3,
      maxSeats: 8,
      isPartnerSeatEnabled: false,
      onlineCount: 2,
      popularity: 100,
      createdAt: serverTs(),
      updatedAt: serverTs(),
    });
  });

  const bobDb = testEnv.authenticatedContext(BOB_UID).firestore();
  const charlieDb = testEnv.authenticatedContext(CHARLIE_UID).firestore();

  // Matching member query succeeds
  await assertSucceeds(
    bobDb.collection("voice_rooms").where("memberUids", "array-contains", BOB_UID).get()
  );

  // Unfiltered list query fails
  await assertFails(bobDb.collection("voice_rooms").get());

  // Non-member cannot read
  await assertFails(charlieDb.collection("voice_rooms").doc("room_shared").get());

  // Member can update activity counters only
  await assertSucceeds(
    bobDb.collection("voice_rooms").doc("room_shared").update({
      onlineCount: 5,
      popularity: 150,
      updatedAt: serverTs(),
    })
  );

  // Member cannot toggle owner's partner seat or rename room
  await assertFails(
    bobDb.collection("voice_rooms").doc("room_shared").update({
      isPartnerSeatEnabled: true,
      updatedAt: serverTs(),
    })
  );

  // Owner shadow field fails
  const aliceDb = testEnv.authenticatedContext(ALICE_UID).firestore();
  await assertFails(
    aliceDb.collection("voice_rooms").doc("room_shared").update({
      ghostField: "bad",
      updatedAt: serverTs(),
    })
  );
});
