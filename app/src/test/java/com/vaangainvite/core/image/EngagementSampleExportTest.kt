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
class EngagementSampleExportTest {

    @Test
    fun exportEngagementWithPhotoAndFullText() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val generator = InvitationImageGenerator(context)
        val repo = TemplateRepository()
        val outputDir = File(
            System.getenv("PREVIEW_OUTPUT_DIR") ?: "docs/samples/engagement"
        )
        outputDir.mkdirs()

        val photoUri = copySamplePhoto(context.cacheDir)
        val details = InvitationDetails(
            occasionTitle = "Engagement Ceremony",
            name = "Kavin & Sowmya",
            date = "29 May 2026",
            time = "8:00 PM",
            venue = "Annamalaiyar Mahal, Tiruvannamalai",
            mobileNumber = "+91 98765 43210",
            message = "Your presence means a lot."
        )

        (1..5).forEach { index ->
            val id = "engagement_%02d".format(index)
            val template = repo.templateById(id) ?: return@forEach
            val bitmap = generator.createInvitationBitmap(
                template = template,
                details = details,
                language = InvitationLanguage.ENGLISH,
                uploadedPhotoUri = photoUri
            ).bitmap
            File(outputDir, "${id}_with_photo.png").outputStream().use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }
            bitmap.recycle()
        }
    }

    private fun copySamplePhoto(cacheDir: File): Uri {
        val outFile = File(cacheDir, "engagement_sample_portrait.png")
        if (!outFile.exists()) {
            val stream = javaClass.classLoader!!.getResourceAsStream("samples/couple_portrait.png")
                ?: error("Missing test resource samples/couple_portrait.png")
            FileOutputStream(outFile).use { out -> stream.copyTo(out) }
        }
        return Uri.fromFile(outFile)
    }
}
