package com.vaangainvite.core.share

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri

object InvitationShare {
    enum class ShareResult {
        OPENED_WHATSAPP,
        OPENED_SHARE_SHEET
    }

    fun shareToWhatsAppChat(
        context: Context,
        imageUri: Uri,
        message: String
    ): ShareResult {
        val whatsappIntent = baseShareIntent(imageUri, message).apply {
            setPackage("com.whatsapp")
        }

        return try {
            context.startActivity(whatsappIntent)
            ShareResult.OPENED_WHATSAPP
        } catch (exception: ActivityNotFoundException) {
            openShareSheet(context, imageUri, message)
            ShareResult.OPENED_SHARE_SHEET
        }
    }

    fun shareToWhatsAppStatus(
        context: Context,
        imageUri: Uri,
        message: String
    ): ShareResult {
        val statusIntent = baseShareIntent(imageUri, message).apply {
            setPackage("com.whatsapp")
            putExtra("jid", "status@broadcast")
        }

        return try {
            context.startActivity(statusIntent)
            ShareResult.OPENED_WHATSAPP
        } catch (exception: ActivityNotFoundException) {
            openShareSheet(context, imageUri, message)
            ShareResult.OPENED_SHARE_SHEET
        }
    }

    private fun openShareSheet(
        context: Context,
        imageUri: Uri,
        message: String
    ) {
        val chooser = Intent.createChooser(
            baseShareIntent(imageUri, message),
            "Share invitation"
        )
        context.startActivity(chooser)
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
