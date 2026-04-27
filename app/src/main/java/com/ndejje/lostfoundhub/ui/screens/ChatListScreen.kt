package com.ndejje.lostfoundhub.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.ndejje.lostfoundhub.R
import com.ndejje.lostfoundhub.data.model.Chat
import com.ndejje.lostfoundhub.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatListScreen(
    onChatClick: (String, String) -> Unit,
    onBack: () -> Unit,
    chatViewModel: ChatViewModel = viewModel()
) {
    val chats by chatViewModel.chats.collectAsState()
    val isLoading by chatViewModel.isLoading.collectAsState()

    LaunchedEffect(Unit) { chatViewModel.loadChats() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.messages)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            when {
                isLoading -> CircularProgressIndicator(Modifier.align(Alignment.Center))
                chats.isEmpty() -> Text(stringResource(R.string.no_chats), Modifier.align(Alignment.Center))
                else -> LazyColumn {
                    items(chats) { chat ->
                        Card(Modifier.fillMaxWidth().padding(8.dp).clickable {
                            val otherId = if (chat.user1Id == FirebaseAuth.getInstance().currentUser?.uid) chat.user2Id else chat.user1Id
                            onChatClick(chat.chatId, otherId)
                        }) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Email, contentDescription = null, modifier = Modifier.size(40.dp))
                                Spacer(Modifier.width(16.dp))
                                Column {
                                    Text(chat.itemTitle, style = MaterialTheme.typography.titleMedium)
                                    Text(chat.lastMessage, style = MaterialTheme.typography.bodyMedium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
