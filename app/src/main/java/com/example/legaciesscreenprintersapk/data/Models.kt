package com.example.legaciesscreenprintersapk.data

/** One step of the four-stage production timeline shown on the Track screen. */
data class TrackingStage(
    val stageNumber: Int,
    val title: String,
    val description: String,
    val isCompleted: Boolean,
    val isCurrent: Boolean,
)
