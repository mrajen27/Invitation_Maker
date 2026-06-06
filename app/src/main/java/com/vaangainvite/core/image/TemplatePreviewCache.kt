package com.vaangainvite.core.image

import android.content.Context
import android.graphics.BitmapFactory
import android.util.LruCache
import androidx.annotation.DrawableRes
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Subsampled decode cache for template WebP previews in list screens.
 */
object TemplatePreviewCache {
    private const val CACHE_ENTRIES = 40
    const val LIST_THUMBNAIL_MAX_PX = 240

    private val cache = LruCache<CacheKey, ImageBitmap>(CACHE_ENTRIES)

    fun peek(@DrawableRes resId: Int, maxDimension: Int = LIST_THUMBNAIL_MAX_PX): ImageBitmap? =
        cache.get(CacheKey(resId, maxDimension))

    suspend fun load(
        context: Context,
        @DrawableRes resId: Int,
        maxDimension: Int = LIST_THUMBNAIL_MAX_PX
    ): ImageBitmap? {
        val key = CacheKey(resId, maxDimension)
        cache.get(key)?.let { return it }
        return withContext(Dispatchers.IO) {
            decode(context, resId, maxDimension)?.also { cache.put(key, it) }
        }
    }

    suspend fun preload(context: Context, @DrawableRes resIds: Collection<Int>) {
        withContext(Dispatchers.IO) {
            resIds.forEach { resId ->
                if (peek(resId) == null) {
                    decode(context, resId, LIST_THUMBNAIL_MAX_PX)?.let {
                        cache.put(CacheKey(resId, LIST_THUMBNAIL_MAX_PX), it)
                    }
                }
            }
        }
    }

    private fun decode(context: Context, @DrawableRes resId: Int, maxDimension: Int): ImageBitmap? {
        val bounds = BitmapFactory.Options().apply { inJustDecodeBounds = true }
        BitmapFactory.decodeResource(context.resources, resId, bounds)
        val sampleSize = calculateInSampleSize(bounds.outWidth, bounds.outHeight, maxDimension)
        val decodeOptions = BitmapFactory.Options().apply {
            inSampleSize = sampleSize
            inPreferredConfig = android.graphics.Bitmap.Config.RGB_565
        }
        return BitmapFactory.decodeResource(context.resources, resId, decodeOptions)?.asImageBitmap()
    }

    private fun calculateInSampleSize(width: Int, height: Int, maxDimension: Int): Int {
        if (width <= 0 || height <= 0) return 1
        var sampleSize = 1
        var halfWidth = width / 2
        var halfHeight = height / 2
        while (halfWidth / sampleSize >= maxDimension || halfHeight / sampleSize >= maxDimension) {
            sampleSize *= 2
        }
        return sampleSize
    }

    private data class CacheKey(
        @DrawableRes val resId: Int,
        val maxDimension: Int
    )
}
