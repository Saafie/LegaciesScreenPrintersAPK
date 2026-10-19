package com.example.legaciesscreenprintersapk

import com.example.legaciesscreenprintersapk.data.MockRepository
import com.example.legaciesscreenprintersapk.data.OrderItem
import com.example.legaciesscreenprintersapk.util.SecurityUtils
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class SecurityUtilsTest {

    @Test
    fun testPasswordHashingSHA256WithSalt() {
        val rawPassword = "MySecretPassword123"
        val hash1 = SecurityUtils.hashPassword(rawPassword)
        val hash2 = SecurityUtils.hashPassword(rawPassword)

        assertNotNull(hash1)
        assertEquals(64, hash1.length) // SHA-256 output hex string length
        assertEquals(hash1, hash2) // Deterministic output

        val differentHash = SecurityUtils.hashPassword("DifferentPassword")
        assertNotEquals(hash1, differentHash)
    }

    @Test
    fun testEmailValidation() {
        assertTrue(SecurityUtils.isValidEmail("user@example.com"))
        assertTrue(SecurityUtils.isValidEmail("sipho.dlamini@legacies.co.za"))
        assertFalse(SecurityUtils.isValidEmail(""))
        assertFalse(SecurityUtils.isValidEmail("invalid-email"))
        assertFalse(SecurityUtils.isValidEmail("user@.com"))
    }

    @Test
    fun testPhoneValidation() {
        assertTrue(SecurityUtils.isValidPhone("+27 82 123 4567"))
        assertTrue(SecurityUtils.isValidPhone("0821234567"))
        assertFalse(SecurityUtils.isValidPhone("12345"))
        assertFalse(SecurityUtils.isValidPhone(""))
    }

    @Test
    fun testMockRepositorySeededOrdersAndSearch() {
        val orders = MockRepository.getOrders()
        assertTrue("Repository should contain at least 10 seeded orders", orders.size >= 10)

        val searched = MockRepository.searchOrders("ORD5824")
        assertEquals(1, searched.size)
        assertEquals("ORD5824", searched.first().id)

        val phoneSearch = MockRepository.searchOrders("082 123 4567")
        assertTrue(phoneSearch.isNotEmpty())
    }

    @Test
    fun testMockRepositoryOrderCreationAndCalculation() {
        val items = listOf(
            OrderItem("T1", "Banner Design", "Standard Banner Vinyl", null, 3, 100.00, 300.00),
            OrderItem("T2", "Polo Shirt", "10oz Vinyl (Black, Gold)", null, 2, 120.00, 240.00),
        )

        val newOrder = MockRepository.createOrder(
            clientName = "Test Client",
            clientPhone = "+27 82 999 8888",
            clientEmail = "test@client.com",
            items = items,
            isRemoteOrder = true,
        )

        assertNotNull(newOrder)
        assertTrue(newOrder.id.startsWith("ORD"))
        assertEquals(540.00, newOrder.totalAmount, 0.01)
        assertEquals(0, newOrder.currentStageIndex) // Stage 1 (index 0): Order Received & Verified

        val stages = MockRepository.getTrackingStages(newOrder.currentStageIndex)
        assertEquals(4, stages.size)
        assertTrue(stages[0].isCurrent)
        assertTrue(stages[0].isCompleted)
        assertFalse(stages[1].isCurrent)
    }
}
