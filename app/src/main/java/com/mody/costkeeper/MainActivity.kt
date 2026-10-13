package com.mody.costkeeper

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.LocaleList
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.ViewModelProvider
import com.mody.costkeeper.data.local.ExpenseDatabase
import com.mody.costkeeper.data.repository.ExpenseRepository
import com.mody.costkeeper.ui.screens.LockScreen
import com.mody.costkeeper.ui.screens.MainScreen
import com.mody.costkeeper.ui.screens.PrivacyDialog
import com.mody.costkeeper.ui.screens.SplashScreen
import com.mody.costkeeper.ui.theme.CostKeeperTheme
import com.mody.costkeeper.ui.viewmodel.ExpenseViewModel
import com.mody.costkeeper.ui.viewmodel.ExpenseViewModelFactory
import com.mody.costkeeper.util.SecurityManager
import java.util.*

class MainActivity : ComponentActivity() {

    private lateinit var viewModel: ExpenseViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)

        val db = ExpenseDatabase.getDatabase(applicationContext)
        val repository = ExpenseRepository(db.expenseDao())
        val factory = ExpenseViewModelFactory(applicationContext, repository)

        viewModel = ViewModelProvider(this, factory)[ExpenseViewModel::class.java]

        val prefs = getSharedPreferences("costkeeper_prefs", Context.MODE_PRIVATE)
        val isFirstLaunch = prefs.getBoolean("is_first_launch", true)
        val savedLanguage = prefs.getString("app_language", "system") ?: "system"

        if (savedLanguage != "system") {
            applyLanguage(savedLanguage)
        }

        val savedTheme = prefs.getBoolean("dark_theme", true)

        setContent {
            var isDarkTheme by remember { mutableStateOf(savedTheme) }

            LaunchedEffect(isDarkTheme) {
                prefs.edit().putBoolean("dark_theme", isDarkTheme).apply()
            }

            CostKeeperTheme(darkTheme = isDarkTheme) {
                var showSplash by remember { mutableStateOf(true) }
                var showPrivacy by remember { mutableStateOf(isFirstLaunch) }

                // Lock state - check once at startup
                var showLock by remember {
                    mutableStateOf(SecurityManager.isLockEnabled(applicationContext))
                }

                when {
                    // 1. Privacy dialog on first launch
                    showPrivacy -> {
                        PrivacyDialog(
                            onAgree = {
                                prefs.edit().putBoolean("is_first_launch", false).apply()
                                showPrivacy = false
                            },
                            onPrivacyPolicy = {
                                val privacyUrl = "https://raw.githubusercontent.com/maeskanaex-code/CostKeeper/refs/heads/main/Privacy%20Policy.txt"
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(privacyUrl))
                                startActivity(intent)
                            },
                            onExit = { finish() }
                        )
                    }

                    // 2. Splash animation
                    showSplash -> {
                        SplashScreen(onSplashFinished = { showSplash = false })
                    }

                    // 3. Lock screen (if PIN is set and lock is enabled)
                    showLock -> {
                        LockScreen(
                            isDarkTheme = isDarkTheme,
                            isSetupMode = false,
                            onSuccess = { showLock = false },
                            onCancel = { finish() },
                            onForgotPin = {
                                // For v2.0 we just exit — user would need to reinstall to reset
                                // (a "reset PIN by email" flow would be added in a future version)
                                finish()
                            }
                        )
                    }

                    // 4. Main app
                    else -> {
                        MainScreen(
                            viewModel = viewModel,
                            isDarkTheme = isDarkTheme,
                            onThemeChange = { isDarkTheme = it }
                        )
                    }
                }
            }
        }
    }

    private fun applyLanguage(languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration(resources.configuration)
        config.setLocale(locale)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocales(LocaleList(locale))
        }

        resources.updateConfiguration(config, resources.displayMetrics)
    }
}