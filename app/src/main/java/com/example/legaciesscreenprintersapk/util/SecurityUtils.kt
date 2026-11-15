package com.example.legaciesscreenprintersapk.util

/** Input validation used by the sign-in, register, order and settings forms. */
object SecurityUtils {

    private val emailRegex = Regex("""^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$""")

    /** True for a plausible email address, e.g. name@example.co.za. */
    fun isValidEmail(email: String): Boolean {
        if (email.isBlank()) return false
        return emailRegex.matches(email.trim())
    }

    /** True if the number has at least 7 digits (spaces, + and dashes are allowed). */
    fun isValidPhone(phone: String): Boolean {
        val digitsOnly = phone.filter { it.isDigit() }
        return digitsOnly.length >= 7
    }
}
