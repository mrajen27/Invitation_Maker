package com.vaangainvite.ui

import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import com.vaangainvite.ui.navigation.VaangaNavHost
import com.vaangainvite.ui.theme.VaangaInviteTheme
import com.vaangainvite.ui.viewmodel.InviteViewModel

@Composable
fun VaangaInviteApp(
    viewModel: InviteViewModel = viewModel()
) {
    VaangaInviteTheme {
        Surface {
            VaangaNavHost(viewModel = viewModel)
        }
    }
}
