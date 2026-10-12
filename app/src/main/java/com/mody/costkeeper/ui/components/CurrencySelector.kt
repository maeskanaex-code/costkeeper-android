package com.mody.costkeeper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mody.costkeeper.utils.AppCurrency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CurrencySelectorDialog(
    currentCurrency: AppCurrency,
    onCurrencySelected: (AppCurrency) -> Unit,
    onDismiss: () -> Unit,
    isDarkTheme: Boolean = true
) {
    val currencies = AppCurrency.values()
    var selectedCurrency by remember { mutableStateOf(currentCurrency) }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = if (isDarkTheme) Color(0xFF1E293B) else Color.White,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("🌍", fontSize = 24.sp)
                Text(
                    text = "Select Currency",
                    color = if (isDarkTheme) Color.White else Color(0xFF1E293B),
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                Text(
                    text = "Choose your preferred currency for all amounts",
                    color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                    fontSize = 12.sp,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(currencies) { currency ->
                        CurrencyOptionItem(
                            currency = currency,
                            isSelected = selectedCurrency == currency,
                            onClick = { selectedCurrency = currency },
                            isDarkTheme = isDarkTheme
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onCurrencySelected(selectedCurrency)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(Color(0xFF3B82F6))
            ) {
                Text("Apply", color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                )
            }
        }
    )
}

@Composable
fun CurrencyOptionItem(
    currency: AppCurrency,
    isSelected: Boolean,
    onClick: () -> Unit,
    isDarkTheme: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected && !isDarkTheme) Color(0xFFEFF6FF) else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(currency.flag, fontSize = 20.sp)
            Column {
                Text(
                    text = "${currency.symbol} ${currency.code}",
                    color = if (isDarkTheme) Color.White else Color(0xFF1E293B),
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
                Text(
                    text = currency.currencyName,
                    color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                    fontSize = 11.sp
                )
            }
        }

        if (isSelected) {
            Icon(
                Icons.Default.Check,
                contentDescription = "Selected",
                tint = Color(0xFF4ADE80),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}