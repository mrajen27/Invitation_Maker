package com.vaangainvite.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vaangainvite.ui.screens.EditorScreen
import com.vaangainvite.ui.screens.HomeScreen
import com.vaangainvite.ui.screens.PhotoCropScreen
import com.vaangainvite.ui.screens.TemplateSelectionScreen
import com.vaangainvite.ui.viewmodel.InviteViewModel

private object Routes {
    const val HOME = "home"
    const val TEMPLATES = "templates"
    const val EDITOR = "editor"
    const val PHOTO_CROP = "photo_crop"
}

@Composable
fun VaangaNavHost(viewModel: InviteViewModel) {
    val navController = rememberNavController()
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME) {
            HomeScreen(
                viewModel = viewModel,
                onCategorySelected = {
                    navController.navigate(Routes.TEMPLATES)
                }
            )
        }
        composable(Routes.TEMPLATES) {
            TemplateSelectionScreen(
                viewModel = viewModel,
                onBack = navController::popBackStack,
                onTemplateSelected = {
                    navController.navigate(Routes.EDITOR)
                }
            )
        }
        composable(Routes.EDITOR) {
            EditorScreen(
                viewModel = viewModel,
                onBack = navController::popBackStack,
                onNavigateToPhotoCrop = {
                    navController.navigate(Routes.PHOTO_CROP)
                }
            )
        }
        composable(Routes.PHOTO_CROP) {
            val sourceUri = state.cropSourceUri
            if (sourceUri != null) {
                PhotoCropScreen(
                    sourceUri = sourceUri,
                    language = state.selectedLanguage,
                    initialTransform = state.photoCropTransform?.takeIf {
                        state.originalPhotoUri == sourceUri
                    },
                    onConfirm = { croppedUri, transform ->
                        viewModel.confirmPhotoCrop(
                            croppedUri = croppedUri,
                            transform = transform,
                            originalUri = state.originalPhotoUri ?: sourceUri
                        )
                        navController.popBackStack()
                    },
                    onCancel = {
                        viewModel.clearCropSession()
                        navController.popBackStack()
                    }
                )
            } else {
                navController.popBackStack()
            }
        }
    }
}
