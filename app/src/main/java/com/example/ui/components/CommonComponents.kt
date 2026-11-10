package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MatchMode
import com.example.data.model.MatchStatus
import com.example.data.model.RegistrationStatus
import com.example.data.model.TournamentType
import com.example.ui.theme.CyberCyan
import com.example.ui.theme.EsportsRed
import com.example.ui.theme.EsportsRedContainer
import com.example.ui.theme.GamingBorder
import com.example.ui.theme.GamingBorderGlow
import com.example.ui.theme.GamingCardElevated
import com.example.ui.theme.GamingCardSurface
import com.example.ui.theme.PendingYellow
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TournamentGold
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun GamerCard(
    modifier: Modifier = Modifier,
    borderColor: Color = GamingBorder,
    glowEffect: Boolean = false,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val finalBorderColor = if (glowEffect) EsportsRed.copy(alpha = 0.6f) else borderColor
    val cardModifier = if (onClick != null) {
        modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, finalBorderColor, RoundedCornerShape(14.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        GamingCardElevated,
                        GamingCardSurface
                    )
                )
            )
            .clickable { onClick() }
    } else {
        modifier
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, finalBorderColor, RoundedCornerShape(14.dp))
            .background(
                Brush.verticalGradient(
                    listOf(
                        GamingCardElevated,
                        GamingCardSurface
                    )
                )
            )
    }

    Box(modifier = cardModifier) {
        content()
    }
}

@Composable
fun GamerButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isPrimary: Boolean = true,
    testTag: String = "gamer_button"
) {
    if (isPrimary) {
        Button(
            onClick = onClick,
            enabled = enabled,
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = EsportsRed,
                contentColor = Color.White,
                disabledContainerColor = EsportsRed.copy(alpha = 0.35f),
                disabledContentColor = Color.White.copy(alpha = 0.5f)
            ),
            modifier = modifier
                .height(48.dp)
                .testTag(testTag)
        ) {
            Text(
                text = text.uppercase(),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                letterSpacing = 1.sp
            )
        }
    } else {
        OutlinedButton(
            onClick = onClick,
            enabled = enabled,
            shape = RoundedCornerShape(10.dp),
            border = ButtonDefaults.outlinedButtonBorder.copy(
                brush = Brush.horizontalGradient(listOf(EsportsRed, TournamentGold))
            ),
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = TextPrimary
            ),
            modifier = modifier
                .height(48.dp)
                .testTag(testTag)
        ) {
            Text(
                text = text.uppercase(),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                letterSpacing = 1.sp
            )
        }
    }
}

@Composable
fun ModeBadge(mode: MatchMode) {
    val (color, text) = when (mode) {
        MatchMode.SOLO -> CyberCyan to "SOLO"
        MatchMode.DUO -> TournamentGold to "DUO"
        MatchMode.SQUAD -> EsportsRed to "SQUAD"
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Black,
            letterSpacing = 0.8.sp
        )
    }
}

@Composable
fun TypeBadge(type: TournamentType, entryFee: Int) {
    if (type == TournamentType.FREE || entryFee == 0) {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(SuccessGreen.copy(alpha = 0.18f))
                .border(1.dp, SuccessGreen.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = "FREE ENTRY",
                color = SuccessGreen,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )
        }
    } else {
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(6.dp))
                .background(TournamentGold.copy(alpha = 0.18f))
                .border(1.dp, TournamentGold.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = "₹$entryFee ENTRY",
                color = TournamentGold,
                fontSize = 11.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 0.5.sp
            )
        }
    }
}

