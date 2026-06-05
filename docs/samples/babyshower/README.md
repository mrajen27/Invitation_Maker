# Baby shower samples

Five **illustrated WebP** photo cards (1080×1350 full-bleed portrait):

- Seamless cream center (no inner frame box or divider)
- Unique pastel illustrated borders per template
- Rectangular photo clip only

| ID | Title |
|----|--------|
| babyshower_01 | Blush Rose Pastel |
| babyshower_02 | Sky Peacock Dream |
| babyshower_03 | Mint Toran Joy |
| babyshower_04 | Lavender Marigold |
| babyshower_05 | Peach Mango Blessing |

## Regenerate

Masters: `tools/bg_sources/babyshower_XX_landscape_master.png`

```bash
python3 tools/import_landscape_masters.py --all-special   # or per --stem babyshower_01
python3 tools/regenerate_photo_cards.py --babyshower
PREVIEW_OUTPUT_DIR=docs/samples/babyshower ./gradlew testDebugUnitTest --tests "com.vaangainvite.core.image.BabyShowerSampleExportTest"
```
