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
import androidx.core.graphics.drawable.DrawableCompat
import com.vaangainvite.R
import com.vaangainvite.data.model.InvitationDetails
import com.vaangainvite.data.model.clampedForCard
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
    ): InvitationBitmapResult {
        val bitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        drawTemplateBackground(canvas, template, imageWidth, imageHeight)

        val usesPhotoBackground = template.usesPhotoBackground()
        val expectsPhoto = uploadedPhotoUri != null
        val hasUploadedPhoto = if (usesPhotoBackground) {
            drawUploadedPhoto(
                canvas = canvas,
                uploadedPhotoUri = uploadedPhotoUri,
                frame = InvitationLayout.photoFrame(template.id)
            )
        } else {
            val classicZone = InvitationLayout.classicTextZone(expectsPhoto)
            drawFrostedCard(canvas, classicZone)
            drawUploadedPhoto(
                canvas = canvas,
                uploadedPhotoUri = uploadedPhotoUri,
                frame = RectF(370f, classicZone.top + 16f, 710f, classicZone.top + 236f)
            )
        }

        val textZone = if (usesPhotoBackground) {
            InvitationLayout.photoTextZone(template.id, hasUploadedPhoto)
        } else {
            InvitationLayout.classicTextZone(hasUploadedPhoto)
        }

        val renderReport = drawInvitationText(
            canvas = canvas,
            template = template,
            details = details.clampedForCard(hasUploadedPhoto),
            language = language,
            zone = textZone,
            hasUploadedPhoto = hasUploadedPhoto,
            usesPhotoBackground = usesPhotoBackground,
            textStartY = InvitationLayout.textStartY(template.id, hasUploadedPhoto)
        )

        return InvitationBitmapResult(bitmap = bitmap, renderReport = renderReport)
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
        usesPhotoBackground: Boolean,
        textStartY: Float
    ): InvitationRenderReport {
        val palette = invitationTextPalette(template, language, usesPhotoBackground)
        val tamilTypeface = tamilTypeface()
        val script = when (language) {
            InvitationLanguage.TAMIL -> tamilTypeface
            InvitationLanguage.ENGLISH -> scriptTypeface()
        }
        val serif = when (language) {
            InvitationLanguage.TAMIL -> Typeface.create(tamilTypeface, Typeface.BOLD)
            InvitationLanguage.ENGLISH -> serifTypeface()
        }

        val userMessage = details.message.trim()
        val hasUserMessage = userMessage.isNotBlank()
        val compactLayout = hasUploadedPhoto && hasUserMessage

        val introSize = if (template.id == "wedding_02" && hasUploadedPhoto) 28f else 32f
        val occasionSize = if (template.id == "wedding_02" && hasUploadedPhoto) 40f else 46f

        val introPaint = textPaint(palette.introColor, introSize, serif, palette.strongShadow)
        val occasionPaint = textPaint(palette.primaryColor, occasionSize, serif, palette.strongShadow)
        val namePaint = textPaint(
            palette.primaryColor,
            when {
                compactLayout && usesPhotoBackground -> 52f
                hasUploadedPhoto && usesPhotoBackground -> 54f
                template.id == "wedding_02" && usesPhotoBackground -> 62f
                usesPhotoBackground -> 70f
                hasUploadedPhoto -> 60f
                else -> 72f
            },
            script,
            palette.strongShadow
        )
        val bodyPaint = textPaint(
            palette.bodyColor,
            if (compactLayout) 24f else 28f,
            when (language) {
                InvitationLanguage.TAMIL -> tamilTypeface
                InvitationLanguage.ENGLISH -> serifTypeface()
            },
            palette.strongShadow
        )
        val messagePaint = textPaint(
            palette.messageColor,
            if (usesPhotoBackground) 24f else 26f,
            when (language) {
                InvitationLanguage.TAMIL -> tamilTypeface
                InvitationLanguage.ENGLISH -> serifTypeface()
            },
            palette.strongShadow
        )

        var blockTop = textStartY
        val maxTextWidth = zone.width() - 40f

        blockTop = drawCenteredLines(
            canvas,
            language.inviteIntro,
            introPaint,
            blockTop,
            maxTextWidth,
            4f,
            maxLines = 1,
            horizontalBounds = zone
        ) + InvitationLayout.Spacing.afterIntro

        blockTop = drawCenteredLines(
            canvas = canvas,
            text = details.occasionTitle.ifBlank { defaultOccasionTitle(language, template) },
            paint = occasionPaint,
            topY = blockTop,
            maxWidth = maxTextWidth,
            lineSpacing = 4f,
            maxLines = 2,
            horizontalBounds = zone
        ) + InvitationLayout.Spacing.afterOccasion

        blockTop = drawCenteredLines(
            canvas = canvas,
            text = details.name.ifBlank { language.fallbackName },
            paint = namePaint,
            topY = blockTop,
            maxWidth = maxTextWidth,
            lineSpacing = 2f,
            maxLines = 2,
            horizontalBounds = zone
        ) + if (compactLayout) 6f else InvitationLayout.Spacing.afterName

        val detailEntries = buildList {
            add(details.date.ifBlank { language.fallbackDate } to 1)
            add(details.time.ifBlank { language.fallbackTime } to 1)
            add(details.venue.ifBlank { language.fallbackVenue } to InvitationDetails.VENUE_MAX_LINES)
            if (details.mobileNumber.isNotBlank()) {
                add(details.mobileNumber to 1)
            }
        }
        val detailLayout = computeDetailColumnLayout(bodyPaint, zone, detailEntries)

        blockTop = drawDetailWithIcon(
            canvas = canvas,
            iconResId = R.drawable.ic_invite_calendar,
            value = details.date.ifBlank { language.fallbackDate },
            paint = bodyPaint,
            topY = blockTop,
            layout = detailLayout,
            iconTint = palette.iconTint,
            maxLines = 1
        )
        blockTop = drawDetailWithIcon(
            canvas = canvas,
            iconResId = R.drawable.ic_invite_clock,
            value = details.time.ifBlank { language.fallbackTime },
            paint = bodyPaint,
            topY = blockTop,
            layout = detailLayout,
            iconTint = palette.iconTint,
            maxLines = 1
        )
        blockTop = drawDetailWithIcon(
            canvas = canvas,
            iconResId = R.drawable.ic_invite_location,
            value = details.venue.ifBlank { language.fallbackVenue },
            paint = bodyPaint,
            topY = blockTop,
            layout = detailLayout,
            iconTint = palette.iconTint,
            maxLines = InvitationDetails.VENUE_MAX_LINES
        )
        if (details.mobileNumber.isNotBlank()) {
            blockTop = drawDetailWithIcon(
                canvas = canvas,
                iconResId = R.drawable.ic_invite_phone,
                value = details.mobileNumber,
                paint = bodyPaint,
                topY = blockTop,
                layout = detailLayout,
                iconTint = palette.iconTint,
                maxLines = 1
            )
        }

        val messageText = userMessage.ifBlank { language.fallbackMessage }
        if (messageText.isBlank()) {
            return InvitationRenderReport()
        }

        val messageTruncated = drawAdditionalMessage(
            canvas = canvas,
            text = messageText,
            paint = messagePaint,
            blockTopAfterLastDetail = blockTop,
            zone = zone,
            maxTextWidth = maxTextWidth,
            maxLines = InvitationDetails.MESSAGE_MAX_LINES_ON_CARD,
            lineSpacing = 5f
        )
        return InvitationRenderReport(
            messageShown = true,
            messageTruncated = messageTruncated
        )
    }

    /**
     * Places the additional message 10px below the last detail row (phone).
     * Uses flow layout so it never overlaps date/time/venue/phone — bottom-anchoring
     * caused overlap on compact templates like Temple Mandapam with a photo.
     */
    private fun drawAdditionalMessage(
        canvas: Canvas,
        text: String,
        paint: Paint,
        blockTopAfterLastDetail: Float,
        zone: RectF,
        maxTextWidth: Float,
        maxLines: Int,
        lineSpacing: Float
    ): Boolean {
        val messageStartY = blockTopAfterLastDetail -
            InvitationLayout.Spacing.betweenDetails +
            InvitationLayout.Spacing.beforeMessageNoPhoto
        val maxBottomY = zone.bottom - 8f
        val wrapped = wrapText(text, paint, maxTextWidth)
        val truncated = wrapped.size > maxLines
        val lines = wrapped.limitLines(maxLines, paint, maxTextWidth)
        if (lines.isEmpty()) return false

        val fm = paint.fontMetrics
        val messageHeight = lines.size * lineHeight(paint, lineSpacing) - fm.descent
        val topY = if (messageStartY + messageHeight <= maxBottomY) {
            messageStartY
        } else {
            (maxBottomY - messageHeight).coerceAtLeast(messageStartY)
        }

        drawCenteredLines(
            canvas = canvas,
            text = text,
            paint = paint,
            topY = topY,
            maxWidth = maxTextWidth,
            lineSpacing = lineSpacing,
            maxLines = maxLines,
            horizontalBounds = zone,
            maxBottomY = maxBottomY
        )
        return truncated
    }

    private data class InvitationTextPalette(
        val introColor: Int,
        val primaryColor: Int,
        val bodyColor: Int,
        val messageColor: Int,
        val iconTint: Int,
        val strongShadow: Boolean
    )

    private fun invitationTextPalette(
        template: InvitationTemplate,
        language: InvitationLanguage,
        usesPhotoBackground: Boolean
    ): InvitationTextPalette {
        if (template.usesLightText) {
            return InvitationTextPalette(
                introColor = Color.parseColor("#FFF8E7"),
                primaryColor = template.primaryColor,
                bodyColor = Color.parseColor("#FFFFFF"),
                messageColor = Color.parseColor("#FCE4EC"),
                iconTint = Color.parseColor("#FFFFFF"),
                strongShadow = true
            )
        }
        val shadow = usesPhotoBackground
        return InvitationTextPalette(
            introColor = Color.parseColor("#4E342E"),
            primaryColor = template.primaryColor,
            bodyColor = Color.parseColor("#3E2723"),
            messageColor = Color.parseColor("#4E342E"),
            iconTint = Color.parseColor("#5D4037"),
            strongShadow = shadow
        )
    }

    private data class DetailColumnLayout(
        val iconLeft: Float,
        val textStartX: Float,
        val maxLineWidth: Float,
        val iconSize: Float = 36f,
        val iconGap: Float = 14f
    )

    /**
     * Centers detail text under the honoree name and hangs icons to the left,
     * instead of centering the icon+text block (which shifts copy to the right).
     */
    private fun computeDetailColumnLayout(
        paint: Paint,
        zone: RectF,
        entries: List<Pair<String, Int>>
    ): DetailColumnLayout {
        val iconSize = 36f
        val iconGap = 14f
        val iconReserve = iconSize + iconGap + 8f
        val maxLineWidth = zone.width() - 40f - iconReserve
        var widest = 0f
        entries.forEach { (value, maxLines) ->
            wrapText(value, paint, maxLineWidth)
                .limitLines(maxLines, paint, maxLineWidth)
                .forEach { line ->
                    val fitted = fitLineToWidth(line, paint, maxLineWidth)
                    widest = maxOf(widest, paint.measureText(fitted))
                }
        }
        var textStartX = zone.centerX() - widest / 2f
        var iconLeft = textStartX - iconSize - iconGap
        val minIconLeft = zone.left + 8f
        if (iconLeft < minIconLeft) {
            val shift = minIconLeft - iconLeft
            iconLeft += shift
            textStartX += shift
        }
        return DetailColumnLayout(
            iconLeft = iconLeft,
            textStartX = textStartX,
            maxLineWidth = maxLineWidth,
            iconSize = iconSize,
            iconGap = iconGap
        )
    }

    private fun drawDetailWithIcon(
        canvas: Canvas,
        iconResId: Int,
        value: String,
        paint: Paint,
        topY: Float,
        layout: DetailColumnLayout,
        iconTint: Int,
        maxLines: Int
    ): Float {
        val rawLines = wrapText(value, paint, layout.maxLineWidth)
        val lines = rawLines.limitLines(maxLines, paint, layout.maxLineWidth)
        val fittedLines = lines.map { fitLineToWidth(it, paint, layout.maxLineWidth) }

        val icon = requireNotNull(ContextCompat.getDrawable(context, iconResId)?.mutate()) {
            "Missing detail icon"
        }
        DrawableCompat.setTint(icon, iconTint)
        val fm = paint.fontMetrics
        var baseline = topY - fm.ascent

        fittedLines.forEachIndexed { index, line ->
            if (index == 0) {
                val iconTop = (baseline + fm.ascent - 4f).toInt()
                icon.setBounds(
                    layout.iconLeft.toInt(),
                    iconTop,
                    (layout.iconLeft + layout.iconSize).toInt(),
                    iconTop + layout.iconSize.toInt()
                )
                icon.draw(canvas)
            }
            canvas.drawText(line, layout.textStartX, baseline, paint)
            baseline += lineHeight(paint, InvitationLayout.Spacing.betweenDetails)
        }
        return baseline - fm.descent + InvitationLayout.Spacing.betweenDetails
    }

    private fun lineHeight(paint: Paint, extra: Float): Float {
        val fm = paint.fontMetrics
        return fm.descent - fm.ascent + extra
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
            null,
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

    /**
     * Draws wrapped centered lines starting at [topY] (top edge of this text block).
     * Returns the Y coordinate of the bottom edge for stacking the next block.
     */
    private fun drawCenteredLines(
        canvas: Canvas,
        text: String,
        paint: Paint,
        topY: Float,
        maxWidth: Float,
        lineSpacing: Float,
        maxLines: Int = Int.MAX_VALUE,
        horizontalBounds: RectF? = null,
        maxBottomY: Float = Float.MAX_VALUE
    ): Float {
        val fm = paint.fontMetrics
        var baseline = topY - fm.ascent
        wrapText(text, paint, maxWidth)
            .limitLines(maxLines, paint, maxWidth)
            .forEach { line ->
                if (baseline + fm.descent > maxBottomY) return@forEach
                val fittedLine = fitLineToWidth(line, paint, maxWidth)
                val x = centeredX(fittedLine, paint, horizontalBounds)
                canvas.drawText(fittedLine, x, baseline, paint)
                baseline += lineHeight(paint, lineSpacing)
            }
        return baseline - fm.descent
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

    private fun splitLongToken(token: String, paint: Paint, maxWidth: Float): List<String> {
        if (token.isEmpty()) return emptyList()
        if (paint.measureText(token) <= maxWidth) return listOf(token)

        val parts = mutableListOf<String>()
        var remaining = token
        while (remaining.isNotEmpty()) {
            var length = remaining.length
            while (length > 1 && paint.measureText(remaining.substring(0, length)) > maxWidth) {
                length--
            }
            parts.add(remaining.substring(0, length))
            remaining = remaining.substring(length)
        }
        return parts
    }

    private fun fitLineToWidth(text: String, paint: Paint, maxWidth: Float): String {
        if (paint.measureText(text) <= maxWidth) return text
        var fitted = text
        while (fitted.isNotEmpty() && paint.measureText(fitted) > maxWidth) {
            fitted = fitted.dropLast(1)
        }
        return fitted
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
                            val wordParts = splitLongToken(word, paint, maxWidth)
                            wordParts.forEach { part ->
                                val candidate = if (currentLine.isEmpty()) part else "$currentLine $part"
                                if (paint.measureText(candidate) <= maxWidth) {
                                    currentLine = candidate
                                } else {
                                    if (currentLine.isNotEmpty()) {
                                        yield(currentLine)
                                    }
                                    currentLine = part
                                }
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
                setShadowLayer(10f, 0f, 2f, Color.argb(160, 0, 0, 0))
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

    private fun centeredX(text: String, paint: Paint, bounds: RectF? = null): Float {
        val lineWidth = paint.measureText(text)
        val centerX = bounds?.centerX() ?: (imageWidth / 2f)
        return centerX - lineWidth / 2f
    }
}
