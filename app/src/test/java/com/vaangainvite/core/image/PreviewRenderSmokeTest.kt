package com.vaangainvite.core.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import androidx.test.core.app.ApplicationProvider
import com.vaangainvite.R
import org.junit.Assert.assertNotEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode
import java.io.File

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PreviewRenderSmokeTest {

    @Test
    fun canvasDrawsPixels() {
        val bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888)
        Canvas(bitmap).drawColor(Color.RED)
        assertNotEquals(0, bitmap.getPixel(50, 50))
    }

    @Test
    fun vectorDrawableDrawsPixels() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val bitmap = Bitmap.createBitmap(1080, 1350, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        canvas.drawColor(Color.WHITE)
        val drawable = context.getDrawable(R.drawable.template_engagement_01)
        requireNotNull(drawable) { "Drawable null" }
        drawable.setBounds(0, 0, 1080, 1350)
        drawable.draw(canvas)
        val pixel = bitmap.getPixel(540, 675)
        assertNotEquals(0, pixel)
        File("/tmp/engagement_smoke.png").outputStream().use {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, it)
        }
    }
}
