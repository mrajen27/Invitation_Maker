package com.vaangainvite.core.image

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
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

/**
 * Exports one filled invitation per category for design review.
 * Output: workspace/artifacts/category-previews/{category}.png
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class InvitationCategoryPreviewExportTest {

  private val repository = TemplateRepository()

  @Test
  fun exportOnePreviewPerCategory() {
    val context = ApplicationProvider.getApplicationContext<android.content.Context>()
    val generator = InvitationImageGenerator(context)
    val outputDir = File(System.getenv("PREVIEW_OUTPUT_DIR") ?: "artifacts/category-previews")
    outputDir.mkdirs()

    val samples = sampleDetailsByCategory()
    repository.categories.forEach { category ->
      val template = repository.templatesForCategory(category.id).firstOrNull()
        ?: error("No template for ${category.id}")
      val details = samples[category.id] ?: InvitationDetails()
      val bitmap = generator.createInvitationBitmap(
        template = template,
        details = details,
        language = InvitationLanguage.ENGLISH,
        uploadedPhotoUri = null
      ).bitmap
      val file = File(outputDir, "${category.id}_${template.id}.png")
      FileOutputStream(file).use { out ->
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
      }
      bitmap.recycle()
    }
  }

  private fun sampleDetailsByCategory(): Map<String, InvitationDetails> {
    return mapOf(
      "birthday" to InvitationDetails(
        occasionTitle = "5th Birthday Celebration",
        name = "Kavin",
        date = "Sunday, 25 May 2025",
        time = "6:00 PM onwards",
        venue = "The Grand Pavilion\nAnna Nagar, Chennai",
        mobileNumber = "+91 98765 43210",
        message = "Join us for cake, fun & blessings!"
      ),
      "wedding" to InvitationDetails(
        occasionTitle = "Wedding Invitation",
        name = "Karthik & Meena",
        date = "Sunday, 15 June 2025",
        time = "9:00 AM Muhurtham",
        venue = "Sri Meenakshi Kalyana\nMadurai",
        mobileNumber = "+91 91234 56789",
        message = "Your blessings mean the world to us."
      ),
      "housewarming" to InvitationDetails(
        occasionTitle = "Grihapravesam",
        name = "Ravi & Family",
        date = "Friday, 10 January 2025",
        time = "8:00 AM onwards",
        venue = "12, Temple Street\nVelachery, Chennai",
        mobileNumber = "+91 99887 76655",
        message = "Please bless our new home."
      ),
      "puberty" to InvitationDetails(
        occasionTitle = "Puberty Ceremony",
        name = "Priya",
        date = "Saturday, 8 March 2025",
        time = "10:00 AM",
        venue = "Community Hall\nT. Nagar, Chennai",
        mobileNumber = "+91 90031 12233",
        message = "With family & friends — please join us."
      ),
      "engagement" to InvitationDetails(
        occasionTitle = "Engagement Celebration",
        name = "Arjun & Divya",
        date = "Sunday, 25 May 2025",
        time = "6:00 PM onwards",
        venue = "The Grand Pavilion\nAnna Nagar, Chennai",
        mobileNumber = "+91 98765 43210",
        message = "Together begins our forever."
      ),
      "naming" to InvitationDetails(
        occasionTitle = "Naming Ceremony",
        name = "Baby Aadhya",
        date = "Sunday, 2 February 2025",
        time = "11:00 AM",
        venue = "Our Residence\nCoimbatore",
        mobileNumber = "+91 94440 11223",
        message = "Bless our little one on this sacred day."
      ),
      "babyshower" to InvitationDetails(
        occasionTitle = "Baby Shower",
        name = "Keerthi",
        date = "Saturday, 12 April 2025",
        time = "5:00 PM onwards",
        venue = "Sunrise Banquet Hall\nECR, Chennai",
        mobileNumber = "+91 95001 33445",
        message = "A little miracle is on the way!"
      )
    )
  }
}
