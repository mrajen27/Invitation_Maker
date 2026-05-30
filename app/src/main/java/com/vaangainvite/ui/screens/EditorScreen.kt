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
import androidx.compose.runtime.rememberCoroutineScope
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.vaangainvite.core.share.InvitationShare
import com.vaangainvite.data.model.InvitationDetails
import com.vaangainvite.data.model.InvitationFieldLimits
import com.vaangainvite.data.model.InvitationLanguage
import com.vaangainvite.ui.viewmodel.InviteViewModel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private data class QuickInviteMessage(
    val id: String,
    val text: String,
    val language: InvitationLanguage,
    val categoryId: String?,
    val tone: QuickMessageTone
)

private enum class QuickMessageTone(val label: String) {
    ALL("All"),
    FORMAL("Formal"),
    CASUAL("Casual"),
    BLESSING("Blessing")
}

private fun QuickMessageTone.localizedLabel(language: InvitationLanguage): String {
    return when (language) {
        InvitationLanguage.ENGLISH -> label
        InvitationLanguage.TAMIL -> when (this) {
            QuickMessageTone.ALL -> "அனைத்தும்"
            QuickMessageTone.FORMAL -> "முறையான"
            QuickMessageTone.CASUAL -> "சாதாரண"
            QuickMessageTone.BLESSING -> "ஆசீர்வாதம்"
        }
    }
}

