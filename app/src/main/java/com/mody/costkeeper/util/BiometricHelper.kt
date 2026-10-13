package com.mody.costkeeper.util

import android.content.Context
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

/**
 * Wrapper around BiometricPrompt for fingerprint / face unlock.
 * Habivance-style: single entry point, returns via callbacks.
 */
object BiometricHelper {

    /**
     * Returns true if the device supports biometric authentication
     * (fingerprint or face) AND the user has at least one enrolled.
     */
    fun canUseBiometric(context: Context): Boolean {
        val manager = BiometricManager.from(context)
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.BIOMETRIC_WEAK
        return when (manager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }

    /**
     * Show the biometric prompt.
     * @param activity FragmentActivity (ComponentActivity subclasses are FragmentActivity)
     * @param title text shown at top of prompt
     * @param subtitle optional subtitle
     * @param onSuccess called when biometric succeeds
     * @param onError called with error message if biometric fails
     */
    fun promptBiometric(
        activity: FragmentActivity,
        title: String = "Unlock CostKeeper",
        subtitle: String = "Use your fingerprint or face to unlock",
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        val executor = ContextCompat.getMainExecutor(activity)
        val authenticators = BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.BIOMETRIC_WEAK

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setSubtitle(subtitle)
            .setNegativeButtonText("Use PIN")
            .setAllowedAuthenticators(authenticators)
            .build()

        val prompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    onSuccess()
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    onError(errString.toString())
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    // User's biometric didn't match — leave prompt open
                }
            }
        )

        prompt.authenticate(promptInfo)
    }
}