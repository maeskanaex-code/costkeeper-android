package com.mody.costkeeper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mody.costkeeper.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryManagerDialog(
    customCategories: List<String>,
    onAddCategory: (String) -> Unit,
    onDeleteCategory: (String) -> Unit,
    onDismiss: () -> Unit,
    isDarkTheme: Boolean = true
) {
    var newCategoryName by remember { mutableStateOf("") }
    var showError by remember { mutableStateOf(false) }

    val surfaceBg = if (isDarkTheme) DarkSurfaceElevated else LightSurfaceElevated
    val textPrimary = if (isDarkTheme) TextPrimary else LightTextPrimary
    val textMuted = if (isDarkTheme) TextMuted else LightTextMuted
    val inputBorder = if (isDarkTheme) BorderDark else LightBorder

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = surfaceBg,
        shape = RoundedCornerShape(24.dp),
        title = {
            Column {
                Text(
                    text = "Categories",
                    color = textPrimary,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Manage your custom categories",
                    color = textMuted,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 480.dp)
            ) {
                // Add row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = newCategoryName,
                        onValueChange = {
                            newCategoryName = it
                            showError = false
                        },
                        placeholder = {
                            Text(
                                "New category",
                                color = textMuted,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        },
                        singleLine = true,
                        isError = showError,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = textPrimary,
                            unfocusedTextColor = textPrimary,
                            focusedBorderColor = PrimaryBlue,
                            unfocusedBorderColor = inputBorder,
                            cursorColor = PrimaryBlue
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.weight(1f)
                    )

                    IconButton(
                        onClick = {
                            if (newCategoryName.isBlank()) {
                                showError = true
                                return@IconButton
                            }
                            onAddCategory(newCategoryName.trim())
                            newCategoryName = ""
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(PrimaryBlue)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add",
                            tint = TextOnAccent
                        )
                    }
                }

                if (showError) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Please enter a category name",
                        color = ExpenseRed,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Text(
                    text = "YOUR CATEGORIES",
                    color = textMuted,
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(10.dp))

                if (customCategories.isEmpty()) {
                    Text(
                        text = "No custom categories yet. Add one above!",
                        color = textMuted,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(vertical = 12.dp)
                    )
                } else {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(customCategories) { category ->
                            CustomCategoryRow(
                                name = category,
                                onDelete = { onDeleteCategory(category) },
                                isDarkTheme = isDarkTheme
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(
                    text = "Done",
                    color = PrimaryBlue,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
}

@Composable
private fun CustomCategoryRow(
    name: String,
    onDelete: () -> Unit,
    isDarkTheme: Boolean
) {
    val rowBg = if (isDarkTheme) DarkSurface else LightSurface
    val border = if (isDarkTheme) BorderDark else LightBorder
    val textPrimary = if (isDarkTheme) TextPrimary else LightTextPrimary

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(rowBg)
            .border(1.dp, border, RoundedCornerShape(12.dp))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = name,
            color = textPrimary,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.weight(1f)
        )

        IconButton(
            onClick = onDelete,
            modifier = Modifier.size(28.dp)
        ) {
            Icon(
                Icons.Default.Close,
                contentDescription = "Delete",
                tint = ExpenseRed,
                modifier = Modifier.size(18.dp)
            )
        }
    }
}
