package com.vaangainvite.data.model

/**
 * Character limits aligned with invitation card layout (1080×1350) and wrapping rules.
 */
object InvitationFieldLimits {
    const val OCCASION_MAX_LENGTH = 50
    const val NAME_MAX_LENGTH = 42
    const val DATE_MAX_LENGTH = 28
    const val TIME_MAX_LENGTH = 18
    const val VENUE_MAX_LENGTH = 99
    const val MOBILE_MAX_LENGTH = 18
    /** Fits ~2 lines inside photo-template side/bottom safe area without overlapping art. */
    const val MESSAGE_MAX_LENGTH = 80
    const val MESSAGE_MAX_LINES_ON_CARD = 2
}

fun InvitationDetails.clampedForCard(): InvitationDetails {
    return copy(
        occasionTitle = occasionTitle.take(InvitationFieldLimits.OCCASION_MAX_LENGTH),
        name = name.take(InvitationFieldLimits.NAME_MAX_LENGTH),
        date = date.take(InvitationFieldLimits.DATE_MAX_LENGTH),
        time = time.take(InvitationFieldLimits.TIME_MAX_LENGTH),
        venue = venue.take(InvitationFieldLimits.VENUE_MAX_LENGTH),
        mobileNumber = mobileNumber.take(InvitationFieldLimits.MOBILE_MAX_LENGTH),
        message = message.take(InvitationFieldLimits.MESSAGE_MAX_LENGTH)
    )
}

fun InvitationDetails.validationError(): String? {
    when {
        occasionTitle.length > InvitationFieldLimits.OCCASION_MAX_LENGTH ->
            return "Occasion title must be ${InvitationFieldLimits.OCCASION_MAX_LENGTH} characters or less"
        name.length > InvitationFieldLimits.NAME_MAX_LENGTH ->
            return "Name must be ${InvitationFieldLimits.NAME_MAX_LENGTH} characters or less"
        date.length > InvitationFieldLimits.DATE_MAX_LENGTH ->
            return "Date must be ${InvitationFieldLimits.DATE_MAX_LENGTH} characters or less"
        time.length > InvitationFieldLimits.TIME_MAX_LENGTH ->
            return "Time must be ${InvitationFieldLimits.TIME_MAX_LENGTH} characters or less"
        venue.length > InvitationFieldLimits.VENUE_MAX_LENGTH ->
            return "Venue must be ${InvitationFieldLimits.VENUE_MAX_LENGTH} characters or less"
        mobileNumber.length > InvitationFieldLimits.MOBILE_MAX_LENGTH ->
            return "Mobile number must be ${InvitationFieldLimits.MOBILE_MAX_LENGTH} characters or less"
        message.length > InvitationFieldLimits.MESSAGE_MAX_LENGTH ->
            return "Message must be ${InvitationFieldLimits.MESSAGE_MAX_LENGTH} characters or less"
    }
    return null
}
