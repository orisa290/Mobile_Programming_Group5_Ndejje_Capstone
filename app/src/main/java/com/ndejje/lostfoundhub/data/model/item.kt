package com.ndejje.lostfoundhub.data.model

enum class ItemType {
    LOST, FOUND
}

enum class ItemStatus {
    OPEN, RESOLVED
}

data class Item(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val type: ItemType = ItemType.LOST,
    val category: String = "",
    val location: String = "",
    val date: String = "",
    val userId: String = "",
    val userName: String = "",
    val userPhone: String = "",
    val userEmail: String = "",
    val imageUrl: String = "",  // Will store Imgur URL!
    val status: ItemStatus = ItemStatus.OPEN,
    val timestamp: Long = System.currentTimeMillis()
)
