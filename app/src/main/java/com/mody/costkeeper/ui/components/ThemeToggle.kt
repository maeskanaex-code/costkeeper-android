package com.mody.costkeeper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ThemeToggle(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = if (isDarkTheme) Color(0xFF1E293B) else Color(0xFFE2E8F0),
                shape = RoundedCornerShape(14.dp)
            )
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ThemeOption(
            title = "Light",
            icon = Icons.Default.LightMode,
            isSelected = !isDarkTheme,
            onClick = { onThemeChange(false) },
            modifier = Modifier.weight(1f)
        )

        ThemeOption(
            title = "Dark",
            icon = Icons.Default.DarkMode,
            isSelected = isDarkTheme,
            onClick = { onThemeChange(true) },
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ThemeOption(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(10.dp))
            .background(
                if (isSelected) Color(0xFF3B82F6) else Color.Transparent
            )
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = title,
            tint = if (isSelected) Color.White else Color(0xFF94A3B8),
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            color = if (isSelected) Color.White else Color(0xFF94A3B8),
            fontSize = MaterialTheme.typography.bodyMedium.fontSize
        )
    }
}