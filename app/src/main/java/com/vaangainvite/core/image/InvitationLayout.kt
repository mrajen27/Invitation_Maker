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
        const val beforeMessageNoPhoto = 10f
    }

    private data class TextZoneSpec(
        val left: Float,
        val right: Float,
        val topNoPhoto: Float,
        val topWithPhoto: Float,
        val bottom: Float = 1240f,
        val messageBottom: Float = 1160f,
        val topInset: Float = 4f
    )

    /**
     * Per-template safe areas measured from WebP cream panels (1080×1350).
     * Temple Mandapam has a deep arch — text must start below the inner gold lip.
     */
    private val templateTextZones = mapOf(
        "wedding_02" to TextZoneSpec(
            left = 210f,
            right = 870f,
            topNoPhoto = 478f,
            topWithPhoto = 528f,
            bottom = 1240f,
            messageBottom = 1200f,
            topInset = 8f
        ),
        "wedding_04" to TextZoneSpec(left = 220f, right = 860f, topNoPhoto = 288f, topWithPhoto = 492f),
        "wedding_05" to TextZoneSpec(left = 468f, right = 612f, topNoPhoto = 368f, topWithPhoto = 492f),
        "housewarming_03" to TextZoneSpec(left = 220f, right = 860f, topNoPhoto = 252f, topWithPhoto = 492f),
        "housewarming_05" to TextZoneSpec(left = 220f, right = 860f, topNoPhoto = 292f, topWithPhoto = 492f),
        "puberty_01" to TextZoneSpec(left = 290f, right = 790f, topNoPhoto = 280f, topWithPhoto = 492f),
    )

    private val templatePhotoFrames = mapOf(
        // Lower/wider slot inside the temple arch cream panel.
        "wedding_02" to RectF(360f, 308f, 720f, 498f)
    )

    fun photoFrame(templateId: String = ""): RectF {
        return templatePhotoFrames[templateId] ?: defaultPhotoFrame()
    }

    fun defaultPhotoFrame(): RectF = RectF(390f, 292f, 690f, 478f)

    /**
     * Horizontal + bottom inset for the additional message on photo WebP templates.
     */
    fun photoMessageSafeArea(templateId: String): RectF {
        val zone = photoTextZone(templateId, hasUploadedPhoto = false)
        val spec = templateTextZones[templateId]
        val bottom = spec?.messageBottom ?: 1160f
        return RectF(
            zone.left + 32f,
            0f,
            zone.right - 32f,
            bottom
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

    /** First line top Y, kept below photo frame and arch artwork when needed. */
    fun textStartY(templateId: String, hasUploadedPhoto: Boolean): Float {
        val zone = photoTextZone(templateId, hasUploadedPhoto)
        val spec = templateTextZones[templateId]
        val inset = spec?.topInset ?: 4f
        if (!hasUploadedPhoto) {
            return zone.top + inset
        }
        val frameBottom = photoFrame(templateId).bottom
        return maxOf(zone.top, frameBottom + 22f) + inset
    }

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
