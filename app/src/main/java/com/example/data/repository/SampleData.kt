package com.example.data.repository

import com.example.data.local.AdminSettingsEntity
import com.example.data.local.LeaderboardEntity
import com.example.data.local.TournamentEntity
import com.example.data.local.TournamentResultEntity
import com.example.data.model.MatchMode
import com.example.data.model.MatchStatus
import com.example.data.model.TournamentType

object SampleData {

    fun getInitialTournaments(): List<TournamentEntity> {
        val now = System.currentTimeMillis()
        val oneHour = 3600 * 1000L
        val oneDay = 24 * oneHour

        return listOf(
            // 1. FREE SOLO TOURNAMENT
            TournamentEntity(
                id = "tour_free_solo_01",
                title = "LBG ARMY FREE SOLO CUP",
                type = TournamentType.FREE,
                mode = MatchMode.SOLO,
                entryFee = 0,
                totalSlots = 48,
                filledSlots = 32,
                mapName = "Bermuda",
                matchSchedule = now + 2 * oneHour,
                firstPrize = 100,
                secondPrize = 50,
                thirdPrize = 25,
                perKillBonus = 2,
                totalPrizePool = 200,
                roomId = "889421",
                roomPassword = "lbg",
                status = MatchStatus.REGISTRATION_OPEN,
                rules = "• Free Entry for all verified players.\n• Map: Bermuda (Classic Battle Royale).\n• Gun Properties: OFF.\n• Character Skills: ACTIVE.\n• No Grenade spamming in buildings.\n• Room ID & Password released 15 mins before match."
            ),
            // 2. PAID SOLO TOURNAMENT (₹15)
            TournamentEntity(
                id = "tour_paid_solo_15",
                title = "SOLO WARRIORS BOUNTY #104",
                type = TournamentType.PAID,
                mode = MatchMode.SOLO,
                entryFee = 15,
                totalSlots = 48,
                filledSlots = 39,
                mapName = "Purgatory",
                matchSchedule = now + 4 * oneHour,
                firstPrize = 250,
                secondPrize = 120,
                thirdPrize = 60,
                perKillBonus = 5,
                totalPrizePool = 650,
                roomId = "",
                roomPassword = "",
                status = MatchStatus.REGISTRATION_OPEN,
                rules = "• Entry Fee: ₹15 per player.\n• ₹5 cash reward per verified kill.\n• Admin manual verification of UTR within 10 minutes.\n• Strict Anti-Cheat & Screen recording on demand.\n• Emulators strictly banned."
            ),
            // 3. PAID DUO TOURNAMENT (₹20)
            TournamentEntity(
                id = "tour_paid_duo_20",
                title = "DUO CLASH OF TITANS #42",
                type = TournamentType.PAID,
                mode = MatchMode.DUO,
                entryFee = 20,
                totalSlots = 24,
                filledSlots = 18,
                mapName = "Alpine",
                matchSchedule = now + 7 * oneHour,
                firstPrize = 220,
                secondPrize = 100,
                thirdPrize = 50,
                perKillBonus = 4,
                totalPrizePool = 450,
                roomId = "",
                roomPassword = "",
                status = MatchStatus.REGISTRATION_OPEN,
                rules = "• Entry Fee: ₹20 per team (2 players).\n• Both players must submit UID.\n• Teaming with other duos will lead to zero prize and instant ban.\n• Map: Alpine."
            ),
            // 4. PAID SQUAD TOURNAMENT (₹30)
            TournamentEntity(
                id = "tour_paid_squad_30",
                title = "LBG ARMY PRO SQUAD LEAGUE",
                type = TournamentType.PAID,
                mode = MatchMode.SQUAD,
                entryFee = 30,
                totalSlots = 12,
                filledSlots = 9,
                mapName = "Kalahari",
                matchSchedule = now + 12 * oneHour,
                firstPrize = 200,
                secondPrize = 100,
                thirdPrize = 40,
                perKillBonus = 5,
                totalPrizePool = 360,
                roomId = "",
                roomPassword = "",
                status = MatchStatus.REGISTRATION_OPEN,
                rules = "• Entry Fee: ₹30 per squad (4 players).\n• 12 Teams battle for Grand Trophy.\n• Top 3 teams get instant UPI payout.\n• Map: Kalahari."
            ),
            // 5. FREE SQUAD TOURNAMENT
            TournamentEntity(
                id = "tour_free_squad_02",
                title = "SUNDAY SQUAD BATTLE ROYALE",
                type = TournamentType.FREE,
                mode = MatchMode.SQUAD,
                entryFee = 0,
                totalSlots = 12,
                filledSlots = 12,
                mapName = "Bermuda",
                matchSchedule = now + 24 * oneHour,
                firstPrize = 150,
                secondPrize = 75,
                thirdPrize = 0,
                perKillBonus = 0,
                totalPrizePool = 225,
                roomId = "",
                roomPassword = "",
                status = MatchStatus.REGISTRATION_FULL,
                rules = "• Free entry squad showdown.\n• Slots full - join waiting list or register for Paid matches."
            ),
            // 6. COMPLETED PAST TOURNAMENT
            TournamentEntity(
                id = "tour_completed_grand",
                title = "LBG ARMY GRAND CHAMPIONSHIP",
                type = TournamentType.PAID,
                mode = MatchMode.SOLO,
                entryFee = 15,
                totalSlots = 48,
                filledSlots = 48,
                mapName = "Bermuda",
                matchSchedule = now - 2 * oneDay,
                firstPrize = 300,
                secondPrize = 150,
                thirdPrize = 75,
                perKillBonus = 5,
                totalPrizePool = 750,
                roomId = "771203",
                roomPassword = "ggwp",
                status = MatchStatus.COMPLETED,
                rules = "Completed match. Winner declared and prizes sent via UPI."
            )
        )
    }

