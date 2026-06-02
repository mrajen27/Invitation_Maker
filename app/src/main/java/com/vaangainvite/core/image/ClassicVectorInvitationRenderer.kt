package com.vaangainvite.core.image

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Matrix
import android.graphics.Paint
import android.graphics.Path
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.content.res.ResourcesCompat
import com.vaangainvite.R
import com.vaangainvite.data.model.InvitationDetails
import com.vaangainvite.data.model.InvitationFieldLimits
import com.vaangainvite.data.model.InvitationLanguage
import com.vaangainvite.data.model.InvitationTemplate
import com.vaangainvite.data.model.clampedForCard
import java.io.File
import java.io.FileOutputStream

/** Renders legacy vector-arch templates (birthday, wedding, housewarming, puberty). */
class ClassicVectorInvitationRenderer(private val context: Context) {
    private val imageWidth = InvitationTextLayout.canvasWidth.toInt()
    private val imageHeight = InvitationTextLayout.canvasHeight.toInt()

    fun createInvitationBitmap(
        template: InvitationTemplate,
        details: InvitationDetails,
        language: InvitationLanguage,
        uploadedPhotoUri: Uri?
    ): Bitmap {
        val hasUploadedPhoto = uploadedPhotoUri != null
        val cardDetails = details.clampedForCard(hasUploadedPhoto)
        val bitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        ContextCompat.getDrawable(context, template.drawableResId)?.let { drawable ->
            drawable.setBounds(0, 0, imageWidth, imageHeight)
            drawable.draw(canvas)
        } ?: canvas.drawColor(Color.rgb(255, 244, 230))

        drawReadablePanel(canvas, hasUploadedPhoto)
        val photoDrawn = drawUploadedPhoto(canvas, uploadedPhotoUri)
        drawInvitationText(
            canvas = canvas,
            template = template,
            details = cardDetails,
            language = language,
            hasUploadedPhoto = photoDrawn
        )

        return bitmap
    }

    fun saveBitmapToCache(bitmap: Bitmap): Uri {
        val shareDirectory = File(context.cacheDir, "shared_invites").apply {
            mkdirs()
        }
        val file = File(shareDirectory, "vaanga_invite_${System.currentTimeMillis()}.png")
        FileOutputStream(file).use { output ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
        }
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }

