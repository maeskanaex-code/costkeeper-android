package com.mody.costkeeper.ui.screens

import android.graphics.Paint
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mody.costkeeper.R
import com.mody.costkeeper.data.local.ExpenseEntity
import com.mody.costkeeper.ui.components.AnalyticsEmptyState
import com.mody.costkeeper.ui.components.CategoryPieChart
import com.mody.costkeeper.ui.components.YandexBannerAd
import com.mody.costkeeper.ui.theme.*
import com.mody.costkeeper.ui.viewmodel.ExpenseViewModel
import com.mody.costkeeper.utils.AppCurrency
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

@Composable
fun AnalyticsScreen(
    viewModel: ExpenseViewModel,
    isDarkTheme: Boolean,
    currency: AppCurrency
) {
    val expenses by viewModel.expenses.collectAsState(initial = emptyList())
    val scrollState = rememberScrollState()

    val background = if (isDarkTheme) DarkBackground else LightBackground
    val textPrimary = if (isDarkTheme) TextPrimary else LightTextPrimary
    val textMuted = if (isDarkTheme) TextMuted else LightTextMuted

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .verticalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Column {
            Text(
                text = stringResource(R.string.analytics_title),
                color = textPrimary,
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = stringResource(R.string.analytics_subtitle),
                color = textMuted,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (expenses.isEmpty()) {
            AnalyticsEmptyState(isDarkTheme = isDarkTheme)
        } else {
            WeeklySummaryCard(expenses, isDarkTheme, currency)
            WeeklyBarChartCard(expenses, isDarkTheme)
            WeeklyComparisonCard(expenses, isDarkTheme, currency)
            CategoryPieChartCard(expenses, isDarkTheme, currency)
            CategoryBreakdownCard(expenses, stringResource(R.string.analytics_expenses_by_category), isIncome = false, isDarkTheme, currency)
            CategoryBreakdownCard(expenses, stringResource(R.string.analytics_income_by_category), isIncome = true, isDarkTheme, currency)
        }

        Spacer(Modifier.height(8.dp))

        YandexBannerAd(
            modifier = Modifier.padding(horizontal = 8.dp),
            adUnitId = "R-M-19253258-1"
        )

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
fun WeeklySummaryCard(expenses: List<ExpenseEntity>, isDarkTheme: Boolean, currency: AppCurrency) {
    val thisWeekStart = getStartOfDay(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(6))

    val weeklyIncome = expenses.filter { it.amount > 0 }.filter { it.date >= thisWeekStart }.sumOf { it.amount }
    val weeklyExpense = expenses.filter { it.amount < 0 }.filter { it.date >= thisWeekStart }.sumOf { abs(it.amount) }
    val balance = weeklyIncome - weeklyExpense

    val cardBg = if (isDarkTheme) DarkSurface else LightSurface
    val textPrimary = if (isDarkTheme) TextPrimary else LightTextPrimary
    val textMuted = if (isDarkTheme) TextMuted else LightTextMuted

    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.analytics_this_week_label),
                color = textMuted,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(8.dp))

            Text(
                text = "${currency.symbol}${"%.2f".format(balance)}",
                color = if (balance >= 0) IncomeGreen else ExpenseRed,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = stringResource(R.string.analytics_net_balance),
                color = textMuted,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = if (isDarkTheme) DividerDark else LightDivider)
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(IncomeGreen))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.income),
                            color = textMuted,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${currency.symbol}${"%.2f".format(weeklyIncome)}",
                        color = IncomeGreen,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(modifier = Modifier.width(1.dp).height(40.dp).background(if (isDarkTheme) DividerDark else LightDivider))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(ExpenseRed))
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.expense),
                            color = textMuted,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${currency.symbol}${"%.2f".format(weeklyExpense)}",
                        color = ExpenseRed,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
