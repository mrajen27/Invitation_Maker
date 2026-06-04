package com.vaangainvite.core.image

import android.graphics.Path
import android.graphics.RectF

/**
 * Designed WebP photo cards (engagement, naming, …) — plain rectangle on continuous cream panel.
 */
internal object PhotoCardPlacement {

    enum class Mask {
        TRADITIONAL_RECT
    }

    data class Spec(
        val bounds: RectF,
        val mask: Mask = Mask.TRADITIONAL_RECT
    )

    /** Measured on 1080×1350 WebPs (width-preserved conversion keeps side borders). */
    private val traditionalPhoto = RectF(248f, 375f, 830f, 555f)

    private val templateIds = (1..5).flatMap { i ->
        listOf(
            "engagement_%02d".format(i),
            "naming_%02d".format(i),
            "babyshower_%02d".format(i)
        )
    }

    private val specs = templateIds.associateWith { Spec(bounds = traditionalPhoto) }

    fun isPhotoCardTemplate(templateId: String): Boolean =
        templateId.startsWith("engagement_") ||
            templateId.startsWith("naming_") ||
            templateId.startsWith("babyshower_")

    fun specFor(templateId: String): Spec {
        return specs[templateId] ?: Spec(bounds = traditionalPhoto)
    }

    fun clipPath(spec: Spec): Path {
        val frame = spec.bounds
        return Path().apply { addRect(frame, Path.Direction.CW) }
    }

    fun cropAspectRatio(templateId: String): Float {
        val frame = specFor(templateId).bounds
        return frame.width() / frame.height()
    }

    fun photoBottom(templateId: String): Float = specFor(templateId).bounds.bottom
}
