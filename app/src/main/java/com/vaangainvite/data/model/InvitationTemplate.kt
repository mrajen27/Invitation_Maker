package com.vaangainvite.data.model

import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes

data class InvitationTemplate(
    val id: String,
    val categoryId: String,
    val title: String,
    val description: String,
    @DrawableRes val drawableResId: Int,
    @ColorInt val primaryColor: Int
)
