package com.example.legaciesscreenprintersapk.util

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.legaciesscreenprintersapk.ui.remote.models.OrderTrackingDto
import com.example.legaciesscreenprintersapk.ui.remote.models.UserDto
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SessionManager(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)

    private data class LocalUserAccount(
        val userId: Int,
        val fullName: String,
        val email: String,
        val phone: String,
        val passwordHash: String,
        val role: String = "Customer"
    )

    companion object {
        private const val PREF_NAME = "LegaciesUserSession"
        private const val KEY_USER_ID = "userId"
        private const val KEY_FULL_NAME = "fullName"
        private const val KEY_EMAIL = "email"
        private const val KEY_PHONE = "phoneNumber"
        private const val KEY_ROLE = "role"
        private const val KEY_TOKEN = "authToken"
        private const val KEY_IS_LOGGED_IN = "isLoggedIn"
        private const val KEY_SAVED_ORDERS = "saved_created_orders"
        private const val KEY_REGISTERED_ACCOUNTS = "registered_accounts"

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

        fun saveCreatedOrder(context: Context, order: OrderTrackingDto) {
            SessionManager(context).saveCreatedOrder(order)
        }

        fun getSavedCreatedOrders(context: Context): List<OrderTrackingDto> {
            return SessionManager(context).getSavedCreatedOrders()
        }

        fun registerAccount(context: Context, name: String, email: String, phone: String, rawPassword: String) {
            SessionManager(context).registerAccount(name, email, phone, rawPassword)
        }

        fun verifyAccount(context: Context, email: String, rawPassword: String): UserDto? {
            return SessionManager(context).verifyAccount(email, rawPassword)
        }
    }

    fun saveUserSession(
        userId: Int,
        fullName: String,
        email: String,
        phone: String = "+27 82 123 4567",
        role: String = "Customer",
        token: String? = null
    ) {
        prefs.edit {
            putInt(KEY_USER_ID, userId)
            putString(KEY_FULL_NAME, fullName)
            putString(KEY_EMAIL, email)
            putString(KEY_PHONE, phone)
            putString(KEY_ROLE, role)
            putString(KEY_TOKEN, token ?: "mock_jwt_token_$userId")
            putBoolean(KEY_IS_LOGGED_IN, true)
        }
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
        prefs.edit { clear() }
    }

    fun saveCreatedOrder(order: OrderTrackingDto) {
        val existing = getSavedCreatedOrders().toMutableList()
        val index = existing.indexOfFirst { it.orderRef == order.orderRef }
        if (index != -1) {
            existing[index] = order
        } else {
            existing.add(0, order)
        }
        val json = Gson().toJson(existing)
        prefs.edit { putString(KEY_SAVED_ORDERS, json) }
    }

    fun getSavedCreatedOrders(): List<OrderTrackingDto> {
        val json = prefs.getString(KEY_SAVED_ORDERS, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<OrderTrackingDto>>() {}.type
            Gson().fromJson(json, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }

    fun registerAccount(name: String, email: String, phone: String, rawPassword: String) {
        val existing = getRegisteredAccounts().toMutableList()
        val hashed = SecurityUtils.hashPassword(rawPassword)
        val normalizedEmail = email.trim().lowercase()
        val idx = existing.indexOfFirst { it.email.lowercase() == normalizedEmail }
        if (idx != -1) {
            existing[idx] = existing[idx].copy(fullName = name, phone = phone, passwordHash = hashed)
        } else {
            val newUserId = 100 + existing.size + 1
            existing.add(LocalUserAccount(newUserId, name, email.trim(), phone, hashed))
        }
        val json = Gson().toJson(existing)
        prefs.edit { putString(KEY_REGISTERED_ACCOUNTS, json) }
    }

    fun verifyAccount(email: String, rawPassword: String): UserDto? {
        val accounts = getRegisteredAccounts()
        val hashed = SecurityUtils.hashPassword(rawPassword)
        val normalizedEmail = email.trim().lowercase()
        val found = accounts.find { (it.email.lowercase() == normalizedEmail) && (it.passwordHash == hashed) }
        return found?.let {
            UserDto(
                userId = it.userId,
                fullName = it.fullName,
                email = it.email,
                phoneNumber = it.phone,
                role = it.role
            )
        }
    }

    private fun getRegisteredAccounts(): List<LocalUserAccount> {
        val json = prefs.getString(KEY_REGISTERED_ACCOUNTS, null) ?: return emptyList()
        return try {
            val type = object : TypeToken<List<LocalUserAccount>>() {}.type
            Gson().fromJson(json, type) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }
    }
}
