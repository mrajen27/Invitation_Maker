package com.vaangainvite.core.image

import android.graphics.RectF

/**
 * Layout zones for invitation text — keeps copy in the center safe area so edge
 * artwork (balloons, florals, etc.) stays visible.
 */
internal object InvitationLayout {
    const val canvasWidth = 1080f
    const val canvasHeight = 1350f

    /** Center column for photo-style WebP backgrounds (no overlay box). */
    fun photoTextZone(hasUploadedPhoto: Boolean): RectF {
        return RectF(
            220f,
            if (hasUploadedPhoto) 500f else 400f,
            860f,
            1220f
        )
    }

    fun photoFrame(): RectF = RectF(390f, 300f, 690f, 480f)

    /** Narrower frosted panel for vector / painted backgrounds. */
    fun classicTextZone(): RectF = RectF(190f, 280f, 890f, 1160f)
}
