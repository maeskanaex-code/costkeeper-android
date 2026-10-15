package com.mody.costkeeper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mody.costkeeper.R
import com.mody.costkeeper.data.local.ExpenseEntity
import com.mody.costkeeper.ui.theme.*
import com.mody.costkeeper.ui.viewmodel.ExpenseViewModel
import com.mody.costkeeper.utils.AppCurrency
import kotlin.math.abs

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExpenseDialog(
    expense: ExpenseEntity,
    viewModel: ExpenseViewModel,
    onDismiss: () -> Unit,
    isDarkTheme: Boolean = true,
    currency: AppCurrency = AppCurrency.EUR
) {
    var amount by remember { mutableStateOf(abs(expense.amount).toString()) }
    var category by remember { mutableStateOf(expense.category) }
    var showError by remember { mutableStateOf(false) }

    val isIncome = expense.amount > 0

    val expenseCategories = listOf(
        stringResource(R.string.category_food_dining),
        stringResource(R.string.category_transportation),
        stringResource(R.string.category_shopping),
        stringResource(R.string.category_entertainment),
        stringResource(R.string.category_bills_utilities),
        stringResource(R.string.category_healthcare),
        stringResource(R.string.category_education),
        stringResource(R.string.category_rent),
        stringResource(R.string.category_travel),
        stringResource(R.string.category_other)
    )
    val incomeCategories = listOf(
        stringResource(R.string.category_salary),
        stringResource(R.string.category_others_income)
    )
    val categories = if (isIncome) incomeCategories else expenseCategories

    val surfaceBg = if (isDarkTheme) DarkSurfaceElevated else LightSurfaceElevated
    val textPrimary = if (isDarkTheme) TextPrimary else LightTextPrimary
    val textMuted = if (isDarkTheme) TextMuted else LightTextMuted
    val inputBorder = if (isDarkTheme) BorderDark else LightBorder

    val titleEditIncome = stringResource(R.string.edit_income)
    val titleEditExpense = stringResource(R.string.edit_expense)
    val subtitleText = stringResource(R.string.edit_subtitle)
    val amountLabel = stringResource(R.string.amount)
    val categoryLabel = stringResource(R.string.category)
    val categoryLabelUpper = categoryLabel.uppercase()
    val errText = stringResource(R.string.please_enter_valid_amount)
    val saveText = stringResource(R.string.save_changes)
    val cancelText = stringResource(R.string.cancel)

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = surfaceBg,
        shape = RoundedCornerShape(24.dp),
        title = {
            Column {
                Text(
                    text = if (isIncome) titleEditIncome else titleEditExpense,
                    color = textPrimary,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitleText,
                    color = textMuted,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                            amount = it
                            showError = false
                        }
                    },
                    label = { Text(amountLabel) },
                    leadingIcon = {
                        Text(
                            text = currency.symbol,
                            color = PrimaryBlue,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    },
                    isError = showError,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = textPrimary,
                        unfocusedTextColor = textPrimary,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = inputBorder,
                        cursorColor = PrimaryBlue,
                        focusedLabelColor = PrimaryBlue,
                        unfocusedLabelColor = textMuted
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                if (showError) {
                    Text(
                        text = errText,
                        color = ExpenseRed,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Text(
                    text = categoryLabelUpper,
                    color = textMuted,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(categories) { cat ->
                        CategoryChip(
                            name = cat,
                            selected = category == cat,
                            onClick = { category = cat },
                            isDarkTheme = isDarkTheme
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val v = amount.toDoubleOrNull()
                    if (v == null || v <= 0.0) {
                        showError = true
                        return@Button
                    }
                    viewModel.deleteExpense(expense)
                    val finalAmount = if (isIncome) v else -v
                    viewModel.addExpense(category, finalAmount, category)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            ) {
                Text(
                    text = saveText,
                    color = TextOnAccent,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = cancelText,
                    color = textMuted
                )
            }
        }
    )
}

@Composable
private fun CategoryChip(
    name: String,
    selected: Boolean,
    onClick: () -> Unit,
    isDarkTheme: Boolean
) {
    val bg = if (selected) PrimaryBlue else if (isDarkTheme) DarkSurface else LightSurface
    val textColor = if (selected) TextOnAccent else if (isDarkTheme) TextSecondary else LightTextSecondary
    val border = if (selected) PrimaryBlue else if (isDarkTheme) BorderDark else LightBorder

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(999.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(999.dp))
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Text(
            text = name,
            color = textColor,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium
        )
    }
}
