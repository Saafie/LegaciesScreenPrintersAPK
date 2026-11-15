package com.example.legaciesscreenprintersapk

import com.example.legaciesscreenprintersapk.ui.remote.AuthInterceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

/** Session handling in the network layer, tested against a local fake server (MockWebServer). */
class AuthInterceptorTest {

    private lateinit var server: MockWebServer
    private var token: String? = "test-jwt"
    private val rejections = mutableListOf<String>()

    private val client by lazy {
        OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenProvider = { token }, onSessionRejected = { rejections += it }))
            .build()
    }

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    private fun get(path: String) =
        client.newCall(Request.Builder().url(server.url(path)).build()).execute().use { it.code }

    @Test
    fun addsBearerTokenToApiCalls() {
        server.enqueue(MockResponse().setResponseCode(200))
        get("/api/orders/mine")
        assertEquals("Bearer test-jwt", server.takeRequest().getHeader("Authorization"))
    }

    @Test
    fun signInCallsAreSentWithoutToken() {
        server.enqueue(MockResponse().setResponseCode(200))
        get("/api/auth/login")
        assertNull(server.takeRequest().getHeader("Authorization"))
    }

    @Test
    fun meEndpointStillGetsToken() {
        server.enqueue(MockResponse().setResponseCode(200))
        get("/api/auth/me")
        assertEquals("Bearer test-jwt", server.takeRequest().getHeader("Authorization"))
    }

    @Test
    fun noTokenMeansNoHeader() {
        token = null
        server.enqueue(MockResponse().setResponseCode(200))
        get("/api/materials")
        assertNull(server.takeRequest().getHeader("Authorization"))
    }

    @Test
    fun unauthorizedOnSignedInCallEndsSessionWithServerReason() {
        server.enqueue(
            MockResponse().setResponseCode(401)
                .setBody("""{"message":"Your session has expired. Please sign in again.","code":"token_expired"}""")
        )
        assertEquals(401, get("/api/orders/mine"))
        assertEquals(listOf("Your session has expired. Please sign in again."), rejections)
    }

    @Test
    fun wrongPasswordDoesNotTriggerSessionExpiry() {
        server.enqueue(MockResponse().setResponseCode(401).setBody("""{"message":"Invalid email or password."}"""))
        get("/api/auth/login")
        assertEquals(emptyList<String>(), rejections)
    }

    @Test
    fun forbiddenDoesNotEndSession() {
        server.enqueue(MockResponse().setResponseCode(403))
        get("/api/orders")
        assertEquals(emptyList<String>(), rejections)
    }
}
