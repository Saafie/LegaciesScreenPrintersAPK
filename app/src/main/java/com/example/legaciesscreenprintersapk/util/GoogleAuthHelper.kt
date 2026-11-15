package com.example.legaciesscreenprintersapk.util

import android.content.Context
import android.util.Log
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.ClearCredentialException
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import androidx.credentials.exceptions.NoCredentialException
import com.example.legaciesscreenprintersapk.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException

/**
 * "Sign in with Google" using Android Credential Manager.
 * Reference: Android Developers (2025) Authenticate users with Sign in with Google - https://developer.android.com/identity/sign-in/credential-manager-siwg
 * Reference: Android Developers (2025) Sign in your user with Credential Manager - https://developer.android.com/identity/sign-in/credential-manager
 */
object GoogleAuthHelper {
    private const val TAG = "LSP/Auth"

    /** Outcome of asking Google for an ID token. */
    sealed interface Result {
        data class Success(val idToken: String, val email: String) : Result
        /** The user closed the account chooser. Not an error: do nothing. */
        data object Cancelled : Result
        data class Failure(val message: String) : Result
    }

    /** True when a Google web client id was provided at build time (see app/build.gradle.kts). */
    val isConfigured: Boolean get() = BuildConfig.GOOGLE_WEB_CLIENT_ID.isNotBlank()

    /** Shows the Google account chooser and returns an ID token for the chosen account. */
    suspend fun requestIdToken(context: Context): Result {
        if (!isConfigured) {
            Log.e(TAG, "Google sign-in not configured: googleWebClientId is empty.")
            return Result.Failure("Google sign-in isn't set up in this build yet.")
        }

        val googleIdOption = GetGoogleIdOption.Builder()
            .setFilterByAuthorizedAccounts(false) // show all Google accounts on the device
            .setServerClientId(BuildConfig.GOOGLE_WEB_CLIENT_ID)
            .setAutoSelectEnabled(false)
            .build()

        val request = GetCredentialRequest.Builder()
            .addCredentialOption(googleIdOption)
            .build()

        Log.d(TAG, "Opening Google account chooser.")
        return try {
            val credential = CredentialManager.create(context).getCredential(context, request).credential

            if (credential is CustomCredential &&
                credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
            ) {
                val google = GoogleIdTokenCredential.createFrom(credential.data)
                Log.i(TAG, "Google returned an ID token.") // never log the token itself
                Result.Success(idToken = google.idToken, email = google.id)
            } else {
                Log.e(TAG, "Unexpected credential type: ${credential.type}")
                Result.Failure("Google sign-in returned an unexpected response.")
            }
        } catch (e: GetCredentialCancellationException) {
            Log.d(TAG, "User cancelled Google sign-in.")
            Result.Cancelled
        } catch (e: NoCredentialException) {
            Log.w(TAG, "No Google account available on this device.")
            Result.Failure("No Google account found on this device. Add one in Settings, then try again.")
        } catch (e: GoogleIdTokenParsingException) {
            Log.e(TAG, "Could not read Google ID token.", e)
            Result.Failure("Google sign-in failed. Please try again.")
        } catch (e: GetCredentialException) {
            Log.e(TAG, "Credential Manager error: ${e.type}", e)
            Result.Failure("Google sign-in failed: ${e.message ?: e.type}")
        }
    }

    /** Called on sign-out so the account chooser shows again next time. */
    suspend fun clearCredentialState(context: Context) {
        try {
            CredentialManager.create(context).clearCredentialState(ClearCredentialStateRequest())
            Log.d(TAG, "Cleared Credential Manager state.")
        } catch (e: ClearCredentialException) {
            Log.w(TAG, "Could not clear credential state: ${e.message}")
        }
    }
}