    fun getInitialLeaderboard(): List<LeaderboardEntity> {
        return listOf(
            LeaderboardEntity(
                id = "lead_1",
                playerName = "Rahul Verma",
                inGameName = "LBG_TITAN⚡",
                freeFireUid = "1049281723",
                rank = 1,
                matchesPlayed = 24,
                kills = 88,
                placementPoints = 140,
                totalPoints = 316,
                totalCashWon = 1850,
                avatarTag = "MVP"
            ),
            LeaderboardEntity(
                id = "lead_2",
                playerName = "Sahil Khan",
                inGameName = "GHOST_REAPER☠",
                freeFireUid = "1938472910",
                rank = 2,
                matchesPlayed = 20,
                kills = 74,
                placementPoints = 125,
                totalPoints = 273,
                totalCashWon = 1320,
                avatarTag = "PRO"
            ),
            LeaderboardEntity(
                id = "lead_3",
                playerName = "Aman Sharma",
                inGameName = "VIPER_HUNTER🎯",
                freeFireUid = "1726354891",
                rank = 3,
                matchesPlayed = 18,
                kills = 61,
                placementPoints = 110,
                totalPoints = 232,
                totalCashWon = 940,
                avatarTag = "SNIPER"
            ),
            LeaderboardEntity(
                id = "lead_4",
                playerName = "Vikram Das",
                inGameName = "DARK_SOUL🔥",
                freeFireUid = "1654321987",
                rank = 4,
                matchesPlayed = 15,
                kills = 49,
                placementPoints = 85,
                totalPoints = 183,
                totalCashWon = 680,
                avatarTag = "RUSHER"
            ),
            LeaderboardEntity(
                id = "lead_5",
                playerName = "Karan Patel",
                inGameName = "THUNDER_GOD⚡",
                freeFireUid = "1987654321",
                rank = 5,
                matchesPlayed = 14,
                kills = 42,
                placementPoints = 80,
                totalPoints = 164,
                totalCashWon = 520,
                avatarTag = "WARRIOR"
            )
        )
    }

    fun getInitialResults(): List<TournamentResultEntity> {
        val now = System.currentTimeMillis()
        val oneDay = 24 * 3600 * 1000L

        return listOf(
            TournamentResultEntity(
                id = "res_01",
                tournamentId = "tour_completed_grand",
                tournamentTitle = "LBG ARMY GRAND CHAMPIONSHIP",
                matchDate = now - 2 * oneDay,
                mapName = "Bermuda",
                winnerIgn = "LBG_TITAN⚡",
                winnerUid = "1049281723",
                winnerPrize = 300,
                runnerUpIgn = "GHOST_REAPER☠",
                runnerUpPrize = 150,
                thirdPlaceIgn = "VIPER_HUNTER🎯",
                thirdPlacePrize = 75,
                mvpIgn = "LBG_TITAN⚡",
                mvpKills = 9,
                summaryNotes = "Grand Championship concluded with epic 1v1 showdown in Final Zone. ₹300 transferred to LBG_TITAN."
            ),
            TournamentResultEntity(
                id = "res_02",
                tournamentId = "tour_completed_duo_09",
                tournamentTitle = "DUO BATTLE OF THE BRAVE #41",
                matchDate = now - 5 * oneDay,
                mapName = "Purgatory",
                winnerIgn = "VIPER_HUNTER🎯 & VIPER_VIP",
                winnerUid = "1726354891",
                winnerTeam = "VIPER CLAN",
                winnerPrize = 220,
                runnerUpIgn = "DARK_SOUL🔥 & FIRE_BOY",
                runnerUpPrize = 100,
                thirdPlaceIgn = "THUNDER_GOD⚡ & STORM",
                thirdPlacePrize = 50,
                mvpIgn = "VIPER_HUNTER🎯",
                mvpKills = 11,
                summaryNotes = "Viper Clan dominated Purgatory with 16 total team kills. Instant UPI payout confirmed."
            )
        )
    }

    fun getInitialAdminSettings(): AdminSettingsEntity {
        return AdminSettingsEntity(
            id = 1,
            adminPin = "7788",
            adminUpiId = "lbgarmy.esports@upi",
            adminPayeeName = "LBG ARMY ESPORTS",
            supportWhatsapp = "+91 98765 43210",
            supportTelegram = "t.me/lbgarmyesports",
            supportEmail = "tournaments@lbgarmy.com",
            fairPlayNotice = "100% Verified Esports Tournaments. No hacks, no emulators, instant prize payouts."
        )
    }
}
