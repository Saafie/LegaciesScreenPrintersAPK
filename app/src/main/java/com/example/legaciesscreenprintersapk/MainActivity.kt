package com.example.legaciesscreenprintersapk

import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.content.edit
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.legaciesscreenprintersapk.ui.auth.AuthRepository
import com.example.legaciesscreenprintersapk.ui.auth.LoginScreen
import com.example.legaciesscreenprintersapk.ui.auth.RegisterScreen
import com.example.legaciesscreenprintersapk.ui.main.MainScreen
import com.example.legaciesscreenprintersapk.ui.remote.RetrofitClient
import com.example.legaciesscreenprintersapk.ui.theme.LegaciesScreenPrintersAPKTheme
import com.example.legaciesscreenprintersapk.util.SessionEvents
import com.example.legaciesscreenprintersapk.util.SessionManager
import kotlinx.coroutines.launch

/** The app's single activity. */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        // Lets every API request carry the signed-in user's token.
        RetrofitClient.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            // Dark mode is saved so it stays the same after the app restarts.
            val settings = remember { getSharedPreferences("LegaciesSettings", MODE_PRIVATE) }
            var isDarkMode by remember { mutableStateOf(settings.getBoolean("darkMode", true)) }
            LegaciesScreenPrintersAPKTheme(darkTheme = isDarkMode, dynamicColor = false) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background,
                ) {
                    LegaciesAppNavigation(
                        isDarkMode = isDarkMode,
                    ) {
                        isDarkMode = it
                        settings.edit { putBoolean("darkMode", it) }
                    }
                }
            }
        }
    }

    override fun onStart() { super.onStart(); Log.d(TAG, "onStart") }
    override fun onResume() { super.onResume(); Log.d(TAG, "onResume") }
    override fun onPause() { super.onPause(); Log.d(TAG, "onPause") }
    override fun onStop() { super.onStop(); Log.d(TAG, "onStop") }
    override fun onDestroy() { super.onDestroy(); Log.d(TAG, "onDestroy") }

    companion object {
        const val TAG = "LSP/Main"
    }
}

@Composable
fun LegaciesAppNavigation(
    isDarkMode: Boolean,
    onDarkModeChange: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val authRepository = remember { AuthRepository(context) }

    // isLoggedIn() also checks the token's expiry, so an expired session starts at login.
    val startDestination = remember { if (SessionManager.isLoggedIn(context)) "main" else "login" }
    var sessionMessage by remember { mutableStateOf<String?>(null) }

    /** Clears the back stack and shows the login screen, optionally with a reason. */
    fun goToLogin(message: String?) {
        sessionMessage = message
        navController.navigate("login") {
            popUpTo(0) { inclusive = true }
        }
    }

    // The API rejected the token, so go back to login.
    LaunchedEffect(Unit) {
        SessionEvents.expired.collect { reason ->
            Log.i(MainActivity.TAG, "Session ended by server; returning to login.")
            goToLogin(reason)
        }
    }

    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable("login") {
            LoginScreen(
                onLoginSuccess = {
                    Log.d(MainActivity.TAG, "Navigate: login -> main")
                    sessionMessage = null
                    navController.navigate("main") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate("register")
                },
                sessionMessage = sessionMessage,
            )
        }

        composable("register") {
            RegisterScreen(
                onRegisterSuccess = {
                    Log.d(MainActivity.TAG, "Navigate: register -> main")
                    navController.navigate("main") {
                        popUpTo(0) { inclusive = true }
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
                // Sign out: clear the stored session and Google's account choice.
                scope.launch {
                    authRepository.signOut()
                    goToLogin(null)
                }
            }
        }
    }
}
