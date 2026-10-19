package com.example.legaciesscreenprintersapk.util

import android.content.Context
import android.content.SharedPreferences
import com.example.legaciesscreenprintersapk.ui.remote.models.UserDto

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

        fun saveSession(
            context: Context,
            userId: Int,
            fullName: String,
            email: String,
            role: String = "Customer",
            phone: String = "+27 82 123 4567",
            token: String? = null
        ) {
            SessionManager(context).saveUserSession(
                userId = userId,
                fullName = fullName,
                email = email,
                phone = phone,
                role = role,
                token = token
            )
        }

        fun isLoggedIn(context: Context): Boolean {
            return SessionManager(context).isLoggedIn()
        }

        fun getUserDetails(context: Context): UserDto {
            return SessionManager(context).getUserDetails()
        }

        fun clearSession(context: Context) {
            SessionManager(context).clearSession()
        }

        fun registerAccount(
            context: Context,
            fullName: String,
            email: String,
            phone: String,
            rawPassword: String
        ): UserDto {
            return SessionManager(context).registerAccount(fullName, email, phone, rawPassword)
        }

        fun verifyAccount(
            context: Context,
            email: String,
            rawPassword: String
        ): UserDto? {
            return SessionManager(context).verifyAccount(email, rawPassword)
        }
    }

    fun registerAccount(
        fullName: String,
        email: String,
        phone: String,
        rawPassword: String
    ): UserDto {
        val cleanEmail = email.trim().lowercase()
        val passwordHash = SecurityUtils.hashPassword(rawPassword)
        val newUserId = prefs.getInt("total_registered_users", 100) + 1

        val editor = prefs.edit()
        editor.putInt("user_id_$cleanEmail", newUserId)
        editor.putString("user_name_$cleanEmail", fullName.trim())
        editor.putString("user_phone_$cleanEmail", phone.trim())
        editor.putString("user_hash_$cleanEmail", passwordHash)
        editor.putInt("total_registered_users", newUserId)
        editor.apply()

        return UserDto(
            userId = newUserId,
            fullName = fullName.trim(),
            email = cleanEmail,
            phoneNumber = phone.trim(),
            role = "Customer"
        )
    }

    fun verifyAccount(
        email: String,
        rawPassword: String
    ): UserDto? {
        val cleanEmail = email.trim().lowercase()
        val storedHash = prefs.getString("user_hash_$cleanEmail", null) ?: return null
        val inputHash = SecurityUtils.hashPassword(rawPassword)

        if (storedHash == inputHash) {
            val userId = prefs.getInt("user_id_$cleanEmail", 1)
            val name = prefs.getString("user_name_$cleanEmail", "Customer User") ?: "Customer User"
            val phone = prefs.getString("user_phone_$cleanEmail", "+27 82 123 4567") ?: "+27 82 123 4567"
            return UserDto(
                userId = userId,
                fullName = name,
                email = cleanEmail,
                phoneNumber = phone,
                role = "Customer"
            )
        }
        return null
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

    fun getUserDetails(): UserDto {
        return UserDto(
            userId = getUserId(),
            fullName = getUserFullName(),
            email = getUserEmail(),
            phoneNumber = getUserPhone(),
            role = getUserRole()
        )
    }

    fun clearSession() {
        val editor = prefs.edit()
        editor.remove(KEY_USER_ID)
        editor.remove(KEY_FULL_NAME)
        editor.remove(KEY_EMAIL)
        editor.remove(KEY_PHONE)
        editor.remove(KEY_ROLE)
        editor.remove(KEY_TOKEN)
        editor.putBoolean(KEY_IS_LOGGED_IN, false)
        editor.apply()
    }
}
