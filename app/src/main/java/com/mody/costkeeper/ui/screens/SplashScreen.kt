package com.mody.costkeeper.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mody.costkeeper.R
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(onSplashFinished: () -> Unit) {

    // Trigger navigation after animation
    LaunchedEffect(Unit) {
        delay(2500)
        onSplashFinished()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1E293B),
                        Color(0xFF0F172A)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Logo Image (Your custom image)
            LogoImage()

            Spacer(Modifier.height(32.dp))

            // App Name
            Text(
                text = "CostKeeper",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )

            Spacer(Modifier.height(8.dp))

            // Tagline
            Text(
                text = "Track smart. Spend smarter.",
                color = Color(0xFF94A3B8),
                fontSize = 14.sp,
                letterSpacing = 1.sp
            )

            Spacer(Modifier.height(48.dp))

            // Animated Dots
            AnimatedDots()
        }
    }
}

@Composable
fun LogoImage() {
    val scale by rememberInfiniteTransition().animateFloat(
        initialValue = 1f,
        targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        )
    )

    // Your custom image
    Image(
        painter = painterResource(id = R.drawable.ic_splash_logo), // Your image name
        contentDescription = "App Logo",
        modifier = Modifier
            .size(120.dp)
            .scale(scale)
            .clip(RoundedCornerShape(28.dp)),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun AnimatedDots() {
    Row(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            val alpha by rememberInfiniteTransition().animateFloat(
                initialValue = 0.3f,
                targetValue = 1f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis = index * 200),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Box(
                modifier = Modifier
                    .size(10.dp)
                    .alpha(alpha)
                    .clip(CircleShape)
                    .background(Color(0xFF60A5FA))
            )
        }
    }
}