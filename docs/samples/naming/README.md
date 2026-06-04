# Naming ceremony samples

Five **designed WebP** namakaran photo cards (same layout as engagement):

- Rectangular baby photo on continuous cream panel
- Traditional décor top / sides / bottom
- No arch, no round ring, no divider between photo and text

| ID | Title |
|----|--------|
| naming_01 | Jasmine Cradle Pink |
| naming_02 | Royal Blue Namakaran |
| naming_03 | Turmeric Blessing |
| naming_04 | Moon & Lotus |
| naming_05 | Tulsi Paladai Gold |

Sources are landscape ~3:2; `convert_photo_backgrounds.py` uses center-cover to fill 1080×1350 (no top/bottom letterbox).

Regenerate: `PREVIEW_OUTPUT_DIR=docs/samples/naming ./gradlew testDebugUnitTest --tests "NamingSampleExportTest"`
