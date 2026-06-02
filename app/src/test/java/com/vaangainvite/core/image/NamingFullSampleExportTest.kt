package com.vaangainvite.core.image

import android.graphics.Bitmap
import android.net.Uri
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
class NamingFullSampleExportTest {

    @Test
    fun exportNaming01WithPhotoAndAllFields() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val generator = InvitationImageGenerator(context)
        val repo = TemplateRepository()
        val outputDir = File(
            System.getenv("PREVIEW_OUTPUT_DIR") ?: "artifacts/naming-full"
        )
        outputDir.mkdirs()

        val photoBytes = javaClass.getResourceAsStream("/samples/naming_baby_photo.png")
            ?: error("Missing test resource samples/naming_baby_photo.png")
        val cacheCopy = File(context.cacheDir, "naming_baby_photo.png")
        cacheCopy.outputStream().use { out -> photoBytes.copyTo(out) }
        val photoUri = Uri.fromFile(cacheCopy)

        val details = InvitationDetails(
            occasionTitle = "Naming Ceremony",
            name = "Baby Aadhya",
            date = "Sunday, 2 February 2025",
            time = "11:00 AM",
            venue = "Our Residence\nCoimbatore",
            mobileNumber = "+91 94440 11223",
            message = "Bless our little one on this sacred day."
        )

        val template = repo.templateById("naming_01")!!
        val bitmap = generator.createInvitationBitmap(
            template = template,
            details = details,
            language = InvitationLanguage.ENGLISH,
            uploadedPhotoUri = photoUri
        ).bitmap
        File(outputDir, "naming_01_with_photo.png").outputStream().use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
        bitmap.recycle()
    }
}
