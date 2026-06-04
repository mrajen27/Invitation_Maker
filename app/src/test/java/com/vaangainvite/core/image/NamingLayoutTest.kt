package com.vaangainvite.core.image

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class NamingLayoutTest {

    @Test
    fun allNamingTemplatesUseTraditionalRectPhoto() {
        (1..5).forEach { index ->
            val id = "naming_%02d".format(index)
            assertEquals(
                PhotoCardPlacement.Mask.TRADITIONAL_RECT,
                PhotoCardPlacement.specFor(id).mask
            )
        }
    }

    @Test
    fun namingTextZoneFitsMaxLengthCopy() {
        val id = "naming_01"
        val zone = InvitationLayout.photoTextZone(id, hasUploadedPhoto = true)
        val startY = InvitationLayout.textStartY(id, hasUploadedPhoto = true)
        assertTrue(zone.bottom - startY >= 400f)
    }
}
