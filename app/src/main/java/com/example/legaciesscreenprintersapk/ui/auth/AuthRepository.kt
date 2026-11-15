package com.example.legaciesscreenprintersapk.ui.auth

import android.content.Context
import android.util.Log
import com.example.legaciesscreenprintersapk.ui.remote.ApiService
import com.example.legaciesscreenprintersapk.ui.remote.RetrofitClient
import com.example.legaciesscreenprintersapk.ui.remote.models.ApiError
import com.example.legaciesscreenprintersapk.ui.remote.models.AuthRequest
import com.example.legaciesscreenprintersapk.ui.remote.models.AuthResponse
import com.example.legaciesscreenprintersapk.ui.remote.models.GoogleLoginRequest
import com.example.legaciesscreenprintersapk.ui.remote.models.RegisterRequest
import com.example.legaciesscreenprintersapk.util.GoogleAuthHelper
import com.example.legaciesscreenprintersapk.util.SessionManager
import com.google.gson.Gson
import retrofit2.Response
import java.io.IOException

/** All sign-in paths in one place: email/password login, registration, and Google SSO. */
class AuthRepository(
    context: Context,
    private val api: ApiService = RetrofitClient.apiService,
) {
    private val appContext = context.applicationContext
    private val session = SessionManager(appContext)

    /** Result of a sign-in attempt, ready for the screen to show. */
    sealed interface AuthResult {
        data class Success(val fullName: String) : AuthResult
        data class Error(val message: String) : AuthResult
        /** User closed the Google chooser; the screen should do nothing. */
        data object Cancelled : AuthResult
    }

    suspend fun login(email: String, password: String): AuthResult {
        Log.d(TAG, "Email login started.")
        return call { api.login(AuthRequest(email.trim(), password)) }
    }

    suspend fun register(fullName: String, email: String, phone: String, password: String): AuthResult {
        Log.d(TAG, "Registration started.")
        return call(fallbackPhone = phone) {
            api.register(RegisterRequest(fullName.trim(), email.trim(), phone.trim(), password))
        }
    }

    /** Google SSO: get an ID token from Google on the device. */
    suspend fun signInWithGoogle(activityContext: Context): AuthResult {
        return when (val google = GoogleAuthHelper.requestIdToken(activityContext)) {
            is GoogleAuthHelper.Result.Cancelled -> AuthResult.Cancelled
            is GoogleAuthHelper.Result.Failure -> AuthResult.Error(google.message)
            is GoogleAuthHelper.Result.Success -> {
                Log.d(TAG, "Exchanging Google ID token with the API.")
                call { api.googleLogin(GoogleLoginRequest(google.idToken)) }
            }
        }
    }

    /** Signs out locally and resets Google's account choice. */
    suspend fun signOut() {
        session.clearSession()
        GoogleAuthHelper.clearCredentialState(appContext)
        Log.i(TAG, "Signed out.")
    }

    private suspend fun call(fallbackPhone: String? = null, request: suspend () -> Response<AuthResponse>): AuthResult {
        return try {
            val response = request()
            val body = response.body()
            when {
                response.isSuccessful && body != null && session.saveFromAuthResponse(body, fallbackPhone) -> {
                    Log.i(TAG, "Signed in as user ${body.userId} (${body.role}).")
                    AuthResult.Success(body.fullName.orEmpty())
                }
                response.isSuccessful -> AuthResult.Error("The server sent an incomplete sign-in response.")
                else -> {
                    val message = errorMessage(response)
                    Log.w(TAG, "Sign-in rejected: HTTP ${response.code()}.")
                    AuthResult.Error(message)
                }
            }
        } catch (e: IOException) {
            Log.w(TAG, "Sign-in failed: server unreachable (${e.javaClass.simpleName}).")
            AuthResult.Error("Can't reach the server. Check your internet connection and try again.")
        }
    }

    /** Turns an API error response into a message the user can act on. */
    private fun errorMessage(response: Response<*>): String {
        val apiMessage = try {
            response.errorBody()?.string()?.let { Gson().fromJson(it, ApiError::class.java)?.message }
        } catch (_: Exception) {
            null
        }
        return apiMessage ?: when (response.code()) {
            400 -> "Please check the details you entered."
            401 -> "Invalid email or password."
            503 -> "Google sign-in isn't available on the server right now."
            in 500..599 -> "The server had a problem. Please try again shortly."
            else -> "Sign-in failed (error ${response.code()})."
        }
    }

    private companion object {
        const val TAG = "LSP/Auth"
    }
}
