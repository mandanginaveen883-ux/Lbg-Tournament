package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.MatchMode
import com.example.data.model.MatchStatus
import com.example.data.model.TournamentType

@Entity(tableName = "tournaments")
data class TournamentEntity(
    @PrimaryKey
    val id: String,
    val title: String,
    val type: TournamentType,
    val mode: MatchMode,
    val entryFee: Int,
    val totalSlots: Int,
    val filledSlots: Int = 0,
    val mapName: String, // Bermuda, Purgatory, Kalahari, Alpine
    val matchSchedule: Long, // timestamp in ms
    val firstPrize: Int,
    val secondPrize: Int,
    val thirdPrize: Int,
    val perKillBonus: Int,
    val totalPrizePool: Int,
    val roomId: String = "",
    val roomPassword: String = "",
    val status: MatchStatus = MatchStatus.REGISTRATION_OPEN,
    val rules: String = "1. Emulator players strictly forbidden.\n2. Gun attributes OFF.\n3. Grenades restricted in Clash Squad.\n4. Teaming up will result in permanent ban.\n5. Screenshot proof mandatory for kill rewards.",
    val createdAt: Long = System.currentTimeMillis()
)
