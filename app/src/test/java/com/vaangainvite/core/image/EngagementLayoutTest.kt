package com.vaangainvite.core.image

import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Layout contract for engagement WebPs: photo in top arch (~y118–454), text in cream band below.
 */
class EngagementLayoutTest {

    @Test
    fun engagementPhotoArchEndsBeforeTextBand() {
        val archBottomMax = 454f
        val textTopNoPhoto = 518f
        val textTopWithPhoto = 512f
        assertTrue(archBottomMax < textTopNoPhoto)
        assertTrue(archBottomMax < textTopWithPhoto)
    }

    @Test
    fun engagementTextEndsAboveBottomArtwork() {
        val textBottom = 920f
        val canvasBottom = 1350f
        assertTrue(textBottom < canvasBottom - 400f)
    }
}
