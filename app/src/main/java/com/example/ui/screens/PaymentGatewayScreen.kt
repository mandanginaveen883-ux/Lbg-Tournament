package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.PaymentStatus
import com.example.data.model.RegistrationStatus
import com.example.ui.components.GamerButton
import com.example.ui.components.GamerCard
import com.example.ui.components.PaymentQrCard
import com.example.ui.components.TenMinuteCountdownBanner
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
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.TournamentViewModel

@Composable
fun PaymentGatewayScreen(
    registrationId: String,
    viewModel: TournamentViewModel,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val allRegistrations by viewModel.allRegistrations.collectAsState()
    val currentTimeMs by viewModel.currentTimeMs.collectAsState()
    val adminSettings by viewModel.adminSettings.collectAsState()

    val registration = allRegistrations.find { it.id == registrationId }

    if (registration == null) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(GamingBlack),
            contentAlignment = Alignment.Center
        ) {
            Text("Registration not found", color = TextPrimary)
        }
        return
    }

    val isExpired = registration.registrationStatus == RegistrationStatus.EXPIRED ||
            (registration.registrationStatus == RegistrationStatus.AWAITING_PAYMENT && currentTimeMs > registration.paymentExpiresAt)
    val isSubmitted = registration.registrationStatus == RegistrationStatus.PAYMENT_SUBMITTED
    val isConfirmed = registration.registrationStatus == RegistrationStatus.CONFIRMED

    var utrInput by remember { mutableStateOf(registration.utrNumber ?: "") }
    var noteInput by remember { mutableStateOf(registration.paymentProofNote ?: "") }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var isSubmitting by remember { mutableStateOf(false) }

    val upiId = adminSettings?.adminUpiId ?: "lbgarmy.esports@upi"
    val payeeName = adminSettings?.adminPayeeName ?: "LBG ARMY ESPORTS"

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
                    text = "PAYMENT VERIFICATION",
                    color = TextPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Ref ID: ${registration.id.takeLast(8).uppercase()}",
                    color = TournamentGold,
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
            // 1. 10-Minute Countdown Banner
            TenMinuteCountdownBanner(
                expiresAt = registration.paymentExpiresAt,
                currentTimeMs = currentTimeMs
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 2. Tournament & Player Summary Card
            GamerCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = registration.tournamentTitle,
                            color = TextPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                        Text(
                            text = "₹${registration.entryFee}",
                            color = TournamentGold,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Player: ${registration.inGameName} (UID: ${registration.freeFireUid})",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Reserved Slot #${registration.assignedSlotNumber ?: "Pending"}",
                        color = CyberCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // 3. Status View if already Submitted or Confirmed
            if (isConfirmed) {
                GamerCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = SuccessGreen,
                    glowEffect = true
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = SuccessGreen,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "PAYMENT VERIFIED & SLOT CONFIRMED!",
                            color = SuccessGreen,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Your slot #${registration.assignedSlotNumber} is locked. Room ID & password will appear in 'My Matches' 15 minutes before the match.",
                            color = TextPrimary,
                            fontSize = 12.sp,
                            lineHeight = 16.sp
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        GamerButton(
                            text = "GO TO MY MATCHES",
                            onClick = { viewModel.navigateTo(Screen.MyMatches) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else if (isSubmitted) {
                GamerCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = PendingYellow,
                    glowEffect = true
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.HourglassTop,
                            contentDescription = null,
                            tint = PendingYellow,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "PAYMENT SUBMITTED • AWAITING ADMIN VERIFICATION",
                            color = PendingYellow,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "UTR: ${registration.utrNumber}",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Admin will manually check bank credit for this UTR and confirm your slot shortly. Never treat submitted UTR as confirmed proof.",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                    }
                }
            } else if (isExpired) {
                GamerCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = EsportsRed
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = null,
                            tint = EsportsRed,
                            modifier = Modifier.size(36.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "REGISTRATION EXPIRED",
                            color = EsportsRed,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "The 10-minute payment countdown elapsed. Your reserved slot has been released back to the tournament pool. Please register again.",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            lineHeight = 15.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        GamerButton(
                            text = "BACK TO TOURNAMENT",
                            onClick = onBack,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            } else {
                // 4. Normal Payment Flow: Admin QR Code + UTR Submission
                PaymentQrCard(
                    upiId = upiId,
                    payeeName = payeeName,
                    amount = registration.entryFee,
                    note = "LBG_${registration.id.takeLast(6)}"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Security & Manual Verification Disclosure (Explicit requirement #6)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(EsportsRed.copy(alpha = 0.12f))
                        .border(1.dp, EsportsRed.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                        .padding(12.dp)
                ) {
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = EsportsRed,
                            modifier = Modifier
                                .size(20.dp)
                                .padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "SECURITY NOTICE:\n• Admin manually verifies payments in bank/UPI portal.\n• Confirm registration only after successful payment verification.\n• Never treat a submitted UTR as proof of payment automatically.\n• Fake UTR submissions will result in immediate UID permanent ban.",
                            color = TextPrimary,
                            fontSize = 11.sp,
                            lineHeight = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Step 2: Submit UTR
                Text(
                    text = "STEP 2: SUBMIT TRANSACTION UTR NUMBER",
                    color = TournamentGold,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "After paying via UPI, find the 12-digit UPI Ref/UTR number in GPay/PhonePe/Paytm and enter it below.",
                    color = TextSecondary,
                    fontSize = 11.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = utrInput,
                    onValueChange = {
                        if (it.length <= 16) {
                            utrInput = it.filter { char -> char.isDigit() || char.isLetter() }
                            errorMessage = null
                        }
                    },
                    label = { Text("12-Digit UPI UTR / Transaction ID") },
                    placeholder = { Text("e.g. 431098273612") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.CreditCard, contentDescription = null, tint = TournamentGold)
                    },
                    singleLine = true,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("utr_number_input"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TournamentGold,
                        unfocusedBorderColor = GamingBorder,
                        focusedLabelColor = TournamentGold,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = GamingCardElevated,
                        unfocusedContainerColor = GamingCardSurface
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = noteInput,
                    onValueChange = { noteInput = it },
                    label = { Text("Optional Note / Account Name") },
                    placeholder = { Text("e.g. Paid from Rahul GPay") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = EsportsRed,
                        unfocusedBorderColor = GamingBorder,
                        focusedLabelColor = EsportsRed,
                        unfocusedLabelColor = TextSecondary,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = GamingCardElevated,
                        unfocusedContainerColor = GamingCardSurface
                    )
                )

                if (errorMessage != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = errorMessage!!,
                        color = EsportsRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Submit UTR Button
                GamerButton(
                    text = if (isSubmitting) "SUBMITTING..." else "SUBMIT UTR FOR VERIFICATION",
                    onClick = {
                        if (utrInput.trim().length < 8) {
                            errorMessage = "Please enter a valid 12-digit UTR number"
                            return@GamerButton
                        }
                        isSubmitting = true
                        viewModel.submitPaymentUtr(
                            registrationId = registration.id,
                            utr = utrInput.trim(),
                            note = noteInput.trim(),
                            onDone = { success ->
                                isSubmitting = false
                                if (success) {
                                    Toast.makeText(context, "UTR Submitted for Admin Verification!", Toast.LENGTH_LONG).show()
                                }
                            }
                        )
                    },
                    enabled = !isSubmitting,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "submit_utr_button"
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Test Payment Button (Sandbox Simulator requirement: "Use test payments during development")
                OutlinedButton(
                    onClick = {
                        viewModel.submitTestPayment(registration.id) {
                            Toast.makeText(context, "⚡ Test payment auto-verified in Sandbox Mode!", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("test_payment_button"),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = CyberCyan
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.FlashOn,
                        contentDescription = null,
                        tint = CyberCyan,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "⚡ TEST PAYMENT (INSTANT SANDBOX APPROVAL)",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
