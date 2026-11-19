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
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performTextReplacement
import com.example.data.model.DiamondSpendCategory
import com.example.data.model.RoomCategory
import com.example.data.model.UserProfile
import com.example.data.model.VipNicknameColorTier
import com.example.data.model.VipTierConfig
import com.example.data.model.VoiceRoom
import com.example.data.repository.VipCatalog
import com.example.data.repository.VipGrowthEngine
import com.example.data.repository.WefunSeedData
import com.example.ui.home.WefunHomeGamesScreen
import com.example.ui.navigation.BottomNavigationBar
import com.example.ui.navigation.WefunDestination
import com.example.ui.profile.AdminPanelScreen
import com.example.ui.profile.VipCenterScreen
import com.example.ui.theme.WEFUNTheme
import com.example.ui.viewmodel.LiveRoomManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import java.time.ZoneId
import java.time.ZonedDateTime

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class WefunVipAndRedesignTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun `vip growth thresholds match official guide for all 12 levels`() {
        val expectedThresholds = mapOf(
            1 to 1L,
            2 to 1_000L,
            3 to 5_000L,
            4 to 25_000L,
            5 to 60_000L,
            6 to 140_000L,
            7 to 300_000L,
            8 to 600_000L,
            9 to 1_080_000L,
            10 to 2_000_000L,
            11 to 4_800_000L,
            12 to 8_000_000L,
        )
        assertEquals(12, VipCatalog.defaultTiers.size)
        expectedThresholds.forEach { (lvl, threshold) ->
            val cfg = VipCatalog.defaultTiers.first { it.level == lvl }
            assertEquals("Threshold mismatch for VIP $lvl", threshold, cfg.growthThreshold)
            assertEquals(lvl, VipGrowthEngine.resolveLevelForGrowthPoints(threshold))
        }
        assertEquals(0, VipGrowthEngine.resolveLevelForGrowthPoints(0L))
    }

    @Test
    fun `diamond spending grants 1 growth point per 100 diamonds only when vip is active across all 4 categories`() {
        // Inactive VIP earns 0 growth points
        assertEquals(
            0L,
            VipGrowthEngine.calculateGrowthPointsEarned(
                diamondsSpent = 5_000L,
                isVipActive = false,
                category = DiamondSpendCategory.GIFT
            )
        )

        // All 4 eligible spend categories earn 1 pt / 100 diamonds when VIP is active
        val eligible = listOf(
            DiamondSpendCategory.GIFT,
            DiamondSpendCategory.SHOP_LUCKY_DRAW,
            DiamondSpendCategory.EVENT_LUCKY_DRAW,
            DiamondSpendCategory.VOICE_ROOM_BINGO,
        )
        eligible.forEach { cat ->
            assertEquals(
                "Category $cat should yield 3 pts for 350 diamonds",
                3L,
                VipGrowthEngine.calculateGrowthPointsEarned(
                    diamondsSpent = 350L,
                    isVipActive = true,
                    category = cat
                )
            )
        }

        // Ineligible spend category earns 0
        assertEquals(
            0L,
            VipGrowthEngine.calculateGrowthPointsEarned(
                diamondsSpent = 1_000L,
                isVipActive = true,
                category = DiamondSpendCategory.OTHER_EXCLUDED
            )
        )

        // Level progression via processDiamondSpend
        val now = 1_700_000_000_000L
        val outcome = VipGrowthEngine.processDiamondSpend(
            currentGrowthPoints = 995L,
            currentVipLevel = 1,
            isVipActive = true,
            diamondsSpent = 600L, // +6 pts => 1,001 >= 1,000 => VIP 2
            category = DiamondSpendCategory.VOICE_ROOM_BINGO,
            currentRegulationDeadlineMillis = 0L,
            currentRegulationEarnedPoints = 0L,
            nowMillis = now,
        )
        assertEquals(6L, outcome.growthPointsEarned)
        assertEquals(1_001L, outcome.newGrowthPoints)
        assertEquals(1, outcome.levelBefore)
        assertEquals(2, outcome.levelAfter)
        assertTrue(outcome.leveledUp)
        assertTrue(outcome.newRegulationDeadlineMillis > now)
    }

    @Test
    fun `vip membership calendar month duration is accurate to the minute including month end edge cases`() {
        val zone = ZoneId.of("UTC")
        // Official FAQ example: May 20 at 15:24 + 1 month -> June 20 at 15:24
        val may20 = ZonedDateTime.of(2026, 5, 20, 15, 24, 0, 0, zone).toInstant().toEpochMilli()
        val expJune20Millis = VipGrowthEngine.calculateCalendarMonthExpiry(may20, 1, zone)
        val expJune20 = ZonedDateTime.ofInstant(java.time.Instant.ofEpochMilli(expJune20Millis), zone)
        assertEquals(2026, expJune20.year)
        assertEquals(6, expJune20.monthValue)
        assertEquals(20, expJune20.dayOfMonth)
        assertEquals(15, expJune20.hour)
        assertEquals(24, expJune20.minute)

        // Month-end overflow: Jan 31 + 1 month -> Feb 28 (2026 is non-leap year) at same HH:mm
        val jan31 = ZonedDateTime.of(2026, 1, 31, 9, 45, 0, 0, zone).toInstant().toEpochMilli()
        val expFebMillis = VipGrowthEngine.calculateCalendarMonthExpiry(jan31, 1, zone)
        val expFeb = ZonedDateTime.ofInstant(java.time.Instant.ofEpochMilli(expFebMillis), zone)
        assertEquals(2, expFeb.monthValue)
        assertEquals(28, expFeb.dayOfMonth)
        assertEquals(9, expFeb.hour)
        assertEquals(45, expFeb.minute)
    }

    @Test
    fun `vip regulation deducts growth points and downgrades tier when target is missed inside or outside active period`() {
        val dayMs = 86_400_000L
        val start = 1_700_000_000_000L
        val deadline = start + 30 * dayMs

        // Scenario A: VIP 3 user with 5,500 Growth Pts, only 50 relegation pts (< 100 target)
        // Expected: -4,000 deduction => 1,500 Growth Pts => drops to VIP 2 (threshold 1,000)
        val missed = VipGrowthEngine.evaluateRegulation(
            currentGrowthPoints = 5_500L,
            currentVipLevel = 3,
            regulationDeadlineMillis = deadline,
            regulationEarnedPoints = 50L,
            nowMillis = deadline + 1_000L,
        )
        assertTrue(missed.wasEvaluated)
        assertFalse(missed.maintained)
        assertEquals(4_000L, missed.totalDeducted)
        assertEquals(1_500L, missed.newGrowthPoints)
        assertEquals(3, missed.levelBefore)
        assertEquals(2, missed.levelAfter)

        // Scenario B: VIP 3 user meets target (120 >= 100) => 0 deducted, stays VIP 3
        val met = VipGrowthEngine.evaluateRegulation(
            currentGrowthPoints = 5_500L,
            currentVipLevel = 3,
            regulationDeadlineMillis = deadline,
            regulationEarnedPoints = 120L,
            nowMillis = deadline + 1_000L,
        )
        assertTrue(met.wasEvaluated)
        assertTrue(met.maintained)
        assertEquals(0L, met.totalDeducted)
        assertEquals(3, met.levelAfter)
    }

    @Test
    fun `vip privilege tiers, nickname colors, and daily claim window match specification`() {
        assertEquals(VipNicknameColorTier.NONE, VipGrowthEngine.nicknameColorTierForLevel(0))
        assertEquals(VipNicknameColorTier.RED, VipGrowthEngine.nicknameColorTierForLevel(1))
        assertEquals(VipNicknameColorTier.RED, VipGrowthEngine.nicknameColorTierForLevel(5))
        assertEquals(VipNicknameColorTier.NOBLE_GOLD, VipGrowthEngine.nicknameColorTierForLevel(6))
        assertEquals(VipNicknameColorTier.NOBLE_GOLD, VipGrowthEngine.nicknameColorTierForLevel(10))
        assertEquals(VipNicknameColorTier.RAINBOW, VipGrowthEngine.nicknameColorTierForLevel(11))
        assertEquals(VipNicknameColorTier.RAINBOW, VipGrowthEngine.nicknameColorTierForLevel(12))

        // VIP 11 unlocks anti_kickout_anti_leave_mic; VIP 9 unlocks voice_room_bullet_comments; VIP 12 unlocks mute_protection_vip_gift
        assertFalse(VipGrowthEngine.hasPrivilege(8, true, "voice_room_bullet_comments"))
        assertTrue(VipGrowthEngine.hasPrivilege(9, true, "voice_room_bullet_comments"))
        assertFalse(VipGrowthEngine.hasPrivilege(10, true, "anti_kickout_anti_leave_mic"))
        assertTrue(VipGrowthEngine.hasPrivilege(11, true, "anti_kickout_anti_leave_mic"))
        assertTrue(VipGrowthEngine.hasPrivilege(12, true, "mute_protection_vip_gift"))
        // Inactive VIP loses runtime privilege enforcement
        assertFalse(VipGrowthEngine.hasPrivilege(12, false, "mute_protection_vip_gift"))

        // Daily claim eligibility gated by active VIP + epochDay
        val now = System.currentTimeMillis()
        val vipUser = UserProfile(
            uid = "u_vip3",
            wefunId = "WF-300",
            username = "vip3",
            nickname = "RoyalAria",
            bio = "WeFun VIP 3 Member",
            vipLevel = 3,
            vipGrowthPoints = 5_500L,
            vipExpiryAtMillis = now + 86_400_000L,
            vipLastDailyClaimEpochDay = 100L,
        )
        val canClaimToday = VipGrowthEngine.buildMembershipState(vipUser, VipCatalog.defaultTiers, now, todayEpochDay = 101L)
        assertTrue(canClaimToday.canClaimDailyDiamonds)
        assertEquals(60L, canClaimToday.dailyDiamondBonus)

        val alreadyClaimedToday = VipGrowthEngine.buildMembershipState(vipUser, VipCatalog.defaultTiers, now, todayEpochDay = 100L)
        assertFalse(alreadyClaimedToday.canClaimDailyDiamonds)
    }

    @Test
    fun `live room manager enforces vip 9 bullet comments, vip 11 anti kickout, and vip 12 mute protection`() {
        val scope = CoroutineScope(SupervisorJob() + Dispatchers.Unconfined)
        try {
            val manager = LiveRoomManager(scope)
            val host = UserProfile(
                uid = "host_01",
                wefunId = "WF-1",
                username = "host",
                nickname = "RoomOwner",
                bio = "Room Host",
                vipLevel = 12,
                vipExpiryAtMillis = System.currentTimeMillis() + 86_400_000L,
            )
            val room = VoiceRoom(
                id = "320252",
                title = "VIP Royal Lounge",
                description = "VIP Test Room",
                category = RoomCategory.MUSIC,
                hostId = host.uid,
                hostName = host.nickname,
                hostVip = host.vipLevel,
                maxSeats = 8,
            )
            manager.enterRoom(room, host, null, null, emptyList())

            // VIP 11 guest seated on Seat #0
            manager.assignUserToSeat(0, "u_vip11", "SovereignGuest", vipLevel = 11)
            // Normal VIP 2 guest seated on Seat #1
            manager.assignUserToSeat(1, "u_vip2", "StandardGuest", vipLevel = 2)

            // VIP 11 Anti-KickOut blocks removeUserFromSeat and banUserFromRoom
            manager.removeUserFromSeat(0)
            assertEquals("u_vip11", manager.session.value.seats[0].occupantId)
            manager.banUserFromRoom("u_vip11", "SovereignGuest")
            assertEquals("u_vip11", manager.session.value.seats[0].occupantId)
            assertFalse("u_vip11" in manager.session.value.bannedUserIds)

            // Non-VIP-11 guest on Seat #1 CAN be removed
            manager.removeUserFromSeat(1)
            assertEquals(null, manager.session.value.seats[1].occupantId)

            // VIP 12 Mute Protection aura blocks admin mute on Seat #0
            manager.activateMuteProtection(0, durationMs = 600_000L)
            manager.toggleOccupantMuteByAdmin(0)
            assertFalse(manager.session.value.seats[0].isMuted)

            // VIP 9+ can post bullet comments, while VIP 4 falls back to regular chat
            val vip9User = host.copy(uid = "u_vip9", nickname = "VipNine", vipLevel = 9)
            val vip4User = host.copy(uid = "u_vip4", nickname = "VipFour", vipLevel = 4)
            manager.sendRoomChat("VIP9 Spotlight!", vip9User, isEmoji = false, asBulletComment = true)
            manager.sendRoomChat("Trying bullet as VIP4", vip4User, isEmoji = false, asBulletComment = true)

            val messages = manager.session.value.messages
            val vip9Msg = messages.first { it.senderId == "u_vip9" }
            val vip4Msg = messages.first { it.senderId == "u_vip4" }
            assertTrue(vip9Msg.isBulletComment)
            assertFalse(vip4Msg.isBulletComment)
        } finally {
            scope.cancel()
        }
    }

    @Test
    fun `home 3d redesign and vip navigation indicator render cleanly on pure white wefun theme`() {
        val vipUser = UserProfile(
            uid = "usr_nova_01",
            wefunId = "WF-804921",
            username = "ariavance",
            nickname = "Aria Vance",
            bio = "WeFun VIP Host",
            vipLevel = 6,
            coins = 2800L,
            diamonds = 640L,
        )
        composeRule.setContent {
            WEFUNTheme {
                WefunHomeGamesScreen(
                    user = vipUser,
                    events = WefunSeedData.events,
                    dailyTasks = WefunSeedData.dailyTasks,
                    followedUserIds = emptySet(),
                    onOpenWallet = {},
                    onOpenEventsRankings = {},
                    onOpenGamePlaceholder = {},
                    onOpenGameRooms = {},
                    onOpenOwnFullProfile = {},
                    onOpenUserProfile = { _, _ -> },
                    onOpenChatWithPeer = { _, _ -> },
                    onToggleFollow = {},
                    onClaimDailyTask = {},
                )
            }
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("home_brand_logo").assertIsDisplayed()
        composeRule.onNodeWithTag("home_coin_pill").assertIsDisplayed()
        composeRule.onNodeWithTag("home_avatar_vip_tag", useUnmergedTree = true).assertIsDisplayed()
        composeRule.onNodeWithTag("quick_action_ranking").assertIsDisplayed()
        composeRule.onNodeWithTag("quick_action_fun_cards").assertIsDisplayed()
        composeRule.onNodeWithTag("quick_action_tasks").assertIsDisplayed()
        composeRule.onNodeWithTag("quick_action_friends").assertIsDisplayed()
        composeRule.onNodeWithText("Games").assertIsDisplayed()
        composeRule.onNodeWithTag("home_games_list").performScrollToNode(hasText("More Party Games"))
        composeRule.waitForIdle()
        composeRule.onNodeWithText("More Party Games").assertIsDisplayed()
    }

    @Test
    fun `bottom navigation bar displays gold vip indicator for active vip user`() {
        composeRule.setContent {
            WEFUNTheme {
                BottomNavigationBar(
                    currentRoute = WefunDestination.Home.route,
                    onSelectDestination = {},
                    unreadChatsCount = 2,
                    unreadNotificationsCount = 1,
                    showVipGoldIndicator = true,
                )
            }
        }
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("nav_vip_gold_indicator", useUnmergedTree = true).assertIsDisplayed()
    }

    @Test
    fun `vip center renders all 13 dark luxury sections, level selector, and restore purchase`() {
        val now = System.currentTimeMillis()
        val user = UserProfile(
            uid = "usr_vip5",
            wefunId = "WF-5555",
            username = "royal_host",
            nickname = "RoyalHost",
            bio = "WeFun VIP 5 Host",
            vipLevel = 5,
            vipGrowthPoints = 65_000L,
            vipActivatedAtMillis = now - 86_400_000L,
            vipExpiryAtMillis = now + 29L * 86_400_000L,
            coins = 12_000L,
            diamonds = 4_000L,
        )
        val membership = VipGrowthEngine.buildMembershipState(
            user = user,
            configs = VipCatalog.defaultTiers,
            nowMillis = now,
            todayEpochDay = 20_000L,
        )
        var restoreClicked = false

        composeRule.setContent {
            WEFUNTheme {
                VipCenterScreen(
                    user = user,
                    vipConfigs = VipCatalog.defaultTiers,
                    membershipState = membership,
                    vipGifts = VipCatalog.defaultVipGifts,
                    onBack = {},
                    onActivateVip = { _, _, _ -> },
                    onRestorePurchases = { restoreClicked = true },
                )
            }
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("vip_center_screen").assertIsDisplayed()
        composeRule.onNodeWithTag("vip_section_membership").assertIsDisplayed()
        composeRule.onNodeWithTag("vip_section_badge").assertIsDisplayed()

        // Scroll to remaining sections and verify presence
        listOf(
            "vip_section_levels",
            "vip_section_growth",
            "vip_section_privileges",
            "vip_section_nickname_colors",
            "vip_section_entrance",
            "vip_section_gifts",
            "vip_section_duration",
            "vip_section_regulation",
            "vip_section_renewal",
            "vip_section_payments",
            "vip_section_faq",
        ).forEach { tag ->
            composeRule.onNodeWithTag("vip_center_scroll_list").performScrollToNode(hasTestTag(tag))
            composeRule.waitForIdle()
            composeRule.onNodeWithTag(tag).assertIsDisplayed()
        }

        // Scroll back to Section 11 (Renewal & Restore) and click Restore Purchase
        composeRule.onNodeWithTag("vip_center_scroll_list")
            .performScrollToNode(hasTestTag("vip_section_renewal"))
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("vip_restore_purchase_button").performClick()
        composeRule.waitForIdle()
        assertTrue(restoreClicked)
    }

    @Test
    fun `admin panel displays 4 management tabs and dispatches live vip tier config updates`() {
        var updatedTier: VipTierConfig? = null
        val user = UserProfile(
            uid = "usr_admin",
            wefunId = "WF-9999",
            username = "admin",
            nickname = "WeFunAdmin",
            bio = "Administrator",
            vipLevel = 12,
        )
        composeRule.setContent {
            WEFUNTheme {
                AdminPanelScreen(
                    user = user,
                    vipConfigs = VipCatalog.defaultTiers,
                    vipGifts = VipCatalog.defaultVipGifts,
                    vipAuditLogs = emptyList(),
                    onBack = {},
                    onUpdateVipConfig = { updatedTier = it },
                    onGrantUserVip = { _, _, _ -> },
                    onAdjustGrowthPoints = { _, _ -> },
                    onSaveVipGift = {},
                )
            }
        }
        composeRule.waitForIdle()

        composeRule.onNodeWithTag("admin_panel_screen").assertIsDisplayed()
        composeRule.onNodeWithTag("admin_tab_0").assertIsDisplayed()
        composeRule.onNodeWithTag("admin_tab_1").assertIsDisplayed()
        composeRule.onNodeWithTag("admin_tab_2").assertIsDisplayed()
        composeRule.onNodeWithTag("admin_tab_3").assertIsDisplayed()

        // Edit VIP 2 threshold and save
        composeRule.onNodeWithTag("admin_edit_tier_2").performClick()
        composeRule.waitForIdle()
        composeRule.onNodeWithTag("admin_edit_threshold_input").performTextReplacement("1500")
        composeRule.onNodeWithTag("admin_save_tier_btn").performClick()
        composeRule.waitForIdle()

        assertEquals(2, updatedTier?.level)
        assertEquals(1500L, updatedTier?.growthThreshold)
    }
}
