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
class BabyShowerSampleExportTest {

    @Test
    fun exportBabyShowerWithPhotoAndFullText() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val generator = InvitationImageGenerator(context)
        val repo = TemplateRepository()
        val outputDir = resolvePreviewOutputDir()
        outputDir.mkdirs()

        val photoUri = copySamplePhoto(context.cacheDir)
        val details = InvitationDetails(
            occasionTitle = "Baby Shower",
            name = "Mom-to-be Priya",
            date = "Sunday, 20 July 2026",
            time = "11:00 AM",
            venue = "Green Meadows Hall, Chennai",
            mobileNumber = "+91 98765 43210",
            message = "Your presence and blessings will make our day special."
        )

        (1..5).forEach { index ->
            val id = "babyshower_%02d".format(index)
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

    private fun resolvePreviewOutputDir(): File {
        System.getenv("PREVIEW_OUTPUT_DIR")?.let { path ->
            val dir = File(path)
            if (dir.isAbsolute) return dir
            val moduleDir = File(System.getProperty("user.dir"))
            val repoRoot = if (moduleDir.name == "app") moduleDir.parentFile!! else moduleDir
            return File(repoRoot, path)
        }
        val moduleDir = File(System.getProperty("user.dir"))
        val repoRoot = if (moduleDir.name == "app") moduleDir.parentFile!! else moduleDir
        return File(repoRoot, "docs/samples/babyshower")
    }

    private fun copySamplePhoto(cacheDir: File): Uri {
        val outFile = File(cacheDir, "babyshower_sample_portrait.png")
        if (!outFile.exists()) {
            val stream = javaClass.classLoader!!.getResourceAsStream("samples/couple_portrait.png")
                ?: error("Missing test resource samples/couple_portrait.png")
            FileOutputStream(outFile).use { out -> stream.copyTo(out) }
        }
        return Uri.fromFile(outFile)
    }
}
