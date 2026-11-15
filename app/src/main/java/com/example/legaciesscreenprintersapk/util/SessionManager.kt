package com.example.legaciesscreenprintersapk.util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit
import com.example.legaciesscreenprintersapk.ui.remote.OrdersCache
import com.example.legaciesscreenprintersapk.ui.remote.models.AuthResponse
import com.example.legaciesscreenprintersapk.ui.remote.models.OrderTrackingDto
import com.example.legaciesscreenprintersapk.ui.remote.models.UserDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

/**
 * Stores the signed-in session on the device: the API's JWT plus the user's display details.
 * Reference: Android Developers (2025) Save simple data with SharedPreferences - https://developer.android.com/training/data-storage/shared-preferences
 */
class SessionManager(context: Context) {
    private val prefs: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val TAG = "LSP/Session"

        /** Must match the file name excluded in backup_rules.xml / data_extraction_rules.xml. */
        const val PREF_NAME = "LegaciesUserSession"

        private const val KEY_USER_ID = "userId"
        private const val KEY_FULL_NAME = "fullName"
        private const val KEY_EMAIL = "email"
        private const val KEY_PHONE = "phoneNumber"
        private const val KEY_ROLE = "role"
        private const val KEY_TOKEN = "authToken"
        private const val KEY_CACHED_ORDERS = "cached_my_orders"
        private const val KEY_CACHED_AT = "cached_my_orders_at"

        fun isLoggedIn(context: Context): Boolean = SessionManager(context).isLoggedIn()

        fun getUserDetails(context: Context): UserDto = SessionManager(context).getUserDetails()

        fun clearSession(context: Context) = SessionManager(context).clearSession()
    }

    /** Saves a successful sign-in from the API. */
    fun saveFromAuthResponse(auth: AuthResponse, fallbackPhone: String? = null): Boolean {
        val token = auth.token
        val userId = auth.userId
        if (token.isNullOrBlank() || userId == null) {
            Log.w(TAG, "Not saving session: response had no token or user id.")
            return false
        }
        prefs.edit {
            putInt(KEY_USER_ID, userId)
            putString(KEY_FULL_NAME, auth.fullName.orEmpty())
            putString(KEY_EMAIL, auth.email.orEmpty())
            putString(KEY_PHONE, auth.phoneNumber ?: fallbackPhone.orEmpty())
            putString(KEY_ROLE, auth.role ?: "customer")
            putString(KEY_TOKEN, token)
        }
        Log.i(TAG, "Session saved for user $userId (expires ${JwtUtils.expiryMillis(token)?.let { java.util.Date(it) }}).")
        return true
    }

    /** Updates the display details shown in Settings without touching the token. */
    fun updateProfile(fullName: String, email: String, phone: String) {
        prefs.edit {
            putString(KEY_FULL_NAME, fullName)
            putString(KEY_EMAIL, email)
            putString(KEY_PHONE, phone)
        }
        Log.d(TAG, "Profile details updated locally.")
    }

    /** The signed-in user's id, or -1 if nobody is signed in. */
    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, -1)

    fun getUserFullName(): String = prefs.getString(KEY_FULL_NAME, null).orEmpty()

    fun getUserEmail(): String = prefs.getString(KEY_EMAIL, null).orEmpty()

    fun getUserPhone(): String = prefs.getString(KEY_PHONE, null).orEmpty()

    fun getUserRole(): String = prefs.getString(KEY_ROLE, null) ?: "customer"

    /** The stored JWT, or null if signed out. */
    fun getAuthToken(): String? = prefs.getString(KEY_TOKEN, null)

    /** Signed in only if a token is stored and it hasn't expired. Clears an expired session. */
    fun isLoggedIn(): Boolean {
        val token = getAuthToken() ?: return false
        if (JwtUtils.isExpired(token)) {
            Log.i(TAG, "Stored token has expired; clearing session.")
            clearSession()
            return false
        }
        return true
    }

    fun getUserDetails(): UserDto = UserDto(
        userId = getUserId().takeIf { it > 0 },
        fullName = getUserFullName(),
        email = getUserEmail(),
        phoneNumber = getUserPhone(),
        role = getUserRole()
    )

    fun clearSession() {
        prefs.edit { clear() }
        Log.i(TAG, "Session cleared.")
    }

    // ---- Offline copy of "my orders" (see OrderRepository) ----

    /** Keeps the last downloaded order list so it can be shown, labelled, when offline. */
    val ordersCache: OrdersCache = object : OrdersCache {
        override fun save(orders: List<OrderTrackingDto>, savedAtMillis: Long) {
            prefs.edit {
                putString(KEY_CACHED_ORDERS, Gson().toJson(orders))
                putLong(KEY_CACHED_AT, savedAtMillis)
            }
        }

        override fun load(): Pair<List<OrderTrackingDto>, Long>? {
            val json = prefs.getString(KEY_CACHED_ORDERS, null) ?: return null
            return try {
                val type = object : TypeToken<List<OrderTrackingDto>>() {}.type
                val orders: List<OrderTrackingDto> = Gson().fromJson(json, type) ?: return null
                orders to prefs.getLong(KEY_CACHED_AT, 0L)
            } catch (_: Exception) {
                null
            }
        }
    }
}
