# Naming ceremony template previews

These match the five **Naming Ceremony** designs shown in chat (branch `cursor/new-event-templates-6153`).

| ID | Title | Preview file |
|----|-------|----------------|
| `naming_01` | Palna & Marigold | `naming_01_sample.png` |
| `naming_02` | Peacock Lotus | `naming_02_sample.png` |
| `naming_03` | Kalash Toran | `naming_03_sample.png` |
| `naming_04` | Moon & Jasmine | `naming_04_sample.png` |
| `naming_05` | Tulsi Wreath | `naming_05_sample.png` |

Each PNG is a filled invite (no photo) on the matching `bg_naming_*.webp` artwork.

Regenerate:

```bash
PREVIEW_OUTPUT_DIR=docs/samples ./gradlew testDebugUnitTest \
  --tests "com.vaangainvite.core.image.NamingCeremonyPreviewExportTest"
# then rename naming_XX.png → naming_XX_sample.png
```

With photo (`naming_01` only):

```bash
PREVIEW_OUTPUT_DIR=docs/samples ./gradlew testDebugUnitTest \
  --tests "com.vaangainvite.core.image.NamingFullSampleExportTest"
```
