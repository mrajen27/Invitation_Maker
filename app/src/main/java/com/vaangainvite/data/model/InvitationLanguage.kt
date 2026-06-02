package com.vaangainvite.data.model

enum class InvitationLanguage(
    val displayName: String,
    /** Top line on printable photo cards (e.g. "Please join us for an"). */
    val inviteIntro: String,
    val heading: String,
    val fallbackName: String,
    val dateLabel: String,
    val timeLabel: String,
    val venueLabel: String,
    val mobileLabel: String,
    val fallbackDate: String,
    val fallbackTime: String,
    val fallbackVenue: String,
    val fallbackMessage: String,
    val footer: String,
    val shareMessage: String
) {
    ENGLISH(
        displayName = "English",
        inviteIntro = "Please join us for an",
        heading = "You are warmly invited",
        fallbackName = "Name of Honoree",
        dateLabel = "Date",
        timeLabel = "Time",
        venueLabel = "Venue",
        mobileLabel = "Contact",
        fallbackDate = "Add date",
        fallbackTime = "Add time",
        fallbackVenue = "Add venue",
        fallbackMessage = "Please join us with family and friends.",
        footer = "Created with Vaanga Invite",
        shareMessage = "Invitation from Vaanga Invite"
    ),
    TAMIL(
        displayName = "தமிழ்",
        inviteIntro = "எங்கள் விழாவில் கலந்து சிறப்பிக்க",
        heading = "அன்புடன் அழைக்கிறோம்",
        fallbackName = "அழைப்பவரின் பெயர்",
        dateLabel = "தேதி",
        timeLabel = "நேரம்",
        venueLabel = "இடம்",
        mobileLabel = "தொடர்பு",
        fallbackDate = "தேதி சேர்க்கவும்",
        fallbackTime = "நேரம் சேர்க்கவும்",
        fallbackVenue = "இடம் சேர்க்கவும்",
        fallbackMessage = "குடும்பத்தினரும் நண்பர்களும் கலந்து சிறப்பிக்க வேண்டுகிறோம்.",
        footer = "வாங்க அழைப்பிதழ் மூலம் உருவாக்கப்பட்டது",
        shareMessage = "வாங்க அழைப்பிதழ் மூலம் அழைப்பிதழ்"
    )
}
