package com.example.data.repository

import com.example.data.local.DirectMessageEntity
import com.example.data.local.GameMatchEntity
import com.example.data.local.SafetyReportEntity
import com.example.data.local.SocialPostEntity
import com.example.data.local.UserProfileEntity
import com.example.data.local.VoiceRoomEntity
import com.example.data.local.WalletTransactionEntity
import com.example.data.model.AchievementItem
import com.example.data.model.AvatarConfig
import com.example.data.model.BiddingMode
import com.example.data.model.ChatThread
import com.example.data.model.CommunityMember
import com.example.data.model.DailyTaskItem
import com.example.data.model.DiscoverCategory
import com.example.data.model.EventItem
import com.example.data.model.FamilyInfo
import com.example.data.model.FunCardItem
import com.example.data.model.GameId
import com.example.data.model.GiftCategory
import com.example.data.model.HostStats
import com.example.data.model.LeaderboardEntry
import com.example.data.model.NotificationItem
import com.example.data.model.PartyGame
import com.example.data.model.RechargePackage
import com.example.data.model.RelationshipTagOption
import com.example.data.model.RoomCategory
import com.example.data.model.RoomThemeStyle
import com.example.data.model.ShopItem
import com.example.data.model.UserProfile
import com.example.data.model.VipTier
import com.example.data.model.VirtualGift

object WefunSeedData {

    fun defaultUser(
        uid: String = "usr_nova_01",
        nickname: String = "Aria Vance",
        username: String = "ariavance",
        phone: String = "+91 98765 43210",
        email: String = "aria@wefun.live",
        country: String = "India",
        language: String = "English",
    ) = UserProfileEntity(
        uid = uid,
        wefunId = "WF-804921",
        username = username,
        nickname = nickname,
        bio = "Music host, late-night storyteller & Ludo strategist. Come grab a mic!",
        phone = phone,
        email = email,
        dob = "2000-05-14",
        isAgeVerified = true,
        gender = "Female",
        showGender = true,
        showAge = true,
        country = country,
        language = language,
        photoUri = null,
        accentColorHex = 0xFF8B5CF6,
        faceStyle = "Radiant",
        hairStyle = "Neon Braids",
        outfitStyle = "Cyber Jacket",
        accessory = "Holo Headphones",
        backgroundAura = "Cosmic Violet",
        frameName = "Astral Gold",
        vipLevel = 2,
        userLevel = 14,
        charmPoints = 18_400L,
        xp = 3450,
        nextLevelXp = 5000,
        coins = 2800,
        diamonds = 640,
        bonusPoints = 320,
        followersCount = 1280,
        followingCount = 184,
        friendsCount = 72,
        giftsReceivedCount = 419,
        giftsSentCount = 285,
        visitorsCount = 94,
        familyId = "fam_celestial",
        isHostVerified = true,
        hostLevel = 6,
        agencyName = "Nova Star Agency",
        lastCheckInDay = 2,
        isCheckedInToday = false,
        vipGrowthPoints = 1_200L,
        vipActivatedAtMillis = System.currentTimeMillis() - 86_400_000L * 5,
        vipExpiryAtMillis = VipGrowthEngine.calculateCalendarMonthExpiry(System.currentTimeMillis() - 86_400_000L * 5, 1),
        vipAutoRenewal = false,
        vipLastDailyClaimEpochDay = 0L,
        vipRegulationDeadlineMillis = System.currentTimeMillis() + 86_400_000L * 25,
        vipRegulationPointsEarned = 12L,
    )

