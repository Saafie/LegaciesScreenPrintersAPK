package com.example.legaciesscreenprintersapk

import com.example.legaciesscreenprintersapk.ui.remote.ApiResult
import com.example.legaciesscreenprintersapk.ui.remote.ApiService
import com.example.legaciesscreenprintersapk.ui.remote.OrderRepository
import com.example.legaciesscreenprintersapk.ui.remote.OrdersCache
import com.example.legaciesscreenprintersapk.ui.remote.models.AuthRequest
import com.example.legaciesscreenprintersapk.ui.remote.models.AuthResponse
import com.example.legaciesscreenprintersapk.ui.remote.models.CreateOrderItemRequest
import com.example.legaciesscreenprintersapk.ui.remote.models.CreateOrderRequest
import com.example.legaciesscreenprintersapk.ui.remote.models.CreateOrderResponse
import com.example.legaciesscreenprintersapk.ui.remote.models.GoogleLoginRequest
import com.example.legaciesscreenprintersapk.ui.remote.models.MaterialDto
import com.example.legaciesscreenprintersapk.ui.remote.models.OrderTrackingDto
import com.example.legaciesscreenprintersapk.ui.remote.models.RegisterRequest
import com.example.legaciesscreenprintersapk.ui.remote.models.UpdateStatusRequest
import kotlinx.coroutines.runBlocking
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import retrofit2.Response
import java.io.IOException

/** Tests the repository with a fake API. */
class OrderRepositoryTest {

    private val order = OrderTrackingDto(
        orderId = 1, orderRef = "ORD5824", clientName = "Demo", contact = "082", status = "Screen Printing",
        totalAmount = 450.0, createdAt = "2026-11-15T17:43:40Z", items = emptyList(), userId = 2,
    )

    /** Answers with whatever the test sets; `offline = true` simulates no network. */
    private class FakeApi : ApiService {
        var offline = false
        var materials: Response<List<MaterialDto>> = Response.success(emptyList())
        var track: Response<OrderTrackingDto>? = null
        var mine: Response<List<OrderTrackingDto>> = Response.success(emptyList())
        var create: Response<CreateOrderResponse>? = null

        private fun <T> answer(r: Response<T>?): Response<T> {
            if (offline) throw IOException("No network")
            return r ?: error("not set")
        }

        override suspend fun getMaterials() = answer(materials)
        override suspend fun trackOrder(orderRef: String) = answer(track)
        override suspend fun getMyOrders() = answer(mine)
        override suspend fun createOrder(request: CreateOrderRequest) = answer(create)
        override suspend fun register(request: RegisterRequest): Response<AuthResponse> = error("unused")
        override suspend fun login(request: AuthRequest): Response<AuthResponse> = error("unused")
        override suspend fun googleLogin(request: GoogleLoginRequest): Response<AuthResponse> = error("unused")
        override suspend fun getAllOrders(): Response<List<OrderTrackingDto>> = error("unused")
        override suspend fun updateStatus(orderId: Int, request: UpdateStatusRequest): Response<Unit> = error("unused")
    }

    private class MemoryCache : OrdersCache {
        var saved: Pair<List<OrderTrackingDto>, Long>? = null
        override fun save(orders: List<OrderTrackingDto>, savedAtMillis: Long) { saved = orders to savedAtMillis }
        override fun load() = saved
    }

    private fun <T> error(code: Int, json: String): Response<T> =
        Response.error(code, json.toResponseBody("application/json".toMediaType()))

    @Test
    fun materialsComeFromTheApi() = runBlocking {
        val api = FakeApi().apply { materials = Response.success(listOf(MaterialDto(1, "Gloss Vinyl", "White", 45.0, 120))) }
        val result = OrderRepository(api).getMaterials()
        assertTrue(result is ApiResult.Success)
        assertEquals(45.0, (result as ApiResult.Success).data.single().unitPrice, 0.001)
    }

    @Test
    fun offlineMaterialsAreAnErrorNotInventedData() = runBlocking {
        val result = OrderRepository(FakeApi().apply { offline = true }).getMaterials()
        assertTrue(result is ApiResult.NetworkError)
    }

    @Test
    fun trackNotFoundCarriesTheServersMessage() = runBlocking {
        val api = FakeApi().apply { track = error(404, """{"message":"Order reference not found."}""") }
        val result = OrderRepository(api).trackOrder("ord0000")
        assertEquals(ApiResult.HttpError(404, "Order reference not found."), result)
    }

    @Test
    fun outOfStockOrderShowsTheReason() = runBlocking {
        val api = FakeApi().apply { create = error(409, """{"message":"Not enough Matte Gold in stock for 99 meters."}""") }
        val result = OrderRepository(api).createOrder(CreateOrderRequest(2, "mobile", listOf(CreateOrderItemRequest("X", 3, 99))))
        assertEquals(ApiResult.HttpError(409, "Not enough Matte Gold in stock for 99 meters."), result)
    }

    @Test
    fun myOrdersOnlineAreSavedForLater() = runBlocking {
        val cache = MemoryCache()
        val api = FakeApi().apply { mine = Response.success(listOf(order)) }
        val result = OrderRepository(api, cache, clock = { 1_000L }).getMyOrders() as ApiResult.Success
        assertNull(result.data.offlineSavedAtMillis)
        assertEquals(listOf(order) to 1_000L, cache.saved)
    }

    @Test
    fun myOrdersOfflineShowSavedCopyWithItsTime() = runBlocking {
        val cache = MemoryCache().apply { saved = listOf(order) to 1_000L }
        val result = OrderRepository(FakeApi().apply { offline = true }, cache).getMyOrders() as ApiResult.Success
        assertEquals(listOf(order), result.data.orders)
        assertEquals(1_000L, result.data.offlineSavedAtMillis)
    }

    @Test
    fun myOrdersOfflineWithNothingSavedIsAnError() = runBlocking {
        val result = OrderRepository(FakeApi().apply { offline = true }, MemoryCache()).getMyOrders()
        assertTrue(result is ApiResult.NetworkError)
    }
}
