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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mody.costkeeper.R
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

    val titleText = stringResource(R.string.share_report_title)
    val subtitleText = stringResource(R.string.share_report_subtitle)
    val generatingText = stringResource(R.string.share_report_generating)
    val waitText = stringResource(R.string.share_report_wait)
    val descText = stringResource(R.string.share_report_description)
    val includedText = stringResource(R.string.share_report_included)
    val balanceText = stringResource(R.string.share_report_balance)
    val incomeText = stringResource(R.string.share_report_income)
    val topText = stringResource(R.string.share_report_top)
    val generateShareText = stringResource(R.string.share_report_generate_share)
    val cancelText = stringResource(R.string.cancel)

    AlertDialog(
        onDismissRequest = { if (!isGenerating) onDismiss() },
        containerColor = surfaceBg,
        shape = RoundedCornerShape(24.dp),
        title = {
            Column {
                Text(
                    text = titleText,
                    color = textPrimary,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitleText,
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
                        text = generatingText,
                        color = textPrimary,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = waitText,
                        color = textMuted,
                        style = MaterialTheme.typography.bodySmall
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                } else {
                    Text(
                        text = descText,
                        color = textMuted,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

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
                                text = includedText,
                                color = textPrimary,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                PreviewItem(emoji = "💰", label = balanceText, isDarkTheme = isDarkTheme)
                                PreviewItem(emoji = "📈", label = incomeText, isDarkTheme = isDarkTheme)
                                PreviewItem(emoji = "🏷️", label = topText, isDarkTheme = isDarkTheme)
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
                        text = generateShareText,
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
                        text = cancelText,
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
