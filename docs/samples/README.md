# Naming ceremony sample images

| File | Description |
|------|-------------|
| `naming_01_sample.png` | **Template preview** — all text filled, empty top ring (matches in-app look before photo upload). |
| `naming_01_with_photo.png` | Same fields with a sample baby photo in the top medallion. |

Regenerate:

```bash
PREVIEW_OUTPUT_DIR=docs/samples ./gradlew testDebugUnitTest \
  --tests "com.vaangainvite.core.image.NamingCeremonyPreviewExportTest"

PREVIEW_OUTPUT_DIR=docs/samples ./gradlew testDebugUnitTest \
  --tests "com.vaangainvite.core.image.NamingFullSampleExportTest"
```

Copy `NamingCeremonyPreviewExportTest` output `naming_01.png` to `naming_01_sample.png` if you only run the first command.
