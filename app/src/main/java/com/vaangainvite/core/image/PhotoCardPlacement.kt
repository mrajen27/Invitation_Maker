package com.vaangainvite.core.image

import android.graphics.Path
import android.graphics.RectF

/**
 * Designed WebP photo cards (engagement, naming, baby shower) — compact gold-framed photo on cream panel.
 */
internal object PhotoCardPlacement {

    enum class Mask {
        TRADITIONAL_RECT
    }

    data class Spec(
        val bounds: RectF,
        val mask: Mask = Mask.TRADITIONAL_RECT,
        val cornerRadius: Float = 32f
    )

    /** Compact frame under the toran band — fits max-length copy + message. */
    val compactFramedPhoto = RectF(390f, 292f, 690f, 478f)

    /**
     * Wider photo on the same vertical slot — ~73% wider than compact, max-length copy safe.
     * Height cannot grow much (~10px) before the 2-line message truncates.
     */
    val expandedFramedPhoto = RectF(280f, 292f, 800f, 478f)

    val framedPhoto = expandedFramedPhoto

    private val templateIds = (1..5).flatMap { i ->
        listOf(
            "engagement_%02d".format(i),
            "naming_%02d".format(i),
            "babyshower_%02d".format(i)
        )
    }

    private var framedPhotoBounds = framedPhoto

    private var specs = templateIds.associateWith {
        Spec(bounds = framedPhotoBounds, cornerRadius = 32f)
    }

    /** Test-only: swap photo slot bounds for layout probes. */
    internal fun setFramedPhotoForTests(bounds: RectF) {
        framedPhotoBounds = RectF(bounds)
        specs = templateIds.associateWith {
            Spec(bounds = framedPhotoBounds, cornerRadius = 32f)
        }
    }

    fun isPhotoCardTemplate(templateId: String): Boolean =
        templateId.startsWith("engagement_") ||
            templateId.startsWith("naming_") ||
            templateId.startsWith("babyshower_")

    /** Gold stroke frame (birthday style) instead of cream vignette blend. */
    fun usesFramedPhotoBorder(templateId: String): Boolean =
        isPhotoCardTemplate(templateId)

    fun specFor(templateId: String): Spec {
        return specs[templateId] ?: Spec(bounds = framedPhoto)
    }

    fun clipPath(spec: Spec): Path {
        val frame = spec.bounds
        return Path().apply {
            addRoundRect(frame, spec.cornerRadius, spec.cornerRadius, Path.Direction.CW)
        }
    }

    fun cropAspectRatio(templateId: String): Float {
        val frame = specFor(templateId).bounds
        return frame.width() / frame.height()
    }

    fun photoBottom(templateId: String): Float = specFor(templateId).bounds.bottom
}
