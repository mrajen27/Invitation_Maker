package com.vaangainvite.core.image

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class EngagementLayoutTest {

    @Test
    fun eachTemplateUsesDistinctPhotoMask() {
        val masks = (1..5).map { i ->
            EngagementPhotoPlacement.specFor("engagement_%02d".format(i)).mask
        }
        assertEquals(5, masks.toSet().size)
    }

    @Test
    fun photoMaskEndsBeforeTextBand() {
        val pairs = listOf(
            Triple("engagement_01", 545f, 568f),
            Triple("engagement_02", 515f, 538f),
            Triple("engagement_03", 438f, 468f),
            Triple("engagement_04", 495f, 518f),
            Triple("engagement_05", 490f, 518f)
        )
        pairs.forEach { (id, maskBottom, textTop) ->
            val boundsBottom = EngagementPhotoPlacement.specFor(id).bounds.bottom
            assertTrue("$id bounds $boundsBottom < text $textTop", boundsBottom <= maskBottom)
            assertTrue("$id mask above text", maskBottom < textTop)
        }
    }
}
