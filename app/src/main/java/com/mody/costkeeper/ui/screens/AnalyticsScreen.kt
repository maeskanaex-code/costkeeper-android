package com.mody.costkeeper.ui.screens

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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.res.stringResource
import com.mody.costkeeper.ui.components.AnalyticsEmptyState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mody.costkeeper.R
import com.mody.costkeeper.data.local.ExpenseEntity
import com.mody.costkeeper.ui.components.YandexBannerAd

import com.mody.costkeeper.ui.viewmodel.ExpenseViewModel
import com.mody.costkeeper.utils.AppCurrency
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

@Composable
fun AnalyticsScreen(
    viewModel: ExpenseViewModel,
    isDarkTheme: Boolean,
    currency: AppCurrency
) {
    val expenses by viewModel.expenses.collectAsState(initial = emptyList())

    val scrollState = rememberScrollState()

    val backgroundColor = if (isDarkTheme) Color(0xFF0F172A) else Color(0xFFF1F5F9)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = stringResource(R.string.analytics_title),
            color = if (isDarkTheme) Color.White else Color(0xFF1E293B),
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )

        if (expenses.isEmpty()) {
            AnalyticsEmptyState(isDarkTheme = isDarkTheme)
        } else {
            WeeklySummaryCard(expenses, isDarkTheme, currency)
            WeeklyBarChartCard(expenses, isDarkTheme)
            WeeklyComparisonCard(expenses, isDarkTheme, currency)
            CategoryBreakdownCard(expenses, stringResource(R.string.expenses_this_week), isIncome = false, isDarkTheme, currency)
            CategoryBreakdownCard(expenses, stringResource(R.string.income_this_week), isIncome = true, isDarkTheme, currency)
        }

        Spacer(Modifier.height(8.dp))

        YandexBannerAd(
            modifier = Modifier.padding(horizontal = 8.dp),
            adUnitId = "R-M-19253258-1"
        )
    }
}

/* ================= WEEKLY SUMMARY ================= */

@Composable
fun WeeklySummaryCard(expenses: List<ExpenseEntity>, isDarkTheme: Boolean, currency: AppCurrency) {
    val thisWeekStart = getStartOfDay(System.currentTimeMillis() - TimeUnit.DAYS.toMillis(6))

    val weeklyIncome = expenses
        .filter { it.amount > 0 }
        .filter { it.date >= thisWeekStart }
        .sumOf { it.amount }

    val weeklyExpense = expenses
        .filter { it.amount < 0 }
        .filter { it.date >= thisWeekStart }
        .sumOf { kotlin.math.abs(it.amount) }

    val balance = weeklyIncome - weeklyExpense

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) Color(0xFF1E293B) else Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkTheme) 0.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.this_week),
                color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                fontSize = 12.sp
            )
            Spacer(Modifier.height(8.dp))

            Text(
                text = "${currency.symbol}${"%.2f".format(balance)}",
                color = if (balance >= 0) Color(0xFF4ADE80) else Color(0xFFF87171),
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = stringResource(R.string.balance),
                color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                fontSize = 12.sp
            )

            Spacer(Modifier.height(16.dp))
            HorizontalDivider(color = if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0))
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFF4ADE80), CircleShape)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.income),
                            color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                            fontSize = 12.sp
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${currency.symbol}${"%.2f".format(weeklyIncome)}",
                        color = Color(0xFF4ADE80),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(40.dp)
                        .background(if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0))
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(Color(0xFFF87171), CircleShape)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = stringResource(R.string.expense),
                            color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                            fontSize = 12.sp
                        )
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = "${currency.symbol}${"%.2f".format(weeklyExpense)}",
                        color = Color(0xFFF87171),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/* ================= WEEKLY BAR CHART ================= */

