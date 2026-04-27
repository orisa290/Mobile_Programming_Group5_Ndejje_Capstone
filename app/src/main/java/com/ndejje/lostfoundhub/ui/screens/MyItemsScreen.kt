package com.ndejje.lostfoundhub.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.ndejje.lostfoundhub.R
import com.ndejje.lostfoundhub.data.model.Item
import com.ndejje.lostfoundhub.data.model.ItemStatus
import com.ndejje.lostfoundhub.viewmodel.ItemViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyItemsScreen(
    onItemClick: (Item) -> Unit,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val itemViewModel = remember { ItemViewModel(context) }

    val items by itemViewModel.userItems.collectAsState()
    LaunchedEffect(Unit) { itemViewModel.loadUserItems() }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.my_reports)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            if (items.isEmpty()) {
                Text(stringResource(R.string.no_reports), Modifier.align(Alignment.Center))
            } else {
                LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(items) { item ->
                        Card(Modifier.fillMaxWidth().clickable { onItemClick(item) }) {
                            Column(Modifier.padding(16.dp)) {
                                Row(horizontalArrangement = Arrangement.SpaceBetween) {
                                    Text(item.title, style = MaterialTheme.typography.titleMedium)
                                    Surface(shape = MaterialTheme.shapes.small, color = if (item.status == ItemStatus.OPEN) MaterialTheme.colorScheme.primary.copy(alpha = 0.2f) else MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)) {
                                        Text(item.status.name, Modifier.padding(horizontal = 8.dp, vertical = 2.dp))
                                    }
                                }
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
