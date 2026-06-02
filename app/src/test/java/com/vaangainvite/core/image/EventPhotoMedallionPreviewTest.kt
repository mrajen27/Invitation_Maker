package com.vaangainvite.core.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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
class EventPhotoMedallionPreviewTest {

    @Test
    fun exportBabyShowerAndNamingWithMedallionPhoto() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val generator = InvitationImageGenerator(context)
        val repo = TemplateRepository()
        val outputDir = File(
            System.getenv("PREVIEW_OUTPUT_DIR") ?: "artifacts/medallion-previews"
        )
        outputDir.mkdirs()

        val photoUri = writePlaceholderPortrait(context.cacheDir)

        val samples = listOf(
            "babyshower_01" to InvitationDetails(
                occasionTitle = "Baby Shower",
                name = "Keerthi",
                date = "Saturday, 12 April 2025",
                time = "5:00 PM onwards",
                venue = "Sunrise Banquet Hall\nECR, Chennai",
                mobileNumber = "+91 95001 33445",
                message = "A little miracle is on the way!"
            ),
            "naming_01" to InvitationDetails(
                occasionTitle = "Naming Ceremony",
                name = "Baby Aadhya",
                date = "Sunday, 2 February 2025",
                time = "11:00 AM",
                venue = "Our Residence\nCoimbatore",
                mobileNumber = "+91 94440 11223",
                message = "Bless our little one on this sacred day."
            )
        )

        samples.forEach { (id, details) ->
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

    private fun writePlaceholderPortrait(cacheDir: File): Uri {
        val file = File(cacheDir, "medallion_preview_portrait.png")
        val size = 800
        val bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.parseColor("#FFE0EC"))
        val paint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#F48FB1") }
        canvas.drawCircle(size / 2f, size / 2.2f, size * 0.28f, paint)
        paint.color = Color.parseColor("#AD1457")
        canvas.drawCircle(size / 2f, size / 2.5f, size * 0.12f, paint)
        FileOutputStream(file).use { bitmap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        bitmap.recycle()
        return Uri.fromFile(file)
    }
}
