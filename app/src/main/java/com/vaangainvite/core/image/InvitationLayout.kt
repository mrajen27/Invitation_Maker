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
        const val beforeMessage = 16f
        /** Gap between venue block and message when no photo is on the card. */
        const val beforeMessageNoPhoto = 28f
    }

    private data class TextZoneSpec(
        val left: Float,
        val right: Float,
        val topNoPhoto: Float,
        val topWithPhoto: Float,
        val bottom: Float = 1240f
    )

    /**
     * Per-template safe areas measured from WebP cream panels (1080×1350).
     * Temple / arch templates need a lower start so intro text clears the frame.
     */
    private val templateTextZones = mapOf(
        "wedding_02" to TextZoneSpec(left = 210f, right = 870f, topNoPhoto = 438f, topWithPhoto = 508f),
        "wedding_04" to TextZoneSpec(left = 220f, right = 860f, topNoPhoto = 288f, topWithPhoto = 492f),
        "wedding_05" to TextZoneSpec(left = 468f, right = 612f, topNoPhoto = 368f, topWithPhoto = 492f),
        "housewarming_03" to TextZoneSpec(left = 220f, right = 860f, topNoPhoto = 252f, topWithPhoto = 492f),
        "housewarming_05" to TextZoneSpec(left = 220f, right = 860f, topNoPhoto = 292f, topWithPhoto = 492f),
        "puberty_01" to TextZoneSpec(left = 290f, right = 790f, topNoPhoto = 280f, topWithPhoto = 492f),
    )

    /**
     * Horizontal + bottom inset for the additional message on photo WebP templates
     * (peacock/pillar side art and bottom mandap/gold motifs).
     */
    fun photoMessageSafeArea(templateId: String): RectF {
        val zone = photoTextZone(templateId, hasUploadedPhoto = false)
        return RectF(
            zone.left + 40f,
            0f,
            zone.right - 40f,
            1160f
        )
    }

    fun classicMessageSafeArea(): RectF = RectF(
        250f,
        0f,
        830f,
        1070f
    )

    fun messageSafeArea(usesPhotoBackground: Boolean, templateId: String): RectF {
        return if (usesPhotoBackground) photoMessageSafeArea(templateId) else classicMessageSafeArea()
    }

    /** Center column for photo-style WebP backgrounds (no overlay box). */
    fun photoTextZone(templateId: String, hasUploadedPhoto: Boolean): RectF {
        val spec = templateTextZones[templateId]
        if (spec != null) {
            return RectF(
                spec.left,
                if (hasUploadedPhoto) spec.topWithPhoto else spec.topNoPhoto,
                spec.right,
                spec.bottom
            )
        }
        return RectF(
            220f,
            if (hasUploadedPhoto) 492f else 368f,
            860f,
            1240f
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
