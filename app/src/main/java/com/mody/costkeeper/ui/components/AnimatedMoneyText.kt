package com.mody.costkeeper.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.runtime.*
import kotlinx.coroutines.launch

@Composable
fun AnimatedMoneyText(
    targetValue: Double
): String {

    val anim = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(targetValue) {
        scope.launch {
            anim.snapTo(0f)
            anim.animateTo(
                targetValue.toFloat(),
                animationSpec = tween(durationMillis = 900)
            )
        }
    }

    return "€%.2f".format(anim.value)
}