package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.PlayerRegistrationEntity
import com.example.data.local.TournamentEntity
import com.example.data.model.MatchMode
import com.example.data.model.MatchStatus
import com.example.data.model.TournamentType
import com.example.ui.components.GamerButton
import com.example.ui.components.GamerCard
import com.example.ui.components.ModeBadge
import com.example.ui.components.TypeBadge
import com.example.ui.components.formatScheduleDate
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.EsportsRed
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
import com.example.ui.viewmodel.TournamentViewModel

@Composable
fun AdminPanelScreen(
    viewModel: TournamentViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isAuthenticated by viewModel.isAdminAuthenticated.collectAsState()

    if (!isAuthenticated) {
        AdminLoginDialog(
            viewModel = viewModel,
            onBack = onBack
        )
    } else {
        AdminDashboard(
            viewModel = viewModel,
            onBack = onBack,
            modifier = modifier
        )
    }
}

@Composable
private fun AdminLoginDialog(
    viewModel: TournamentViewModel,
    onBack: () -> Unit
) {
    var pinInput by remember { mutableStateOf("") }
    var errorMsg by remember { mutableStateOf<String?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(GamingBlack)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        GamerCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = EsportsRed,
            glowEffect = true
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(50.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(EsportsRed.copy(alpha = 0.2f))
                        .border(1.dp, EsportsRed, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = EsportsRed,
                        modifier = Modifier.size(28.dp)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Text(
                    text = "LBG ARMY ADMIN LOGIN",
                    color = TextPrimary,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Enter master PIN to manage tournaments & verify payments",
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                OutlinedTextField(
                    value = pinInput,
                    onValueChange = {
                        if (it.length <= 6) {
                            pinInput = it.filter { char -> char.isDigit() }
                            errorMsg = null
                        }
                    },
                    label = { Text("Admin Master PIN") },
                    placeholder = { Text("Default is 7788") },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("admin_pin_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EsportsRed,
                        unfocusedBorderColor = GamingBorder,
                        focusedLabelColor = EsportsRed,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    )
                )

                if (errorMsg != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(text = errorMsg!!, color = EsportsRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Default Master PIN: 7788 (Can be changed in settings)",
                    color = TournamentGold,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onBack,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("CANCEL", color = TextSecondary)
                    }

                    Button(
                        onClick = {
                            val success = viewModel.loginAdmin(pinInput)
                            if (!success) {
                                errorMsg = "Incorrect PIN. Default is 7788"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = EsportsRed),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("admin_login_submit")
                    ) {
                        Text("LOGIN", fontWeight = FontWeight.Black, color = Color.White)
                    }
                }
            }
        }
    }
}

@Composable
private fun AdminDashboard(
    viewModel: TournamentViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("PAYMENTS (UTR)", "TOURNAMENTS", "ROOM CREDENTIALS", "DECLARE WINNERS", "SETTINGS")

    val pendingVerifications by viewModel.pendingVerifications.collectAsState()
    val tournaments by viewModel.tournaments.collectAsState()
    val allRegistrations by viewModel.allRegistrations.collectAsState()
    val adminSettings by viewModel.adminSettings.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(GamingBlack)
    ) {
        // Top App Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
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
                        text = "ADMIN CONTROL PANEL",
                        color = TextPrimary,
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "LBG ARMY ESPORTS MANAGEMENT",
                        color = EsportsRed,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            TextButton(onClick = { viewModel.logoutAdmin() }) {
                Text("LOGOUT", color = EsportsRed, fontWeight = FontWeight.Black, fontSize = 11.sp)
            }
        }

        // Horizontal Scrollable Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedTab,
            containerColor = GamingCardSurface,
            contentColor = EsportsRed,
            edgePadding = 12.dp,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = EsportsRed
                )
            }
        ) {
            tabs.forEachIndexed { index, title ->
                val countBadge = if (index == 0) " (${pendingVerifications.size})" else ""
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    text = {
                        Text(
                            text = title + countBadge,
                            fontSize = 11.sp,
                            fontWeight = if (selectedTab == index) FontWeight.Black else FontWeight.Bold,
                            color = if (selectedTab == index) EsportsRed else TextSecondary
                        )
                    }
                )
            }
        }

        // Tab Content
        when (selectedTab) {
            0 -> AdminPaymentsTab(
                pendingVerifications = pendingVerifications,
                onApprove = { regId -> viewModel.approvePayment(regId) },
                onReject = { regId, reason -> viewModel.rejectPayment(regId, reason) }
            )
            1 -> AdminTournamentsTab(
                tournaments = tournaments,
                viewModel = viewModel
            )
            2 -> AdminRoomCredentialsTab(
                tournaments = tournaments,
                onSaveCredentials = { tourId, rId, rPass, status ->
                    viewModel.updateRoomCredentials(tourId, rId, rPass, status)
                }
            )
            3 -> AdminDeclareWinnersTab(
                tournaments = tournaments,
                onDeclare = { tourId, wIgn, wUid, wPrize, rIgn, rPrize, tIgn, tPrize, mvp, kills, notes ->
                    viewModel.declareTournamentResult(
                        tournamentId = tourId,
                        winnerIgn = wIgn,
                        winnerUid = wUid,
                        winnerPrize = wPrize,
                        runnerUpIgn = rIgn,
                        runnerUpPrize = rPrize,
                        thirdPlaceIgn = tIgn,
                        thirdPlacePrize = tPrize,
                        mvpIgn = mvp,
                        mvpKills = kills,
                        notes = notes,
                        onDone = { selectedTab = 0 }
                    )
                }
            )
            4 -> AdminSettingsTab(
                settings = adminSettings,
                onSave = { updated -> viewModel.updateAdminSettings(updated) }
            )
        }
    }
}

