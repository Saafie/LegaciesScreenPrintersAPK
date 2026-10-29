package com.example.legaciesscreenprintersapk.ui.remote

import com.example.legaciesscreenprintersapk.ui.remote.models.*
import retrofit2.Response
import retrofit2.http.*

interface ApiService {
    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @GET("api/orders")
    suspend fun getAllOrders(): Response<List<OrderResponse>>

    @GET("api/orders/track/{trackingNumber}")
    suspend fun trackOrder(@Path("trackingNumber") trackingNumber: String): Response<OrderResponse>

    @GET("api/orders/user/{userId}")
    suspend fun getUserOrders(@Path("userId") userId: Int): Response<List<OrderResponse>>

    @POST("api/orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): Response<OrderResponse>
}