    fun saveBitmapToGallery(bitmap: Bitmap): Uri {
        val filename = "Vaanga_Invite_${System.currentTimeMillis()}.png"
        val resolver = context.contentResolver
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, filename)
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                put(
                    MediaStore.Images.Media.RELATIVE_PATH,
                    "${Environment.DIRECTORY_PICTURES}/Vaanga Invite"
                )
                put(MediaStore.Images.Media.IS_PENDING, 1)
            }
        }

        val uri = requireNotNull(
            resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        ) {
            "Unable to create gallery image"
        }

        resolver.openOutputStream(uri)?.use { output ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output)
        } ?: error("Unable to open gallery image output stream")

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            contentValues.clear()
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(uri, contentValues, null, null)
        }

        return uri
    }

    private fun drawReadablePanel(canvas: Canvas, hasPhoto: Boolean) {
        val panel = InvitationTextLayout.frostedPanelBounds(hasPhoto)
        val panelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            alpha = 228
        }
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(247, 201, 72)
            style = Paint.Style.STROKE
            strokeWidth = 8f
        }
        canvas.drawRoundRect(panel, 36f, 36f, panelPaint)
        canvas.drawRoundRect(panel, 36f, 36f, borderPaint)
    }

    private fun drawInvitationText(
        canvas: Canvas,
        template: InvitationTemplate,
        details: InvitationDetails,
        language: InvitationLanguage,
        hasUploadedPhoto: Boolean
    ) {
        val zone = InvitationTextLayout.textZone(hasUploadedPhoto)
        val primary = template.primaryColor
        val tamilTypeface = tamilTypeface()
        val headingTypeface = when (language) {
            InvitationLanguage.TAMIL -> Typeface.create(tamilTypeface, Typeface.BOLD)
            InvitationLanguage.ENGLISH -> Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }
        val bodyTypeface = when (language) {
            InvitationLanguage.TAMIL -> tamilTypeface
            InvitationLanguage.ENGLISH -> Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        }

        var scale = 1f
        val basePaints = buildContentPaints(
            primary = primary,
            headingTypeface = headingTypeface,
            bodyTypeface = bodyTypeface,
            hasUploadedPhoto = hasUploadedPhoto,
            scale = 1f
        )
        scale = InvitationTextLayout.contentScaleForZone(
            estimatedHeight = InvitationTextLayout.estimateContentHeight(
                details = details,
                hasPhoto = hasUploadedPhoto,
                hasMobile = details.mobileNumber.isNotBlank(),
                paints = basePaints
            ),
            zone = zone,
            hasPhoto = hasUploadedPhoto
        )
        val paints = if (scale == 1f) {
            basePaints
        } else {
            buildContentPaints(
                primary = primary,
                headingTypeface = headingTypeface,
                bodyTypeface = bodyTypeface,
                hasUploadedPhoto = hasUploadedPhoto,
                scale = scale
            )
        }

        val maxTextWidth = zone.width() - 32f
        var blockTop = InvitationTextLayout.textStartY(hasUploadedPhoto)

        blockTop = drawCenteredLines(
            canvas = canvas,
            text = language.heading,
            paint = paints.headingPaint,
            startY = blockTop,
            maxWidth = maxTextWidth,
            lineSpacing = 4f,
            maxLines = 1,
            zone = zone
        ) + InvitationTextLayout.Spacing.afterHeading

        blockTop = drawCenteredLines(
            canvas = canvas,
            text = details.name.ifBlank { language.fallbackName },
            paint = paints.titlePaint,
            startY = blockTop,
            maxWidth = maxTextWidth,
            lineSpacing = 4f,
            maxLines = 2,
            zone = zone
        ) + InvitationTextLayout.Spacing.afterName

        blockTop = drawCenteredLines(
            canvas = canvas,
            text = details.occasionTitle.ifBlank { defaultOccasionTitle(language) },
            paint = paints.occasionPaint,
            startY = blockTop,
            maxWidth = maxTextWidth,
            lineSpacing = 4f,
            maxLines = 2,
            zone = zone
        ) + InvitationTextLayout.Spacing.afterOccasion

        blockTop = drawDetailLine(
            canvas = canvas,
            label = language.dateLabel,
            value = details.date.ifBlank { language.fallbackDate },
            paint = paints.bodyPaint,
            startY = blockTop,
            maxWidth = maxTextWidth,
            maxLines = 1,
            zone = zone
        )
        blockTop = drawDetailLine(
            canvas = canvas,
            label = language.timeLabel,
            value = details.time.ifBlank { language.fallbackTime },
            paint = paints.bodyPaint,
            startY = blockTop,
            maxWidth = maxTextWidth,
            maxLines = 1,
            zone = zone
        )
        blockTop = drawDetailLine(
            canvas = canvas,
            label = language.venueLabel,
            value = details.venue.ifBlank { language.fallbackVenue },
            paint = paints.bodyPaint,
            startY = blockTop,
            maxWidth = maxTextWidth,
            maxLines = InvitationFieldLimits.VENUE_MAX_LINES,
            zone = zone
        )
        if (details.mobileNumber.isNotBlank()) {
            blockTop = drawDetailLine(
                canvas = canvas,
                label = contactLabel(language),
                value = details.mobileNumber,
                paint = paints.bodyPaint,
                startY = blockTop,
                maxWidth = maxTextWidth,
                maxLines = 1,
                zone = zone
            )
        }

        val messageText = details.message.trim()
        if (messageText.isBlank()) return

        val messageLines = wrapText(messageText, paints.messagePaint, maxTextWidth.coerceAtMost(620f))
            .limitLines(InvitationFieldLimits.MESSAGE_MAX_LINES_ON_CARD, paints.messagePaint, maxTextWidth)
        if (messageLines.isEmpty()) return

        val messageTop = InvitationTextLayout.messageTopY(
            blockTopAfterLastDetail = blockTop,
            paint = paints.messagePaint,
            lineCount = messageLines.size,
            lineSpacing = 8f,
            zone = zone
        )

        drawCenteredLines(
            canvas = canvas,
            text = messageText,
            paint = paints.messagePaint,
            startY = messageTop,
            maxWidth = maxTextWidth.coerceAtMost(620f),
            lineSpacing = 8f,
            maxLines = InvitationFieldLimits.MESSAGE_MAX_LINES_ON_CARD,
            zone = zone,
            maxBottomY = zone.bottom - 6f
        )
    }

    private fun buildContentPaints(
        primary: Int,
        headingTypeface: Typeface,
        bodyTypeface: Typeface,
        hasUploadedPhoto: Boolean,
        scale: Float
    ): InvitationTextLayout.ContentPaints {
        fun scaled(size: Float) = size * scale
        return InvitationTextLayout.ContentPaints(
            headingPaint = textPaint(
                color = Color.rgb(72, 50, 38),
                textSize = scaled(if (hasUploadedPhoto) 28f else 32f),
                typeface = headingTypeface
            ),
            titlePaint = textPaint(
                color = primary,
                textSize = scaled(if (hasUploadedPhoto) 46f else 52f),
                typeface = headingTypeface
            ),
            occasionPaint = textPaint(
                color = primary,
                textSize = scaled(if (hasUploadedPhoto) 32f else 36f),
                typeface = headingTypeface
            ),
            bodyPaint = textPaint(
                color = Color.rgb(38, 38, 38),
                textSize = scaled(if (hasUploadedPhoto) 26f else 29f),
                typeface = bodyTypeface
            ),
            messagePaint = textPaint(
                color = Color.rgb(72, 50, 38),
                textSize = scaled(if (hasUploadedPhoto) 24f else 27f),
                typeface = bodyTypeface
            )
        )
    }

    private fun defaultOccasionTitle(language: InvitationLanguage): String {
        return when (language) {
            InvitationLanguage.ENGLISH -> "Special Occasion"
            InvitationLanguage.TAMIL -> "சிறப்பு விழா"
        }
    }

    private fun contactLabel(language: InvitationLanguage): String {
        return when (language) {
            InvitationLanguage.ENGLISH -> "Contact"
            InvitationLanguage.TAMIL -> "தொடர்பு"
        }
    }

    private fun drawUploadedPhoto(canvas: Canvas, uploadedPhotoUri: Uri?): Boolean {
        if (uploadedPhotoUri == null) return false

        val photoBitmap = runCatching {
            context.contentResolver.openInputStream(uploadedPhotoUri)?.use { input ->
                BitmapFactory.decodeStream(input)
            }
        }.getOrNull()?.let { bitmap ->
            correctBitmapOrientation(bitmap, uploadedPhotoUri)
        } ?: return false

        val frame = InvitationTextLayout.photoFrame
        val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(70, 0, 0, 0)
        }
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(247, 201, 72)
            style = Paint.Style.STROKE
            strokeWidth = 10f
        }

        canvas.drawRoundRect(
            RectF(frame.left + 8f, frame.top + 10f, frame.right + 8f, frame.bottom + 10f),
            34f,
            34f,
            shadowPaint
        )

        val path = Path().apply {
            addRoundRect(frame, 32f, 32f, Path.Direction.CW)
        }
        canvas.save()
        canvas.clipPath(path)
        canvas.drawBitmap(
            photoBitmap,
            centeredCropSource(photoBitmap, frame),
            frame,
            Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        )
        canvas.restore()
        canvas.drawRoundRect(frame, 32f, 32f, borderPaint)
        return true
    }

    private fun correctBitmapOrientation(bitmap: Bitmap, uploadedPhotoUri: Uri): Bitmap {
        val orientation = runCatching {
            context.contentResolver.openInputStream(uploadedPhotoUri)?.use { input ->
                ExifInterface(input).getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL
                )
            } ?: ExifInterface.ORIENTATION_NORMAL
        }.getOrDefault(ExifInterface.ORIENTATION_NORMAL)

        val matrix = Matrix()
        when (orientation) {
            ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90f)
            ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180f)
            ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270f)
            ExifInterface.ORIENTATION_FLIP_HORIZONTAL -> matrix.postScale(-1f, 1f)
            ExifInterface.ORIENTATION_FLIP_VERTICAL -> matrix.postScale(1f, -1f)
            ExifInterface.ORIENTATION_TRANSPOSE -> {
                matrix.postRotate(90f)
                matrix.postScale(-1f, 1f)
            }
            ExifInterface.ORIENTATION_TRANSVERSE -> {
                matrix.postRotate(270f)
                matrix.postScale(-1f, 1f)
            }
        }

        if (matrix.isIdentity) return bitmap

        return Bitmap.createBitmap(
            bitmap,
            0,
            0,
            bitmap.width,
            bitmap.height,
            matrix,
            true
        )
    }

    private fun centeredCropSource(bitmap: Bitmap, target: RectF): Rect {
        val bitmapAspect = bitmap.width.toFloat() / bitmap.height.toFloat()
        val targetAspect = target.width() / target.height()

        return if (bitmapAspect > targetAspect) {
            val sourceWidth = (bitmap.height * targetAspect).toInt()
            val left = (bitmap.width - sourceWidth) / 2
            Rect(left, 0, left + sourceWidth, bitmap.height)
        } else {
            val sourceHeight = (bitmap.width / targetAspect).toInt()
            val top = (bitmap.height - sourceHeight) / 2
            Rect(0, top, bitmap.width, top + sourceHeight)
        }
    }

    private fun drawDetailLine(
        canvas: Canvas,
        label: String,
        value: String,
        paint: Paint,
        startY: Float,
        maxWidth: Float,
        maxLines: Int,
        zone: RectF
    ): Float {
        val labelPaint = Paint(paint).apply {
            typeface = Typeface.create(paint.typeface, Typeface.BOLD)
        }
        val labelText = "$label: "
        val combinedText = "$labelText$value"
        val lines = wrapText(combinedText, paint, maxWidth)
            .limitLines(maxLines, paint, maxWidth)
        var y = startY
        lines.forEachIndexed { index, line ->
            if (y > zone.bottom) return@forEachIndexed
            if (index == 0 && line.startsWith(labelText)) {
                val x = centeredX(line, paint, zone)
                canvas.drawText(labelText, x, y, labelPaint)
                canvas.drawText(line.removePrefix(labelText), x + labelPaint.measureText(labelText), y, paint)
            } else {
                canvas.drawText(line, centeredX(line, paint, zone), y, paint)
            }
            y += paint.fontSpacing + InvitationTextLayout.Spacing.betweenDetails
        }
        return y + 4f
    }

    private fun drawCenteredLines(
        canvas: Canvas,
        text: String,
        paint: Paint,
        startY: Float,
        maxWidth: Float,
        lineSpacing: Float,
        maxLines: Int,
        zone: RectF,
        maxBottomY: Float = zone.bottom - 6f
    ): Float {
        var y = startY
        wrapText(text, paint, maxWidth)
            .limitLines(maxLines, paint, maxWidth)
            .forEach { line ->
                if (y > maxBottomY) return@forEach
                canvas.drawText(line, centeredX(line, paint, zone), y, paint)
                y += paint.fontSpacing + lineSpacing
            }
        return y
    }

    private fun List<String>.limitLines(
        maxLines: Int,
        paint: Paint,
        maxWidth: Float
    ): List<String> {
        if (maxLines <= 0) return emptyList()
        if (size <= maxLines) return this

        val visibleLines = take(maxLines).toMutableList()
        visibleLines[maxLines - 1] = truncateToWidth(visibleLines.last(), paint, maxWidth)
        return visibleLines
    }

    private fun truncateToWidth(
        text: String,
        paint: Paint,
        maxWidth: Float
    ): String {
        val suffix = "..."
        if (paint.measureText(text + suffix) <= maxWidth) return text + suffix

        var truncated = text
        while (truncated.isNotEmpty() && paint.measureText(truncated + suffix) > maxWidth) {
            truncated = truncated.dropLast(1).trimEnd()
        }
        return if (truncated.isEmpty()) suffix else truncated + suffix
    }

    private fun wrapText(text: String, paint: Paint, maxWidth: Float): List<String> {
        return text
            .lineSequence()
            .flatMap { paragraph ->
                val words = paragraph.trim().split(Regex("\\s+")).filter { it.isNotBlank() }
                if (words.isEmpty()) {
                    sequenceOf("")
                } else {
                    sequence {
                        var currentLine = ""
                        words.forEach { word ->
                            val candidate = if (currentLine.isEmpty()) word else "$currentLine $word"
                            if (paint.measureText(candidate) <= maxWidth) {
                                currentLine = candidate
                            } else {
                                if (currentLine.isNotEmpty()) {
                                    yield(currentLine)
                                }
                                currentLine = word
                            }
                        }
                        if (currentLine.isNotEmpty()) {
                            yield(currentLine)
                        }
                    }
                }
            }
            .toList()
    }

    private fun textPaint(
        color: Int,
        textSize: Float,
        typeface: Typeface
    ): Paint {
        return Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            this.textSize = textSize
            this.typeface = typeface
            textAlign = Paint.Align.LEFT
        }
    }

    private fun tamilTypeface(): Typeface {
        return ResourcesCompat.getFont(context, R.font.noto_sans_tamil)
            ?: Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
    }

    private fun centeredX(text: String, paint: Paint, zone: RectF): Float {
        val textWidth = paint.measureText(text)
        val zoneWidth = zone.width()
        return zone.left + (zoneWidth - textWidth) / 2f
    }
}
