package com.vaangainvite.core.image

import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider
import com.vaangainvite.data.model.InvitationDetails
import com.vaangainvite.data.model.InvitationLanguage
import com.vaangainvite.data.repository.TemplateRepository
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.io.FileOutputStream

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class EngagementPreviewExportTest {

    @Test
    fun exportAllEngagementTemplates() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val generator = InvitationImageGenerator(context)
        val repo = TemplateRepository()
        val outputDir = File(
            System.getenv("PREVIEW_OUTPUT_DIR") ?: "docs/samples/engagement"
        )
        outputDir.mkdirs()

        val details = InvitationDetails(
            occasionTitle = "Engagement Ceremony",
            name = "Arjun & Divya",
            date = "Sunday, 25 May 2026",
            time = "6:00 PM onwards",
            venue = "Annamalaiyar Mahal\n5th Street, Tiruvannamalai",
            mobileNumber = "+91 98765 43210",
            message = "Together we begin our forever — please join us."
        )

        (1..5).forEach { index ->
            val id = "engagement_%02d".format(index)
            val template = repo.templateById(id) ?: return@forEach
            val bitmap = generator.createInvitationBitmap(
                template = template,
                details = details,
                language = InvitationLanguage.ENGLISH,
                uploadedPhotoUri = null
            ).bitmap
            File(outputDir, "$id.png").outputStream().use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            bitmap.recycle()
        }
    }
}
