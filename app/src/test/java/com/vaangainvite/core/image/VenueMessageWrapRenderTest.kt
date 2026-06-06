package com.vaangainvite.core.image

import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.vaangainvite.data.model.InvitationDetails
import com.vaangainvite.data.model.InvitationLanguage
import com.vaangainvite.data.repository.TemplateRepository
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.io.FileOutputStream

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class VenueMessageWrapRenderTest {

    @Test
    fun photoCardShowsWrappedVenueAndMessage() {
        assertRendersOk(
            templateId = "babyshower_01",
            details = InvitationDetails(
                occasionTitle = "Baby Shower",
                name = "Meena",
                date = "27 Jun 2026",
                time = "12 PM",
                venue = "Annamalaiyar Mahal, Tiruvannamalai Town\nSecond Floor Hall",
                mobileNumber = "953928465959",
                message = "Looking forward to celebrating\nwith all of you soon"
            )
        )
    }

    @Test
    fun birthdayWithPhotoShowsWrappedVenueAndMessage() {
        assertRendersOk(
            templateId = "birthday_01",
            details = InvitationDetails(
                occasionTitle = "Birthday Celebration",
                name = "Arjun",
                date = "15 Jun 2026",
                time = "6 PM",
                venue = "Sri Lakshmi Convention Centre, Coimbatore\nRS Puram Main Road",
                message = "Your presence will make\nthe day special for us"
            )
        )
    }

    private fun assertRendersOk(templateId: String, details: InvitationDetails) {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val generator = InvitationImageGenerator(context)
        val template = TemplateRepository().templateById(templateId)!!
        val report = generator.createInvitationBitmap(
            template = template,
            details = details,
            language = InvitationLanguage.ENGLISH,
            uploadedPhotoUri = copySamplePhoto(context.cacheDir)
        ).renderReport
        assertTrue("$templateId message not shown", report.messageShown)
        assertFalse("$templateId message truncated", report.messageTruncated)
    }

    private fun copySamplePhoto(cacheDir: File): Uri {
        val outFile = File(cacheDir, "venue_message_wrap.png")
        if (!outFile.exists()) {
            val stream = javaClass.classLoader!!.getResourceAsStream("samples/couple_portrait.png")
                ?: error("Missing test resource samples/couple_portrait.png")
            FileOutputStream(outFile).use { out -> stream.copyTo(out) }
        }
        return Uri.fromFile(outFile)
    }
}
