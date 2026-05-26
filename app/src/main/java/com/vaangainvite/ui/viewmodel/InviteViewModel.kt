package com.vaangainvite.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.vaangainvite.core.image.InvitationImageGenerator
import com.vaangainvite.data.model.InvitationCategory
import com.vaangainvite.data.model.InvitationDetails
import com.vaangainvite.data.model.validationError
import com.vaangainvite.data.model.InvitationLanguage
import com.vaangainvite.data.model.InvitationTemplate
import com.vaangainvite.data.repository.TemplateRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class InviteUiState(
    val categories: List<InvitationCategory> = emptyList(),
    val templates: List<InvitationTemplate> = emptyList(),
    val selectedCategory: InvitationCategory? = null,
    val selectedTemplate: InvitationTemplate? = null,
    val selectedLanguage: InvitationLanguage = InvitationLanguage.ENGLISH,
    val details: InvitationDetails = InvitationDetails(),
    val uploadedPhotoUri: Uri? = null,
    val generatedBitmap: Bitmap? = null,
    val cachedImageUri: Uri? = null,
    val statusMessage: String? = null
)

class InviteViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = TemplateRepository()
    private val imageGenerator = InvitationImageGenerator(application.applicationContext)

    private val _uiState = MutableStateFlow(
        InviteUiState(categories = repository.categories)
    )
    val uiState: StateFlow<InviteUiState> = _uiState.asStateFlow()

    fun selectCategory(categoryId: String) {
        val category = repository.categoryById(categoryId) ?: return
        _uiState.update { current ->
            current.copy(
                selectedCategory = category,
                templates = repository.templatesForCategory(categoryId),
                selectedTemplate = null,
                details = current.details.copy(
                    occasionTitle = defaultOccasionTitle(categoryId, current.selectedLanguage)
                ),
                generatedBitmap = null,
                cachedImageUri = null,
                statusMessage = null
            )
        }
    }

    fun selectTemplate(templateId: String) {
        val template = repository.templateById(templateId) ?: return
        _uiState.update { current ->
            current.copy(
                selectedTemplate = template,
                generatedBitmap = null,
                cachedImageUri = null,
                statusMessage = null
            )
        }
    }

    fun updateOccasionTitle(occasionTitle: String) = updateDetails {
        copy(occasionTitle = occasionTitle.take(InvitationDetails.OCCASION_MAX_LENGTH))
    }

    fun updateName(name: String) = updateDetails {
        copy(name = name.take(InvitationDetails.NAME_MAX_LENGTH))
    }

    fun updateDate(date: String) = updateDetails {
        copy(date = date.take(InvitationDetails.DATE_MAX_LENGTH))
    }

    fun updateTime(time: String) = updateDetails {
        copy(time = time.take(InvitationDetails.TIME_MAX_LENGTH))
    }

    fun updateVenue(venue: String) = updateDetails {
        copy(venue = venue.take(InvitationDetails.VENUE_MAX_LENGTH))
    }

    fun updateMobileNumber(mobileNumber: String) = updateDetails {
        copy(mobileNumber = mobileNumber.take(InvitationDetails.MOBILE_MAX_LENGTH))
    }

    fun updateMessage(message: String) = updateDetails {
        copy(message = message.take(InvitationDetails.MESSAGE_MAX_LENGTH))
    }

    fun updateUploadedPhoto(uri: Uri?) {
        _uiState.update { current ->
            current.copy(
                uploadedPhotoUri = uri,
                generatedBitmap = null,
                cachedImageUri = null,
                statusMessage = if (uri == null) "Photo removed" else "Photo added to invitation"
            )
        }
    }

    fun selectLanguage(language: InvitationLanguage) {
        _uiState.update { current ->
            val selectedCategoryId = current.selectedCategory?.id
            val occasionTitle = if (
                selectedCategoryId != null &&
                isDefaultOccasionTitle(current.details.occasionTitle)
            ) {
                defaultOccasionTitle(selectedCategoryId, language)
            } else {
                current.details.occasionTitle
            }
            current.copy(
                selectedLanguage = language,
                details = current.details.copy(occasionTitle = occasionTitle),
                generatedBitmap = null,
                cachedImageUri = null,
                statusMessage = null
            )
        }
    }

    fun generateInvitation() {
        viewModelScope.launch {
            createBitmapAndCache(successMessage = "Invitation image generated")
        }
    }

    fun saveInvitationToGallery() {
        viewModelScope.launch {
            val bitmap = _uiState.value.generatedBitmap ?: createBitmapAndCache(
                successMessage = null
            ) ?: return@launch

            runCatching {
                imageGenerator.saveBitmapToGallery(bitmap)
            }.onSuccess {
                _uiState.update { current ->
                    current.copy(statusMessage = "Saved to gallery")
                }
            }.onFailure { error ->
                _uiState.update { current ->
                    current.copy(statusMessage = error.message ?: "Unable to save invitation")
                }
            }
        }
    }

    suspend fun getOrCreateShareImageUri(): Uri? {
        _uiState.value.cachedImageUri?.let { return it }
        createBitmapAndCache(successMessage = null)
        return _uiState.value.cachedImageUri
    }

    fun setStatusMessage(message: String) {
        _uiState.update { current ->
            current.copy(statusMessage = message)
        }
    }

    fun clearStatusMessage() {
        _uiState.update { current ->
            current.copy(statusMessage = null)
        }
    }

    private fun updateDetails(update: InvitationDetails.() -> InvitationDetails) {
        _uiState.update { current ->
            current.copy(
                details = current.details.update(),
                generatedBitmap = null,
                cachedImageUri = null
            )
        }
    }

    private suspend fun createBitmapAndCache(successMessage: String?): Bitmap? = withContext(Dispatchers.Default) {
        val state = _uiState.value
        val template = state.selectedTemplate
        if (template == null) {
            _uiState.update { current ->
                current.copy(statusMessage = "Choose a template first")
            }
            return@withContext null
        }

        state.details.validationError()?.let { errorMessage ->
            _uiState.update { current ->
                current.copy(statusMessage = errorMessage)
            }
            return@withContext null
        }

        runCatching {
            val bitmap = imageGenerator.createInvitationBitmap(
                template = template,
                details = state.details,
                language = state.selectedLanguage,
                uploadedPhotoUri = state.uploadedPhotoUri
            )
            val uri = imageGenerator.saveBitmapToCache(bitmap)
            bitmap to uri
        }.fold(
            onSuccess = { (bitmap, uri) ->
                _uiState.update { current ->
                    current.copy(
                        generatedBitmap = bitmap,
                        cachedImageUri = uri,
                        statusMessage = successMessage
                    )
                }
                bitmap
            },
            onFailure = { error ->
                _uiState.update { current ->
                    current.copy(statusMessage = error.message ?: "Unable to generate invitation")
                }
                null
            }
        )
    }

    private fun defaultOccasionTitle(categoryId: String, language: InvitationLanguage): String {
        return when (language) {
            InvitationLanguage.ENGLISH -> when (categoryId) {
                "birthday" -> "Birthday Celebration"
                "wedding" -> "Wedding Invitation"
                "housewarming" -> "Housewarming Ceremony"
                "puberty" -> "Puberty Ceremony"
                else -> "Special Occasion"
            }
            InvitationLanguage.TAMIL -> when (categoryId) {
                "birthday" -> "பிறந்தநாள் விழா"
                "wedding" -> "திருமண அழைப்பிதழ்"
                "housewarming" -> "புதுமனை புகுவிழா"
                "puberty" -> "பூப்புனித நீராட்டு விழா"
                else -> "சிறப்பு விழா"
            }
        }
    }

    private fun isDefaultOccasionTitle(title: String): Boolean {
        val defaultTitles = listOf(
            "Birthday Celebration",
            "Wedding Invitation",
            "Housewarming Ceremony",
            "Puberty Ceremony",
            "Special Occasion",
            "பிறந்தநாள் விழா",
            "திருமண அழைப்பிதழ்",
            "புதுமனை புகுவிழா",
            "பூப்புனித நீராட்டு விழா",
            "சிறப்பு விழா"
        )
        return title.isBlank() || title in defaultTitles
    }
}
