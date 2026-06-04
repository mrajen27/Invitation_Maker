package com.vaangainvite.core.image

import android.graphics.Path
import android.graphics.RectF

/**
 * Designed WebP photo cards (engagement, naming, …) — soft rectangle on continuous cream panel.
 */
internal object PhotoCardPlacement {

    enum class Mask {
        TRADITIONAL_RECT
    }

    data class Spec(
        val bounds: RectF,
        val mask: Mask = Mask.TRADITIONAL_RECT,
        val cornerRadius: Float = 26f
    )

    /** Smaller portrait slot — leaves more cream and shows side border art. */
    private val traditionalPhoto = RectF(320f, 428f, 760f, 598f)

    private val templateIds = (1..5).flatMap { i ->
        listOf(
            "engagement_%02d".format(i),
            "naming_%02d".format(i),
            "babyshower_%02d".format(i)
        )
    }

    private val specs = templateIds.associateWith {
        Spec(bounds = traditionalPhoto, cornerRadius = 26f)
    }

    fun isPhotoCardTemplate(templateId: String): Boolean =
        templateId.startsWith("engagement_") ||
            templateId.startsWith("naming_") ||
            templateId.startsWith("babyshower_")

    fun specFor(templateId: String): Spec {
        return specs[templateId] ?: Spec(bounds = traditionalPhoto)
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
