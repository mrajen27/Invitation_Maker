package com.vaangainvite.core.image

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class BabyShowerLayoutTest {

    @Test
    fun allBabyShowerTemplatesUseTraditionalRectPhoto() {
        (1..5).forEach { index ->
            val id = "babyshower_%02d".format(index)
            assertEquals(
                PhotoCardPlacement.Mask.TRADITIONAL_RECT,
                PhotoCardPlacement.specFor(id).mask
            )
        }
    }

    @Test
    fun babyShowerTextZoneFitsMaxLengthCopy() {
        val id = "babyshower_01"
        val zone = InvitationLayout.photoTextZone(id, hasUploadedPhoto = true)
        val startY = InvitationLayout.textStartY(id, hasUploadedPhoto = true)
        assertTrue(zone.bottom - startY >= 400f)
    }

    @Test
    fun photoMaskEndsBeforeTextBand() {
        (1..5).forEach { index ->
            val id = "babyshower_%02d".format(index)
            val spec = PhotoCardPlacement.specFor(id)
            val textTop = InvitationLayout.textStartY(id, hasUploadedPhoto = true)
            assertTrue(
                "$id text $textTop below photo ${spec.bounds.bottom}",
                textTop > spec.bounds.bottom + 6f
            )
        }
    }
}
