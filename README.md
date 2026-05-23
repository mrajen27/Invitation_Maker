# Vaanga Invite

Vaanga Invite is a Kotlin and Jetpack Compose Android app for creating South Indian invitation cards and sharing them on WhatsApp.

## MVP features

- Home screen with invitation categories:
  - Birthday
  - Wedding
  - Housewarming
  - Puberty Ceremony
- Traditional splash/loading screen with spinner to avoid a blank white startup screen.
- Traditional home screen background with toran, kolam, and floral motifs.
- Template selection screen backed by local vector assets, with 20 more varied designs per MVP category.
- Invitation editor with fields for name on invitation, occasion/event title, date picker, time, venue, mobile number for queries/location help, and an additional message.
- Quick additional-message chips with English/Tamil suggestions, category-specific messages, tone filters, emoji-friendly input, image-safe character guidance, and a live message preview card.
- English/Tamil invitation language selector for generated card headings and labels.
- Optional photo upload to place a family, couple, child, or ceremony photo into the generated invitation, with EXIF orientation correction for phone photos.
- Invitation image generation using Android `Bitmap` and `Canvas`.
- Save generated invitations to the device gallery.
- Share generated invitations to WhatsApp Chat or WhatsApp Status, with a fallback Android share sheet if WhatsApp is not installed.
- Material 3 UI built with Jetpack Compose.
- MVVM architecture with repository-backed templates and a ViewModel-driven UI state.

## Project structure

```text
.
├── app/
│   ├── build.gradle.kts
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/vaangainvite/
│       │   ├── MainActivity.kt
│       │   ├── core/
│       │   │   ├── image/InvitationImageGenerator.kt
│       │   │   └── share/InvitationShare.kt
│       │   ├── data/
│       │   │   ├── model/
│       │   │   └── repository/TemplateRepository.kt
│       │   └── ui/
│       │       ├── navigation/VaangaNavHost.kt
│       │       ├── screens/
│       │       ├── theme/
│       │       ├── viewmodel/InviteViewModel.kt
│       │       └── VaangaInviteApp.kt
│       └── res/
│           ├── drawable/template_*.xml
│           ├── mipmap-anydpi-v26/
│           ├── values/
│           └── xml/
├── build.gradle.kts
├── gradle.properties
└── settings.gradle.kts
```

## Setup

1. Open the project in Android Studio.
2. Install Android SDK 35 if prompted.
3. Sync Gradle.
4. Run the `app` configuration on an emulator or Android device.

Command line build:

```bash
./gradlew assembleDebug
```

## Implementation notes

- `TemplateRepository` owns the MVP category and template metadata.
- `InviteViewModel` exposes a single `StateFlow<InviteUiState>` for the Compose screens.
- `VaangaInviteApp` shows a short traditional loading screen while the app UI initializes, and the Android theme uses the same traditional background during cold start.
- Category selection pre-fills an editable occasion title such as Birthday Celebration, Wedding Invitation, Housewarming Ceremony, or Puberty Ceremony.
- Generated cards render in the order: greeting, name on invitation, occasion/event title, date, time, venue, and optional contact number.
- `InvitationImageGenerator` draws the selected local template, optional uploaded photo, and editor text into a PNG bitmap.
- Template assets use multiple layout families such as arches, temple panels, garlands, mandalas, lamps, diagonal panels, and category-specific motifs.
- Tamil text rendering uses the bundled Noto Sans Tamil font.
- Photo selection uses Android's system photo picker and keeps the selected URI in editor state.
- Quick message chips are reusable Compose components that filter by language, event category, and tone before updating the editable message field while keeping the message fully editable.
- Gallery saving uses `MediaStore`; Android 9 and below request `WRITE_EXTERNAL_STORAGE` at runtime.
- WhatsApp sharing uses `ACTION_SEND` image intents with `com.whatsapp` as the preferred package and separate chat/status entry points before falling back to a chooser.

## Third-party assets

- Noto Sans Tamil font from Google Fonts, licensed under the SIL Open Font License 1.1.
