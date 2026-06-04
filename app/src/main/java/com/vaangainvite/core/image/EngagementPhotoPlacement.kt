package com.vaangainvite.core.image

import android.graphics.Path
import android.graphics.RectF
import kotlin.math.cos
import kotlin.math.sin

/**
 * Per-template photo masks for engagement WebP cards — circle, oval, arch, hex, rounded rect.
 */
internal object EngagementPhotoPlacement {

    enum class Mask {
        MEDALLION_CIRCLE,
        PORTRAIT_OVAL,
        CEREMONY_ARCH,
        HEX_MEDALLION,
        ROUNDED_RECT
    }

    enum class Border {
        ARTWORK_FRAME,
        INNER_GLOW
    }

    data class Spec(
        val bounds: RectF,
        val mask: Mask,
        val border: Border = Border.ARTWORK_FRAME,
        val roundRectRadius: Float = 36f,
        /** Bottom of ornamental frame (gold ring / toran) — text starts below this. */
        val ornamentBottom: Float
    )

    private val specs = mapOf(
        "engagement_01" to Spec(
            bounds = RectF(384f, 186f, 696f, 498f),
            mask = Mask.MEDALLION_CIRCLE,
            ornamentBottom = 738f
        ),
        "engagement_02" to Spec(
            bounds = RectF(285f, 152f, 795f, 478f),
            mask = Mask.ROUNDED_RECT,
            roundRectRadius = 22f,
            ornamentBottom = 688f
        ),
        "engagement_03" to Spec(
            bounds = RectF(318f, 122f, 762f, 438f),
            mask = Mask.CEREMONY_ARCH,
            ornamentBottom = 468f
        ),
        "engagement_04" to Spec(
            bounds = RectF(350f, 155f, 730f, 495f),
            mask = Mask.HEX_MEDALLION,
            border = Border.INNER_GLOW,
            ornamentBottom = 528f
        ),
        "engagement_05" to Spec(
            bounds = RectF(273f, 210f, 805f, 518f),
            mask = Mask.ROUNDED_RECT,
            roundRectRadius = 40f,
            ornamentBottom = 548f
        )
    )

    fun specFor(templateId: String): Spec {
        return specs[templateId] ?: Spec(
            bounds = RectF(340f, 145f, 740f, 545f),
            mask = Mask.MEDALLION_CIRCLE,
            ornamentBottom = 617f
        )
    }

    fun clipPath(spec: Spec): Path {
        val frame = spec.bounds
        return when (spec.mask) {
            Mask.MEDALLION_CIRCLE -> {
                val r = minOf(frame.width(), frame.height()) / 2f
                Path().apply { addCircle(frame.centerX(), frame.centerY(), r, Path.Direction.CW) }
            }
            Mask.PORTRAIT_OVAL -> Path().apply { addOval(frame, Path.Direction.CW) }
            Mask.CEREMONY_ARCH -> archPath(frame)
            Mask.HEX_MEDALLION -> hexPath(frame)
            Mask.ROUNDED_RECT -> Path().apply {
                addRoundRect(frame, spec.roundRectRadius, spec.roundRectRadius, Path.Direction.CW)
            }
        }
    }

    fun cropAspectRatio(templateId: String): Float {
        val frame = specFor(templateId).bounds
        return when (specFor(templateId).mask) {
            Mask.PORTRAIT_OVAL -> 0.82f
            Mask.MEDALLION_CIRCLE, Mask.HEX_MEDALLION -> 1f
            Mask.CEREMONY_ARCH -> 0.95f
            Mask.ROUNDED_RECT -> frame.width() / frame.height()
        }
    }

    fun ornamentBottom(templateId: String): Float = specFor(templateId).ornamentBottom

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

    private fun hexPath(frame: RectF): Path {
        val cx = frame.centerX()
        val cy = frame.centerY()
        val rx = frame.width() / 2f
        val ry = frame.height() / 2f
        return Path().apply {
            for (i in 0 until 6) {
                val angle = Math.toRadians(60.0 * i - 90.0)
                val x = cx + rx * cos(angle).toFloat()
                val y = cy + ry * sin(angle).toFloat()
                if (i == 0) moveTo(x, y) else lineTo(x, y)
            }
            close()
        }
    }
}
