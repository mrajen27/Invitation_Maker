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
    const val VENUE_MAX_CHARS_PER_LINE = 30
    const val VENUE_MAX_LINES = 2
    const val MOBILE_MAX_LENGTH = 18
    /** Two lines × 30 characters on the card message area. */
    const val MESSAGE_MAX_LENGTH = 60
    const val MESSAGE_MAX_LENGTH_WITH_PHOTO = 60
    const val MESSAGE_MAX_CHARS_PER_LINE = 30
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
 * Keeps text to [maxLines] with at most [maxCharsPerLine] characters each.
 * Overflow from line 1 rolls into line 2 only when the user does not press Enter.
 */
private fun normalizeTwoLineField(
    raw: String,
    maxLines: Int,
    maxCharsPerLine: Int
): String {
    val normalized = raw.replace("\r\n", "\n")
    val hasExplicitBreak = normalized.contains('\n')
    val explicitLines = normalized.split("\n", limit = maxLines + 1)
    val line1Source = explicitLines.getOrElse(0) { "" }
    val line2Source = buildString {
        append(explicitLines.getOrElse(1) { "" })
        explicitLines.drop(2).forEach { extra ->
            if (extra.isNotEmpty()) {
                if (isNotEmpty()) append(' ')
                append(extra)
            }
        }
    }

    if (hasExplicitBreak) {
        val line1 = line1Source.take(maxCharsPerLine)
        val line2 = line2Source.take(maxCharsPerLine)
        return when {
            line2.isNotEmpty() -> "$line1\n$line2"
            normalized.endsWith("\n") || explicitLines.size >= 2 -> "$line1\n"
            else -> line1
        }
    }

    val line1 = line1Source.take(maxCharsPerLine)
    val line2 = line1Source.drop(maxCharsPerLine).take(maxCharsPerLine)
    return if (line2.isEmpty()) line1 else "$line1\n$line2"
}

fun normalizeVenue(raw: String): String {
    return normalizeTwoLineField(
        raw = raw,
        maxLines = InvitationFieldLimits.VENUE_MAX_LINES,
        maxCharsPerLine = InvitationFieldLimits.VENUE_MAX_CHARS_PER_LINE
    )
}

fun normalizeMessage(raw: String): String {
    return normalizeTwoLineField(
        raw = raw,
        maxLines = InvitationFieldLimits.MESSAGE_MAX_LINES_ON_CARD,
        maxCharsPerLine = InvitationFieldLimits.MESSAGE_MAX_CHARS_PER_LINE
    )
}
