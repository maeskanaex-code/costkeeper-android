package com.mody.costkeeper.utils

import android.content.Context
import java.util.Currency as JavaCurrency
import java.text.NumberFormat
import java.util.*

enum class AppCurrency(
    val symbol: String,
    val code: String,
    val currencyName: String,
    val flag: String
) {
    USD("$", "USD", "US Dollar", "🇺🇸"),
    EUR("€", "EUR", "Euro", "🇪🇺"),
    GBP("£", "GBP", "British Pound", "🇬🇧"),
    JPY("¥", "JPY", "Japanese Yen", "🇯🇵"),
    CAD("$", "CAD", "Canadian Dollar", "🇨🇦"),
    AUD("$", "AUD", "Australian Dollar", "🇦🇺"),
    CHF("Fr", "CHF", "Swiss Franc", "🇨🇭"),
    CNY("¥", "CNY", "Chinese Yuan", "🇨🇳"),
    INR("₹", "INR", "Indian Rupee", "🇮🇳"),
    RUB("₽", "RUB", "Russian Ruble", "🇷🇺")
}

object CurrencyManager {

    private const val PREFS_NAME = "costkeeper_prefs"
    private const val KEY_CURRENCY_CODE = "selected_currency_code"

    fun getSelectedCurrency(context: Context): AppCurrency {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val currencyCode = prefs.getString(KEY_CURRENCY_CODE, "EUR")
        return AppCurrency.values().find { it.code == currencyCode } ?: AppCurrency.EUR
    }

    fun saveSelectedCurrency(context: Context, currency: AppCurrency) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_CURRENCY_CODE, currency.code).apply()
    }

    fun formatAmount(amount: Double, currency: AppCurrency): String {
        try {
            val javaCurrency = JavaCurrency.getInstance(currency.code)
            val formatter = NumberFormat.getCurrencyInstance(Locale.US)
            formatter.currency = javaCurrency
            return formatter.format(amount)
        } catch (e: Exception) {
            return "${currency.symbol}${"%.2f".format(amount)}"
        }
    }

    fun formatAmountSimple(amount: Double, currency: AppCurrency): String {
        return "${currency.symbol}${"%.2f".format(kotlin.math.abs(amount))}"
    }
}