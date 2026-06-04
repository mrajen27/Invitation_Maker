package com.vaangainvite.data.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Test

class InvitationFieldLimitsTest {

    @Test
    fun normalizeMessage_doesNotBreakWordsAtThirtyCharacters() {
        val input = "Congratulations on your baby shower celebration"
        val result = normalizeMessage(input)
        assertFalse(result.contains("\n"))
        assertEquals(input, result)
    }

    @Test
    fun normalizeMessage_preservesExplicitLineBreak() {
        val input = "Line one here\nLine two here"
        assertEquals(input, normalizeMessage(input))
    }

    @Test
    fun normalizeMessage_enforcesTotalCharacterLimit() {
        val input = "A".repeat(80)
        val result = normalizeMessage(input)
        assertEquals(60, result.replace("\n", "").length)
    }

    @Test
    fun normalizeMessage_mergesExtraExplicitLinesIntoSecondLine() {
        val input = "First\nSecond\nThird"
        assertEquals("First\nSecond Third", normalizeMessage(input))
    }

    @Test
    fun normalizeVenue_doesNotBreakWordsAtThirtyCharacters() {
        val input = "Sri Lakshmi Mahal, Race Course Road, Coimbatore"
        val result = normalizeVenue(input)
        assertFalse(result.contains("\n"))
        assertEquals(input, result)
    }

    @Test
    fun normalizeMessage_preservesTrailingEnterForSecondLine() {
        assertEquals("Hello\n", normalizeMessage("Hello\n"))
    }
}
