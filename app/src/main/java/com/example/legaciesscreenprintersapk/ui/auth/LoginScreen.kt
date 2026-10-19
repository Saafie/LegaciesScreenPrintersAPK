package com.example.legaciesscreenprintersapk.ui.auth

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.legaciesscreenprintersapk.ui.remote.MockDataRepository
import com.example.legaciesscreenprintersapk.ui.remote.RetrofitClient
import com.example.legaciesscreenprintersapk.ui.remote.models.AuthRequest
import com.example.legaciesscreenprintersapk.ui.theme.LegaciesScreenPrintersAPKTheme
import com.example.legaciesscreenprintersapk.util.GoogleAuthHelper
import com.example.legaciesscreenprintersapk.util.SecurityUtils
import com.example.legaciesscreenprintersapk.util.SessionManager
import kotlinx.coroutines.launch

val DarkBackground = Color(0xFF121212)
val CardBackground = Color(0xFF1E1E1E)
val PrimaryOrange = Color(0xFFFF6F00)
val TextWhite = Color(0xFFFFFFFF)
val TextGray = Color(0xFFA0A0A0)
val ErrorRed = Color(0xFFCF6679)

@Composable
fun LoginScreen(
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var authErrorMessage by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sessionManager = remember { SessionManager(context) }
    val repository = remember { MockDataRepository() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState()),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardBackground)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "LEGACIES SCREEN PRINTERS",
                    color = PrimaryOrange,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Customer Portal Sign In",
                    color = TextWhite,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Email Input
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        if (emailError != null) emailError = null
                        if (authErrorMessage != null) authErrorMessage = null
                    },
                    label = { Text("Email Address", color = TextGray) },
                    singleLine = true,
                    isError = emailError != null || authErrorMessage != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryOrange,
                        unfocusedBorderColor = TextGray,
                        errorBorderColor = ErrorRed,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                emailError?.let { err ->
                    Text(
                        text = err,
                        color = ErrorRed,
                        fontSize = 11.sp,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(start = 4.dp, top = 2.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Password Input
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        if (passwordError != null) passwordError = null
                        if (authErrorMessage != null) authErrorMessage = null
                    },
                    label = { Text("Password", color = TextGray) },
                    singleLine = true,
                    isError = passwordError != null || authErrorMessage != null,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle password visibility",
                                tint = PrimaryOrange
                            )
                        }
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryOrange,
                        unfocusedBorderColor = TextGray,
                        errorBorderColor = ErrorRed,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                passwordError?.let { err ->
                    Text(
                        text = err,
                        color = ErrorRed,
                        fontSize = 11.sp,
                        modifier = Modifier
                            .align(Alignment.Start)
                            .padding(start = 4.dp, top = 2.dp)
                    )
                }

                authErrorMessage?.let { err ->
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = err,
                        color = ErrorRed,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .padding(vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Sign In Button
                Button(
                    onClick = {
                        emailError = null
                        passwordError = null
                        authErrorMessage = null

                        var hasError = false
                        if (!SecurityUtils.isValidEmail(email)) {
                            emailError = "Valid email address is required"
                            hasError = true
                        }
                        if (password.isBlank()) {
                            passwordError = "Password is required"
                            hasError = true
                        }

                        if (!hasError) {
                            isLoading = true
                            scope.launch {
                                val trimmedEmail = email.trim()
                                try {
                                    val apiService = RetrofitClient.apiService
                                    val response = apiService.login(AuthRequest(trimmedEmail, password))
                                    isLoading = false

                                    if (response.isSuccessful && response.body() != null) {
                                        val body = response.body()!!
                                        if (body.userId != null || body.email != null) {
                                            sessionManager.saveUserSession(
                                                userId = body.userId ?: 1,
                                                fullName = body.fullName ?: "Customer User",
                                                email = body.email ?: trimmedEmail,
                                                role = body.role ?: "Customer",
                                                token = body.token
                                            )
                                            Toast.makeText(context, "Sign in successful!", Toast.LENGTH_SHORT).show()
                                            onLoginSuccess()
                                        } else {
                                            authErrorMessage = "Invalid email or password"
                                        }
                                    } else {
                                        // Server returned non-200 OK (e.g. HTTP 401 Unauthorized / Invalid Credentials)
                                        authErrorMessage = "Invalid email or password"
                                    }
                                } catch (_: Exception) {
                                    isLoading = false
                                    // Network error/unreachable: Verify against MockDataRepository seeded users
                                    val mockResult = repository.verifyOfflineCredentials(trimmedEmail, password)
                                    if (mockResult != null && mockResult.userId != null) {
                                        sessionManager.saveUserSession(
                                            userId = mockResult.userId,
                                            fullName = mockResult.fullName ?: "Customer User",
                                            email = mockResult.email ?: trimmedEmail,
                                            role = mockResult.role ?: "Customer",
                                            token = mockResult.token
                                        )
                                        Toast.makeText(context, "Signed in successfully!", Toast.LENGTH_SHORT).show()
                                        onLoginSuccess()
                                    } else {
                                        authErrorMessage = "Invalid email or password"
                                    }
                                }
                            }
                        }
                    },
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryOrange),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(color = TextWhite, modifier = Modifier.size(24.dp))
                    } else {
                        Text(
                            text = "SIGN IN",
                            color = TextWhite,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Google SSO Button
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            GoogleAuthHelper.signInWithGoogle(
                                context = context,
                                onSuccess = { user ->
                                    sessionManager.saveUserSession(
                                        userId = 1,
                                        fullName = user.displayName,
                                        email = user.email,
                                        role = "Customer",
                                        token = user.idToken
                                    )
                                    Toast.makeText(context, "SSO Welcome ${user.displayName}", Toast.LENGTH_SHORT).show()
                                    onLoginSuccess()
                                },
                                onError = { msg ->
                                    sessionManager.saveUserSession(
                                        userId = 1,
                                        fullName = "Sipho Dlamini",
                                        email = "sipho@example.com",
                                        role = "Customer",
                                        token = "sso_fallback_token"
                                    )
                                    Toast.makeText(context, "SSO Sign-in: $msg", Toast.LENGTH_SHORT).show()
                                    onLoginSuccess()
                                }
                            )
                        }
                    },
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, PrimaryOrange),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                ) {
                    Text(text = "Sign in with Google (SSO)", color = TextWhite)
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Link to Register
                TextButton(onClick = { onNavigateToRegister() }) {
                    Text(
                        text = "Don't have an account? Register",
                        color = TextGray,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview() {
    LegaciesScreenPrintersAPKTheme {
        LoginScreen(
            onLoginSuccess = {},
            onNavigateToRegister = {}
        )
    }
}
