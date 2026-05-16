package com.vaangainvite.core.image

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.RectF
import android.graphics.Typeface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.vaangainvite.data.model.InvitationDetails
import com.vaangainvite.data.model.InvitationTemplate
import java.io.File
import java.io.FileOutputStream

class InvitationImageGenerator(private val context: Context) {
    private val imageWidth = 1080
    private val imageHeight = 1350

    fun createInvitationBitmap(
        template: InvitationTemplate,
        details: InvitationDetails
    ): Bitmap {
        val bitmap = Bitmap.createBitmap(imageWidth, imageHeight, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        ContextCompat.getDrawable(context, template.drawableResId)?.let { drawable ->
            drawable.setBounds(0, 0, imageWidth, imageHeight)
            drawable.draw(canvas)
        } ?: canvas.drawColor(Color.rgb(255, 244, 230))

        drawReadablePanel(canvas)
        drawInvitationText(canvas, template, details)

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

    private fun drawReadablePanel(canvas: Canvas) {
        val panelPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.WHITE
            alpha = 228
        }
        val borderPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
            color = Color.rgb(247, 201, 72)
            style = Paint.Style.STROKE
            strokeWidth = 8f
        }

        val panel = RectF(150f, 330f, 930f, 1065f)
        canvas.drawRoundRect(panel, 36f, 36f, panelPaint)
        canvas.drawRoundRect(panel, 36f, 36f, borderPaint)
    }

    private fun drawInvitationText(
        canvas: Canvas,
        template: InvitationTemplate,
        details: InvitationDetails
    ) {
        val primary = template.primaryColor
        val titlePaint = textPaint(
            color = primary,
            textSize = 58f,
            typeface = Typeface.create(Typeface.SERIF, Typeface.BOLD)
        )
        val headingPaint = textPaint(
            color = Color.rgb(72, 50, 38),
            textSize = 34f,
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        )
        val bodyPaint = textPaint(
            color = Color.rgb(38, 38, 38),
            textSize = 32f,
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.NORMAL)
        )
        val messagePaint = textPaint(
            color = Color.rgb(72, 50, 38),
            textSize = 30f,
            typeface = Typeface.create(Typeface.SERIF, Typeface.ITALIC)
        )
        val footerPaint = textPaint(
            color = primary,
            textSize = 28f,
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        )

        var y = 395f
        y = drawCenteredLines(canvas, "You are warmly invited", headingPaint, y, 700f, 12f)
        y += 28f
        y = drawCenteredLines(
            canvas = canvas,
            text = details.name.ifBlank { "Name of Honoree" },
            paint = titlePaint,
            startY = y,
            maxWidth = 690f,
            lineSpacing = 14f
        )
        y += 44f
        y = drawDetailLine(canvas, "Date", details.date.ifBlank { "Add date" }, bodyPaint, y)
        y = drawDetailLine(canvas, "Time", details.time.ifBlank { "Add time" }, bodyPaint, y)
        y = drawDetailLine(canvas, "Venue", details.venue.ifBlank { "Add venue" }, bodyPaint, y)
        y += 26f
        drawCenteredLines(
            canvas = canvas,
            text = details.message.ifBlank { "Please join us with family and friends." },
            paint = messagePaint,
            startY = y,
            maxWidth = 660f,
            lineSpacing = 12f
        )
        drawCenteredLines(canvas, "Created with Vaanga Invite", footerPaint, 1008f, 660f, 8f)
    }

    private fun drawDetailLine(
        canvas: Canvas,
        label: String,
        value: String,
        paint: Paint,
        startY: Float
    ): Float {
        val labelPaint = Paint(paint).apply {
            typeface = Typeface.create(Typeface.SANS_SERIF, Typeface.BOLD)
        }
        val labelText = "$label: "
        val combinedText = "$labelText$value"
        val lines = wrapText(combinedText, paint, 650f)
        var y = startY
        lines.forEachIndexed { index, line ->
            if (index == 0 && line.startsWith(labelText)) {
                val x = centeredX(line, paint)
                canvas.drawText(labelText, x, y, labelPaint)
                canvas.drawText(value.take(line.length - labelText.length), x + labelPaint.measureText(labelText), y, paint)
            } else {
                canvas.drawText(line, centeredX(line, paint), y, paint)
            }
            y += paint.fontSpacing + 8f
        }
        return y + 10f
    }

    private fun drawCenteredLines(
        canvas: Canvas,
        text: String,
        paint: Paint,
        startY: Float,
        maxWidth: Float,
        lineSpacing: Float
    ): Float {
        var y = startY
        wrapText(text, paint, maxWidth).forEach { line ->
            canvas.drawText(line, centeredX(line, paint), y, paint)
            y += paint.fontSpacing + lineSpacing
        }
        return y
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

    private fun centeredX(text: String, paint: Paint): Float {
        val bounds = Rect()
        paint.getTextBounds(text, 0, text.length, bounds)
        return (imageWidth - paint.measureText(text)) / 2f
    }
}
