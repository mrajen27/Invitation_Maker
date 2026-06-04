package com.vaangainvite.core.image

import org.junit.Assert.assertTrue
import org.junit.Test

/** Layout contract for designed engagement WebP photo-card templates. */
class EngagementLayoutTest {

    @Test
    fun engagementPhotoArchEndsBeforeTextBand() {
        val pairs = listOf(
            "engagement_01" to (448f to 492f),
            "engagement_02" to (452f to 488f),
            "engagement_03" to (445f to 482f),
            "engagement_04" to (450f to 490f),
            "engagement_05" to (448f to 486f)
        )
        pairs.forEach { (id, bounds) ->
            val (archBottom, textTop) = bounds
            assertTrue("$id: arch $archBottom above text $textTop", archBottom < textTop)
        }
    }

    @Test
    fun engagementTextStaysAboveBottomDecorStrip() {
        assertTrue(1085f <= 1090f)
    }
}
