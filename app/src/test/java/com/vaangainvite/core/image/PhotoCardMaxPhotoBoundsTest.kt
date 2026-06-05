package com.vaangainvite.core.image

import android.graphics.RectF
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.vaangainvite.data.model.InvitationDetails
import com.vaangainvite.data.model.InvitationFieldLimits
import com.vaangainvite.data.model.InvitationLanguage
import com.vaangainvite.data.repository.TemplateRepository
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.io.FileOutputStream

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PhotoCardMaxPhotoBoundsTest {

  @Test
  fun expandedWidePhotoFitsMaxLengthCopy() {
    assertMaxTextFits(PhotoCardPlacement.expandedFramedPhoto)
  }

  @Test
  fun tallPhotoFitsMaxLengthCopy() {
    assertMaxTextFits(PhotoCardPlacement.tallFramedPhoto)
  }

  @Test
  fun compactPhotoStillFitsMaxLengthCopy() {
    assertMaxTextFits(PhotoCardPlacement.compactFramedPhoto)
  }

  @Test
  fun tallPhotoTruncatesMaxLengthMessage() {
    PhotoCardPlacement.setFramedPhotoForTests(RectF(280f, 268f, 800f, 545f))
    val context = ApplicationProvider.getApplicationContext<android.content.Context>()
    val generator = InvitationImageGenerator(context)
    val template = TemplateRepository().templateById("engagement_01")!!
    val report = generator.createInvitationBitmap(
      template = template,
      details = maxDetails(),
      language = InvitationLanguage.ENGLISH,
      uploadedPhotoUri = copySamplePhoto(context.cacheDir)
    ).renderReport
    PhotoCardPlacement.setFramedPhotoForTests(PhotoCardPlacement.framedPhoto)
    assertTrue(
      "expected truncation or no room for message",
      report.messageTruncated || !report.messageShown
    )
  }

  private fun assertMaxTextFits(frame: RectF) {
    PhotoCardPlacement.setFramedPhotoForTests(frame)
    val context = ApplicationProvider.getApplicationContext<android.content.Context>()
    val generator = InvitationImageGenerator(context)
    val repo = TemplateRepository()
    val photoUri = copySamplePhoto(context.cacheDir)
    val details = maxDetails()
    listOf("engagement_01", "naming_01", "babyshower_01", "engagement_05").forEach { id ->
      val template = repo.templateById(id) ?: return@forEach
      val report = generator.createInvitationBitmap(
        template = template,
        details = details,
        language = InvitationLanguage.ENGLISH,
        uploadedPhotoUri = photoUri
      ).renderReport
      assertTrue("$id message not shown", report.messageShown)
      assertFalse("$id message truncated", report.messageTruncated)
    }
    PhotoCardPlacement.setFramedPhotoForTests(PhotoCardPlacement.compactFramedPhoto)
  }

  private fun maxDetails() = InvitationDetails(
    occasionTitle = "E".repeat(InvitationFieldLimits.OCCASION_MAX_LENGTH),
    name = "N".repeat(InvitationFieldLimits.NAME_MAX_LENGTH),
    date = "D".repeat(InvitationFieldLimits.DATE_MAX_LENGTH),
    time = "T".repeat(InvitationFieldLimits.TIME_MAX_LENGTH),
    venue = "V".repeat(InvitationFieldLimits.VENUE_MAX_LENGTH),
    mobileNumber = "+".repeat(InvitationFieldLimits.MOBILE_MAX_LENGTH),
    message = "M".repeat(InvitationFieldLimits.MESSAGE_MAX_LENGTH_WITH_PHOTO)
  )

  private fun copySamplePhoto(cacheDir: File): Uri {
    val outFile = File(cacheDir, "photo_card_max_bounds.png")
    if (!outFile.exists()) {
      val stream = javaClass.classLoader!!.getResourceAsStream("samples/couple_portrait.png")
        ?: error("Missing test resource samples/couple_portrait.png")
      FileOutputStream(outFile).use { out -> stream.copyTo(out) }
    }
    return Uri.fromFile(outFile)
  }
}
