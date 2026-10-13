package com.mody.costkeeper.ui.screens

import android.content.Context
import android.os.Build
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import com.mody.costkeeper.ui.theme.*

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
                        DarkBackground,
                        PrimaryBlueDark,
                        DarkBackground
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
            // Shield icon
            Box(
                modifier = Modifier
                    .size(96.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(
                        brush = Brush.linearGradient(
                            colors = listOf(
                                PrimaryBlue,
                                PrimaryBlueDark
                            )
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "🛡️",
                    fontSize = 44.sp
                )
            }

            Spacer(Modifier.height(28.dp))

            Text(
                text = getTitleText(currentLanguage),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                color = TextPrimary
            )

            Spacer(Modifier.height(20.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                shape = RoundedCornerShape(20.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        text = getWelcomeText(currentLanguage),
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.titleMedium
                    )

                    Spacer(Modifier.height(12.dp))

                    Text(
                        text = getPrivacyMessage(currentLanguage),
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        lineHeight = 20.sp
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onAgree,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(54.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = PrimaryBlue
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = getAgreeButtonText(currentLanguage),
                    color = TextOnAccent,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = onPrivacyPolicy,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(vertical = 4.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = PrimaryBlue
                ),
                border = BorderStroke(
                    1.dp,
                    PrimaryBlue.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = getPrivacyPolicyText(currentLanguage),
                    color = PrimaryBlue,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }

            Spacer(Modifier.height(16.dp))

            TextButton(onClick = onExit) {
                Text(
                    text = getExitButtonText(currentLanguage),
                    color = TextMuted,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(Modifier.height(8.dp))
        }
    }
}

// Function to detect current system language
fun getCurrentLanguage(context: Context): String {
    val locale = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        context.resources.configuration.locales.get(0)
    } else {
        context.resources.configuration.locale
    }

    val language = locale.language
    return when {
        language.startsWith("ru") -> "ru"
        language.startsWith("zh") -> "zh"
        else -> "en"
    }
}

fun getTitleText(language: String): String {
    return when (language) {
        "ru" -> "Политика конфиденциальности"
        "zh" -> "隐私政策"
        else -> "Privacy Policy"
    }
}

fun getWelcomeText(language: String): String {
    return when (language) {
        "ru" -> "Добро пожаловать в CostKeeper! 👋"
        "zh" -> "欢迎使用 CostKeeper！ 👋"
        else -> "Welcome to CostKeeper! 👋"
    }
}

fun getPrivacyMessage(language: String): String {
    return when (language) {
        "ru" -> "Ваша конфиденциальность важна. Все ваши финансовые данные остаются на вашем устройстве. Мы не собираем, не храним и не передаем личную информацию."
        "zh" -> "您的隐私很重要。所有财务数据仅保存在您的设备上。我们不会收集、存储或分享任何个人信息。"
        else -> "Your privacy matters. All your financial data stays on your device. We don't collect, store, or share any personal information."
    }
}

fun getAgreeButtonText(language: String): String {
    return when (language) {
        "ru" -> "Согласен и продолжить ✓"
        "zh" -> "同意并继续 ✓"
        else -> "Agree & Continue ✓"
    }
}

fun getPrivacyPolicyText(language: String): String {
    return when (language) {
        "ru" -> "📄 Политика конфиденциальности"
        "zh" -> "📄 隐私政策"
        else -> "📄 Privacy Policy"
    }
}

fun getExitButtonText(language: String): String {
    return when (language) {
        "ru" -> "Не сейчас"
        "zh" -> "暂不使用"
        else -> "Not now"
    }
}
