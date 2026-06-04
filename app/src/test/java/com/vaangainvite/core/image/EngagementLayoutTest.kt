package com.vaangainvite.core.image

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EngagementLayoutTest {

    @Test
    fun allEngagementTemplatesUseTraditionalRectPhoto() {
        (1..5).forEach { index ->
            val id = "engagement_%02d".format(index)
            assertEquals(
                EngagementPhotoPlacement.Mask.TRADITIONAL_RECT,
                EngagementPhotoPlacement.specFor(id).mask
            )
        }
    }

    @Test
    fun engagementTextStartsDirectlyBelowPhotoWithNoOrnamentGap() {
        val id = "engagement_01"
        val photoBottom = EngagementPhotoPlacement.photoBottom(id)
        val textTop = InvitationLayout.textStartY(id, hasUploadedPhoto = true)
        assertTrue(textTop <= photoBottom + 30f)
        assertTrue(textTop > photoBottom)
    }

    @Test
    fun engagementTextZoneFitsMaxLengthCopy() {
        val minHeight = 400f
        listOf("engagement_01", "engagement_02").forEach { id ->
            val zone = InvitationLayout.photoTextZone(id, hasUploadedPhoto = true)
            val startY = InvitationLayout.textStartY(id, hasUploadedPhoto = true)
            assertTrue(
                "$id text band ${zone.bottom - startY}px",
                zone.bottom - startY >= minHeight
            )
        }
    }

    @Test
    fun photoMaskEndsBeforeTextBand() {
        (1..5).forEach { index ->
            val id = "engagement_%02d".format(index)
            val spec = EngagementPhotoPlacement.specFor(id)
            val textTop = InvitationLayout.textStartY(id, hasUploadedPhoto = true)
            assertTrue(
                "$id text $textTop below photo ${spec.bounds.bottom}",
                textTop > spec.bounds.bottom + 6f
            )
        }
    }
}
