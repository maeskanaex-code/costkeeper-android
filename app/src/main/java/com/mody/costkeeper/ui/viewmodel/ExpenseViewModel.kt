package com.mody.costkeeper.ui.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mody.costkeeper.data.local.ExpenseEntity
import com.mody.costkeeper.data.repository.ExpenseRepository
import com.mody.costkeeper.utils.ExportHelper
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

class ExpenseViewModel(
    private val repository: ExpenseRepository
) : ViewModel() {

    val expenses: Flow<List<ExpenseEntity>> =
        repository.getAllExpenses()

    // LANGUAGE STATE
    private val _language = MutableStateFlow("EN")
    val language: StateFlow<String> = _language

    fun loadLanguage(context: Context) {
        val prefs = context.getSharedPreferences("costkeeper_prefs", Context.MODE_PRIVATE)
        val savedLang = prefs.getString("language", "EN") ?: "EN"
        _language.value = savedLang
    }

    fun setLanguage(context: Context, lang: String) {
        _language.value = lang
        val prefs = context.getSharedPreferences("costkeeper_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("language", lang).apply()
    }

    // ADD EXPENSE / INCOME (with current date)
    fun addExpense(title: String, amount: Double, category: String) {
        viewModelScope.launch {
            repository.insert(
                ExpenseEntity(
                    title = title,
                    amount = amount,
                    category = category,
                    date = System.currentTimeMillis()
                )
            )
        }
    }

    // ADD EXPENSE / INCOME (with custom date from date picker)
    fun addExpenseWithDate(title: String, amount: Double, category: String, date: Long) {
        viewModelScope.launch {
            repository.insert(
                ExpenseEntity(
                    title = title,
                    amount = amount,
                    category = category,
                    date = date
                )
            )
        }
    }

    // UPDATE EXPENSE (delete old, create new)
    fun updateExpense(oldExpense: ExpenseEntity, newTitle: String, newAmount: Double, newCategory: String, newDate: Long) {
        viewModelScope.launch {
            repository.delete(oldExpense)
            repository.insert(
                ExpenseEntity(
                    title = newTitle,
                    amount = newAmount,
                    category = newCategory,
                    date = newDate
                )
            )
        }
    }

    fun deleteExpense(expense: ExpenseEntity) {
        viewModelScope.launch {
            repository.delete(expense)
        }
    }

    fun clearAllExpenses() {
        viewModelScope.launch {
            repository.clearAll()
        }
    }

    // EXPORT TO CSV - returns an Intent for sharing
    suspend fun exportToCsv(context: Context): Intent? {
        val allExpenses = expenses.first()
        return if (allExpenses.isNotEmpty()) {
            ExportHelper.exportToCsv(context, allExpenses)
        } else {
            null
        }
    }

    // EXPORT TO CSV STRING
    suspend fun exportToCsvString(): String {
        val allExpenses = expenses.first()
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        val header = "ID,Title,Amount,Category,Date,Type"
        val rows = allExpenses.joinToString("\n") { expense ->
            val type = if (expense.amount > 0) "Income" else "Expense"
            val amount = abs(expense.amount)
            "${expense.id},${expense.title},$amount,${expense.category},${dateFormat.format(Date(expense.date))},$type"
        }

        return "$header\n$rows"
    }

    // GET STATISTICS
    fun getTotalIncome(expenses: List<ExpenseEntity>): Double {
        return expenses.filter { it.amount > 0 }.sumOf { it.amount }
    }

    fun getTotalExpense(expenses: List<ExpenseEntity>): Double {
        return expenses.filter { it.amount < 0 }.sumOf { abs(it.amount) }
    }

    fun getBalance(expenses: List<ExpenseEntity>): Double {
        return getTotalIncome(expenses) - getTotalExpense(expenses)
    }

    // GET EXPENSES BY DATE RANGE
    fun getExpensesByDateRange(
        expenses: List<ExpenseEntity>,
        startDate: Long,
        endDate: Long
    ): List<ExpenseEntity> {
        return expenses.filter { it.date in startDate..endDate }
    }

    // GET EXPENSES BY CATEGORY
    fun getExpensesByCategory(
        expenses: List<ExpenseEntity>,
        category: String
    ): List<ExpenseEntity> {
        return expenses.filter { it.category == category }
    }

    // HELPER: Get start of day
    private fun getStartOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    // GET WEEKLY EXPENSES
    fun getWeeklyExpenses(expenses: List<ExpenseEntity>): Map<String, Double> {
        val now = System.currentTimeMillis()
        val dayInMillis = 24 * 60 * 60 * 1000L

        return (0..6).associate { daysAgo ->
            val dayStart = getStartOfDay(now - (daysAgo * dayInMillis))
            val dayEnd = dayStart + dayInMillis

            val dayExpenses = expenses.filter { it.date in dayStart until dayEnd }
            val total = dayExpenses.sumOf { it.amount }

            val dayName = SimpleDateFormat("EEE", Locale.getDefault()).format(Date(dayStart))
            dayName to total
        }.toList().reversed().toMap()
    }

    // GET CATEGORY BREAKDOWN
    fun getCategoryBreakdown(expenses: List<ExpenseEntity>): Map<String, Double> {
        return expenses
            .filter { it.amount < 0 }
            .groupBy { it.category }
            .mapValues { it.value.sumOf { abs(it.amount) } }
            .toList()
            .sortedByDescending { it.second }
            .toMap()
    }

    // GET MONTHLY SUMMARY
    fun getMonthlySummary(expenses: List<ExpenseEntity>): Map<Int, Double> {
        return expenses.groupBy {
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = it.date
            calendar.get(Calendar.MONTH)
        }.mapValues { entry ->
            entry.value.sumOf { expense -> expense.amount }
        }
    }
}