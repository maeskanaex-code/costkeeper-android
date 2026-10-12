package com.mody.costkeeper.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun EmptyState(
    title: String = "No Data Yet",
    message: String = "Add your first expense to get started",
    isDarkTheme: Boolean = true,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "📊",
            fontSize = 64.sp,
            color = Color(0xFF60A5FA)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            color = if (isDarkTheme) Color.White else Color(0xFF1E293B),
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = message,
            color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}