private val QuickInviteMessages = listOf(
    QuickInviteMessage("en_excited", "🎉 Excited to see you!", InvitationLanguage.ENGLISH, null, QuickMessageTone.CASUAL),
    QuickInviteMessage("en_join", "🙏 Please join us", InvitationLanguage.ENGLISH, null, QuickMessageTone.FORMAL),
    QuickInviteMessage("en_presence", "❤️ Your presence means a lot", InvitationLanguage.ENGLISH, null, QuickMessageTone.BLESSING),
    QuickInviteMessage("en_celebrating", "😊 Looking forward to celebrating together", InvitationLanguage.ENGLISH, null, QuickMessageTone.CASUAL),
    QuickInviteMessage("en_special", "🎊 Don’t miss this special day", InvitationLanguage.ENGLISH, null, QuickMessageTone.CASUAL),
    QuickInviteMessage("en_celebrate", "🥳 Come celebrate with us", InvitationLanguage.ENGLISH, null, QuickMessageTone.CASUAL),
    QuickInviteMessage("en_birthday", "🎂 Come bless and celebrate this birthday with us", InvitationLanguage.ENGLISH, "birthday", QuickMessageTone.BLESSING),
    QuickInviteMessage("en_wedding", "💍 Kindly grace the wedding and bless the couple", InvitationLanguage.ENGLISH, "wedding", QuickMessageTone.FORMAL),
    QuickInviteMessage("en_housewarming", "🏡 Please join our housewarming ceremony with your blessings", InvitationLanguage.ENGLISH, "housewarming", QuickMessageTone.FORMAL),
    QuickInviteMessage("en_puberty", "🌸 Your blessings and presence will make this ceremony special", InvitationLanguage.ENGLISH, "puberty", QuickMessageTone.BLESSING),
    QuickInviteMessage("ta_join", "🙏 தயவு செய்து கலந்து கொள்ளுங்கள்", InvitationLanguage.TAMIL, null, QuickMessageTone.FORMAL),
    QuickInviteMessage("ta_presence", "❤️ உங்கள் வருகை எங்களுக்கு மகிழ்ச்சி தரும்", InvitationLanguage.TAMIL, null, QuickMessageTone.BLESSING),
    QuickInviteMessage("ta_celebrate", "😊 ஒன்றாக கொண்டாட ஆவலுடன் காத்திருக்கிறோம்", InvitationLanguage.TAMIL, null, QuickMessageTone.CASUAL),
    QuickInviteMessage("ta_special", "🎊 இந்த சிறப்பு நாளை தவற விடாதீர்கள்", InvitationLanguage.TAMIL, null, QuickMessageTone.CASUAL),
    QuickInviteMessage("ta_birthday", "🎂 பிறந்தநாள் விழாவில் கலந்து கொண்டு ஆசீர்வதிக்கவும்", InvitationLanguage.TAMIL, "birthday", QuickMessageTone.BLESSING),
    QuickInviteMessage("ta_wedding", "💍 திருமண விழாவில் கலந்து கொண்டு மணமக்களை ஆசீர்வதிக்கவும்", InvitationLanguage.TAMIL, "wedding", QuickMessageTone.FORMAL),
    QuickInviteMessage("ta_housewarming", "🏡 புதுமனை புகுவிழாவில் கலந்து கொண்டு ஆசீர்வதிக்கவும்", InvitationLanguage.TAMIL, "housewarming", QuickMessageTone.FORMAL),
    QuickInviteMessage("ta_puberty", "🌸 இவ்விழாவில் உங்கள் வருகையும் ஆசீர்வாதமும் வேண்டுகிறோம்", InvitationLanguage.TAMIL, "puberty", QuickMessageTone.BLESSING)
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
    val scope = rememberCoroutineScope()
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
                title = { Text(text = state.selectedLanguage.editorScreenTitle) },
                navigationIcon = {
                    TextButton(onClick = onBack) {
                        Text(text = state.selectedLanguage.editorBackButton)
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
                hasMessage = state.details.message.isNotBlank(),
                selectedLanguage = state.selectedLanguage,
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
                selectedCategoryId = state.selectedCategory?.id,
                onLanguageSelected = viewModel::selectLanguage,
                onOccasionTitleChanged = viewModel::updateOccasionTitle,
                onNameChanged = viewModel::updateName,
                onDateChanged = viewModel::updateDate,
                onTimeChanged = viewModel::updateTime,
                onVenueChanged = viewModel::updateVenue,
                onMobileNumberChanged = viewModel::updateMobileNumber,
                onMessageChanged = viewModel::updateMessage,
                hasUploadedPhoto = state.uploadedPhotoUri != null
            )

            Button(
                onClick = viewModel::generateInvitation,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = state.selectedLanguage.editorGenerateButton)
            }

            state.generatedBitmap?.let { bitmap ->
                Text(
                    text = state.selectedLanguage.editorPreviewTitle,
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
                    Text(text = state.selectedLanguage.editorSaveButton)
                }
            }

            ShareActions(
                enabled = state.selectedTemplate != null,
                selectedLanguage = state.selectedLanguage,
                onShareChat = {
                    scope.launch {
                        val imageUri = viewModel.getOrCreateShareImageUri()
                        if (imageUri != null) {
                            val result = InvitationShare.shareToWhatsAppChat(
                                context = context,
                                imageUri = imageUri,
                                message = state.selectedLanguage.shareMessage
                            )
                            viewModel.setStatusMessage(result.toStatusMessage("Opening WhatsApp chat"))
                        } else {
                            viewModel.setStatusMessage("Generate the invitation first")
                        }
                    }
                },
                onShareStatus = {
                    scope.launch {
                        val imageUri = viewModel.getOrCreateShareImageUri()
                        if (imageUri != null) {
                            val result = InvitationShare.shareToWhatsAppStatus(
                                context = context,
                                imageUri = imageUri,
                                message = state.selectedLanguage.shareMessage
                            )
                            viewModel.setStatusMessage(result.toStatusMessage("Opening WhatsApp Status"))
                        } else {
                            viewModel.setStatusMessage("Generate the invitation first")
                        }
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
    selectedLanguage: InvitationLanguage,
    onShareChat: () -> Unit,
    onShareStatus: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = selectedLanguage.editorShareTitle,
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
                        text = selectedLanguage.editorShareChat,
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
                        text = selectedLanguage.editorShareStatus,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        }
        Text(
            text = selectedLanguage.editorShareFallbackHint,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Composable
private fun PhotoUploadSection(
    hasPhoto: Boolean,
    hasMessage: Boolean,
    selectedLanguage: InvitationLanguage,
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
                text = selectedLanguage.editorPhotoSectionTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = when {
                    hasPhoto && hasMessage -> if (selectedLanguage == InvitationLanguage.TAMIL) {
                        "புகைப்படம் சேர்க்கப்பட்டது. கூடுதல் செய்தி அழைப்பிதழின் கீழ்ப்பகுதியில், அலங்காரத்துக்கு மேலே காட்டப்படும்."
                    } else {
                        "Photo selected. Your additional message is placed at the bottom of the card, above the decoration."
                    }
                    hasPhoto -> if (selectedLanguage == InvitationLanguage.TAMIL) {
                        "புகைப்படம் சேர்க்கப்பட்டது. உருவாக்கப்படும் அழைப்பிதழில் காட்டப்படும்."
                    } else {
                        "Photo selected. It will appear inside the generated invitation card."
                    }
                    else -> selectedLanguage.editorPhotoSectionHint
                },
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = onChoosePhoto) {
                    Text(text = if (hasPhoto) selectedLanguage.editorChangePhoto else selectedLanguage.editorUploadPhoto)
                }
                if (hasPhoto) {
                    TextButton(onClick = onRemovePhoto) {
                        Text(text = selectedLanguage.editorRemovePhoto)
                    }
                }
            }
        }
    }
}