fun WeeklyBarChartCard(expenses: List<ExpenseEntity>, isDarkTheme: Boolean) {
    val now = System.currentTimeMillis()
    val dateFormat = SimpleDateFormat("EEE", Locale.getDefault())

    val dailyData = remember(expenses) {
        (0..6).map { daysAgo ->
            val dayStart = getStartOfDay(now - TimeUnit.DAYS.toMillis(daysAgo.toLong()))
            val dayEnd = dayStart + TimeUnit.DAYS.toMillis(1)
            val dailyIncome = expenses.filter { it.amount > 0 }.filter { it.date in dayStart until dayEnd }.sumOf { it.amount }
            val dailyExpense = expenses.filter { it.amount < 0 }.filter { it.date in dayStart until dayEnd }.sumOf { abs(it.amount) }
            val label = dateFormat.format(Date(dayStart))
            Triple(label, dailyIncome, dailyExpense)
        }.reversed()
    }

    val maxValue = dailyData.maxOfOrNull { maxOf(it.second, it.third) } ?: 0.0

    val cardBg = if (isDarkTheme) DarkSurface else LightSurface
    val textPrimary = if (isDarkTheme) TextPrimary else LightTextPrimary
    val textMuted = if (isDarkTheme) TextMuted else LightTextMuted

    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Text(
                text = stringResource(R.string.analytics_weekly_activity),
                color = textMuted,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.analytics_daily_income_vs_expense),
                color = textPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(IncomeGreen))
                Spacer(Modifier.width(6.dp))
                Text(stringResource(R.string.income), color = textMuted, style = MaterialTheme.typography.labelSmall)
                Spacer(Modifier.width(16.dp))
                Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(ExpenseRed))
                Spacer(Modifier.width(6.dp))
                Text(stringResource(R.string.expense), color = textMuted, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(Modifier.height(12.dp))

            if (maxValue == 0.0) {
                Box(modifier = Modifier.fillMaxWidth().height(180.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.analytics_no_activity),
                        color = textMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                Canvas(modifier = Modifier.fillMaxWidth().height(180.dp)) {
                    val groupCount = dailyData.size
                    val groupWidth = size.width / groupCount
                    val barWidth = groupWidth * 0.28f
                    val gap = barWidth * 0.4f
                    val safeMax = if (maxValue > 0) maxValue else 1.0
                    val labelHeightPx = 40f

                    dailyData.forEachIndexed { index, (label, income, expense) ->
                        val incomeRatio = (income / safeMax).toFloat()
                        val expenseRatio = (expense / safeMax).toFloat()
                        val incomeHeight = (size.height - labelHeightPx) * incomeRatio
                        val expenseHeight = (size.height - labelHeightPx) * expenseRatio
                        val groupStartX = index * groupWidth
                        val centerX = groupStartX + groupWidth / 2
                        val incomeX = centerX - barWidth - gap / 2
                        val expenseX = centerX + gap / 2

                        if (incomeHeight > 0f) {
                            drawRoundRect(
                                color = IncomeGreen,
                                topLeft = Offset(incomeX, size.height - labelHeightPx - incomeHeight),
                                size = Size(barWidth, incomeHeight),
                                cornerRadius = CornerRadius(4f, 4f)
                            )
                        }
                        if (expenseHeight > 0f) {
                            drawRoundRect(
                                color = ExpenseRed,
                                topLeft = Offset(expenseX, size.height - labelHeightPx - expenseHeight),
                                size = Size(barWidth, expenseHeight),
                                cornerRadius = CornerRadius(4f, 4f)
                            )
                        }

                        drawContext.canvas.nativeCanvas.drawText(
                            label,
                            centerX,
                            size.height - 10f,
                            Paint().apply {
                                color = if (isDarkTheme) android.graphics.Color.parseColor("#71717A") else android.graphics.Color.parseColor("#52525B")
                                textAlign = Paint.Align.CENTER
                                textSize = 28f
                                isAntiAlias = true
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun WeeklyComparisonCard(expenses: List<ExpenseEntity>, isDarkTheme: Boolean, currency: AppCurrency) {
    val now = System.currentTimeMillis()
    val thisWeekStart = getStartOfDay(now - TimeUnit.DAYS.toMillis(6))

    val thisWeekIncome = expenses.filter { it.amount > 0 }.filter { it.date >= thisWeekStart }.sumOf { it.amount }
    val thisWeekExpense = expenses.filter { it.amount < 0 }.filter { it.date >= thisWeekStart }.sumOf { abs(it.amount) }

    val lastWeekStart = getStartOfDay(now - TimeUnit.DAYS.toMillis(13))
    val lastWeekEnd = thisWeekStart

    val lastWeekIncome = expenses.filter { it.amount > 0 }.filter { it.date in lastWeekStart until lastWeekEnd }.sumOf { it.amount }
    val lastWeekExpense = expenses.filter { it.amount < 0 }.filter { it.date in lastWeekStart until lastWeekEnd }.sumOf { abs(it.amount) }

    val incomeDiff = thisWeekIncome - lastWeekIncome
    val incomePercent = if (lastWeekIncome > 0) ((incomeDiff / lastWeekIncome) * 100) else 0.0
    val expenseDiff = thisWeekExpense - lastWeekExpense
    val expensePercent = if (lastWeekExpense > 0) ((expenseDiff / lastWeekExpense) * 100) else 0.0

    val cardBg = if (isDarkTheme) DarkSurface else LightSurface
    val textPrimary = if (isDarkTheme) TextPrimary else LightTextPrimary
    val textMuted = if (isDarkTheme) TextMuted else LightTextMuted

    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Text(
                text = stringResource(R.string.analytics_vs_last_week),
                color = textMuted,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.analytics_how_compares),
                color = textPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.income), color = textMuted, style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = "${currency.symbol}${"%.2f".format(thisWeekIncome)}",
                        color = IncomeGreen,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${if (incomeDiff > 0) "+" else ""}${"%.1f".format(incomePercent)}%",
                        color = if (incomeDiff > 0) IncomeGreen else ExpenseRed,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Box(modifier = Modifier.width(1.dp).height(50.dp).background(if (isDarkTheme) DividerDark else LightDivider))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(stringResource(R.string.expense), color = textMuted, style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = "${currency.symbol}${"%.2f".format(thisWeekExpense)}",
                        color = ExpenseRed,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${if (expenseDiff > 0) "+" else ""}${"%.1f".format(expensePercent)}%",
                        color = if (expenseDiff > 0) ExpenseRed else IncomeGreen,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryPieChartCard(
    expenses: List<ExpenseEntity>,
    isDarkTheme: Boolean,
    currency: AppCurrency
) {
    val cardBg = if (isDarkTheme) DarkSurface else LightSurface
    val textPrimary = if (isDarkTheme) TextPrimary else LightTextPrimary
    val textMuted = if (isDarkTheme) TextMuted else LightTextMuted

    val weekStart = getStartOfDay(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(6))
    val weeklyExpenses = remember(expenses) { expenses.filter { it.date >= weekStart } }

    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Text(
                text = stringResource(R.string.analytics_spending_breakdown),
                color = textMuted,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.analytics_tap_slice),
                color = textPrimary,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            if (weeklyExpenses.isEmpty()) {
                Box(modifier = Modifier.fillMaxWidth().height(200.dp), contentAlignment = Alignment.Center) {
                    Text(
                        text = stringResource(R.string.analytics_no_expenses_week),
                        color = textMuted,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else {
                CategoryPieChart(
                    expenses = weeklyExpenses,
                    isDarkTheme = isDarkTheme,
                    currency = currency
                )
            }
        }
    }
}

@Composable
fun CategoryBreakdownCard(
    expenses: List<ExpenseEntity>,
    title: String,
    isIncome: Boolean,
    isDarkTheme: Boolean,
    currency: AppCurrency
) {
    val thisWeekStart = getStartOfDay(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(6))

    val weeklyByCategory = expenses
        .filter { if (isIncome) it.amount > 0 else it.amount < 0 }
        .filter { it.date >= thisWeekStart }
        .groupBy { it.category }
        .mapValues { entry ->
            if (isIncome) entry.value.sumOf { it.amount }
            else entry.value.sumOf { abs(it.amount) }
        }
        .toList()
        .sortedByDescending { it.second }

    val total = weeklyByCategory.sumOf { it.second }
    val hasData = total > 0.0

    val colors = if (isIncome) {
        listOf(IncomeGreen, Color(0xFF34D399), Color(0xFF6EE7B7), Color(0xFF10B981))
    } else {
        listOf(ExpenseRed, Color(0xFFF87171), Color(0xFFFB7185), Color(0xFFDC2626))
    }

    val titleColor = if (isIncome) IncomeGreen else ExpenseRed
    val emptyText = if (isIncome) stringResource(R.string.analytics_no_income_week)
    else stringResource(R.string.analytics_no_expenses_week)

    val cardBg = if (isDarkTheme) DarkSurface else LightSurface
    val textPrimary = if (isDarkTheme) TextPrimary else LightTextPrimary
    val textMuted = if (isDarkTheme) TextMuted else LightTextMuted

    Card(
        colors = CardDefaults.cardColors(containerColor = cardBg),
        shape = RoundedCornerShape(20.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(20.dp)) {
            Text(
                text = title.uppercase(),
                color = textMuted,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.SemiBold
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${currency.symbol}${"%.2f".format(total)} ${stringResource(R.string.analytics_total)}",
                color = titleColor,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(Modifier.height(16.dp))

            if (!hasData) {
                Text(text = emptyText, color = textMuted, style = MaterialTheme.typography.bodyMedium)
            } else {
                weeklyByCategory.forEachIndexed { index, (category, amount) ->
                    val percent = if (total > 0) (amount / total * 100) else 0.0

                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                            Box(modifier = Modifier.size(10.dp).clip(CircleShape).background(colors[index % colors.size]))
                            Spacer(Modifier.width(10.dp))
                            Text(
                                text = category,
                                color = textPrimary,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Medium,
                                maxLines = 1
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${currency.symbol}${"%.2f".format(amount)}",
                                color = textPrimary,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${"%.0f".format(percent)}%",
                                color = textMuted,
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                    }
                }
            }
        }
    }
}

fun getStartOfDay(timestamp: Long): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timestamp
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}
