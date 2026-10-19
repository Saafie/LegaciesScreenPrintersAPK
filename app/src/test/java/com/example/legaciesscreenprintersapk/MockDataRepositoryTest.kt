package com.example.legaciesscreenprintersapk

import com.example.legaciesscreenprintersapk.ui.remote.MockDataRepository
import com.example.legaciesscreenprintersapk.ui.remote.models.CreateOrderItemRequest
import com.example.legaciesscreenprintersapk.ui.remote.models.CreateOrderRequest
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class MockDataRepositoryTest {

    private lateinit var repository: MockDataRepository

    @Before
    fun setUp() {
        MockDataRepository.forceOfflineMode = true
        repository = MockDataRepository()
    }

    @Test
    fun testSeededUsersCount() {
        assertTrue("Repository should have at least 10 seeded users", MockDataRepository.seededUsers.size >= 10)
        val user1 = MockDataRepository.seededUsers.first()
        assertEquals("Sipho Dlamini", user1.fullName)
        assertEquals("sipho@example.com", user1.email)
    }

    @Test
    fun testSeededMaterialsCount() {
        val materials = runBlocking { repository.getMaterials() }
        assertEquals("Repository should have 10 seeded vinyl/print materials", 10, materials.size)
        assertEquals("10oz Vinyl (Black, Gold)", materials.first().materialName)
        assertEquals(120.00, materials.first().unitPrice, 0.01)
    }

    @Test
    fun testSeededOrdersCount() {
        val orders = runBlocking { repository.getAllOrders() }
        assertTrue("Repository should have at least 10 seeded orders", orders.size >= 10)
        val order1 = orders.find { it.orderRef == "ORD5824" }
        assertNotNull(order1)
        assertEquals("Sipho Dlamini", order1?.clientName)
        assertEquals(340.00, order1?.totalAmount ?: 0.0, 0.01)
    }

    @Test
    fun testUserLoginAndRegistration() = runBlocking {
        val authResult = repository.login("sipho@example.com", "Password123")
        assertNotNull(authResult.token)
        assertEquals("Sipho Dlamini", authResult.fullName)

        val regResult = repository.register("New User", "newuser@example.com", "+27 82 000 1111", "NewPass123")
        assertNotNull(regResult.token)
        assertEquals("New User", regResult.fullName)
        assertTrue(MockDataRepository.seededUsers.size >= 11)
    }

    @Test
    fun testCreateOrderAndTracking() = runBlocking {
        val createRequest = CreateOrderRequest(
            userId = 1,
            orderType = "Remote",
            items = listOf(
                CreateOrderItemRequest("Custom T-Shirt Logo", 1, 3), // 3 * 120 = 360
                CreateOrderItemRequest("Banner Sign", 4, 2)         // 2 * 100 = 200
            )
        )

        val createResponse = repository.createOrder(createRequest)
        assertNotNull(createResponse.orderRef)
        assertEquals(560.00, createResponse.total ?: 0.0, 0.01)

        val trackedOrder = repository.trackOrder(createResponse.orderRef!!)
        assertNotNull(trackedOrder)
        assertEquals(560.00, trackedOrder?.totalAmount ?: 0.0, 0.01)
        assertEquals("Stage 1: Order Received & Verified", trackedOrder?.status)
    }

    @Test
    fun testUpdateOrderStatus() = runBlocking {
        val success = repository.updateStatus(1, "Stage 3: Screen Printing In Progress")
        assertTrue(success)

        val updatedOrder = repository.trackOrder("ORD5824")
        assertNotNull(updatedOrder)
        assertEquals("Stage 3: Screen Printing In Progress", updatedOrder?.status)
    }
}
