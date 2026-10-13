package com.mody.costkeeper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mody.costkeeper.ui.theme.*
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

    val surfaceBg = if (isDarkTheme) DarkSurfaceElevated else LightSurfaceElevated
    val textPrimary = if (isDarkTheme) TextPrimary else LightTextPrimary
    val textMuted = if (isDarkTheme) TextMuted else LightTextMuted

    DatePickerDialog(
        onDismissRequest = onDismiss,
        colors = DatePickerDefaults.colors(
            containerColor = surfaceBg,
            titleContentColor = textPrimary,
            headlineContentColor = textPrimary,
            weekdayContentColor = textMuted,
            subheadContentColor = textMuted,
            yearContentColor = textPrimary,
            currentYearContentColor = PrimaryBlue,
            selectedYearContentColor = TextOnAccent,
            selectedYearContainerColor = PrimaryBlue,
            dayContentColor = textPrimary,
            selectedDayContentColor = TextOnAccent,
            selectedDayContainerColor = PrimaryBlue,
            todayContentColor = PrimaryBlue,
            todayDateBorderColor = PrimaryBlue,
            dividerColor = if (isDarkTheme) DividerDark else LightDivider
        ),
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(selectedDateMillis)
                    onDismiss()
                }
            ) {
                Text(
                    text = "OK",
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Cancel",
                    color = textMuted
                )
            }
        }
    ) {
        DatePicker(state = datePickerState)
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

    val bg = if (isDarkTheme) DarkSurface else LightSurface
    val border = if (isDarkTheme) BorderDark else LightBorder
    val textPrimary = if (isDarkTheme) TextPrimary else LightTextPrimary
    val textMuted = if (isDarkTheme) TextMuted else LightTextMuted

    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(bg)
            .border(1.dp, border, RoundedCornerShape(14.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(PrimaryBlueContainer.copy(alpha = 0.5f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = null,
                tint = PrimaryBlue,
                modifier = Modifier.size(20.dp)
            )
        }

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label.uppercase(),
                fontSize = 11.sp,
                color = textMuted,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 0.8.sp
            )
            Text(
                text = formattedDate,
                color = textPrimary,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
