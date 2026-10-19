package com.example.legaciesscreenprintersapk.data

import com.example.legaciesscreenprintersapk.util.SecurityUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.concurrent.CopyOnWriteArrayList

object MockRepository {

    // Seeded Vinyl Materials
    val standardMaterials = listOf(
        VinylMaterial(
            id = "MAT01",
            name = "10oz Vinyl (Black, Gold)",
            unitPrice = 120.00,
            description = "Heavy-duty 10oz outdoor vinyl with rich black/gold finish.",
            colors = listOf("Black", "Gold"),
            inStockMeters = 250.0
        ),
        VinylMaterial(
            id = "MAT02",
            name = "Gloss Vinyl White (6m)",
            unitPrice = 150.00,
            description = "High-gloss bright white premium cast vinyl roll.",
            colors = listOf("White"),
            inStockMeters = 180.0
        ),
        VinylMaterial(
            id = "MAT03",
            name = "Matte Vinyl Gold (3m)",
            unitPrice = 180.00,
            description = "Ultra-matte metallic gold film for premium branding.",
            colors = listOf("Gold"),
            inStockMeters = 95.0
        ),
        VinylMaterial(
            id = "MAT04",
            name = "Standard Banner Vinyl",
            unitPrice = 100.00,
            description = "Durable flex banner substrate suitable for wide format printing.",
            colors = listOf("White", "Yellow"),
            inStockMeters = 300.0
        )
    )

    // Seeded Mock Users
    private val mockUsers = CopyOnWriteArrayList<User>(
        listOf(
            User(
                id = "USR101",
                name = "Sipho Dlamini",
                email = "sipho@example.com",
                phone = "+27 82 123 4567",
                passwordHash = SecurityUtils.hashPassword("Password123")
            ),
            User(
                id = "USR102",
                name = "Jessica Taylor",
                email = "jessica@example.com",
                phone = "+27 83 987 6543",
                passwordHash = SecurityUtils.hashPassword("SecurePass2026")
            )
        )
    )

