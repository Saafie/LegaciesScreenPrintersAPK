package com.example.legaciesscreenprintersapk

import com.example.legaciesscreenprintersapk.ui.remote.MockDataRepository
import com.example.legaciesscreenprintersapk.ui.remote.models.CreateOrderItemRequest
import com.example.legaciesscreenprintersapk.ui.remote.models.CreateOrderRequest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class AuthAndOrderTest {

    private lateinit var repository: MockDataRepository

    @Before
    fun setUp() {
        MockDataRepository.forceOfflineMode = true
        repository = MockDataRepository()
    }

    @Test
    fun testOfflineCredentialVerificationValidAndInvalid() {
        // Sipho Dlamini - Correct password
        val siphoAuth = repository.verifyOfflineCredentials("sipho@example.com", "Password123")
        assertNotNull("Valid credentials for Sipho should be accepted", siphoAuth)
        assertEquals(1, siphoAuth?.userId)
        assertEquals("Sipho Dlamini", siphoAuth?.fullName)

        // Jessica Taylor - Correct password
        val jessicaAuth = repository.verifyOfflineCredentials("jessica@example.com", "SecurePass2026")
        assertNotNull("Valid credentials for Jessica should be accepted", jessicaAuth)
        assertEquals(2, jessicaAuth?.userId)
        assertEquals("Jessica Taylor", jessicaAuth?.fullName)

        // Invalid Password
        val invalidPassAuth = repository.verifyOfflineCredentials("sipho@example.com", "WrongPassword")
        assertNull("Invalid password should be rejected", invalidPassAuth)

        // Non-existent Email
        val invalidUserAuth = repository.verifyOfflineCredentials("nonexistent@example.com", "Password123")
        assertNull("Non-existent email should be rejected", invalidUserAuth)
    }

    @Test
    fun testSeededOrdersUserIdMappingAndFiltering() = runBlocking {
        val allOrders = repository.getAllOrders()
        assertTrue("Repository should contain seeded orders", allOrders.isNotEmpty())

        // User 1 (Sipho) order filtering
        val siphoOrders = allOrders.filter { it.userId == 1 }
        assertEquals("User 1 should have exactly 1 seeded order", 1, siphoOrders.size)
        assertEquals("ORD5824", siphoOrders.first().orderRef)

        // User 2 (Jessica) order filtering
        val jessicaOrders = allOrders.filter { it.userId == 2 }
        assertEquals("User 2 should have exactly 1 seeded order", 1, jessicaOrders.size)
        assertEquals("ORD5825", jessicaOrders.first().orderRef)

        // Non-existent User ID
        val emptyOrders = allOrders.filter { it.userId == 999 }
        assertTrue("Non-existent user should have no orders", emptyOrders.isEmpty())
    }

    @Test
    fun testCreateOrderAttachesCorrectUserId() = runBlocking {
        val createRequest = CreateOrderRequest(
            userId = 3,
            orderType = "Remote",
            items = listOf(
                CreateOrderItemRequest("Wall Vinyl Print", 2, 2)
            )
        )

        val response = repository.createOrder(createRequest)
        assertNotNull(response.orderRef)

        val user3Orders = repository.getAllOrders().filter { it.userId == 3 }
        assertTrue("User 3 should now have at least 2 orders (1 seeded + 1 created)", user3Orders.size >= 2)
        val newlyCreated = user3Orders.find { it.orderRef == response.orderRef }
        assertNotNull(newlyCreated)
        assertEquals(3, newlyCreated?.userId)
    }
}
