# Baby shower samples

Five **original party-themed** WebP photo cards (not naming-ceremony art):

- Balloons, bunting, clouds, gifts, booties — no kalash, cradle, or tulsi motifs
- Same photo-card layout as engagement / naming

| ID | Title |
|----|--------|
| babyshower_01 | Pink Balloon Bunting |
| babyshower_02 | Blue Cloud Dream |
| babyshower_03 | Mint Gift Celebration |
| babyshower_04 | Lavender Moon Stars |
| babyshower_05 | Peach Ribbon Party |

Regenerate art: `python3 tools/generate_babyshower_sources.py` then `python3 tools/convert_photo_backgrounds.py --prefix babyshower`

Regenerate previews: `PREVIEW_OUTPUT_DIR=docs/samples/babyshower ./gradlew testDebugUnitTest --tests "BabyShowerSampleExportTest"`
