package com.example.legaciesscreenprintersapk.ui.remote.models

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("userId") val userId: Int? = null,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("role") val role: String = "Customer"
)

data class AuthRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("password") val password: String
)

data class AuthResponse(
    @SerializedName("userId") val userId: Int? = null,
    @SerializedName("fullName") val fullName: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("role") val role: String? = null,
    @SerializedName("token") val token: String? = null
)

data class MaterialDto(
    @SerializedName("materialId") val materialId: Int? = null,
    @SerializedName("materialName") val materialName: String,
    @SerializedName("color") val color: String,
    @SerializedName("unitPrice") val unitPrice: Double,
    @SerializedName("stockQuantity") val stockQuantity: Int,
    @SerializedName("unitMeasure") val unitMeasure: String = "meters",
    @SerializedName("lowStockThreshold") val lowStockThreshold: Int = 10
)

data class CreateOrderItemRequest(
    @SerializedName("designName") val designName: String,
    @SerializedName("materialId") val materialId: Int,
    @SerializedName("quantity") val quantity: Int
)

data class CreateOrderRequest(
    @SerializedName("userId") val userId: Int,
    @SerializedName("orderType") val orderType: String = "Remote",
    @SerializedName("items") val items: List<CreateOrderItemRequest>
)

data class CreateOrderResponse(
    @SerializedName("message") val message: String? = null,
    @SerializedName("orderRef") val orderRef: String? = null,
    @SerializedName("orderId") val orderId: Int? = null,
    @SerializedName("total") val total: Double? = null
)

data class OrderItemDto(
    @SerializedName("itemId") val itemId: Int? = null,
    @SerializedName("orderId") val orderId: Int? = null,
    @SerializedName("designName") val designName: String,
    @SerializedName("artworkFileUrl") val artworkFileUrl: String? = null,
    @SerializedName("materialId") val materialId: Int? = null,
    @SerializedName("materialName") val materialName: String? = null,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("unitPrice") val unitPrice: Double,
    @SerializedName("subtotal") val subtotal: Double = quantity * unitPrice
)

data class OrderTrackingDto(
    @SerializedName("orderId") val orderId: Int? = null,
    @SerializedName("orderRef") val orderRef: String,
    @SerializedName("clientName") val clientName: String,
    @SerializedName("contact") val contact: String,
    @SerializedName("status") val status: String,
    @SerializedName("totalAmount") val totalAmount: Double,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("items") val items: List<OrderItemDto>,
    @SerializedName("userId") val userId: Int? = null
)

data class UpdateStatusRequest(
    @SerializedName("status") val status: String
)