@Composable
fun StatusBadge(status: MatchStatus) {
    val (color, label) = when (status) {
        MatchStatus.UPCOMING -> CyberCyan to "UPCOMING"
        MatchStatus.REGISTRATION_OPEN -> SuccessGreen to "OPEN"
        MatchStatus.REGISTRATION_FULL -> EsportsRed to "FULL"
        MatchStatus.LIVE -> EsportsRed to "LIVE NOW"
        MatchStatus.COMPLETED -> TextMuted to "COMPLETED"
        MatchStatus.CANCELLED -> Color.Gray to "CANCELLED"
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.18f))
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun RegistrationStatusBadge(status: RegistrationStatus) {
    val (bgColor, textColor, text) = when (status) {
        RegistrationStatus.CONFIRMED -> Triple(SuccessGreen.copy(alpha = 0.2f), SuccessGreen, "CONFIRMED")
        RegistrationStatus.PAYMENT_SUBMITTED -> Triple(PendingYellow.copy(alpha = 0.2f), PendingYellow, "VERIFYING")
        RegistrationStatus.AWAITING_PAYMENT -> Triple(TournamentGold.copy(alpha = 0.2f), TournamentGold, "PAYMENT PENDING")
        RegistrationStatus.REJECTED -> Triple(EsportsRed.copy(alpha = 0.2f), EsportsRed, "REJECTED")
        RegistrationStatus.EXPIRED -> Triple(Color.Gray.copy(alpha = 0.2f), Color.LightGray, "EXPIRED")
        RegistrationStatus.REFUNDED -> Triple(CyberCyan.copy(alpha = 0.2f), CyberCyan, "REFUNDED")
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bgColor)
            .border(1.dp, textColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = text,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun SlotsProgressBar(
    filled: Int,
    total: Int,
    modifier: Modifier = Modifier
) {
    val progress = if (total > 0) (filled.toFloat() / total.toFloat()).coerceIn(0f, 1f) else 0f
    val barColor = when {
        progress >= 0.9f -> EsportsRed
        progress >= 0.6f -> TournamentGold
        else -> SuccessGreen
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "SLOTS FILLED",
                color = TextSecondary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "$filled / $total (${total - filled} left)",
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp)
                .clip(RoundedCornerShape(3.dp)),
            color = barColor,
            trackColor = GamingBorder
        )
    }
}

@Composable
fun CountdownTimerView(
    targetTimeMs: Long,
    currentTimeMs: Long,
    label: String = "STARTS IN",
    modifier: Modifier = Modifier
) {
    val diffMs = targetTimeMs - currentTimeMs
    val isPast = diffMs <= 0

    val hours = if (diffMs > 0) diffMs / (1000 * 3600) else 0
    val minutes = if (diffMs > 0) (diffMs % (1000 * 3600)) / (1000 * 60) else 0
    val seconds = if (diffMs > 0) (diffMs % (1000 * 60)) / 1000 else 0

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(EsportsRedContainer.copy(alpha = 0.4f))
            .border(1.dp, EsportsRed.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.HourglassTop,
            contentDescription = null,
            tint = EsportsRed,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Column {
            Text(
                text = label,
                color = TextSecondary,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isPast) "MATCH READY / LIVE" else String.format(Locale.getDefault(), "%02dh %02dm %02ds", hours, minutes, seconds),
                color = if (isPast) SuccessGreen else TextPrimary,
                fontSize = 12.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
        }
    }
}

@Composable
fun TenMinuteCountdownBanner(
    expiresAt: Long,
    currentTimeMs: Long,
    modifier: Modifier = Modifier
) {
    val remainingMs = expiresAt - currentTimeMs
    val isExpired = remainingMs <= 0
    val totalSeconds = if (remainingMs > 0) remainingMs / 1000 else 0
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60

    val bannerColor = if (totalSeconds < 120) EsportsRed else TournamentGold

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(bannerColor.copy(alpha = 0.15f))
            .border(1.dp, bannerColor.copy(alpha = 0.6f), RoundedCornerShape(10.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = if (isExpired) Icons.Default.Warning else Icons.Default.HourglassTop,
                contentDescription = null,
                tint = bannerColor,
                modifier = Modifier.size(22.dp)
            )
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = if (isExpired) "PAYMENT WINDOW EXPIRED" else "PAYMENT TIME REMAINING",
                    color = bannerColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 0.8.sp
                )
                Text(
                    text = if (isExpired) "Unpaid registration auto-expired. Slot released." else "Submit UTR within 10 minutes to secure your slot.",
                    color = TextSecondary,
                    fontSize = 11.sp
                )
            }
        }
        if (!isExpired) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(GamingCardSurface)
                    .border(1.dp, bannerColor, RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Text(
                    text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds),
                    color = bannerColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Black,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}

fun formatScheduleDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMM, hh:mm a", Locale.getDefault())
    return sdf.format(Date(timestamp))
}
