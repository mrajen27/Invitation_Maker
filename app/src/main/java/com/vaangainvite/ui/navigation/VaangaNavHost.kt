package com.vaangainvite.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.vaangainvite.ui.screens.EditorScreen
import com.vaangainvite.ui.screens.HomeScreen
import com.vaangainvite.ui.screens.TemplateSelectionScreen
import com.vaangainvite.ui.viewmodel.InviteViewModel

private object Routes {
    const val HOME = "home"
    const val TEMPLATES = "templates"
    const val EDITOR = "editor"
}

@Composable
fun VaangaNavHost(viewModel: InviteViewModel) {
    val navController = rememberNavController()

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
                onBack = navController::popBackStack
            )
        }
    }
}