@Composable
fun WeeklyBarChartCard(expenses: List<ExpenseEntity>, isDarkTheme: Boolean) {
    val now = System.currentTimeMillis()
    val dateFormat = SimpleDateFormat("EEE", Locale.getDefault())

    val dailyData = remember(expenses) {
        (0..6).map { daysAgo ->
            val dayStart = getStartOfDay(now - TimeUnit.DAYS.toMillis(daysAgo.toLong()))
            val dayEnd = dayStart + TimeUnit.DAYS.toMillis(1)

            val dailyIncome = expenses
                .filter { it.amount > 0 }
                .filter { it.date in dayStart until dayEnd }
                .sumOf { it.amount }

            val dailyExpense = expenses
                .filter { it.amount < 0 }
                .filter { it.date in dayStart until dayEnd }
                .sumOf { kotlin.math.abs(it.amount) }

            val label = dateFormat.format(Date(dayStart))
            Triple(label, dailyIncome, dailyExpense)
        }.reversed()
    }

    val maxValue = dailyData.maxOfOrNull { maxOf(it.second, it.third) } ?: 0.0

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) Color(0xFF1E293B) else Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkTheme) 0.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(modifier = Modifier.size(10.dp).background(Color(0xFF4ADE80), CircleShape))
                Spacer(Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.income),
                    color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                    fontSize = 11.sp
                )
                Spacer(Modifier.width(16.dp))
                Box(modifier = Modifier.size(10.dp).background(Color(0xFFF87171), CircleShape))
                Spacer(Modifier.width(6.dp))
                Text(
                    text = stringResource(R.string.expense),
                    color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                    fontSize = 11.sp
                )
            }

            Spacer(Modifier.height(12.dp))

            if (maxValue == 0.0) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(R.string.no_data),
                        color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                        fontSize = 14.sp
                    )
                }
            } else {
                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                ) {
                    val groupCount = dailyData.size
                    val groupWidth = size.width / groupCount
                    val barWidth = groupWidth * 0.3f
                    val gapBetweenBars = barWidth * 0.3f
                    val safeMax = if (maxValue > 0) maxValue else 1.0

                    dailyData.forEachIndexed { index, (label, income, expense) ->
                        val incomeRatio = (income / safeMax).toFloat()
                        val expenseRatio = (expense / safeMax).toFloat()

                        val incomeHeight = (size.height - 40f) * incomeRatio
                        val expenseHeight = (size.height - 40f) * expenseRatio

                        val groupStartX = index * groupWidth
                        val centerX = groupStartX + groupWidth / 2

                        val incomeX = centerX - barWidth - gapBetweenBars / 2
                        val expenseX = centerX + gapBetweenBars / 2

                        if (incomeHeight > 0f) {
                            drawRoundRect(
                                color = Color(0xFF4ADE80),
                                topLeft = Offset(incomeX, size.height - 40f - incomeHeight),
                                size = Size(barWidth, incomeHeight),
                                cornerRadius = CornerRadius(3f, 3f)
                            )
                        }

                        if (expenseHeight > 0f) {
                            drawRoundRect(
                                color = Color(0xFFF87171),
                                topLeft = Offset(expenseX, size.height - 40f - expenseHeight),
                                size = Size(barWidth, expenseHeight),
                                cornerRadius = CornerRadius(3f, 3f)
                            )
                        }

                        drawContext.canvas.nativeCanvas.drawText(
                            label,
                            centerX,
                            size.height - 8f,
                            android.graphics.Paint().apply {
                                color = if (isDarkTheme) android.graphics.Color.parseColor("#94A3B8") else android.graphics.Color.parseColor("#475569")
                                textAlign = android.graphics.Paint.Align.CENTER
                                textSize = 26f
                            }
                        )
                    }
                }
            }
        }
    }
}

/* ================= WEEKLY COMPARISON ================= */

