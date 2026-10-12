package com.mody.costkeeper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

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

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = if (isDarkTheme) Color(0xFF1E293B) else Color.White,
        title = { Text("Manage Categories", color = if (isDarkTheme) Color.White else Color(0xFF1E293B)) },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 400.dp)
            ) {
                // Add new category
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
                        label = { Text("New Category") },
                        isError = showError,
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = if (isDarkTheme) Color.White else Color(0xFF1E293B),
                            unfocusedTextColor = if (isDarkTheme) Color.White else Color(0xFF1E293B)
                        )
                    )

                    IconButton(
                        onClick = {
                            if (newCategoryName.isBlank()) {
                                showError = true
                                return@IconButton
                            }
                            onAddCategory(newCategoryName)
                            newCategoryName = ""
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .background(Color(0xFF3B82F6), CircleShape)
                    ) {
                        Icon(Icons.Default.Add, "Add", tint = Color.White)
                    }
                }

                if (showError) {
                    Text(
                        "Please enter a category name",
                        color = Color(0xFFF87171),
                        fontSize = 12.sp,
                        modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                HorizontalDivider(color = if (isDarkTheme) Color(0xFF334155) else Color(0xFFE2E8F0))

                Spacer(modifier = Modifier.height(16.dp))

                // List of custom categories
                Text(
                    "Custom Categories",
                    color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(customCategories) { category ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    if (isDarkTheme) Color(0xFF1F2937) else Color(0xFFF1F5F9),
                                    RoundedCornerShape(8.dp)
                                )
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(category, color = if (isDarkTheme) Color.White else Color(0xFF1E293B))

                            IconButton(
                                onClick = { onDeleteCategory(category) }
                            ) {
                                Icon(
                                    Icons.Default.Delete,
                                    "Delete",
                                    tint = Color(0xFFF87171),
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    if (customCategories.isEmpty()) {
                        item {
                            Text(
                                "No custom categories yet. Add one above!",
                                color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B),
                                fontSize = 14.sp,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    "Default categories are always available",
                    color = if (isDarkTheme) Color(0xFF64748B) else Color(0xFF94A3B8),
                    fontSize = 11.sp
                )
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = Color(0xFF60A5FA))
            }
        }
    )
}