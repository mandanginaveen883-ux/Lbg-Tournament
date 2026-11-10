package com.example.ui.screens

import android.widget.Toast
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MatchMode
import com.example.data.model.RegistrationStatus
import com.example.data.model.TournamentType
import com.example.ui.components.GamerButton
import com.example.ui.components.GamerCard
import com.example.ui.components.ModeBadge
import com.example.ui.components.TypeBadge
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
fun PlayerRegistrationScreen(
    tournamentId: String,
    viewModel: TournamentViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val tournaments by viewModel.tournaments.collectAsState()
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

    var playerName by remember { mutableStateOf(viewModel.repository.getSavedPlayerName()) }
    var freeFireUid by remember { mutableStateOf(viewModel.repository.getSavedFfUid()) }
    var inGameName by remember { mutableStateOf(viewModel.repository.getSavedIgn()) }
    var mobileNumber by remember { mutableStateOf(viewModel.repository.getSavedMobile()) }
    var teamName by remember { mutableStateOf("") }
    var teammateUids by remember { mutableStateOf("") }

    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    val isTeamMode = tournament.mode == MatchMode.DUO || tournament.mode == MatchMode.SQUAD

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
                    text = "PLAYER REGISTRATION",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = tournament.title,
                    color = EsportsRed,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Match Summary Card
            GamerCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ModeBadge(mode = tournament.mode)
                            TypeBadge(type = tournament.type, entryFee = tournament.entryFee)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Map: ${tournament.mapName} • ${tournament.filledSlots}/${tournament.totalSlots} Slots",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "FEE",
                            color = TextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (tournament.entryFee == 0) "FREE" else "₹${tournament.entryFee}",
                            color = if (tournament.entryFee == 0) SuccessGreen else TournamentGold,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Instructions notice
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(GamingCardSurface)
                    .border(1.dp, GamingBorder, RoundedCornerShape(8.dp))
                    .padding(12.dp)
            ) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier
                            .size(18.dp)
                            .padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Enter accurate Free Fire UID & in-game name. Duplicate registrations with the same UID are strictly blocked. Room ID will be shared only to verified accounts.",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        lineHeight = 15.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Form Inputs
            Text(
                text = "PLAYER CREDENTIALS",
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Player Name
            OutlinedTextField(
                value = playerName,
                onValueChange = {
                    playerName = it
                    errorMessage = null
                },
                label = { Text("Player Full Name") },
                placeholder = { Text("e.g. Rahul Verma") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Person, contentDescription = null, tint = EsportsRed)
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("player_name_input"),
                colors = outlinedColors()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Free Fire UID
            OutlinedTextField(
                value = freeFireUid,
                onValueChange = {
                    if (it.length <= 12) {
                        freeFireUid = it.filter { char -> char.isDigit() }
                        errorMessage = null
                    }
                },
                label = { Text("Free Fire UID (8-10 digits)") },
                placeholder = { Text("e.g. 1049281723") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Badge, contentDescription = null, tint = TournamentGold)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("free_fire_uid_input"),
                colors = outlinedColors()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // In-Game Name (IGN)
            OutlinedTextField(
                value = inGameName,
                onValueChange = {
                    inGameName = it
                    errorMessage = null
                },
                label = { Text("In-Game Name (IGN)") },
                placeholder = { Text("e.g. LBG_TITAN⚡") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.SportsEsports, contentDescription = null, tint = CyberCyan)
                },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("in_game_name_input"),
                colors = outlinedColors()
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Mobile Number
            OutlinedTextField(
                value = mobileNumber,
                onValueChange = {
                    if (it.length <= 10) {
                        mobileNumber = it.filter { char -> char.isDigit() }
                        errorMessage = null
                    }
                },
                label = { Text("WhatsApp Mobile Number (10 digits)") },
                placeholder = { Text("e.g. 9876543210") },
                leadingIcon = {
                    Icon(imageVector = Icons.Default.Phone, contentDescription = null, tint = SuccessGreen)
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("mobile_number_input"),
                colors = outlinedColors()
            )

            // Duo / Squad additional fields
            if (isTeamMode) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "${tournament.mode.name} TEAM DETAILS",
                    color = TournamentGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = teamName,
                    onValueChange = { teamName = it },
                    label = { Text("Team / Clan Name") },
                    placeholder = { Text("e.g. LBG WARRIORS") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Group, contentDescription = null, tint = TournamentGold)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedColors()
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = teammateUids,
                    onValueChange = { teammateUids = it },
                    label = { Text("Teammate UIDs (Comma separated)") },
                    placeholder = { Text(if (tournament.mode == MatchMode.DUO) "e.g. 1938472910" else "e.g. 1938472910, 1726354891, 1654321987") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = outlinedColors()
                )
            }

            if (errorMessage != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = errorMessage!!,
                    color = EsportsRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Submit Button
            GamerButton(
                text = when {
                    isSubmitting -> "REGISTERING..."
                    tournament.entryFee == 0 -> "CONFIRM FREE REGISTRATION"
                    else -> "PROCEED TO PAY ₹${tournament.entryFee}"
                },
                onClick = {
                    // Validation
                    if (playerName.trim().length < 2) {
                        errorMessage = "Please enter your valid name"
                        return@GamerButton
                    }
                    if (freeFireUid.trim().length < 8) {
                        errorMessage = "Free Fire UID must be at least 8 digits"
                        return@GamerButton
                    }
                    if (inGameName.trim().isEmpty()) {
                        errorMessage = "Please enter your In-Game Name"
                        return@GamerButton
                    }
                    if (mobileNumber.trim().length != 10) {
                        errorMessage = "Mobile number must be exactly 10 digits"
                        return@GamerButton
                    }
                    if (isTeamMode && teamName.trim().isEmpty()) {
                        errorMessage = "Please provide Team/Clan Name"
                        return@GamerButton
                    }

                    isSubmitting = true
                    viewModel.registerPlayer(
                        tournamentId = tournament.id,
                        playerName = playerName,
                        freeFireUid = freeFireUid,
                        inGameName = inGameName,
                        mobileNumber = mobileNumber,
                        teamName = if (isTeamMode) teamName else null,
                        teammateUids = if (isTeamMode) teammateUids else null,
                        onSuccess = { reg ->
                            isSubmitting = false
                            if (reg.registrationStatus == RegistrationStatus.CONFIRMED) {
                                // Free registration confirmed -> Go to My Matches
                                viewModel.navigateTo(Screen.MyMatches)
                            } else {
                                // Paid registration -> Go to Payment Gateway
                                viewModel.navigateTo(Screen.PaymentGateway(reg.id))
                            }
                        }
                    )
                },
                enabled = !isSubmitting,
                modifier = Modifier.fillMaxWidth(),
                testTag = "submit_registration_button"
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun outlinedColors() = OutlinedTextFieldDefaults.colors(
    focusedBorderColor = EsportsRed,
    unfocusedBorderColor = GamingBorder,
    focusedLabelColor = EsportsRed,
    unfocusedLabelColor = TextSecondary,
    focusedTextColor = TextPrimary,
    unfocusedTextColor = TextPrimary,
    focusedContainerColor = GamingCardElevated,
    unfocusedContainerColor = GamingCardSurface
)
