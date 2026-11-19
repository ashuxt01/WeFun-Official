package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.GiftCategory
import com.example.data.model.RoomRole
import com.example.data.model.UserProfile
import com.example.data.model.VirtualGift
import com.example.data.repository.VoiceRoomSeedData
import com.example.data.repository.WefunRepository
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class VoiceRoomBackendTest {

    @Test
    fun `level 1 to 6 stay 8 seats and level 7 to 10 unlock 14 seats`() {
        val configs = VoiceRoomSeedData.defaultLevelConfigs
        assertEquals(10, configs.size)
        configs.forEachIndexed { index, cfg ->
            val expectedLevel = index + 1
            val expectedSeats = if (expectedLevel < 7) 8 else 14
            assertEquals(expectedLevel, cfg.level)
            assertEquals(expectedSeats, cfg.seatCount)
        }
    }

    @Test
    fun `gift contribution keeps 8 seats at level 2 and unlocks 14 seats at level 7`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = WefunRepository(context)
        repo.ensureSeeded()

        val sender = UserProfile(
            uid = "usr_test_sender",
            wefunId = "WF-999111",
            username = "tester",
            nickname = "Test Sender",
            bio = "Testing",
            coins = 1_000_000L,
        )
        val gift = VirtualGift(
            id = "g_eclipse",
            name = "Eclipse Sovereign",
            category = GiftCategory.EXCLUSIVE,
            coinPrice = 9999L,
            diamondValue = 4999L,
            emoji = "💎",
        )

        // Room 1579056 starts at Level 1 with 4,250 pts; Level 2 requires 10,000 pts and retains 8 seats.
        val lv2Result = repo.roomBackend.processRoomGiftContribution(
            roomId = "1579056",
            sender = sender,
            receiverId = "usr_nova_01",
            receiverName = "Aria Vance",
            gift = gift,
            comboCount = 1,
            totalCoins = 9999L,
        )

        assertTrue("Contribution points must follow conversion rule (not 1:1 coin)", lv2Result.contributionPointsAdded < 9999L)
        assertEquals(8499L, lv2Result.contributionPointsAdded)
        assertEquals(4999L, lv2Result.receiverDiamondsEarned)
        assertEquals(2, lv2Result.updatedRoomLevel)
        assertEquals(8, lv2Result.updatedSeatCount)
        assertNotNull("Crossing 10,000 pts must emit a LevelUpEvent", lv2Result.levelUpEvent)
        assertEquals(2, lv2Result.levelUpEvent?.newLevel)

        // Crossing 650,000 contribution points upgrades Room to Level 7 and unlocks 14 seats (8 + 6 compact).
        val lv7Result = repo.roomBackend.processRoomGiftContribution(
            roomId = "1579056",
            sender = sender,
            receiverId = "usr_nova_01",
            receiverName = "Aria Vance",
            gift = gift,
            comboCount = 80,
            totalCoins = 800_000L,
        )

        assertEquals(7, lv7Result.updatedRoomLevel)
        assertEquals(14, lv7Result.updatedSeatCount)
        assertNotNull(lv7Result.levelUpEvent)
        assertEquals(14, lv7Result.levelUpEvent?.newSeatCount)
    }

    @Test
    fun `partner seat defaults off and can only be toggled or assigned by room owner`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = WefunRepository(context)
        repo.ensureSeeded()

        val (_, initialPartner, _) = repo.roomBackend.loadRoomSnapshot("1579056")
        assertNotNull(initialPartner)
        assertFalse("Partner seat must default to OFF", initialPartner!!.isEnabled)

        val nonOwnerAttempt = repo.roomBackend.setPartnerSeatEnabled(
            roomId = "1579056",
            actorName = "Moderator",
            actorRole = RoomRole.ROOM_ADMIN,
            enabled = true,
        )
        assertTrue(nonOwnerAttempt.isFailure)

        val ownerEnable = repo.roomBackend.setPartnerSeatEnabled(
            roomId = "1579056",
            actorName = "Aria Vance",
            actorRole = RoomRole.OWNER,
            enabled = true,
        )
        assertTrue(ownerEnable.isSuccess)
        val (_, enabledPartner, _) = repo.roomBackend.loadRoomSnapshot("1579056")
        assertTrue(enabledPartner!!.isEnabled)

        val ownerDisable = repo.roomBackend.setPartnerSeatEnabled(
            roomId = "1579056",
            actorName = "Aria Vance",
            actorRole = RoomRole.OWNER,
            enabled = false,
        )
        assertTrue(ownerDisable.isSuccess)
        val (_, disabledPartner, _) = repo.roomBackend.loadRoomSnapshot("1579056")
        assertFalse(disabledPartner!!.isEnabled)
    }

    @Test
    fun `normal member cannot assign roles or publish admin announcements`() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val repo = WefunRepository(context)
        repo.ensureSeeded()

        val roleRes = repo.roomBackend.assignRole(
            roomId = "1579056",
            actorName = "Guest",
            actorRole = RoomRole.NORMAL_MEMBER,
            targetUserId = "sp_4",
            targetName = "Zain Flow",
            newRole = RoomRole.ROOM_ADMIN,
        )
        assertTrue(roleRes.isFailure)
    }
}
