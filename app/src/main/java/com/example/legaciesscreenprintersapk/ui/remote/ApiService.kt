package com.example.legaciesscreenprintersapk.ui.remote

import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

data class LoginRequest(val email: String, val passwordHash: String)
data class AuthResponse(val success: Boolean, val message: String, val token: String?)

data class OrderRequest(val customerId: Int, val designName: String, val materialName: String, val quantity: Int)
data class OrderResponse(val success: Boolean, val message: String, val orderId: Int?)

interface ApiService {
    @POST("api/auth/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/orders/create")
    suspend fun createOrder(@Body request: OrderRequest): Response<OrderResponse>

    companion object {
        private const val BASE_URL = "http://10.0.2.2:5000/"

        fun create(): ApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ApiService::class.java)
        }
    }
}