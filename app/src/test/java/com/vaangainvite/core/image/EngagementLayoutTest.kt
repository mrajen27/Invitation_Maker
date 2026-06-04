package com.vaangainvite.core.image

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EngagementLayoutTest {

    @Test
    fun templates01And02UseDifferentPhotoShapes() {
        val first = EngagementPhotoPlacement.specFor("engagement_01").mask
        val second = EngagementPhotoPlacement.specFor("engagement_02").mask
        assertEquals(EngagementPhotoPlacement.Mask.MEDALLION_CIRCLE, first)
        assertEquals(EngagementPhotoPlacement.Mask.ROUNDED_RECT, second)
        assertNotEquals(first, second)
    }

    @Test
    fun engagementSetUsesFourPlusMaskShapes() {
        val masks = (1..5).map { i ->
            EngagementPhotoPlacement.specFor("engagement_%02d".format(i)).mask
        }
        assertTrue(masks.toSet().size >= 4)
    }

    @Test
    fun photoMaskEndsBeforeTextBand() {
        (1..5).forEach { index ->
            val id = "engagement_%02d".format(index)
            val spec = EngagementPhotoPlacement.specFor(id)
            assertTrue("$id ornament below frame", spec.ornamentBottom >= spec.bounds.bottom)
            val textTop = InvitationLayout.textStartY(id, hasUploadedPhoto = true)
            assertTrue(
                "$id text $textTop clears ornament ${spec.ornamentBottom}",
                textTop > spec.ornamentBottom + 8f
            )
        }
    }
}
