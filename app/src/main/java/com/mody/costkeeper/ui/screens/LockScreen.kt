package com.mody.costkeeper.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.fragment.app.FragmentActivity
import com.mody.costkeeper.R
import com.mody.costkeeper.ui.theme.*
import com.mody.costkeeper.util.BiometricHelper
import com.mody.costkeeper.util.SecurityManager
import kotlinx.coroutines.delay

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

    var showHintInput by remember { mutableStateOf(false) }
    var hintText by remember { mutableStateOf("") }
    var revealedHint by remember { mutableStateOf<String?>(null) }

    val biometricAvailable = remember { BiometricHelper.canUseBiometric(context) }
    val biometricEnabled = remember { SecurityManager.isBiometricEnabled(context) }
    val showBiometricKey = !isSetupMode && biometricAvailable && biometricEnabled

    val titleEnter = stringResource(R.string.lock_title_enter)
    val subtitleEnter = stringResource(R.string.lock_subtitle_enter)
    val titleCreate = stringResource(R.string.lock_title_create)
    val titleConfirm = stringResource(R.string.lock_title_confirm)
    val subtitleCreate = stringResource(R.string.lock_subtitle_create)
    val subtitleConfirm = stringResource(R.string.lock_subtitle_confirm)
    val titleHint = stringResource(R.string.lock_title_hint)
    val subtitleHint = stringResource(R.string.lock_subtitle_hint)
    val hintPlaceholder = stringResource(R.string.lock_hint_placeholder)
    val btnSkip = stringResource(R.string.lock_skip)
    val btnSave = stringResource(R.string.lock_save)
    val errMismatch = stringResource(R.string.lock_error_mismatch)
    val errIncorrect = stringResource(R.string.lock_error_incorrect)
    val errBiometricUnavailable = stringResource(R.string.lock_biometric_unavailable)
    val btnShowHint = stringResource(R.string.lock_show_hint)
    val btnHideHint = stringResource(R.string.lock_hide_hint)
    val biometricDesc = stringResource(R.string.lock_biometric_content_desc)
    val backspaceDesc = stringResource(R.string.lock_backspace_desc)

    val background = if (isDarkTheme) DarkBackground else LightBackground
    val textPrimary = if (isDarkTheme) TextPrimary else LightTextPrimary
    val textMuted = if (isDarkTheme) TextMuted else LightTextMuted

    LaunchedEffect(Unit) {
        if (!isSetupMode && biometricEnabled && biometricAvailable) {
            delay(300)
            val activity = context as? FragmentActivity
            if (activity != null) {
                BiometricHelper.promptBiometric(
                    activity = activity,
                    onSuccess = { onSuccess() },
                    onError = { }
                )
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(background)
            .windowInsetsPadding(WindowInsets.systemBars)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(PrimaryBlueContainer.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (isSetupMode) "🔐" else "🔒",
                    fontSize = 36.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = when {
                    showHintInput -> titleHint
                    isSetupMode && !isConfirming -> titleCreate
                    isSetupMode && isConfirming -> titleConfirm
                    else -> titleEnter
                },
                color = textPrimary,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = when {
                    showHintInput -> subtitleHint
                    isSetupMode && !isConfirming -> subtitleCreate
                    isSetupMode && isConfirming -> subtitleConfirm
                    else -> subtitleEnter
                },
                color = textMuted,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))

            if (showHintInput) {
                OutlinedTextField(
                    value = hintText,
                    onValueChange = { if (it.length <= 40) hintText = it },
                    placeholder = {
                        Text(
                            hintPlaceholder,
                            color = textMuted
                        )
                    },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = textPrimary,
                        unfocusedTextColor = textPrimary,
                        focusedBorderColor = PrimaryBlue,
                        unfocusedBorderColor = if (isDarkTheme) BorderDark else LightBorder,
                        cursorColor = PrimaryBlue
                    ),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            SecurityManager.setPinHint(context, "")
                            SecurityManager.setPin(context, pin)
                            SecurityManager.setLockEnabled(context, true)
                            onSuccess()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        shape = RoundedCornerShape(14.dp),
                        border = BorderStroke(1.dp, if (isDarkTheme) BorderDark else LightBorder)
                    ) {
                        Text(
                            btnSkip,
                            color = textPrimary,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Button(
                        onClick = {
                            SecurityManager.setPinHint(context, hintText.trim())
                            SecurityManager.setPin(context, pin)
                            SecurityManager.setLockEnabled(context, true)
                            onSuccess()
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(52.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = PrimaryBlue),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(
                            btnSave,
                            color = TextOnAccent,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                val currentLength = if (isConfirming) confirmPin.length else pin.length

                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    repeat(4) { index ->
                        val filled = index < currentLength
                        Box(
                            modifier = Modifier
                                .size(16.dp)
                                .clip(CircleShape)
                                .background(
                                    if (filled) PrimaryBlue
                                    else if (isDarkTheme) DarkSurfaceHighest
                                    else LightSurfaceHighest
                                )
                                .then(
                                    if (errorMessage != null) {
                                        Modifier.border(2.dp, ExpenseRed, CircleShape)
                                    } else Modifier
                                )
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                Box(modifier = Modifier.height(22.dp)) {
                    if (errorMessage != null) {
                        Text(
                            text = errorMessage!!,
                            color = ExpenseRed,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(Modifier.height(16.dp))

                NumberPad(
                    isDarkTheme = isDarkTheme,
                    showBiometricKey = showBiometricKey,
                    biometricDesc = biometricDesc,
                    backspaceDesc = backspaceDesc,
                    onBiometricClick = {
                        val activity = context as? FragmentActivity
                        if (activity != null) {
                            BiometricHelper.promptBiometric(
                                activity = activity,
                                onSuccess = { onSuccess() },
                                onError = { err ->
                                    if (err.contains("No biometric", true) ||
                                        err.contains("not available", true)) {
                                        errorMessage = errBiometricUnavailable
                                    }
                                }
                            )
                        }
                    },
                    onDigit = { digit ->
                        errorMessage = null
                        if (isConfirming) {
                            if (confirmPin.length < 4) {
                                confirmPin += digit
                                if (confirmPin.length == 4) {
                                    if (confirmPin == pin) {
                                        if (isSetupMode) {
                                            showHintInput = true
                                        } else {
                                            onSuccess()
                                        }
                                    } else {
                                        errorMessage = errMismatch
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
                                        isConfirming = true
                                    } else {
                                        if (SecurityManager.verifyPin(context, pin)) {
                                            onSuccess()
                                        } else {
                                            errorMessage = errIncorrect
                                            pin = ""
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

                if (!isSetupMode) {
                    val hint = SecurityManager.getPinHint(context)
                    if (!hint.isNullOrBlank()) {
                        Spacer(Modifier.height(16.dp))

                        TextButton(onClick = {
                            revealedHint = if (revealedHint == null) hint else null
                        }) {
                            Text(
                                text = if (revealedHint == null) btnShowHint else btnHideHint,
                                color = PrimaryBlue,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        revealedHint?.let {
                            Spacer(Modifier.height(4.dp))
                            Surface(
                                color = PrimaryBlue.copy(alpha = 0.1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = it,
                                    color = PrimaryBlue,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NumberPad(
    isDarkTheme: Boolean,
    showBiometricKey: Boolean,
    biometricDesc: String,
    backspaceDesc: String,
    onBiometricClick: () -> Unit,
    onDigit: (String) -> Unit,
    onBackspace: () -> Unit
) {
    val keyBg = if (isDarkTheme) DarkSurface else LightSurface
    val keyText = if (isDarkTheme) TextPrimary else LightTextPrimary

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            NumberKey("1", keyBg, keyText, onDigit)
            NumberKey("2", keyBg, keyText, onDigit)
            NumberKey("3", keyBg, keyText, onDigit)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            NumberKey("4", keyBg, keyText, onDigit)
            NumberKey("5", keyBg, keyText, onDigit)
            NumberKey("6", keyBg, keyText, onDigit)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            NumberKey("7", keyBg, keyText, onDigit)
            NumberKey("8", keyBg, keyText, onDigit)
            NumberKey("9", keyBg, keyText, onDigit)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            if (showBiometricKey) {
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(PrimaryBlue.copy(alpha = 0.12f))
                        .clickable { onBiometricClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Fingerprint,
                        contentDescription = biometricDesc,
                        tint = PrimaryBlue,
                        modifier = Modifier.size(26.dp)
                    )
                }
            } else {
                Spacer(Modifier.size(60.dp))
            }

            NumberKey("0", keyBg, keyText, onDigit)

            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .clickable { onBackspace() },
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.AutoMirrored.Filled.Backspace,
                    contentDescription = backspaceDesc,
                    tint = keyText,
                    modifier = Modifier.size(22.dp)
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
            .size(60.dp)
            .clip(CircleShape)
            .background(bgColor)
            .clickable { onDigit(digit) },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = digit,
            color = textColor,
            fontSize = 24.sp,
            fontWeight = FontWeight.SemiBold
        )
    }
}
