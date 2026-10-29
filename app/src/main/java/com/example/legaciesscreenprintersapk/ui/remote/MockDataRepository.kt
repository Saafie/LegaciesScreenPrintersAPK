package com.example.legaciesscreenprintersapk.ui.remote

import android.content.Context
import android.util.Log
import com.example.legaciesscreenprintersapk.ui.remote.models.*
import com.example.legaciesscreenprintersapk.util.SecurityUtils
import com.example.legaciesscreenprintersapk.util.SessionManager
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.CopyOnWriteArrayList

class MockDataRepository(
    private val context: Context? = null,
    private val apiService: ApiService = RetrofitClient.apiService
) {
    companion object {
        private const val TAG = "MockDataRepository"

        // Manual offline override toggle (useful for offline video demos)
        var forceOfflineMode: Boolean = false

        // --- SEEDED DATA: 10 USERS ---
        val seededUsers = CopyOnWriteArrayList<UserDto>(
            listOf(
                UserDto(1, "Sipho Dlamini", "sipho@example.com", "+27 82 123 4567", "Customer"),
                UserDto(2, "Jessica Taylor", "jessica@example.com", "+27 83 987 6543", "Customer"),
                UserDto(3, "David Khumalo", "david@khumalo.co.za", "+27 71 555 0192", "Customer"),
                UserDto(4, "Amanda Naidoo", "amanda@designhub.co.za", "+27 84 333 8811", "Customer"),
                UserDto(5, "Michael Marais", "michael@maraisprints.com", "+27 82 444 2200", "Customer"),
                UserDto(6, "Nomvula Zungu", "nomvula@zungubranding.co.za", "+27 72 888 9911", "Customer"),
                UserDto(7, "Keagan Botha", "keagan@bothasports.co.za", "+27 83 666 1234", "Customer"),
                UserDto(8, "Thabo Mbeki Studio", "info@thabostudio.com", "+27 81 999 4433", "Customer"),
                UserDto(9, "Lisa van der Merwe", "lisa@vdm-events.co.za", "+27 82 777 6655", "Customer"),
                UserDto(10, "Zubair Patel", "zubair@patelautowrap.co.za", "+27 84 111 2233", "Admin/Staff")
            )
        )

        // Seeded User Passwords Hashed (SHA-256 with Salt)
        private val userPasswordHashes = mutableMapOf(
            "sipho@example.com" to SecurityUtils.hashPassword("Password123"),
            "jessica@example.com" to SecurityUtils.hashPassword("SecurePass2026"),
            "david@khumalo.co.za" to SecurityUtils.hashPassword("Password123"),
            "amanda@designhub.co.za" to SecurityUtils.hashPassword("Password123"),
            "michael@maraisprints.com" to SecurityUtils.hashPassword("Password123"),
            "nomvula@zungubranding.co.za" to SecurityUtils.hashPassword("Password123"),
            "keagan@bothasports.co.za" to SecurityUtils.hashPassword("Password123"),
            "info@thabostudio.com" to SecurityUtils.hashPassword("Password123"),
            "lisa@vdm-events.co.za" to SecurityUtils.hashPassword("Password123"),
            "zubair@patelautowrap.co.za" to SecurityUtils.hashPassword("Password123")
        )

        // --- SEEDED DATA: 10 VINYL & PRINT MATERIALS ---
        val seededMaterials = CopyOnWriteArrayList<MaterialDto>(
            listOf(
                MaterialDto(1, "10oz Vinyl (Black, Gold)", "Black/Gold", 120.00, 250, "meters", 20),
                MaterialDto(2, "Gloss Vinyl White (6m)", "White", 150.00, 180, "meters", 15),
                MaterialDto(3, "Matte Vinyl Gold (3m)", "Gold", 180.00, 95, "meters", 10),
                MaterialDto(4, "Standard Banner Vinyl", "White/Yellow", 100.00, 300, "meters", 30),
                MaterialDto(5, "Reflective Safety Vinyl Red", "Red", 210.00, 75, "meters", 10),
                MaterialDto(6, "Transparent Clear Cast Film", "Clear", 160.00, 110, "meters", 15),
                MaterialDto(7, "Brushed Metallic Silver Film", "Silver", 220.00, 60, "meters", 10),
                MaterialDto(8, "High-Tack Wall Vinyl Black", "Black", 135.00, 140, "meters", 20),
                MaterialDto(9, "Perforated Window One-Way Mesh", "White/Black", 195.00, 85, "meters", 15),
                MaterialDto(10, "Fluorescent Neon Yellow Banner", "Neon Yellow", 175.00, 90, "meters", 15)
            )
        )

        // --- SEEDED DATA: 10 ORDERS WITH ITEMS & STATUSES ---
        val seededOrders = CopyOnWriteArrayList<OrderTrackingDto>(
            listOf(
                OrderTrackingDto(
                    orderId = 1,
                    orderRef = "ORD5824",
                    clientName = "Sipho Dlamini",
                    contact = "+27 82 123 4567",
                    status = "Stage 2: Vinyl Cutting & Screen Prep",
                    totalAmount = 340.00,
                    createdAt = "2026-03-29 09:15",
                    items = listOf(
                        OrderItemDto(101, 1, "Legacies Staff Polo Logo", null, 1, "10oz Vinyl (Black, Gold)", 2, 120.00, 240.00),
                        OrderItemDto(102, 1, "Promo Event Banner", null, 4, "Standard Banner Vinyl", 1, 100.00, 100.00)
                    ),
                    userId = 1
                ),
                OrderTrackingDto(
                    orderId = 2,
                    orderRef = "ORD5825",
                    clientName = "Jessica Taylor",
                    contact = "+27 83 987 6543",
                    status = "Stage 3: Screen Printing In Progress",
                    totalAmount = 540.00,
                    createdAt = "2026-03-29 10:30",
                    items = listOf(
                        OrderItemDto(103, 2, "Gold Metallic Wall Decal", null, 3, "Matte Vinyl Gold (3m)", 3, 180.00, 540.00)
                    ),
                    userId = 2
                ),
                OrderTrackingDto(
                    orderId = 3,
                    orderRef = "ORD5826",
                    clientName = "David Khumalo",
                    contact = "+27 71 555 0192",
                    status = "Stage 4: Ready for Collection",
                    totalAmount = 300.00,
                    createdAt = "2026-03-28 14:00",
                    items = listOf(
                        OrderItemDto(104, 3, "Glossy White Storefront Signs", null, 2, "Gloss Vinyl White (6m)", 2, 150.00, 300.00)
                    ),
                    userId = 3
                ),
                OrderTrackingDto(
                    orderId = 4,
                    orderRef = "ORD5827",
                    clientName = "Amanda Naidoo",
                    contact = "+27 84 333 8811",
                    status = "Stage 1: Order Received & Verified",
                    totalAmount = 600.00,
                    createdAt = "2026-03-30 08:00",
                    items = listOf(
                        OrderItemDto(105, 4, "Custom Music Festival Hoodies", null, 1, "10oz Vinyl (Black, Gold)", 5, 120.00, 600.00)
                    ),
                    userId = 4
                ),
                OrderTrackingDto(
                    orderId = 5,
                    orderRef = "ORD5828",
                    clientName = "Michael Marais",
                    contact = "+27 82 444 2200",
                    status = "Stage 2: Vinyl Cutting & Screen Prep",
                    totalAmount = 400.00,
                    createdAt = "2026-03-29 11:45",
                    items = listOf(
                        OrderItemDto(106, 5, "Outdoor Flex Banners 3x1m", null, 4, "Standard Banner Vinyl", 4, 100.00, 400.00)
                    ),
                    userId = 5
                ),
                OrderTrackingDto(
                    orderId = 6,
                    orderRef = "ORD5829",
                    clientName = "Nomvula Zungu",
                    contact = "+27 72 888 9911",
                    status = "Stage 3: Screen Printing In Progress",
                    totalAmount = 360.00,
                    createdAt = "2026-03-29 13:20",
                    items = listOf(
                        OrderItemDto(107, 6, "Matte Gold Luxury Packaging Stickers", null, 3, "Matte Vinyl Gold (3m)", 2, 180.00, 360.00)
                    ),
                    userId = 6
                ),
                OrderTrackingDto(
                    orderId = 7,
                    orderRef = "ORD5830",
                    clientName = "Keagan Botha",
                    contact = "+27 83 666 1234",
                    status = "Stage 4: Ready for Collection",
                    totalAmount = 1200.00,
                    createdAt = "2026-03-27 16:00",
                    items = listOf(
                        OrderItemDto(108, 7, "Sports Team Jersey Names", null, 1, "10oz Vinyl (Black, Gold)", 10, 120.00, 1200.00)
                    ),
                    userId = 7
                ),
                OrderTrackingDto(
                    orderId = 8,
                    orderRef = "ORD5831",
                    clientName = "Thabo Mbeki Studio",
                    contact = "+27 81 999 4433",
                    status = "Stage 1: Order Received & Verified",
                    totalAmount = 450.00,
                    createdAt = "2026-03-30 09:30",
                    items = listOf(
                        OrderItemDto(109, 8, "Exhibition Gloss Banner", null, 2, "Gloss Vinyl White (6m)", 3, 150.00, 450.00)
                    ),
                    userId = 8
                ),
                OrderTrackingDto(
                    orderId = 9,
                    orderRef = "ORD5832",
                    clientName = "Lisa van der Merwe",
                    contact = "+27 82 777 6655",
                    status = "Stage 2: Vinyl Cutting & Screen Prep",
                    totalAmount = 180.00,
                    createdAt = "2026-03-29 15:10",
                    items = listOf(
                        OrderItemDto(110, 9, "Gold Foil Wedding Welcome Sign", null, 3, "Matte Vinyl Gold (3m)", 1, 180.00, 180.00)
                    ),
                    userId = 9
                ),
                OrderTrackingDto(
                    orderId = 10,
                    orderRef = "ORD5833",
                    clientName = "Zubair Patel",
                    contact = "+27 84 111 2233",
                    status = "Stage 3: Screen Printing In Progress",
                    totalAmount = 600.00,
                    createdAt = "2026-03-29 12:00",
                    items = listOf(
                        OrderItemDto(111, 10, "Custom Fleet Vehicle Decals", null, 2, "Gloss Vinyl White (6m)", 4, 150.00, 600.00)
                    ),
                    userId = 10
                )
            )
        )

        private var nextOrderRefCounter = 5834
        private var nextOrderIdCounter = 11
    }

    // --- API METHODS WITH AUTOMATIC NETWORK FALLBACK ---

    // Method for strict offline credential verification against seeded users and SHA-256 salted password hashes
    fun verifyOfflineCredentials(email: String, rawPassword: String): AuthResponse? {
        val trimmedEmail = email.trim()
        val user = seededUsers.find { it.email.equals(trimmedEmail, ignoreCase = true) } ?: return null
        val hashedPass = SecurityUtils.hashPassword(rawPassword)
        val expectedHash = userPasswordHashes[user.email.lowercase()] ?: userPasswordHashes[trimmedEmail.lowercase()]

        return if (expectedHash != null && expectedHash == hashedPass) {
            AuthResponse(user.userId, user.fullName, user.email, user.role, "mock_jwt_token_${user.userId}", user.phoneNumber)
        } else {
            null
        }
    }

    fun authenticate(email: String, rawPassword: String): AuthResponse? {
        return verifyOfflineCredentials(email, rawPassword)
    }

    suspend fun login(email: String, rawPassword: String): AuthResponse {
        if (!forceOfflineMode) {
            try {
                val response = apiService.login(AuthRequest(email.trim(), rawPassword))
                if (response.isSuccessful && response.body() != null) {
                    return response.body()!!
                }
            } catch (e: Exception) {
                Log.w(TAG, "Network login failed, using mock fallback: ${e.message}")
            }
        }

        // Strict Mock Fallback Verification
        return verifyOfflineCredentials(email, rawPassword) ?: AuthResponse(null, null, null, null, null)
    }

    suspend fun register(fullName: String, email: String, phone: String, rawPassword: String): AuthResponse {
        if (!forceOfflineMode) {
            try {
                val hashedPass = SecurityUtils.hashPassword(rawPassword)
                val response = apiService.register(RegisterRequest(fullName, email, phone, hashedPass))
                if (response.isSuccessful && response.body() != null) {
                    return response.body()!!
                }
            } catch (e: Exception) {
                Log.w(TAG, "Network registration failed, using mock fallback: ${e.message}")
            }
        }

        // Mock Fallback
        val newUserId = seededUsers.size + 1
        val newUser = UserDto(newUserId, fullName, email, phone, "Customer")
        seededUsers.add(newUser)
        userPasswordHashes[email.lowercase()] = SecurityUtils.hashPassword(rawPassword)

        return AuthResponse(newUserId, fullName, email, "Customer", "mock_jwt_token_$newUserId", phone)
    }

    suspend fun getMaterials(): List<MaterialDto> {
        if (!forceOfflineMode) {
            try {
                val response = apiService.getMaterials()
                if (response.isSuccessful && response.body() != null) {
                    return response.body()!!
                }
            } catch (e: Exception) {
                Log.w(TAG, "Network getMaterials failed, using mock fallback: ${e.message}")
            }
        }
        return seededMaterials.toList()
    }

    suspend fun createOrder(request: CreateOrderRequest): CreateOrderResponse {
        if (!forceOfflineMode) {
            try {
                val response = apiService.createOrder(request)
                if (response.isSuccessful && response.body() != null) {
                    return response.body()!!
                }
            } catch (e: Exception) {
                Log.w(TAG, "Network createOrder failed, using mock fallback: ${e.message}")
            }
        }

        // Mock Fallback
        val newOrderRef = "ORD${nextOrderRefCounter++}"
        val newOrderId = nextOrderIdCounter++
        val user = seededUsers.find { it.userId == request.userId } ?: seededUsers.first()

        val orderItems = request.items.mapIndexed { idx, itemReq ->
            val mat = seededMaterials.find { it.materialId == itemReq.materialId }
            val unitPrice = mat?.unitPrice ?: 120.00
            OrderItemDto(
                itemId = 200 + newOrderId * 10 + idx,
                orderId = newOrderId,
                designName = itemReq.designName,
                materialId = itemReq.materialId,
                materialName = mat?.materialName ?: "10oz Vinyl",
                quantity = itemReq.quantity,
                unitPrice = unitPrice,
                subtotal = itemReq.quantity * unitPrice
            )
        }

        val totalAmount = orderItems.sumOf { it.subtotal }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

        val newOrderTracking = OrderTrackingDto(
            orderId = newOrderId,
            orderRef = newOrderRef,
            clientName = user.fullName,
            contact = user.phoneNumber,
            status = "Stage 1: Order Received & Verified",
            totalAmount = totalAmount,
            createdAt = dateFormat.format(Date()),
            items = orderItems,
            userId = request.userId
        )

        seededOrders.add(0, newOrderTracking)
        if (context != null) {
            SessionManager.saveCreatedOrder(context, newOrderTracking)
        }

        return CreateOrderResponse(
            message = "Order created successfully (Mock Fallback)",
            orderRef = newOrderRef,
            orderId = newOrderId,
            total = totalAmount
        )
    }

    private fun getCombinedOrders(): List<OrderTrackingDto> {
        if (context == null) return seededOrders.toList()
        val saved = SessionManager.getSavedCreatedOrders(context)
        if (saved.isEmpty()) return seededOrders.toList()
        val savedRefs = saved.map { it.orderRef }.toSet()
        val list = mutableListOf<OrderTrackingDto>()
        list.addAll(saved)
        list.addAll(seededOrders.filter { !savedRefs.contains(it.orderRef) })
        return list
    }

    suspend fun trackOrder(orderRefQuery: String): OrderTrackingDto? {
        if (!forceOfflineMode) {
            try {
                val response = apiService.trackOrder(orderRefQuery.trim())
                if (response.isSuccessful && response.body() != null) {
                    return response.body()!!
                }
            } catch (e: Exception) {
                Log.w(TAG, "Network trackOrder failed, using mock fallback: ${e.message}")
            }
        }

        // Mock Fallback Search
        val trimmed = orderRefQuery.trim()
        val digits = trimmed.filter { it.isDigit() }
        val digitsNoLeadingZero = digits.removePrefix("0")

        return getCombinedOrders().find { order ->
            val contactDigits = order.contact.filter { it.isDigit() }
            order.orderRef.equals(trimmed, ignoreCase = true) ||
                    (digits.isNotEmpty() && (contactDigits.contains(digits) || (digitsNoLeadingZero.isNotEmpty() && contactDigits.contains(digitsNoLeadingZero)))) ||
                    order.clientName.contains(trimmed, ignoreCase = true)
        }
    }

    suspend fun getAllOrders(): List<OrderTrackingDto> {
        if (!forceOfflineMode) {
            try {
                val response = apiService.getAllOrders()
                if (response.isSuccessful && response.body() != null) {
                    return response.body()!!
                }
            } catch (e: Exception) {
                Log.w(TAG, "Network getAllOrders failed, using mock fallback: ${e.message}")
            }
        }
        return getCombinedOrders()
    }

    suspend fun updateStatus(orderId: Int, newStatus: String): Boolean {
        if (!forceOfflineMode) {
            try {
                val response = apiService.updateStatus(orderId, UpdateStatusRequest(newStatus))
                if (response.isSuccessful) {
                    return true
                }
            } catch (e: Exception) {
                Log.w(TAG, "Network updateStatus failed, using mock fallback: ${e.message}")
            }
        }

        // Mock Fallback Update
        val idx = seededOrders.indexOfFirst { it.orderId == orderId }
        if (idx != -1) {
            val existing = seededOrders[idx]
            seededOrders[idx] = existing.copy(status = newStatus)
            return true
        }
        return false
    }
}
