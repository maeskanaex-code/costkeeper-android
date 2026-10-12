package com.mody.costkeeper.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mody.costkeeper.R
import com.mody.costkeeper.ui.viewmodel.ExpenseViewModel
import com.mody.costkeeper.utils.AppCurrency
import com.mody.costkeeper.utils.CurrencyManager
import android.content.Context
import androidx.compose.ui.platform.LocalContext

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

    Scaffold(
        containerColor = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF8FAFC),
        bottomBar = {
            NavigationBar(
                containerColor = if (isDarkTheme) Color(0xFF1E293B) else Color.White,
                contentColor = if (isDarkTheme) Color.White else Color(0xFF1E293B),
                tonalElevation = 0.dp
            ) {
                BottomTab.entries.forEach { tab ->
                    val label = stringResource(tab.labelResId)

                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Icon(tab.icon, contentDescription = label) },
                        label = { Text(label) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = if (isDarkTheme) Color(0xFF60A5FA) else Color(0xFF3B82F6),
                            selectedTextColor = if (isDarkTheme) Color(0xFF60A5FA) else Color(0xFF3B82F6),
                            unselectedIconColor = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                            unselectedTextColor = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                            indicatorColor = if (isDarkTheme) Color(0xFF1E40AF) else Color(0xFFDBEAFE)
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