@Composable
private fun LimitedOutlinedTextField(
    value: String,
    onValueChange: (String) -> Unit,
    maxLength: Int,
    modifier: Modifier = Modifier,
    label: @Composable () -> Unit,
    singleLine: Boolean = true,
    minLines: Int = 1,
    maxLines: Int = 1,
    placeholder: @Composable (() -> Unit)? = null,
    helperText: String? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    readOnly: Boolean = false,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    var lengthError by remember { mutableStateOf<String?>(null) }
    val isError = lengthError != null

    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.length <= maxLength) {
                lengthError = null
                onValueChange(newValue)
            } else {
                lengthError = "Maximum $maxLength characters allowed"
            }
        },
        modifier = modifier,
        label = label,
        singleLine = singleLine,
        minLines = minLines,
        maxLines = maxLines,
        placeholder = placeholder,
        readOnly = readOnly,
        isError = isError,
        keyboardOptions = keyboardOptions,
        trailingIcon = trailingIcon,
        supportingText = {
            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                lengthError?.let { error ->
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
                helperText?.let { helper ->
                    Text(
                        text = helper,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = "${value.length}/$maxLength",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (isError) {
                        MaterialTheme.colorScheme.error
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    },
                    fontWeight = FontWeight.Bold
                )
            }
        }
    )
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
    selectedCategoryId: String?,
    onLanguageSelected: (InvitationLanguage) -> Unit,
    onOccasionTitleChanged: (String) -> Unit,
    onNameChanged: (String) -> Unit,
    onDateChanged: (String) -> Unit,
    onTimeChanged: (String) -> Unit,
    onVenueChanged: (String) -> Unit,
    onMobileNumberChanged: (String) -> Unit,
    onMessageChanged: (String) -> Unit,
    hasUploadedPhoto: Boolean
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Text(
            text = selectedLanguage.editorDetailsTitle,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        LanguageSelector(
            selectedLanguage = selectedLanguage,
            onLanguageSelected = onLanguageSelected
        )
        LimitedOutlinedTextField(
            value = details.occasionTitle,
            onValueChange = onOccasionTitleChanged,
            maxLength = InvitationDetails.OCCASION_MAX_LENGTH,
            label = { Text(selectedLanguage.editorOccasionLabel) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(selectedLanguage.editorOccasionPlaceholder)
            },
            helperText = selectedLanguage.editorOccasionHelper.format(InvitationDetails.OCCASION_MAX_LENGTH)
        )
        LimitedOutlinedTextField(
            value = details.name,
            onValueChange = onNameChanged,
            maxLength = InvitationDetails.NAME_MAX_LENGTH,
            label = { Text(selectedLanguage.editorNameLabel) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(selectedLanguage.editorNamePlaceholder)
            },
            helperText = selectedLanguage.editorNameHelper.format(InvitationDetails.NAME_MAX_LENGTH)
        )
        DatePickerField(
            date = details.date,
            selectedLanguage = selectedLanguage,
            onDateSelected = onDateChanged
        )
        LimitedOutlinedTextField(
            value = details.time,
            onValueChange = onTimeChanged,
            maxLength = InvitationDetails.TIME_MAX_LENGTH,
            label = { Text(selectedLanguage.timeLabel) },
            modifier = Modifier.fillMaxWidth(),
            helperText = selectedLanguage.editorTimeHelper.format(InvitationDetails.TIME_MAX_LENGTH)
        )
        LimitedOutlinedTextField(
            value = details.venue,
            onValueChange = onVenueChanged,
            maxLength = InvitationDetails.VENUE_MAX_LENGTH,
            label = { Text(selectedLanguage.venueLabel) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false,
            minLines = InvitationDetails.VENUE_MAX_LINES,
            maxLines = InvitationDetails.VENUE_MAX_LINES,
            helperText = selectedLanguage.editorVenueHelper.format(
                InvitationDetails.VENUE_MAX_LINES,
                InvitationDetails.VENUE_MAX_LENGTH
            )
        )
        LimitedOutlinedTextField(
            value = details.mobileNumber,
            onValueChange = onMobileNumberChanged,
            maxLength = InvitationDetails.MOBILE_MAX_LENGTH,
            label = { Text(selectedLanguage.mobileLabel) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text("+91 98765 43210") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
            helperText = selectedLanguage.editorMobileHelper.format(InvitationDetails.MOBILE_MAX_LENGTH)
        )
        InviteMessageSection(
            message = details.message,
            selectedLanguage = selectedLanguage,
            selectedCategoryId = selectedCategoryId,
            hasUploadedPhoto = hasUploadedPhoto,
            onMessageChanged = onMessageChanged
        )
    }
}

@Composable
private fun InviteMessageSection(
    message: String,
    selectedLanguage: InvitationLanguage,
    selectedCategoryId: String?,
    hasUploadedPhoto: Boolean,
    onMessageChanged: (String) -> Unit
) {
    val maxMessageChars = InvitationFieldLimits.MESSAGE_MAX_LENGTH
    var selectedQuickMessageId by remember { mutableStateOf<String?>(null) }
    var selectedTone by remember { mutableStateOf(QuickMessageTone.ALL) }
    val quickMessages = remember(selectedLanguage, selectedCategoryId, selectedTone) {
        QuickInviteMessages.filter { quickMessage ->
            quickMessage.language == selectedLanguage &&
                (quickMessage.categoryId == null || quickMessage.categoryId == selectedCategoryId) &&
                (selectedTone == QuickMessageTone.ALL || quickMessage.tone == selectedTone)
        }
    }
    val selectedQuickMessage = QuickInviteMessages.firstOrNull { it.id == selectedQuickMessageId }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = selectedLanguage.editorMessageSectionTitle,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = selectedLanguage.editorMessageHint,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        if (hasUploadedPhoto) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = selectedLanguage.editorPhotoWithMessageHint.format(
                        maxMessageChars,
                        InvitationDetails.MESSAGE_MAX_LINES_ON_CARD
                    ),
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onTertiaryContainer
                )
            }
        }

        MessageToneFilters(
            selectedTone = selectedTone,
            selectedLanguage = selectedLanguage,
            onToneSelected = { tone ->
                selectedTone = tone
                selectedQuickMessageId = null
            }
        )

        QuickMessageChips(
            quickMessages = quickMessages,
            selectedMessageId = selectedQuickMessageId,
            onMessageSelected = { quickMessage ->
                selectedQuickMessageId = quickMessage.id
                onMessageChanged(quickMessage.text)
            }
        )

        LimitedOutlinedTextField(
            value = message,
            onValueChange = { updatedMessage ->
                if (selectedQuickMessage?.text != updatedMessage) {
                    selectedQuickMessageId = null
                }
                onMessageChanged(updatedMessage)
            },
            maxLength = maxMessageChars,
            label = { Text(selectedLanguage.editorMessageFieldLabel) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = false,
            minLines = 3,
            maxLines = 4,
            keyboardOptions = KeyboardOptions(
                capitalization = KeyboardCapitalization.Sentences,
                keyboardType = KeyboardType.Text
            ),
            helperText = if (hasUploadedPhoto) {
                selectedLanguage.editorMessageHelperWithPhoto
            } else {
                selectedLanguage.editorMessageHelperNoPhoto.format(
                    InvitationDetails.MESSAGE_MAX_LINES_ON_CARD
                )
            }
        )

        ImageSafeMessageCounter(
            characterCount = message.length,
            maxLength = maxMessageChars,
            selectedLanguage = selectedLanguage
        )
        InviteMessagePreview(
            message = message,
            selectedLanguage = selectedLanguage
        )
    }
}

