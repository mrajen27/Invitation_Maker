package com.vaangainvite.data.model

import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes

data class InvitationTemplate(
    val id: String,
    val categoryId: String,
    val title: String,
    val description: String,
    @DrawableRes val drawableResId: Int,
    @ColorInt val primaryColor: Int,
    @DrawableRes val backgroundResId: Int? = null,
    /** Cream/gold text on dark photo backgrounds (e.g. teal night engagement). */
    val usesLightText: Boolean = false
) {
    fun previewResId(): Int = backgroundResId ?: drawableResId

    fun usesPhotoBackground(): Boolean = backgroundResId != null
}
