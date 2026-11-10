package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "leaderboard")
data class LeaderboardEntity(
    @PrimaryKey
    val id: String,
    val playerName: String,
    val inGameName: String,
    val freeFireUid: String,
    val rank: Int,
    val matchesPlayed: Int,
    val kills: Int,
    val placementPoints: Int,
    val totalPoints: Int,
    val totalCashWon: Int,
    val avatarTag: String = "WARRIOR"
)

@Entity(tableName = "tournament_results")
data class TournamentResultEntity(
    @PrimaryKey
    val id: String,
    val tournamentId: String,
    val tournamentTitle: String,
    val matchDate: Long,
    val mapName: String,
    val winnerIgn: String,
    val winnerUid: String,
    val winnerTeam: String? = null,
    val winnerPrize: Int,
    val runnerUpIgn: String,
    val runnerUpPrize: Int,
    val thirdPlaceIgn: String,
    val thirdPlacePrize: Int,
    val mvpIgn: String,
    val mvpKills: Int,
    val summaryNotes: String = "Official verified results by LBG ARMY Admin."
)

@Entity(tableName = "admin_settings")
data class AdminSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val adminPin: String = "7788",
    val adminUpiId: String = "lbgarmy.esports@upi",
    val adminPayeeName: String = "LBG ARMY ESPORTS",
    val supportWhatsapp: String = "+91 98765 43210",
    val supportTelegram: String = "t.me/lbgarmyesports",
    val supportEmail: String = "tournaments@lbgarmy.com",
    val fairPlayNotice: String = "Zero tolerance for modded APKs, scripts, or emulator players. Instant ban without refund."
)