// --- TAB 1: PAYMENTS APPROVAL (UTR) ---
@Composable
private fun AdminPaymentsTab(
    pendingVerifications: List<PlayerRegistrationEntity>,
    onApprove: (String) -> Unit,
    onReject: (String, String) -> Unit
) {
    var rejectDialogReg by remember { mutableStateOf<PlayerRegistrationEntity?>(null) }
    var rejectReason by remember { mutableStateOf("No bank credit received for this UTR") }

    if (rejectDialogReg != null) {
        AlertDialog(
            onDismissRequest = { rejectDialogReg = null },
            title = { Text("Reject Payment Proof", color = TextPrimary) },
            text = {
                Column {
                    Text("Enter rejection reason for player:", color = TextSecondary, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = rejectReason,
                        onValueChange = { rejectReason = it },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onReject(rejectDialogReg!!.id, rejectReason)
                        rejectDialogReg = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = EsportsRed)
                ) {
                    Text("REJECT", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { rejectDialogReg = null }) {
                    Text("CANCEL", color = TextSecondary)
                }
            },
            containerColor = GamingCardSurface
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PENDING UTR PROOFS (${pendingVerifications.size})",
                    color = TournamentGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "Verify in Bank Portal First",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        }

        if (pendingVerifications.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No pending payment proofs. All submissions verified!",
                        color = TextMuted,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            items(pendingVerifications, key = { it.id }) { reg ->
                GamerCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = PendingYellow.copy(alpha = 0.5f)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = reg.tournamentTitle,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Black
                            )
                            Text(
                                text = "₹${reg.entryFee}",
                                color = TournamentGold,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Black
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = "Applicant: ${reg.playerName} • IGN: ${reg.inGameName}",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Free Fire UID: ${reg.freeFireUid} • Phone: ${reg.mobileNumber}",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // UTR Display Box
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color.Black.copy(alpha = 0.4f))
                                .border(1.dp, GamingBorder, RoundedCornerShape(8.dp))
                                .padding(10.dp)
                        ) {
                            Column {
                                Text(
                                    text = "SUBMITTED UTR / TRANSACTION ID",
                                    color = TextMuted,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                                Text(
                                    text = reg.utrNumber ?: "N/A",
                                    color = TournamentGold,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Black,
                                    fontFamily = FontFamily.Monospace
                                )
                                if (!reg.paymentProofNote.isNullOrEmpty()) {
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Note: ${reg.paymentProofNote}",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // Approve / Reject Actions
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = { rejectDialogReg = reg },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = EsportsRed)
                            ) {
                                Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("REJECT", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }

                            Button(
                                onClick = { onApprove(reg.id) },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)
                            ) {
                                Icon(imageVector = Icons.Default.Check, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("CONFIRM SLOT", fontWeight = FontWeight.Black, fontSize = 11.sp, color = Color.Black)
                            }
                        }
                    }
                }
            }
        }
    }
}

