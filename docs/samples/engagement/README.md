# Engagement samples — designed WebP photo cards

Five **illustrated WebP** backgrounds (`bg_engagement_01` … `05`) with:

- Traditional décor on **top, sides, and bottom**
- One **continuous cream panel** for photo + text (no divider line)
- **Rectangular** photo clip only (no arch, circle, oval, or gold ring)

| ID | Title |
|----|--------|
| engagement_01 | Green Toran Classic |
| engagement_02 | Marigold Royal |
| engagement_03 | Peacock Lotus |
| engagement_04 | Rose Temple Border |
| engagement_05 | Mango Leaf Gold |

## Replace artwork

1. Save designed PNGs as `tools/bg_sources/engagement_XX_source.png` (landscape ~3:2; converted with center-cover to 1080×1350).
2. `python3 tools/convert_photo_backgrounds.py`
3. `PREVIEW_OUTPUT_DIR=docs/samples/engagement ./gradlew testDebugUnitTest --tests "EngagementSampleExportTest"`
