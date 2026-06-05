package com.vaangainvite.core.image

import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.vaangainvite.data.model.InvitationDetails
import com.vaangainvite.data.model.InvitationFieldLimits
import com.vaangainvite.data.model.InvitationLanguage
import com.vaangainvite.data.repository.TemplateRepository
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.io.FileOutputStream

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PhotoCardTextLayoutTest {

    @Test
    fun introStartsJustBelowPhotoFrame() {
        listOf("engagement_01", "naming_01", "babyshower_01").forEach { id ->
            val photoBottom = PhotoCardPlacement.photoBottom(id)
            val textTop = InvitationLayout.textStartY(id, hasUploadedPhoto = true)
            val gap = textTop - photoBottom
            assertTrue("$id gap ${gap}px", gap in 6f..14f)
        }
    }

    @Test
    fun photoCardsShowAdditionalMessageWithMaxFields() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val generator = InvitationImageGenerator(context)
        val repo = TemplateRepository()
        val photoUri = copySamplePhoto(context.cacheDir)
        val details = InvitationDetails(
            occasionTitle = "Engagement Ceremony",
            name = "Priya & Arjun",
            date = "Sunday, 15 June 2026",
            time = "10:00 AM",
            venue = "Sri Lakshmi Mahal, Coimbatore",
            mobileNumber = "+91 98765 43210",
            message = "M".repeat(InvitationFieldLimits.MESSAGE_MAX_LENGTH_WITH_PHOTO)
        )

        listOf("engagement_01", "naming_01", "babyshower_01", "engagement_05").forEach { id ->
            val template = repo.templateById(id) ?: return@forEach
            val report = generator.createInvitationBitmap(
                template = template,
                details = details,
                language = InvitationLanguage.ENGLISH,
                uploadedPhotoUri = photoUri
            ).renderReport
            assertTrue("$id message not shown", report.messageShown)
            assertTrue("$id message truncated", !report.messageTruncated)
        }
    }

    private fun copySamplePhoto(cacheDir: File): Uri {
        val outFile = File(cacheDir, "photo_card_text_layout.png")
        if (!outFile.exists()) {
            val stream = javaClass.classLoader!!.getResourceAsStream("samples/couple_portrait.png")
                ?: error("Missing test resource samples/couple_portrait.png")
            FileOutputStream(outFile).use { out -> stream.copyTo(out) }
        }
        return Uri.fromFile(outFile)
    }
}
