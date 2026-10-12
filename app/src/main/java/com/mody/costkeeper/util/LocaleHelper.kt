package com.mody.costkeeper.util

import android.content.Context
import android.content.res.Configuration
import java.util.Locale

object LocaleHelper {

    fun setLocale(context: Context, languageCode: String): Context {
        val locale = when (languageCode) {
            "RU" -> Locale("ru")
            else -> Locale("en")
        }

        Locale.setDefault(locale)

        val config = Configuration(context.resources.configuration)
        config.setLocale(locale)

        return context.createConfigurationContext(config)
    }

    fun applyLocale(context: Context, languageCode: String): Context {
        return setLocale(context, languageCode)
    }
}