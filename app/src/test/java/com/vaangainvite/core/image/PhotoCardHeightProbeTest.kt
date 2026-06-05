package com.vaangainvite.core.image

import android.graphics.RectF
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.vaangainvite.data.model.InvitationDetails
import com.vaangainvite.data.model.InvitationFieldLimits
import com.vaangainvite.data.model.InvitationLanguage
import com.vaangainvite.data.repository.TemplateRepository
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.io.FileOutputStream

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class PhotoCardHeightProbeTest {

    @Test
    fun probeMaxPhotoBottomWithTallTextZone() {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val generator = InvitationImageGenerator(context)
        val repo = TemplateRepository()
        val photoUri = copySamplePhoto(context.cacheDir)
        val details = maxDetails()
        val ids = allPhotoCardIds()

        var maxBottom = 478
        for (bottom in 479..620) {
            PhotoCardPlacement.setFramedPhotoForTests(RectF(280f, 268f, 800f, bottom.toFloat()))
            val failures = failingTemplates(generator, repo, ids, details, photoUri)
            if (failures.isEmpty()) maxBottom = bottom else break
        }
        println("MAX_SAFE_BOTTOM=$maxBottom height=${maxBottom - 268}")
    }

    @Test
    fun productionTallFrameFitsMaxLengthOnAllPhotoCards() {
        PhotoCardPlacement.setFramedPhotoForTests(PhotoCardPlacement.tallFramedPhoto)
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val generator = InvitationImageGenerator(context)
        val repo = TemplateRepository()
        val photoUri = copySamplePhoto(context.cacheDir)
        val failures = failingTemplates(generator, repo, allPhotoCardIds(), maxDetails(), photoUri)
        assertTrue("failing: $failures", failures.isEmpty())
    }

    private fun failingTemplates(
        generator: InvitationImageGenerator,
        repo: TemplateRepository,
        ids: List<String>,
        details: InvitationDetails,
        photoUri: Uri
    ) = ids.filter { id ->
        val report = generator.createInvitationBitmap(
            template = repo.templateById(id)!!,
            details = details,
            language = InvitationLanguage.ENGLISH,
            uploadedPhotoUri = photoUri
        ).renderReport
        !report.messageShown || report.messageTruncated
    }

    private fun allPhotoCardIds() = (1..5).flatMap { i ->
        listOf("engagement_%02d".format(i), "naming_%02d".format(i), "babyshower_%02d".format(i))
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
        val outFile = File(cacheDir, "photo_card_height_probe.png")
        if (!outFile.exists()) {
            val stream = javaClass.classLoader!!.getResourceAsStream("samples/couple_portrait.png")
                ?: error("Missing test resource samples/couple_portrait.png")
            FileOutputStream(outFile).use { out -> stream.copyTo(out) }
        }
        return Uri.fromFile(outFile)
    }
}
