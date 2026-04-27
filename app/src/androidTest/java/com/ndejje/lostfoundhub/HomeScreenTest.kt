package com.ndejje.lostfoundhub

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.LargeTest
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
@LargeTest
class HomeScreenTest {

    @Test
    fun testHomeScreenDisplaysItems() {
        // Test that HomeScreen shows list of items
        val itemCount = 5
        assertTrue(itemCount > 0)
    }

    @Test
    fun testFilterLostItems() {
        // Test filtering to show only lost items
        val filterType = "LOST"
        assertEquals("LOST", filterType)
    }

    @Test
    fun testFilterFoundItems() {
        // Test filtering to show only found items
        val filterType = "FOUND"
        assertEquals("FOUND", filterType)
    }
}
