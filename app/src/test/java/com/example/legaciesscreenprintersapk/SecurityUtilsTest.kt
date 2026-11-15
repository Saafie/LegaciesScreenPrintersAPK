package com.example.legaciesscreenprintersapk

import com.example.legaciesscreenprintersapk.util.SecurityUtils
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Form validation rules used by the sign-in, register, order and settings screens. */
class SecurityUtilsTest {

    @Test
    fun validEmailsAreAccepted() {
        assertTrue(SecurityUtils.isValidEmail("user@example.com"))
        assertTrue(SecurityUtils.isValidEmail("first.last@legacies.co.za"))
        assertTrue(SecurityUtils.isValidEmail("  padded@example.com  "))
    }

    @Test
    fun invalidEmailsAreRejected() {
        assertFalse(SecurityUtils.isValidEmail(""))
        assertFalse(SecurityUtils.isValidEmail("invalid-email"))
        assertFalse(SecurityUtils.isValidEmail("user@.com"))
        assertFalse(SecurityUtils.isValidEmail("user@example"))
    }

    @Test
    fun phoneNeedsAtLeastSevenDigits() {
        assertTrue(SecurityUtils.isValidPhone("+27 82 123 4567"))
        assertTrue(SecurityUtils.isValidPhone("0821234567"))
        assertFalse(SecurityUtils.isValidPhone("12345"))
        assertFalse(SecurityUtils.isValidPhone(""))
    }
}
