package com.example.legaciesscreenprintersapk.util

import java.util.Base64

/**
 * Reads the expiry time from the login token (JWT).
 * Reference: Jones, M., Bradley, J. and Sakimura, N. (2015) RFC 7519: JSON Web Token (JWT) - https://www.rfc-editor.org/rfc/rfc7519
 */
object JwtUtils {

    private val expPattern = Regex("\"exp\"\\s*:\\s*(\\d+)")

    /** The token's expiry in epoch milliseconds, or null if it can't be read. */
    fun expiryMillis(token: String?): Long? {
        if (token.isNullOrBlank()) return null
        val parts = token.split('.')
        if (parts.size != 3) return null
        return try {
            val payload = String(Base64.getUrlDecoder().decode(parts[1]), Charsets.UTF_8)
            expPattern.find(payload)?.groupValues?.get(1)?.toLongOrNull()?.times(1000)
        } catch (_: IllegalArgumentException) {
            null // not valid Base64URL
        }
    }

    /** True if the token is missing, unreadable, or past its expiry (with a small safety margin). */
    fun isExpired(token: String?, nowMillis: Long = System.currentTimeMillis(), marginMillis: Long = 30_000): Boolean {
        val expiry = expiryMillis(token) ?: return true
        return nowMillis >= expiry - marginMillis
    }
}
