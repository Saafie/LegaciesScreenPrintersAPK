package com.example.legaciesscreenprintersapk.ui.remote

import com.example.legaciesscreenprintersapk.ui.remote.models.AuthRequest
import com.example.legaciesscreenprintersapk.ui.remote.models.AuthResponse
import com.example.legaciesscreenprintersapk.ui.remote.models.CreateOrderRequest
import com.example.legaciesscreenprintersapk.ui.remote.models.CreateOrderResponse
import com.example.legaciesscreenprintersapk.ui.remote.models.MaterialDto
import com.example.legaciesscreenprintersapk.ui.remote.models.OrderTrackingDto
import com.example.legaciesscreenprintersapk.ui.remote.models.RegisterRequest
import com.example.legaciesscreenprintersapk.ui.remote.models.UpdateStatusRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: AuthRequest): Response<AuthResponse>

    @GET("api/materials")
    suspend fun getMaterials(): Response<List<MaterialDto>>

    @POST("api/orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): Response<CreateOrderResponse>

    @GET("api/orders/track/{orderRef}")
    suspend fun trackOrder(@Path("orderRef") orderRef: String): Response<OrderTrackingDto>

    @GET("api/orders")
    suspend fun getAllOrders(): Response<List<OrderTrackingDto>>

    @PUT("api/orders/{orderId}/status")
    suspend fun updateStatus(@Path("orderId") orderId: Int, @Body request: UpdateStatusRequest): Response<Unit>
}
