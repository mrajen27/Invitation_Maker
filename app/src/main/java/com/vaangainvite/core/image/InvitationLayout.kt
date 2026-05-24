package com.vaangainvite.core.image

import android.graphics.RectF

/**
 * Layout zones for invitation text — keeps copy in the center safe area so edge
 * artwork (balloons, florals, etc.) stays visible.
 */
internal object InvitationLayout {
    const val canvasWidth = 1080f
    const val canvasHeight = 1350f

    object Spacing {
        const val afterIntro = 6f
        /** Tight gap between occasion title and honoree name. */
        const val afterOccasion = 2f
        /** Gap between name and date/details block. */
        const val afterName = 12f
        const val betweenDetails = 6f
        const val beforeMessage = 20f
    }

    /** Center column for photo-style WebP backgrounds (no overlay box). */
    fun photoTextZone(hasUploadedPhoto: Boolean): RectF {
        return RectF(
            220f,
            if (hasUploadedPhoto) 492f else 368f,
            860f,
            1260f
        )
    }

    fun photoFrame(): RectF = RectF(390f, 292f, 690f, 478f)

    /** Narrower frosted panel for vector / painted backgrounds. */
    fun classicTextZone(hasUploadedPhoto: Boolean): RectF {
        return RectF(
            190f,
            if (hasUploadedPhoto) 300f else 280f,
            890f,
            1160f
        )
    }
}
