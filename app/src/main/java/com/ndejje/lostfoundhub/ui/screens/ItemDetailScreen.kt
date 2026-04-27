package com.ndejje.lostfoundhub.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.ndejje.lostfoundhub.R
import com.ndejje.lostfoundhub.data.model.Item
import com.ndejje.lostfoundhub.data.model.ItemStatus
import com.ndejje.lostfoundhub.viewmodel.ChatViewModel
import com.ndejje.lostfoundhub.viewmodel.ItemViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ItemDetailScreen(
    item: Item,
    currentUserId: String,
    onBack: () -> Unit,
    onStartChat: (String, String, String) -> Unit,
    onMarkResolved: () -> Unit
) {
    val context = LocalContext.current
    val itemViewModel = remember { ItemViewModel(context) }
    val chatViewModel = remember { ChatViewModel() }

    var isResolving by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.item_details)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Image Section - Using AndroidView for simplicity
                    if (item.imageUrl.isNotBlank()) {
                        androidx.compose.foundation.Image(
                            painter = rememberAsyncImagePainter(item.imageUrl),
                            contentDescription = item.title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    } else {
                        // Placeholder when no image
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(150.dp)
                                .background(Color(0xFFE0E0E0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("No Image", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
                        }
                    }

                    Text(item.title, style = MaterialTheme.typography.headlineSmall)
                    Text(item.description, style = MaterialTheme.typography.bodyLarge)
                    Divider()
                    Text("${stringResource(R.string.category)}: ${item.category}")
                    Text("${stringResource(R.string.location_label)}: ${item.location}")
                    Text("${stringResource(R.string.date_label)}: ${item.date}")
                    Text(
                        "${stringResource(R.string.status_label)}: ${item.status.name}",
                        color = if (item.status == ItemStatus.OPEN)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.secondary
                    )
                }
            }

            if (item.userId != currentUserId) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            val result = chatViewModel.getOrCreateChat(item.id, item.title, item.userId)
                            result.onSuccess { chatId ->
                                onStartChat(chatId, item.userId, item.title)
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.message_owner))
                }
            }

            if (item.userId == currentUserId && item.status == ItemStatus.OPEN) {
                Button(
                    onClick = {
                        isResolving = true
                        itemViewModel.updateItemStatus(item.id, ItemStatus.RESOLVED) { success, _ ->
                            isResolving = false
                            if (success) onMarkResolved()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    enabled = !isResolving
                ) {
                    if (isResolving) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                    }
                    Text(stringResource(R.string.mark_as_resolved))
                }
            }
        }
    }
}
