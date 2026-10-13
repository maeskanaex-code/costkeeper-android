package com.mody.costkeeper.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mody.costkeeper.data.local.ExpenseEntity
import com.mody.costkeeper.ui.theme.*
import com.mody.costkeeper.utils.AppCurrency
import kotlinx.coroutines.launch
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

@Composable
fun CategoryPieChart(
    expenses: List<ExpenseEntity>,
    isDarkTheme: Boolean = true,
    currency: AppCurrency = AppCurrency.EUR,
    modifier: Modifier = Modifier
) {
    val haptic = LocalHapticFeedback.current

    val grouped = remember(expenses) {
        expenses
            .filter { it.amount < 0 }
            .groupBy { it.category }
            .mapValues { it.value.sumOf { e -> kotlin.math.abs(e.amount) } }
            .toList()
            .sortedByDescending { it.second }
    }

    val colors = listOf(
        PrimaryBlue,
        IncomeGreen,
        WarningAmber,
        ExpenseRed,
        Color(0xFF8B5CF6),
        Color(0xFF06B6D4),
        Color(0xFFEC4899),
        Color(0xFFF59E0B)
    )

    val total = grouped.sumOf { it.second }.toFloat()

    val animation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    var selectedIndex by remember { mutableStateOf(-1) }

    LaunchedEffect(grouped) {
        animation.snapTo(0f)
        scope.launch {
            animation.animateTo(1f, tween(900))
        }
    }

    val popOffset = remember { Animatable(0f) }
    LaunchedEffect(selectedIndex) {
        if (selectedIndex >= 0) {
            popOffset.snapTo(0f)
            popOffset.animateTo(1f, tween(250))
        } else {
            popOffset.snapTo(0f)
        }
    }

    val textPrimary = if (isDarkTheme) TextPrimary else LightTextPrimary
    val textSecondary = if (isDarkTheme) TextSecondary else LightTextSecondary
    val holeColor = if (isDarkTheme) DarkSurface else LightSurface

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(280.dp),
        contentAlignment = Alignment.Center
    ) {
        if (total <= 0f) {
            Text(
                text = "No expenses",
                color = textSecondary,
                style = MaterialTheme.typography.bodyMedium
            )
            return@Box
        }

        // Fixed chart size — tap math works against these fixed pixel dimensions.
        val chartSizeDp = 240.dp

        Canvas(
            modifier = Modifier
                .size(chartSizeDp)
                .pointerInput(grouped, total) {
                    detectTapGestures { offset ->
                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)

                        // Use tap coordinates relative to the Canvas box
                        val sidePx = size.width.toFloat()
                        val cx = sidePx / 2f
                        val cy = size.height / 2f
                        val x = offset.x - cx
                        val y = offset.y - cy

                        val distance = sqrt(x * x + y * y)
                        val outerR = sidePx / 2f
                        val innerR = outerR * 0.55f
                        if (distance < innerR || distance > outerR) {
                            selectedIndex = -1
                            return@detectTapGestures
                        }

                        val angleDeg = ((atan2(y, x) * (180 / PI).toFloat()) + 360f) % 360f
                        val adjustedAngle = (angleDeg + 90f) % 360f

                        var startAngle = 0f
                        grouped.forEachIndexed { index, item ->
                            val value = item.second.toFloat()
                            val sweep = (value / total) * 360f
                            val endAngle = startAngle + sweep

                            if (adjustedAngle in startAngle..endAngle) {
                                selectedIndex = if (selectedIndex == index) -1 else index
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                return@detectTapGestures
                            }
                            startAngle += sweep
                        }

                        selectedIndex = -1
                    }
                }
        ) {
            if (total <= 0f) return@Canvas

            val outerRadius = size.minDimension / 2f
            val strokeWidth = outerRadius * 0.45f
            val ringRadius = outerRadius - strokeWidth / 2f
            val centerX = size.width / 2f
            val centerY = size.height / 2f

            var startAngle = -90f

            grouped.forEachIndexed { index, item ->
                val value = item.second.toFloat()
                val sweep = (value / total) * 360f
                val animatedSweep = sweep * animation.value

                val isSelected = selectedIndex == index
                val isDimmed = selectedIndex >= 0 && !isSelected

                val midAngleRad = Math.toRadians((startAngle + sweep / 2).toDouble())
                val popDistance = if (isSelected) 12f * popOffset.value else 0f
                val offsetX = cos(midAngleRad).toFloat() * popDistance
                val offsetY = sin(midAngleRad).toFloat() * popDistance

                val sliceColor = colors[index % colors.size].copy(
                    alpha = when {
                        isSelected -> 1f
                        isDimmed -> 0.35f
                        else -> 1f
                    }
                )

                drawArc(
                    color = sliceColor,
                    startAngle = startAngle,
                    sweepAngle = animatedSweep,
                    useCenter = false,
                    topLeft = Offset(
                        centerX - ringRadius + offsetX,
                        centerY - ringRadius + offsetY
                    ),
                    size = Size(ringRadius * 2, ringRadius * 2),
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Butt)
                )

                startAngle += sweep
            }
        }

        val centerData = if (selectedIndex >= 0 && selectedIndex < grouped.size)
            grouped[selectedIndex] else null

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(holeColor)
        ) {
            if (centerData != null) {
                Text(
                    text = centerData.first,
                    color = textSecondary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${currency.symbol}${"%.2f".format(centerData.second)}",
                    color = textPrimary,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${"%.0f".format(centerData.second / total * 100)}%",
                    color = colors[grouped.indexOf(centerData) % colors.size],
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold
                )
            } else {
                Text(
                    text = "TOTAL",
                    color = textSecondary,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = "${currency.symbol}${"%.2f".format(total)}",
                    color = textPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${grouped.size} categories",
                    color = textSecondary,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        }
    }
}