package com.mody.costkeeper.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.unit.dp
import com.mody.costkeeper.data.local.ExpenseEntity
import kotlinx.coroutines.launch
import kotlin.math.atan2
import kotlin.math.PI

@Composable
fun CategoryPieChart(
    expenses: List<ExpenseEntity>
) {

    val haptic = LocalHapticFeedback.current

    val grouped = expenses
        .groupBy { it.category }
        .mapValues { it.value.sumOf { e -> e.amount } }
        .toList()
        .sortedByDescending { it.second }

    val colors = listOf(
        Color(0xFF6FA8FF),
        Color(0xFF3DDC97),
        Color(0xFFFFC857),
        Color(0xFFFF6B6B),
        Color(0xFFB388FF),
        Color(0xFF4DD0E1)
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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        contentAlignment = Alignment.Center
    ) {

        Canvas(
            modifier = Modifier
                .size(220.dp)
                .pointerInput(Unit) {

                    detectTapGestures { offset ->

                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)

                        val center = Offset(size.width / 2f, size.height / 2f)
                        val x = offset.x - center.x
                        val y = offset.y - center.y

                        val angle = (atan2(y, x) * (180 / PI).toFloat() + 360) % 360

                        var startAngle = -90f

                        grouped.forEachIndexed { index, item ->

                            val value = item.second.toFloat()
                            val sweep = (value / total) * 360f
                            val endAngle = startAngle + sweep

                            if (angle in startAngle..endAngle) {

                                selectedIndex = index

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

            var startAngle = -90f

            grouped.forEachIndexed { index, item ->

                val value = item.second.toFloat()
                val sweep = (value / total) * 360f
                val animatedSweep = sweep * animation.value

                val isSelected = selectedIndex == index

                drawArc(
                    color = if (isSelected)
                        colors[index % colors.size]
                    else
                        colors[index % colors.size].copy(alpha = 0.5f),

                    startAngle = startAngle,
                    sweepAngle = animatedSweep,
                    useCenter = true,
                    size = Size(size.width, size.height)
                )

                startAngle += sweep
            }
        }

        val centerData = if (selectedIndex >= 0)
            grouped[selectedIndex]
        else null

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .background(
                    Color.Black.copy(alpha = 0.35f),
                    RoundedCornerShape(14.dp)
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {

            Text(
                text = centerData?.first ?: "Total Balance",
                color = Color.White.copy(alpha = 0.9f)
            )

            Text(
                text = "€${"%.2f".format(centerData?.second ?: total)}",
                color = Color.White
            )
        }
    }
}