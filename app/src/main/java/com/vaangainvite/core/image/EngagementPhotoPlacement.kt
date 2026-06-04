package com.vaangainvite.core.image

import android.graphics.Path
import android.graphics.RectF

/**
 * Engagement photo slots — plain vertical rectangle on continuous cream panel.
 * No arch, circle, oval, hex, or rounded ring.
 */
internal object EngagementPhotoPlacement {

    enum class Mask {
        TRADITIONAL_RECT
    }

    enum class Border {
        ARTWORK_FRAME,
        INNER_GLOW
    }

    data class Spec(
        val bounds: RectF,
        val mask: Mask = Mask.TRADITIONAL_RECT,
        val border: Border = Border.ARTWORK_FRAME
    )

    /** Measured on designed 1080×1350 WebP cream panels (no inner frame). */
    private val traditionalPhoto = RectF(184f, 147f, 894f, 554f)

    private val specs = mapOf(
        "engagement_01" to Spec(bounds = traditionalPhoto),
        "engagement_02" to Spec(bounds = traditionalPhoto),
        "engagement_03" to Spec(bounds = traditionalPhoto),
        "engagement_04" to Spec(bounds = traditionalPhoto),
        "engagement_05" to Spec(bounds = traditionalPhoto)
    )

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
