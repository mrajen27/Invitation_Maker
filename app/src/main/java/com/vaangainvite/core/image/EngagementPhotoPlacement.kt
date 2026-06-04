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
        "engagement_01" to Spec(RectF(298f, 118f, 782f, 450f)),
        "engagement_02" to Spec(RectF(294f, 112f, 786f, 454f)),
        "engagement_03" to Spec(RectF(302f, 120f, 778f, 448f)),
        "engagement_04" to Spec(
            bounds = RectF(300f, 115f, 780f, 452f),
            border = Border.INNER_GLOW
        ),
        "engagement_05" to Spec(RectF(296f, 118f, 784f, 450f))
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
