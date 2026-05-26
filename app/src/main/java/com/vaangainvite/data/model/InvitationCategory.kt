package com.vaangainvite.data.model

import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes

data class InvitationCategory(
    val id: String,
    val title: String,
    val description: String,
    @ColorInt val accentColor: Int,
    @DrawableRes val iconResId: Int
)
