package com.mody.costkeeper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mody.costkeeper.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareReportDialog(
    onShare: () -> Unit,
    onDismiss: () -> Unit,
    isGenerating: Boolean,
    isDarkTheme: Boolean
) {
    val surfaceBg = if (isDarkTheme) DarkSurfaceElevated else LightSurfaceElevated
    val textPrimary = if (isDarkTheme) TextPrimary else LightTextPrimary
    val textMuted = if (isDarkTheme) TextMuted else LightTextMuted

    AlertDialog(
        onDismissRequest = { if (!isGenerating) onDismiss() },
        containerColor = surfaceBg,
        shape = RoundedCornerShape(24.dp),
        title = {
            Column {
                Text(
                    text = "Share Report",
                    color = textPrimary,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Generate a beautiful summary image",
                    color = textMuted,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (isGenerating) {
                    Spacer(modifier = Modifier.height(8.dp))
                    CircularProgressIndicator(
                        color = PrimaryBlue,
                        modifier = Modifier.size(52.dp),
                        strokeWidth = 4.dp
                    )
                    Spacer(modifier = Modifier.height(20.dp))
                    Text(
                        text = "Generating your report...",
                        color = textPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Please wait a moment",
                        color = textMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                } else {
                    Text(
                        text = "Your report will include a clean summary of your balance, income, expenses, and top spending categories.",
                        color = textMuted,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Preview card with 3 items
                    Surface(
                        color = if (isDarkTheme) DarkSurface else LightSurface,
                        shape = RoundedCornerShape(18.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(64.dp)
                                    .clip(CircleShape)
                                    .background(PrimaryBlueContainer.copy(alpha = 0.4f)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text("📊", fontSize = 32.sp)
                            }

                            Spacer(modifier = Modifier.height(16.dp))

                            Text(
                                text = "What's included",
                                color = textPrimary,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                PreviewItem(emoji = "💰", label = "Balance", isDarkTheme = isDarkTheme)
                                PreviewItem(emoji = "📈", label = "Income", isDarkTheme = isDarkTheme)
                                PreviewItem(emoji = "🏷️", label = "Top", isDarkTheme = isDarkTheme)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            if (!isGenerating) {
                Button(
                    onClick = onShare,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(
                        Icons.Default.Share,
                        contentDescription = null,
                        tint = TextOnAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Generate & Share",
                        color = TextOnAccent,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        },
        dismissButton = {
            if (!isGenerating) {
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Cancel",
                        color = textMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }
    )
}

@Composable
private fun PreviewItem(
    emoji: String,
    label: String,
    isDarkTheme: Boolean
) {
    val textColor = if (isDarkTheme) TextPrimary else LightTextPrimary

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(72.dp)
    ) {
        Text(emoji, fontSize = 24.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold
        )
    }
}
