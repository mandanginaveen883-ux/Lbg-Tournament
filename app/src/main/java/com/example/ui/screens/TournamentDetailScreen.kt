package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MatchStatus
import com.example.data.model.TournamentType
import com.example.ui.components.CountdownTimerView
import com.example.ui.components.GamerButton
import com.example.ui.components.GamerCard
import com.example.ui.components.ModeBadge
import com.example.ui.components.SlotsProgressBar
import com.example.ui.components.StatusBadge
import com.example.ui.components.TypeBadge
import com.example.ui.components.formatScheduleDate
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.EsportsRed
import com.example.ui.theme.GamingBlack
import com.example.ui.theme.GamingBorder
import com.example.ui.theme.GamingCardElevated
import com.example.ui.theme.GamingCardSurface
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TournamentGold
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.TournamentViewModel

@Composable
fun TournamentDetailScreen(
    tournamentId: String,
    viewModel: TournamentViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tournaments by viewModel.tournaments.collectAsState()
    val currentTimeMs by viewModel.currentTimeMs.collectAsState()
    val tournament = tournaments.find { it.id == tournamentId }

    if (tournament == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(GamingBlack),
            contentAlignment = Alignment.Center
        ) {
            Text("Tournament not found", color = TextPrimary)
        }
        return
    }

    val isFull = tournament.filledSlots >= tournament.totalSlots
    val isCompleted = tournament.status == MatchStatus.COMPLETED

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GamingBlack)
    ) {
        // App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = "MATCH DETAILS",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header Card
            GamerCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = EsportsRed.copy(alpha = 0.5f),
                glowEffect = true
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ModeBadge(mode = tournament.mode)
                            TypeBadge(type = tournament.type, entryFee = tournament.entryFee)
                        }
                        StatusBadge(status = tournament.status)
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = tournament.title,
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CalendarToday,
                                contentDescription = null,
                                tint = EsportsRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = formatScheduleDate(tournament.matchSchedule),
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Map: ${tournament.mapName}",
                                color = TextPrimary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    CountdownTimerView(
                        targetTimeMs = tournament.matchSchedule,
                        currentTimeMs = currentTimeMs,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Slots Status
            GamerCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SlotsProgressBar(
                        filled = tournament.filledSlots,
                        total = tournament.totalSlots
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Prize Distribution Breakdown (Mandatory requirement #5)
            GamerCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = TournamentGold.copy(alpha = 0.5f)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = TournamentGold,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "PRIZE DISTRIBUTION",
                                color = TournamentGold,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 1.sp
                            )
                        }

                        Text(
                            text = "TOTAL: ₹${tournament.totalPrizePool}",
                            color = TournamentGold,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    HorizontalDivider(color = GamingBorder)
                    Spacer(modifier = Modifier.height(12.dp))

                    PrizeRow(rank = "1ST PLACE (BOOYAH)", prize = "₹${tournament.firstPrize}", isWinner = true)
                    PrizeRow(rank = "2ND PLACE (RUNNER UP)", prize = "₹${tournament.secondPrize}")
                    if (tournament.thirdPrize > 0) {
                        PrizeRow(rank = "3RD PLACE", prize = "₹${tournament.thirdPrize}")
                    }
                    if (tournament.perKillBonus > 0) {
                        PrizeRow(rank = "PER KILL BOUNTY", prize = "₹${tournament.perKillBonus} / Kill", isSpecial = true)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Room ID & Credentials Notice
            GamerCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = CyberCyan,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "ROOM CREDENTIALS POLICY",
                            color = CyberCyan,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "• Room ID and Password will be displayed directly inside 'My Matches' 15 minutes before match start.\n• Only players with Confirmed Registration will be allowed in the custom room.\n• Unregistered players entering the room will be immediately kicked.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Match Rules
            GamerCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Gavel,
                            contentDescription = null,
                            tint = EsportsRed,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "RULES & FAIR PLAY",
                            color = EsportsRed,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black,
                            letterSpacing = 0.8.sp
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = tournament.rules,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))
        }

        // Bottom Fixed CTA
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(GamingCardSurface)
                .border(1.dp, GamingBorder)
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "ENTRY FEE",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = if (tournament.entryFee == 0) "FREE" else "₹${tournament.entryFee}",
                        color = if (tournament.entryFee == 0) SuccessGreen else TournamentGold,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Black
                    )
                }

                GamerButton(
                    text = when {
                        isCompleted -> "MATCH COMPLETED"
                        isFull -> "SLOTS FULL"
                        tournament.entryFee == 0 -> "REGISTER FREE"
                        else -> "JOIN FOR ₹${tournament.entryFee}"
                    },
                    onClick = {
                        viewModel.navigateTo(Screen.PlayerRegistration(tournament.id))
                    },
                    enabled = !isFull && !isCompleted,
                    modifier = Modifier.width(220.dp)
                )
            }
        }
    }
}

@Composable
private fun PrizeRow(
    rank: String,
    prize: String,
    isWinner: Boolean = false,
    isSpecial: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = rank,
            color = if (isWinner) Color.White else if (isSpecial) CyberCyan else TextSecondary,
            fontSize = 12.sp,
            fontWeight = if (isWinner || isSpecial) FontWeight.Black else FontWeight.SemiBold
        )
        Text(
            text = prize,
            color = if (isWinner) TournamentGold else if (isSpecial) SuccessGreen else TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Black
        )
    }
}
