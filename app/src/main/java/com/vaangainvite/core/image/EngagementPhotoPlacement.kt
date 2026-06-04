package com.vaangainvite.core.image

import android.graphics.Path
import android.graphics.RectF

/**
 * Photo clip and bounds for engagement WebP cards (top arch + cream text band below).
 * Other categories keep using [InvitationLayout.photoFrame] with a rounded rectangle.
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
        "engagement_01" to Spec(RectF(292f, 118f, 788f, 442f)),
        "engagement_02" to Spec(RectF(290f, 115f, 790f, 440f)),
        "engagement_03" to Spec(RectF(294f, 118f, 786f, 442f)),
        "engagement_04" to Spec(
            bounds = RectF(296f, 112f, 784f, 438f),
            border = Border.INNER_GLOW
        ),
        "engagement_05" to Spec(
            bounds = RectF(294f, 110f, 786f, 436f),
            border = Border.INNER_GLOW
        )
    )

    fun specFor(templateId: String): Spec {
        return specs[templateId] ?: Spec(RectF(298f, 118f, 782f, 450f))
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
