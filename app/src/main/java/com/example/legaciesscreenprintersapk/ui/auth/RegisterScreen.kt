package com.example.legaciesscreenprintersapk.ui.auth

import android.widget.Toast
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.legaciesscreenprintersapk.ui.remote.MockDataRepository
import com.example.legaciesscreenprintersapk.ui.remote.RetrofitClient
import com.example.legaciesscreenprintersapk.ui.remote.models.RegisterRequest
import com.example.legaciesscreenprintersapk.util.SecurityUtils
import com.example.legaciesscreenprintersapk.util.SessionManager
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    onRegisterSuccess: () -> Unit,
    onNavigateToLogin: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    var nameError by remember { mutableStateOf<String?>(null) }
    var phoneError by remember { mutableStateOf<String?>(null) }
    var emailError by remember { mutableStateOf<String?>(null) }
    var passwordError by remember { mutableStateOf<String?>(null) }
    var confirmPasswordError by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()
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

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Create Customer Account",
                    color = TextWhite,
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Full Name
                OutlinedTextField(
                    value = name,
                    onValueChange = {
                        name = it
                        if (nameError != null) nameError = null
                    },
                    label = { Text("Full Name", color = TextGray) },
                    singleLine = true,
                    isError = nameError != null,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryOrange,
                        unfocusedBorderColor = TextGray,
                        errorBorderColor = ErrorRed,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                nameError?.let {
                    Text(text = it, color = ErrorRed, fontSize = 11.sp, modifier = Modifier.align(Alignment.Start))
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Phone Number
                OutlinedTextField(
                    value = phone,
                    onValueChange = {
                        phone = it
                        if (phoneError != null) phoneError = null
                    },
                    label = { Text("Contact Number", color = TextGray) },
                    singleLine = true,
                    isError = phoneError != null,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PrimaryOrange,
                        unfocusedBorderColor = TextGray,
                        errorBorderColor = ErrorRed,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                phoneError?.let {
                    Text(text = it, color = ErrorRed, fontSize = 11.sp, modifier = Modifier.align(Alignment.Start))
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Email
                OutlinedTextField(
                    value = email,
                    onValueChange = {
                        email = it
                        if (emailError != null) emailError = null
                    },
                    label = { Text("Email Address", color = TextGray) },
                    singleLine = true,
                    isError = emailError != null,
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
                emailError?.let {
                    Text(text = it, color = ErrorRed, fontSize = 11.sp, modifier = Modifier.align(Alignment.Start))
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Password
                OutlinedTextField(
                    value = password,
                    onValueChange = {
                        password = it
                        if (passwordError != null) passwordError = null
                    },
                    label = { Text("Password", color = TextGray) },
                    singleLine = true,
                    isError = passwordError != null,
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
                passwordError?.let {
                    Text(text = it, color = ErrorRed, fontSize = 11.sp, modifier = Modifier.align(Alignment.Start))
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Confirm Password
                OutlinedTextField(
                    value = confirmPassword,
                    onValueChange = {
                        confirmPassword = it
                        if (confirmPasswordError != null) confirmPasswordError = null
                    },
                    label = { Text("Confirm Password", color = TextGray) },
                    singleLine = true,
                    isError = confirmPasswordError != null,
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Toggle confirm password visibility",
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
                confirmPasswordError?.let {
                    Text(text = it, color = ErrorRed, fontSize = 11.sp, modifier = Modifier.align(Alignment.Start))
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        nameError = null
                        phoneError = null
                        emailError = null
                        passwordError = null
                        confirmPasswordError = null

                        var hasError = false
                        if (name.isBlank()) {
                            nameError = "Full name is required"
                            hasError = true
                        }
                        if (!SecurityUtils.isValidPhone(phone)) {
                            phoneError = "Valid contact phone number is required"
                            hasError = true
                        }
                        if (!SecurityUtils.isValidEmail(email)) {
                            emailError = "Valid email address is required"
                            hasError = true
                        }
                        if (password.length < 6) {
                            passwordError = "Password must be at least 6 characters"
                            hasError = true
                        }
                        if (password != confirmPassword) {
                            confirmPasswordError = "Passwords do not match"
                            hasError = true
                        }

                        if (!hasError) {
                            isLoading = true
                            scope.launch {
                                val trimmedName = name.trim()
                                val trimmedEmail = email.trim()
                                val trimmedPhone = phone.trim()
                                try {
                                    val api = RetrofitClient.apiService
                                    val response = api.register(RegisterRequest(trimmedName, trimmedEmail, trimmedPhone, password))
                                    isLoading = false

                                    if (response.isSuccessful && response.body() != null) {
                                        val body = response.body()!!
                                        SessionManager.saveSession(
                                            context = context,
                                            userId = body.userId ?: 1,
                                            fullName = body.fullName ?: trimmedName,
                                            email = body.email ?: trimmedEmail,
                                            role = body.role ?: "Customer",
                                            phone = trimmedPhone,
                                            token = body.token
                                        )
                                        Toast.makeText(context, "Registration successful!", Toast.LENGTH_SHORT).show()
                                        onRegisterSuccess()
                                    } else {
                                        val mockResult = repository.register(trimmedName, trimmedEmail, trimmedPhone, password)
                                        SessionManager.registerAccount(context, trimmedName, trimmedEmail, trimmedPhone, password)
                                        SessionManager.saveSession(
                                            context = context,
                                            userId = mockResult.userId ?: 1,
                                            fullName = mockResult.fullName ?: trimmedName,
                                            email = mockResult.email ?: trimmedEmail,
                                            role = mockResult.role ?: "Customer",
                                            phone = trimmedPhone,
                                            token = mockResult.token
                                        )
                                        Toast.makeText(context, "Account registered successfully!", Toast.LENGTH_SHORT).show()
                                        onRegisterSuccess()
                                    }
                                } catch (_: Exception) {
                                    isLoading = false
                                    val mockResult = repository.register(trimmedName, trimmedEmail, trimmedPhone, password)
                                    SessionManager.registerAccount(context, trimmedName, trimmedEmail, trimmedPhone, password)
                                    SessionManager.saveSession(
                                        context = context,
                                        userId = mockResult.userId ?: 1,
                                        fullName = mockResult.fullName ?: trimmedName,
                                        email = mockResult.email ?: trimmedEmail,
                                        role = mockResult.role ?: "Customer",
                                        phone = trimmedPhone,
                                        token = mockResult.token
                                    )
                                    Toast.makeText(context, "Account registered successfully!", Toast.LENGTH_SHORT).show()
                                    onRegisterSuccess()
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
                        Text(text = "REGISTER ACCOUNT", color = TextWhite, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                TextButton(onClick = { onNavigateToLogin() }) {
                    Text(text = "Already have an account? Sign In", color = TextGray, fontSize = 12.sp)
                }
            }
        }
    }
}