    // Seeded Mock Orders (10 realistic records)
    private val mockOrders = CopyOnWriteArrayList<Order>(
        listOf(
            Order(
                id = "ORD5824",
                clientName = "Sipho Dlamini",
                clientPhone = "+27 82 123 4567",
                clientEmail = "sipho@example.com",
                items = listOf(
                    OrderItem("ITM101", "Legacies Staff Polo Logo", "10oz Vinyl (Black, Gold)", null, 2, 120.00, 240.00),
                    OrderItem("ITM102", "Promo Event Banner", "Standard Banner Vinyl", null, 1, 100.00, 100.00)
                ),
                totalAmount = 340.00,
                currentStageIndex = 1, // Vinyl Cutting & Screen Prep
                statusText = "CURRENT STATUS: VINYL CUTTING & PREPARATION",
                estimatedCompletion = "Estimated Completion: Today at 15:30",
                createdAt = "2026-03-29 09:15",
                isRemoteOrder = true
            ),
            Order(
                id = "ORD5825",
                clientName = "Jessica Taylor",
                clientPhone = "+27 83 987 6543",
                clientEmail = "jessica@example.com",
                items = listOf(
                    OrderItem("ITM103", "Gold Metallic Wall Decal", "Matte Vinyl Gold (3m)", null, 3, 180.00, 540.00)
                ),
                totalAmount = 540.00,
                currentStageIndex = 2, // Screen Printing
                statusText = "CURRENT STATUS: SCREEN PRINTING IN PROGRESS",
                estimatedCompletion = "Estimated Completion: Tomorrow at 11:00",
                createdAt = "2026-03-29 10:30",
                isRemoteOrder = true
            ),
            Order(
                id = "ORD5826",
                clientName = "David Khumalo",
                clientPhone = "+27 71 555 0192",
                clientEmail = "david@khumalo.co.za",
                items = listOf(
                    OrderItem("ITM104", "Glossy White Storefront Signs", "Gloss Vinyl White (6m)", null, 2, 150.00, 300.00)
                ),
                totalAmount = 300.00,
                currentStageIndex = 3, // Ready for Collection
                statusText = "CURRENT STATUS: READY FOR COLLECTION AT WORKSHOP",
                estimatedCompletion = "Ready Now for Counter Pickup",
                createdAt = "2026-03-28 14:00",
                isRemoteOrder = false
            ),
            Order(
                id = "ORD5827",
                clientName = "Amanda Naidoo",
                clientPhone = "+27 84 333 8811",
                clientEmail = "amanda@designhub.co.za",
                items = listOf(
                    OrderItem("ITM105", "Custom Music Festival Hoodies", "10oz Vinyl (Black, Gold)", null, 5, 120.00, 600.00)
                ),
                totalAmount = 600.00,
                currentStageIndex = 0, // Order Received & Verified
                statusText = "CURRENT STATUS: ORDER RECEIVED & VERIFIED",
                estimatedCompletion = "Estimated Completion: In 2 Business Days",
                createdAt = "2026-03-30 08:00",
                isRemoteOrder = true
            ),
            Order(
                id = "ORD5828",
                clientName = "Michael Marais",
                clientPhone = "+27 82 444 2200",
                clientEmail = "michael@maraisprints.com",
                items = listOf(
                    OrderItem("ITM106", "Outdoor Flex Banners 3x1m", "Standard Banner Vinyl", null, 4, 100.00, 400.00)
                ),
                totalAmount = 400.00,
                currentStageIndex = 1,
                statusText = "CURRENT STATUS: VINYL CUTTING & PREPARATION",
                estimatedCompletion = "Estimated Completion: Tomorrow at 16:00",
                createdAt = "2026-03-29 11:45",
                isRemoteOrder = true
            ),
            Order(
                id = "ORD5829",
                clientName = "Nomvula Zungu",
                clientPhone = "+27 72 888 9911",
                clientEmail = "nomvula@zungubranding.co.za",
                items = listOf(
                    OrderItem("ITM107", "Matte Gold Luxury Packaging Stickers", "Matte Vinyl Gold (3m)", null, 2, 180.00, 360.00)
                ),
                totalAmount = 360.00,
                currentStageIndex = 2,
                statusText = "CURRENT STATUS: SCREEN PRINTING IN PROGRESS",
                estimatedCompletion = "Estimated Completion: Today at 17:00",
                createdAt = "2026-03-29 13:20",
                isRemoteOrder = false
            ),
            Order(
                id = "ORD5830",
                clientName = "Keagan Botha",
                clientPhone = "+27 83 666 1234",
                clientEmail = "keagan@bothasports.co.za",
                items = listOf(
                    OrderItem("ITM108", "Sports Team Jersey Names", "10oz Vinyl (Black, Gold)", null, 10, 120.00, 1200.00)
                ),
                totalAmount = 1200.00,
                currentStageIndex = 3,
                statusText = "CURRENT STATUS: READY FOR COLLECTION AT WORKSHOP",
                estimatedCompletion = "Ready Now for Pickup",
                createdAt = "2026-03-27 16:00",
                isRemoteOrder = true
            ),
            Order(
                id = "ORD5831",
                clientName = "Thabo Mbeki Studio",
                clientPhone = "+27 81 999 4433",
                clientEmail = "info@thabostudio.com",
                items = listOf(
                    OrderItem("ITM109", "Exhibition Gloss Banner", "Gloss Vinyl White (6m)", null, 3, 150.00, 450.00)
                ),
                totalAmount = 450.00,
                currentStageIndex = 0,
                statusText = "CURRENT STATUS: ORDER RECEIVED & VERIFIED",
                estimatedCompletion = "Estimated Completion: In 3 Business Days",
                createdAt = "2026-03-30 09:30",
                isRemoteOrder = true
            ),
            Order(
                id = "ORD5832",
                clientName = "Lisa van der Merwe",
                clientPhone = "+27 82 777 6655",
                clientEmail = "lisa@vdm-events.co.za",
                items = listOf(
                    OrderItem("ITM110", "Gold Foil Wedding Welcome Sign", "Matte Vinyl Gold (3m)", null, 1, 180.00, 180.00)
                ),
                totalAmount = 180.00,
                currentStageIndex = 1,
                statusText = "CURRENT STATUS: VINYL CUTTING & PREPARATION",
                estimatedCompletion = "Estimated Completion: Tomorrow at 14:00",
                createdAt = "2026-03-29 15:10",
                isRemoteOrder = true
            ),
            Order(
                id = "ORD5833",
                clientName = "Zubair Patel",
                clientPhone = "+27 84 111 2233",
                clientEmail = "zubair@patelautowrap.co.za",
                items = listOf(
                    OrderItem("ITM111", "Custom Fleet Vehicle Decals", "Gloss Vinyl White (6m)", null, 4, 150.00, 600.00)
                ),
                totalAmount = 600.00,
                currentStageIndex = 2,
                statusText = "CURRENT STATUS: SCREEN PRINTING IN PROGRESS",
                estimatedCompletion = "Estimated Completion: Today at 16:45",
                createdAt = "2026-03-29 12:00",
                isRemoteOrder = false
            )
        )
    )

