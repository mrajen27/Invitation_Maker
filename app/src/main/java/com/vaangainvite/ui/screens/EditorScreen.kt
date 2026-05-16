package com.vaangainvite.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.vaangainvite.ui.viewmodel.InviteViewModel

@Composable
fun EditorScreen(
    viewModel: InviteViewModel,
    onBack: () -> Unit
) {
    Text(text = "Create invitation")
}
