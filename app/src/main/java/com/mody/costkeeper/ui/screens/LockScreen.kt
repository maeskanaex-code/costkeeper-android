package com.mody.costkeeper.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.mody.costkeeper.ui.theme.*
import com.mody.costkeeper.util.BiometricHelper
import com.mody.costkeeper.util.SecurityManager
import kotlinx.coroutines.delay

/**
 * LockScreen — PIN entry UI with optional biometric shortcut.
 *
 * Two modes:
 *  - Setup mode (isSetupMode = true): user creates a new 4-digit PIN.
 *    After first entry, asks for confirmation. If match, saves and calls onSuccess.
 *  - Verify mode (isSetupMode = false): user enters PIN to unlock.
 *    If correct, calls onSuccess. If wrong 3 times, shows error.
 */
@Composable
fun LockScreen(
    isDarkTheme: Boolean,
    isSetupMode: Boolean,
    onSuccess: () -> Unit,
    onCancel: (() -> Unit)? = null,
    onForgotPin: (() -> Unit)? = null
) {
    val context = LocalContext.current

    var pin by remember { mutableStateOf("") }
    var confirmPin by remember { mutableStateOf("") }
    var isConfirming by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var attempts by remember { mutableIntStateOf(0) }
    var shakeError by remember { mutableStateOf(false) }

    val biometricAvailable = remember { BiometricHelper.canUseBiometric(context) }
    val biometricEnabled = remember { SecurityManager.isBiometricEnabled(context) }

    val background = if (isDarkTheme) DarkBackground else LightBackground
    val textPrimary = if (isDarkTheme) TextPrimary else LightTextPrimary
    val textMuted = if (isDarkTheme) TextMuted else LightTextMuted
    val surfaceColor = if (isDarkTheme) DarkSurface else LightSurface

    // Auto-prompt biometric on entry if available & enabled
    LaunchedEffect(Unit) {
        if (!isSetupMode && biometricEnabled && biometricAvailable) {
            delay(300)
            val activity = context as? FragmentActivity
            if (activity != null) {
                BiometricHelper.promptBiometric(
                    activity = activity,
                    onSuccess = { onSuccess() },
                    onError = { /* fall back to PIN, ignore */ }
                )
            }
        }
    }

    // Shake animation trigger
    LaunchedEffect(shakeError) {
        if (shakeError) {
            delay(400)
            shakeError = false
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp)
        ) {

            // ============ HEADER ============
            Box(
                modifier = Modifier
                    .size(88.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlueContainer.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isSetupMode) "🔐" else "🔒",
                    fontSize = 44.sp
                )
            }

            Spacer(Modifier.height(24.dp))

            Text(
                text = when {
                    isSetupMode && !isConfirming -> "Create your PIN"
                    isSetupMode && isConfirming -> "Confirm your PIN"
                    else -> "Enter your PIN"
                },
                color = textPrimary,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = when {
                    isSetupMode && !isConfirming -> "Choose a 4-digit code to lock the app"
                    isSetupMode && isConfirming -> "Enter the same PIN again"
                    else -> "Unlock CostKeeper to continue"
                },
                color = textMuted,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(40.dp))

            // ============ PIN DOTS ============
            val currentLength = if (isConfirming) confirmPin.length else pin.length

            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                repeat(4) { index ->
                    val filled = index < currentLength
                    Box(
                        modifier = Modifier
                            .size(18.dp)
                            .clip(CircleShape)
                            .background(
                                if (filled) PrimaryBlue
                                else if (isDarkTheme) DarkSurfaceHighest
                                else LightSurfaceHighest
                            )
                            .border(
                                width = if (errorMessage != null) 2.dp else 0.dp,
                                color = if (errorMessage != null) ExpenseRed else Color.Transparent,
                                shape = CircleShape
                            )
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Error message slot (reserved space so layout doesn't jump)
            Box(modifier = Modifier.height(24.dp)) {
                if (errorMessage != null) {
                    Text(
                        text = errorMessage!!,
                        color = ExpenseRed,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.height(24.dp))

            // ============ NUMBER PAD ============
            NumberPad(
                isDarkTheme = isDarkTheme,
                onDigit = { digit ->
                    errorMessage = null
                    if (isConfirming) {
                        if (confirmPin.length < 4) {
                            confirmPin += digit
                            if (confirmPin.length == 4) {
                                if (confirmPin == pin) {
                                    // Save the PIN
                                    SecurityManager.setPin(context, pin)
                                    SecurityManager.setLockEnabled(context, true)
                                    onSuccess()
                                } else {
                                    errorMessage = "PINs don't match. Try again."
                                    confirmPin = ""
                                    isConfirming = false
                                    pin = ""
                                }
                            }
                        }
                    } else {
                        if (pin.length < 4) {
                            pin += digit
                            if (pin.length == 4) {
                                if (isSetupMode) {
                                    // Move to confirmation
                                    isConfirming = true
                                } else {
                                    // Verify mode: check the PIN
                                    if (SecurityManager.verifyPin(context, pin)) {
                                        onSuccess()
                                    } else {
                                        attempts++
                                        errorMessage = "Incorrect PIN. ${3 - attempts} attempts left."
                                        pin = ""
                                                                                if (attempts >= 3) {
                                            errorMessage = "Too many attempts. Try again later."
                                            attempts = 0
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                onBackspace = {
                    errorMessage = null
                    if (isConfirming) {
                        if (confirmPin.isNotEmpty()) confirmPin = confirmPin.dropLast(1)
                    } else {
                        if (pin.isNotEmpty()) pin = pin.dropLast(1)
                    }
                }
            )

            Spacer(Modifier.height(24.dp))

            // ============ BIOMETRIC BUTTON ============
            if (!isSetupMode && biometricAvailable && biometricEnabled) {
                TextButton(
                    onClick = {
                        val activity = context as? FragmentActivity
                        if (activity != null) {
                            BiometricHelper.promptBiometric(
                                activity = activity,
                                onSuccess = { onSuccess() },
                                onError = { err ->
                                    errorMessage = err
                                }
                            )
                        }
                    }
                ) {
                    Icon(
                        Icons.Default.Fingerprint,
                        contentDescription = null,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Use Biometric",
                        color = PrimaryBlue,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // ============ CANCEL / FORGOT ============
            if (!isSetupMode) {
                Spacer(Modifier.height(16.dp))

                if (onForgotPin != null) {
                    TextButton(onClick = onForgotPin) {
                        Text(
                            "Forgot PIN?",
                            color = textMuted,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                if (onCancel != null) {
                    TextButton(onClick = onCancel) {
                        Text(
                            "Exit",
                            color = textMuted,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }
            }
        }
    }
}

/* ================= NUMBER PAD ================= */

@Composable
private fun NumberPad(
    isDarkTheme: Boolean,
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit
) {
    val keyBg = if (isDarkTheme) DarkSurface else LightSurface
    val keyText = if (isDarkTheme) TextPrimary else LightTextPrimary

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Row 1: 1 2 3
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            NumberKey("1", keyBg, keyText, onDigit)
            NumberKey("2", keyBg, keyText, onDigit)
            NumberKey("3", keyBg, keyText, onDigit)
        }

        // Row 2: 4 5 6
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            NumberKey("4", keyBg, keyText, onDigit)
            NumberKey("5", keyBg, keyText, onDigit)
            NumberKey("6", keyBg, keyText, onDigit)
        }

        // Row 3: 7 8 9
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            NumberKey("7", keyBg, keyText, onDigit)
            NumberKey("8", keyBg, keyText, onDigit)
            NumberKey("9", keyBg, keyText, onDigit)
        }

        // Row 4: empty, 0, backspace
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Spacer(Modifier.size(72.dp))
            NumberKey("0", keyBg, keyText, onDigit)
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .clickable { onBackspace() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.Backspace,
                    contentDescription = "Delete",
                    tint = keyText,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
private fun NumberKey(
    digit: String,
    bgColor: Color,
    textColor: Color,
    onDigit: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .size(72.dp)
            .clip(CircleShape)
            .background(bgColor)
            .clickable { onDigit(digit) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = digit,
            color = textColor,
            fontSize = 28.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}