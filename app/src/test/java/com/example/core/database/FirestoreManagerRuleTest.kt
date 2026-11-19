package com.example.core.database

import com.example.base.FirestoreEmulatorTestBase
import com.google.firebase.firestore.FirebaseFirestoreException
import java.util.UUID
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class FirestoreManagerRuleTest : FirestoreEmulatorTestBase() {

    @Test
    fun `user profile create and read succeeds for owner and blocks cross-user read`() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val manager = FirestoreManager(firestore, auth)

        val saveResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            manager.createOrUpdateUserProfile(
                FirestoreUserProfile(
                    userId = aliceUid,
                    wefunId = "WF-700101",
                    username = "alice_host",
                    nickname = "Alice Host",
                    email = ALICE_EMAIL,
                    coins = 1200L,
                    diamonds = 45L,
                    vipLevel = 2L,
                    userLevel = 12L,
                )
            )
        }
        assertTrue(saveResult.isSuccess)

        val fetched = withTimeout(DEFAULT_TIMEOUT_MS) {
            manager.getUserProfile(aliceUid).getOrThrow()
        }
        assertNotNull(fetched)
        assertEquals("Alice Host", fetched?.nickname)

        signInTestUser(BOB_EMAIL)
        val bobManager = FirestoreManager(firestore, auth)
        val crossRead = withTimeout(DEFAULT_TIMEOUT_MS) {
            bobManager.getUserProfile(aliceUid)
        }
        assertTrue(crossRead.isFailure)
        val firestoreException = crossRead.exceptionOrNull()?.unwrapFirestoreException()
        assertNotNull(firestoreException)
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException?.code)
    }

    @Test
    fun `voice room defaults partner seat OFF and enforces 8 to 14 seat capacity by level`() = runBlocking {
        val aliceUid = signInTestUser(ALICE_EMAIL)
        val manager = FirestoreManager(firestore, auth)
        val roomId = "room_${UUID.randomUUID().toString().replace("-", "").take(10)}"

        val createResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            manager.createVoiceRoom(
                FirestoreVoiceRoom(
                    id = roomId,
                    ownerId = aliceUid,
                    memberUids = listOf(aliceUid),
                    title = "Evening Vibes",
                    description = "8-seat party",
                    category = "Friends",
                    hostName = "Alice Host",
                    roomLevel = 1L,
                    maxSeats = 8L,
                    isPartnerSeatEnabled = false,
                )
            )
        }
        assertTrue(createResult.isSuccess)

        val initialRoom = withTimeout(DEFAULT_TIMEOUT_MS) {
            manager.getVoiceRoom(roomId).getOrThrow()
        }
        assertNotNull(initialRoom)
        assertEquals(8L, initialRoom?.maxSeats)
        assertFalse(initialRoom?.isPartnerSeatEnabled ?: true)

        val togglePartnerResult = withTimeout(DEFAULT_TIMEOUT_MS) {
            manager.updatePartnerSeatConfig(roomId, enabled = true)
        }
        assertTrue(togglePartnerResult.isSuccess)

        val upgradeLv7Result = withTimeout(DEFAULT_TIMEOUT_MS) {
            manager.updateRoomSeatCapacity(roomId, roomLevel = 7L, maxSeats = 14L)
        }
        assertTrue(upgradeLv7Result.isSuccess)

        val observedRooms = withTimeout(FLOW_TIMEOUT_MS) {
            manager.observeAccessibleRooms(aliceUid).first { rooms ->
                rooms.any { it.id == roomId && it.maxSeats == 14L && it.isPartnerSeatEnabled }
            }
        }
        assertTrue(observedRooms.any { it.id == roomId })
    }

    @Test
    fun `unauthenticated observer fails with permission denied`() = runBlocking {
        auth.signOut()
        val manager = FirestoreManager(firestore, auth)

        var caught: Throwable? = null
        try {
            withTimeout(FLOW_TIMEOUT_MS) {
                manager.observeAccessibleRooms("unauth_uid").first()
            }
        } catch (t: Throwable) {
            caught = t
        }
        assertNotNull(caught)
        val firestoreException = caught?.unwrapFirestoreException()
        assertNotNull("Expected nested FirebaseFirestoreException, got $caught", firestoreException)
        assertEquals(FirebaseFirestoreException.Code.PERMISSION_DENIED, firestoreException?.code)
    }

    private companion object {
        const val ALICE_EMAIL = "alice_wefun@test.com"
        const val BOB_EMAIL = "bob_wefun@test.com"
        const val DEFAULT_TIMEOUT_MS = 5000L
        const val FLOW_TIMEOUT_MS = 4000L
    }
}
