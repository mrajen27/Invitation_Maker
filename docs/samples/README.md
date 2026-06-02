# Naming ceremony sample images

| File | Description |
|------|-------------|
| `naming_01_sample.png` | **Palna & Marigold** — horizontal oval at top, **full golden cradle** visible bottom-left, kolam floor. Text only (empty oval). |
| `naming_01_with_photo.png` | Same card with sample baby photo in the horizontal oval. |

**Note:** `naming_01` uses the original artwork with the complete palna; a later redesign had cropped the cradle at the left edge — that version was reverted.

Regenerate:

```bash
PREVIEW_OUTPUT_DIR=docs/samples ./gradlew testDebugUnitTest \
  --tests "com.vaangainvite.core.image.NamingCeremonyPreviewExportTest"

PREVIEW_OUTPUT_DIR=docs/samples ./gradlew testDebugUnitTest \
  --tests "com.vaangainvite.core.image.NamingFullSampleExportTest"
```

Copy `NamingCeremonyPreviewExportTest` output `naming_01.png` to `naming_01_sample.png` if you only run the first command.
