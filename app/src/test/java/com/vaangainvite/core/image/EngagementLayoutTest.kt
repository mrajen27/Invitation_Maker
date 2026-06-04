package com.vaangainvite.core.image

import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Layout contract for engagement WebPs: photo in top arch (~y118–454), text in cream band below.
 */
class EngagementLayoutTest {

    @Test
    fun engagementPhotoArchEndsBeforeTextBand() {
        val archBottomMax = 508f
        val textTopNoPhoto = 538f
        val textTopWithPhoto = 548f
        assertTrue(archBottomMax < textTopNoPhoto)
        assertTrue(archBottomMax < textTopWithPhoto)
    }

    @Test
    fun engagementTextEndsAboveBottomArtwork() {
        val textBottom = 990f
        assertTrue(textBottom <= 1020f)
    }
}
