package com.mody.costkeeper.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mody.costkeeper.ui.theme.*

@Composable
fun ThemeToggle(
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit,
    lightText: String = "Light",
    darkText: String = "Dark",
    modifier: Modifier = Modifier
) {
    val containerBg = if (isDarkTheme) DarkSurfaceHighest else LightSurfaceHighest

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(999.dp))
            .background(containerBg)
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        ThemeOption(
            title = lightText,
            icon = Icons.Default.LightMode,
            isSelected = !isDarkTheme,
            onClick = { onThemeChange(false) },
            isDarkTheme = isDarkTheme,
            modifier = Modifier.weight(1f)
        )

        ThemeOption(
            title = darkText,
            icon = Icons.Default.DarkMode,
            isSelected = isDarkTheme,
            onClick = { onThemeChange(true) },
            isDarkTheme = isDarkTheme,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ThemeOption(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    isDarkTheme: Boolean,
    modifier: Modifier = Modifier
) {
    val bgColor = if (isSelected) PrimaryBlue else containerBgOf(isDarkTheme)
    val contentColor = if (isSelected) TextOnAccent else (if (isDarkTheme) TextSecondary else LightTextSecondary)

    val animatedBg by animateColorAsState(
        targetValue = bgColor,
        animationSpec = tween(220),
        label = "themeOptionBg"
    )

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(animatedBg)
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = contentColor,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = title,
            color = contentColor,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

private fun containerBgOf(isDarkTheme: Boolean) =
    if (isDarkTheme) DarkSurfaceHighest else LightSurfaceHighest
