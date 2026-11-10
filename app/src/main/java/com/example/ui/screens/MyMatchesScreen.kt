package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PlayerRegistrationEntity
import com.example.data.local.TournamentEntity
import com.example.data.model.MatchStatus
import com.example.data.model.RegistrationStatus
import com.example.ui.components.CountdownTimerView
import com.example.ui.components.GamerButton
import com.example.ui.components.GamerCard
import com.example.ui.components.RegistrationStatusBadge
import com.example.ui.components.formatScheduleDate
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.EsportsRed
import com.example.ui.theme.EsportsRedContainer
import com.example.ui.theme.GamingBlack
import com.example.ui.theme.GamingBorder
import com.example.ui.theme.GamingCardElevated
import com.example.ui.theme.GamingCardSurface
import com.example.ui.theme.PendingYellow
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TournamentGold
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.TournamentViewModel

@Composable
fun MyMatchesScreen(
    viewModel: TournamentViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allRegistrations by viewModel.allRegistrations.collectAsState()
    val tournaments by viewModel.tournaments.collectAsState()
    val currentTimeMs by viewModel.currentTimeMs.collectAsState()

    var selectedTab by remember { mutableStateOf(0) } // 0: ACTIVE/REGISTERED, 1: COMPLETED/ALL

    val activeRegistrations = allRegistrations.filter {
        it.registrationStatus == RegistrationStatus.CONFIRMED ||
                it.registrationStatus == RegistrationStatus.PAYMENT_SUBMITTED ||
                it.registrationStatus == RegistrationStatus.AWAITING_PAYMENT
    }

    val displayList = if (selectedTab == 0) activeRegistrations else allRegistrations

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
            Column {
                Text(
                    text = "MY MATCHES",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Room Credentials & Match Passes",
                    color = CyberCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Tabs
        val tabs = listOf("ACTIVE (${activeRegistrations.size})", "ALL HISTORY (${allRegistrations.size})")
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = GamingCardSurface,
            contentColor = CyberCyan,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = CyberCyan
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 12.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Black else FontWeight.Bold,
                            color = if (selectedTab == index) CyberCyan else TextSecondary
                        )
                    }
                )
            }
        }

        if (displayList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.CardMembership,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(54.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (selectedTab == 0) "No active tournament registrations." else "You haven't joined any tournaments yet.",
                        color = TextPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Join Free or Paid tournaments to compete for cash rewards!",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    GamerButton(
                        text = "EXPLORE TOURNAMENTS",
                        onClick = onBack,
                        modifier = Modifier.width(220.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(displayList, key = { it.id }) { reg ->
                    val tournament = tournaments.find { it.id == reg.tournamentId }
                    MyMatchTicketCard(
                        registration = reg,
                        tournament = tournament,
                        currentTimeMs = currentTimeMs,
                        onResumePayment = {
                            viewModel.navigateTo(Screen.PaymentGateway(reg.id))
                        },
                        onViewTournament = {
                            viewModel.navigateTo(Screen.TournamentDetail(reg.tournamentId))
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun MyMatchTicketCard(
    registration: PlayerRegistrationEntity,
    tournament: TournamentEntity?,
    currentTimeMs: Long,
    onResumePayment: () -> Unit,
    onViewTournament: () -> Unit
) {
    val context = LocalContext.current
    val isConfirmed = registration.registrationStatus == RegistrationStatus.CONFIRMED
    val isVerifying = registration.registrationStatus == RegistrationStatus.PAYMENT_SUBMITTED
    val isAwaiting = registration.registrationStatus == RegistrationStatus.AWAITING_PAYMENT
    val isExpired = registration.registrationStatus == RegistrationStatus.EXPIRED
    val isRejected = registration.registrationStatus == RegistrationStatus.REJECTED

    val matchSchedule = tournament?.matchSchedule ?: registration.registeredAt
    val diffToMatchMs = matchSchedule - currentTimeMs
    // Room ID is revealed only if Confirmed AND (Admin set it) AND (within 15 minutes or past start)
    val hasRoomCredentials = isConfirmed && !tournament?.roomId.isNullOrEmpty()

    GamerCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = if (isConfirmed) SuccessGreen.copy(alpha = 0.5f) else GamingBorder,
        glowEffect = isConfirmed
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Header bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(GamingCardElevated)
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(TournamentGold.copy(alpha = 0.2f))
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "SLOT #${registration.assignedSlotNumber ?: "PENDING"}",
                                color = TournamentGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    RegistrationStatusBadge(status = registration.registrationStatus)
                }
            }

            // Ticket Body
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = registration.tournamentTitle,
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Schedule and Map Info
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            tint = EsportsRed,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = formatScheduleDate(matchSchedule),
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (tournament != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = null,
                                tint = CyberCyan,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = tournament.mapName,
                                color = TextSecondary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Player Info Row
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Black.copy(alpha = 0.35f))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "IGN: ${registration.inGameName}",
                        color = TextPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "UID: ${registration.freeFireUid}",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Room ID & Password Box
                if (isConfirmed) {
                    if (hasRoomCredentials) {
                        // REVEALED
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(EsportsRedContainer.copy(alpha = 0.5f))
                                .border(1.dp, EsportsRed, RoundedCornerShape(10.dp))
                                .padding(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = "ROOM ID & PASSWORD",
                                        color = EsportsRed,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black,
                                        letterSpacing = 1.sp
                                    )
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Room ID: ${tournament?.roomId}  |  Pass: ${tournament?.roomPassword}",
                                        color = Color.White,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Black,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }

                                Row(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(6.dp))
                                        .background(EsportsRed)
                                        .clickable {
                                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                            val copyText = "Room ID: ${tournament?.roomId}\nPass: ${tournament?.roomPassword}"
                                            clipboard.setPrimaryClip(ClipData.newPlainText("Room Credentials", copyText))
                                            Toast.makeText(context, "Room ID & Pass Copied!", Toast.LENGTH_SHORT).show()
                                        }
                                        .padding(horizontal = 8.dp, vertical = 6.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "Copy",
                                        tint = Color.White,
                                        modifier = Modifier.size(13.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = "COPY",
                                        color = Color.White,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    } else {
                        // LOCKED / NOT YET RELEASED
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(GamingCardSurface)
                                .border(1.dp, GamingBorder, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Lock,
                                    contentDescription = null,
                                    tint = CyberCyan,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Room ID & Password will be unlocked 15 minutes before the match start.",
                                    color = TextSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                } else if (isVerifying) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(PendingYellow.copy(alpha = 0.1f))
                            .border(1.dp, PendingYellow.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "UTR ${registration.utrNumber} submitted. Admin is verifying bank receipt. Slot will be confirmed shortly.",
                            color = PendingYellow,
                            fontSize = 11.sp
                        )
                    }
                } else if (isAwaiting) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Payment Pending (10m window)",
                            color = TournamentGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        GamerButton(
                            text = "COMPLETE PAYMENT",
                            onClick = onResumePayment,
                            modifier = Modifier.height(36.dp)
                        )
                    }
                } else if (isRejected) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(EsportsRed.copy(alpha = 0.12f))
                            .border(1.dp, EsportsRed.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                            .padding(10.dp)
                    ) {
                        Text(
                            text = "Registration Rejected: ${registration.rejectionReason ?: "Invalid UTR or verification failed"}. Contact admin for refund/support.",
                            color = EsportsRed,
                            fontSize = 11.sp
                        )
                    }
                } else if (isExpired) {
                    Text(
                        text = "Registration Expired: 10-minute payment countdown elapsed.",
                        color = Color.LightGray,
                        fontSize = 11.sp
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Bottom row with live countdown or match view
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CountdownTimerView(
                        targetTimeMs = matchSchedule,
                        currentTimeMs = currentTimeMs
                    )

                    Text(
                        text = "VIEW TOURNAMENT >",
                        color = EsportsRed,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black,
                        modifier = Modifier.clickable { onViewTournament() }
                    )
                }
            }
        }
    }
}
