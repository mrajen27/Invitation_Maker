# Naming ceremony templates

| ID | Title | Style |
|----|-------|--------|
| `naming_01` | Heritage Silk | **Kept** — red & gold heritage (user approved) |
| `naming_02` | Gopuram Blessing | Tamil temple — gopuram, bells, kolam |
| `naming_03` | Temple Mandapam | Tamil temple — pillars, vilakku, toran |
| `naming_04` | Pastel Sky | Light pastel — blue/lavender clouds |
| `naming_05` | Mint Blossom | Light pastel — mint, blush, jasmine |

Regenerate all previews:

```bash
PREVIEW_OUTPUT_DIR=docs/samples ./gradlew testDebugUnitTest --tests "com.vaangainvite.core.image.NamingCeremonyPreviewExportTest"
for i in 1 2 3 4 5; do mv -f docs/samples/naming_0${i}.png docs/samples/naming_0${i}_sample.png; done
```
