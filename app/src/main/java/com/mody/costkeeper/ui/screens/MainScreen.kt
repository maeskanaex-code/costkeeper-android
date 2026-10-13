package com.mody.costkeeper.ui.screens

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.mody.costkeeper.R
import com.mody.costkeeper.ui.theme.*
import com.mody.costkeeper.ui.viewmodel.ExpenseViewModel
import com.mody.costkeeper.utils.AppCurrency
import com.mody.costkeeper.utils.CurrencyManager

enum class BottomTab(val labelResId: Int, val icon: ImageVector) {
    Expenses(R.string.tab_expenses, Icons.Default.List),
    Analytics(R.string.tab_analytics, Icons.Default.Star)
}

@Composable
fun MainScreen(
    viewModel: ExpenseViewModel,
    isDarkTheme: Boolean,
    onThemeChange: (Boolean) -> Unit
) {
    var selectedTab by remember { mutableStateOf(BottomTab.Expenses) }
    val context = LocalContext.current
    val selectedCurrency = remember { CurrencyManager.getSelectedCurrency(context) }

    val background = if (isDarkTheme) DarkBackground else LightBackground
    val navBg = if (isDarkTheme) DarkSurface else LightSurface

    Scaffold(
        containerColor = background,
        bottomBar = {
            NavigationBar(
                containerColor = navBg,
                tonalElevation = 0.dp
            ) {
                BottomTab.entries.forEach { tab ->
                    val label = stringResource(tab.labelResId)
                    val selected = selectedTab == tab

                    NavigationBarItem(
                        selected = selected,
                        onClick = { selectedTab = tab },
                        icon = {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(
                                        if (selected) PrimaryBlue.copy(alpha = 0.15f)
                                        else navBg
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = tab.icon,
                                    contentDescription = label,
                                    tint = if (selected) PrimaryBlue
                                    else if (isDarkTheme) TextMuted else LightTextMuted,
                                    modifier = Modifier.size(22.dp)
                                )
                            }
                        },
                        label = {
                            Text(
                                text = label,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                                color = if (selected) PrimaryBlue
                                else if (isDarkTheme) TextMuted else LightTextMuted
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (selectedTab) {
                BottomTab.Expenses -> ExpenseScreen(
                    viewModel = viewModel,
                    isDarkTheme = isDarkTheme,
                    onThemeChange = onThemeChange
                )
                BottomTab.Analytics -> AnalyticsScreen(
                    viewModel = viewModel,
                    isDarkTheme = isDarkTheme,
                    currency = selectedCurrency
                )
            }
        }
    }
}
