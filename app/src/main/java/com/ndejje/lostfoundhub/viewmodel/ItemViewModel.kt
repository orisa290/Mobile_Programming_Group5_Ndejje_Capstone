package com.ndejje.lostfoundhub.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ndejje.lostfoundhub.data.model.Item
import com.ndejje.lostfoundhub.data.model.ItemStatus
import com.ndejje.lostfoundhub.data.model.ItemType
import com.ndejje.lostfoundhub.data.repository.ImgbbRepository
import com.ndejje.lostfoundhub.data.repository.ItemRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ItemViewModel(private val context: Context) : ViewModel() {
    private val repository = ItemRepository()
    private val imgbbRepository = ImgbbRepository(context)

    init {
        Log.d("ItemViewModel", "✅ ImgbbRepository initialized")
    }

    private val _items = MutableStateFlow<List<Item>>(emptyList())
    val items: StateFlow<List<Item>> = _items.asStateFlow()

    private val _userItems = MutableStateFlow<List<Item>>(emptyList())
    val userItems: StateFlow<List<Item>> = _userItems.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _isUploading = MutableStateFlow(false)
    val isUploading: StateFlow<Boolean> = _isUploading.asStateFlow()

    fun loadItems(type: ItemType? = null) {
        viewModelScope.launch {
            _isLoading.value = true
            repository.getAllItems(type).collect { items ->
                _items.value = items
                _isLoading.value = false
            }
        }
    }

    fun loadUserItems() {
        viewModelScope.launch {
            repository.getUserItems().collect { items ->
                _userItems.value = items
            }
        }
    }

    fun createItemWithImage(item: Item, imageUri: Uri?, onResult: (Boolean, String?) -> Unit) {
        Log.d("ItemViewModel", "createItemWithImage called")
        Log.d("ItemViewModel", "ImageUri: $imageUri")

        viewModelScope.launch {
            _isUploading.value = true

            try {
                val result = if (imageUri != null) {
                    Log.d("ItemViewModel", "Uploading to ImgBB...")
                    repository.createItemWithImage(item, imageUri, imgbbRepository)
                } else {
                    Log.d("ItemViewModel", "No image, saving item only")
                    repository.createItem(item)
                }

                _isUploading.value = false
                result.onSuccess {
                    Log.d("ItemViewModel", "✅ Success!")
                    onResult(true, null)
                }.onFailure { e ->
                    Log.e("ItemViewModel", "❌ Failed: ${e.message}", e)
                    onResult(false, e.message)
                }
            } catch (e: Exception) {
                _isUploading.value = false
                Log.e("ItemViewModel", "Exception: ${e.message}", e)
                onResult(false, e.message)
            }
        }
    }

    fun updateItemStatus(itemId: String, status: ItemStatus, onResult: (Boolean, String?) -> Unit) {
        viewModelScope.launch {
            val result = repository.updateItemStatus(itemId, status)
            result.onSuccess { onResult(true, null) }
                .onFailure { onResult(false, it.message) }
        }
    }
}
