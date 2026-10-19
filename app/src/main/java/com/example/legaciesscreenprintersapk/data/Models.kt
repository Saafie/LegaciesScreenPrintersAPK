package com.example.legaciesscreenprintersapk.data

data class User(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val passwordHash: String
)

data class VinylMaterial(
    val id: String,
    val name: String,
    val unitPrice: Double,
    val description: String,
    val colors: List<String> = emptyList(),
    val inStockMeters: Double = 100.0
)

data class OrderItem(
    val id: String,
    val designName: String,
    val materialName: String,
    val artworkUri: String? = null,
    val quantity: Int,
    val unitPrice: Double,
    val totalPrice: Double = unitPrice * quantity
)

data class Order(
    val id: String,
    val clientName: String,
    val clientPhone: String,
    val clientEmail: String,
    val items: List<OrderItem>,
    val totalAmount: Double,
    val currentStageIndex: Int, // 0..3
    val statusText: String,
    val estimatedCompletion: String,
    val createdAt: String,
    val isRemoteOrder: Boolean = true
)

data class TrackingStage(
    val stageNumber: Int,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val isCurrent: Boolean
)