    val initialRooms = listOf(
        VoiceRoomEntity(
            id = "1579056",
            title = "💎❤️✨ Midnight Neon Music",
            description = "Level 1 standard 8-seat music room. Grab a golden seat & request tracks!",
            categoryName = RoomCategory.MUSIC.name,
            password = null,
            hostId = "usr_nova_01",
            hostName = "Aria Vance",
            hostVip = 5,
            maxSeats = 8,
            roomLevel = 1,
            totalContribution = 4_250L,
            isFavorited = true,
            decorationStyle = "Charcoal Gold Standard",
            themeName = RoomThemeStyle.GALAXY.name,
            announcement = "Welcome to WEFUN! Keep vibes golden ✨ Request songs or hop on one of the 8 mic seats 🙏",
            popularity = 42800,
            onlineCount = 148,
            language = "English",
            familyName = "Celestial House",
            tagsCsv = "Music,Acoustic,Chill",
            isRecentlyActive = true,
        ),
        VoiceRoomEntity(
            id = "320252",
            title = "Royal Relationship Auction",
            description = "Deep purple & gold live Auction Room — bid virtual points to unlock BFF & relationship badges!",
            categoryName = RoomCategory.PUBLIC.name,
            password = null,
            hostId = "host_rhea",
            hostName = "DJ Rhea",
            hostVip = 5,
            maxSeats = 8,
            roomLevel = 4,
            totalContribution = 112_000L,
            isFavorited = true,
            decorationStyle = "Royal Velvet Aura",
            themeName = RoomThemeStyle.ROYAL_GOLD.name,
            announcement = "Auction Room open! Tap Auction Settings to choose Ordinary bidding or BFF relationship tags.",
            popularity = 76400,
            onlineCount = 268,
            language = "English",
            familyName = "Velvet Pulse",
            roomModeName = "AUCTION",
            tagsCsv = "Auction,Friends,VIP",
            isRecentlyActive = true,
        ),
        VoiceRoomEntity(
            id = "rm_102",
            title = "Royal PK Arena: Titans vs Phoenix",
            description = "Level 3 (8 seats) PK Arena — Reach Level 7 to unlock 14 seats!",
            categoryName = RoomCategory.PK.name,
            password = null,
            hostId = "host_kai",
            hostName = "Kai Storm",
            hostVip = 6,
            maxSeats = 8,
            roomLevel = 3,
            totalContribution = 75_000L,
            isFavorited = false,
            decorationStyle = "Gold Ribbon Crest",
            themeName = RoomThemeStyle.ROYAL_GOLD.name,
            announcement = "Double PK spotlight active! Reach Level 7 to unlock 6 extra compact seats!",
            popularity = 68900,
            onlineCount = 312,
            language = "English",
            familyName = "Golden Crown",
            tagsCsv = "PK,Popular",
            isRecentlyActive = true,
        ),
        VoiceRoomEntity(
            id = "rm_101",
            title = "DJ Rhea Acoustic & Chill",
            description = "Level 2 cozy lounge with 8 microphone seats & acoustic requests.",
            categoryName = RoomCategory.MUSIC.name,
            password = null,
            hostId = "host_rhea",
            hostName = "DJ Rhea",
            hostVip = 5,
            maxSeats = 8,
            roomLevel = 2,
            totalContribution = 18_400L,
            decorationStyle = "Purple Pulse Ring",
            themeName = RoomThemeStyle.GALAXY.name,
            announcement = "Request your favorite track in the Music Queue! Respect all speakers.",
            popularity = 39200,
            onlineCount = 118,
            language = "English",
            familyName = "Velvet Pulse",
            tagsCsv = "Friends,Music",
            isRecentlyActive = true,
        ),
        VoiceRoomEntity(
            id = "rm_103",
            title = "Space Werewolf & Spy Pro Lobby",
            description = "8-seat voice deduction lobby. Mic required for voting rounds!",
            categoryName = RoomCategory.GAME.name,
            password = null,
            hostId = "host_milo",
            hostName = "Milo Taktik",
            hostVip = 3,
            maxSeats = 8,
            roomLevel = 3,
            totalContribution = 44_200L,
            decorationStyle = "Gold Ribbon Crest",
            themeName = RoomThemeStyle.CYBER_NEON.name,
            announcement = "No spoilers in text chat during night phase. Have fun!",
            popularity = 31500,
            onlineCount = 96,
            language = "Hindi",
            familyName = "Shadow Syndicate",
            tagsCsv = "Games,Strategy",
            isNewRoom = true,
        ),
        VoiceRoomEntity(
            id = "rm_104",
            title = "Starlight Social & Icebreakers",
            description = "Level 1 friendly social room with 8 open golden mic seats.",
            categoryName = RoomCategory.DATING.name,
            password = null,
            hostId = "host_zara",
            hostName = "Zara Moon",
            hostVip = 4,
            maxSeats = 8,
            roomLevel = 1,
            totalContribution = 6_800L,
            themeName = RoomThemeStyle.SAKURA_NIGHT.name,
            announcement = "Respectful vibes only. Zero tolerance for harassment.",
            popularity = 27400,
            onlineCount = 114,
            language = "English",
            familyName = "Velvet Pulse",
            tagsCsv = "Friends,New",
            isNewRoom = true,
        ),
        VoiceRoomEntity(
            id = "rm_105",
            title = "Celestial Family Weekly Townhall",
            description = "Family war strategy, contribution badges & member rewards.",
            categoryName = RoomCategory.FAMILY.name,
            password = null,
            hostId = "usr_nova_01",
            hostName = "Aria Vance",
            hostVip = 2,
            maxSeats = 8,
            roomLevel = 5,
            totalContribution = 248_000L,
            decorationStyle = "Astral Halo Frame",
            themeName = RoomThemeStyle.GALAXY.name,
            announcement = "Family PK tournament starts Friday 9 PM IST!",
            popularity = 19200,
            onlineCount = 54,
            language = "English",
            familyName = "Celestial House",
            tagsCsv = "Family,Friends",
            isRecentlyActive = true,
        ),
        VoiceRoomEntity(
            id = "rm_106",
            title = "VIP Obsidian Sanctuary (Pass: 1234)",
            description = "Private acoustic jam & close friends circle.",
            categoryName = RoomCategory.PASSWORD.name,
            password = "1234",
            hostId = "host_leo",
            hostName = "Leo Beats",
            hostVip = 6,
            maxSeats = 8,
            roomLevel = 1,
            totalContribution = 2_400L,
            themeName = RoomThemeStyle.ROYAL_GOLD.name,
            announcement = "Private room — ask host for passcode (Demo pass: 1234).",
            popularity = 12400,
            onlineCount = 18,
            language = "English",
            familyName = null,
            tagsCsv = "Private,Music",
            isNewRoom = true,
        ),
        VoiceRoomEntity(
            id = "rm_107",
            title = "WeFun Official Starlight Carnival",
            description = "Level 5 official platform showcase with verified hosts & coin drops!",
            categoryName = RoomCategory.OFFICIAL.name,
            password = null,
            hostId = "host_official",
            hostName = "WeFun Official",
            hostVip = 6,
            maxSeats = 8,
            roomLevel = 5,
            totalContribution = 312_000L,
            decorationStyle = "Astral Halo Frame",
            themeName = RoomThemeStyle.ROYAL_GOLD.name,
            announcement = "Official Carnival Finale tonight! Win exclusive SVIP frames.",
            popularity = 91500,
            onlineCount = 540,
            language = "English",
            familyName = "WeFun Creators",
            tagsCsv = "Official,Carnival",
            isRecentlyActive = true,
        ),
        VoiceRoomEntity(
            id = "rm_108",
            title = "Neon Stage Live Video & DJ Mix",
            description = "Interactive stage visualizers, live DJ sets & video hangout!",
            categoryName = RoomCategory.VIDEO.name,
            password = null,
            hostId = "host_leo",
            hostName = "Leo Beats",
            hostVip = 6,
            maxSeats = 8,
            roomLevel = 2,
            totalContribution = 16_200L,
            decorationStyle = "Purple Pulse Ring",
            themeName = RoomThemeStyle.CYBER_NEON.name,
            announcement = "Turn up the bass! Video stage spotlight open for VIP performers.",
            popularity = 44100,
            onlineCount = 204,
            language = "English",
            familyName = "Velvet Pulse",
            tagsCsv = "Video,DJ,New",
            isNewRoom = true,
        ),
    )

