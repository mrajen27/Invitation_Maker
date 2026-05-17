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
            id = "birthday_lotus",
            categoryId = "birthday",
            title = "Lotus Celebration",
            description = "Pink lotus borders with festive gold accents.",
            drawableResId = R.drawable.template_birthday_lotus,
            primaryColor = 0xFF8B1E3F.toInt()
        ),
        InvitationTemplate(
            id = "birthday_festive_arch",
            categoryId = "birthday",
            title = "Festive Birthday Arch",
            description = "Layered arch, toran leaves, and cheerful pink-gold details.",
            drawableResId = R.drawable.template_birthday_festive_arch,
            primaryColor = 0xFF8B1E3F.toInt()
        ),
        InvitationTemplate(
            id = "wedding_kolam",
            categoryId = "wedding",
            title = "Kolam Muhurtham",
            description = "Classic maroon, gold, and kolam-inspired details.",
            drawableResId = R.drawable.template_wedding_kolam,
            primaryColor = 0xFF8B1E3F.toInt()
        ),
        InvitationTemplate(
            id = "wedding_temple_bells",
            categoryId = "wedding",
            title = "Temple Bells",
            description = "Temple-inspired roofline, bells, and a rich maroon frame.",
            drawableResId = R.drawable.template_wedding_temple_bells,
            primaryColor = 0xFF6E1634.toInt()
        ),
        InvitationTemplate(
            id = "housewarming_mango",
            categoryId = "housewarming",
            title = "Mango Leaf Home",
            description = "Green and gold card with auspicious mango leaves.",
            drawableResId = R.drawable.template_housewarming_mango,
            primaryColor = 0xFF2E7D32.toInt()
        ),
        InvitationTemplate(
            id = "housewarming_deepam",
            categoryId = "housewarming",
            title = "Deepam Grihapravesam",
            description = "House motif with lamps, mango leaves, and green-gold accents.",
            drawableResId = R.drawable.template_housewarming_deepam,
            primaryColor = 0xFF1F6F35.toInt()
        ),
        InvitationTemplate(
            id = "puberty_floral",
            categoryId = "puberty",
            title = "Floral Blessings",
            description = "Soft floral frame for a puberty ceremony invitation.",
            drawableResId = R.drawable.template_puberty_floral,
            primaryColor = 0xFF9C27B0.toInt()
        ),
        InvitationTemplate(
            id = "puberty_peacock",
            categoryId = "puberty",
            title = "Peacock Blessings",
            description = "Peacock-inspired floral frame with jewel-toned highlights.",
            drawableResId = R.drawable.template_puberty_peacock,
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
