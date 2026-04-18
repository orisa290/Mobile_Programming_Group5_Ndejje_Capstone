package com.ndejje.lostfoundhub.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ndejje.lostfoundhub.data.model.Chat
import com.ndejje.lostfoundhub.data.model.Message
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ChatRepository {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    suspend fun sendMessage(chatId: String, receiverId: String, text: String): Result<Unit> {
        return try {
            val currentUser = auth.currentUser ?: throw Exception("Not logged in")
            val messageId = database.child("messages").child(chatId).push().key ?: UUID.randomUUID().toString()

            val message = Message(
                id = messageId,
                chatId = chatId,
                senderId = currentUser.uid,
                senderName = currentUser.displayName ?: currentUser.email ?: "User",
                text = text,
                timestamp = System.currentTimeMillis()
            )

            database.child("messages").child(chatId).child(messageId).setValue(message).await()
            database.child("chats").child(chatId).child("lastMessage").setValue(text).await()
            database.child("chats").child(chatId).child("lastMessageTime").setValue(System.currentTimeMillis()).await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        val listener = database.child("messages").child(chatId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messages = snapshot.children.mapNotNull {
                        it.getValue(Message::class.java)
                    }.sortedBy { it.timestamp }
                    trySend(messages)
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })

        awaitClose { database.child("messages").child(chatId).removeEventListener(listener) }
    }

    suspend fun getOrCreateChat(itemId: String, itemTitle: String, otherUserId: String): Result<String> {
        return try {
            val currentUserId = auth.currentUser?.uid ?: throw Exception("Not logged in")
            val chatId = listOf(currentUserId, otherUserId).sorted().joinToString("_")

            val chatSnapshot = database.child("chats").child(chatId).get().await()

            if (!chatSnapshot.exists()) {
                val chat = Chat(
                    chatId = chatId,
                    itemId = itemId,
                    itemTitle = itemTitle,
                    user1Id = currentUserId,
                    user2Id = otherUserId,
                    lastMessage = "Chat started"
                )
                database.child("chats").child(chatId).setValue(chat).await()
            }

            Result.success(chatId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeUserChats(): Flow<List<Chat>> = callbackFlow {
        val currentUserId = auth.currentUser?.uid ?: return@callbackFlow
        val listener = database.child("chats")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val chats = snapshot.children.mapNotNull {
                        it.getValue(Chat::class.java)
                    }.filter {
                        it.user1Id == currentUserId || it.user2Id == currentUserId
                    }.sortedByDescending { it.lastMessageTime }
                    trySend(chats)
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })

        awaitClose { database.child("chats").removeEventListener(listener) }
    }
}
