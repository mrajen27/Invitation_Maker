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

Portrait sources are built from landscape masters by **stretching the side borders and center cream panel** (top toran and bottom lotus keep their original height — no letterbox, no mirrored seams).

**Exception:** `engagement_05` and `naming_05` use a dedicated compositor because their landscape art embeds footer décor inside the cream panel:

```bash
python3 tools/generate_portrait_05_templates.py
```

1. Landscape masters: `tools/bg_sources/engagement_XX_source_landscape_backup.png`
2. Build 1080×1350 sources: `python3 tools/build_portrait_sources.py --prefix engagement --restore-landscape`
3. WebP: `python3 tools/convert_photo_backgrounds.py --prefix engagement`
4. Samples: `PREVIEW_OUTPUT_DIR=docs/samples/engagement ./gradlew testDebugUnitTest --tests "EngagementSampleExportTest"`
