package com.vaangainvite.ui.screens

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.vaangainvite.ui.viewmodel.InviteViewModel

@Composable
fun HomeScreen(
    viewModel: InviteViewModel,
    onCategorySelected: () -> Unit
) {
    Text(text = "Vaanga Invite")
}
