package com.vaangainvite.core.image

import android.graphics.Path
import android.graphics.RectF

/**
 * Per-template photo masks so uploads sit inside the artwork (ovals, circles, arches)
 * instead of a generic rounded rectangle on top of the design.
 */
internal object InvitationPhotoPlacement {

    enum class Mask {
        /** Tall portrait oval — engagement cards. */
        PORTRAIT_OVAL,
        /** Wide oval — naming / cradle layouts. */
        LANDSCAPE_OVAL,
        /** Fits decorative rings on baby shower art. */
        MEDALLION_CIRCLE,
        /** Temple-style arch opening. */
        CEREMONY_ARCH,
        /** Soft hexagon — modern accent without pill-shaped corners. */
        HEX_MEDALLION
    }

    enum class Border {
        /** Background art already has a frame; only a soft shadow. */
        ARTWORK_FRAME,
        /** Thin inner highlight for dark backgrounds (e.g. teal engagement). */
        INNER_GLOW
    }

    data class Spec(
        val bounds: RectF,
        val mask: Mask,
        val border: Border = Border.ARTWORK_FRAME
    )

    fun specFor(templateId: String): Spec {
        return templateSpecs[templateId] ?: categoryDefault(templateId)
    }

    fun clipPath(spec: Spec): Path {
        val frame = spec.bounds
        return when (spec.mask) {
            Mask.PORTRAIT_OVAL -> Path().apply { addOval(frame, Path.Direction.CW) }
            Mask.LANDSCAPE_OVAL -> Path().apply { addOval(frame, Path.Direction.CW) }
            Mask.MEDALLION_CIRCLE -> {
                val radius = minOf(frame.width(), frame.height()) / 2f
                val cx = frame.centerX()
                val cy = frame.centerY()
                Path().apply { addCircle(cx, cy, radius, Path.Direction.CW) }
            }
            Mask.CEREMONY_ARCH -> archPath(frame)
            Mask.HEX_MEDALLION -> hexMedallionPath(frame)
        }
    }

    /** Crop aspect ratio hint (width / height) for future crop UI. */
    fun cropAspectRatio(templateId: String): Float {
        return when (specFor(templateId).mask) {
            Mask.PORTRAIT_OVAL -> 0.78f
            Mask.LANDSCAPE_OVAL -> 1.25f
            Mask.MEDALLION_CIRCLE, Mask.CEREMONY_ARCH, Mask.HEX_MEDALLION -> 1f
        }
    }

    private fun categoryDefault(templateId: String): Spec = when {
        templateId.startsWith("babyshower") -> Spec(
            bounds = RectF(378f, 168f, 702f, 492f),
            mask = Mask.MEDALLION_CIRCLE
        )
        templateId.startsWith("engagement") -> Spec(
            bounds = RectF(408f, 178f, 672f, 518f),
            mask = Mask.PORTRAIT_OVAL
        )
        templateId.startsWith("naming") -> Spec(
            bounds = RectF(328f, 198f, 752f, 438f),
            mask = Mask.LANDSCAPE_OVAL
        )
        else -> Spec(
            bounds = InvitationLayout.defaultPhotoFrame(),
            mask = Mask.PORTRAIT_OVAL
        )
    }

    private val templateSpecs = mapOf(
        // Engagement — tall portrait medallions inside gold ovals
        // Engagement — top arch photo slot (housewarming-style printable cards)
        "engagement_01" to Spec(RectF(312f, 128f, 768f, 448f), Mask.CEREMONY_ARCH),
        "engagement_02" to Spec(RectF(308f, 122f, 772f, 452f), Mask.CEREMONY_ARCH),
        "engagement_03" to Spec(RectF(318f, 135f, 762f, 445f), Mask.CEREMONY_ARCH),
        "engagement_04" to Spec(
            bounds = RectF(322f, 130f, 758f, 450f),
            mask = Mask.CEREMONY_ARCH,
            border = Border.INNER_GLOW
        ),
        "engagement_05" to Spec(RectF(315f, 125f, 765f, 448f), Mask.CEREMONY_ARCH),

        // Naming — masks tuned to refreshed artwork set
        "naming_01" to Spec(RectF(384f, 162f, 696f, 476f), Mask.MEDALLION_CIRCLE),
        "naming_02" to Spec(RectF(378f, 158f, 702f, 478f), Mask.MEDALLION_CIRCLE),
        "naming_03" to Spec(RectF(342f, 142f, 738f, 472f), Mask.CEREMONY_ARCH),
        "naming_04" to Spec(RectF(388f, 168f, 692f, 472f), Mask.MEDALLION_CIRCLE),
        "naming_05" to Spec(RectF(392f, 172f, 688f, 468f), Mask.MEDALLION_CIRCLE),

        // Baby shower — circles inside top rings / scallops
        "babyshower_01" to Spec(RectF(384f, 162f, 696f, 474f), Mask.MEDALLION_CIRCLE),
        "babyshower_02" to Spec(RectF(390f, 168f, 690f, 468f), Mask.MEDALLION_CIRCLE),
        "babyshower_03" to Spec(RectF(378f, 175f, 702f, 485f), Mask.HEX_MEDALLION),
        "babyshower_04" to Spec(RectF(386f, 170f, 694f, 478f), Mask.MEDALLION_CIRCLE),
        "babyshower_05" to Spec(RectF(400f, 188f, 680f, 468f), Mask.LANDSCAPE_OVAL)
    )

    private fun archPath(frame: RectF): Path {
        val w = frame.width()
        val archRise = w * 0.22f
        return Path().apply {
            moveTo(frame.left, frame.bottom)
            lineTo(frame.left, frame.top + archRise)
            quadTo(frame.left, frame.top, frame.centerX(), frame.top)
            quadTo(frame.right, frame.top, frame.right, frame.top + archRise)
            lineTo(frame.right, frame.bottom)
            close()
        }
    }

    private fun hexMedallionPath(frame: RectF): Path {
        val cx = frame.centerX()
        val cy = frame.centerY()
        val rx = frame.width() / 2f
        val ry = frame.height() / 2f
        val path = Path()
        for (i in 0 until 6) {
            val angle = Math.toRadians((60.0 * i - 90.0))
            val x = cx + rx * kotlin.math.cos(angle).toFloat()
            val y = cy + ry * kotlin.math.sin(angle).toFloat()
            if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        path.close()
        return path
    }
}
