package com.vaangainvite.core.image

import org.junit.Assert.assertTrue
import org.junit.Test

class InvitationPhotoPlacementTest {

    @Test
    fun eventTemplatePhotoFramesStayInsideCanvas() {
        val ids = buildList {
            (1..5).forEach { add("engagement_%02d".format(it)) }
            (1..5).forEach { add("naming_%02d".format(it)) }
            (1..5).forEach { add("babyshower_%02d".format(it)) }
        }
        ids.forEach { id ->
            val bounds = InvitationPhotoPlacement.specFor(id).bounds
            assertTrue("$id left", bounds.left >= 0f)
            assertTrue("$id top", bounds.top >= 0f)
            assertTrue("$id right", bounds.right <= InvitationLayout.canvasWidth)
            assertTrue("$id bottom", bounds.bottom <= InvitationLayout.canvasHeight)
            assertTrue("$id height", bounds.bottom - bounds.top > 120f)
        }
    }

    @Test
    fun babyShowerUsesCircleMedallion() {
        val mask = InvitationPhotoPlacement.specFor("babyshower_01").mask
        assertTrue(
            mask == InvitationPhotoPlacement.Mask.MEDALLION_CIRCLE ||
                mask == InvitationPhotoPlacement.Mask.HEX_MEDALLION
        )
    }
}
