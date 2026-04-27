package com.ndejje.lostfoundhub.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.ndejje.lostfoundhub.R
import com.ndejje.lostfoundhub.data.model.Message
import com.ndejje.lostfoundhub.viewmodel.ChatViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatId: String,
    receiverId: String,
    onBack: () -> Unit,
    chatViewModel: ChatViewModel = viewModel()
) {
    val messages by chatViewModel.messages.collectAsState()
    var currentMessage by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(chatId) { chatViewModel.observeMessages(chatId) }
    LaunchedEffect(messages) { if (messages.isNotEmpty()) listState.animateScrollToItem(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.chat)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            LazyColumn(Modifier.weight(1f).fillMaxWidth().padding(8.dp), reverseLayout = true, state = listState) {
                items(messages.reversed()) { message ->
                    val isCurrentUser = message.senderId == FirebaseAuth.getInstance().currentUser?.uid
                    Row(Modifier.fillMaxWidth().padding(4.dp), horizontalArrangement = if (isCurrentUser) Arrangement.End else Arrangement.Start) {
                        Surface(shape = MaterialTheme.shapes.medium, color = if (isCurrentUser) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant) {
                            Column(Modifier.padding(12.dp)) {
                                if (!isCurrentUser) Text(message.senderName, style = MaterialTheme.typography.labelSmall)
                                Text(message.text, color = if (isCurrentUser) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface)
                            }
                        }
                    }
                }
            }

            Row(Modifier.fillMaxWidth().padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = currentMessage,
                    onValueChange = { currentMessage = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text(stringResource(R.string.type_message)) }
                )
                Spacer(Modifier.width(8.dp))
                Button(
                    onClick = {
                        if (currentMessage.isNotBlank()) {
                            chatViewModel.sendMessage(chatId, receiverId, currentMessage)
                            currentMessage = ""
                        }
                    }
                ) {
                    Icon(Icons.Default.Send, contentDescription = stringResource(R.string.send))
                }
            }
        }
    }
}