    val virtualGifts = listOf(
        VirtualGift("g_rose", "Crystal Rose", GiftCategory.ROSES, 10, 5, 1600, "🌹", false, false, false, 0xFFF43F5E),
        VirtualGift("g_bouquet", "Velvet Bouquet", GiftCategory.FLOWERS, 39, 20, 1800, "💐", false, false, false, 0xFFEC4899),
        VirtualGift("g_heart", "Neon Heart", GiftCategory.HEARTS, 20, 10, 1600, "💜", false, false, false, 0xFF8B5CF6),
        VirtualGift("g_kiss", "Love U", GiftCategory.LOVE, 50, 25, 2000, "💋", false, false, false, 0xFFEC4899),
        VirtualGift("g_ring", "Starlight Ring", GiftCategory.ROMANTIC, 99, 49, 2200, "💍", false, false, false, 0xFF00C2B8),
        VirtualGift("g_teddy", "Cosmic Bear", GiftCategory.ANIMALS, 99, 49, 2200, "🧸", false, false, false, 0xFF38BDF8),
        VirtualGift("g_giftbox", "Lucky Gift Box", GiftCategory.GIFT_BOXES, 150, 75, 2400, "🎁", true, false, false, 0xFF26B887),
        VirtualGift("g_lantern", "Celebration Fireworks", GiftCategory.CELEBRATION, 188, 94, 2400, "🎆", true, false, false, 0xFFF59E0B),
        VirtualGift("g_crown", "Royal Crown", GiftCategory.VIP, 199, 99, 2600, "👑", true, false, false, 0xFFFBBF24),
        VirtualGift("g_watch", "Tourbillon", GiftCategory.LUXURY, 299, 149, 2600, "⌚", true, false, false, 0xFF38BDF8),
        VirtualGift("g_pills", "Happy Spark", GiftCategory.SPECIAL, 66, 33, 2000, "✨", false, false, false, 0xFFFF4FA3),
        VirtualGift("g_supercar", "Hyper Roadster", GiftCategory.SUPERCARS, 599, 299, 3200, "🏎️", true, false, false, 0xFFF43F5E),
        VirtualGift("g_lion", "Golden Lion", GiftCategory.GOLDEN_LIONS, 888, 444, 3400, "🦁", true, false, false, 0xFFFBBF24),
        VirtualGift("g_phoenix", "Astral Phoenix", GiftCategory.FANTASY, 1200, 600, 3600, "🔥", true, false, false, 0xFFA855F7),
        VirtualGift("g_jet", "Nebula Jet", GiftCategory.PRIVATE_JETS, 1888, 944, 3800, "🛩️", true, false, false, 0xFF38BDF8),
        VirtualGift("g_dragon", "Cosmic Dragon", GiftCategory.DRAGONS, 2500, 1250, 4200, "🐉", true, true, true, 0xFFFBBF24),
        VirtualGift("g_galaxy", "Galaxy Throne", GiftCategory.PREMIUM, 5200, 2600, 4600, "🌌", true, true, true, 0xFF8B5CF6),
        VirtualGift("g_eclipse", "Eclipse Sovereign", GiftCategory.EXCLUSIVE, 9999, 4999, 5000, "💎", true, true, true, 0xFF38BDF8),
    )

    val initialPackageInventory: Map<String, Int> = mapOf(
        "g_rose" to 16,
        "g_crown" to 1,
        "g_pills" to 1,
        "g_kiss" to 2,
        "g_giftbox" to 3,
    )

    val ordinaryRelationshipTags: List<RelationshipTagOption> = listOf(
        RelationshipTagOption("tag_cp", "CP", BiddingMode.ORDINARY_BIDDING, "ribbon"),
        RelationshipTagOption("tag_attendant", "Attendant", BiddingMode.ORDINARY_BIDDING, "shield"),
        RelationshipTagOption("tag_brother", "Brother", BiddingMode.ORDINARY_BIDDING, "bro"),
        RelationshipTagOption("tag_bestie", "Bestie", BiddingMode.ORDINARY_BIDDING, "clover"),
        RelationshipTagOption("tag_master", "Master", BiddingMode.ORDINARY_BIDDING, "shield"),
        RelationshipTagOption("tag_apprentice", "Apprentice", BiddingMode.ORDINARY_BIDDING, "puzzle"),
        RelationshipTagOption("tag_confidential", "Confidential", BiddingMode.ORDINARY_BIDDING, "puzzle"),
        RelationshipTagOption("tag_appreciation", "Appreciation", BiddingMode.ORDINARY_BIDDING, "heart"),
        RelationshipTagOption("tag_ord_bro_sis", "Bro and Sis", BiddingMode.ORDINARY_BIDDING, "clover"),
        RelationshipTagOption("tag_ord_sibling", "Sibling", BiddingMode.ORDINARY_BIDDING, "clover"),
        RelationshipTagOption("tag_ord_besties", "Besties", BiddingMode.ORDINARY_BIDDING, "ribbon"),
        RelationshipTagOption("tag_ord_bro", "Bro", BiddingMode.ORDINARY_BIDDING, "bro"),
    )

    val bffRelationshipTags: List<RelationshipTagOption> = listOf(
        RelationshipTagOption("tag_bro_sis", "Bro and sis", BiddingMode.BFF, "clover"),
        RelationshipTagOption("tag_besties", "Besties", BiddingMode.BFF, "ribbon"),
        RelationshipTagOption("tag_bff_bro", "Bro", BiddingMode.BFF, "bro"),
        RelationshipTagOption("tag_confidant", "Confidant", BiddingMode.BFF, "puzzle"),
        RelationshipTagOption("tag_bff_apprentice", "Apprentice", BiddingMode.BFF, "shield"),
        RelationshipTagOption("tag_sibling", "Sibling", BiddingMode.BFF, "clover"),
    )

    val rechargePackages = listOf(
        RechargePackage("pkg_100", 500, 50, "₹49", "Starter"),
        RechargePackage("pkg_300", 1500, 200, "₹149", "Popular"),
        RechargePackage("pkg_600", 3500, 600, "₹299", "+17% Bonus"),
        RechargePackage("pkg_1500", 10000, 2000, "₹799", "Best Value"),
        RechargePackage("pkg_4000", 30000, 7500, "₹1,999", "VIP Choice"),
    )

    val vipTiers: List<VipTier> = VipCatalog.defaultTiers.map { cfg ->
        VipTier(
            level = cfg.level,
            name = cfg.name,
            monthlyCoins = cfg.monthlySubscriptionCoins,
            accentHex = cfg.accentHex,
            frameTitle = cfg.badgeTitle,
            entryEffect = cfg.entranceEffectTitle,
            chatBubble = cfg.chatBubbleTier,
            privileges = cfg.privileges,
        )
    }

