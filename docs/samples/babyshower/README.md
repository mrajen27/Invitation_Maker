# Baby shower samples

Five **illustrated WebP** photo-card invites (same pipeline as engagement / naming):

- Designed landscape art → center-cover WebP at 1080×1350
- Pastel-graded from distinct engagement border artwork (not flat vector)
- Rectangular photo on continuous cream panel; text below

| ID | Title |
|----|--------|
| babyshower_01 | Blush Rose Pastel |
| babyshower_02 | Sky Peacock Dream |
| babyshower_03 | Mint Toran Joy |
| babyshower_04 | Lavender Marigold |
| babyshower_05 | Peach Mango Blessing |

Regenerate sources: `python3 tools/generate_babyshower_sources.py` then `python3 tools/convert_photo_backgrounds.py --prefix babyshower`

Replace with custom designer PNGs: save as `tools/bg_sources/babyshower_XX_source.png` and re-run convert only.

Regenerate previews: `PREVIEW_OUTPUT_DIR=docs/samples/babyshower ./gradlew testDebugUnitTest --tests "BabyShowerSampleExportTest"`