    private var nextOrderNumber = 5834

    fun getOrders(): List<Order> = mockOrders.toList()

    fun searchOrders(query: String): List<Order> {
        val trimmed = query.trim()
        if (trimmed.isEmpty()) return getOrders()
        val digitsQuery = trimmed.filter { it.isDigit() }
        val digitsNoLeadingZero = digitsQuery.removePrefix("0")

        return mockOrders.filter { order ->
            val phoneDigits = order.clientPhone.filter { it.isDigit() }
            order.id.equals(trimmed, ignoreCase = true) ||
                    (digitsQuery.isNotEmpty() && (phoneDigits.contains(digitsQuery) || (digitsNoLeadingZero.isNotEmpty() && phoneDigits.contains(digitsNoLeadingZero)))) ||
                    order.clientPhone.contains(trimmed, ignoreCase = true) ||
                    order.clientName.contains(trimmed, ignoreCase = true) ||
                    order.clientEmail.contains(trimmed, ignoreCase = true)
        }
    }

    fun findOrderById(orderId: String): Order? {
        val trimmed = orderId.trim()
        return mockOrders.find { it.id.equals(trimmed, ignoreCase = true) }
    }

    fun createOrder(
        clientName: String,
        clientPhone: String,
        clientEmail: String,
        items: List<OrderItem>,
        isRemoteOrder: Boolean = true
    ): Order {
        val orderId = "ORD${nextOrderNumber++}"
        val total = items.sumOf { it.totalPrice }
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
        val currentDate = dateFormat.format(Date())

        val newOrder = Order(
            id = orderId,
            clientName = clientName,
            clientPhone = clientPhone,
            clientEmail = clientEmail,
            items = items,
            totalAmount = total,
            currentStageIndex = 0,
            statusText = "CURRENT STATUS: ORDER RECEIVED & VERIFIED",
            estimatedCompletion = "Estimated Completion: In 2 Business Days",
            createdAt = currentDate,
            isRemoteOrder = isRemoteOrder
        )

        mockOrders.add(0, newOrder)
        return newOrder
    }

    fun getTrackingStages(currentStageIndex: Int): List<TrackingStage> {
        val stageDefinitions = listOf(
            Pair("Order Received & Verified", "Artwork and dimensions verified by prepress"),
            Pair("Vinyl Cutting & Screen Prep", "Precision plotter cutting vinyl & burning screens"),
            Pair("Screen Printing", "Multi-pass ink application & tunnel curing"),
            Pair("Ready for Collection", "Quality checked and packaged at workshop counter")
        )

        return stageDefinitions.mapIndexed { index, (title, desc) ->
            TrackingStage(
                stageNumber = index + 1,
                title = title,
                description = desc,
                isCompleted = index <= currentStageIndex,
                isCurrent = index == currentStageIndex
            )
        }
    }

    fun registerUser(name: String, phone: String, email: String, passwordHash: String): User {
        val existing = mockUsers.find { it.email.equals(email, ignoreCase = true) }
        if (existing != null) {
            return existing
        }
        val newUser = User(
            id = "USR${System.currentTimeMillis() % 10000}",
            name = name,
            phone = phone,
            email = email,
            passwordHash = passwordHash
        )
        mockUsers.add(newUser)
        return newUser
    }

    fun loginUser(email: String, passwordHash: String): User? {
        return mockUsers.find {
            it.email.equals(email, ignoreCase = true) && it.passwordHash == passwordHash
        }
    }
}
