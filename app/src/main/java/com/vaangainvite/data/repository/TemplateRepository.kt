package com.vaangainvite.data.repository

import com.vaangainvite.R
import com.vaangainvite.data.model.InvitationCategory
import com.vaangainvite.data.model.InvitationTemplate

class TemplateRepository {
    val categories: List<InvitationCategory> = listOf(
        InvitationCategory(
            id = "birthday",
            title = "Birthday",
            description = "Bright lotus-inspired cards for joyful birthday celebrations.",
            accentColor = 0xFFFF7A90.toInt()
        ),
        InvitationCategory(
            id = "wedding",
            title = "Wedding",
            description = "Traditional kolam and maroon-gold invitations for weddings.",
            accentColor = 0xFF8B1E3F.toInt()
        ),
        InvitationCategory(
            id = "housewarming",
            title = "Housewarming",
            description = "Banana leaf and mango motifs for a warm grihapravesam invite.",
            accentColor = 0xFF2E7D32.toInt()
        ),
        InvitationCategory(
            id = "puberty",
            title = "Puberty Ceremony",
            description = "Floral cards for celebrating a traditional puberty ceremony.",
            accentColor = 0xFF9C27B0.toInt()
        )
    )

    private val templates: List<InvitationTemplate> = listOf(

InvitationTemplate(
    id = "birthday_01",
    categoryId = "birthday",
    title = "Lotus Birthday",
    description = "Festive South Indian birthday layout with local motifs and warm colors.",
    drawableResId = R.drawable.template_birthday_01,
    primaryColor = 0xFF8B1E3F.toInt()
),

InvitationTemplate(
    id = "birthday_02",
    categoryId = "birthday",
    title = "Festive Arch",
    description = "Festive South Indian birthday layout with local motifs and warm colors.",
    drawableResId = R.drawable.template_birthday_02,
    primaryColor = 0xFF8B1E3F.toInt()
),

InvitationTemplate(
    id = "birthday_03",
    categoryId = "birthday",
    title = "Rose Garland",
    description = "Festive South Indian birthday layout with local motifs and warm colors.",
    drawableResId = R.drawable.template_birthday_03,
    primaryColor = 0xFF8B1E3F.toInt()
),

InvitationTemplate(
    id = "birthday_04",
    categoryId = "birthday",
    title = "Golden Joy",
    description = "Festive South Indian birthday layout with local motifs and warm colors.",
    drawableResId = R.drawable.template_birthday_04,
    primaryColor = 0xFF8B1E3F.toInt()
),

InvitationTemplate(
    id = "birthday_05",
    categoryId = "birthday",
    title = "Temple Toran",
    description = "Festive South Indian birthday layout with local motifs and warm colors.",
    drawableResId = R.drawable.template_birthday_05,
    primaryColor = 0xFF8B1E3F.toInt()
),

InvitationTemplate(
    id = "birthday_06",
    categoryId = "birthday",
    title = "Pink Kolam",
    description = "Festive South Indian birthday layout with local motifs and warm colors.",
    drawableResId = R.drawable.template_birthday_06,
    primaryColor = 0xFF8B1E3F.toInt()
),

InvitationTemplate(
    id = "birthday_07",
    categoryId = "birthday",
    title = "Celebration Lamp",
    description = "Festive South Indian birthday layout with local motifs and warm colors.",
    drawableResId = R.drawable.template_birthday_07,
    primaryColor = 0xFF8B1E3F.toInt()
),

InvitationTemplate(
    id = "birthday_08",
    categoryId = "birthday",
    title = "Mango Leaf Party",
    description = "Festive South Indian birthday layout with local motifs and warm colors.",
    drawableResId = R.drawable.template_birthday_08,
    primaryColor = 0xFF8B1E3F.toInt()
),

InvitationTemplate(
    id = "birthday_09",
    categoryId = "birthday",
    title = "Sandalwood Bloom",
    description = "Festive South Indian birthday layout with local motifs and warm colors.",
    drawableResId = R.drawable.template_birthday_09,
    primaryColor = 0xFF8B1E3F.toInt()
),

InvitationTemplate(
    id = "birthday_10",
    categoryId = "birthday",
    title = "Jasmine Wishes",
    description = "Festive South Indian birthday layout with local motifs and warm colors.",
    drawableResId = R.drawable.template_birthday_10,
    primaryColor = 0xFF8B1E3F.toInt()
),

InvitationTemplate(
    id = "birthday_11",
    categoryId = "birthday",
    title = "Maroon Sparkle",
    description = "Festive South Indian birthday layout with local motifs and warm colors.",
    drawableResId = R.drawable.template_birthday_11,
    primaryColor = 0xFF8B1E3F.toInt()
),

InvitationTemplate(
    id = "birthday_12",
    categoryId = "birthday",
    title = "Sweet Pongal",
    description = "Festive South Indian birthday layout with local motifs and warm colors.",
    drawableResId = R.drawable.template_birthday_12,
    primaryColor = 0xFF8B1E3F.toInt()
),

InvitationTemplate(
    id = "birthday_13",
    categoryId = "birthday",
    title = "Peacock Pink",
    description = "Festive South Indian birthday layout with local motifs and warm colors.",
    drawableResId = R.drawable.template_birthday_13,
    primaryColor = 0xFF8B1E3F.toInt()
),

InvitationTemplate(
    id = "birthday_14",
    categoryId = "birthday",
    title = "Banana Leaf Joy",
    description = "Festive South Indian birthday layout with local motifs and warm colors.",
    drawableResId = R.drawable.template_birthday_14,
    primaryColor = 0xFF8B1E3F.toInt()
),

InvitationTemplate(
    id = "birthday_15",
    categoryId = "birthday",
    title = "Gold Crown",
    description = "Festive South Indian birthday layout with local motifs and warm colors.",
    drawableResId = R.drawable.template_birthday_15,
    primaryColor = 0xFF8B1E3F.toInt()
),

InvitationTemplate(
    id = "birthday_16",
    categoryId = "birthday",
    title = "Lotus Mandapam",
    description = "Festive South Indian birthday layout with local motifs and warm colors.",
    drawableResId = R.drawable.template_birthday_16,
    primaryColor = 0xFF8B1E3F.toInt()
),

InvitationTemplate(
    id = "birthday_17",
    categoryId = "birthday",
    title = "Coral Blessings",
    description = "Festive South Indian birthday layout with local motifs and warm colors.",
    drawableResId = R.drawable.template_birthday_17,
    primaryColor = 0xFF8B1E3F.toInt()
),

InvitationTemplate(
    id = "birthday_18",
    categoryId = "birthday",
    title = "Kuthu Vilakku",
    description = "Festive South Indian birthday layout with local motifs and warm colors.",
    drawableResId = R.drawable.template_birthday_18,
    primaryColor = 0xFF8B1E3F.toInt()
),

InvitationTemplate(
    id = "birthday_19",
    categoryId = "birthday",
    title = "Pastel Kolam",
    description = "Festive South Indian birthday layout with local motifs and warm colors.",
    drawableResId = R.drawable.template_birthday_19,
    primaryColor = 0xFF8B1E3F.toInt()
),

InvitationTemplate(
    id = "birthday_20",
    categoryId = "birthday",
    title = "Vaanga Birthday",
    description = "Festive South Indian birthday layout with local motifs and warm colors.",
    drawableResId = R.drawable.template_birthday_20,
    primaryColor = 0xFF8B1E3F.toInt()
),

InvitationTemplate(
    id = "wedding_01",
    categoryId = "wedding",
    title = "Kolam Muhurtham",
    description = "Traditional maroon-gold wedding card with temple and kolam details.",
    drawableResId = R.drawable.template_wedding_01,
    primaryColor = 0xFF6E1634.toInt()
),

InvitationTemplate(
    id = "wedding_02",
    categoryId = "wedding",
    title = "Temple Bells",
    description = "Traditional maroon-gold wedding card with temple and kolam details.",
    drawableResId = R.drawable.template_wedding_02,
    primaryColor = 0xFF6E1634.toInt()
),

InvitationTemplate(
    id = "wedding_03",
    categoryId = "wedding",
    title = "Maroon Mandapam",
    description = "Traditional maroon-gold wedding card with temple and kolam details.",
    drawableResId = R.drawable.template_wedding_03,
    primaryColor = 0xFF6E1634.toInt()
),

InvitationTemplate(
    id = "wedding_04",
    categoryId = "wedding",
    title = "Golden Garland",
    description = "Traditional maroon-gold wedding card with temple and kolam details.",
    drawableResId = R.drawable.template_wedding_04,
    primaryColor = 0xFF6E1634.toInt()
),

InvitationTemplate(
    id = "wedding_05",
    categoryId = "wedding",
    title = "Sacred Knot",
    description = "Traditional maroon-gold wedding card with temple and kolam details.",
    drawableResId = R.drawable.template_wedding_05,
    primaryColor = 0xFF6E1634.toInt()
),

InvitationTemplate(
    id = "wedding_06",
    categoryId = "wedding",
    title = "Jasmine Vows",
    description = "Traditional maroon-gold wedding card with temple and kolam details.",
    drawableResId = R.drawable.template_wedding_06,
    primaryColor = 0xFF6E1634.toInt()
),

InvitationTemplate(
    id = "wedding_07",
    categoryId = "wedding",
    title = "Mango Leaf Muhurtham",
    description = "Traditional maroon-gold wedding card with temple and kolam details.",
    drawableResId = R.drawable.template_wedding_07,
    primaryColor = 0xFF6E1634.toInt()
),

InvitationTemplate(
    id = "wedding_08",
    categoryId = "wedding",
    title = "Sandalwood Wedding",
    description = "Traditional maroon-gold wedding card with temple and kolam details.",
    drawableResId = R.drawable.template_wedding_08,
    primaryColor = 0xFF6E1634.toInt()
),

InvitationTemplate(
    id = "wedding_09",
    categoryId = "wedding",
    title = "Royal Maroon",
    description = "Traditional maroon-gold wedding card with temple and kolam details.",
    drawableResId = R.drawable.template_wedding_09,
    primaryColor = 0xFF6E1634.toInt()
),

InvitationTemplate(
    id = "wedding_10",
    categoryId = "wedding",
    title = "Peacock Mandapam",
    description = "Traditional maroon-gold wedding card with temple and kolam details.",
    drawableResId = R.drawable.template_wedding_10,
    primaryColor = 0xFF6E1634.toInt()
),

InvitationTemplate(
    id = "wedding_11",
    categoryId = "wedding",
    title = "Lotus Kalyanam",
    description = "Traditional maroon-gold wedding card with temple and kolam details.",
    drawableResId = R.drawable.template_wedding_11,
    primaryColor = 0xFF6E1634.toInt()
),

InvitationTemplate(
    id = "wedding_12",
    categoryId = "wedding",
    title = "Deepam Wedding",
    description = "Traditional maroon-gold wedding card with temple and kolam details.",
    drawableResId = R.drawable.template_wedding_12,
    primaryColor = 0xFF6E1634.toInt()
),

InvitationTemplate(
    id = "wedding_13",
    categoryId = "wedding",
    title = "Banana Leaf Vows",
    description = "Traditional maroon-gold wedding card with temple and kolam details.",
    drawableResId = R.drawable.template_wedding_13,
    primaryColor = 0xFF6E1634.toInt()
),

InvitationTemplate(
    id = "wedding_14",
    categoryId = "wedding",
    title = "Classic Kolam",
    description = "Traditional maroon-gold wedding card with temple and kolam details.",
    drawableResId = R.drawable.template_wedding_14,
    primaryColor = 0xFF6E1634.toInt()
),

InvitationTemplate(
    id = "wedding_15",
    categoryId = "wedding",
    title = "Golden Thoranam",
    description = "Traditional maroon-gold wedding card with temple and kolam details.",
    drawableResId = R.drawable.template_wedding_15,
    primaryColor = 0xFF6E1634.toInt()
),

InvitationTemplate(
    id = "wedding_16",
    categoryId = "wedding",
    title = "Ruby Blessings",
    description = "Traditional maroon-gold wedding card with temple and kolam details.",
    drawableResId = R.drawable.template_wedding_16,
    primaryColor = 0xFF6E1634.toInt()
),

InvitationTemplate(
    id = "wedding_17",
    categoryId = "wedding",
    title = "Temple Gopuram",
    description = "Traditional maroon-gold wedding card with temple and kolam details.",
    drawableResId = R.drawable.template_wedding_17,
    primaryColor = 0xFF6E1634.toInt()
),

InvitationTemplate(
    id = "wedding_18",
    categoryId = "wedding",
    title = "Pearl Garland",
    description = "Traditional maroon-gold wedding card with temple and kolam details.",
    drawableResId = R.drawable.template_wedding_18,
    primaryColor = 0xFF6E1634.toInt()
),

InvitationTemplate(
    id = "wedding_19",
    categoryId = "wedding",
    title = "Muhurtham Bloom",
    description = "Traditional maroon-gold wedding card with temple and kolam details.",
    drawableResId = R.drawable.template_wedding_19,
    primaryColor = 0xFF6E1634.toInt()
),

InvitationTemplate(
    id = "wedding_20",
    categoryId = "wedding",
    title = "Vaanga Wedding",
    description = "Traditional maroon-gold wedding card with temple and kolam details.",
    drawableResId = R.drawable.template_wedding_20,
    primaryColor = 0xFF6E1634.toInt()
),

InvitationTemplate(
    id = "housewarming_01",
    categoryId = "housewarming",
    title = "Mango Leaf Home",
    description = "Auspicious housewarming invite with leaves, lamps, and home motifs.",
    drawableResId = R.drawable.template_housewarming_01,
    primaryColor = 0xFF1F6F35.toInt()
),

InvitationTemplate(
    id = "housewarming_02",
    categoryId = "housewarming",
    title = "Deepam Grihapravesam",
    description = "Auspicious housewarming invite with leaves, lamps, and home motifs.",
    drawableResId = R.drawable.template_housewarming_02,
    primaryColor = 0xFF1F6F35.toInt()
),

InvitationTemplate(
    id = "housewarming_03",
    categoryId = "housewarming",
    title = "Green Threshold",
    description = "Auspicious housewarming invite with leaves, lamps, and home motifs.",
    drawableResId = R.drawable.template_housewarming_03,
    primaryColor = 0xFF1F6F35.toInt()
),

InvitationTemplate(
    id = "housewarming_04",
    categoryId = "housewarming",
    title = "Temple Doorway",
    description = "Auspicious housewarming invite with leaves, lamps, and home motifs.",
    drawableResId = R.drawable.template_housewarming_04,
    primaryColor = 0xFF1F6F35.toInt()
),

InvitationTemplate(
    id = "housewarming_05",
    categoryId = "housewarming",
    title = "Sandalwood Home",
    description = "Auspicious housewarming invite with leaves, lamps, and home motifs.",
    drawableResId = R.drawable.template_housewarming_05,
    primaryColor = 0xFF1F6F35.toInt()
),

InvitationTemplate(
    id = "housewarming_06",
    categoryId = "housewarming",
    title = "Golden Griham",
    description = "Auspicious housewarming invite with leaves, lamps, and home motifs.",
    drawableResId = R.drawable.template_housewarming_06,
    primaryColor = 0xFF1F6F35.toInt()
),

InvitationTemplate(
    id = "housewarming_07",
    categoryId = "housewarming",
    title = "Banana Leaf Welcome",
    description = "Auspicious housewarming invite with leaves, lamps, and home motifs.",
    drawableResId = R.drawable.template_housewarming_07,
    primaryColor = 0xFF1F6F35.toInt()
),

InvitationTemplate(
    id = "housewarming_08",
    categoryId = "housewarming",
    title = "Kolam Entrance",
    description = "Auspicious housewarming invite with leaves, lamps, and home motifs.",
    drawableResId = R.drawable.template_housewarming_08,
    primaryColor = 0xFF1F6F35.toInt()
),

InvitationTemplate(
    id = "housewarming_09",
    categoryId = "housewarming",
    title = "Auspicious Lamp",
    description = "Auspicious housewarming invite with leaves, lamps, and home motifs.",
    drawableResId = R.drawable.template_housewarming_09,
    primaryColor = 0xFF1F6F35.toInt()
),

InvitationTemplate(
    id = "housewarming_10",
    categoryId = "housewarming",
    title = "Tulasi Blessing",
    description = "Auspicious housewarming invite with leaves, lamps, and home motifs.",
    drawableResId = R.drawable.template_housewarming_10,
    primaryColor = 0xFF1F6F35.toInt()
),

InvitationTemplate(
    id = "housewarming_11",
    categoryId = "housewarming",
    title = "Maroon Roofline",
    description = "Auspicious housewarming invite with leaves, lamps, and home motifs.",
    drawableResId = R.drawable.template_housewarming_11,
    primaryColor = 0xFF1F6F35.toInt()
),

InvitationTemplate(
    id = "housewarming_12",
    categoryId = "housewarming",
    title = "Fresh Start",
    description = "Auspicious housewarming invite with leaves, lamps, and home motifs.",
    drawableResId = R.drawable.template_housewarming_12,
    primaryColor = 0xFF1F6F35.toInt()
),

InvitationTemplate(
    id = "housewarming_13",
    categoryId = "housewarming",
    title = "Vaastu Deepam",
    description = "Auspicious housewarming invite with leaves, lamps, and home motifs.",
    drawableResId = R.drawable.template_housewarming_13,
    primaryColor = 0xFF1F6F35.toInt()
),

InvitationTemplate(
    id = "housewarming_14",
    categoryId = "housewarming",
    title = "Home Sweet Home",
    description = "Auspicious housewarming invite with leaves, lamps, and home motifs.",
    drawableResId = R.drawable.template_housewarming_14,
    primaryColor = 0xFF1F6F35.toInt()
),

InvitationTemplate(
    id = "housewarming_15",
    categoryId = "housewarming",
    title = "Leaf Garland",
    description = "Auspicious housewarming invite with leaves, lamps, and home motifs.",
    drawableResId = R.drawable.template_housewarming_15,
    primaryColor = 0xFF1F6F35.toInt()
),

InvitationTemplate(
    id = "housewarming_16",
    categoryId = "housewarming",
    title = "Gopuram Home",
    description = "Auspicious housewarming invite with leaves, lamps, and home motifs.",
    drawableResId = R.drawable.template_housewarming_16,
    primaryColor = 0xFF1F6F35.toInt()
),

InvitationTemplate(
    id = "housewarming_17",
    categoryId = "housewarming",
    title = "Warm Hearth",
    description = "Auspicious housewarming invite with leaves, lamps, and home motifs.",
    drawableResId = R.drawable.template_housewarming_17,
    primaryColor = 0xFF1F6F35.toInt()
),

InvitationTemplate(
    id = "housewarming_18",
    categoryId = "housewarming",
    title = "Prosperity Door",
    description = "Auspicious housewarming invite with leaves, lamps, and home motifs.",
    drawableResId = R.drawable.template_housewarming_18,
    primaryColor = 0xFF1F6F35.toInt()
),

InvitationTemplate(
    id = "housewarming_19",
    categoryId = "housewarming",
    title = "Green Kolam",
    description = "Auspicious housewarming invite with leaves, lamps, and home motifs.",
    drawableResId = R.drawable.template_housewarming_19,
    primaryColor = 0xFF1F6F35.toInt()
),

InvitationTemplate(
    id = "housewarming_20",
    categoryId = "housewarming",
    title = "Vaanga Home",
    description = "Auspicious housewarming invite with leaves, lamps, and home motifs.",
    drawableResId = R.drawable.template_housewarming_20,
    primaryColor = 0xFF1F6F35.toInt()
),

InvitationTemplate(
    id = "puberty_01",
    categoryId = "puberty",
    title = "Floral Blessings",
    description = "Elegant puberty ceremony card with floral and peacock-inspired details.",
    drawableResId = R.drawable.template_puberty_01,
    primaryColor = 0xFF6A1B9A.toInt()
),

InvitationTemplate(
    id = "puberty_02",
    categoryId = "puberty",
    title = "Peacock Blessings",
    description = "Elegant puberty ceremony card with floral and peacock-inspired details.",
    drawableResId = R.drawable.template_puberty_02,
    primaryColor = 0xFF6A1B9A.toInt()
),

InvitationTemplate(
    id = "puberty_03",
    categoryId = "puberty",
    title = "Lotus Ceremony",
    description = "Elegant puberty ceremony card with floral and peacock-inspired details.",
    drawableResId = R.drawable.template_puberty_03,
    primaryColor = 0xFF6A1B9A.toInt()
),

InvitationTemplate(
    id = "puberty_04",
    categoryId = "puberty",
    title = "Pink Mandapam",
    description = "Elegant puberty ceremony card with floral and peacock-inspired details.",
    drawableResId = R.drawable.template_puberty_04,
    primaryColor = 0xFF6A1B9A.toInt()
),

InvitationTemplate(
    id = "puberty_05",
    categoryId = "puberty",
    title = "Jasmine Garland",
    description = "Elegant puberty ceremony card with floral and peacock-inspired details.",
    drawableResId = R.drawable.template_puberty_05,
    primaryColor = 0xFF6A1B9A.toInt()
),

InvitationTemplate(
    id = "puberty_06",
    categoryId = "puberty",
    title = "Golden Girlhood",
    description = "Elegant puberty ceremony card with floral and peacock-inspired details.",
    drawableResId = R.drawable.template_puberty_06,
    primaryColor = 0xFF6A1B9A.toInt()
),

InvitationTemplate(
    id = "puberty_07",
    categoryId = "puberty",
    title = "Temple Floral",
    description = "Elegant puberty ceremony card with floral and peacock-inspired details.",
    drawableResId = R.drawable.template_puberty_07,
    primaryColor = 0xFF6A1B9A.toInt()
),

InvitationTemplate(
    id = "puberty_08",
    categoryId = "puberty",
    title = "Rose Kolam",
    description = "Elegant puberty ceremony card with floral and peacock-inspired details.",
    drawableResId = R.drawable.template_puberty_08,
    primaryColor = 0xFF6A1B9A.toInt()
),

InvitationTemplate(
    id = "puberty_09",
    categoryId = "puberty",
    title = "Purple Peacock",
    description = "Elegant puberty ceremony card with floral and peacock-inspired details.",
    drawableResId = R.drawable.template_puberty_09,
    primaryColor = 0xFF6A1B9A.toInt()
),

InvitationTemplate(
    id = "puberty_10",
    categoryId = "puberty",
    title = "Sandalwood Bloom",
    description = "Elegant puberty ceremony card with floral and peacock-inspired details.",
    drawableResId = R.drawable.template_puberty_10,
    primaryColor = 0xFF6A1B9A.toInt()
),

InvitationTemplate(
    id = "puberty_11",
    categoryId = "puberty",
    title = "Mango Leaf Ceremony",
    description = "Elegant puberty ceremony card with floral and peacock-inspired details.",
    drawableResId = R.drawable.template_puberty_11,
    primaryColor = 0xFF6A1B9A.toInt()
),

InvitationTemplate(
    id = "puberty_12",
    categoryId = "puberty",
    title = "Ruby Floral",
    description = "Elegant puberty ceremony card with floral and peacock-inspired details.",
    drawableResId = R.drawable.template_puberty_12,
    primaryColor = 0xFF6A1B9A.toInt()
),

InvitationTemplate(
    id = "puberty_13",
    categoryId = "puberty",
    title = "Banana Leaf Blessing",
    description = "Elegant puberty ceremony card with floral and peacock-inspired details.",
    drawableResId = R.drawable.template_puberty_13,
    primaryColor = 0xFF6A1B9A.toInt()
),

InvitationTemplate(
    id = "puberty_14",
    categoryId = "puberty",
    title = "Pearl Pink",
    description = "Elegant puberty ceremony card with floral and peacock-inspired details.",
    drawableResId = R.drawable.template_puberty_14,
    primaryColor = 0xFF6A1B9A.toInt()
),

InvitationTemplate(
    id = "puberty_15",
    categoryId = "puberty",
    title = "Deepam Blessing",
    description = "Elegant puberty ceremony card with floral and peacock-inspired details.",
    drawableResId = R.drawable.template_puberty_15,
    primaryColor = 0xFF6A1B9A.toInt()
),

InvitationTemplate(
    id = "puberty_16",
    categoryId = "puberty",
    title = "Vaanga Ceremony",
    description = "Elegant puberty ceremony card with floral and peacock-inspired details.",
    drawableResId = R.drawable.template_puberty_16,
    primaryColor = 0xFF6A1B9A.toInt()
),

InvitationTemplate(
    id = "puberty_17",
    categoryId = "puberty",
    title = "Coral Lotus",
    description = "Elegant puberty ceremony card with floral and peacock-inspired details.",
    drawableResId = R.drawable.template_puberty_17,
    primaryColor = 0xFF6A1B9A.toInt()
),

InvitationTemplate(
    id = "puberty_18",
    categoryId = "puberty",
    title = "Traditional Bloom",
    description = "Elegant puberty ceremony card with floral and peacock-inspired details.",
    drawableResId = R.drawable.template_puberty_18,
    primaryColor = 0xFF6A1B9A.toInt()
),

InvitationTemplate(
    id = "puberty_19",
    categoryId = "puberty",
    title = "Sacred Floral",
    description = "Elegant puberty ceremony card with floral and peacock-inspired details.",
    drawableResId = R.drawable.template_puberty_19,
    primaryColor = 0xFF6A1B9A.toInt()
),

InvitationTemplate(
    id = "puberty_20",
    categoryId = "puberty",
    title = "Tamil Blessings",
    description = "Elegant puberty ceremony card with floral and peacock-inspired details.",
    drawableResId = R.drawable.template_puberty_20,
    primaryColor = 0xFF6A1B9A.toInt()
)
    )

    fun templatesForCategory(categoryId: String): List<InvitationTemplate> {
        return templates.filter { it.categoryId == categoryId }
    }

    fun categoryById(categoryId: String): InvitationCategory? {
        return categories.firstOrNull { it.id == categoryId }
    }

    fun templateById(templateId: String): InvitationTemplate? {
        return templates.firstOrNull { it.id == templateId }
    }
}
