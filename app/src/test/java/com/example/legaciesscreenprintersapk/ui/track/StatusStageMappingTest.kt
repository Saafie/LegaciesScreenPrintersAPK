package com.example.legaciesscreenprintersapk.ui.track

import org.junit.Assert.assertEquals
import org.junit.Test

/** The tracking timeline must show the right step for each of the API's five statuses. */
class StatusStageMappingTest {

    @Test
    fun apiStatusesMapToTheirTimelineStep() {
        assertEquals(0, mapStatusToStageIndex("Order Received & Verified"))
        assertEquals(1, mapStatusToStageIndex("Vinyl Cutting & Screen Prep")) // contains "Screen" but is step 2
        assertEquals(2, mapStatusToStageIndex("Screen Printing"))
        assertEquals(3, mapStatusToStageIndex("Ready for Collection"))
        assertEquals(3, mapStatusToStageIndex("Completed"))
    }

    @Test
    fun legacyStageLabelsStillMap() {
        assertEquals(1, mapStatusToStageIndex("Stage 2: Vinyl Cutting & Screen Prep"))
        assertEquals(2, mapStatusToStageIndex("Stage 3: Screen Printing In Progress"))
        assertEquals(0, mapStatusToStageIndex("Unknown"))
    }
}
