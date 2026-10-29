package com.example.legaciesscreenprintersapk.ui.remote.models

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String
)

data class RegisterRequest(
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("password") val password: String,
    @SerializedName("phone") val phone: String? = null
)

data class AuthResponse(
    @SerializedName("userId") val userId: Int,
    @SerializedName("name") val name: String,
    @SerializedName("email") val email: String,
    @SerializedName("role") val role: String,
    @SerializedName("token") val token: String
)

data class CreateOrderItemRequest(
    @SerializedName("itemName") val itemName: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("unitPrice") val unitPrice: Double,
    @SerializedName("printColor") val printColor: String?,
    @SerializedName("size") val size: String?
)

data class CreateOrderRequest(
    @SerializedName("userId") val userId: Int,
    @SerializedName("deliveryAddress") val deliveryAddress: String?,
    @SerializedName("items") val items: List<CreateOrderItemRequest>
)

data class OrderItemResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("orderId") val orderId: Int,
    @SerializedName("itemName") val itemName: String,
    @SerializedName("quantity") val quantity: Int,
    @SerializedName("unitPrice") val unitPrice: Double,
    @SerializedName("printColor") val printColor: String?,
    @SerializedName("size") val size: String?
)

data class OrderResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("userId") val userId: Int,
    @SerializedName("trackingNumber") val trackingNumber: String,
    @SerializedName("status") val status: String,
    @SerializedName("totalAmount") val totalAmount: Double,
    @SerializedName("deliveryAddress") val deliveryAddress: String?,
    @SerializedName("createdAt") val createdAt: String,
    @SerializedName("orderItems") val orderItems: List<OrderItemResponse> = emptyList()
)