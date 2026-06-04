package com.vaangainvite.core.image

import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.vaangainvite.data.model.InvitationDetails
import com.vaangainvite.data.model.InvitationLanguage
import com.vaangainvite.data.model.InvitationFieldLimits
import com.vaangainvite.data.repository.TemplateRepository
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.io.FileOutputStream

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class EngagementMaxTextRenderTest {

    @Test
    fun engagement01And02RenderMaxLengthWithoutMessageTruncation() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val generator = InvitationImageGenerator(context)
        val repo = TemplateRepository()
        val photoUri = copySamplePhoto(context.cacheDir)
        val details = InvitationDetails(
            occasionTitle = "E".repeat(InvitationFieldLimits.OCCASION_MAX_LENGTH),
            name = "N".repeat(InvitationFieldLimits.NAME_MAX_LENGTH),
            date = "D".repeat(InvitationFieldLimits.DATE_MAX_LENGTH),
            time = "T".repeat(InvitationFieldLimits.TIME_MAX_LENGTH),
            venue = "V".repeat(InvitationFieldLimits.VENUE_MAX_LENGTH),
            mobileNumber = "+".repeat(InvitationFieldLimits.MOBILE_MAX_LENGTH),
            message = "M".repeat(InvitationFieldLimits.MESSAGE_MAX_LENGTH_WITH_PHOTO)
        )

        listOf("engagement_01", "engagement_02").forEach { id ->
            val template = repo.templateById(id) ?: return@forEach
            val report = generator.createInvitationBitmap(
                template = template,
                details = details,
                language = InvitationLanguage.ENGLISH,
                uploadedPhotoUri = photoUri
            ).renderReport
            assertFalse("$id message truncated", report.messageTruncated)
        }
    }

    private fun copySamplePhoto(cacheDir: File): Uri {
        val outFile = File(cacheDir, "engagement_max_text_portrait.png")
        if (!outFile.exists()) {
            val stream = javaClass.classLoader!!.getResourceAsStream("samples/couple_portrait.png")
                ?: error("Missing test resource samples/couple_portrait.png")
            FileOutputStream(outFile).use { out -> stream.copyTo(out) }
        }
        return Uri.fromFile(outFile)
    }
}