    val wefunFeaturedGames = listOf(
        PartyGame(
            id = GameId.SPACE_WEREWOLF,
            title = "Space Werewolf",
            category = "Social Deduction",
            playersOnline = 1840,
            entryCoins = 50,
            rewardCoins = 220,
            maxPlayers = 10,
            rules = "Crewmates repair starship modules while impostors lurk in the shadows. Debate clues with friends and eject the suspects!",
            emoji = "🛸",
            accentHex = 0xFF6B4EFF,
            secondaryAccentHex = 0xFF3E22B8,
            isNew = true,
            isFeaturedBanner = true
        ),
        PartyGame(
            id = GameId.JACKAROO,
            title = "Jackaroo",
            category = "Card & Board",
            playersOnline = 1420,
            entryCoins = 100,
            rewardCoins = 350,
            maxPlayers = 4,
            rules = "Classic 4-player team tactical card & marble board race. Play power cards to swap seats, shield partners, and sprint home!",
            emoji = "🃏",
            accentHex = 0xFFFF9F1C,
            secondaryAccentHex = 0xFFE66A00,
            isNew = false,
            isFeaturedBanner = true
        ),
        PartyGame(
            id = GameId.GUESS_THEIR_ANSWERS,
            title = "Guess Their Answers",
            category = "Quiz & Party",
            playersOnline = 1190,
            entryCoins = 30,
            rewardCoins = 160,
            maxPlayers = 6,
            rules = "Think like the crowd! Type the most popular answers to funny everyday prompts and climb the round scoreboard!",
            emoji = "💡",
            accentHex = 0xFF00C2B8,
            secondaryAccentHex = 0xFF0088A3,
            isNew = true,
            isFeaturedBanner = true
        ),
        PartyGame(
            id = GameId.BRAIN_STORM,
            title = "Brain Storm",
            category = "Trivia Speed",
            playersOnline = 980,
            entryCoins = 40,
            rewardCoins = 190,
            maxPlayers = 6,
            rules = "Fast-paced visual puzzles, logic streaks, and rapid trivia duels. Outsmart rivals before the countdown sparks out!",
            emoji = "⚡",
            accentHex = 0xFF9333EA,
            secondaryAccentHex = 0xFFEC4899,
            isNew = true,
            isFeaturedBanner = true
        ),
        PartyGame(
            id = GameId.OH_MY_CARD,
            title = "Oh My Cards",
            category = "Card & Board",
            playersOnline = 1060,
            entryCoins = 40,
            rewardCoins = 180,
            maxPlayers = 4,
            rules = "High-energy color & action card showdown! Drop +2, Reverse, and Wild swaps to empty your hand before anyone else!",
            emoji = "🎴",
            accentHex = 0xFFFF4FA3,
            secondaryAccentHex = 0xFFDF2969,
            isNew = true,
            isFeaturedBanner = false
        ),
        PartyGame(
            id = GameId.HAPPY_CARROM,
            title = "Happy Carrom",
            category = "Board Arcade",
            playersOnline = 1310,
            entryCoins = 50,
            rewardCoins = 240,
            maxPlayers = 4,
            rules = "Smooth striker physics on a sunlit wooden board! Pocket the red Queen, line up bank shots, and sweep the table!",
            emoji = "🎯",
            accentHex = 0xFF10B981,
            secondaryAccentHex = 0xFF047857,
            isNew = false,
            isFeaturedBanner = false
        ),
    )

    val partyGames = wefunFeaturedGames + listOf(
        PartyGame(GameId.WHOS_THE_SPY, "Who's the Spy", "Social Deduction", 940, 20, 100, 8,
            "Each player gets a secret word—one spy has a similar word. Describe cleverly and vote out the spy!", "🕵️", 0xFFF43F5E, 0xFFBE123C),
        PartyGame(GameId.LUDO, "Ludo", "Card & Board", 1420, 50, 180, 4,
            "Roll the dice, move your 4 tokens around the circuit, and race to the home star!", "🎲", 0xFF3B82F6, 0xFF1D4ED8),
        PartyGame(GameId.QUIZ_BATTLE, "Quiz Battle", "Trivia Speed", 760, 30, 120, 6,
            "Answer rapid-fire pop culture, music, science, and gaming trivia faster than opponents!", "⚡", 0xFFF59E0B, 0xFFB45309),
        PartyGame(GameId.DRAW_AND_GUESS, "Draw & Guess", "Creative Party", 830, 20, 90, 8,
            "Sketch the secret prompt on the live canvas before time runs out while friends race to guess!", "🖌️", 0xFF14B8A6, 0xFF0F766E),
    )

    val dailyTasks = listOf(
        DailyTaskItem("tsk_1", "Daily Check-In Streak", "Check in once today to keep your 7-day coin streak", 150, 100, isCompleted = true, isClaimed = false),
        DailyTaskItem("tsk_2", "Join a Voice Party", "Spend 5 minutes listening or speaking in any Voice Room", 200, 150, isCompleted = true, isClaimed = false),
        DailyTaskItem("tsk_3", "Play 1 Multiplayer Game", "Complete a match in Ludo, Jackaroo, or Space Werewolf", 250, 200, isCompleted = false, isClaimed = false),
        DailyTaskItem("tsk_4", "Send a Virtual Gift", "Cheer a host with any virtual gift in a live room", 300, 250, isCompleted = false, isClaimed = false),
    )

    val initialTransactions = listOf(
        WalletTransactionEntity("tx_1", "RECHARGE", "COINS", 1500, true, "Starter Welcome Bundle", "ref_seed_001", System.currentTimeMillis() - 86400000L * 3),
        WalletTransactionEntity("tx_2", "GIFT_SENT", "COINS", 199, false, "Sent Royal Crown to DJ Rhea", "ref_seed_002", System.currentTimeMillis() - 86400000L * 2),
        WalletTransactionEntity("tx_3", "GIFT_EARNED", "DIAMONDS", 320, true, "Received Astral Phoenix in Room #101", "ref_seed_003", System.currentTimeMillis() - 86400000L),
        WalletTransactionEntity("tx_4", "GAME_REWARD", "COINS", 180, true, "Ludo Express 1st Place", "ref_seed_004", System.currentTimeMillis() - 3600000L * 5),
    )

    val initialPosts = listOf(
        SocialPostEntity(
            id = "post_1",
            authorId = "host_rhea",
            authorName = "DJ Rhea",
            authorVip = 5,
            content = "Just wrapped a 3-hour Midnight Neon acoustic set with 150+ listeners! Who's joining tonight's Mic Grab?",
            hashtag = "#MidnightNeon",
            mediaType = "SHORT_VIDEO",
            imageUri = null,
            likesCount = 342,
            commentsCount = 48,
            isLiked = false,
            timestamp = System.currentTimeMillis() - 3600000L * 2,
        ),
        SocialPostEntity(
            id = "post_2",
            authorId = "host_kai",
            authorName = "Kai Storm",
            authorVip = 6,
            content = "Royal PK Arena hit 68K popularity! Shoutout to Celestial House for dropping two Cosmic Dragons in the final 10 seconds!",
            hashtag = "#PKBattle",
            mediaType = "IMAGE",
            imageUri = null,
            likesCount = 518,
            commentsCount = 91,
            isLiked = true,
            timestamp = System.currentTimeMillis() - 3600000L * 6,
        ),
        SocialPostEntity(
            id = "post_3",
            authorId = "host_zara",
            authorName = "Zara Moon",
            authorVip = 4,
            content = "Hosting a fresh Space Werewolf + Truth or Dare marathon at 9 PM. Drop your Wefun ID to get an invite!",
            hashtag = "#SpaceWerewolf",
            mediaType = "TEXT",
            imageUri = null,
            likesCount = 194,
            commentsCount = 37,
            isLiked = false,
            timestamp = System.currentTimeMillis() - 3600000L * 11,
        ),
    )

