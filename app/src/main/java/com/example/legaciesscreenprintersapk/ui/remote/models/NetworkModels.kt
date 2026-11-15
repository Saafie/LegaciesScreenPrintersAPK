package com.example.legaciesscreenprintersapk.ui.remote.models

import com.google.gson.annotations.SerializedName

// JSON shapes exchanged with the Legacies Printers API.

/** The signed-in user's details as the app displays them. */
data class UserDto(
    @SerializedName("userId") val userId: Int? = null,
    @SerializedName("fullName") val fullName: String,
    @SerializedName("email") val email: String,
    @SerializedName("phoneNumber") val phoneNumber: String,
    @SerializedName("role") val role: String = "customer"
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

/** Body for POST api/auth/google: the ID token Google gave the app. */
data class GoogleLoginRequest(
    @SerializedName("idToken") val idToken: String
)

/** Returned by register, login and Google sign-in. */
data class AuthResponse(
    @SerializedName("userId") val userId: Int? = null,
    @SerializedName("fullName") val fullName: String? = null,
    @SerializedName("email") val email: String? = null,
    @SerializedName("role") val role: String? = null,
    @SerializedName("token") val token: String? = null,
    @SerializedName("phoneNumber") val phoneNumber: String? = null,
    @SerializedName("expiresAt") val expiresAt: String? = null
)

/** Error body the API sends with 4xx responses, e.g. {"message":"...","code":"token_expired"}. */
data class ApiError(
    @SerializedName("message") val message: String? = null,
    @SerializedName("code") val code: String? = null
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

/** Body for POST api/orders. */
data class CreateOrderRequest(
    @SerializedName("userId") val userId: Int,
    @SerializedName("orderType") val orderType: String = "mobile",
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
    @SerializedName("userId") val userId: Int? = null,
    /** Where it was placed: "mobile" (this app), "web" (the website) or "walk_in" (shop counter). */
    @SerializedName("orderType") val orderType: String? = null
)

data class UpdateStatusRequest(
    @SerializedName("status") val status: String
)
