# Vaanga Invite - Agent Instructions

## Cursor Cloud specific instructions

This is a pure Android/Kotlin app (Jetpack Compose, Material 3, MVVM) with no backend, database, or external services. All functionality is client-side.

### Environment

- **JDK 21** is pre-installed on the VM.
- **Android SDK** lives at `/opt/android-sdk` with `ANDROID_HOME` set in `~/.bashrc`. Platform 35, build-tools 35.0.0, and platform-tools are installed.
- The Gradle wrapper (`./gradlew`) auto-downloads Gradle 8.10.2 on first run.

### Common commands

See `README.md` for project structure and setup. Key Gradle tasks:

| Task | Command |
|---|---|
| Build debug APK | `./gradlew assembleDebug` |
| Run lint | `./gradlew lintDebug` |
| Run unit tests | `./gradlew testDebugUnitTest` |

### Known issues

- `./gradlew lintDebug` exits with failure due to 3 pre-existing `FullBackupContent` errors in `backup_rules.xml` and `data_extraction_rules.xml`. The 10 warnings are outdated dependency versions. These are not introduced by agents.
- No unit or instrumented test source files exist yet (`src/test/` and `src/androidTest/` are empty).
- The first Kotlin compile daemon startup may log `terminated unexpectedly on startup attempt #1` — this is a known Kotlin daemon issue and the build still succeeds.

### Caveats

- This is a mobile-only app; there is no web UI or dev server to run. The "hello world" validation is building `app-debug.apk` successfully.
- Instrumented tests and UI testing require an Android emulator or physical device, which is not available in the cloud VM.