    val initialThreads = listOf(
        ChatThread("th_official", "wefun_official", "WEFUN Official", "WF", 0xFF00C2B8, isGroup = false, isRequest = false, isOnline = true, isMuted = false, vipLevel = 6, isOfficial = true, timeLabel = "11:30 am", lastSeenLabel = "Official System", unreadCount = 1, lastMessagePreview = "Welcome to WEFUN Carnival Week! Claim bonus tasks."),
        ChatThread("th_rhea", "host_rhea", "DJ Rhea", "DR", 0xFF8B5CF6, isGroup = false, isRequest = false, isOnline = true, isMuted = false, vipLevel = 5, isOfficial = false, timeLabel = "11:23 am", lastSeenLabel = "Online", unreadCount = 2, lastMessagePreview = "Ready for our duet PK tonight?"),
        ChatThread("th_family", "fam_celestial", "Celestial House Group", "CH", 0xFFFBBF24, isGroup = true, isRequest = false, isOnline = true, isMuted = false, vipLevel = 4, isOfficial = false, timeLabel = "10:48 am", lastSeenLabel = "14 online", unreadCount = 5, lastMessagePreview = "Aria: Family tournament starts at 9 PM!"),
        ChatThread("th_kai", "host_kai", "Kai Storm", "KS", 0xFF38BDF8, isGroup = false, isRequest = false, isOnline = true, isMuted = false, vipLevel = 6, isOfficial = false, timeLabel = "Yesterday", lastSeenLabel = "In PK Room", unreadCount = 0, lastMessagePreview = "GG! That last-second dragon was wild."),
        ChatThread("th_zara", "host_zara", "Zara Moon", "ZM", 0xFFEC4899, isGroup = false, isRequest = false, isOnline = false, isMuted = true, vipLevel = 4, isOfficial = false, timeLabel = "Yesterday", lastSeenLabel = "Last seen 2h ago", unreadCount = 0, lastMessagePreview = "Space Werewolf table opens at 9 PM!"),
        ChatThread("th_req1", "usr_kiran", "Kiran Beats", "KB", 0xFF10B981, isGroup = false, isRequest = true, isOnline = false, isMuted = false, vipLevel = 1, isOfficial = false, timeLabel = "18m ago", lastSeenLabel = "Last seen 18m ago", unreadCount = 1, lastMessagePreview = "Hey! Loved your singing in Midnight Lounge, can we collab?"),
    )

    val initialMessages = listOf(
        DirectMessageEntity("m_1", "th_rhea", "host_rhea", "DJ Rhea", "Hey Aria! Loved your acoustic cover earlier.", null, null, "🔥", true, System.currentTimeMillis() - 7200000L),
        DirectMessageEntity("m_2", "th_rhea", "usr_nova_01", "Aria Vance", "Thank you Rhea! Your room mix sounded super crisp.", null, null, "💜", true, System.currentTimeMillis() - 7000000L),
        DirectMessageEntity("m_3", "th_rhea", "host_rhea", "DJ Rhea", "Ready for our duet PK tonight?", null, null, null, false, System.currentTimeMillis() - 1200000L),
        DirectMessageEntity("m_4", "th_family", "host_milo", "Milo Taktik", "Who is representing us in Jackaroo tonight?", null, null, null, true, System.currentTimeMillis() - 3600000L),
        DirectMessageEntity("m_5", "th_family", "usr_nova_01", "Aria Vance", "Family tournament starts at 9 PM!", null, null, "⚡", true, System.currentTimeMillis() - 1800000L),
        DirectMessageEntity("m_6", "th_kai", "host_kai", "Kai Storm", "GG! That last-second dragon was wild.", "Cosmic Dragon", null, "🐉", true, System.currentTimeMillis() - 5400000L),
        DirectMessageEntity("m_7", "th_req1", "usr_kiran", "Kiran Beats", "Hey! Loved your singing in Midnight Lounge, can we collab?", null, null, null, false, System.currentTimeMillis() - 1080000L),
    )

    val initialMatches = listOf(
        GameMatchEntity("gm_1", "Ludo", "1st Place Victory", 420, 180, 120, System.currentTimeMillis() - 7200000L),
        GameMatchEntity("gm_2", "Quiz Battle", "2nd Place", 290, 60, 80, System.currentTimeMillis() - 86400000L),
    )

    val initialReports = listOf(
        SafetyReportEntity("rep_1", "Room", "Spam Promo Lobby #99", "Unsolicited external links in room announcement", "Pending", System.currentTimeMillis() - 3600000L * 4),
        SafetyReportEntity("rep_2", "User", "Guest_4920", "Repeated mic spam during Space Werewolf", "Reviewed", System.currentTimeMillis() - 3600000L * 18),
    )

    val families = listOf(
        FamilyInfo("fam_celestial", "Celestial House", "🌌", "Creators, singers & late-night party hosts uniting under starlight.", "Aria Vance", 128, 12, 485200, 1, true),
        FamilyInfo("fam_golden", "Golden Crown", "👑", "Elite PK warriors & top gift legends.", "Kai Storm", 96, 11, 412900, 2, false),
        FamilyInfo("fam_velvet", "Velvet Pulse", "🎧", "DJs, acoustic musicians & indie lyricists.", "DJ Rhea", 84, 9, 319400, 3, false),
        FamilyInfo("fam_shadow", "Shadow Syndicate", "🛸", "Competitive Space Werewolf, Ludo & Jackaroo champions.", "Milo Taktik", 110, 10, 298700, 4, false),
    )

