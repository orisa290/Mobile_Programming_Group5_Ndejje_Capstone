package com.ndejje.lostfoundhub.data.model

data class Chat(
    val chatId: String = "",
    val itemId: String = "",
    val itemTitle: String = "",
    val user1Id: String = "",
    val user2Id: String = "",
    val lastMessage: String = "",
    val lastMessageTime: Long = System.currentTimeMillis()
)
