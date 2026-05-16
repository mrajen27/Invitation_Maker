package com.vaangainvite.ui.viewmodel

import android.app.Application
import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import com.vaangainvite.core.image.InvitationImageGenerator
import com.vaangainvite.data.model.InvitationCategory
import com.vaangainvite.data.model.InvitationDetails
import com.vaangainvite.data.model.InvitationTemplate
import com.vaangainvite.data.repository.TemplateRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class InviteUiState(
    val categories: List<InvitationCategory> = emptyList(),
    val templates: List<InvitationTemplate> = emptyList(),
    val selectedCategory: InvitationCategory? = null,
    val selectedTemplate: InvitationTemplate? = null,
    val details: InvitationDetails = InvitationDetails(),
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

    fun updateName(name: String) = updateDetails { copy(name = name) }

    fun updateDate(date: String) = updateDetails { copy(date = date) }

    fun updateTime(time: String) = updateDetails { copy(time = time) }

    fun updateVenue(venue: String) = updateDetails { copy(venue = venue) }

    fun updateMessage(message: String) = updateDetails { copy(message = message) }

    fun generateInvitation() {
        createBitmapAndCache(successMessage = "Invitation image generated")
    }

    fun saveInvitationToGallery() {
        val bitmap = _uiState.value.generatedBitmap ?: createBitmapAndCache(
            successMessage = null
        ) ?: return

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

    fun getShareImageUri(): Uri? {
        val cachedUri = _uiState.value.cachedImageUri
        if (cachedUri != null) return cachedUri

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

    private fun createBitmapAndCache(successMessage: String?): Bitmap? {
        val state = _uiState.value
        val template = state.selectedTemplate
        if (template == null) {
            _uiState.update { current ->
                current.copy(statusMessage = "Choose a template first")
            }
            return null
        }

        return runCatching {
            val bitmap = imageGenerator.createInvitationBitmap(template, state.details)
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
}