    val events = listOf(
        EventItem("ev_1", "Starlight Singing Grand Prix", "Singing Competition", "Tonight • 9:00 PM IST", "rm_101", "50,000 Coins + SVIP Frame + Official Host Badge", "3 rounds of 60s live vocals. Audience votes + gift weight.", 184, true),
        EventItem("ev_2", "Titan PK Carnival Finals", "PK Carnival", "Sat • 8:30 PM IST", "rm_102", "100,000 Diamonds Pool + Golden Lion Trophy", "Best-of-3 5-minute PK battles between top 8 families.", 96, false),
        EventItem("ev_3", "Ludo & Jackaroo Weekend Cup", "Official Tournament", "Sun • 6:00 PM IST", "rm_103", "25,000 Coins + Gaming Master Title", "Single elimination 4-player tables. Anti-cheat verified.", 240, false),
        EventItem("ev_4", "Diwali & Cosmic Lantern Gala", "Seasonal Festival", "Nov 15 • All Day", "rm_107", "Limited Lantern Gift + 2x XP Boost", "Send or receive 10 Festival Lanterns to unlock badge.", 512, true),
    )

    val achievements = listOf(
        AchievementItem("ach_1", "Newcomer", "Create your WEFUN identity & enter a live voice room", "🌟", true, "Completed"),
        AchievementItem("ach_2", "Rising Star", "Reach User Level 10 and host 5 voice sessions", "🚀", true, "Completed"),
        AchievementItem("ach_3", "Super Host", "Maintain 4.8+ rating across 20 live hours", "🎙️", true, "24 / 20 hrs"),
        AchievementItem("ach_4", "PK Champion", "Win 10 timed PK battles with your room", "⚔️", false, "7 / 10 wins"),
        AchievementItem("ach_5", "Social Star", "Reach 1,000 followers & publish 10 moments", "💫", true, "1,280 followers"),
        AchievementItem("ach_6", "Gift Master", "Send 50,000 Coins worth of animated gifts", "🎁", false, "18,400 / 50,000"),
        AchievementItem("ach_7", "Gaming Master", "Win matches across 5 different party games", "🏆", false, "3 / 5 games"),
        AchievementItem("ach_8", "Family Leader", "Grow a Family past 100 members & Level 10", "🏰", true, "Lv.12 • 128 members"),
    )

    val notifications = listOf(
        NotificationItem("n_1", "Gifts", "DJ Rhea thanked you!", "Your Royal Crown boosted Midnight Neon Lounge to #2!", "4m ago", false),
        NotificationItem("n_2", "PK Invites", "PK Challenge from Kai Storm", "Royal PK Arena invited your room to a 5-min Host PK.", "16m ago", false),
        NotificationItem("n_3", "Events", "Starlight Singing Grand Prix starts at 9 PM", "You are registered in Official Room #rm_101.", "1h ago", true),
        NotificationItem("n_4", "VIP", "VIP 2 Pulse Active", "Your Neon Beam room entry effect is active for 24 more days.", "3h ago", true),
        NotificationItem("n_5", "Security", "Age & Profile Verified", "Your WEFUN safety check is complete.", "1d ago", true),
    )

    val hostStats = HostStats(
        totalHoursThisMonth = 38.5f,
        verifiedStatus = "Verified Pro Host",
        nextSchedule = "Tonight • 9:00 PM — Midnight Neon Acoustic",
        monthlyDiamonds = 14820,
        avgListeners = 132,
        agencyName = "Nova Star Agency",
        agencyCommissionPct = 15,
        settlementStatus = "In-App Diamonds Verified • Cycle closes Nov 30",
    )

    fun leaderboardFor(tab: String): List<LeaderboardEntry> = when (tab) {
        "Senders" -> listOf(
            LeaderboardEntry(1, "Aarav Royal", "SVIP • Celestial House", 184500, 6, 0xFFFBBF24),
            LeaderboardEntry(2, "Elena Noir", "VIP 5 • Golden Crown", 142200, 5, 0xFF8B5CF6),
            LeaderboardEntry(3, "Vikram X", "VIP 4 • Shadow Syndicate", 98400, 4, 0xFF38BDF8),
            LeaderboardEntry(4, "Aria Vance", "VIP 2 • Celestial House", 42100, 2, 0xFFF43F5E),
        )
        "Receivers" -> listOf(
            LeaderboardEntry(1, "DJ Rhea", "Verified Host • Pulse", 215000, 5, 0xFF8B5CF6),
            LeaderboardEntry(2, "Kai Storm", "PK Champion • Golden Crown", 198300, 6, 0xFFFBBF24),
            LeaderboardEntry(3, "Zara Moon", "Starlight Host • Velvet", 126900, 4, 0xFFEC4899),
            LeaderboardEntry(4, "Aria Vance", "Music Host • Celestial", 74800, 2, 0xFF38BDF8),
        )
        "Hosts" -> listOf(
            LeaderboardEntry(1, "DJ Rhea", "72.4 hrs • 98% retention", 9850, 5, 0xFF8B5CF6),
            LeaderboardEntry(2, "Kai Storm", "64.0 hrs • 19 PK wins", 9420, 6, 0xFFFBBF24),
            LeaderboardEntry(3, "Aria Vance", "38.5 hrs • 132 avg listeners", 8610, 2, 0xFF38BDF8),
        )
        "Agencies" -> listOf(
            LeaderboardEntry(1, "Nova Star Agency", "48 Verified Hosts • 15% tier", 942000, 6, 0xFFFBBF24),
            LeaderboardEntry(2, "Pulse Creative", "35 Verified Hosts • 12% tier", 785000, 5, 0xFF8B5CF6),
            LeaderboardEntry(3, "Crown Talent Hub", "29 Verified Hosts • 14% tier", 614000, 4, 0xFF38BDF8),
        )
        "PK" -> listOf(
            LeaderboardEntry(1, "Kai Storm", "19 Wins • 312K PK Score", 312000, 6, 0xFFF43F5E),
            LeaderboardEntry(2, "DJ Rhea", "15 Wins • 248K PK Score", 248000, 5, 0xFF8B5CF6),
            LeaderboardEntry(3, "Leo Beats", "11 Wins • 189K PK Score", 189000, 6, 0xFFFBBF24),
        )
        else -> listOf( // Popularity / Games / Families
            LeaderboardEntry(1, "WEFUN Official Carnival", "540 live • Official", 91500, 6, 0xFFFBBF24),
            LeaderboardEntry(2, "Royal PK Arena", "312 live • Kai Storm", 68900, 6, 0xFFF43F5E),
            LeaderboardEntry(3, "Midnight Neon Lounge", "148 live • DJ Rhea", 42800, 5, 0xFF8B5CF6),
            LeaderboardEntry(4, "Space Werewolf Pro", "96 live • Milo Taktik", 31500, 3, 0xFF38BDF8),
        )
    }

