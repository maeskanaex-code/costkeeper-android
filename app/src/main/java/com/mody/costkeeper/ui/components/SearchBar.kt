package com.mody.costkeeper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.mody.costkeeper.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchBar(
    query: String,
    onQueryChange: (String) -> Unit,
    isDarkTheme: Boolean = true,
    modifier: Modifier = Modifier,
    placeholder: String = stringResource(R.string.search_hint)
) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        placeholder = {
            Text(
                text = placeholder,
                color = if (isDarkTheme) Color(0xFF94A3B8) else Color(0xFF64748B)
            )
        },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF60A5FA))
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChange("") }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear", tint = Color(0xFF94A3B8))
                }
            }
        },
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = if (isDarkTheme) Color.White else Color(0xFF1E293B),
            unfocusedTextColor = if (isDarkTheme) Color.White else Color(0xFF1E293B),
            focusedBorderColor = Color(0xFF60A5FA),
            unfocusedBorderColor = if (isDarkTheme) Color(0xFF334155) else Color(0xFFCBD5E1),
            cursorColor = Color(0xFF60A5FA)
        ),
        shape = RoundedCornerShape(12.dp),
        modifier = modifier.fillMaxWidth()
    )
}