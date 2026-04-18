package com.ndejje.lostfoundhub.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ndejje.lostfoundhub.data.model.Chat
import com.ndejje.lostfoundhub.data.model.Message
import com.ndejje.lostfoundhub.data.repository.ChatRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChatViewModel : ViewModel() {
    private val repository = ChatRepository()

    private val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages: StateFlow<List<Message>> = _messages.asStateFlow()

    private val _chats = MutableStateFlow<List<Chat>>(emptyList())
    val chats: StateFlow<List<Chat>> = _chats.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    fun observeMessages(chatId: String) {
        viewModelScope.launch {
            repository.observeMessages(chatId).collect { messages ->
                _messages.value = messages
            }
        }
    }

    fun sendMessage(chatId: String, receiverId: String, text: String) {
        viewModelScope.launch {
            repository.sendMessage(chatId, receiverId, text)
        }
    }

    fun loadChats() {
        viewModelScope.launch {
            _isLoading.value = true
            repository.observeUserChats().collect { chats ->
                _chats.value = chats
                _isLoading.value = false
            }
        }
    }

    suspend fun getOrCreateChat(itemId: String, itemTitle: String, otherUserId: String): Result<String> {
        return repository.getOrCreateChat(itemId, itemTitle, otherUserId)
    }
}