// --- TAB 2: TOURNAMENTS MANAGEMENT ---
@Composable
private fun AdminTournamentsTab(
    tournaments: List<TournamentEntity>,
    viewModel: TournamentViewModel
) {
    var showCreateDialog by remember { mutableStateOf(false) }

    if (showCreateDialog) {
        CreateTournamentDialog(
            viewModel = viewModel,
            onDismiss = { showCreateDialog = false }
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "ALL TOURNAMENTS (${tournaments.size})",
                    color = TextPrimary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Black
                )

                Button(
                    onClick = { showCreateDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = EsportsRed),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("CREATE NEW", fontSize = 11.sp, fontWeight = FontWeight.Black)
                }
            }
        }

        items(tournaments, key = { it.id }) { tour ->
            GamerCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = tour.title,
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = { viewModel.deleteTournament(tour.id) }) {
                            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = EsportsRed)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Mode: ${tour.mode.name} • Fee: ₹${tour.entryFee} • Slots: ${tour.filledSlots}/${tour.totalSlots} • Map: ${tour.mapName}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Schedule: ${formatScheduleDate(tour.matchSchedule)} • Status: ${tour.status.name}",
                        color = TournamentGold,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

// --- CREATE TOURNAMENT DIALOG ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CreateTournamentDialog(
    viewModel: TournamentViewModel,
    onDismiss: () -> Unit
) {
    var title by remember { mutableStateOf("LBG ARMY BATTLE ROYALE") }
    var selectedType by remember { mutableStateOf(TournamentType.PAID) }
    var selectedMode by remember { mutableStateOf(MatchMode.SOLO) }
    var entryFee by remember { mutableStateOf("15") }
    var totalSlots by remember { mutableStateOf("48") }
    var mapName by remember { mutableStateOf("Bermuda") }
    var firstPrize by remember { mutableStateOf("250") }
    var secondPrize by remember { mutableStateOf("120") }
    var thirdPrize by remember { mutableStateOf("60") }
    var perKill by remember { mutableStateOf("5") }
    var totalPrize by remember { mutableStateOf("500") }
    var rules by remember { mutableStateOf("No emulators, gun attributes OFF, screen recording on demand.") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Tournament", color = TextPrimary) },
        text = {
            Column(
                modifier = Modifier
                    .verticalScroll(rememberScrollState())
                    .fillMaxWidth()
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Tournament Title") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Type & Mode selection
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = {
                            selectedType = TournamentType.FREE
                            entryFee = "0"
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (selectedType == TournamentType.FREE) SuccessGreen else TextSecondary
                        )
                    ) {
                        Text("FREE", fontWeight = FontWeight.Bold)
                    }

                    OutlinedButton(
                        onClick = {
                            selectedType = TournamentType.PAID
                            entryFee = "15"
                        },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = if (selectedType == TournamentType.PAID) TournamentGold else TextSecondary
                        )
                    ) {
                        Text("PAID", fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Mode Buttons
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    listOf(MatchMode.SOLO, MatchMode.DUO, MatchMode.SQUAD).forEach { mode ->
                        OutlinedButton(
                            onClick = {
                                selectedMode = mode
                                totalSlots = when (mode) {
                                    MatchMode.SOLO -> "48"
                                    MatchMode.DUO -> "24"
                                    MatchMode.SQUAD -> "12"
                                }
                                if (selectedType == TournamentType.PAID) {
                                    entryFee = when (mode) {
                                        MatchMode.SOLO -> "15"
                                        MatchMode.DUO -> "20"
                                        MatchMode.SQUAD -> "30"
                                    }
                                }
                            },
                            modifier = Modifier.weight(1f),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = if (selectedMode == mode) EsportsRed else TextSecondary
                            )
                        ) {
                            Text(mode.name, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = entryFee,
                        onValueChange = { entryFee = it.filter { c -> c.isDigit() } },
                        label = { Text("Entry Fee (₹)") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = totalSlots,
                        onValueChange = { totalSlots = it.filter { c -> c.isDigit() } },
                        label = { Text("Total Slots") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = mapName,
                    onValueChange = { mapName = it },
                    label = { Text("Map (Bermuda / Purgatory / Alpine)") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = firstPrize,
                        onValueChange = { firstPrize = it.filter { c -> c.isDigit() } },
                        label = { Text("1st Prize ₹") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = perKill,
                        onValueChange = { perKill = it.filter { c -> c.isDigit() } },
                        label = { Text("Per Kill ₹") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    viewModel.createTournament(
                        title = title,
                        type = selectedType,
                        mode = selectedMode,
                        entryFee = entryFee.toIntOrNull() ?: 0,
                        totalSlots = totalSlots.toIntOrNull() ?: 48,
                        mapName = mapName,
                        matchSchedule = System.currentTimeMillis() + 3 * 3600 * 1000L,
                        firstPrize = firstPrize.toIntOrNull() ?: 200,
                        secondPrize = secondPrize.toIntOrNull() ?: 100,
                        thirdPrize = thirdPrize.toIntOrNull() ?: 50,
                        perKillBonus = perKill.toIntOrNull() ?: 5,
                        totalPrizePool = totalPrize.toIntOrNull() ?: 450,
                        rules = rules,
                        onDone = onDismiss
                    )
                },
                colors = ButtonDefaults.buttonColors(containerColor = EsportsRed)
            ) {
                Text("SAVE & PUBLISH", color = Color.White, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("CANCEL", color = TextSecondary)
            }
        },
        containerColor = GamingCardSurface
    )
}

// --- TAB 3: ROOM CREDENTIALS MANAGEMENT ---
@Composable
private fun AdminRoomCredentialsTab(
    tournaments: List<TournamentEntity>,
    onSaveCredentials: (String, String, String, MatchStatus) -> Unit
) {
    val context = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = "ASSIGN ROOM ID & PASSWORD FOR TOURNAMENTS",
                color = CyberCyan,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black
            )
        }

        items(tournaments, key = { it.id }) { tour ->
            var roomIdInput by remember { mutableStateOf(tour.roomId) }
            var roomPassInput by remember { mutableStateOf(tour.roomPassword) }

            GamerCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = tour.title,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = "Schedule: ${formatScheduleDate(tour.matchSchedule)} • ${tour.mode.name}",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedTextField(
                            value = roomIdInput,
                            onValueChange = { roomIdInput = it },
                            label = { Text("Custom Room ID") },
                            placeholder = { Text("e.g. 889421") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = roomPassInput,
                            onValueChange = { roomPassInput = it },
                            label = { Text("Room Password") },
                            placeholder = { Text("e.g. lbg123") },
                            modifier = Modifier.weight(1f),
                            singleLine = true
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    GamerButton(
                        text = "UPDATE ROOM CREDENTIALS",
                        onClick = {
                            onSaveCredentials(tour.id, roomIdInput, roomPassInput, MatchStatus.REGISTRATION_OPEN)
                            Toast.makeText(context, "Room credentials saved! Confirmed players will see this.", Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}

// --- TAB 4: DECLARE WINNERS ---
@Composable
private fun AdminDeclareWinnersTab(
    tournaments: List<TournamentEntity>,
    onDeclare: (String, String, String, Int, String, Int, String, Int, String, Int, String) -> Unit
) {
    var selectedTournament by remember { mutableStateOf<TournamentEntity?>(tournaments.firstOrNull()) }
    var winnerIgn by remember { mutableStateOf("") }
    var winnerUid by remember { mutableStateOf("") }
    var winnerPrize by remember { mutableStateOf("300") }
    var runnerUpIgn by remember { mutableStateOf("") }
    var runnerUpPrize by remember { mutableStateOf("150") }
    var thirdPlaceIgn by remember { mutableStateOf("") }
    var thirdPlacePrize by remember { mutableStateOf("75") }
    var mvpIgn by remember { mutableStateOf("") }
    var mvpKills by remember { mutableStateOf("8") }
    var notes by remember { mutableStateOf("Verified match results by LBG ARMY Admin.") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "DECLARE MATCH RESULTS & WINNERS",
            color = TournamentGold,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black
        )
        Text(
            text = "Record officially verified winners and distribute prizes to leaderboard.",
            color = TextSecondary,
            fontSize = 11.sp
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Tournament selection
        Text(text = "SELECT TOURNAMENT", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        tournaments.forEach { tour ->
            val isSelected = selectedTournament?.id == tour.id
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) EsportsRed.copy(alpha = 0.2f) else GamingCardSurface)
                    .border(1.dp, if (isSelected) EsportsRed else GamingBorder, RoundedCornerShape(8.dp))
                    .clickable { selectedTournament = tour }
                    .padding(10.dp)
            ) {
                Text(
                    text = "${tour.title} (${tour.mode.name})",
                    color = if (isSelected) EsportsRed else TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = winnerIgn,
            onValueChange = { winnerIgn = it },
            label = { Text("1st Place Winner IGN (Booyah)") },
            placeholder = { Text("e.g. LBG_TITAN⚡") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = winnerUid,
                onValueChange = { winnerUid = it },
                label = { Text("Winner Free Fire UID") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = winnerPrize,
                onValueChange = { winnerPrize = it.filter { c -> c.isDigit() } },
                label = { Text("Prize (₹)") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = runnerUpIgn,
                onValueChange = { runnerUpIgn = it },
                label = { Text("2nd Place IGN") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = runnerUpPrize,
                onValueChange = { runnerUpPrize = it.filter { c -> c.isDigit() } },
                label = { Text("2nd Prize (₹)") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(8.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            OutlinedTextField(
                value = mvpIgn,
                onValueChange = { mvpIgn = it },
                label = { Text("MVP / Top Fragger IGN") },
                modifier = Modifier.weight(1f)
            )
            OutlinedTextField(
                value = mvpKills,
                onValueChange = { mvpKills = it.filter { c -> c.isDigit() } },
                label = { Text("MVP Kills") },
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        GamerButton(
            text = "DECLARE & PUBLISH WINNERS",
            onClick = {
                val tour = selectedTournament
                if (tour == null) return@GamerButton
                if (winnerIgn.trim().isEmpty()) return@GamerButton

                onDeclare(
                    tour.id,
                    winnerIgn,
                    winnerUid,
                    winnerPrize.toIntOrNull() ?: 300,
                    runnerUpIgn,
                    runnerUpPrize.toIntOrNull() ?: 150,
                    thirdPlaceIgn,
                    thirdPlacePrize.toIntOrNull() ?: 75,
                    mvpIgn,
                    mvpKills.toIntOrNull() ?: 8,
                    notes
                )
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}

// --- TAB 5: ADMIN SETTINGS ---
@Composable
private fun AdminSettingsTab(
    settings: com.example.data.local.AdminSettingsEntity?,
    onSave: (com.example.data.local.AdminSettingsEntity) -> Unit
) {
    val context = LocalContext.current
    var pin by remember { mutableStateOf(settings?.adminPin ?: "7788") }
    var upiId by remember { mutableStateOf(settings?.adminUpiId ?: "lbgarmy.esports@upi") }
    var payeeName by remember { mutableStateOf(settings?.adminPayeeName ?: "LBG ARMY ESPORTS") }
    var whatsapp by remember { mutableStateOf(settings?.supportWhatsapp ?: "+91 98765 43210") }
    var telegram by remember { mutableStateOf(settings?.supportTelegram ?: "t.me/lbgarmyesports") }
    var email by remember { mutableStateOf(settings?.supportEmail ?: "tournaments@lbgarmy.com") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "ADMIN & PAYMENT GATEWAY CONFIGURATION",
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Black
        )

        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = pin,
            onValueChange = { pin = it },
            label = { Text("Admin Master PIN") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = upiId,
            onValueChange = { upiId = it },
            label = { Text("Admin UPI ID (for QR & Scan)") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = payeeName,
            onValueChange = { payeeName = it },
            label = { Text("Payee Name on UPI") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = whatsapp,
            onValueChange = { whatsapp = it },
            label = { Text("Admin WhatsApp Support Number") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = telegram,
            onValueChange = { telegram = it },
            label = { Text("Telegram Channel Link") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(10.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Support Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        GamerButton(
            text = "SAVE CONFIGURATION",
            onClick = {
                val updated = (settings ?: com.example.data.local.AdminSettingsEntity()).copy(
                    adminPin = pin.trim(),
                    adminUpiId = upiId.trim(),
                    adminPayeeName = payeeName.trim(),
                    supportWhatsapp = whatsapp.trim(),
                    supportTelegram = telegram.trim(),
                    supportEmail = email.trim()
                )
                onSave(updated)
                Toast.makeText(context, "Settings Saved!", Toast.LENGTH_SHORT).show()
            },
            modifier = Modifier.fillMaxWidth()
        )
    }
}
