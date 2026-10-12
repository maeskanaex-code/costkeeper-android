package com.mody.costkeeper.ui.screens

import android.content.Context
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*

@Composable
fun PrivacyDialog(
    onAgree: () -> Unit,
    onPrivacyPolicy: () -> Unit,
    onExit: () -> Unit
) {
    val scale by rememberInfiniteTransition().animateFloat(
        initialValue = 1f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOutCubic),
            repeatMode = RepeatMode.Reverse
        )
    )

    val context = LocalContext.current
    val currentLanguage = getCurrentLanguage(context)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F172A),
                        Color(0xFF1A2332),
                        Color(0xFF0F172A)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Shield Icon
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                Color(0xFF3B82F6),
                                Color(0xFF1E40AF)
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🛡️",
                    fontSize = 36.sp
                )
            }

            Spacer(Modifier.height(28.dp))

            // Title
            Text(
                text = getTitleText(currentLanguage),
                textAlign = TextAlign.Center,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                lineHeight = 32.sp
            )

            Spacer(Modifier.height(20.dp))

            // Description Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(Color(0xFF111827)),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color(0xFF60A5FA).copy(alpha = 0.2f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    // Welcome message
                    Text(
                        text = getWelcomeText(currentLanguage),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

                    Spacer(Modifier.height(12.dp))

                    // Privacy message
                    Text(
                        text = getPrivacyMessage(currentLanguage),
                        color = Color(0xFFCBD5E1),
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // Agree Button
            Button(
                onClick = onAgree,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B82F6)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = getAgreeButtonText(currentLanguage),
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(12.dp))

            // Privacy Policy Link
            // Privacy Policy Link - Improved to prevent cropping
            OutlinedButton(
                onClick = onPrivacyPolicy,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()  // Changed from fixed height (48.dp) to wrapContentHeight
                    .padding(vertical = 8.dp),  // Add some vertical padding
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF60A5FA)
                ),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    Color(0xFF60A5FA).copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(14.dp)
            ) {
                Text(
                    text = getPrivacyPolicyText(currentLanguage),
                    color = Color(0xFF60A5FA),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,  // Center the text
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)  // Add inner padding
                )
            }

            Spacer(Modifier.height(16.dp))

            // Exit Button
            TextButton(onClick = onExit) {
                Text(
                    text = getExitButtonText(currentLanguage),
                    color = Color(0xFF64748B),
                    fontSize = 13.sp
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

// Function to detect current system language
fun getCurrentLanguage(context: Context): String {
    val locale = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        context.resources.configuration.locales.get(0)
    } else {
        context.resources.configuration.locale
    }

    val language = locale.language
    return when {
        language.startsWith("ru") -> "ru"
        language.startsWith("zh") -> "zh"
        else -> "en" // Default to English for any other language
    }
}

// Title text
fun getTitleText(language: String): String {
    return when (language) {
        "ru" -> "Политика конфиденциальности"
        "zh" -> "隐私政策"
        else -> "Privacy Policy"
    }
}

// Welcome message
fun getWelcomeText(language: String): String {
    return when (language) {
        "ru" -> "Добро пожаловать в CostKeeper! 👋"
        "zh" -> "欢迎使用 CostKeeper！ 👋"
        else -> "Welcome to CostKeeper! 👋"
    }
}

// Privacy message
fun getPrivacyMessage(language: String): String {
    return when (language) {
        "ru" -> "Ваша конфиденциальность важна. Все ваши финансовые данные остаются на вашем устройстве. Мы не собираем, не храним и не передаем личную информацию."
        "zh" -> "您的隐私很重要。所有财务数据仅保存在您的设备上。我们不会收集、存储或分享任何个人信息。"
        else -> "Your privacy matters. All your financial data stays on your device. We don't collect, store, or share any personal information."
    }
}

// Agree button text
fun getAgreeButtonText(language: String): String {
    return when (language) {
        "ru" -> "Согласен и продолжить ✓"
        "zh" -> "同意并继续 ✓"
        else -> "Agree & Continue ✓"
    }
}

// Privacy policy button text - SHORTENED for Russian
fun getPrivacyPolicyText(language: String): String {
    return when (language) {
        "ru" -> "📄 Политика конфиденциальности"  // Changed from "Прочитать политику конфиденциальности"
        "zh" -> "📄 隐私政策"
        else -> "📄 Privacy Policy"
    }
}

// Exit button text
fun getExitButtonText(language: String): String {
    return when (language) {
        "ru" -> "Не сейчас"
        "zh" -> "暂不使用"
        else -> "Not now"
    }
}