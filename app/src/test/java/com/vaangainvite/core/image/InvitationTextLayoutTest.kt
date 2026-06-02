package com.vaangainvite.core.image

import android.graphics.Paint
import android.graphics.RectF
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class InvitationTextLayoutTest {

    @Test
    fun textStartY_withPhoto_staysBelowPhotoFrame() {
        val startY = InvitationTextLayout.textStartY(hasPhoto = true)
        assertTrue(startY >= InvitationTextLayout.photoFrame.bottom + InvitationTextLayout.Spacing.photoToTextGap)
    }

    @Test
    fun messageTopY_flowsBelowDetails_notAbove() {
        val zone = InvitationTextLayout.textZone(hasPhoto = true)
        val paint = Paint().apply { textSize = 24f }
        val detailsEndY = 960f
        val top = InvitationTextLayout.messageTopY(
            blockTopAfterLastDetail = detailsEndY,
            paint = paint,
            lineCount = 2,
            lineSpacing = 8f,
            zone = zone
        )
        assertTrue(top > detailsEndY - InvitationTextLayout.Spacing.betweenDetails)
    }

    @Test
    fun messageTopY_fitsInsideZone_whenDetailsAreLong() {
        val zone = InvitationTextLayout.textZone(hasPhoto = true)
        val paint = Paint().apply { textSize = 24f }
        val detailsEndY = 1020f
        val top = InvitationTextLayout.messageTopY(
            blockTopAfterLastDetail = detailsEndY,
            paint = paint,
            lineCount = 2,
            lineSpacing = 8f,
            zone = zone
        )
        val fm = paint.fontMetrics
        val lineAdvance = paint.fontSpacing + 8f
        val messageHeight = -fm.ascent + lineAdvance + fm.descent
        assertTrue(top + messageHeight <= zone.bottom)
    }

    @Test
    fun contentScale_reducesWhenEstimateExceedsZone() {
        val zone = RectF(200f, 600f, 880f, 1088f)
        val scale = InvitationTextLayout.contentScaleForZone(
            estimatedHeight = zone.height() * 1.2f,
            zone = zone,
            hasPhoto = true
        )
        assertTrue(scale < 1f)
    }

    @Test
    fun contentScale_staysOneWhenContentFits() {
        val zone = InvitationTextLayout.textZone(hasPhoto = false)
        val scale = InvitationTextLayout.contentScaleForZone(
            estimatedHeight = zone.height() * 0.7f,
            zone = zone,
            hasPhoto = false
        )
        assertEquals(1f, scale, 0.001f)
    }
}
