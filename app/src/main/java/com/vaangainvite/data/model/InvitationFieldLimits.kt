package com.vaangainvite.data.model

/**
 * Character limits aligned with invitation card layout (1080×1350) and wrapping rules.
 */
object InvitationFieldLimits {
    const val OCCASION_MAX_LENGTH = 50
    const val NAME_MAX_LENGTH = 42
    const val DATE_MAX_LENGTH = 28
    const val TIME_MAX_LENGTH = 18
    const val VENUE_MAX_LENGTH = 60
    const val VENUE_MAX_LINES = 2
    const val MOBILE_MAX_LENGTH = 18
    /** Max content characters for the additional message (newlines not counted). */
    const val MESSAGE_MAX_LENGTH = 60
    const val MESSAGE_MAX_LENGTH_WITH_PHOTO = 60
    const val MESSAGE_MAX_LINES_ON_CARD = 2
}

fun InvitationDetails.messageMaxLength(@Suppress("UNUSED_PARAMETER") hasUploadedPhoto: Boolean): Int {
    return InvitationFieldLimits.MESSAGE_MAX_LENGTH
}

fun InvitationDetails.clampedForCard(hasUploadedPhoto: Boolean = false): InvitationDetails {
    return copy(
        occasionTitle = occasionTitle.take(InvitationFieldLimits.OCCASION_MAX_LENGTH),
        name = name.take(InvitationFieldLimits.NAME_MAX_LENGTH),
        date = date.take(InvitationFieldLimits.DATE_MAX_LENGTH),
        time = time.take(InvitationFieldLimits.TIME_MAX_LENGTH),
        venue = normalizeVenue(venue),
        mobileNumber = mobileNumber.take(InvitationFieldLimits.MOBILE_MAX_LENGTH),
        message = normalizeMessage(message)
    )
}

fun InvitationDetails.validationError(hasUploadedPhoto: Boolean = false): String? {
    val messageLimit = messageMaxLength(hasUploadedPhoto)
    when {
        occasionTitle.length > InvitationFieldLimits.OCCASION_MAX_LENGTH ->
            return "Occasion title must be ${InvitationFieldLimits.OCCASION_MAX_LENGTH} characters or less"
        name.length > InvitationFieldLimits.NAME_MAX_LENGTH ->
            return "Name must be ${InvitationFieldLimits.NAME_MAX_LENGTH} characters or less"
        date.length > InvitationFieldLimits.DATE_MAX_LENGTH ->
            return "Date must be ${InvitationFieldLimits.DATE_MAX_LENGTH} characters or less"
        time.length > InvitationFieldLimits.TIME_MAX_LENGTH ->
            return "Time must be ${InvitationFieldLimits.TIME_MAX_LENGTH} characters or less"
        venue.replace("\n", "").length > InvitationFieldLimits.VENUE_MAX_LENGTH ->
            return "Venue must be ${InvitationFieldLimits.VENUE_MAX_LENGTH} characters or less"
        mobileNumber.length > InvitationFieldLimits.MOBILE_MAX_LENGTH ->
            return "Mobile number must be ${InvitationFieldLimits.MOBILE_MAX_LENGTH} characters or less"
        message.replace("\n", "").length > messageLimit ->
            return "Message must be $messageLimit characters or less"
    }
    return null
}

/**
 * Keeps at most [maxLines] explicit rows and [maxTotalChars] of text (newlines not counted).
 * Line breaks are only where the user presses Enter; the card renderer wraps words by width.
 */
private fun normalizeMultiLineField(
    raw: String,
    maxLines: Int,
    maxTotalChars: Int
): String {
    val normalized = raw.replace("\r\n", "\n")
    if (normalized.isEmpty()) return ""

    val parts = normalized.split("\n")
    val merged = when {
        parts.size <= maxLines -> parts
        else -> {
            val head = parts.take(maxLines - 1)
            val tail = parts.drop(maxLines - 1).joinToString(" ")
            head + tail
        }
    }

    val result = StringBuilder()
    var remaining = maxTotalChars
    merged.forEachIndexed { index, line ->
        if (index > 0) {
            if (remaining <= 0) return@forEachIndexed
            result.append('\n')
        }
        val segment = line.take(remaining)
        result.append(segment)
        remaining -= segment.length
    }

    // Preserve trailing Enter when user started a second line (e.g. "Line one\n").
    if (normalized.endsWith("\n") && !result.endsWith("\n") && result.count { it == '\n' } < maxLines - 1) {
        result.append('\n')
    }
    return result.toString()
}

fun normalizeVenue(raw: String): String {
    return normalizeMultiLineField(
        raw = raw,
        maxLines = InvitationFieldLimits.VENUE_MAX_LINES,
        maxTotalChars = InvitationFieldLimits.VENUE_MAX_LENGTH
    )
}

fun normalizeMessage(raw: String): String {
    return normalizeMultiLineField(
        raw = raw,
        maxLines = InvitationFieldLimits.MESSAGE_MAX_LINES_ON_CARD,
        maxTotalChars = InvitationFieldLimits.MESSAGE_MAX_LENGTH
    )
}
