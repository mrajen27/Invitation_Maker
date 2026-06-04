# Baby shower samples

Five **designed WebP** photo-card invites (same layout as engagement / naming):

- Rectangular photo on continuous cream panel
- Traditional décor top / sides / bottom with pastel balloon accents
- No divider between photo and text

| ID | Title |
|----|--------|
| babyshower_01 | Pink Cloud Blessing |
| babyshower_02 | Blue Balloon Dream |
| babyshower_03 | Sunshine Marigold |
| babyshower_04 | Lavender Moon Stars |
| babyshower_05 | Rose Garden Party |

Regenerate sources: `python3 tools/generate_babyshower_sources.py` then `python3 tools/convert_photo_backgrounds.py --prefix babyshower`

Regenerate previews: `PREVIEW_OUTPUT_DIR=docs/samples/babyshower ./gradlew testDebugUnitTest --tests "BabyShowerSampleExportTest"`
