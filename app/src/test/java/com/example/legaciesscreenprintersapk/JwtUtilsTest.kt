package com.example.legaciesscreenprintersapk

import com.example.legaciesscreenprintersapk.util.JwtUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Base64

/** The app reads the JWT's "exp" to decide whether a stored session is still valid. */
class JwtUtilsTest {

    /** Builds an unsigned JWT-shaped string with the given payload (signature isn't checked here). */
    private fun tokenWithPayload(json: String): String {
        val enc = Base64.getUrlEncoder().withoutPadding()
        val header = enc.encodeToString("""{"alg":"HS256","typ":"JWT"}""".toByteArray())
        val payload = enc.encodeToString(json.toByteArray())
        return "$header.$payload.signature"
    }

    @Test
    fun readsExpiryFromPayload() {
        val token = tokenWithPayload("""{"sub":"2","role":"customer","exp":1794765150}""")
        assertEquals(1_794_765_150_000L, JwtUtils.expiryMillis(token))
    }

    @Test
    fun tokenBeforeExpiryIsNotExpired() {
        val token = tokenWithPayload("""{"exp":2000}""") // expires at 2,000,000 ms
        assertFalse(JwtUtils.isExpired(token, nowMillis = 1_000_000, marginMillis = 0))
    }

    @Test
    fun tokenAfterExpiryIsExpired() {
        val token = tokenWithPayload("""{"exp":2000}""")
        assertTrue(JwtUtils.isExpired(token, nowMillis = 2_000_001, marginMillis = 0))
    }

    @Test
    fun safetyMarginTreatsNearlyExpiredTokenAsExpired() {
        val token = tokenWithPayload("""{"exp":2000}""")
        assertTrue(JwtUtils.isExpired(token, nowMillis = 1_990_000, marginMillis = 30_000))
    }

    @Test
    fun missingOrMalformedTokensCountAsExpired() {
        assertNull(JwtUtils.expiryMillis(null))
        assertNull(JwtUtils.expiryMillis("not-a-jwt"))
        assertNull(JwtUtils.expiryMillis("a.%%%.c"))
        assertNull(JwtUtils.expiryMillis(tokenWithPayload("""{"sub":"2"}""")))
        assertTrue(JwtUtils.isExpired(null))
        assertTrue(JwtUtils.isExpired("not-a-jwt"))
    }
}