@Composable
private fun QuickMessageChips(
    quickMessages: List<QuickInviteMessage>,
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
            items = quickMessages,
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
private fun MessageToneFilters(
    selectedTone: QuickMessageTone,
    selectedLanguage: InvitationLanguage,
    onToneSelected: (QuickMessageTone) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        items(
            items = QuickMessageTone.entries,
            key = { tone -> tone.name }
        ) { tone ->
            FilterChip(
                selected = tone == selectedTone,
                onClick = { onToneSelected(tone) },
                label = { Text(text = tone.localizedLabel(selectedLanguage)) },
                modifier = Modifier.animateContentSize()
            )
        }
    }
}

@Composable
private fun ImageSafeMessageCounter(
    characterCount: Int,
    maxLength: Int,
    selectedLanguage: InvitationLanguage
) {
    val isAtLimit = characterCount >= maxLength
    val counterColor = if (isAtLimit) {
        MaterialTheme.colorScheme.error
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }
    val guidance = if (isAtLimit) {
        selectedLanguage.editorMessageAtLimit
    } else {
        selectedLanguage.editorMessageGoodLength
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = guidance,
            style = MaterialTheme.typography.bodySmall,
            color = counterColor
        )
        Text(
            text = "$characterCount/$maxLength",
            style = MaterialTheme.typography.labelMedium,
            color = counterColor,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun InviteMessagePreview(
    message: String,
    selectedLanguage: InvitationLanguage
) {
    val previewMessage = message.ifBlank {
        selectedLanguage.editorMessagePreviewEmpty
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
                text = selectedLanguage.editorMessagePreviewTitle,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = previewMessage,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onTertiaryContainer,
                maxLines = InvitationDetails.MESSAGE_MAX_LINES_ON_CARD + 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DatePickerField(
    date: String,
    selectedLanguage: InvitationLanguage,
    onDateSelected: (String) -> Unit
) {
    var showDatePicker by remember { mutableStateOf(false) }
    val datePickerState = rememberDatePickerState()

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        OutlinedTextField(
            value = date,
            onValueChange = {},
            label = { Text(selectedLanguage.dateLabel) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = true,
            placeholder = { Text(selectedLanguage.editorDatePlaceholder) },
            trailingIcon = {
                TextButton(onClick = { showDatePicker = true }) {
                    Text(text = selectedLanguage.editorDatePickButton)
                }
            }
        )
        Text(
            text = selectedLanguage.editorDateHelper,
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
                            onDateSelected(formatDate(millis, selectedLanguage))
                        }
                        showDatePicker = false
                    }
                ) {
                    Text(text = selectedLanguage.editorDialogOk)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text(text = selectedLanguage.editorDialogCancel)
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

private fun formatDate(dateMillis: Long, language: InvitationLanguage): String {
    val locale = when (language) {
        InvitationLanguage.TAMIL -> Locale.forLanguageTag("ta-IN")
        InvitationLanguage.ENGLISH -> Locale.ENGLISH
    }
    val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", locale)
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
            text = selectedLanguage.editorLanguageSectionTitle,
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
            text = selectedLanguage.editorLanguageHint,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
