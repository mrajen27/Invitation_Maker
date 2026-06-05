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

## Regenerate portrait assets

```bash
python3 tools/build_portrait_sources.py --prefix engagement --restore-landscape
python3 tools/generate_new_photo_cards.py   # engagement_05 only
python3 tools/convert_photo_backgrounds.py --prefix engagement
PREVIEW_OUTPUT_DIR=docs/samples/engagement ./gradlew testDebugUnitTest --tests "EngagementSampleExportTest"
```
