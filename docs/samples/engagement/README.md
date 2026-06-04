# Engagement template samples (new designs)

Five **original** engagement backgrounds — **3 traditional** + **2 modern** — not reused from housewarming or other categories.

| ID | Style | Title |
|----|--------|--------|
| engagement_01 | Traditional | Gopuram Gold |
| engagement_02 | Traditional | Jasmine Mandapam |
| engagement_03 | Traditional | Saffron Kolam |
| engagement_04 | Modern | Blush Minimal |
| engagement_05 | Modern | Midnight Deco |

Files `engagement_XX_with_photo.png` are generated at 1080×1350 with a sample portrait in the top arch and full event text below.

Regenerate:

```bash
python3 tools/generate_engagement_backgrounds.py
python3 tools/convert_photo_backgrounds.py
PREVIEW_OUTPUT_DIR=docs/samples/engagement ./gradlew testDebugUnitTest --tests "com.vaangainvite.core.image.EngagementSampleExportTest"
```
