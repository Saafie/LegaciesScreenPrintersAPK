package com.example.legaciesscreenprintersapk.ui.remote

import android.util.Log
import com.example.legaciesscreenprintersapk.ui.remote.models.ApiError
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.Response

/**
 * Adds the login token to API calls and handles expired sessions (401).
 * Reference: Square (2025) OkHttp Interceptors - https://square.github.io/okhttp/features/interceptors/
 */
class AuthInterceptor(
    private val tokenProvider: () -> String?,
    private val onSessionRejected: (reason: String) -> Unit,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val isSignInCall = original.url.encodedPath.startsWith("/api/auth/") &&
            !original.url.encodedPath.endsWith("/me")
        val token = if (isSignInCall) null else tokenProvider()

        val request = if (token != null) {
            original.newBuilder().header("Authorization", "Bearer $token").build()
        } else {
            original
        }

        val response = chain.proceed(request)

        if (response.code == 401 && token != null) {
            // peekBody reads a copy, so the caller can still read the response normally.
            val reason = try {
                Gson().fromJson(response.peekBody(4096).string(), ApiError::class.java)?.message
            } catch (_: Exception) {
                null
            } ?: "Your session has ended. Please sign in again."
            Log.w(TAG, "API rejected the session token (401) on ${original.url.encodedPath}.")
            onSessionRejected(reason)
        }
        return response
    }

    private companion object {
        const val TAG = "LSP/Net"
    }
}
