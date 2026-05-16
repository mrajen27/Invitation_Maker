package com.vaangainvite.data.model

import androidx.annotation.ColorInt

data class InvitationCategory(
    val id: String,
    val title: String,
    val description: String,
    @ColorInt val accentColor: Int
)
