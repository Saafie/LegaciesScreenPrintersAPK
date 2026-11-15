package com.example.legaciesscreenprintersapk.ui.remote

import android.content.Context
import com.example.legaciesscreenprintersapk.BuildConfig
import com.example.legaciesscreenprintersapk.util.SessionEvents
import com.example.legaciesscreenprintersapk.util.SessionManager
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/** Single shared Retrofit client for the Legacies Printers API. */
object RetrofitClient {

    // Set per build type in app/build.gradle.kts.
    private val BASE_URL = BuildConfig.API_BASE_URL

    @Volatile
    private var session: SessionManager? = null

    /** Connects the client to the stored session. Safe to call more than once. */
    fun init(context: Context) {
        if (session == null) session = SessionManager(context.applicationContext)
    }

    private val authInterceptor = AuthInterceptor(
        tokenProvider = { session?.getAuthToken() },
        onSessionRejected = { reason ->
            session?.clearSession()
            SessionEvents.notifyExpired(reason)
        },
    )

    // Only logs the URL and status in debug builds (never passwords or tokens).
    private val logging = HttpLoggingInterceptor().apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
        redactHeader("Authorization")
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(logging)
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .build()

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
