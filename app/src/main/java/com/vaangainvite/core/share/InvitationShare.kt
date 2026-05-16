package com.vaangainvite.core.share

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

object InvitationShare {
    fun shareToWhatsApp(
        context: Context,
        imageUri: Uri,
        message: String
    ): Boolean {
        val whatsappIntent = baseShareIntent(imageUri, message).apply {
            setPackage("com.whatsapp")
        }

        return try {
            context.startActivity(whatsappIntent)
            true
        } catch (exception: ActivityNotFoundException) {
            val chooser = Intent.createChooser(
                baseShareIntent(imageUri, message),
                "Share invitation"
            )
            context.startActivity(chooser)
            false
        }
    }

    private fun baseShareIntent(
        imageUri: Uri,
        message: String
    ): Intent {
        return Intent(Intent.ACTION_SEND).apply {
            type = "image/png"
            putExtra(Intent.EXTRA_STREAM, imageUri)
            putExtra(Intent.EXTRA_TEXT, message)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }
}
