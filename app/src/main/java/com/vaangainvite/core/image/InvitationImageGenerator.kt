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
import com.vaangainvite.data.model.InvitationLanguage
import com.vaangainvite.data.model.InvitationTemplate
import java.io.File
import java.io.FileOutputStream

class InvitationImageGenerator(private val context: Context) {
    private val imageWidth = 1080
    private val imageHeight = 1350

    fun createInvitationBitmap(
        template: InvitationTemplate,
        details: InvitationDetails,
        language: InvitationLanguage,
        uploadedPhotoUri: Uri?
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        drawTemplateBackground(canvas, template, imageWidth, imageHeight)

        val usesPhotoBackground = template.usesPhotoBackground()
        val hasUploadedPhoto = if (usesPhotoBackground) {
            drawUploadedPhoto(
                canvas = canvas,
                uploadedPhotoUri = uploadedPhotoUri,
                frame = InvitationLayout.photoFrame()
            )
        } else {
            val classicZone = InvitationLayout.classicTextZone()
            drawFrostedCard(canvas, classicZone)
            drawUploadedPhoto(
                canvas = canvas,
                uploadedPhotoUri = uploadedPhotoUri,
                frame = RectF(370f, classicZone.top + 20f, 710f, classicZone.top + 250f)
            )
        }

        val textZone = if (usesPhotoBackground) {
            InvitationLayout.photoTextZone(hasUploadedPhoto)
        } else {
            InvitationLayout.classicTextZone()
        }

        drawInvitationText(
            canvas = canvas,
            template = template,
            details = details,
            language = language,
            zone = textZone,
            hasUploadedPhoto = hasUploadedPhoto,
            usesPhotoBackground = usesPhotoBackground
        )

        return bitmap
    }

