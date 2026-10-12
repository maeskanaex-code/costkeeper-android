package com.mody.costkeeper.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mody.costkeeper.data.local.ExpenseEntity
import kotlinx.coroutines.delay

@Composable
fun CategoryLegend(
    expenses: List<ExpenseEntity>
) {

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

    val total = grouped.sumOf { it.second }

    Column(modifier = Modifier.fillMaxWidth()) {

        grouped.forEachIndexed { index, item ->

            var visible by remember { mutableStateOf(false) }

            LaunchedEffect(Unit) {
                delay(index * 60L) // stagger animation
                visible = true
            }

            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(animationSpec = tween(400))
            ) {

                val (category, value) = item
                val percent = if (total > 0) (value / total * 100) else 0.0

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Row(verticalAlignment = Alignment.CenterVertically) {

                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .background(
                                    colors[index % colors.size],
                                    CircleShape
                                )
                        )

                        Spacer(Modifier.width(10.dp))

                        Text(category, color = Color.White)
                    }

                    Column(horizontalAlignment = Alignment.End) {

                        Text(
                            "€${"%.2f".format(value)}",
                            color = Color.White
                        )

                        Text(
                            "${"%.0f".format(percent)}%",
                            color = Color(0xFF8A94A3)
                        )
                    }
                }
            }
        }
    }
}