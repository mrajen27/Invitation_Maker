package com.vaangainvite.data.model

data class InvitationDetails(
    val occasionTitle: String = "",
    val name: String = "",
    val date: String = "",
    val time: String = "",
    val venue: String = "",
    val mobileNumber: String = "",
    val message: String = ""
) {
    companion object {
        const val VENUE_MAX_LINES = 2

        const val OCCASION_MAX_LENGTH = InvitationFieldLimits.OCCASION_MAX_LENGTH
        const val NAME_MAX_LENGTH = InvitationFieldLimits.NAME_MAX_LENGTH
        const val DATE_MAX_LENGTH = InvitationFieldLimits.DATE_MAX_LENGTH
        const val TIME_MAX_LENGTH = InvitationFieldLimits.TIME_MAX_LENGTH
        const val VENUE_MAX_LENGTH = InvitationFieldLimits.VENUE_MAX_LENGTH
        const val MOBILE_MAX_LENGTH = InvitationFieldLimits.MOBILE_MAX_LENGTH
        const val MESSAGE_MAX_LENGTH = InvitationFieldLimits.MESSAGE_MAX_LENGTH
        const val MESSAGE_MAX_LINES_ON_CARD = InvitationFieldLimits.MESSAGE_MAX_LINES_ON_CARD
    }
}
