package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CardMembership
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.HeadsetMic
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.local.TournamentEntity
import com.example.data.model.MatchMode
import com.example.data.model.TournamentType
import com.example.ui.components.GamerCard
import com.example.ui.components.TournamentCard
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.EsportsRed
import com.example.ui.theme.EsportsRedContainer
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
fun HomeScreen(
    viewModel: TournamentViewModel,
    modifier: Modifier = Modifier
) {
    val tournaments by viewModel.tournaments.collectAsState()
    val allRegistrations by viewModel.allRegistrations.collectAsState()
    val currentTimeMs by viewModel.currentTimeMs.collectAsState()
    val isAdmin by viewModel.isAdminAuthenticated.collectAsState()

    var selectedFilter by remember { mutableStateOf("ALL") }

    val filteredTournaments = remember(tournaments, selectedFilter) {
        when (selectedFilter) {
            "FREE" -> tournaments.filter { it.type == TournamentType.FREE }
            "PAID" -> tournaments.filter { it.type == TournamentType.PAID }
            "SOLO" -> tournaments.filter { it.mode == MatchMode.SOLO }
            "DUO" -> tournaments.filter { it.mode == MatchMode.DUO }
            "SQUAD" -> tournaments.filter { it.mode == MatchMode.SQUAD }
            else -> tournaments
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(GamingBlack),
        contentPadding = PaddingValues(bottom = 80.dp)
    ) {
        // 1. Top Esports Branding Bar
        item {
            HomeTopBar(
                isAdmin = isAdmin,
                onAdminClick = { viewModel.navigateTo(Screen.AdminPanel) }
            )
        }

        // 2. Esports Hero Banner
        item {
            HeroBannerSection(
                onJoinPaid = { viewModel.navigateTo(Screen.PaidTournaments) },
                onJoinFree = { viewModel.navigateTo(Screen.FreeTournaments) }
            )
        }

        // 3. Quick Stats Strip
        item {
            QuickStatsStrip(
                activeCount = tournaments.count { it.status != com.example.data.model.MatchStatus.COMPLETED },
                registeredCount = allRegistrations.size + 42,
                totalPrizePool = tournaments.sumOf { it.totalPrizePool }
            )
        }

        // 4. Hub Feature Grid (Free, Paid, Upcoming, My Matches, Leaderboard, Results, Contact)
        item {
            HubFeaturesSection(
                onFreeClick = { viewModel.navigateTo(Screen.FreeTournaments) },
                onPaidClick = { viewModel.navigateTo(Screen.PaidTournaments) },
                onMyMatchesClick = { viewModel.navigateTo(Screen.MyMatches) },
                onLeaderboardClick = { viewModel.navigateTo(Screen.Leaderboard) },
                onResultsClick = { viewModel.navigateTo(Screen.Results) },
                onContactClick = { viewModel.navigateTo(Screen.ContactAdmin) },
                myMatchesCount = allRegistrations.count { it.freeFireUid == viewModel.activePlayerUid.collectAsState().value }
            )
        }

        // 5. Section Header + Filter Chips
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "LIVE & UPCOMING MATCHES",
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "${filteredTournaments.size} Available",
                        color = EsportsRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Filter Chips Row
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    val filters = listOf("ALL", "FREE", "PAID", "SOLO", "DUO", "SQUAD")
                    items(filters) { filter ->
                        val isSelected = selectedFilter == filter
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(8.dp))
                                .background(if (isSelected) EsportsRed else GamingCardSurface)
                                .border(1.dp, if (isSelected) EsportsRed else GamingBorder, RoundedCornerShape(8.dp))
                                .clickable { selectedFilter = filter }
                                .padding(horizontal = 12.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = filter,
                                color = if (isSelected) Color.White else TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }
                }
            }
        }

        // 6. Tournament Cards List
        if (filteredTournaments.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tournaments found for $selectedFilter filter.",
                        color = TextMuted,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            items(filteredTournaments, key = { it.id }) { tournament ->
                Box(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                    TournamentCard(
                        tournament = tournament,
                        currentTimeMs = currentTimeMs,
                        onClick = { viewModel.navigateTo(Screen.TournamentDetail(tournament.id)) },
                        onJoinClick = {
                            if (tournament.type == TournamentType.FREE) {
                                viewModel.navigateTo(Screen.PlayerRegistration(tournament.id))
                            } else {
                                viewModel.navigateTo(Screen.TournamentDetail(tournament.id))
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun HomeTopBar(
    isAdmin: Boolean,
    onAdminClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(GamingBlack)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(CircleShape)
                    .background(EsportsRedContainer)
                    .border(1.5.dp, EsportsRed, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "LBG ARMY",
                    tint = EsportsRed,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "LBG ARMY",
                        color = EsportsRed,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 1.2.sp
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(4.dp))
                            .background(TournamentGold.copy(alpha = 0.2f))
                            .padding(horizontal = 4.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = "ESPORTS",
                            color = TournamentGold,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
                Text(
                    text = "FREE FIRE TOURNAMENT HUB",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.8.sp
                )
            }
        }

        // Admin Button
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(if (isAdmin) SuccessGreen.copy(alpha = 0.2f) else GamingCardElevated)
                .border(1.dp, if (isAdmin) SuccessGreen else GamingBorder, RoundedCornerShape(8.dp))
                .clickable { onAdminClick() }
                .padding(horizontal = 10.dp, vertical = 6.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.AdminPanelSettings,
                    contentDescription = "Admin Mode",
                    tint = if (isAdmin) SuccessGreen else EsportsRed,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isAdmin) "ADMIN ON" else "ADMIN",
                    color = if (isAdmin) SuccessGreen else TextPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black
                )
            }
        }
    }
}

@Composable
private fun HeroBannerSection(
    onJoinPaid: () -> Unit,
    onJoinFree: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, EsportsRed.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
    ) {
        Image(
            painter = painterResource(id = R.drawable.hero_banner_1794290333130),
            contentDescription = "Tournament Banner",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.85f)
                        )
                    )
                )
        )

        // Banner Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.Bottom
        ) {
            Text(
                text = "SEASON 2026 GRAND BATTLE",
                color = TournamentGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            Text(
                text = "SOLO, DUO & SQUAD WARRIORS",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(EsportsRed)
                        .clickable { onJoinPaid() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "PAID MATCHES (₹15-₹30)",
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color.Black.copy(alpha = 0.6f))
                        .border(1.dp, SuccessGreen, RoundedCornerShape(6.dp))
                        .clickable { onJoinFree() }
                        .padding(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "FREE MATCHES",
                        color = SuccessGreen,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun QuickStatsStrip(
    activeCount: Int,
    registeredCount: Int,
    totalPrizePool: Int
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatBox(
            label = "ACTIVE MATCHES",
            value = activeCount.toString(),
            color = CyberCyan,
            modifier = Modifier.weight(1f)
        )
        StatBox(
            label = "PLAYERS JOINED",
            value = registeredCount.toString(),
            color = EsportsRed,
            modifier = Modifier.weight(1f)
        )
        StatBox(
            label = "TOTAL PRIZE",
            value = "₹$totalPrizePool",
            color = TournamentGold,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun StatBox(
    label: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(GamingCardSurface)
            .border(1.dp, GamingBorder, RoundedCornerShape(10.dp))
            .padding(vertical = 10.dp, horizontal = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = value,
                color = color,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = label,
                color = TextMuted,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun HubFeaturesSection(
    onFreeClick: () -> Unit,
    onPaidClick: () -> Unit,
    onMyMatchesClick: () -> Unit,
    onLeaderboardClick: () -> Unit,
    onResultsClick: () -> Unit,
    onContactClick: () -> Unit,
    myMatchesCount: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HubCard(
                title = "FREE TOURNAMENT",
                subtitle = "Solo • Duo • Squad",
                badge = "FREE ENTRY",
                icon = Icons.Default.SportsEsports,
                accentColor = SuccessGreen,
                onClick = onFreeClick,
                modifier = Modifier.weight(1f)
            )
            HubCard(
                title = "PAID TOURNAMENT",
                subtitle = "₹15 • ₹20 • ₹30",
                badge = "CASH PRIZES",
                icon = Icons.Default.Payments,
                accentColor = TournamentGold,
                onClick = onPaidClick,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HubCard(
                title = "MY MATCHES",
                subtitle = "Room ID & Status",
                badge = if (myMatchesCount > 0) "$myMatchesCount ACTIVE" else "VIEW PASS",
                icon = Icons.Default.CardMembership,
                accentColor = CyberCyan,
                onClick = onMyMatchesClick,
                modifier = Modifier.weight(1f)
            )
            HubCard(
                title = "LEADERBOARD",
                subtitle = "Top Fraggers & MVP",
                badge = "RANKINGS",
                icon = Icons.Default.EmojiEvents,
                accentColor = TournamentGold,
                onClick = onLeaderboardClick,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            HubCard(
                title = "MATCH RESULTS",
                subtitle = "Winners Archive",
                badge = "DECLARED",
                icon = Icons.Default.MilitaryTech,
                accentColor = EsportsRed,
                onClick = onResultsClick,
                modifier = Modifier.weight(1f)
            )
            HubCard(
                title = "CONTACT ADMIN",
                subtitle = "WhatsApp & Support",
                badge = "FAIR PLAY",
                icon = Icons.Default.HeadsetMic,
                accentColor = TextSecondary,
                onClick = onContactClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun HubCard(
    title: String,
    subtitle: String,
    badge: String,
    icon: ImageVector,
    accentColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    GamerCard(
        modifier = modifier,
        borderColor = GamingBorder,
        glowEffect = false,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(accentColor.copy(alpha = 0.15f))
                        .border(1.dp, accentColor.copy(alpha = 0.4f), RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(4.dp))
                        .background(accentColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge,
                        color = accentColor,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = subtitle,
                color = TextSecondary,
                fontSize = 11.sp
            )
        }
    }
}
