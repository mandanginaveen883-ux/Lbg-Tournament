package com.example.ui.screens

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
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MilitaryTech
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.SportsEsports
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.TournamentEntity
import com.example.data.model.MatchMode
import com.example.data.model.TournamentType
import com.example.ui.components.GamerCard
import com.example.ui.components.TournamentCard
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.EsportsRed
import com.example.ui.theme.GamingBlack
import com.example.ui.theme.GamingBorder
import com.example.ui.theme.GamingCardSurface
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TournamentGold
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.TournamentViewModel

@Composable
fun FreeTournamentsScreen(
    viewModel: TournamentViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tournaments by viewModel.tournaments.collectAsState()
    val currentTimeMs by viewModel.currentTimeMs.collectAsState()
    var selectedModeTab by remember { mutableStateOf(0) } // 0: ALL, 1: SOLO, 2: DUO, 3: SQUAD

    val freeTournaments = tournaments.filter { it.type == TournamentType.FREE || it.entryFee == 0 }
    val filteredList = when (selectedModeTab) {
        1 -> freeTournaments.filter { it.mode == MatchMode.SOLO }
        2 -> freeTournaments.filter { it.mode == MatchMode.DUO }
        3 -> freeTournaments.filter { it.mode == MatchMode.SQUAD }
        else -> freeTournaments
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GamingBlack)
    ) {
        // Top Header
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
                    text = "FREE TOURNAMENTS",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Zero Entry Fee • Real Diamond & Cash Rewards",
                    color = SuccessGreen,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Mode Tabs (Solo, Duo, Squad)
        val tabs = listOf("ALL MODES", "SOLO (48)", "DUO (24)", "SQUAD (12)")
        TabRow(
            selectedTabIndex = selectedModeTab,
            containerColor = GamingCardSurface,
            contentColor = SuccessGreen,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedModeTab]),
                    color = SuccessGreen
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedModeTab == index,
                    onClick = { selectedModeTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 11.sp,
                            fontWeight = if (selectedModeTab == index) FontWeight.Black else FontWeight.Bold,
                            color = if (selectedModeTab == index) SuccessGreen else TextSecondary
                        )
                    }
                )
            }
        }

        // Info Banner
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(SuccessGreen.copy(alpha = 0.1f))
                .border(1.dp, SuccessGreen.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                .padding(12.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = null,
                    tint = SuccessGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Free tournaments give instant entry! Room ID and Password will be visible under 'My Matches' 15 minutes before the match.",
                    color = TextPrimary,
                    fontSize = 11.sp,
                    lineHeight = 16.sp
                )
            }
        }

        // List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (filteredList.isEmpty()) {
                item {
                    Text(
                        text = "No free tournaments currently available in this mode.",
                        color = TextMuted,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp)
                    )
                }
            } else {
                items(filteredList, key = { it.id }) { tournament ->
                    TournamentCard(
                        tournament = tournament,
                        currentTimeMs = currentTimeMs,
                        onClick = { viewModel.navigateTo(Screen.TournamentDetail(tournament.id)) },
                        onJoinClick = { viewModel.navigateTo(Screen.PlayerRegistration(tournament.id)) }
                    )
                }
            }
        }
    }
}

@Composable
fun PaidTournamentsScreen(
    viewModel: TournamentViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val tournaments by viewModel.tournaments.collectAsState()
    val currentTimeMs by viewModel.currentTimeMs.collectAsState()
    var selectedFeeTab by remember { mutableStateOf(0) } // 0: ALL, 1: SOLO ₹15, 2: DUO ₹20, 3: SQUAD ₹30

    val paidTournaments = tournaments.filter { it.type == TournamentType.PAID && it.entryFee > 0 }
    val filteredList = when (selectedFeeTab) {
        1 -> paidTournaments.filter { it.entryFee == 15 }
        2 -> paidTournaments.filter { it.entryFee == 20 }
        3 -> paidTournaments.filter { it.entryFee == 30 }
        else -> paidTournaments
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GamingBlack)
    ) {
        // Top Header
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
                    text = "PAID TOURNAMENTS",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Solo ₹15 • Duo ₹20 • Squad ₹30 • High Cash Prize Pool",
                    color = TournamentGold,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Fee Tabs (Solo ₹15, Duo ₹20, Squad ₹30)
        val tabs = listOf("ALL PAID", "SOLO (₹15)", "DUO (₹20)", "SQUAD (₹30)")
        TabRow(
            selectedTabIndex = selectedFeeTab,
            containerColor = GamingCardSurface,
            contentColor = TournamentGold,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedFeeTab]),
                    color = TournamentGold
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedFeeTab == index,
                    onClick = { selectedFeeTab = index },
                    text = {
                        Text(
                            text = title,
                            fontSize = 11.sp,
                            fontWeight = if (selectedFeeTab == index) FontWeight.Black else FontWeight.Bold,
                            color = if (selectedFeeTab == index) TournamentGold else TextSecondary
                        )
                    }
                )
            }
        }

        // Prize Breakdown Overview Strip
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FeePill(
                mode = "SOLO",
                fee = "₹15",
                perKill = "+₹5/Kill",
                modifier = Modifier.weight(1f)
            )
            FeePill(
                mode = "DUO",
                fee = "₹20",
                perKill = "+₹4/Kill",
                modifier = Modifier.weight(1f)
            )
            FeePill(
                mode = "SQUAD",
                fee = "₹30",
                perKill = "+₹5/Kill",
                modifier = Modifier.weight(1f)
            )
        }

        // List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 40.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (filteredList.isEmpty()) {
                item {
                    Text(
                        text = "No paid tournaments available in this category.",
                        color = TextMuted,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp)
                    )
                }
            } else {
                items(filteredList, key = { it.id }) { tournament ->
                    TournamentCard(
                        tournament = tournament,
                        currentTimeMs = currentTimeMs,
                        onClick = { viewModel.navigateTo(Screen.TournamentDetail(tournament.id)) },
                        onJoinClick = { viewModel.navigateTo(Screen.TournamentDetail(tournament.id)) }
                    )
                }
            }
        }
    }
}

@Composable
private fun FeePill(
    mode: String,
    fee: String,
    perKill: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(GamingCardSurface)
            .border(1.dp, GamingBorder, RoundedCornerShape(8.dp))
            .padding(vertical = 8.dp, horizontal = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = mode,
                color = TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = fee,
                color = TournamentGold,
                fontSize = 15.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                text = perKill,
                color = SuccessGreen,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
