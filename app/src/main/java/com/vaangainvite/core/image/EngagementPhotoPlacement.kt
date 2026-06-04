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

    private val defaultBounds = RectF(258f, 128f, 822f, 508f)

    private val specs = mapOf(
        "engagement_01" to Spec(defaultBounds),
        "engagement_02" to Spec(defaultBounds),
        "engagement_03" to Spec(defaultBounds),
        "engagement_04" to Spec(defaultBounds, border = Border.INNER_GLOW),
        "engagement_05" to Spec(defaultBounds, border = Border.INNER_GLOW)
    )

    fun specFor(templateId: String): Spec {
        return specs[templateId] ?: Spec(defaultBounds)
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
