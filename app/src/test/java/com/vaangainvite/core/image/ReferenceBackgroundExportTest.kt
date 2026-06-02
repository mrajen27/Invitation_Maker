package com.vaangainvite.core.image

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.test.core.app.ApplicationProvider
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.io.FileOutputStream

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class ReferenceBackgroundExportTest {

    @Test
    fun exportReferenceBackgroundWebps() {
        ApplicationProvider.getApplicationContext<android.content.Context>()
        val outputDir = File(
            System.getenv("BG_OUTPUT_DIR")
                ?: "app/src/main/res/drawable-nodpi"
        )
        outputDir.mkdirs()

        val templateIds = buildList {
            (1..5).forEach { add("engagement_%02d".format(it)) }
            (1..5).forEach { add("naming_%02d".format(it)) }
            (1..5).forEach { add("babyshower_%02d".format(it)) }
        }

        templateIds.forEach { templateId ->
            val bitmap = Bitmap.createBitmap(1080, 1350, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap)
            InvitationBackgroundPainter.draw(canvas, templateId, 1080, 1350)
            val file = File(outputDir, "bg_$templateId.webp")
            FileOutputStream(file).use { output ->
                bitmap.compress(Bitmap.CompressFormat.WEBP_LOSSY, 92, output)
            }
            bitmap.recycle()
        }
    }
}
