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
        "engagement_01" to Spec(RectF(412f, 172f, 668f, 512f), Mask.PORTRAIT_OVAL),
        "engagement_02" to Spec(RectF(418f, 185f, 662f, 505f), Mask.PORTRAIT_OVAL),
        "engagement_03" to Spec(
            bounds = RectF(420f, 168f, 660f, 498f),
            mask = Mask.PORTRAIT_OVAL,
            border = Border.INNER_GLOW
        ),
        "engagement_04" to Spec(RectF(400f, 175f, 680f, 520f), Mask.HEX_MEDALLION),
        "engagement_05" to Spec(RectF(408f, 188f, 672f, 508f), Mask.CEREMONY_ARCH),

        // Naming — masks tuned to refreshed artwork set
        "naming_01" to Spec(RectF(384f, 162f, 696f, 476f), Mask.MEDALLION_CIRCLE),
        "naming_02" to Spec(RectF(320f, 148f, 760f, 438f), Mask.LANDSCAPE_OVAL),
        "naming_03" to Spec(RectF(348f, 138f, 732f, 468f), Mask.CEREMONY_ARCH),
        "naming_04" to Spec(
            bounds = RectF(372f, 152f, 708f, 488f),
            mask = Mask.PORTRAIT_OVAL,
            border = Border.INNER_GLOW
        ),
        "naming_05" to Spec(RectF(382f, 155f, 698f, 471f), Mask.MEDALLION_CIRCLE),

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
