# Engagement photo-card samples

Five **designed WebP** backgrounds (1080×1350) with the photo-card pipeline:

- WebP artwork in `drawable-nodpi/bg_engagement_*.webp`
- Portrait clipped to the **top ceremony arch** (`EngagementPhotoPlacement`)
- Event copy in the **cream band below** (`InvitationLayout` per template)

| ID | Title | Style |
|----|--------|--------|
| engagement_01 | Banana Leaf Toran | Traditional |
| engagement_02 | Marigold Royale | Traditional |
| engagement_03 | Cream Kolam Glow | Traditional |
| engagement_04 | Teal Lantern Night | Modern (light text) |
| engagement_05 | Rose Gold Vows | Modern |

Regenerate previews:

```bash
PREVIEW_OUTPUT_DIR=docs/samples/engagement ./gradlew testDebugUnitTest --tests "com.vaangainvite.core.image.EngagementSampleExportTest"
```
