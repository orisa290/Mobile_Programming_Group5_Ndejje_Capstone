package com.ndejje.lostfoundhub

import com.ndejje.lostfoundhub.data.model.Item
import com.ndejje.lostfoundhub.data.model.ItemStatus
import com.ndejje.lostfoundhub.data.model.ItemType
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {

    @Test
    fun testItemCreation() {
        val item = Item(
            title = "Test Lost Phone",
            description = "Samsung phone lost in library",
            type = ItemType.LOST,
            category = "Electronics",
            location = "Main Campus Library",
            date = "2026-04-17",
            userId = "test-user-123",
            userName = "Test User"
        )

        assertEquals("Test Lost Phone", item.title)
        assertEquals(ItemType.LOST, item.type)
        assertEquals(ItemStatus.OPEN, item.status)
        assertTrue(item.timestamp > 0)
    }

    @Test
    fun testItemStatusUpdate() {
        val item = Item(status = ItemStatus.OPEN)
        val resolvedItem = item.copy(status = ItemStatus.RESOLVED)

        assertEquals(ItemStatus.RESOLVED, resolvedItem.status)
        assertNotEquals(item.status, resolvedItem.status)
    }

    @Test
    fun testItemTypeValues() {
        assertEquals("LOST", ItemType.LOST.name)
        assertEquals("FOUND", ItemType.FOUND.name)
        assertNotEquals(ItemType.LOST, ItemType.FOUND)
    }
}
