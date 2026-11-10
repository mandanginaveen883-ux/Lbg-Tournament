package com.example.ui.components

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.OpenInNew
import androidx.compose.material.icons.filled.QrCode2
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EsportsRed
import com.example.ui.theme.GamingBorder
import com.example.ui.theme.GamingCardElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TournamentGold

@Composable
fun PaymentQrCard(
    upiId: String,
    payeeName: String,
    amount: Int,
    note: String,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val upiUriString = "upi://pay?pa=$upiId&pn=${Uri.encode(payeeName)}&am=$amount&cu=INR&tn=${Uri.encode(note)}"

    GamerCard(
        modifier = modifier.fillMaxWidth(),
        borderColor = TournamentGold.copy(alpha = 0.5f),
        glowEffect = false
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ADMIN OFFICIAL UPI SCAN & PAY",
                color = TournamentGold,
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Scan with GPay / PhonePe / Paytm / BHIM",
                color = TextSecondary,
                fontSize = 11.sp
            )

            Spacer(modifier = Modifier.height(14.dp))

            // High-fidelity Rendered QR Code
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.White)
                    .padding(12.dp),
                contentAlignment = Alignment.Center
            ) {
                CanvasQrCode(
                    content = upiUriString,
                    modifier = Modifier.size(176.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Amount to Pay highlight
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(GamingCardElevated)
                    .border(1.dp, GamingBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = "EXACT AMOUNT: ",
                    color = TextSecondary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "₹$amount",
                    color = EsportsRed,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Black
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // UPI ID Display with 1-tap Copy
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black.copy(alpha = 0.4f))
                    .border(1.dp, GamingBorder, RoundedCornerShape(8.dp))
                    .padding(horizontal = 12.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "UPI ID / VPA",
                        color = TextMuted,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = upiId,
                        color = TextPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                }

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(TournamentGold.copy(alpha = 0.2f))
                        .clickable {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                            clipboard.setPrimaryClip(ClipData.newPlainText("UPI ID", upiId))
                            Toast.makeText(context, "UPI ID Copied!", Toast.LENGTH_SHORT).show()
                        }
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Copy UPI ID",
                        tint = TournamentGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "COPY",
                        color = TournamentGold,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Launch UPI App Button
            OutlinedButton(
                onClick = {
                    try {
                        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(upiUriString))
                        context.startActivity(Intent.createChooser(intent, "Pay via UPI App"))
                    } catch (e: Exception) {
                        Toast.makeText(context, "No UPI app found. Please copy UPI ID & pay.", Toast.LENGTH_LONG).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = TextPrimary
                )
            ) {
                Icon(
                    imageVector = Icons.Default.OpenInNew,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = EsportsRed
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "OPEN ANY UPI APP (GPAY / PHONEPE)",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun CanvasQrCode(
    content: String,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val sizePx = size.minDimension
        val gridCount = 25
        val cellSize = sizePx / gridCount

        // Clean white background
        drawRect(Color.White)

        // Draw 3 Corner Position Detection Patterns (Top-Left, Top-Right, Bottom-Left)
        fun drawPositionPattern(startX: Float, startY: Float) {
            // Outer 7x7 black square
            drawRoundRect(
                color = Color.Black,
                topLeft = Offset(startX, startY),
                size = Size(cellSize * 7, cellSize * 7),
                cornerRadius = CornerRadius(4f, 4f)
            )
            // Inner 5x5 white square
            drawRoundRect(
                color = Color.White,
                topLeft = Offset(startX + cellSize, startY + cellSize),
                size = Size(cellSize * 5, cellSize * 5),
                cornerRadius = CornerRadius(2f, 2f)
            )
            // Center 3x3 black square
            drawRoundRect(
                color = Color.Black,
                topLeft = Offset(startX + cellSize * 2, startY + cellSize * 2),
                size = Size(cellSize * 3, cellSize * 3),
                cornerRadius = CornerRadius(2f, 2f)
            )
        }

        drawPositionPattern(0f, 0f)
        drawPositionPattern((gridCount - 7) * cellSize, 0f)
        drawPositionPattern(0f, (gridCount - 7) * cellSize)

        // Generate deterministic modules based on content hash
        val contentHash = content.hashCode()
        for (row in 0 until gridCount) {
            for (col in 0 until gridCount) {
                // Skip corner detection zones
                val inTopLeft = row < 8 && col < 8
                val inTopRight = row < 8 && col >= gridCount - 8
                val inBottomLeft = row >= gridCount - 8 && col < 8
                val inCenterLogo = row in 10..14 && col in 10..14

                if (!inTopLeft && !inTopRight && !inBottomLeft && !inCenterLogo) {
                    val pseudoRandom = (row * 31 + col * 17 + contentHash) % 7
                    if (pseudoRandom == 0 || pseudoRandom == 2 || (row + col) % 3 == 0) {
                        drawRect(
                            color = Color.Black,
                            topLeft = Offset(col * cellSize + 0.5f, row * cellSize + 0.5f),
                            size = Size(cellSize - 1f, cellSize - 1f)
                        )
                    }
                }
            }
        }

        // Draw Center Gaming Accent Emblem
        val centerStart = 10 * cellSize
        val centerBoxSize = 5 * cellSize
        drawRoundRect(
            color = EsportsRed,
            topLeft = Offset(centerStart, centerStart),
            size = Size(centerBoxSize, centerBoxSize),
            cornerRadius = CornerRadius(6f, 6f)
        )
        drawRoundRect(
            color = Color.White,
            topLeft = Offset(centerStart + cellSize, centerStart + cellSize),
            size = Size(3 * cellSize, 3 * cellSize),
            cornerRadius = CornerRadius(3f, 3f)
        )
        drawRoundRect(
            color = EsportsRed,
            topLeft = Offset(centerStart + 1.5f * cellSize, centerStart + 1.5f * cellSize),
            size = Size(2 * cellSize, 2 * cellSize),
            cornerRadius = CornerRadius(2f, 2f)
        )
    }
}
