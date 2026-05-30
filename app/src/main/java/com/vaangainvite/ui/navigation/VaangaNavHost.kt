package com.vaangainvite.ui.navigation

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
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

private fun NavController.returnFromPhotoCrop() {
    if (!popBackStack(Routes.EDITOR, inclusive = false)) {
        popBackStack()
    }
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
                    navController.navigate(Routes.TEMPLATES) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Routes.TEMPLATES) {
            TemplateSelectionScreen(
                viewModel = viewModel,
                onBack = navController::popBackStack,
                onTemplateSelected = {
                    navController.navigate(Routes.EDITOR) {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(Routes.EDITOR) {
            EditorScreen(
                viewModel = viewModel,
                onBack = navController::popBackStack,
                onNavigateToPhotoCrop = { sourceUri ->
                    val encodedUri = Uri.encode(sourceUri.toString())
                    navController.navigate("${Routes.PHOTO_CROP}?sourceUri=$encodedUri") {
                        launchSingleTop = true
                    }
                }
            )
        }
        composable(
            route = "${Routes.PHOTO_CROP}?sourceUri={sourceUri}",
            arguments = listOf(
                navArgument("sourceUri") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val encodedUri = backStackEntry.arguments?.getString("sourceUri")
            val sourceUri = encodedUri
                ?.takeIf { it.isNotBlank() }
                ?.let { Uri.parse(Uri.decode(it)) }

            if (sourceUri == null) {
                LaunchedEffect(Unit) {
                    navController.returnFromPhotoCrop()
                }
                return@composable
            }

            PhotoCropScreen(
                sourceUri = sourceUri,
                templateId = state.selectedTemplate?.id.orEmpty(),
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
                    navController.returnFromPhotoCrop()
                },
                onCancel = {
                    viewModel.clearCropSession()
                    navController.returnFromPhotoCrop()
                }
            )
        }
    }
}
