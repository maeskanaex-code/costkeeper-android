package com.mody.costkeeper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mody.costkeeper.R
import com.mody.costkeeper.utils.AppCurrency
import kotlin.math.min

@Composable
fun BudgetCard(
    budget: Double,
    spent: Double,
    onEditBudget: () -> Unit,
    isDarkTheme: Boolean = true,
    currency: AppCurrency = AppCurrency.EUR,
    modifier: Modifier = Modifier
) {
    val percentage = if (budget > 0) min((spent / budget * 100).toFloat(), 100f) else 0f
    val remaining = budget - spent
    val isOverBudget = remaining < 0

    // Eye-friendly colors
    val lightCardColor = Color(0xFFF0F9FF)
    val darkCardColor = Color(0xFF1E3A5F)

    // For warning/over budget - softer orange
    val warningColor = Color(0xFFE8913E)
    val warningLight = Color(0xFFF5A623)

    // For good status - soft green
    val goodColor = Color(0xFF3BA373)
    val goodColorLight = Color(0xFF2E7D64)

    // Get localized strings
    val monthlyBudgetText = stringResource(R.string.monthly_budget)
    val editText = stringResource(R.string.edit)
    val budgetText = stringResource(R.string.budget_label)
    val spentText = stringResource(R.string.spent_label)
    val overBudgetText = stringResource(R.string.over_budget)
    val remainingText = stringResource(R.string.remaining)
    val warningText = stringResource(R.string.budget_warning)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) darkCardColor else lightCardColor
        ),
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = monthlyBudgetText,
                    color = if (isDarkTheme) Color.White else Color(0xFF1E3A5F),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                TextButton(onClick = onEditBudget) {
                    Text(
                        text = editText,
                        color = if (isDarkTheme) Color(0xFF7EB8DA) else Color(0xFF4A90C4)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = budgetText,
                        color = if (isDarkTheme) Color(0xFFA8B8C8) else Color(0xFF5A6E8A),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${currency.symbol}${"%.2f".format(budget)}",
                        color = if (isDarkTheme) Color.White else Color(0xFF1E3A5F),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = spentText,
                        color = if (isDarkTheme) Color(0xFFA8B8C8) else Color(0xFF5A6E8A),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${currency.symbol}${"%.2f".format(spent)}",
                        color = if (isOverBudget) {
                            if (isDarkTheme) warningColor else warningLight
                        } else {
                            if (isDarkTheme) goodColor else goodColorLight
                        },
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Progress Bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
                    .background(if (isDarkTheme) Color(0xFF2D3E5F) else Color(0xFFE2E8F0))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(percentage / 100f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(4.dp))
                        .background(
                            when {
                                isOverBudget -> if (isDarkTheme) warningColor else warningLight
                                percentage > 80f -> if (isDarkTheme) Color(0xFFE8913E) else Color(0xFFF5A623)
                                else -> if (isDarkTheme) goodColor else goodColorLight
                            }
                        )
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Status message
            Text(
                text = if (isOverBudget)
                    "$warningText ${currency.symbol}${"%.2f".format(-remaining)}"
                else
                    "💰 ${currency.symbol}${"%.2f".format(remaining)} $remainingText",
                color = if (isOverBudget) {
                    if (isDarkTheme) warningColor else warningLight
                } else {
                    if (isDarkTheme) Color(0xFFA8B8C8) else Color(0xFF5A6E8A)
                },
                fontSize = 12.sp
            )

            if (percentage > 80f && !isOverBudget) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$warningText ${"%.0f".format(percentage)}%",
                    color = if (isDarkTheme) Color(0xFFE8913E) else Color(0xFFF5A623),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
fun EditBudgetDialog(
    currentBudget: Double,
    onBudgetSet: (Double) -> Unit,
    onDismiss: () -> Unit,
    isDarkTheme: Boolean = true,
    currency: AppCurrency = AppCurrency.EUR
) {
    var budgetInput by remember { mutableStateOf(currentBudget.toString()) }
    var showError by remember { mutableStateOf(false) }

    val setBudgetText = stringResource(R.string.set_budget)
    val cancelText = stringResource(R.string.cancel)
    val budgetAmountText = stringResource(R.string.budget_amount)
    val pleaseEnterValidAmountText = stringResource(R.string.please_enter_valid_amount)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = if (isDarkTheme) Color(0xFF1E293B) else Color.White,
        title = {
            Text(
                text = setBudgetText,
                color = if (isDarkTheme) Color.White else Color(0xFF1E3A5F)
            )
        },
        text = {
            Column {
                OutlinedTextField(
                    value = budgetInput,
                    onValueChange = {
                        budgetInput = it
                        showError = false
                    },
                    label = { Text("$budgetAmountText (${currency.symbol})") },
                    isError = showError,
                    leadingIcon = {
                        Text(
                            text = currency.symbol,
                            color = if (isDarkTheme) Color(0xFF7EB8DA) else Color(0xFF4A90C4)
                        )
                    },
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = if (isDarkTheme) Color.White else Color(0xFF1E3A5F),
                        unfocusedTextColor = if (isDarkTheme) Color.White else Color(0xFF1E3A5F),
                        focusedBorderColor = Color(0xFF4A90C4),
                        unfocusedBorderColor = if (isDarkTheme) Color(0xFF334155) else Color(0xFFCBD5E1)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                if (showError) {
                    Text(
                        text = pleaseEnterValidAmountText,
                        color = if (isDarkTheme) Color(0xFFE8913E) else Color(0xFFF5A623),
                        fontSize = 12.sp
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newBudget = budgetInput.toDoubleOrNull()
                    if (newBudget == null || newBudget <= 0) {
                        showError = true
                        return@Button
                    }
                    onBudgetSet(newBudget)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(if (isDarkTheme) Color(0xFF4A90C4) else Color(0xFF4A90C4))
            ) {
                Text(setBudgetText, color = Color.White)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = cancelText,
                    color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
                )
            }
        }
    )
}