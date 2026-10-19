package com.example.legaciesscreenprintersapk.util

import java.security.MessageDigest

object SecurityUtils {
    private const val DEFAULT_SALT = "LegaciesScreenPrintersSalt2026"

    //Hashes RAW passwords by using SHA-256 algorithm with salt.
    fun hashPassword(password: String, salt: String = DEFAULT_SALT): String {
        val saltedPassword = password + salt
        val bytes = MessageDigest.getInstance("SHA-256").digest(saltedPassword.toByteArray(Charsets.UTF_8))
        return bytes.joinToString("") { "%02x".format(it) }
    }


    //Validates the format of the email address
    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false
        val emailRegex = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$".toRegex()
        return emailRegex.matches(email.trim())
    }

    //Validates the format of the contact number
    fun isValidPhone(phone: String): Boolean {
        val digitsOnly = phone.filter { it.isDigit() }
        return digitsOnly.length >= 7
    }
}
