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
    /** Fits ~2 lines in the message area without overlapping art. */
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
        message = message.take(messageMaxLength(hasUploadedPhoto))
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
        message.length > messageLimit ->
            return "Message must be $messageLimit characters or less"
    }
    return null
}

/**
 * Keeps venue to two lines with at most [VENUE_MAX_CHARS_PER_LINE] characters each.
 * Overflow from line 1 rolls into line 2 when the user types without pressing Enter.
 */
fun normalizeVenue(raw: String): String {
    val normalized = raw.replace("\r\n", "\n")
    val explicitLines = normalized.split("\n", limit = InvitationFieldLimits.VENUE_MAX_LINES + 1)
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

    val line1 = line1Source.take(InvitationFieldLimits.VENUE_MAX_CHARS_PER_LINE)
    val line2 = (line1Source.drop(InvitationFieldLimits.VENUE_MAX_CHARS_PER_LINE) + line2Source)
        .take(InvitationFieldLimits.VENUE_MAX_CHARS_PER_LINE)

    return if (line2.isEmpty()) line1 else "$line1\n$line2"
}
