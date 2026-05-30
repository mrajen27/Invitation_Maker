package com.vaangainvite.core.image

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream

object PhotoCropExporter {
    fun exportCroppedUriFromBitmap(context: Context, source: Bitmap, transform: PhotoCropTransform): Uri {
        val cropped = exportCroppedBitmap(source, transform)
        return saveToCache(context, cropped)
    }

    fun exportCroppedUri(
        context: Context,
        sourceUri: Uri,
        transform: PhotoCropTransform
    ): Uri {
        val source = PhotoBitmapLoader.load(context, sourceUri)
        val cropped = exportCroppedBitmap(source, transform)
        return saveToCache(context, cropped)
    }

    fun exportCroppedBitmap(source: Bitmap, transform: PhotoCropTransform): Bitmap {
        val src = transform.toSourceRect(source.width, source.height)
        return Bitmap.createBitmap(
            source,
            src.left,
            src.top,
            src.width(),
            src.height()
        )
    }

    private fun saveToCache(context: Context, bitmap: Bitmap): Uri {
        val directory = File(context.cacheDir, "cropped_photos").apply { mkdirs() }
        val file = File(directory, "invite_photo_${System.currentTimeMillis()}.jpg")
        FileOutputStream(file).use { output ->
            bitmap.compress(Bitmap.CompressFormat.JPEG, 92, output)
        }
        return FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            file
        )
    }
}
