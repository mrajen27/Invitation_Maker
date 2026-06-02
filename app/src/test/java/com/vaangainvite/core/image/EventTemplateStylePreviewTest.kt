package com.vaangainvite.core.image

import android.graphics.Bitmap
import androidx.test.core.app.ApplicationProvider
import com.vaangainvite.data.model.InvitationDetails
import com.vaangainvite.data.model.InvitationLanguage
import com.vaangainvite.data.repository.TemplateRepository
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.GraphicsMode
import org.robolectric.RobolectricTestRunner
import java.io.File
import java.io.FileOutputStream

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class EventTemplateStylePreviewTest {

    @Test
    fun exportStyleComparisonPreviews() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val generator = InvitationImageGenerator(context)
        val repo = TemplateRepository()
        val outputDir = File(
            System.getenv("PREVIEW_OUTPUT_DIR") ?: "artifacts/event-style-previews"
        )
        outputDir.mkdirs()

        val details = InvitationDetails(
            occasionTitle = "Sample Celebration",
            name = "Karthik & Meena",
            date = "Sunday, 25 May 2025",
            time = "6:00 PM onwards",
            venue = "The Grand Pavilion\nAnna Nagar, Chennai",
            mobileNumber = "+91 98765 43210",
            message = "With love — please join us!"
        )

        val ids = listOf(
            "housewarming_01",
            "engagement_01",
            "naming_01",
            "babyshower_01"
        )
        ids.forEach { id ->
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
