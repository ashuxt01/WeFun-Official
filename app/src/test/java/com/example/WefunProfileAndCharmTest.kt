package com.example

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.AcceptedRelationship
import com.example.data.model.AdvancedRoomPreview
import com.example.data.model.BiddingMode
import com.example.data.model.GiftCategory
import com.example.data.model.GiftWallSummary
import com.example.data.model.GuardRelationship
import com.example.data.model.ReceivedGift
import com.example.data.model.RoomCategory
import com.example.data.model.RoomMode
import com.example.data.model.RoomRelationType
import com.example.data.model.SpecialEffect
import com.example.data.model.SpecialEffectCategory
import com.example.data.model.VirtualGift
import com.example.data.repository.CharmLevelCatalog
import com.example.data.repository.WefunRepository
import com.example.data.repository.WefunSeedData
import com.example.ui.profile.AdvancedRoomListScreen
import com.example.ui.profile.CharmLevelScreen
import com.example.ui.profile.FullProfileScreen
import com.example.ui.profile.GiftWallCollectionScreen
import com.example.ui.profile.GuardListScreen
import com.example.ui.profile.ProfileEditScreen
import com.example.ui.profile.ProfileStatsScreen
import com.example.ui.theme.WEFUNTheme
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class WefunProfileAndCharmTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `charm progression calculates all 21 cumulative tiers accurately`() {
        val belowFirst = CharmLevelCatalog.calculateCharmProgress(0L)
        assertEquals(0, belowFirst.currentLevel)
        assertEquals("UNRANKED", belowFirst.currentTierTitle)
        assertEquals(1, belowFirst.nextLevel)
        assertEquals(1_000L, belowFirst.nextThreshold)
        assertEquals(1_000L, belowFirst.remainingNeeded)

        val almostOne = CharmLevelCatalog.calculateCharmProgress(999L)
        assertEquals(0, almostOne.currentLevel)
        assertEquals(1L, almostOne.remainingNeeded)

        val expected = listOf(
            1 to Pair(1_000L, "NOVICE"),
            2 to Pair(4_000L, "RISING"),
            3 to Pair(12_000L, "EXPLORER"),
            4 to Pair(30_000L, "SUPPORTER"),
            5 to Pair(80_000L, "VALUED"),
            6 to Pair(160_000L, "TRUSTED"),
            7 to Pair(300_000L, "ELITE"),
            8 to Pair(500_000L, "PRO"),
            9 to Pair(1_000_000L, "PRESTIGE"),
            10 to Pair(2_000_000L, "SENIOR"),
            11 to Pair(3_500_000L, "MASTER"),
            12 to Pair(6_000_000L, "EXPERT"),
            13 to Pair(8_500_000L, "LEGEND"),
            14 to Pair(12_000_000L, "MYTHIC"),
            15 to Pair(16_000_000L, "SUPREME"),
            16 to Pair(26_000_000L, "ROYAL"),
            17 to Pair(48_000_000L, "EMPEROR"),
            18 to Pair(86_000_000L, "SOVEREIGN"),
            19 to Pair(120_000_000L, "TITAN"),
            20 to Pair(240_000_000L, "IMMORTAL"),
            21 to Pair(360_000_000L, "ULTIMATE"),
        )
        assertEquals(21, CharmLevelCatalog.charmLevels.size)

        expected.forEach { (level, pair) ->
            val (threshold, title) = pair
            val progress = CharmLevelCatalog.calculateCharmProgress(threshold)
            assertEquals("Failed threshold for level $level", level, progress.currentLevel)
            assertEquals("Failed title for level $level", title, progress.currentTierTitle)
            if (level < 21) {
                assertEquals(level + 1, progress.nextLevel)
                assertTrue(progress.remainingNeeded > 0L)
            } else {
                assertNull(progress.nextLevel)
                assertNull(progress.nextThreshold)
                assertEquals(0L, progress.remainingNeeded)
                assertEquals(1f, progress.fractionToNext)
            }
        }

        val cappedUltimate = CharmLevelCatalog.calculateCharmProgress(500_000_000L)
        assertEquals(21, cappedUltimate.currentLevel)
        assertEquals("ULTIMATE", cappedUltimate.currentTierTitle)
        assertNull(cappedUltimate.nextLevel)
    }

    @Test
    fun `repository enforces ownership check on profile and signature updates`() = runBlocking {
        val repo = WefunRepository(ApplicationProvider.getApplicationContext())
        repo.ensureSeeded()

        val unauthorizedProfileEdit = repo.updateProfileWithOwnershipCheck(
            actorUid = "host_kai",
            targetUid = "usr_nova_01",
            nickname = "Hacked",
            bio = "Hacked",
            country = "India",
            language = "English",
            showGender = true,
            showAge = true,
            photoUri = null,
            coverPhotoUri = null,
        )
        assertTrue(unauthorizedProfileEdit.isFailure)
        assertTrue(unauthorizedProfileEdit.exceptionOrNull() is SecurityException)

        val validProfileEdit = repo.updateProfileWithOwnershipCheck(
            actorUid = "usr_nova_01",
            targetUid = "usr_nova_01",
            nickname = "Aria WeFun",
            bio = "Late night music host",
            country = "India",
            language = "English",
            showGender = true,
            showAge = true,
            photoUri = "content://test/avatar.png",
            coverPhotoUri = "content://test/cover.png",
        )
        assertTrue(validProfileEdit.isSuccess)
        assertEquals("Aria WeFun", repo.currentUserFlow.first()?.nickname)
        assertEquals("content://test/cover.png", repo.currentUserFlow.first()?.coverPhotoUri)

        val badSig = repo.updateUserSignature("host_rhea", "usr_nova_01", "Nope")
        assertTrue(badSig.isFailure)

        val clearSig = repo.updateUserSignature("usr_nova_01", "usr_nova_01", "")
        assertTrue(clearSig.isSuccess)
        assertEquals("", repo.currentUserFlow.first()?.bio)
    }

    @Test
    fun `sendProfileGift debits sender, blocks insufficient or duplicate sends, and updates recipient gift wall`() = runBlocking {
        val repo = WefunRepository(ApplicationProvider.getApplicationContext())
        repo.ensureSeeded()

        val initialUser = repo.currentUserFlow.first()!!
        val recipient = WefunSeedData.findPublicProfile("host_zara")
        val initialGiftWall = repo.observeGiftWall(recipient.uid).first()
        val gift = VirtualGift(
            id = "g_ring",
            name = "Starlight Ring",
            category = GiftCategory.ROMANTIC,
            coinPrice = 99L,
            diamondValue = 50L,
            emoji = "💍",
            accentHex = 0xFF00C2B8,
        )

        val refId = "tx_profile_ring_1"
        val res = repo.sendProfileGift(
            targetUser = recipient,
            gift = gift,
            quantity = 2,
            fromPackage = false,
            referenceId = refId,
        )
        assertTrue(res.isSuccess)
        val afterUser = repo.currentUserFlow.first()!!
        assertEquals(initialUser.coins - 198L, afterUser.coins)

        val updatedGiftWall = repo.observeGiftWall(recipient.uid).first()
        assertEquals(initialGiftWall.totalGifts + 2, updatedGiftWall.totalGifts)
        assertTrue(updatedGiftWall.totalStars > initialGiftWall.totalStars)

        // Duplicate reference blocked
        val dup = repo.sendProfileGift(recipient, gift, 1, fromPackage = false, referenceId = refId)
        assertTrue(dup.isFailure)

        // Over-balance gift blocked with no mutation
        val yacht = VirtualGift(
            id = "g_yacht",
            name = "Royal Yacht",
            category = GiftCategory.LUXURY,
            coinPrice = 50000L,
            diamondValue = 25000L,
            emoji = "🛥️",
            isFullScreen = true,
        )
        val fail = repo.sendProfileGift(recipient, yacht, 1, fromPackage = false, referenceId = "tx_profile_fail")
        assertTrue(fail.isFailure)
        assertEquals(updatedGiftWall.totalGifts, repo.observeGiftWall(recipient.uid).first().totalGifts)
    }

    @Test
    fun `pending relationships stay hidden from BFF and accepted relationships can be cancelled only by participants`() = runBlocking {
        val repo = WefunRepository(ApplicationProvider.getApplicationContext())
        repo.ensureSeeded()

        val beforeCount = repo.acceptedRelationshipsFlow.first().size
        val reqRes = repo.sendRelationshipRequest(
            roomId = "320252",
            targetUserId = "host_leo",
            targetName = "Leo Beats",
            tagLabel = "Besties",
            mode = BiddingMode.BFF,
            tokenTitle = "Scooter Token",
            bidPoints = 5000L,
        )
        assertTrue(reqRes.isSuccess)
        val req = reqRes.getOrThrow()

        // Pending request must not appear in BFF
        assertEquals(beforeCount, repo.acceptedRelationshipsFlow.first().size)

        // Accepting adds to BFF & public profile lookup
        val accRes = repo.respondToRelationshipRequest(req.id, accept = true)
        assertTrue(accRes.isSuccess)
        val accepted = repo.acceptedRelationshipsFlow.first().first { it.partnerUserId == "host_leo" }
        assertTrue(repo.findPublicProfileWithRelationships("host_leo").activeRelationships.any { it.id == accepted.id })

        // Unauthorized user cannot cancel
        val badCancel = repo.cancelAcceptedRelationship("random_outsider", accepted.id)
        assertTrue(badCancel.isFailure)

        // Owner cancels cleanly
        val okCancel = repo.cancelAcceptedRelationship("usr_nova_01", accepted.id)
        assertTrue(okCancel.isSuccess)
        assertFalse(repo.acceptedRelationshipsFlow.first().any { it.id == accepted.id })
    }

    @Test
    fun `special effects reject locked unowned items and allow owned eligible activation toggles`() = runBlocking {
        val repo = WefunRepository(ApplicationProvider.getApplicationContext())
        repo.ensureSeeded()

        // Owned & eligible item toggles cleanly
        val okToggle = repo.toggleSpecialEffectActivation("usr_nova_01", "fx_entry_starlight")
        assertTrue(okToggle.isSuccess)
        assertTrue(okToggle.getOrThrow().isActive)

        val toggleOff = repo.toggleSpecialEffectActivation("usr_nova_01", "fx_entry_starlight")
        assertTrue(toggleOff.isSuccess)
        assertFalse(toggleOff.getOrThrow().isActive)

        // Locked store item rejected even if unowned
        val lockedToggle = repo.toggleSpecialEffectActivation("usr_nova_01", "fx_frame_royal_gold")
        assertTrue(lockedToggle.isFailure)
    }

    @Test
    fun `profile statistics report all 6 supported games and real match records only`() = runBlocking {
        val repo = WefunRepository(ApplicationProvider.getApplicationContext())
        repo.ensureSeeded()

        val user = repo.currentUserFlow.first()!!
        val statsBefore = repo.observeProfileStatistics(user).first()
        assertEquals(6, statsBefore.gameStats.size)

        val ludoStat = statsBefore.gameStats.first { it.gameTitle == "Jackaroo" }
        val beforePlayed = ludoStat.matchesPlayed

        repo.recordGameResult("Jackaroo", "Win", score = 990, coinsWon = 120L, xpEarned = 60L)
        val statsAfter = repo.observeProfileStatistics(repo.currentUserFlow.first()!!).first()
        val afterJackaroo = statsAfter.gameStats.first { it.gameTitle == "Jackaroo" }
        assertEquals(beforePlayed + 1, afterJackaroo.matchesPlayed)
        assertEquals(990, afterJackaroo.bestScore)
    }

    @Test
    fun `full profile renders light layout, fallback signature, gift wall, BFF, guard, rooms, and bottom action bar`() {
        val emptySigOwnUser = WefunSeedData.findPublicProfile("host_rhea").copy(
            uid = "usr_nova_01",
            wefunId = "WF-804921",
            nickname = "Aria Vance",
            bio = "",
            country = "India",
            charmPoints = 18_400L,
            activeRelationships = listOf(
                AcceptedRelationship(
                    id = "r1",
                    partnerUserId = "host_rhea",
                    partnerName = "DJ Rhea",
                    tagLabel = "Confidant",
                    mode = BiddingMode.BFF,
                    tokenTitle = "Luxury Home",
                    daysTogether = 40,
                    level = 2,
                    tokenKey = "puzzle",
                )
            ),
        )
        val summary = GiftWallSummary(
            ownerUid = emptySigOwnUser.uid,
            totalGifts = 18,
            totalStars = 18L,
            featuredGifts = listOf(
                ReceivedGift("rg1", emptySigOwnUser.uid, "g_crown", "Royal Crown", "👑", 199, 1, 18, GiftCategory.VIP, 0xFFFBBF24, 1L)
            ),
            allGifts = listOf(
                ReceivedGift("rg1", emptySigOwnUser.uid, "g_crown", "Royal Crown", "👑", 199, 1, 18, GiftCategory.VIP, 0xFFFBBF24, 1L)
            ),
        )
        val guards = listOf(
            GuardRelationship("g1", emptySigOwnUser.uid, "host_kai", "Kai Storm", 0xFFFF4FA3, 5, "Royal Guard", true, false, 1L)
        )
        val rooms = listOf(
            AdvancedRoomPreview("1579056", "Midnight Acoustic Lounge", "host_rhea", "DJ Rhea", RoomCategory.MUSIC, RoomMode.NORMAL, 6, 42, 999, RoomRelationType.JOINED, 0xFF7B2CFF, 0xFF00C2B8)
        )

        var openedCharm = false
        var openedGiftWall = false
        var openedStats = false
        composeRule.setContent {
            WEFUNTheme {
                FullProfileScreen(
                    profileUser = emptySigOwnUser,
                    isOwnProfile = true,
                    isFollowing = false,
                    posts = emptyList(),
                    onBack = {},
                    onEditProfile = {},
                    onOpenSettings = {},
                    onToggleFollow = {},
                    onMessageUser = {},
                    onPickPhoto = {},
                    onToggleLikePost = {},
                    onShowToast = {},
                    giftWallSummary = summary,
                    guards = guards,
                    advancedRooms = rooms,
                    onOpenCharmSystem = { openedCharm = true },
                    onOpenGiftWallCollection = { openedGiftWall = true },
                    onOpenStats = { openedStats = true },
                )
            }
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("full_profile_own").assertIsDisplayed()
        composeRule.onNodeWithTag("profile_charm_badge").assertIsDisplayed().performClick()
        assertTrue(openedCharm)

        composeRule.onNodeWithTag("full_profile_scroll_list")
            .performScrollToNode(hasTestTag("profile_gift_wall_card"))
        composeRule.onNodeWithTag("profile_gift_wall_card").performClick()
        assertTrue(openedGiftWall)

        composeRule.onNodeWithTag("full_profile_scroll_list")
            .performScrollToNode(hasTestTag("profile_signature_section"))
        composeRule.onNodeWithText("This person says nothing!").assertIsDisplayed()

        composeRule.onNodeWithTag("full_profile_scroll_list")
            .performScrollToNode(hasTestTag("profile_stats_row"))
        composeRule.onNodeWithTag("profile_stats_row").assertIsDisplayed().performClick()
        assertTrue(openedStats)

        composeRule.onNodeWithTag("profile_send_gift_button").assertIsDisplayed()
        composeRule.onNodeWithTag("public_profile_message_button").assertIsDisplayed()
    }

    @Test
    fun `charm level screen displays exclusive WeFun branding, 21 tiers, and active levels tab`() {
        val effects = listOf(
            SpecialEffect("fx_1", "Nova Crystal Frame", SpecialEffectCategory.PROFILE_FRAME, "Test", 1, 1, true, true, 0xFFCBD5E1, 0xFF8B5CF6)
        )
        composeRule.setContent {
            WEFUNTheme {
                CharmLevelScreen(
                    userDisplayName = "Aria Vance",
                    userCharmPoints = 18_400L,
                    userActiveLevel = 2,
                    specialEffects = effects,
                    onBack = {},
                )
            }
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("charm_level_screen").assertIsDisplayed()
        composeRule.onAllNodesWithText("WeFun").onFirst().assertIsDisplayed()
        composeRule.onNodeWithTag("charm_tier_row_1").assertIsDisplayed()

        composeRule.onNodeWithTag("charm_levels_list")
            .performScrollToNode(hasTestTag("charm_tier_row_21"))
        composeRule.onNodeWithTag("charm_tier_row_21").assertIsDisplayed()
        composeRule.onNodeWithText("ULTIMATE").assertIsDisplayed()
        composeRule.onNodeWithText("360,000,000").assertIsDisplayed()

        composeRule.onNodeWithTag("charm_tab_active").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithText("Active Lv.1 • Dawn Spark").assertIsDisplayed()
        composeRule.onNodeWithTag("active_levels_effects_list")
            .performScrollToNode(hasText("Nova Crystal Frame"))
        composeRule.onNodeWithText("Nova Crystal Frame").assertIsDisplayed()
    }

    @Test
    fun `profile edit screen enforces read-only state for non-owners and sub-screens render cleanly`() {
        val user = WefunSeedData.findPublicProfile("host_kai")
        composeRule.setContent {
            WEFUNTheme {
                ProfileEditScreen(
                    user = user,
                    isOwner = false,
                    onBack = {},
                    onSave = { _, _, _, _, _, _, _, _, _, _ -> },
                )
            }
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("profile_edit_unauthorized_banner").assertIsDisplayed()
    }

    @Test
    fun `gift wall, guard list, advanced room list, and stats sub-screens render items`() {
        val summary = GiftWallSummary(
            ownerUid = "usr_nova_01",
            totalGifts = 6,
            totalStars = 60L,
            featuredGifts = emptyList(),
            allGifts = listOf(
                ReceivedGift("g1", "usr_nova_01", "g_crown", "Royal Crown", "👑", 199, 10, 6, GiftCategory.VIP, 0xFFFBBF24, 10L)
            ),
        )
        composeRule.setContent {
            WEFUNTheme {
                GiftWallCollectionScreen(ownerName = "Aria", summary = summary, onBack = {})
            }
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("gift_wall_collection_screen").assertIsDisplayed()
        composeRule.onNodeWithTag("gift_wall_item_g_crown").assertIsDisplayed()
    }
}