    val shopItems = listOf(
        ShopItem("shp_1", "Turquoise Prism Ring", "Avatar Frame", "Glowing cyan halo for 30 days", 480, 0xFF00C2B8, "HOT"),
        ShopItem("shp_2", "Astral Gold Crown", "Avatar Frame", "Royal gold crest & shimmer border", 880, 0xFFFFB800, "VIP"),
        ShopItem("shp_3", "Sakura Blossom Crest", "Profile Decoration", "Soft pink floral header badge", 360, 0xFFFF4FA3, "NEW"),
        ShopItem("shp_4", "Neon Cyber Cruiser", "Entrance Mount", "Sparkling entry banner showcase", 1200, 0xFF7B2CFF, "RARE"),
        ShopItem("shp_5", "Aurora Chat Bubble", "Chat Bubble", "Custom turquoise gradient bubble", 320, 0xFF3B82F6, null),
        ShopItem("shp_6", "Starlight Wing Badge", "Badge", "Display exclusive wing crest on profile", 640, 0xFFFF8A2B, "LIMITED"),
    )

    val funCards = listOf(
        FunCardItem("fc_1", "Lucky Clover Card", "Rare", "Boosts daily task XP and unlocks cheerful profile sparkles.", 0xFF26B887),
        FunCardItem("fc_2", "Party Starlight Pass", "Epic", "Highlights your profile in Discovery and Weekend Tournaments.", 0xFF00C2B8),
        FunCardItem("fc_3", "Golden Trivia Shield", "Legendary", "Showcases top streak honors across Guess Their Answers & Brain Storm.", 0xFFFFB800),
        FunCardItem("fc_4", "Friendship Heart Card", "Special", "Send a greeting card badge to best friends in Chats.", 0xFFFF4FA3),
    )

    val publicProfiles: List<UserProfile> = listOf(
        UserProfile(
            uid = "host_rhea",
            wefunId = "WF-912340",
            username = "djrhea",
            nickname = "DJ Rhea",
            bio = "Acoustic singer, late-night host & Velvet Pulse founder. Always down for duet jams!",
            country = "India",
            language = "English",
            accentColorHex = 0xFF7B2CFF,
            avatarConfig = AvatarConfig("Sparkle", "Violet Waves", "Velvet Stage", "Studio Mic", "Violet Mist", "Prism Crown"),
            vipLevel = 8,
            userLevel = 24,
            charmPoints = 1_450_000L,
            vipGrowthPoints = 640_000L,
            customTitle = "Starlight Diva",
            xp = 4200,
            nextLevelXp = 5000,
            coins = 9200,
            diamonds = 2140,
            followersCount = 4820,
            followingCount = 210,
            friendsCount = 116,
            giftsReceivedCount = 1840,
            giftsSentCount = 620,
            visitorsCount = 412,
            familyId = "fam_velvet",
            isHostVerified = true,
            hostLevel = 9,
            agencyName = "Pulse Creative"
        ),
        UserProfile(
            uid = "host_kai",
            wefunId = "WF-664120",
            username = "kaistorm",
            nickname = "Kai Storm",
            bio = "PK Champion & Jackaroo strategist. Catch our team every evening!",
            country = "UAE",
            language = "English",
            accentColorHex = 0xFFFF4FA3,
            avatarConfig = AvatarConfig("Bold", "Silver Spikes", "Titan Armor", "Gold Crest", "Solar Gold", "Eclipse Dragon"),
            vipLevel = 11,
            userLevel = 28,
            charmPoints = 4_200_000L,
            vipGrowthPoints = 5_100_000L,
            customTitle = "PK Legend",
            xp = 4780,
            nextLevelXp = 5000,
            coins = 15400,
            diamonds = 4800,
            followersCount = 6390,
            followingCount = 148,
            friendsCount = 98,
            giftsReceivedCount = 2650,
            giftsSentCount = 1420,
            visitorsCount = 580,
            familyId = "fam_golden",
            isHostVerified = true,
            hostLevel = 10,
            agencyName = "Crown Talent Hub"
        ),
        UserProfile(
            uid = "host_zara",
            wefunId = "WF-510892",
            username = "zaramoon",
            nickname = "Zara Moon",
            bio = "Space Werewolf & Brain Storm enthusiast. Kind vibes & nightly trivia!",
            country = "UK",
            language = "English",
            accentColorHex = 0xFF00C2B8,
            avatarConfig = AvatarConfig("Radiant", "Sakura Bob", "Starlight Cape", "Moon Pin", "Turquoise Aura", "Cyan Glow"),
            vipLevel = 4,
            userLevel = 21,
            charmPoints = 320_000L,
            xp = 3100,
            nextLevelXp = 4500,
            coins = 4900,
            diamonds = 980,
            followersCount = 2490,
            followingCount = 305,
            friendsCount = 89,
            giftsReceivedCount = 870,
            giftsSentCount = 540,
            visitorsCount = 225,
            familyId = "fam_velvet",
            isHostVerified = true,
            hostLevel = 7,
            agencyName = "Nova Star Agency"
        ),
        UserProfile(
            uid = "host_milo",
            wefunId = "WF-338201",
            username = "milotaktik",
            nickname = "Milo Taktik",
            bio = "Happy Carrom & Oh My Cards pro. Always happy to mentor new players!",
            country = "India",
            language = "Hindi",
            accentColorHex = 0xFF3B82F6,
            avatarConfig = AvatarConfig("Clever", "Undercut", "Esports Hoodie", "Neon Visor", "Ocean Blue", "Vanguard Ring"),
            vipLevel = 3,
            userLevel = 18,
            charmPoints = 88_000L,
            xp = 2600,
            nextLevelXp = 4000,
            coins = 3600,
            diamonds = 710,
            followersCount = 1650,
            followingCount = 274,
            friendsCount = 64,
            giftsReceivedCount = 510,
            giftsSentCount = 390,
            visitorsCount = 154,
            familyId = "fam_shadow",
            isHostVerified = false,
            hostLevel = 5,
            agencyName = "Shadow Syndicate"
        ),
        UserProfile(
            uid = "host_leo",
            wefunId = "WF-779014",
            username = "leobeats",
            nickname = "Leo Beats",
            bio = "Producer, trivia buff & chill music collector.",
            country = "Singapore",
            language = "English",
            accentColorHex = 0xFFFF8A2B,
            avatarConfig = AvatarConfig("Cool", "Curly Fade", "Synth Bomber", "Gold Shades", "Sunset Glow", "Amber Halo"),
            vipLevel = 5,
            userLevel = 22,
            charmPoints = 620_000L,
            xp = 3850,
            nextLevelXp = 4500,
            coins = 7100,
            diamonds = 1640,
            followersCount = 3180,
            followingCount = 192,
            friendsCount = 81,
            giftsReceivedCount = 1120,
            giftsSentCount = 670,
            visitorsCount = 290,
            familyId = "fam_velvet",
            isHostVerified = true,
            hostLevel = 8,
            agencyName = "Pulse Creative"
        ),
        UserProfile(
            uid = "host_official",
            wefunId = "WF-000001",
            username = "wefun_official",
            nickname = "WeFun Official",
            bio = "Official WeFun community team, weekly events, carnivals & creator spotlights.",
            country = "Global",
            language = "English",
            accentColorHex = 0xFF00C2B8,
            avatarConfig = AvatarConfig("Official", "Star Crest", "Turquoise Blazer", "Verified Seal", "Aurora", "Official Crown"),
            vipLevel = 12,
            userLevel = 50,
            charmPoints = 360_000_000L,
            vipGrowthPoints = 8_500_000L,
            customTitle = "WeFun Sovereign",
            customNamecardTheme = "Celestial Gold",
            xp = 9999,
            nextLevelXp = 10000,
            coins = 99999,
            diamonds = 50000,
            followersCount = 128400,
            followingCount = 24,
            friendsCount = 120,
            giftsReceivedCount = 18500,
            giftsSentCount = 9200,
            visitorsCount = 9400,
            familyId = "fam_celestial",
            isHostVerified = true,
            hostLevel = 10,
            agencyName = "WeFun Global"
        ),
        UserProfile(
            uid = "usr_kiran",
            wefunId = "WF-409112",
            username = "kiranbeats",
            nickname = "Kiran Beats",
            bio = "Indie vocalist, friendly gamer & Guess Their Answers champion.",
            country = "India",
            language = "English",
            accentColorHex = 0xFF26B887,
            avatarConfig = AvatarConfig("Warm", "Side Sweep", "Denim Jacket", "Acoustic Pick", "Mint Breeze", "Spark Ring"),
            vipLevel = 2,
            userLevel = 15,
            xp = 1800,
            nextLevelXp = 3000,
            coins = 1950,
            diamonds = 380,
            followersCount = 840,
            followingCount = 220,
            friendsCount = 47,
            giftsReceivedCount = 290,
            giftsSentCount = 185,
            visitorsCount = 72,
            familyId = "fam_celestial",
            isHostVerified = false,
            hostLevel = 4,
            agencyName = "Independent"
        ),
    )

