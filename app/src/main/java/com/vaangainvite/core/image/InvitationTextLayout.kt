package com.vaangainvite.core.image

import android.graphics.Paint
import android.graphics.RectF
import com.vaangainvite.data.model.InvitationDetails
import com.vaangainvite.data.model.InvitationFieldLimits

/**
 * Safe zones for vector templates (engagement, naming, baby shower, birthday, etc.).
 * All use the same 1080×1350 card with a center photo slot and frosted text panel.
 */
object InvitationTextLayout {
    const val canvasWidth = 1080f
    const val canvasHeight = 1350f

    /** Portrait slot shared by vector templates — sits inside the white center cutout. */
    val photoFrame: RectF = RectF(350f, 285f, 730f, 575f)

    object Spacing {
        const val afterHeading = 6f
        const val afterName = 4f
        const val afterOccasion = 14f
        const val betweenDetails = 4f
        const val beforeMessage = 10f
        /** Minimum gap between photo bottom and first text baseline. */
        const val photoToTextGap = 26f
    }

    fun frostedPanelBounds(hasPhoto: Boolean): RectF {
        val zone = textZone(hasPhoto)
        return RectF(
            zone.left - 50f,
            268f,
            zone.right + 50f,
            zone.bottom + 30f
        )
    }

    fun textZone(hasPhoto: Boolean): RectF {
        val topNoPhoto = 292f
        val topWithPhoto = maxOf(topNoPhoto, photoFrame.bottom + Spacing.photoToTextGap)
        return RectF(
            200f,
            if (hasPhoto) topWithPhoto else topNoPhoto,
            880f,
            1088f
        )
    }

    fun textStartY(hasPhoto: Boolean): Float = textZone(hasPhoto).top + 4f

    /**
     * Places the additional message directly below the last detail row so it never
     * overlaps date/time/venue/contact. If needed, shifts upward while staying inside [zone].
     */
    fun messageTopY(
        blockTopAfterLastDetail: Float,
        paint: Paint,
        lineCount: Int,
        lineSpacing: Float,
        zone: RectF,
        gapBeforeMessage: Float = Spacing.beforeMessage
    ): Float {
        if (lineCount <= 0) return blockTopAfterLastDetail

        val fm = paint.fontMetrics
        val lineAdvance = paint.fontSpacing + lineSpacing
        val messageHeight = if (lineCount == 1) {
            -fm.ascent + fm.descent
        } else {
            -fm.ascent + (lineCount - 1) * lineAdvance + fm.descent
        }

        val preferredTop = blockTopAfterLastDetail -
            Spacing.betweenDetails +
            gapBeforeMessage
        val maxBottom = zone.bottom - 6f
        return if (preferredTop + messageHeight <= maxBottom) {
            preferredTop
        } else {
            (maxBottom - messageHeight).coerceAtMost(preferredTop)
        }
    }

    /**
     * Estimates total vertical space for the text block (used to pick a slight scale-down).
     */
    fun estimateContentHeight(
        details: InvitationDetails,
        hasPhoto: Boolean,
        hasMobile: Boolean,
        paints: ContentPaints
    ): Float {
        var height = 0f
        height += paints.headingPaint.fontSpacing + Spacing.afterHeading
        height += lineBlockHeight(details.name, paints.titlePaint, 690f, 2) + Spacing.afterName
        height += lineBlockHeight(details.occasionTitle, paints.occasionPaint, 690f, 2) + Spacing.afterOccasion
        height += detailBlockHeight(details.date, paints.bodyPaint, 1)
        height += detailBlockHeight(details.time, paints.bodyPaint, 1)
        height += detailBlockHeight(details.venue, paints.bodyPaint, InvitationFieldLimits.VENUE_MAX_LINES)
        if (hasMobile) {
            height += detailBlockHeight(details.mobileNumber, paints.bodyPaint, 1)
        }
        val messageLines = details.message.lineSequence().count { it.isNotBlank() }.coerceAtMost(2)
        if (messageLines > 0) {
            height += Spacing.beforeMessage + lineBlockHeight(
                details.message,
                paints.messagePaint,
                620f,
                InvitationFieldLimits.MESSAGE_MAX_LINES_ON_CARD
            )
        }
        return height
    }

    fun contentScaleForZone(
        estimatedHeight: Float,
        zone: RectF,
        hasPhoto: Boolean
    ): Float {
        val available = zone.height() - if (hasPhoto) 8f else 16f
        return when {
            estimatedHeight <= available -> 1f
            estimatedHeight <= available * 1.08f -> 0.94f
            estimatedHeight <= available * 1.16f -> 0.88f
            else -> 0.82f
        }.coerceIn(0.82f, 1f)
    }

    private fun lineBlockHeight(text: String, paint: Paint, maxWidth: Float, maxLines: Int): Float {
        val lines = text.lineSequence().filter { it.isNotBlank() }.take(maxLines).count()
            .coerceAtLeast(if (text.isBlank()) 0 else 1)
        if (lines == 0) return 0f
        return lines * (paint.fontSpacing + 4f)
    }

    private fun detailBlockHeight(value: String, paint: Paint, maxLines: Int): Float {
        val lines = value.lineSequence().filter { it.isNotBlank() }.take(maxLines).count()
            .coerceAtLeast(if (value.isBlank()) 1 else 1)
        return lines * (paint.fontSpacing + 4f) + 4f
    }

    data class ContentPaints(
        val headingPaint: Paint,
        val titlePaint: Paint,
        val occasionPaint: Paint,
        val bodyPaint: Paint,
        val messagePaint: Paint
    )
}
