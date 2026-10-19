package com.example.legaciesscreenprintersapk.ui.remote

import com.example.legaciesscreenprintersapk.data.Order
import com.example.legaciesscreenprintersapk.data.VinylMaterial
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import java.util.concurrent.TimeUnit

data class LoginRequest(
    val email: String,
    val passwordHash: String,
)

data class RegisterRequest(
    val name: String,
    val phone: String,
    val email: String,
    val passwordHash: String,
)

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val token: String? = null,
    val userId: String? = null,
    val userName: String? = null,
)

data class CreateOrderRequest(
    val clientName: String,
    val clientPhone: String,
    val clientEmail: String,
    val items: List<OrderItemDto>,
    val isRemoteOrder: Boolean = true,
)

data class OrderItemDto(
    val designName: String,
    val materialName: String,
    val artworkUri: String?,
    val quantity: Int,
    val unitPrice: Double,
)

data class OrderResponse(
    val success: Boolean,
    val message: String,
    val orderId: String? = null,
    val order: Order? = null,
)

interface ApiService {
    @POST("api/auth/login")
    suspend fun loginUser(@Body request: LoginRequest): Response<AuthResponse>

    @POST("api/auth/register")
    suspend fun registerUser(@Body request: RegisterRequest): Response<AuthResponse>

    @GET("api/materials")
    suspend fun getMaterials(): Response<List<VinylMaterial>>

    @POST("api/orders/create")
    suspend fun createOrder(@Body request: CreateOrderRequest): Response<OrderResponse>

    @GET("api/orders/track/{orderId}")
    suspend fun trackOrder(@Path("orderId") orderId: String): Response<OrderResponse>

    companion object {
        private const val BASE_URL = "http://10.0.2.2:5000/"

        fun create(): ApiService {
            val logging = HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            }

            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .connectTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)
                .build()

            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

            return retrofit.create(ApiService::class.java)
        }
    }
}
