package com.mody.costkeeper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mody.costkeeper.R
import com.mody.costkeeper.ui.theme.*
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
    val isWarning = percentage > 80f && !isOverBudget

    // Status color
    val statusColor = when {
        isOverBudget -> ExpenseRed
        isWarning -> WarningAmber
        else -> IncomeGreen
    }

    val backgroundColor = if (isDarkTheme) DarkSurface else LightSurface
    val textPrimary = if (isDarkTheme) TextPrimary else LightTextPrimary
    val textMuted = if (isDarkTheme) TextMuted else LightTextMuted
    val dividerColor = if (isDarkTheme) DividerDark else LightDivider

    val monthlyBudgetText = stringResource(R.string.monthly_budget)
    val editText = stringResource(R.string.edit)
    val budgetText = stringResource(R.string.budget_label)
    val spentText = stringResource(R.string.spent_label)
    val overBudgetText = stringResource(R.string.over_budget)
    val remainingText = stringResource(R.string.remaining)

    Card(
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            // Header row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = monthlyBudgetText.uppercase(),
                    color = textMuted,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold
                )

                IconButton(
                    onClick = onEditBudget,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = editText,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Big spent number
            Text(
                text = "${currency.symbol}${"%.2f".format(spent)}",
                color = textPrimary,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = "of ${currency.symbol}${"%.2f".format(budget)} ${budgetText.lowercase()}",
                color = textMuted,
                style = MaterialTheme.typography.bodyMedium
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Progress bar - thicker, rounded
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp))
                    .background(if (isDarkTheme) DarkSurfaceHighest else LightSurfaceHighest)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(percentage / 100f)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(5.dp))
                        .background(
                            brush = Brush.horizontalGradient(
                                colors = listOf(statusColor, statusColor.copy(alpha = 0.85f))
                            )
                        )
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Status line
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = when {
                        isOverBudget -> "$overBudgetText ${currency.symbol}${"%.2f".format(-remaining)}"
                        else -> "${currency.symbol}${"%.2f".format(remaining)} $remainingText"
                    },
                    color = if (isOverBudget) ExpenseRed else textMuted,
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "${"%.0f".format(percentage)}%",
                    color = statusColor,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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
        containerColor = if (isDarkTheme) DarkSurfaceElevated else LightSurfaceElevated,
        shape = RoundedCornerShape(24.dp),
        title = {
            Text(
                text = setBudgetText,
                color = if (isDarkTheme) TextPrimary else LightTextPrimary,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
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
                            color = PrimaryBlue,
                            style = MaterialTheme.typography.titleLarge
                        )
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = if (isDarkTheme) TextPrimary else LightTextPrimary,
                        unfocusedTextColor = if (isDarkTheme) TextPrimary else LightTextPrimary,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = if (isDarkTheme) BorderDark else LightBorder,
                        cursorColor = PrimaryBlue
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (showError) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = pleaseEnterValidAmountText,
                        color = ExpenseRed,
                        style = MaterialTheme.typography.bodySmall
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
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Text(
                    text = setBudgetText,
                    color = TextOnAccent,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = cancelText,
                    color = if (isDarkTheme) TextSecondary else LightTextSecondary
                )
            }
        }
    )
}
