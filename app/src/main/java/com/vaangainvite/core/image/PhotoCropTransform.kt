package com.vaangainvite.core.image

import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.RectF

/**
 * Crop region in source-bitmap pixel coordinates for the invitation photo frame.
 */
data class PhotoCropTransform(
    val srcLeft: Float,
    val srcTop: Float,
    val srcRight: Float,
    val srcBottom: Float
) {
    val width: Float get() = srcRight - srcLeft
    val height: Float get() = srcBottom - srcTop

    fun toSourceRect(bitmapWidth: Int, bitmapHeight: Int): Rect {
        val left = srcLeft.toInt().coerceIn(0, bitmapWidth - 1)
        val top = srcTop.toInt().coerceIn(0, bitmapHeight - 1)
        val right = srcRight.toInt().coerceIn(left + 1, bitmapWidth)
        val bottom = srcBottom.toInt().coerceIn(top + 1, bitmapHeight)
        return Rect(left, top, right, bottom)
    }

    fun pan(deltaX: Float, deltaY: Float, bitmapWidth: Int, bitmapHeight: Int): PhotoCropTransform {
        var left = srcLeft + deltaX
        var top = srcTop + deltaY
        var right = srcRight + deltaX
        var bottom = srcBottom + deltaY

        if (left < 0f) {
            right -= left
            left = 0f
        }
        if (top < 0f) {
            bottom -= top
            top = 0f
        }
        if (right > bitmapWidth) {
            val overflow = right - bitmapWidth
            left -= overflow
            right = bitmapWidth.toFloat()
        }
        if (bottom > bitmapHeight) {
            val overflow = bottom - bitmapHeight
            top -= overflow
            bottom = bitmapHeight.toFloat()
        }

        left = left.coerceAtLeast(0f)
        top = top.coerceAtLeast(0f)
        return copy(srcLeft = left, srcTop = top, srcRight = right, srcBottom = bottom)
    }

    fun zoom(scaleFactor: Float, bitmapWidth: Int, bitmapHeight: Int, targetAspect: Float): PhotoCropTransform {
        if (scaleFactor == 1f) return this

        val centerX = (srcLeft + srcRight) / 2f
        val centerY = (srcTop + srcBottom) / 2f
        var newWidth = (width / scaleFactor)
            .coerceIn(minCropWidth(bitmapWidth, targetAspect), bitmapWidth.toFloat())
        var newHeight = newWidth / targetAspect
        if (newHeight > bitmapHeight) {
            newHeight = bitmapHeight.toFloat()
            newWidth = newHeight * targetAspect
        }

        var left = centerX - newWidth / 2f
        var top = centerY - newHeight / 2f
        var right = left + newWidth
        var bottom = top + newHeight

        if (left < 0f) {
            right -= left
            left = 0f
        }
        if (top < 0f) {
            bottom -= top
            top = 0f
        }
        if (right > bitmapWidth) {
            left -= right - bitmapWidth
            right = bitmapWidth.toFloat()
        }
        if (bottom > bitmapHeight) {
            top -= bottom - bitmapHeight
            bottom = bitmapHeight.toFloat()
        }

        left = left.coerceAtLeast(0f)
        top = top.coerceAtLeast(0f)
        right = right.coerceAtMost(bitmapWidth.toFloat())
        bottom = bottom.coerceAtMost(bitmapHeight.toFloat())
        return copy(srcLeft = left, srcTop = top, srcRight = right, srcBottom = bottom)
    }

    companion object {
        fun targetAspectRatio(templateId: String = ""): Float {
            val frame = InvitationLayout.photoFrame(templateId)
            return frame.width() / frame.height()
        }

        fun autoDetect(bitmap: Bitmap, templateId: String = ""): PhotoCropTransform {
            val frame = InvitationLayout.photoFrame(templateId)
            val src = PhotoCropHelper.cropSource(bitmap, frame)
            return PhotoCropTransform(
                srcLeft = src.left.toFloat(),
                srcTop = src.top.toFloat(),
                srcRight = src.right.toFloat(),
                srcBottom = src.bottom.toFloat()
            )
        }

        private fun minCropWidth(bitmapWidth: Int, targetAspect: Float): Float {
            return (bitmapWidth * 0.12f).coerceAtLeast(80f)
                .coerceAtMost(bitmapWidth / targetAspect)
        }
    }
}
