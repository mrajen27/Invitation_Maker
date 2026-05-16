package com.vaangainvite.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.vaangainvite.ui.viewmodel.InviteViewModel

@Composable
fun TemplateSelectionScreen(
    viewModel: InviteViewModel,
    onBack: () -> Unit,
    onTemplateSelected: () -> Unit
) {
    Text(text = "Choose a template")
}
