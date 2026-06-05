package com.vaangainvite.core.image

import android.graphics.RectF
import android.net.Uri
import androidx.test.core.app.ApplicationProvider
import com.vaangainvite.data.model.InvitationDetails
import com.vaangainvite.data.model.InvitationFieldLimits
import com.vaangainvite.data.model.InvitationLanguage
import com.vaangainvite.data.repository.TemplateRepository
import org.junit.After
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
class AllCategoriesPhotoBoundsTest {

    @After
    fun tearDown() {
        InvitationLayout.resetPhotoFramesForTests()
    }

    @Test
    fun allCategoriesCurrentFramesFitMaxLengthCopy() {
        val failures = mutableListOf<String>()
        allPhotoWebpTemplateIds().forEach { id ->
            val report = render(id)
            if (!report.messageShown) failures += "$id message not shown"
            if (report.messageTruncated) failures += "$id message truncated"
        }
        assertTrue(failures.joinToString("\n"), failures.isEmpty())
    }

    @Test
    fun birthdayPubertyHousewarmingDefaultExpandedWideFitsMaxText() {
        InvitationLayout.setDefaultPhotoFrameForTests(InvitationLayout.expandedDefaultPhotoFrame)
        defaultFrameTemplateIds().forEach { id ->
            assertMaxTextOk(id, label = "expanded-default")
        }
    }

    @Test
    fun engagementNamingBabyShowerTallFrameFitsMaxText() {
        PhotoCardPlacement.setFramedPhotoForTests(PhotoCardPlacement.tallFramedPhoto)
        photoCardTemplateIds().forEach { id ->
            assertMaxTextOk(id, label = "expanded-photo-card")
        }
    }

    @Test
    fun weddingCustomTemplatesExpandedWideFitsMaxText() {
        InvitationLayout.setTemplatePhotoFrameForTests(
            "wedding_02",
            RectF(320f, 308f, 760f, 498f)
        )
        InvitationLayout.setTemplatePhotoFrameForTests(
            "wedding_04",
            RectF(300f, 410f, 780f, 530f)
        )
        InvitationLayout.setTemplatePhotoFrameForTests(
            "wedding_05",
            RectF(390f, 360f, 690f, 490f)
        )
        listOf("wedding_02", "wedding_04", "wedding_05").forEach { id ->
            assertMaxTextOk(id, label = "expanded-wedding")
        }
    }

    @Test
    fun housewarmingGopuramExpandedWideFitsMaxText() {
        InvitationLayout.setTemplatePhotoFrameForTests(
            "housewarming_03",
            RectF(300f, 360f, 780f, 530f)
        )
        assertMaxTextOk("housewarming_03", label = "expanded-gopuram")
    }

    @Test
    fun tallDefaultFrameTruncatesMaxLengthMessage() {
        InvitationLayout.setDefaultPhotoFrameForTests(RectF(340f, 248f, 740f, 598f))
        val report = render("birthday_01")
        assertTrue(report.messageTruncated)
    }

    private fun assertMaxTextOk(templateId: String, label: String) {
        val report = render(templateId)
        assertTrue("$label $templateId message not shown", report.messageShown)
        assertFalse("$label $templateId message truncated", report.messageTruncated)
    }

    private fun render(templateId: String) =
        InvitationImageGenerator(ApplicationProvider.getApplicationContext())
            .createInvitationBitmap(
                template = TemplateRepository().templateById(templateId)!!,
                details = maxDetails(),
                language = InvitationLanguage.ENGLISH,
                uploadedPhotoUri = copySamplePhoto(
                    ApplicationProvider.getApplicationContext<android.content.Context>().cacheDir
                )
            ).renderReport

    private fun maxDetails() = InvitationDetails(
        occasionTitle = "E".repeat(InvitationFieldLimits.OCCASION_MAX_LENGTH),
        name = "N".repeat(InvitationFieldLimits.NAME_MAX_LENGTH),
        date = "D".repeat(InvitationFieldLimits.DATE_MAX_LENGTH),
        time = "T".repeat(InvitationFieldLimits.TIME_MAX_LENGTH),
        venue = "V".repeat(InvitationFieldLimits.VENUE_MAX_LENGTH),
        mobileNumber = "+".repeat(InvitationFieldLimits.MOBILE_MAX_LENGTH),
        message = "M".repeat(InvitationFieldLimits.MESSAGE_MAX_LENGTH_WITH_PHOTO)
    )

    private fun allPhotoWebpTemplateIds(): List<String> =
        listOf("birthday", "wedding", "housewarming", "puberty", "engagement", "naming", "babyshower")
            .flatMap { category -> (1..5).map { i -> "${category}_%02d".format(i) } }

    private fun defaultFrameTemplateIds(): List<String> =
        (1..5).map { "birthday_%02d".format(it) } +
            (1..5).map { "puberty_%02d".format(it) } +
            (1..5).map { "housewarming_%02d".format(it) }.filter { it != "housewarming_03" } +
            listOf("wedding_01", "wedding_03")

    private fun customFrameTemplateIds() = listOf(
        "wedding_02", "wedding_04", "wedding_05", "housewarming_03"
    )

    private fun photoCardTemplateIds(): List<String> =
        (1..5).flatMap { i ->
            listOf(
                "engagement_%02d".format(i),
                "naming_%02d".format(i),
                "babyshower_%02d".format(i)
            )
        }

    private fun copySamplePhoto(cacheDir: File): Uri {
        val outFile = File(cacheDir, "all_categories_bounds.png")
        if (!outFile.exists()) {
            val stream = javaClass.classLoader!!.getResourceAsStream("samples/couple_portrait.png")
                ?: error("Missing test resource samples/couple_portrait.png")
            FileOutputStream(outFile).use { out -> stream.copyTo(out) }
        }
        return Uri.fromFile(outFile)
    }
}
