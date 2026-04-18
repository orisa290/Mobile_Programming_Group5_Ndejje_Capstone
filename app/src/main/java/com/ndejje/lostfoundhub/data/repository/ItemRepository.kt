package com.ndejje.lostfoundhub.data.repository

import android.net.Uri
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.ndejje.lostfoundhub.data.model.Item
import com.ndejje.lostfoundhub.data.model.ItemStatus
import com.ndejje.lostfoundhub.data.model.ItemType
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class ItemRepository {
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance().reference

    // Create item with Imgbb image upload
    suspend fun createItemWithImage(
        item: Item,
        imageUri: Uri?,
        imgbbRepo: ImgbbRepository
    ): Result<String> {
        return try {
            var imageUrl = ""

            if (imageUri != null) {
                Log.d("ItemRepository", "Starting Imgbb upload...")
                val uploadResult = imgbbRepo.uploadImage(imageUri)
                if (uploadResult.isSuccess) {
                    imageUrl = uploadResult.getOrNull() ?: ""
                    Log.d("ItemRepository", "✅ Imgbb upload success! URL: $imageUrl")
                } else {
                    Log.e("ItemRepository", "Imgbb upload failed: ${uploadResult.exceptionOrNull()?.message}")
                }
            }

            val itemId = database.child("items").push().key ?: UUID.randomUUID().toString()
            val newItem = item.copy(id = itemId, imageUrl = imageUrl, timestamp = System.currentTimeMillis())
            database.child("items").child(itemId).setValue(newItem).await()

            Log.d("ItemRepository", "✅ Item saved to Firebase")
            Result.success(itemId)
        } catch (e: Exception) {
            Log.e("ItemRepository", "Error: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Save item without image
    suspend fun createItem(item: Item): Result<String> {
        return try {
            val itemId = database.child("items").push().key ?: UUID.randomUUID().toString()
            val newItem = item.copy(id = itemId, timestamp = System.currentTimeMillis())
            database.child("items").child(itemId).setValue(newItem).await()
            Result.success(itemId)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getAllItems(type: ItemType? = null): Flow<List<Item>> = callbackFlow {
        val query = if (type != null) {
            database.child("items").orderByChild("type").equalTo(type.name)
        } else {
            database.child("items").orderByChild("timestamp")
        }

        val listener = query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = snapshot.children.mapNotNull {
                    it.getValue(Item::class.java)
                }.filter { it.status == ItemStatus.OPEN }
                    .sortedByDescending { it.timestamp }
                trySend(items)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        })

        awaitClose { query.removeEventListener(listener) }
    }

    fun getUserItems(): Flow<List<Item>> = callbackFlow {
        val userId = auth.currentUser?.uid ?: return@callbackFlow
        val listener = database.child("items")
            .orderByChild("userId")
            .equalTo(userId)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val items = snapshot.children.mapNotNull {
                        it.getValue(Item::class.java)
                    }.sortedByDescending { it.timestamp }
                    trySend(items)
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            })

        awaitClose { database.child("items").removeEventListener(listener) }
    }

    suspend fun updateItemStatus(itemId: String, status: ItemStatus): Result<Unit> {
        return try {
            database.child("items").child(itemId).child("status").setValue(status.name).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
