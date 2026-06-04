# Engagement ceremony templates (test branch)

Five printable WebP photo-card designs with a **top arch** for couple photos and a cream text panel (similar layout to housewarming cards).

| ID | Title | Motifs |
|----|-------|--------|
| `engagement_01` | Banana Leaf Toran | Green banana leaves, kalash, vilakku, kolam |
| `engagement_02` | Marigold Royale | Maroon marigolds, banana trees, kalash |
| `engagement_03` | Cream Kolam Glow | Cream arch, bells, vilakku, kalash |
| `engagement_04` | Teal Lantern Night | Teal & gold lanterns (light text on card) |
| `engagement_05` | Rose Gold Vows | Rose-gold arch, rings, diyas |

Regenerate previews:

```bash
PREVIEW_OUTPUT_DIR=docs/samples/engagement ./gradlew testDebugUnitTest \
  --tests "com.vaangainvite.core.image.EngagementPreviewExportTest"
```
