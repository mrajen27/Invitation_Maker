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
class NamingCeremonyPreviewExportTest {

    @Test
    fun exportAllNamingTemplates() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val generator = InvitationImageGenerator(context)
        val repo = TemplateRepository()
        val outputDir = File(
            System.getenv("PREVIEW_OUTPUT_DIR") ?: "artifacts/naming-redesign"
        )
        outputDir.mkdirs()

        val details = InvitationDetails(
            occasionTitle = "Naming Ceremony",
            name = "Baby Aadhya",
            date = "Sunday, 2 February 2025",
            time = "11:00 AM",
            venue = "Our Residence\nCoimbatore",
            mobileNumber = "+91 94440 11223",
            message = "Bless our little one on this sacred day."
        )

        (1..5).forEach { index ->
            val id = "naming_%02d".format(index)
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
