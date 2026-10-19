package com.example.legaciesscreenprintersapk

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.legaciesscreenprintersapk.ui.auth.LoginScreen
import com.example.legaciesscreenprintersapk.ui.auth.RegisterScreen
import com.example.legaciesscreenprintersapk.ui.main.MainScreen
import com.example.legaciesscreenprintersapk.ui.theme.LegaciesScreenPrintersAPKTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            var isDarkMode by remember { mutableStateOf(value = true) }
            LegaciesScreenPrintersAPKTheme(darkTheme = isDarkMode, dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    LegaciesAppNavigation(
                        isDarkMode = isDarkMode,
                    ) { isDarkMode = it }
                }
            }
        }
    }
}

@Composable
fun LegaciesAppNavigation(
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login",
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate("main") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                },
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate("main") {
                        popUpTo("register") { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                },
            )
        }

        composable("main") {
            MainScreen(
                isDarkMode = isDarkMode,
                onDarkModeChange = onDarkModeChange,
            ) {
                navController.navigate("login") {
                    popUpTo("main") { inclusive = true }
                }
            }
        }
    }
}