@Composable
fun WeeklyComparisonCard(expenses: List<ExpenseEntity>, isDarkTheme: Boolean, currency: AppCurrency) {
    val now = System.currentTimeMillis()

    val thisWeekStart = getStartOfDay(now - TimeUnit.DAYS.toMillis(6))

    val thisWeekIncome = expenses
        .filter { it.amount > 0 }
        .filter { it.date >= thisWeekStart }
        .sumOf { it.amount }

    val thisWeekExpense = expenses
        .filter { it.amount < 0 }
        .filter { it.date >= thisWeekStart }
        .sumOf { kotlin.math.abs(it.amount) }

    val lastWeekStart = getStartOfDay(now - TimeUnit.DAYS.toMillis(13))
    val lastWeekEnd = thisWeekStart

    val lastWeekIncome = expenses
        .filter { it.amount > 0 }
        .filter { it.date in lastWeekStart until lastWeekEnd }
        .sumOf { it.amount }

    val lastWeekExpense = expenses
        .filter { it.amount < 0 }
        .filter { it.date in lastWeekStart until lastWeekEnd }
        .sumOf { kotlin.math.abs(it.amount) }

    val incomeDiff = thisWeekIncome - lastWeekIncome
    val incomePercent = if (lastWeekIncome > 0) ((incomeDiff / lastWeekIncome) * 100) else 0.0

    val expenseDiff = thisWeekExpense - lastWeekExpense
    val expensePercent = if (lastWeekExpense > 0) ((expenseDiff / lastWeekExpense) * 100) else 0.0

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) Color(0xFF1E293B) else Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkTheme) 0.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = stringResource(R.string.vs_last_week),
                color = if (isDarkTheme) Color.White else Color(0xFF1E293B),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.income),
                        color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${currency.symbol}${"%.2f".format(thisWeekIncome)}",
                        color = Color(0xFF4ADE80),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${if (incomeDiff > 0) "+" else ""}${"%.1f".format(incomePercent)}%",
                        color = if (incomeDiff > 0) Color(0xFF4ADE80) else Color(0xFFF87171),
                        fontSize = 13.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(50.dp)
                        .background(if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0))
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = stringResource(R.string.expense),
                        color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                        fontSize = 12.sp
                    )
                    Text(
                        text = "${currency.symbol}${"%.2f".format(thisWeekExpense)}",
                        color = Color(0xFFF87171),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "${if (expenseDiff > 0) "+" else ""}${"%.1f".format(expensePercent)}%",
                        color = if (expenseDiff > 0) Color(0xFFF87171) else Color(0xFF4ADE80),
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}

/* ================= CATEGORY BREAKDOWN ================= */

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
            else entry.value.sumOf { kotlin.math.abs(it.amount) }
        }
        .toList()
        .sortedByDescending { it.second }

    val total = weeklyByCategory.sumOf { it.second }
    val hasData = total > 0.0

    val colors = if (isIncome) {
        listOf(
            Color(0xFF4ADE80), Color(0xFF34D399), Color(0xFF6EE7B7),
            Color(0xFFA7F3D0), Color(0xFF22C55E), Color(0xFF10B981)
        )
    } else {
        listOf(
            Color(0xFFF87171), Color(0xFFFB7185), Color(0xFFFCA5A5),
            Color(0xFFDC2626), Color(0xFFE11D48), Color(0xFFBE123C)
        )
    }

    val titleColor = if (isIncome) Color(0xFF4ADE80) else Color(0xFFF87171)
    val emptyText = if (isIncome) stringResource(R.string.no_income_this_week)
    else stringResource(R.string.no_expenses_this_week)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = if (isDarkTheme) Color(0xFF1E293B) else Color.White
        ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isDarkTheme) 0.dp else 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Text(
                text = title,
                color = if (isDarkTheme) Color.White else Color(0xFF1E293B),
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
            Spacer(Modifier.height(4.dp))
            Text(
                text = "${currency.symbol}${"%.2f".format(total)} ${stringResource(R.string.total_label)}",
                color = titleColor,
                fontSize = 13.sp
            )
            Spacer(Modifier.height(12.dp))

            if (!hasData) {
                Text(
                    text = emptyText,
                    color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                    fontSize = 13.sp
                )
            } else {
                weeklyByCategory.forEachIndexed { index, (category, amount) ->
                    val percent = if (total > 0) (amount / total * 100) else 0.0

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .background(colors[index % colors.size], CircleShape)
                            )
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = category,
                                color = if (isDarkTheme) Color.White else Color(0xFF1E293B),
                                fontSize = 14.sp,
                                maxLines = 1
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text(
                                text = "${currency.symbol}${"%.2f".format(amount)}",
                                color = if (isDarkTheme) Color.White else Color(0xFF1E293B),
                                fontSize = 14.sp
                            )
                            Text(
                                text = "${"%.0f".format(percent)}%",
                                color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

/* ================= HELPER ================= */

fun getStartOfDay(timestamp: Long): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = timestamp
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}