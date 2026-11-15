package com.example.legaciesscreenprintersapk.util

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import java.util.Locale

/**
 * Shows API dates (UTC) in the phone's local time.
 * Reference: Oracle (2025) DateTimeFormatter (Java SE) - https://docs.oracle.com/en/java/javase/21/docs/api/java.base/java/time/format/DateTimeFormatter.html
 */
object DateFormatUtils {

    private val displayFormat = DateTimeFormatter.ofPattern("d MMM yyyy, HH:mm", Locale.getDefault())

    /** Readable local date and time, or the original text if it isn't an ISO-8601 instant. */
    fun formatOrderDate(isoUtc: String, zone: ZoneId = ZoneId.systemDefault()): String = try {
        displayFormat.withZone(zone).format(Instant.parse(isoUtc))
    } catch (_: DateTimeParseException) {
        isoUtc
    }
}
