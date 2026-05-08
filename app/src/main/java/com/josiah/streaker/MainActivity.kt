package com.josiah.streaker

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.josiah.streaker.ui.screens.AppNavigation
import com.josiah.streaker.ui.theme.StreakerTheme
import com.josiah.streaker.viewmodel.HabitViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: HabitViewModel by viewModels()

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            NotificationScheduler.scheduleDailyReminder(this, true)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        viewModel.checkAndResetDaily()

        // Create notification channel
        StreakerNotificationService.createNotificationChannel(this)

        // Request notification permission on Android 13+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this, Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            } else {
                NotificationScheduler.scheduleDailyReminder(this, true)
            }
        } else {
            NotificationScheduler.scheduleDailyReminder(this, true)
        }

        setContent {
            val isDarkTheme by viewModel.isDarkTheme.collectAsStateWithLifecycle()
            StreakerTheme(isDarkTheme = isDarkTheme) {
                AppNavigation(viewModel = viewModel)
            }
        }
    }
}