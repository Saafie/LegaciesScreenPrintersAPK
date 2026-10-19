package com.example.legaciesscreenprintersapk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.legaciesscreenprintersapk.ui.auth.LoginScreen
import com.example.legaciesscreenprintersapk.ui.settings.SettingsScreen
import com.example.legaciesscreenprintersapk.ui.theme.LegaciesScreenPrintersAPKTheme

enum class Screen {
    LOGIN,
    SETTINGS
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LegaciesScreenPrintersAPKTheme {
                var currentScreen by remember { mutableStateOf(Screen.LOGIN) }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                ) {
                    when (currentScreen) {
                        Screen.LOGIN -> LoginScreen(
                            onLoginSuccess = { currentScreen = Screen.SETTINGS },
                            onSSOClicked = { currentScreen = Screen.SETTINGS },
                        )
                        Screen.SETTINGS -> SettingsScreen(
                            onLogoutClicked = { currentScreen = Screen.LOGIN },
                        )
                    }
                }
            }
        }
    }
}