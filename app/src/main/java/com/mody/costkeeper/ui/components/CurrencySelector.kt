package com.mody.costkeeper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mody.costkeeper.ui.theme.*
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

    val surfaceBg = if (isDarkTheme) DarkSurfaceElevated else LightSurfaceElevated
    val textPrimary = if (isDarkTheme) TextPrimary else LightTextPrimary
    val textMuted = if (isDarkTheme) TextMuted else LightTextMuted
    val borderColor = if (isDarkTheme) BorderDark else LightBorder

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = surfaceBg,
        shape = RoundedCornerShape(24.dp),
        title = {
            Column {
                Text(
                    text = "Currency",
                    color = textPrimary,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Choose your preferred currency",
                    color = textMuted,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(currencies) { currency ->
                    CurrencyOptionItem(
                        currency = currency,
                        isSelected = selectedCurrency == currency,
                        onClick = { selectedCurrency = currency },
                        isDarkTheme = isDarkTheme,
                        borderColor = borderColor
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    onCurrencySelected(selectedCurrency)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Text(
                    text = "Apply",
                    color = TextOnAccent,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = textMuted
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
    isDarkTheme: Boolean,
    borderColor: Color
) {
    val rowBg = when {
        isSelected -> PrimaryBlue.copy(alpha = 0.12f)
        isDarkTheme -> DarkSurface
        else -> LightSurface
    }
    val rowBorder = if (isSelected) PrimaryBlue else borderColor
    val textPrimary = if (isDarkTheme) TextPrimary else LightTextPrimary
    val textMuted = if (isDarkTheme) TextMuted else LightTextMuted

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(rowBg)
            .border(
                width = if (isSelected) 1.5.dp else 1.dp,
                color = rowBorder,
                shape = RoundedCornerShape(14.dp)
            )
            .clickable { onClick() }
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(if (isDarkTheme) DarkSurfaceHighest else LightSurfaceHighest),
                contentAlignment = Alignment.Center
            ) {
                Text(currency.flag, fontSize = 22.sp)
            }

            Column {
                Text(
                    text = "${currency.symbol} ${currency.code}",
                    color = textPrimary,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.SemiBold
                )
                Text(
                    text = currency.currencyName,
                    color = textMuted,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        if (isSelected) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlue),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Check,
                    contentDescription = "Selected",
                    tint = TextOnAccent,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
