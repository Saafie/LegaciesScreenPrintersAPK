package com.example.legaciesscreenprintersapk.ui.remote

import android.util.Log
import com.example.legaciesscreenprintersapk.ui.remote.models.ApiError
import com.example.legaciesscreenprintersapk.ui.remote.models.CreateOrderRequest
import com.example.legaciesscreenprintersapk.ui.remote.models.CreateOrderResponse
import com.example.legaciesscreenprintersapk.ui.remote.models.MaterialDto
import com.example.legaciesscreenprintersapk.ui.remote.models.OrderTrackingDto
import com.google.gson.Gson
import retrofit2.Response
import java.io.IOException

/** Result of an API call: the data, a server error, or no connection. */
sealed interface ApiResult<out T> {
    data class Success<T>(val data: T) : ApiResult<T>

    /** The server answered with an error (e.g. 404 not found, 409 out of stock). */
    data class HttpError(val code: Int, val message: String) : ApiResult<Nothing>

    /** No answer from the server (no internet, server down, timeout). */
    data class NetworkError(val message: String) : ApiResult<Nothing>
}

/** Where the "my orders" list came from: live from the API, or the copy saved on the phone. */
data class OrdersResult(
    val orders: List<OrderTrackingDto>,
    /** Non-null when showing the saved copy because the server couldn't be reached. */
    val offlineSavedAtMillis: Long? = null,
)

/** Saves the last downloaded "my orders" list for offline use. */
interface OrdersCache {
    fun save(orders: List<OrderTrackingDto>, savedAtMillis: Long)
    fun load(): Pair<List<OrderTrackingDto>, Long>?
}

/**
 * Gets orders and materials from the API.
 * Reference: Android Developers (2025) Data layer (app architecture) - https://developer.android.com/topic/architecture/data-layer
 */
class OrderRepository(
    private val api: ApiService = RetrofitClient.apiService,
    private val cache: OrdersCache? = null,
    private val clock: () -> Long = System::currentTimeMillis,
) {

    suspend fun getMaterials(): ApiResult<List<MaterialDto>> = call("getMaterials") { api.getMaterials() }

    suspend fun trackOrder(orderRef: String): ApiResult<OrderTrackingDto> =
        call("trackOrder") { api.trackOrder(orderRef.trim().uppercase()) }

    suspend fun createOrder(request: CreateOrderRequest): ApiResult<CreateOrderResponse> =
        call("createOrder") { api.createOrder(request) }

    /** The signed-in customer's orders. */
    suspend fun getMyOrders(): ApiResult<OrdersResult> {
        return when (val result = call("getMyOrders") { api.getMyOrders() }) {
            is ApiResult.Success -> {
                cache?.save(result.data, clock())
                ApiResult.Success(OrdersResult(result.data))
            }
            is ApiResult.HttpError -> result
            is ApiResult.NetworkError -> {
                val saved = cache?.load()
                if (saved != null) {
                    Log.i(TAG, "Offline: showing ${saved.first.size} saved order(s).")
                    ApiResult.Success(OrdersResult(saved.first, offlineSavedAtMillis = saved.second))
                } else {
                    result
                }
            }
        }
    }

    /** Runs one API call and turns the response into an ApiResult. */
    private suspend fun <T> call(name: String, request: suspend () -> Response<T>): ApiResult<T> {
        return try {
            val response = request()
            val body = response.body()
            when {
                response.isSuccessful && body != null -> ApiResult.Success(body)
                response.isSuccessful -> ApiResult.HttpError(response.code(), "The server sent an empty response.")
                else -> {
                    val message = errorMessage(response)
                    Log.w(TAG, "$name failed: HTTP ${response.code()}")
                    ApiResult.HttpError(response.code(), message)
                }
            }
        } catch (e: IOException) {
            Log.w(TAG, "$name failed: server unreachable (${e.javaClass.simpleName}).")
            ApiResult.NetworkError("Can't reach the server. Check your internet connection and try again.")
        }
    }

    private fun errorMessage(response: Response<*>): String {
        val apiMessage = try {
            response.errorBody()?.string()?.let { Gson().fromJson(it, ApiError::class.java)?.message }
        } catch (_: Exception) {
            null
        }
        return apiMessage ?: when (response.code()) {
            401 -> "Your session has ended. Please sign in again."
            403 -> "You don't have permission to do that."
            404 -> "Not found."
            in 500..599 -> "The server had a problem. Please try again shortly."
            else -> "Something went wrong (error ${response.code()})."
        }
    }

    private companion object {
        const val TAG = "LSP/Repo"
    }
}
