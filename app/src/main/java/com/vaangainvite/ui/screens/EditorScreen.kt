package com.vaangainvite.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vaangainvite.core.share.InvitationShare
import com.vaangainvite.data.model.InvitationDetails
import com.vaangainvite.data.model.InvitationLanguage
import com.vaangainvite.ui.viewmodel.InviteViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private data class QuickInviteMessage(
    val id: String,
    val text: String
)

private val QuickInviteMessages = listOf(
    QuickInviteMessage("excited", "🎉 Excited to see you!"),
    QuickInviteMessage("join", "🙏 Please join us"),
    QuickInviteMessage("presence", "❤️ Your presence means a lot"),
    QuickInviteMessage("celebrating", "😊 Looking forward to celebrating together"),
    QuickInviteMessage("special", "🎊 Don’t miss this special day"),
    QuickInviteMessage("celebrate", "🥳 Come celebrate with us")
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    viewModel: InviteViewModel,
    onBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val galleryPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            viewModel.saveInvitationToGallery()
        } else {
            viewModel.setStatusMessage("Storage permission is required to save on this Android version")
        }
    }
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.updateUploadedPhoto(uri)
        }
    }

    LaunchedEffect(state.statusMessage) {
        state.statusMessage?.let { message ->
            snackbarHostState.showSnackbar(message)
            viewModel.clearStatusMessage()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Edit invitation") },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text(text = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            val selectedTemplate = state.selectedTemplate
            if (selectedTemplate != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(28.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Image(
                        painter = painterResource(id = selectedTemplate.drawableResId),
                        contentDescription = selectedTemplate.title,
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(0.8f),
                        contentScale = ContentScale.Crop
                    )
                }
            }

            PhotoUploadSection(
                hasPhoto = state.uploadedPhotoUri != null,
                onChoosePhoto = {
                    photoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                    )
                },
                onRemovePhoto = {
                    viewModel.updateUploadedPhoto(null)
                }
            )

            EditorFields(
                details = state.details,
                selectedLanguage = state.selectedLanguage,
                onLanguageSelected = viewModel::selectLanguage,
                onNameChanged = viewModel::updateName,
                onDateChanged = viewModel::updateDate,
                onTimeChanged = viewModel::updateTime,
                onVenueChanged = viewModel::updateVenue,
                onMessageChanged = viewModel::updateMessage
            )

            Button(
                onClick = viewModel::generateInvitation,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Generate invitation image")
            }

            state.generatedBitmap?.let { bitmap ->
                Text(
                    text = "Generated preview",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Generated invitation preview",
                    modifier = Modifier
                        .fillMaxWidth()
                        .aspectRatio(0.8f)
                        .clip(RoundedCornerShape(28.dp)),
                    contentScale = ContentScale.Crop
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                FilledTonalButton(
                    onClick = {
                        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P &&
                            ContextCompat.checkSelfPermission(
                                context,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                            ) != PackageManager.PERMISSION_GRANTED
                        ) {
                            galleryPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        } else {
                            viewModel.saveInvitationToGallery()
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = state.selectedTemplate != null
                ) {
                    Text(text = "Save")
                }
            }

            ShareActions(
                enabled = state.selectedTemplate != null,
                onShareChat = {
                    val imageUri = viewModel.getShareImageUri()
                    if (imageUri != null) {
                        val result = InvitationShare.shareToWhatsAppChat(
                            context = context,
                            imageUri = imageUri,
                            message = state.selectedLanguage.shareMessage
                        )
                        viewModel.setStatusMessage(result.toStatusMessage("Opening WhatsApp chat"))
                    }
                },
                onShareStatus = {
                    val imageUri = viewModel.getShareImageUri()
                    if (imageUri != null) {
                        val result = InvitationShare.shareToWhatsAppStatus(
                            context = context,
                            imageUri = imageUri,
                            message = state.selectedLanguage.shareMessage
                        )
                        viewModel.setStatusMessage(result.toStatusMessage("Opening WhatsApp Status"))
                    }
                }
            )

            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

@Composable
private fun ShareActions(
    enabled: Boolean,
    onShareChat: () -> Unit,
    onShareStatus: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = "Easy WhatsApp sharing",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onShareChat,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                enabled = enabled
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "WhatsApp")
                    Text(
                        text = "Chat",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
            FilledTonalButton(
                onClick = onShareStatus,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight(),
                enabled = enabled
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = "WhatsApp")
                    Text(
                        text = "Status",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
        Text(
            text = "If WhatsApp is not installed, the Android share sheet opens automatically.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PhotoUploadSection(
    hasPhoto: Boolean,
    onChoosePhoto: () -> Unit,
    onRemovePhoto: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Photo template option",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (hasPhoto) {
                    "Photo selected. It will appear inside the generated invitation card."
                } else {
                    "Upload a family, couple, child, or ceremony photo to include in the invitation."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onChoosePhoto) {
                    Text(text = if (hasPhoto) "Change photo" else "Upload photo")
                }
                if (hasPhoto) {
                    TextButton(onClick = onRemovePhoto) {
                        Text(text = "Remove")
                    }
                }
            }
        }
    }
}

private fun InvitationShare.ShareResult.toStatusMessage(whatsAppMessage: String): String {
    return when (this) {
        InvitationShare.ShareResult.OPENED_WHATSAPP -> whatsAppMessage
        InvitationShare.ShareResult.OPENED_SHARE_SHEET -> "WhatsApp not found; opening share sheet"
    }
}

@Composable
private fun EditorFields(
    details: InvitationDetails,
    selectedLanguage: InvitationLanguage,
    onLanguageSelected: (InvitationLanguage) -> Unit,
    onNameChanged: (String) -> Unit,
    onDateChanged: (String) -> Unit,
    onTimeChanged: (String) -> Unit,
    onVenueChanged: (String) -> Unit,
    onMessageChanged: (String) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = "Invitation details",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        LanguageSelector(
            selectedLanguage = selectedLanguage,
            onLanguageSelected = onLanguageSelected
        )
        OutlinedTextField(
            value = details.name,
            onValueChange = onNameChanged,
            label = { Text("Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        DatePickerField(
            date = details.date,
            onDateSelected = onDateChanged
        )
        OutlinedTextField(
            value = details.time,
            onValueChange = onTimeChanged,
            label = { Text("Time") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = details.venue,
            onValueChange = onVenueChanged,
            label = { Text("Venue") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2
        )
        InviteMessageSection(
            message = details.message,
            onMessageChanged = onMessageChanged
        )
    }
}

@Composable
private fun InviteMessageSection(
    message: String,
    onMessageChanged: (String) -> Unit
) {
    var selectedQuickMessageId by remember { mutableStateOf<String?>(null) }
    val selectedQuickMessage = QuickInviteMessages.firstOrNull { it.id == selectedQuickMessageId }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Additional message",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Tap a quick message or type your own. Emojis are welcome.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        QuickMessageChips(
            selectedMessageId = selectedQuickMessageId,
            onMessageSelected = { quickMessage ->
                selectedQuickMessageId = quickMessage.id
                onMessageChanged(quickMessage.text)
            }
        )

        OutlinedTextField(
            value = message,
            onValueChange = { updatedMessage ->
                if (selectedQuickMessage?.text != updatedMessage) {
                    selectedQuickMessageId = null
                }
                onMessageChanged(updatedMessage)
            },
            label = { Text("Message to guests") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 6,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = KeyboardType.Text
            ),
            supportingText = {
                Text(text = "This message appears on the generated invitation image.")
            }
        )

        InviteMessagePreview(message = message)
    }
}

@Composable
private fun QuickMessageChips(
    selectedMessageId: String?,
    onMessageSelected: (QuickInviteMessage) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        items(
            items = QuickInviteMessages,
            key = { quickMessage -> quickMessage.id }
        ) { quickMessage ->
            FilterChip(
                selected = selectedMessageId == quickMessage.id,
                onClick = { onMessageSelected(quickMessage) },
                label = {
                    Text(text = quickMessage.text)
                }
            )
        }
    }
}

@Composable
private fun InviteMessagePreview(message: String) {
    val previewMessage = message.ifBlank {
        "Your invitation message preview will appear here."
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.tertiaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Live invite message preview",
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = previewMessage,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onTertiaryContainer
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerField(
    date: String,
    onDateSelected: (String) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = date,
            onValueChange = {},
            label = { Text("Date") },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            placeholder = { Text("Pick function date") },
            trailingIcon = {
                TextButton(onClick = { showDatePicker = true }) {
                    Text(text = "Pick")
                }
            }
        )
        Text(
            text = "Use the date picker to avoid typing mistakes.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }

    if (showDatePicker) {
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            onDateSelected(formatDate(millis))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text(text = "OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(text = "Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private fun formatDate(dateMillis: Long): String {
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy")
    return Instant.ofEpochMilli(dateMillis)
        .atZone(ZoneId.systemDefault())
        .toLocalDate()
        .format(formatter)
}

@Composable
private fun LanguageSelector(
    selectedLanguage: InvitationLanguage,
    onLanguageSelected: (InvitationLanguage) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Invitation language",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            InvitationLanguage.entries.forEach { language ->
                FilterChip(
                    selected = selectedLanguage == language,
                    onClick = { onLanguageSelected(language) },
                    label = { Text(text = language.displayName) }
                )
            }
        }
        Text(
            text = "Choose Tamil to render invitation headings and labels in Tamil. You can type names and messages in Tamil using your phone keyboard.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
