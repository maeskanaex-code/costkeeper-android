package com.mody.costkeeper.util

import android.content.Context
import android.content.SharedPreferences
import java.security.MessageDigest
import java.security.SecureRandom

/**
 * Manages PIN setup, verification, hint and lock settings via SharedPreferences.
 * PIN is stored as SHA-256 hash with a random salt — never in plaintext.
 *
 * Storage keys in "costkeeper_security" prefs:
 *  - pin_hash   : hex SHA-256 of (salt + pin)
 *  - pin_salt   : hex random salt (16 bytes)
 *  - pin_hint   : optional hint the user writes when setting up the PIN
 *  - lock_enabled : whether the lock is active
 *  - biometric_enabled : whether biometric prompt is offered before PIN
 *  - auto_lock_minutes : minutes of inactivity before re-lock (0 = never, default 5)
 */
object SecurityManager {

    private const val PREFS = "costkeeper_security"
    private const val KEY_PIN_HASH = "pin_hash"
    private const val KEY_PIN_SALT = "pin_salt"
    private const val KEY_PIN_HINT = "pin_hint"
    private const val KEY_LOCK_ENABLED = "lock_enabled"
    private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
    private const val KEY_AUTO_LOCK_MINUTES = "auto_lock_minutes"

    private const val DEFAULT_AUTO_LOCK_MINUTES = 5

    private fun prefs(context: Context): SharedPreferences =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)

    // ============ PIN setup / verify ============

    fun hasPin(context: Context): Boolean {
        return prefs(context).getString(KEY_PIN_HASH, null) != null
    }

    fun setPin(context: Context, pin: String) {
        val salt = generateSalt()
        val hash = hashPin(pin, salt)
        prefs(context).edit()
            .putString(KEY_PIN_SALT, salt)
            .putString(KEY_PIN_HASH, hash)
            .apply()
    }

    fun verifyPin(context: Context, pin: String): Boolean {
        val p = prefs(context)
        val storedHash = p.getString(KEY_PIN_HASH, null) ?: return false
        val storedSalt = p.getString(KEY_PIN_SALT, null) ?: return false
        val attemptHash = hashPin(pin, storedSalt)
        return attemptHash == storedHash
    }

    fun clearPin(context: Context) {
        prefs(context).edit()
            .remove(KEY_PIN_HASH)
            .remove(KEY_PIN_SALT)
            .remove(KEY_PIN_HINT)
            .putBoolean(KEY_LOCK_ENABLED, false)
            .putBoolean(KEY_BIOMETRIC_ENABLED, false)
            .apply()
    }

    // ============ PIN hint ============

    fun setPinHint(context: Context, hint: String) {
        prefs(context).edit().putString(KEY_PIN_HINT, hint).apply()
    }

    fun getPinHint(context: Context): String? {
        return prefs(context).getString(KEY_PIN_HINT, null)
    }

    // ============ Lock settings ============

    fun isLockEnabled(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_LOCK_ENABLED, false) && hasPin(context)
    }

    fun setLockEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_LOCK_ENABLED, enabled).apply()
    }

    fun isBiometricEnabled(context: Context): Boolean {
        return prefs(context).getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }

    fun setBiometricEnabled(context: Context, enabled: Boolean) {
        prefs(context).edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }

    fun getAutoLockMinutes(context: Context): Int {
        return prefs(context).getInt(KEY_AUTO_LOCK_MINUTES, DEFAULT_AUTO_LOCK_MINUTES)
    }

    fun setAutoLockMinutes(context: Context, minutes: Int) {
        prefs(context).edit().putInt(KEY_AUTO_LOCK_MINUTES, minutes).apply()
    }

    // ============ Hashing ============

    private fun generateSalt(): String {
        val random = SecureRandom()
        val bytes = ByteArray(16)
        random.nextBytes(bytes)
        return bytes.toHex()
    }

    private fun hashPin(pin: String, salt: String): String {
        val digest = MessageDigest.getInstance("SHA-256")
        val input = (salt + pin).toByteArray(Charsets.UTF_8)
        return digest.digest(input).toHex()
    }

    private fun ByteArray.toHex(): String {
        return joinToString("") { "%02x".format(it) }
    }
}