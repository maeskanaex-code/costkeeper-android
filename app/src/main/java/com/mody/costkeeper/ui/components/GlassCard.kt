package com.mody.costkeeper.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.unit.dp
import com.mody.costkeeper.ui.theme.*

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier
            .shadow(
                elevation = 10.dp,
                shape = RoundedCornerShape(28.dp),
                ambientColor = AccentBlue.copy(alpha = 0.1f),
                spotColor = AccentBlue.copy(alpha = 0.1f)
            )
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        GlassSurface.copy(alpha = 0.85f),
                        GlassSurface.copy(alpha = 0.65f)
                    )
                ),
                shape = RoundedCornerShape(28.dp)
            )
            .border(
                1.dp,
                GlassStroke,
                RoundedCornerShape(28.dp)
            )
            .padding(20.dp),
        content = content
    )
}