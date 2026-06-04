package com.vaangainvite.core.image

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class EngagementLayoutTest {

    @Test
    fun templates01And02UseTraditionalArchNotRoundMasks() {
        val roundMasks = setOf(
            EngagementPhotoPlacement.Mask.MEDALLION_CIRCLE,
            EngagementPhotoPlacement.Mask.PORTRAIT_OVAL,
            EngagementPhotoPlacement.Mask.ROUNDED_RECT
        )
        val first = EngagementPhotoPlacement.specFor("engagement_01").mask
        val second = EngagementPhotoPlacement.specFor("engagement_02").mask
        assertEquals(EngagementPhotoPlacement.Mask.CEREMONY_ARCH, first)
        assertEquals(EngagementPhotoPlacement.Mask.CEREMONY_ARCH, second)
        assertTrue(first !in roundMasks)
        assertTrue(second !in roundMasks)
    }

    @Test
    fun engagement01And02TextZoneFitsMaxLengthCopy() {
        val minHeight = 400f
        listOf("engagement_01", "engagement_02").forEach { id ->
            val zone = InvitationLayout.photoTextZone(id, hasUploadedPhoto = true)
            val startY = InvitationLayout.textStartY(id, hasUploadedPhoto = true)
            val available = zone.bottom - startY
            assertTrue(
                "$id text band height ${available}px (start=$startY, bottom=${zone.bottom})",
                available >= minHeight
            )
        }
    }

    @Test
    fun engagementSetUsesThreePlusMaskShapes() {
        val masks = (1..5).map { i ->
            EngagementPhotoPlacement.specFor("engagement_%02d".format(i)).mask
        }
        assertTrue(masks.toSet().size >= 3)
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
