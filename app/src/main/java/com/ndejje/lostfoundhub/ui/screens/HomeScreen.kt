package com.ndejje.lostfoundhub.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Email
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.ndejje.lostfoundhub.R
import com.ndejje.lostfoundhub.data.model.Item
import com.ndejje.lostfoundhub.data.model.ItemType
import com.ndejje.lostfoundhub.viewmodel.ItemViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onItemClick: (Item) -> Unit,
    onAddItemClick: () -> Unit,
    onMessagesClick: () -> Unit,
    onMyItemsClick: () -> Unit,
    onLogout: () -> Unit
) {
    val context = LocalContext.current
    val itemViewModel = remember { ItemViewModel(context) }

    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf(stringResource(R.string.lost), stringResource(R.string.found))

    LaunchedEffect(selectedTab) {
        itemViewModel.loadItems(if (selectedTab == 0) ItemType.LOST else ItemType.FOUND)
    }

    val items by itemViewModel.items.collectAsState()
    val isLoading by itemViewModel.isLoading.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.lost_found_hub)) },
                actions = {
                    IconButton(onClick = onMessagesClick) {
                        Icon(Icons.Default.Email, contentDescription = stringResource(R.string.messages))
                    }
                    IconButton(onClick = onMyItemsClick) {
                        Icon(Icons.AutoMirrored.Filled.List, contentDescription = stringResource(R.string.my_items))
                    }
                    IconButton(onClick = onLogout) {
                        Icon(Icons.AutoMirrored.Filled.ExitToApp, contentDescription = stringResource(R.string.logout))
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAddItemClick) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.report_item))
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { i, title ->
                    Tab(selected = selectedTab == i, onClick = { selectedTab = i }, text = { Text(title) })
                }
            }

            when {
                isLoading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
                items.isEmpty() -> Box(Modifier.fillMaxSize(), Alignment.Center) { Text(stringResource(R.string.no_items_found)) }
                else -> LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { item ->
                        Card(Modifier.fillMaxWidth().clickable { onItemClick(item) }) {
                            Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                                if (item.imageUrl.isNotBlank()) {
                                    Image(
                                        painter = rememberAsyncImagePainter(item.imageUrl),
                                        contentDescription = item.title,
                                        modifier = Modifier.size(60.dp)
                                    )
                                    Spacer(Modifier.width(12.dp))
                                }
                                Column {
                                    Text(item.title, style = MaterialTheme.typography.titleMedium)
                                    Text(item.description.take(80), style = MaterialTheme.typography.bodyMedium)
                                    Text("${item.location} • ${item.date}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
