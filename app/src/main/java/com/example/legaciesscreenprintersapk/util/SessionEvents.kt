package com.example.legaciesscreenprintersapk.util

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * App-wide "your session ended" signal.
 * Reference: Android Developers (2025) StateFlow and SharedFlow - https://developer.android.com/kotlin/flow/stateflow-and-sharedflow
 */
object SessionEvents {
    private val _expired = MutableSharedFlow<String>(extraBufferCapacity = 1)

    /** Emits a user-facing reason, e.g. "Your session has expired. Please sign in again." */
    val expired: SharedFlow<String> = _expired.asSharedFlow()

    fun notifyExpired(reason: String) {
        _expired.tryEmit(reason)
    }
}
