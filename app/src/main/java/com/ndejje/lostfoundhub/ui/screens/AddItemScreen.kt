package com.ndejje.lostfoundhub.ui.screens

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
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
import com.ndejje.lostfoundhub.data.model.ItemStatus
import com.ndejje.lostfoundhub.viewmodel.AuthViewModel
import com.ndejje.lostfoundhub.viewmodel.ItemViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddItemScreen(
    onItemAdded: () -> Unit,
    onBack: () -> Unit,
    authViewModel: AuthViewModel = androidx.lifecycle.viewmodel.compose.viewModel()
) {
    val context = LocalContext.current
    val itemViewModel = ItemViewModel(context)

    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var location by remember { mutableStateOf("") }
    var date by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(ItemType.LOST) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        Log.d("AddItemScreen", "📸 Image selected: $uri")
        selectedImageUri = uri
    }

    val errorTitleRequired = stringResource(R.string.error_title_required)
    val errorDescriptionRequired = stringResource(R.string.error_description_required)
    val errorCategoryRequired = stringResource(R.string.error_category_required)
    val errorLocationRequired = stringResource(R.string.error_location_required)
    val errorDateRequired = stringResource(R.string.error_date_required)
    val errorCreateFailed = stringResource(R.string.error_create_failed)
    val submittingText = stringResource(R.string.submitting)
    val submitReportText = stringResource(R.string.submit_report)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.report_item)) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(R.string.back))
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(stringResource(R.string.report_as), style = MaterialTheme.typography.bodyMedium)
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                FilterChip(
                    selected = selectedType == ItemType.LOST,
                    onClick = { selectedType = ItemType.LOST },
                    label = { Text(stringResource(R.string.lost)) }
                )
                FilterChip(
                    selected = selectedType == ItemType.FOUND,
                    onClick = { selectedType = ItemType.FOUND },
                    label = { Text(stringResource(R.string.found)) }
                )
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clickable {
                        Log.d("AddItemScreen", "🖼️ Image picker clicked")
                        imagePickerLauncher.launch("image/*")
                    }
            ) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (selectedImageUri != null) {
                        Image(
                            painter = rememberAsyncImagePainter(selectedImageUri),
                            contentDescription = stringResource(R.string.add_image),
                            modifier = Modifier.fillMaxSize()
                        )
                    } else {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Add, contentDescription = stringResource(R.string.add_image))
                            Text(stringResource(R.string.tap_to_add_image), style = MaterialTheme.typography.bodySmall)
                        }
                    }
                }
            }

            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text(stringResource(R.string.item_title)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text(stringResource(R.string.description)) },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )

            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text(stringResource(R.string.category_hint)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = location,
                onValueChange = { location = it },
                label = { Text(stringResource(R.string.location_hint)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            OutlinedTextField(
                value = date,
                onValueChange = { date = it },
                label = { Text(stringResource(R.string.date_hint)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            if (errorMessage != null) {
                Text(
                    text = errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Button(
                onClick = {
                    Log.d("AddItemScreen", "🔘 Submit button clicked")
                    Log.d("AddItemScreen", "Title: $title")
                    Log.d("AddItemScreen", "Selected image URI: $selectedImageUri")

                    when {
                        title.isBlank() -> errorMessage = errorTitleRequired
                        description.isBlank() -> errorMessage = errorDescriptionRequired
                        category.isBlank() -> errorMessage = errorCategoryRequired
                        location.isBlank() -> errorMessage = errorLocationRequired
                        date.isBlank() -> errorMessage = errorDateRequired
                        else -> {
                            isLoading = true
                            errorMessage = null
                            val currentUserId = authViewModel.getCurrentUserId()
                            val currentUserName = "User"

                            val item = Item(
                                title = title,
                                description = description,
                                type = selectedType,
                                category = category,
                                location = location,
                                date = date,
                                userId = currentUserId,
                                userName = currentUserName,
                                status = ItemStatus.OPEN,
                                timestamp = System.currentTimeMillis()
                            )

                            Log.d("AddItemScreen", "📤 Calling createItemWithImage...")
                            itemViewModel.createItemWithImage(item, selectedImageUri) { success, error ->
                                isLoading = false
                                Log.d("AddItemScreen", "📥 Result - Success: $success, Error: $error")
                                if (success) {
                                    Toast.makeText(context, "Item created successfully!", Toast.LENGTH_SHORT).show()
                                    onItemAdded()
                                } else {
                                    errorMessage = error ?: errorCreateFailed
                                    Toast.makeText(context, "Error: ${error ?: "Unknown error"}", Toast.LENGTH_LONG).show()
                                }
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(submittingText)
                } else {
                    Text(submitReportText, style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}
