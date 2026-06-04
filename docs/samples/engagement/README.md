# Engagement samples

Five **new** illustrated WebP templates using the **same photo-card implementation**:

- `photoTemplate` + `bg_engagement_XX.webp`
- `EngagementPhotoPlacement` (arch mask)
- `InvitationLayout` (per-template text band)

| ID | Title |
|----|--------|
| engagement_01 | Green Toran Blessing |
| engagement_02 | Marigold Heritage |
| engagement_03 | Peacock Lotus Garden |
| engagement_04 | Blush Rose Lights |
| engagement_05 | Sage Garden Glow |

Replace art: add PNGs under `tools/bg_sources/engagement_XX_source.png`, then:

```bash
python3 tools/convert_photo_backgrounds.py
PREVIEW_OUTPUT_DIR=docs/samples/engagement ./gradlew testDebugUnitTest --tests "EngagementSampleExportTest"
```
