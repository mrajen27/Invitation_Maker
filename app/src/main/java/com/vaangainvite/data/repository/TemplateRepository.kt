package com.vaangainvite.data.repository

import com.vaangainvite.R
import com.vaangainvite.data.model.InvitationCategory
import com.vaangainvite.data.model.InvitationTemplate

class TemplateRepository {
    val categories: List<InvitationCategory> = listOf(
        InvitationCategory(
            id = "birthday",
            title = "Birthday",
            description = "Five photo-style printable cards with balloons, florals, and party themes.",
            accentColor = 0xFFFF1744.toInt()
        ),
        InvitationCategory(
            id = "wedding",
            title = "Wedding",
            description = "Royal maroon-and-gold looks for your big day.",
            accentColor = 0xFF6E1634.toInt()
        ),
        InvitationCategory(
            id = "housewarming",
            title = "Housewarming",
            description = "Fresh, auspicious greens for grihapravesam.",
            accentColor = 0xFF1B5E20.toInt()
        ),
        InvitationCategory(
            id = "puberty",
            title = "Puberty Ceremony",
            description = "Elegant purple-and-rose ceremony styles.",
            accentColor = 0xFF6A1B9A.toInt()
        )
    )

    private val templates: List<InvitationTemplate> = listOf(
        // Birthday — 5 photo-style WebP backgrounds (printable invitation look)
        InvitationTemplate(
            id = "birthday_01",
            categoryId = "birthday",
            title = "Pink & Gold Balloons",
            description = "Soft pink card with realistic balloons and gold frame — like a print-shop invite.",
            drawableResId = R.drawable.bg_birthday_balloon_blast,
            backgroundResId = R.drawable.bg_birthday_balloon_blast,
            primaryColor = 0xFFC2185B.toInt()
        ),
        InvitationTemplate(
            id = "birthday_02",
            categoryId = "birthday",
            title = "Navy Gold Party",
            description = "Premium navy and gold balloons with lights — elegant evening celebration.",
            drawableResId = R.drawable.bg_birthday_navy_gold,
            backgroundResId = R.drawable.bg_birthday_navy_gold,
            primaryColor = 0xFF5D1532.toInt()
        ),
        InvitationTemplate(
            id = "birthday_03",
            categoryId = "birthday",
            title = "Kids Party Fun",
            description = "Bright cartoon balloons, cake, and confetti — perfect for children.",
            drawableResId = R.drawable.bg_birthday_playful,
            backgroundResId = R.drawable.bg_birthday_playful,
            primaryColor = 0xFFE65100.toInt()
        ),
        InvitationTemplate(
            id = "birthday_04",
            categoryId = "birthday",
            title = "Watercolor Roses",
            description = "Sage green watercolor with pink roses — soft and beautiful.",
            drawableResId = R.drawable.bg_birthday_floral,
            backgroundResId = R.drawable.bg_birthday_floral,
            primaryColor = 0xFFAD1457.toInt()
        ),
        InvitationTemplate(
            id = "birthday_05",
            categoryId = "birthday",
            title = "Purple Night Glow",
            description = "Glam purple bokeh with pink balloons — bold party vibe.",
            drawableResId = R.drawable.bg_birthday_purple_party,
            backgroundResId = R.drawable.bg_birthday_purple_party,
            primaryColor = 0xFF7B1FA2.toInt()
        ),

        // Wedding — 5 standout styles
        InvitationTemplate(
            id = "wedding_01",
            categoryId = "wedding",
            title = "Royal Kolam",
            description = "Maroon-gold kolam mandala — classic South Indian wedding charm.",
            drawableResId = R.drawable.template_wedding_01,
            primaryColor = 0xFF6E1634.toInt()
        ),
        InvitationTemplate(
            id = "wedding_02",
            categoryId = "wedding",
            title = "Temple Mandapam",
            description = "Temple arch with sacred bells — ceremonial and striking.",
            drawableResId = R.drawable.template_wedding_02,
            primaryColor = 0xFF5D1532.toInt()
        ),
        InvitationTemplate(
            id = "wedding_03",
            categoryId = "wedding",
            title = "Golden Mandapam",
            description = "Diagonal gold drape with maroon pillars — luxury wedding feel.",
            drawableResId = R.drawable.template_wedding_03,
            primaryColor = 0xFF3E0F24.toInt()
        ),
        InvitationTemplate(
            id = "wedding_04",
            categoryId = "wedding",
            title = "Sacred Knot",
            description = "Thali knot motif at center — meaningful and eye-catching.",
            drawableResId = R.drawable.template_wedding_04,
            primaryColor = 0xFF8B1E3F.toInt()
        ),
        InvitationTemplate(
            id = "wedding_05",
            categoryId = "wedding",
            title = "Peacock Kalyanam",
            description = "Twin peacock crowns in maroon-gold — show-stopping wedding card.",
            drawableResId = R.drawable.template_wedding_05,
            primaryColor = 0xFF4A0E1F.toInt()
        ),

        // Housewarming — 5 standout styles
        InvitationTemplate(
            id = "housewarming_01",
            categoryId = "housewarming",
            title = "Mango Toran",
            description = "Mango-leaf toran with home silhouette — warm grihapravesam welcome.",
            drawableResId = R.drawable.template_housewarming_01,
            primaryColor = 0xFF1B5E20.toInt()
        ),
        InvitationTemplate(
            id = "housewarming_02",
            categoryId = "housewarming",
            title = "Deepam Glow",
            description = "Row of glowing lamps — auspicious light for your new home.",
            drawableResId = R.drawable.template_housewarming_02,
            primaryColor = 0xFF0D3B1E.toInt()
        ),
        InvitationTemplate(
            id = "housewarming_03",
            categoryId = "housewarming",
            title = "Gopuram Welcome",
            description = "Temple doorway frame in green-gold — grand housewarming entrance.",
            drawableResId = R.drawable.template_housewarming_03,
            primaryColor = 0xFF1F6F35.toInt()
        ),
        InvitationTemplate(
            id = "housewarming_04",
            categoryId = "housewarming",
            title = "Banana Leaf Feast",
            description = "Banana-leaf panel with festive border — homely and celebratory.",
            drawableResId = R.drawable.template_housewarming_04,
            primaryColor = 0xFF2E7D32.toInt()
        ),
        InvitationTemplate(
            id = "housewarming_05",
            categoryId = "housewarming",
            title = "Prosperity Kolam",
            description = "Green kolam ring with gold accents — blessings for the new threshold.",
            drawableResId = R.drawable.template_housewarming_05,
            primaryColor = 0xFF0B4F2A.toInt()
        ),

        // Puberty ceremony — 5 standout styles
        InvitationTemplate(
            id = "puberty_01",
            categoryId = "puberty",
            title = "Peacock Grace",
            description = "Twin peacock crests in purple-gold — elegant ceremony statement.",
            drawableResId = R.drawable.template_puberty_01,
            primaryColor = 0xFF6A1B9A.toInt()
        ),
        InvitationTemplate(
            id = "puberty_02",
            categoryId = "puberty",
            title = "Jasmine Garland",
            description = "Rose-gold garland frame with floral corners — soft and festive.",
            drawableResId = R.drawable.template_puberty_02,
            primaryColor = 0xFFAD1457.toInt()
        ),
        InvitationTemplate(
            id = "puberty_03",
            categoryId = "puberty",
            title = "Lotus Bloom",
            description = "Pink lotus mandala on purple — delicate yet vibrant.",
            drawableResId = R.drawable.template_puberty_03,
            primaryColor = 0xFF6A1B9A.toInt()
        ),
        InvitationTemplate(
            id = "puberty_04",
            categoryId = "puberty",
            title = "Royal Purple",
            description = "Deep violet with gold arch and gem florals — regal ceremony look.",
            drawableResId = R.drawable.template_puberty_04,
            primaryColor = 0xFF311B92.toInt()
        ),
        InvitationTemplate(
            id = "puberty_05",
            categoryId = "puberty",
            title = "Rose Festival",
            description = "Rose-gold diagonal sweep with lamp garland — bright and memorable.",
            drawableResId = R.drawable.template_puberty_05,
            primaryColor = 0xFF7B1FA2.toInt()
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
