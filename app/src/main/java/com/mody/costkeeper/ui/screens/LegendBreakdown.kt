package com.mody.costkeeper.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.mody.costkeeper.data.local.ExpenseEntity

@Composable
fun LegendBreakdown(expenses: List<ExpenseEntity>) {

    val grouped = expenses.groupBy { it.category }
        .mapValues { it.value.sumOf { e -> e.amount } }
        .toList()
        .sortedByDescending { it.second }

    val colors = listOf(
        Color(0xFF4CAF50),
        Color(0xFF2196F3),
        Color(0xFFFF9800),
        Color(0xFFE91E63),
        Color(0xFF9C27B0),
        Color(0xFF00BCD4)
    )

    Column {

        grouped.forEachIndexed { index, item ->

            val (category, value) = item

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Row {

                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .background(colors[index % colors.size])
                    )

                    Spacer(Modifier.width(8.dp))

                    Text(
                        text = category,
                        color = Color.White
                    )
                }

                Text(
                    text = "€${"%.2f".format(value)}",
                    color = Color.LightGray
                )
            }
        }
    }
}