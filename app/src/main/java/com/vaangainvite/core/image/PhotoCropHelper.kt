package com.vaangainvite.core.image

import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.RectF
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.face.FaceDetection
import com.google.mlkit.vision.face.FaceDetector
import com.google.mlkit.vision.face.FaceDetectorOptions

/**
 * Picks a scale-to-fill source [Rect] for drawing an uploaded photo into [targetFrame].
 * Uses on-device face detection when a face is found; otherwise top-aligned crop.
 */
internal object PhotoCropHelper {

    private val faceDetector: FaceDetector by lazy {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .setMinFaceSize(0.12f)
            .build()
        FaceDetection.getClient(options)
    }

    fun cropSource(bitmap: Bitmap, targetFrame: RectF): Rect {
        val faceBounds = detectPrimaryFaceBounds(bitmap)
        return if (faceBounds != null) {
            faceAwareCropSource(bitmap, targetFrame, faceBounds)
        } else {
            topAlignedCropSource(bitmap, targetFrame)
        }
    }

    private fun detectPrimaryFaceBounds(bitmap: Bitmap): RectF? {
        val maxSide = 640
        val largestSide = maxOf(bitmap.width, bitmap.height)
        val scale = if (largestSide > maxSide) maxSide.toFloat() / largestSide else 1f
        val detectWidth = (bitmap.width * scale).toInt().coerceAtLeast(1)
        val detectHeight = (bitmap.height * scale).toInt().coerceAtLeast(1)
        val detectBitmap = if (scale < 1f) {
            Bitmap.createScaledBitmap(bitmap, detectWidth, detectHeight, true)
        } else {
            bitmap
        }

        return try {
            val faces = Tasks.await(
                faceDetector.process(InputImage.fromBitmap(detectBitmap, 0))
            )
            if (faces.isEmpty()) return null

            val scaleX = bitmap.width.toFloat() / detectBitmap.width
            val scaleY = bitmap.height.toFloat() / detectBitmap.height
            val primary = faces.maxBy { it.boundingBox.width() * it.boundingBox.height() }
            val box = primary.boundingBox
            RectF(
                box.left * scaleX,
                box.top * scaleY,
                box.right * scaleX,
                box.bottom * scaleY
            )
        } catch (_: Exception) {
            null
        } finally {
            if (detectBitmap !== bitmap) {
                detectBitmap.recycle()
            }
        }
    }

    private fun faceAwareCropSource(bitmap: Bitmap, target: RectF, face: RectF): Rect {
        val targetAspect = target.width() / target.height()

        val padX = face.width() * 0.4f
        val padTop = face.height() * 0.55f
        val padBottom = face.height() * 0.35f
        val focus = RectF(
            (face.left - padX).coerceAtLeast(0f),
            (face.top - padTop).coerceAtLeast(0f),
            (face.right + padX).coerceAtMost(bitmap.width.toFloat()),
            (face.bottom + padBottom).coerceAtMost(bitmap.height.toFloat())
        )

        var cropW: Float
        var cropH: Float
        if (focus.width() / focus.height() > targetAspect) {
            cropW = focus.width()
            cropH = cropW / targetAspect
        } else {
            cropH = focus.height()
            cropW = cropH * targetAspect
        }

        if (cropW > bitmap.width) {
            cropW = bitmap.width.toFloat()
            cropH = cropW / targetAspect
        }
        if (cropH > bitmap.height) {
            cropH = bitmap.height.toFloat()
            cropW = cropH * targetAspect
        }

        // Keep the face in the upper portion of the frame (room for hair / forehead).
        val faceAnchorY = face.top - padTop * 0.35f
        var left = focus.centerX() - cropW / 2f
        var top = faceAnchorY

        left = left.coerceIn(0f, bitmap.width - cropW)
        top = top.coerceIn(0f, bitmap.height - cropH)

        if (focus.left < left) left = focus.left
        if (focus.right > left + cropW) left = focus.right - cropW
        if (focus.top < top) top = focus.top
        if (focus.bottom > top + cropH) top = focus.bottom - cropH
        left = left.coerceIn(0f, bitmap.width - cropW)
        top = top.coerceIn(0f, bitmap.height - cropH)

        return Rect(
            left.toInt(),
            top.toInt(),
            (left + cropW).toInt().coerceAtMost(bitmap.width),
            (top + cropH).toInt().coerceAtMost(bitmap.height)
        )
    }

    /**
     * Scale-to-fill crop: horizontally centered; vertically top-aligned when no face is found.
     */
    private fun topAlignedCropSource(bitmap: Bitmap, target: RectF): Rect {
        val bitmapAspect = bitmap.width.toFloat() / bitmap.height.toFloat()
        val targetAspect = target.width() / target.height()

        return if (bitmapAspect > targetAspect) {
            val sourceWidth = (bitmap.height * targetAspect).toInt().coerceAtMost(bitmap.width)
            val left = ((bitmap.width - sourceWidth) / 2).coerceAtLeast(0)
            Rect(left, 0, left + sourceWidth, bitmap.height)
        } else {
            val sourceHeight = (bitmap.width / targetAspect).toInt().coerceAtMost(bitmap.height)
            Rect(0, 0, bitmap.width, sourceHeight)
        }
    }
}
