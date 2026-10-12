package com.mody.costkeeper.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareReportDialog(
    onShare: () -> Unit,
    onDismiss: () -> Unit,
    isGenerating: Boolean,
    isDarkTheme: Boolean
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = if (isDarkTheme) Color(0xFF1E293B) else Color.White,
        shape = RoundedCornerShape(24.dp),
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text("📸", fontSize = 28.sp)
                Text(
                    text = "Share Report",
                    color = if (isDarkTheme) Color.White else Color(0xFF1E293B),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isGenerating) {
                    // Loading state
                    CircularProgressIndicator(
                        color = if (isDarkTheme) Color(0xFF60A5FA) else Color(0xFF3B82F6),
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Generating your report...",
                        color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                        fontSize = 14.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Please wait",
                        color = if (isDarkTheme) Color(0xFF64748B) else Color(0xFF94A3B8),
                        fontSize = 12.sp
                    )
                } else {
                    // Ready state
                    Text(
                        text = "Generate a beautiful summary image of your expenses and share it with anyone.",
                        color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Preview info
                    Surface(
                        color = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF1F5F9),
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("📊", fontSize = 36.sp)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "What's included:",
                                color = if (isDarkTheme) Color.White else Color(0xFF1E293B),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))

                            // Three columns with better spacing
                            Row(
                                horizontalArrangement = Arrangement.SpaceEvenly,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                // Balance
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("💰", fontSize = 28.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Balance",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isDarkTheme) Color.White else Color(0xFF1E293B)
                                    )
                                }

                                // Income/Expense - fixed to single line
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("📈", fontSize = 28.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Income",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isDarkTheme) Color(0xFF4ADE80) else Color(0xFF059669)
                                    )
                                    Text(
                                        "Expense",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isDarkTheme) Color(0xFFE8913E) else Color(0xFFD97706)
                                    )
                                }

                                // Top Categories
                                Column(
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("🏷️", fontSize = 28.sp)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Top",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isDarkTheme) Color.White else Color(0xFF1E293B)
                                    )
                                    Text(
                                        "Categories",
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = if (isDarkTheme) Color.White else Color(0xFF1E293B)
                                    )
                                }
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
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isDarkTheme) Color(0xFF3B82F6) else Color(0xFF3B82F6)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                ) {
                    Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Generate & Share", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
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
                        "Cancel",
                        color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                        fontSize = 14.sp
                    )
                }
            }
        }
    )
}