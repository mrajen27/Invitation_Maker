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
            accentColor = 0xFFFF1744.toInt(),
            iconResId = R.drawable.ic_category_birthday
        ),
        InvitationCategory(
            id = "wedding",
            title = "Wedding",
            description = "Five royal maroon-and-gold printable cards for your muhurtham.",
            accentColor = 0xFF6E1634.toInt(),
            iconResId = R.drawable.ic_category_wedding
        ),
        InvitationCategory(
            id = "housewarming",
            title = "Housewarming",
            description = "Five auspicious green-and-gold grihapravesam printable cards.",
            accentColor = 0xFF1B5E20.toInt(),
            iconResId = R.drawable.ic_category_housewarming
        ),
        InvitationCategory(
            id = "puberty",
            title = "Puberty Ceremony",
            description = "Five elegant purple-and-rose printable ceremony cards.",
            accentColor = 0xFF6A1B9A.toInt(),
            iconResId = R.drawable.ic_category_puberty
        ),
        InvitationCategory(
            id = "engagement",
            title = "Engagement",
            description = "Five designed photo-card invites — photo on top, details below.",
            accentColor = 0xFFC2185B.toInt(),
            iconResId = R.drawable.ic_category_engagement
        ),
        InvitationCategory(
            id = "naming",
            title = "Naming Ceremony",
            description = "Five traditional namakaran photo-card invites for your little one.",
            accentColor = 0xFF6A1B9A.toInt(),
            iconResId = R.drawable.ic_category_naming
        ),
        InvitationCategory(
            id = "babyshower",
            title = "Baby Shower",
            description = "Five pastel photo-card invites to celebrate the mom-to-be.",
            accentColor = 0xFFEC407A.toInt(),
            iconResId = R.drawable.ic_category_babyshower
        )
    )

    private val templates: List<InvitationTemplate> = listOf(
        // Birthday
        photoTemplate(
            id = "birthday_01",
            categoryId = "birthday",
            title = "Pink & Gold Balloons",
            description = "Soft pink with realistic balloons and gold frame.",
            resId = R.drawable.bg_birthday_balloon_blast,
            primaryColor = 0xFFC2185B.toInt()
        ),
        photoTemplate(
            id = "birthday_02",
            categoryId = "birthday",
            title = "Navy Gold Party",
            description = "Premium navy and gold balloons with string lights.",
            resId = R.drawable.bg_birthday_navy_gold,
            primaryColor = 0xFF5D1532.toInt()
        ),
        photoTemplate(
            id = "birthday_03",
            categoryId = "birthday",
            title = "Kids Party Fun",
            description = "Bright cartoon balloons, cake, and confetti.",
            resId = R.drawable.bg_birthday_playful,
            primaryColor = 0xFFE65100.toInt()
        ),
        photoTemplate(
            id = "birthday_04",
            categoryId = "birthday",
            title = "Watercolor Roses",
            description = "Sage green watercolor with pink roses.",
            resId = R.drawable.bg_birthday_floral,
            primaryColor = 0xFFAD1457.toInt()
        ),
        photoTemplate(
            id = "birthday_05",
            categoryId = "birthday",
            title = "Purple Night Glow",
            description = "Glam purple bokeh with pink balloons.",
            resId = R.drawable.bg_birthday_purple_party,
            primaryColor = 0xFFFFB74D.toInt(),
            usesLightText = true
        ),

        // Wedding
        photoTemplate(
            id = "wedding_01",
            categoryId = "wedding",
            title = "Royal Kolam",
            description = "Maroon and gold kolam borders — classic kalyanam look.",
            resId = R.drawable.bg_wedding_kolam,
            primaryColor = 0xFF6E1634.toInt()
        ),
        photoTemplate(
            id = "wedding_02",
            categoryId = "wedding",
            title = "Temple Mandapam",
            description = "Temple arch, bells, and jasmine — ceremonial elegance.",
            resId = R.drawable.bg_wedding_temple,
            primaryColor = 0xFF5D1532.toInt()
        ),
        photoTemplate(
            id = "wedding_03",
            categoryId = "wedding",
            title = "Golden Mandapam",
            description = "Gold silk drape with maroon pillars — luxury wedding.",
            resId = R.drawable.bg_wedding_golden_mandapam,
            primaryColor = 0xFF3E0F24.toInt()
        ),
        photoTemplate(
            id = "wedding_04",
            categoryId = "wedding",
            title = "Sacred Knot",
            description = "Traditional thali motif with marigold accents.",
            resId = R.drawable.bg_wedding_sacred_knot,
            primaryColor = 0xFF8B1E3F.toInt()
        ),
        photoTemplate(
            id = "wedding_05",
            categoryId = "wedding",
            title = "Peacock Kalyanam",
            description = "Twin peacock crests in maroon and gold.",
            resId = R.drawable.bg_wedding_peacock,
            primaryColor = 0xFF4A0E1F.toInt()
        ),

        // Housewarming
        photoTemplate(
            id = "housewarming_01",
            categoryId = "housewarming",
            title = "Mango Toran",
            description = "Mango-leaf toran on fresh green and gold.",
            resId = R.drawable.bg_housewarming_mango_toran,
            primaryColor = 0xFF1B5E20.toInt()
        ),
        photoTemplate(
            id = "housewarming_02",
            categoryId = "housewarming",
            title = "Deepam Glow",
            description = "Glowing oil lamps — auspicious grihapravesam light.",
            resId = R.drawable.bg_housewarming_deepam,
            primaryColor = 0xFF0D3B1E.toInt()
        ),
        photoTemplate(
            id = "housewarming_03",
            categoryId = "housewarming",
            title = "Gopuram Welcome",
            description = "Temple doorway in green and gold.",
            resId = R.drawable.bg_housewarming_gopuram,
            primaryColor = 0xFF1F6F35.toInt()
        ),
        photoTemplate(
            id = "housewarming_04",
            categoryId = "housewarming",
            title = "Banana Leaf Feast",
            description = "Traditional banana-leaf border frame.",
            resId = R.drawable.bg_housewarming_banana_leaf,
            primaryColor = 0xFF2E7D32.toInt()
        ),
        photoTemplate(
            id = "housewarming_05",
            categoryId = "housewarming",
            title = "Prosperity Kolam",
            description = "Green kolam ring with marigold corners.",
            resId = R.drawable.bg_housewarming_kolam,
            primaryColor = 0xFF0B4F2A.toInt()
        ),

        // Puberty ceremony
        photoTemplate(
            id = "puberty_01",
            categoryId = "puberty",
            title = "Peacock Grace",
            description = "Purple and gold peacock crests — regal ceremony.",
            resId = R.drawable.bg_puberty_peacock,
            primaryColor = 0xFF6A1B9A.toInt()
        ),
        photoTemplate(
            id = "puberty_02",
            categoryId = "puberty",
            title = "Jasmine Garland",
            description = "Rose and jasmine garland on blush gold.",
            resId = R.drawable.bg_puberty_jasmine,
            primaryColor = 0xFFAD1457.toInt()
        ),
        photoTemplate(
            id = "puberty_03",
            categoryId = "puberty",
            title = "Lotus Bloom",
            description = "Lotus mandala in purple and pink.",
            resId = R.drawable.bg_puberty_lotus,
            primaryColor = 0xFF6A1B9A.toInt()
        ),
        photoTemplate(
            id = "puberty_04",
            categoryId = "puberty",
            title = "Royal Purple",
            description = "Deep violet with gold arch and florals.",
            resId = R.drawable.bg_puberty_royal_purple,
            primaryColor = 0xFFFFD54F.toInt(),
            usesLightText = true
        ),
        photoTemplate(
            id = "puberty_05",
            categoryId = "puberty",
            title = "Rose Festival",
            description = "Rose-gold sweep with lamps and roses.",
            resId = R.drawable.bg_puberty_rose_festival,
            primaryColor = 0xFF7B1FA2.toInt()
        ),

        // Engagement — traditional borders; rectangular photo on continuous cream panel
        photoTemplate(
            id = "engagement_01",
            categoryId = "engagement",
            title = "Green Toran Classic",
            description = "Traditional — toran top, leaf sides, diyas & kalash.",
            resId = R.drawable.bg_engagement_01,
            primaryColor = 0xFF2E7D32.toInt()
        ),
        photoTemplate(
            id = "engagement_02",
            categoryId = "engagement",
            title = "Marigold Royal",
            description = "Traditional — marigold top, maroon sides, kalash base.",
            resId = R.drawable.bg_engagement_02,
            primaryColor = 0xFFB71C1C.toInt()
        ),
        photoTemplate(
            id = "engagement_03",
            categoryId = "engagement",
            title = "Peacock Lotus",
            description = "Traditional — peacock side, lotus & kalash bottom.",
            resId = R.drawable.bg_engagement_03,
            primaryColor = 0xFF00695C.toInt()
        ),
        photoTemplate(
            id = "engagement_04",
            categoryId = "engagement",
            title = "Rose Temple Border",
            description = "Traditional — rose sides, marigold top, diya base.",
            resId = R.drawable.bg_engagement_04,
            primaryColor = 0xFFAD1457.toInt()
        ),
        photoTemplate(
            id = "engagement_05",
            categoryId = "engagement",
            title = "Mango Leaf Gold",
            description = "Mango leaves & marigold — unified toran, sides & kalash footer.",
            resId = R.drawable.bg_engagement_05,
            primaryColor = 0xFF33691E.toInt()
        ),

        // Naming ceremony — designed WebP photo cards (same layout as engagement)
        photoTemplate(
            id = "naming_01",
            categoryId = "naming",
            title = "Jasmine Cradle Pink",
            description = "Soft pink — jasmine toran, silver cradle & lotus.",
            resId = R.drawable.bg_naming_01,
            primaryColor = 0xFFAD1457.toInt()
        ),
        photoTemplate(
            id = "naming_02",
            categoryId = "naming",
            title = "Royal Blue Namakaran",
            description = "Blue & gold — marigold, ghungroo, turmeric rice.",
            resId = R.drawable.bg_naming_02,
            primaryColor = 0xFF1565C0.toInt()
        ),
        photoTemplate(
            id = "naming_03",
            categoryId = "naming",
            title = "Turmeric Blessing",
            description = "Auspicious yellow — mango leaves, kalash & jasmine.",
            resId = R.drawable.bg_naming_03,
            primaryColor = 0xFFF57F17.toInt()
        ),
        photoTemplate(
            id = "naming_04",
            categoryId = "naming",
            title = "Moon & Lotus",
            description = "Celestial lavender — stars, moon garland, lotus row.",
            resId = R.drawable.bg_naming_04,
            primaryColor = 0xFF5E35B1.toInt()
        ),
        photoTemplate(
            id = "naming_05",
            categoryId = "naming",
            title = "Tulsi Paladai Gold",
            description = "Tulsi toran & sage green — unified sides & golden paladai footer.",
            resId = R.drawable.bg_naming_05,
            primaryColor = 0xFF2E7D32.toInt()
        ),

        // Baby shower — illustrated WebP photo cards (pastel engagement-style art)
        photoTemplate(
            id = "babyshower_01",
            categoryId = "babyshower",
            title = "Blush Rose Pastel",
            description = "Soft pink — rose temple border with pastel blush tones.",
            resId = R.drawable.bg_babyshower_01,
            primaryColor = 0xFFE91E8C.toInt()
        ),
        photoTemplate(
            id = "babyshower_02",
            categoryId = "babyshower",
            title = "Sky Peacock Dream",
            description = "Sky blue — peacock lotus border in gentle pastel blue.",
            resId = R.drawable.bg_babyshower_02,
            primaryColor = 0xFF1976D2.toInt()
        ),
        photoTemplate(
            id = "babyshower_03",
            categoryId = "babyshower",
            title = "Mint Toran Joy",
            description = "Mint green — mango-leaf toran with soft celebration tones.",
            resId = R.drawable.bg_babyshower_03,
            primaryColor = 0xFF43A047.toInt()
        ),
        photoTemplate(
            id = "babyshower_04",
            categoryId = "babyshower",
            title = "Lavender Marigold",
            description = "Lavender — marigold royal border in lilac pastel.",
            resId = R.drawable.bg_babyshower_04,
            primaryColor = 0xFF7E57C2.toInt()
        ),
        photoTemplate(
            id = "babyshower_05",
            categoryId = "babyshower",
            title = "Peach Mango Blessing",
            description = "Warm peach — mango leaf gold in soft golden peach.",
            resId = R.drawable.bg_babyshower_05,
            primaryColor = 0xFFFF7043.toInt()
        )
    )

    private fun photoTemplate(
        id: String,
        categoryId: String,
        title: String,
        description: String,
        resId: Int,
        primaryColor: Int,
        usesLightText: Boolean = false
    ): InvitationTemplate {
        return InvitationTemplate(
            id = id,
            categoryId = categoryId,
            title = title,
            description = description,
            drawableResId = resId,
            backgroundResId = resId,
            primaryColor = primaryColor,
            usesLightText = usesLightText
        )
    }

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
