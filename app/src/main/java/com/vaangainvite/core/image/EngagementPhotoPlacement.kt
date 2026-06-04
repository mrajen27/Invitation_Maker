package com.vaangainvite.core.image

import android.graphics.Path
import android.graphics.RectF

/**
 * Per-template photo arch masks for designed engagement WebP backgrounds (1080×1350).
 * Matches artwork openings on [bg_engagement_01] … [bg_engagement_05].
 */
internal object EngagementPhotoPlacement {

    enum class Mask {
        CEREMONY_ARCH
    }

    enum class Border {
        ARTWORK_FRAME,
        INNER_GLOW
    }

    data class Spec(
        val bounds: RectF,
        val mask: Mask = Mask.CEREMONY_ARCH,
        val border: Border = Border.ARTWORK_FRAME
    )

    private val specs = mapOf(
        "engagement_01" to Spec(RectF(312f, 128f, 768f, 448f)),
        "engagement_02" to Spec(RectF(308f, 122f, 772f, 452f)),
        "engagement_03" to Spec(RectF(318f, 135f, 762f, 445f)),
        "engagement_04" to Spec(
            bounds = RectF(322f, 130f, 758f, 450f),
            border = Border.INNER_GLOW
        ),
        "engagement_05" to Spec(RectF(315f, 125f, 765f, 448f))
    )

    fun specFor(templateId: String): Spec {
        return specs[templateId] ?: Spec(RectF(312f, 128f, 768f, 448f))
    }

    fun clipPath(spec: Spec): Path = archPath(spec.bounds)

    private fun archPath(frame: RectF): Path {
        val rise = frame.width() * 0.2f
        return Path().apply {
            moveTo(frame.left, frame.bottom)
            lineTo(frame.left, frame.top + rise)
            quadTo(frame.left, frame.top, frame.centerX(), frame.top)
            quadTo(frame.right, frame.top, frame.right, frame.top + rise)
            lineTo(frame.right, frame.bottom)
            close()
        }
    }
}