    fun createTemplatePreviewBitmap(template: InvitationTemplate): Bitmap {
        val width = 324
        val height = 405
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)
        drawTemplateBackground(canvas, template, width, height)
        return bitmap
    }

    private fun drawTemplateBackground(
        canvas: Canvas,
        template: InvitationTemplate,
        width: Int,
        height: Int
    ) {
        val backgroundRes = template.backgroundResId
        if (backgroundRes != null) {
            val options = BitmapFactory.Options().apply {
                inScaled = false
            }
            val source = BitmapFactory.decodeResource(context.resources, backgroundRes, options)
            if (source != null) {
                val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
                canvas.drawBitmap(source, null, Rect(0, 0, width, height), paint)
                return
            }
        }
        InvitationBackgroundPainter.draw(canvas, template.id, width, height)
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

    private fun drawFrostedCard(
        canvas: Canvas,
        bounds: RectF,
        cornerRadius: Float = 28f,
        shadow: Boolean = true
    ) {
        if (shadow) {
            val shadowPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
                color = Color.argb(45, 0, 0, 0)
            }
            canvas.drawRoundRect(
                RectF(bounds.left + 6f, bounds.top + 8f, bounds.right + 6f, bounds.bottom + 8f),
                cornerRadius,
                cornerRadius,
                shadowPaint
            )
        }
        val panelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(230, 255, 255, 255)
        }
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.argb(200, 247, 201, 72)
            style = Paint.Style.STROKE
            strokeWidth = 4f
        }
        canvas.drawRoundRect(bounds, cornerRadius, cornerRadius, panelPaint)
        canvas.drawRoundRect(bounds, cornerRadius, cornerRadius, borderPaint)
    }

    private fun drawInvitationText(
        canvas: Canvas,
        template: InvitationTemplate,
        details: InvitationDetails,
        language: InvitationLanguage,
        zone: RectF,
        hasUploadedPhoto: Boolean,
        usesPhotoBackground: Boolean
    ) {
        val primary = template.primaryColor
        val tamilTypeface = tamilTypeface()
        val script = when (language) {
            InvitationLanguage.TAMIL -> tamilTypeface
            InvitationLanguage.ENGLISH -> scriptTypeface()
        }
        val serif = when (language) {
            InvitationLanguage.TAMIL -> Typeface.create(tamilTypeface, Typeface.BOLD)
            InvitationLanguage.ENGLISH -> serifTypeface()
        }

        val shadowed = usesPhotoBackground
        val introPaint = textPaint(Color.parseColor("#4E342E"), 32f, serif, shadowed)
        val occasionPaint = textPaint(primary, 46f, serif, shadowed)
        val namePaint = textPaint(
            primary,
            if (hasUploadedPhoto) 64f else 76f,
            script,
            shadowed
        )
        val bodyPaint = textPaint(
            Color.parseColor("#3E2723"),
            28f,
            when (language) {
                InvitationLanguage.TAMIL -> tamilTypeface
                InvitationLanguage.ENGLISH -> serifTypeface()
            },
            shadowed
        )
        val messagePaint = textPaint(
            Color.parseColor("#4E342E"),
            26f,
            when (language) {
                InvitationLanguage.TAMIL -> tamilTypeface
                InvitationLanguage.ENGLISH -> serifTypeface()
            },
            shadowed
        )

        var y = zone.top + 8f
        val maxTextWidth = zone.width() - 40f

        y = drawCenteredLines(canvas, language.inviteIntro, introPaint, y, maxTextWidth, 6f, maxLines = 1)
        y += 10f
        y = drawCenteredLines(
            canvas = canvas,
            text = details.occasionTitle.ifBlank { defaultOccasionTitle(language, template) },
            paint = occasionPaint,
            startY = y,
            maxWidth = maxTextWidth,
            lineSpacing = 6f,
            maxLines = 2
        )
        y += 8f
        y = drawCenteredLines(
            canvas = canvas,
            text = details.name.ifBlank { language.fallbackName },
            paint = namePaint,
            startY = y,
            maxWidth = maxTextWidth,
            lineSpacing = 4f,
            maxLines = 2
        )
        y += 20f

        y += 12f
        y = drawDetailWithIcon(
            canvas = canvas,
            iconResId = R.drawable.ic_invite_calendar,
            value = details.date.ifBlank { language.fallbackDate },
            paint = bodyPaint,
            startY = y,
            zone = zone,
            maxLines = 1
        )
        y = drawDetailWithIcon(
            canvas = canvas,
            iconResId = R.drawable.ic_invite_clock,
            value = details.time.ifBlank { language.fallbackTime },
            paint = bodyPaint,
            startY = y,
            zone = zone,
            maxLines = 1
        )
        y = drawDetailWithIcon(
            canvas = canvas,
            iconResId = R.drawable.ic_invite_location,
            value = details.venue.ifBlank { language.fallbackVenue },
            paint = bodyPaint,
            startY = y,
            zone = zone,
            maxLines = InvitationDetails.VENUE_MAX_LINES
        )
        if (details.mobileNumber.isNotBlank()) {
            y = drawDetailWithIcon(
                canvas = canvas,
                iconResId = R.drawable.ic_invite_phone,
                value = details.mobileNumber,
                paint = bodyPaint,
                startY = y,
                zone = zone,
                maxLines = 1
            )
        }

        val messageTop = (y + 20f).coerceIn(zone.top + 280f, zone.bottom - 100f)
        val messageMaxLines = ((zone.bottom - messageTop) / (messagePaint.fontSpacing + 8f))
            .toInt()
            .coerceIn(1, 3)
        drawCenteredLines(
            canvas = canvas,
            text = details.message.ifBlank { language.fallbackMessage },
            paint = messagePaint,
            startY = messageTop,
            maxWidth = maxTextWidth,
            lineSpacing = 8f,
            maxLines = messageMaxLines
        )
    }

    private fun drawDetailWithIcon(
        canvas: Canvas,
        iconResId: Int,
        value: String,
        paint: Paint,
        startY: Float,
        zone: RectF,
        maxLines: Int
    ): Float {
        val iconSize = 36f
        val gap = 14f
        val maxLineWidth = zone.width() - iconSize - gap - 24f
        val lines = wrapText(value, paint, maxLineWidth).limitLines(maxLines, paint, maxLineWidth)
        val icon = requireNotNull(ContextCompat.getDrawable(context, iconResId)) {
            "Missing detail icon"
        }
        var y = startY + paint.textSize

        lines.forEachIndexed { index, line ->
            if (index == 0) {
                val lineWidth = paint.measureText(line)
                val rowWidth = iconSize + gap + lineWidth
                val rowLeft = zone.centerX() - rowWidth / 2f
                val iconTop = (y - iconSize + 6f).toInt()
                icon.setBounds(
                    rowLeft.toInt(),
                    iconTop,
                    (rowLeft + iconSize).toInt(),
                    iconTop + iconSize.toInt()
                )
                icon.draw(canvas)
                canvas.drawText(line, rowLeft + iconSize + gap, y, paint)
            } else {
                canvas.drawText(line, centeredX(line, paint), y, paint)
            }
            y += paint.fontSpacing + 10f
        }
        return y + 8f
    }

    private fun defaultOccasionTitle(language: InvitationLanguage, template: InvitationTemplate): String {
        return when (language) {
            InvitationLanguage.ENGLISH -> when {
                template.id.startsWith("wedding") -> "Wedding Celebration"
                template.id.startsWith("housewarming") -> "Housewarming Ceremony"
                template.id.startsWith("puberty") -> "Puberty Ceremony"
                else -> "Birthday Celebration"
            }
            InvitationLanguage.TAMIL -> when {
                template.id.startsWith("wedding") -> "திருமண விழா"
                template.id.startsWith("housewarming") -> "கிருஹப்பிரவேசம்"
                template.id.startsWith("puberty") -> "பருவ விழா"
                else -> "பிறந்தநாள் விழா"
            }
        }
    }

    private fun drawUploadedPhoto(
        canvas: Canvas,
        uploadedPhotoUri: Uri?,
        frame: RectF
    ): Boolean {
        if (uploadedPhotoUri == null) return false

        val photoBitmap = runCatching {
            context.contentResolver.openInputStream(uploadedPhotoUri)?.use { input ->
                BitmapFactory.decodeStream(input)
            }
        }.getOrNull()?.let { bitmap ->
            correctBitmapOrientation(bitmap, uploadedPhotoUri)
        } ?: return false
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

    private fun drawCenteredLines(
        canvas: Canvas,
        text: String,
        paint: Paint,
        startY: Float,
        maxWidth: Float,
        lineSpacing: Float,
        maxLines: Int = Int.MAX_VALUE
    ): Float {
        var y = startY + paint.textSize
        wrapText(text, paint, maxWidth)
            .limitLines(maxLines, paint, maxWidth)
            .forEach { line ->
                canvas.drawText(line, centeredX(line, paint), y, paint)
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
        typeface: Typeface,
        shadowed: Boolean = false
    ): Paint {
        return Paint(Paint.ANTI_ALIAS_FLAG).apply {
            this.color = color
            this.textSize = textSize
            this.typeface = typeface
            textAlign = Paint.Align.LEFT
            if (shadowed) {
                setShadowLayer(8f, 0f, 2f, Color.argb(120, 0, 0, 0))
            }
        }
    }

    private fun scriptTypeface(): Typeface {
        return ResourcesCompat.getFont(context, R.font.great_vibes)
            ?: Typeface.create(Typeface.SANS_SERIF, Typeface.ITALIC)
    }

    private fun serifTypeface(): Typeface {
        return ResourcesCompat.getFont(context, R.font.playfair_display_bold)
            ?: Typeface.create(Typeface.SERIF, Typeface.BOLD)
    }

    private fun tamilTypeface(): Typeface {
        return ResourcesCompat.getFont(context, R.font.noto_sans_tamil)
            ?: Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
    }

    private fun centeredX(text: String, paint: Paint): Float {
        return (imageWidth - paint.measureText(text)) / 2f
    }
}
