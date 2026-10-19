package com.example.legaciesscreenprintersapk.util

import android.content.Context
import android.content.SharedPreferences

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREF_NAME = "LegaciesUserSession"
        private const val KEY_USER_ID = "userId"
        private const val KEY_FULL_NAME = "fullName"
        private const val KEY_EMAIL = "email"
        private const val KEY_PHONE = "phoneNumber"
        private const val KEY_ROLE = "role"
        private const val KEY_TOKEN = "authToken"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
    }

    fun saveUserSession(
        userId: Int,
        fullName: String,
        email: String,
        phone: String = "+27 82 123 4567",
        role: String = "Customer",
        token: String? = null
    ) {
        val editor = prefs.edit()
        editor.putInt(KEY_USER_ID, userId)
        editor.putString(KEY_FULL_NAME, fullName)
        editor.putString(KEY_EMAIL, email)
        editor.putString(KEY_PHONE, phone)
        editor.putString(KEY_ROLE, role)
        editor.putString(KEY_TOKEN, token ?: "mock_jwt_token_$userId")
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.apply()
    }

    fun getUserId(): Int = prefs.getInt(KEY_USER_ID, 1)

    fun getUserFullName(): String = prefs.getString(KEY_FULL_NAME, "Sipho Dlamini") ?: "Sipho Dlamini"

    fun getUserEmail(): String = prefs.getString(KEY_EMAIL, "sipho@example.com") ?: "sipho@example.com"

    fun getUserPhone(): String = prefs.getString(KEY_PHONE, "+27 82 123 4567") ?: "+27 82 123 4567"

    fun getUserRole(): String = prefs.getString(KEY_ROLE, "Customer") ?: "Customer"

    fun getAuthToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun isLoggedIn(): Boolean = prefs.getBoolean(KEY_IS_LOGGED_IN, false)

    fun clearSession() {
        prefs.edit().clear().apply()
    }
}
