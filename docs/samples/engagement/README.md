# Engagement samples — traditional panel layout

All five cards share one layout: **rectangular photo** on a **continuous cream panel** (no arch, no round ring, no line between photo and text). Décor sits on the **top, sides, and bottom** only.

| ID | Title | Theme |
|----|--------|--------|
| engagement_01 | Green Toran Classic | Toran, banana leaves, diyas |
| engagement_02 | Marigold Royal | Marigold, maroon sides, kalash |
| engagement_03 | Peacock Lotus | Peacock, lotus row |
| engagement_04 | Rose Temple Border | Roses, marigold, diyas |
| engagement_05 | Mango Leaf Gold | Mango leaves, kalash |

Regenerate backgrounds: `python3 tools/generate_engagement_backgrounds.py && python3 tools/convert_photo_backgrounds.py`

Regenerate samples: `PREVIEW_OUTPUT_DIR=docs/samples/engagement ./gradlew testDebugUnitTest --tests "EngagementSampleExportTest"`
