package com.josiah.streaker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.josiah.streaker.ui.screens.AppNavigation
import com.josiah.streaker.ui.theme.StreakerTheme
import com.josiah.streaker.viewmodel.HabitViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: HabitViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        viewModel.checkAndResetDaily()
        setContent {
            StreakerTheme {
                AppNavigation()
            }
        }
    }
}