# Baby shower samples

Five **illustrated WebP** photo cards (1080×1350 full-bleed portrait):

- Continuous cream center for photo + text
- Pastel-graded traditional borders (distinct from engagement/naming)
- Rectangular photo clip only

| ID | Title |
|----|--------|
| babyshower_01 | Blush Rose Pastel |
| babyshower_02 | Sky Peacock Dream |
| babyshower_03 | Mint Toran Joy |
| babyshower_04 | Lavender Marigold |
| babyshower_05 | Peach Mango Blessing |

## Regenerate

```bash
python3 tools/generate_babyshower_templates.py
python3 tools/convert_photo_backgrounds.py --prefix babyshower
PREVIEW_OUTPUT_DIR=docs/samples/babyshower ./gradlew testDebugUnitTest --tests "BabyShowerSampleExportTest"
```

Requires engagement `*_landscape_backup.png` masters and portrait layout refs on `main`.
