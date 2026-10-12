package com.mody.costkeeper.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomDatePickerDialog(
    initialDate: Long = System.currentTimeMillis(),
    onDateSelected: (Long) -> Unit,
    onDismiss: () -> Unit,
    isDarkTheme: Boolean = true
) {
    var selectedDateMillis by remember { mutableStateOf(initialDate) }

    val datePickerState = rememberDatePickerState(
        initialSelectedDateMillis = selectedDateMillis
    )

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(selectedDateMillis)
                    onDismiss()
                }
            ) {
                Text("OK", color = Color(0xFF60A5FA))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B))
            }
        }
    ) {
        DatePicker(
            state = datePickerState
        )
    }

    LaunchedEffect(datePickerState.selectedDateMillis) {
        datePickerState.selectedDateMillis?.let {
            selectedDateMillis = it
        }
    }
}

@Composable
fun DateSelectorButton(
    selectedDate: Long,
    onClick: () -> Unit,
    isDarkTheme: Boolean = true,
    modifier: Modifier = Modifier,
    label: String = "Date"
) {
    val dateFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(selectedDate))

    OutlinedButton(
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        colors = ButtonDefaults.outlinedButtonColors(
            contentColor = if (isDarkTheme) Color.White else Color(0xFF1E293B)
        )
    ) {
        Icon(
            imageVector = Icons.Default.DateRange,
            contentDescription = null,
            tint = Color(0xFF60A5FA)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = label,
                fontSize = 12.sp,
                color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
            )
            Text(
                text = formattedDate,
                color = if (isDarkTheme) Color.White else Color(0xFF1E293B),
                fontSize = 14.sp
            )
        }
    }
}