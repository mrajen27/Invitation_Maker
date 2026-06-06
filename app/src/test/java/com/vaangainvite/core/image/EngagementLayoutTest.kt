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
                PhotoCardPlacement.Mask.TRADITIONAL_RECT,
                PhotoCardPlacement.specFor(id).mask
            )
        }
    }

    @Test
    fun engagementPhotoSlotUsesTallFrame() {
        val frame = PhotoCardPlacement.specFor("engagement_01").bounds
        assertEquals(PhotoCardPlacement.tallFramedPhoto, frame)
        assertTrue(PhotoCardPlacement.usesFramedPhotoBorder("engagement_01"))
    }

    @Test
    fun engagementTextStartsJustBelowPhotoFrame() {
        val id = "engagement_01"
        val photoBottom = PhotoCardPlacement.photoBottom(id)
        val textTop = InvitationLayout.textStartY(id, hasUploadedPhoto = true)
        val gap = textTop - photoBottom
        assertTrue("gap ${gap}px", gap in 14f..24f)
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
            val spec = PhotoCardPlacement.specFor(id)
            val textTop = InvitationLayout.textStartY(id, hasUploadedPhoto = true)
            assertTrue(
                "$id text $textTop below photo ${spec.bounds.bottom}",
                textTop > spec.bounds.bottom + 6f
            )
        }
    }
}