    fun findPublicProfile(userId: String, fallbackName: String? = null): UserProfile {
        publicProfiles.firstOrNull { it.uid == userId || it.wefunId == userId }?.let { return it }
        if (userId == "wefun_official") return publicProfiles.first { it.uid == "host_official" }
        val cleanName = fallbackName?.takeIf { it.isNotBlank() } ?: "WeFun Creator"
        val handle = cleanName.lowercase().filter { it.isLetterOrDigit() }.ifBlank { "wefunuser" }
        return UserProfile(
            uid = userId,
            wefunId = "WF-${(100000 + (userId.hashCode().let { if (it < 0) -it else it } % 899999))}",
            username = handle,
            nickname = cleanName,
            bio = "Active WeFun community member, social gamer & event fan.",
            accentColorHex = 0xFF00C2B8,
            vipLevel = 2,
            userLevel = 16,
            xp = 2100,
            nextLevelXp = 3200,
            coins = 2200,
            diamonds = 410,
            followersCount = 620,
            followingCount = 140,
            friendsCount = 42,
            giftsReceivedCount = 215,
            giftsSentCount = 130,
            visitorsCount = 65,
            isHostVerified = false,
            hostLevel = 3
        )
    }

    fun discoverMembers(category: DiscoverCategory): List<CommunityMember> = when (category) {
        DiscoverCategory.CHURCH -> listOf(
            CommunityMember("host_zara", "Zara Moon", "Fellowship Circle Host", "Grace & Light Circle • 64 members", 4, 0xFFFF4FA3),
            CommunityMember("usr_kiran", "Kiran Beats", "Worship Acoustic Lead", "Morning Harmony • 42 members", 2, 0xFF26B887),
            CommunityMember("host_rhea", "DJ Rhea", "Choir & Uplift Mentor", "Sunday Soul Hub • 98 members", 5, 0xFF7B2CFF),
        )
        DiscoverCategory.FAMILY -> listOf(
            CommunityMember("host_kai", "Kai Storm", "Golden Crown Leader", "Rank #2 • 96 House Members", 6, 0xFFFF8A2B),
            CommunityMember("host_rhea", "DJ Rhea", "Velvet Pulse Founder", "Rank #3 • 84 House Members", 5, 0xFF7B2CFF),
            CommunityMember("host_milo", "Milo Taktik", "Shadow Syndicate Captain", "Rank #4 • 110 House Members", 3, 0xFF3B82F6),
        )
        DiscoverCategory.MENTORSHIP -> listOf(
            CommunityMember("host_leo", "Leo Beats", "Audio & Hosting Coach", "14 mentees • 4.9★ Guide", 5, 0xFF3B82F6),
            CommunityMember("host_milo", "Milo Taktik", "Game Strategy Mentor", "Jackaroo & Carrom Tactics", 3, 0xFF00C2B8),
            CommunityMember("host_official", "WeFun Official", "Creator Academy", "Verified Onboarding Guides", 6, 0xFF00C2B8),
        )
        DiscoverCategory.NEARBY -> listOf(
            CommunityMember("usr_kiran", "Kiran Beats", "1.4 km away • Online", "Playing Happy Carrom", 2, 0xFF26B887),
            CommunityMember("host_rhea", "DJ Rhea", "3.2 km away • Online", "Active in Music Discovery", 5, 0xFF7B2CFF),
            CommunityMember("host_milo", "Milo Taktik", "4.8 km away • Online", "Looking for Space Werewolf squad", 3, 0xFF3B82F6),
            CommunityMember("host_zara", "Zara Moon", "6.1 km away • Online", "Posted a new Moment", 4, 0xFFFF4FA3),
        )
    }
}
