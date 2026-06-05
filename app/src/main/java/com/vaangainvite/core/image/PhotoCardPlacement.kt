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

    /** Same slot as birthday WebP cards — compact frame under the toran band. */
    val framedPhoto = RectF(390f, 292f, 690f, 478f)

    private val templateIds = (1..5).flatMap { i ->
        listOf(
            "engagement_%02d".format(i),
            "naming_%02d".format(i),
            "babyshower_%02d".format(i)
        )
    }

    private val specs = templateIds.associateWith {
        Spec(bounds = framedPhoto, cornerRadius = 32f)
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
