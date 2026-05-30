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
 * Uses on-device face detection when faces are found; otherwise top-aligned crop.
 */
internal object PhotoCropHelper {

    private val faceDetector: FaceDetector by lazy {
        val options = FaceDetectorOptions.Builder()
            .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
            .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
            .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_NONE)
            .setMinFaceSize(0.08f)
            .build()
        FaceDetection.getClient(options)
    }

    fun cropSource(bitmap: Bitmap, targetFrame: RectF): Rect {
        val faceBounds = detectFaceFocusBounds(bitmap)
        return if (faceBounds != null) {
            faceAwareCropSource(bitmap, targetFrame, faceBounds)
        } else {
            topAlignedCropSource(bitmap, targetFrame)
        }
    }

    /**
     * Returns a padded focus region around one face, or the union of all detected faces
     * for group photos so people on the sides are not cut off.
     */
    private fun detectFaceFocusBounds(bitmap: Bitmap): RectF? {
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
            val faceBoxes = faces.map { face ->
                val box = face.boundingBox
                RectF(
                    box.left * scaleX,
                    box.top * scaleY,
                    box.right * scaleX,
                    box.bottom * scaleY
                )
            }

            val focus = if (faceBoxes.size == 1) {
                faceBoxes.first()
            } else {
                faceBoxes.drop(1).fold(faceBoxes.first()) { union, box ->
                    RectF(union).apply { union(box) }
                }
            }

            paddedFaceFocus(focus, bitmap.width, bitmap.height, isGroupPhoto = faceBoxes.size > 1)
        } catch (_: Exception) {
            null
        } finally {
            if (detectBitmap !== bitmap) {
                detectBitmap.recycle()
            }
        }
    }

    private fun paddedFaceFocus(
        face: RectF,
        bitmapWidth: Int,
        bitmapHeight: Int,
        isGroupPhoto: Boolean
    ): RectF {
        val padX = face.width() * if (isGroupPhoto) 0.35f else 0.4f
        val padTop = face.height() * if (isGroupPhoto) 0.45f else 0.55f
        val padBottom = face.height() * if (isGroupPhoto) 0.3f else 0.35f
        return RectF(
            (face.left - padX).coerceAtLeast(0f),
            (face.top - padTop).coerceAtLeast(0f),
            (face.right + padX).coerceAtMost(bitmapWidth.toFloat()),
            (face.bottom + padBottom).coerceAtMost(bitmapHeight.toFloat())
        )
    }

    private fun faceAwareCropSource(bitmap: Bitmap, target: RectF, face: RectF): Rect {
        val targetAspect = target.width() / target.height()
        val focus = RectF(
            face.left.coerceAtLeast(0f),
            face.top.coerceAtLeast(0f),
            face.right.coerceAtMost(bitmap.width.toFloat()),
            face.bottom.coerceAtMost(bitmap.height.toFloat())
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

        var left = focus.centerX() - cropW / 2f
        var top = focus.centerY() - cropH * 0.42f

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
