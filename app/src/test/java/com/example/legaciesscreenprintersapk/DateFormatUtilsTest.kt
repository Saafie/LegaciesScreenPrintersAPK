package com.example.legaciesscreenprintersapk

import com.example.legaciesscreenprintersapk.util.DateFormatUtils
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.ZoneId

/** API timestamps are UTC; the app shows them in the phone's time zone. */
class DateFormatUtilsTest {

    private val johannesburg = ZoneId.of("Africa/Johannesburg") // UTC+2

    @Test
    fun convertsUtcToLocalTime() {
        val shown = DateFormatUtils.formatOrderDate("2026-11-15T17:51:33.498729Z", johannesburg)
        assertEquals(true, shown.startsWith("15 Nov 2026, 19:51") || shown.startsWith("15 nov. 2026, 19:51"))
    }

    @Test
    fun unparseableTextIsShownUnchanged() {
        assertEquals("2026-03-29 09:15", DateFormatUtils.formatOrderDate("2026-03-29 09:15", johannesburg))
    }
}
