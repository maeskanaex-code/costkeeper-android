package com.mody.costkeeper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mody.costkeeper.data.local.ExpenseEntity
import com.mody.costkeeper.ui.viewmodel.ExpenseViewModel
import com.mody.costkeeper.utils.AppCurrency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditExpenseDialog(
    expense: ExpenseEntity,
    viewModel: ExpenseViewModel,
    onDismiss: () -> Unit,
    isDarkTheme: Boolean = true,
    currency: AppCurrency = AppCurrency.EUR
) {
    var amount by remember { mutableStateOf(kotlin.math.abs(expense.amount).toString()) }
    var category by remember { mutableStateOf(expense.category) }
    var showError by remember { mutableStateOf(false) }

    val expenseCategories = listOf(
        "Food & Dining", "Transportation", "Shopping", "Entertainment",
        "Bills & Utilities", "Healthcare", "Education", "Rent", "Travel", "Other"
    )
    val incomeCategories = listOf("Salary", "Others")
    val categories = if (expense.amount > 0) incomeCategories else expenseCategories

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = if (isDarkTheme) Color(0xFF1E293B) else Color.White,
        title = {
            Text(
                text = "Edit Expense",
                color = if (isDarkTheme) Color.White else Color(0xFF1E293B)
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                            amount = it
                            showError = false
                        }
                    },
                    label = { Text("Amount (${currency.symbol})") },
                    isError = showError,
                    keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                        keyboardType = androidx.compose.ui.text.input.KeyboardType.Decimal
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = if (isDarkTheme) Color.White else Color(0xFF1E293B),
                        unfocusedTextColor = if (isDarkTheme) Color.White else Color(0xFF1E293B),
                        focusedLabelColor = if (isDarkTheme) Color.White else Color(0xFF3B82F6),
                        unfocusedLabelColor = if (isDarkTheme) Color(0xFFCBD5E1) else Color(0xFF64748B)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                var expanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = it }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = if (isDarkTheme) Color.White else Color(0xFF1E293B),
                            unfocusedTextColor = if (isDarkTheme) Color.White else Color(0xFF1E293B),
                            focusedLabelColor = if (isDarkTheme) Color.White else Color(0xFF3B82F6),
                            unfocusedLabelColor = if (isDarkTheme) Color(0xFFCBD5E1) else Color(0xFF64748B)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.background(if (isDarkTheme) Color(0xFF1E293B) else Color.White)
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        text = cat,
                                        color = if (isDarkTheme) Color.White else Color(0xFF1E293B)
                                    )
                                },
                                onClick = {
                                    category = cat
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                if (showError) {
                    Text(
                        text = "Please enter a valid amount",
                        color = Color(0xFFF87171),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(top = 4.dp)
                    )
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
                    val finalAmount = if (expense.amount > 0) v else -v
                    viewModel.addExpense(category, finalAmount, category)
                    onDismiss()
                },
                colors = ButtonDefaults.buttonColors(Color(0xFF3B82F6))
            ) {
                Text("Save Changes", color = Color.White)
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