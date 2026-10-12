# Nexo Banca Agent Notes

## Project Boundary

- This file governs the entire `banca mobile/` tree. The actual Gradle root is `banca_movile/android-app`; open that directory in Android Studio and run all Gradle commands there, never from the outer directory or `app/` alone.

## Toolchain And Verification

- Use the checked-in Windows wrapper (`.\gradlew.bat`) with JDK 17+ and Android SDK 37. Java source/target compatibility is 11; that is separate from the JDK running Gradle.
- Unit tests: `.\gradlew.bat :app:testDebugUnitTest`.
- Current test class only: `.\gradlew.bat :app:testDebugUnitTest --tests "com.nexo.banca.domain.BankingRulesTest"`.
- Static analysis: `.\gradlew.bat :app:lintDebug`.
- Debug build: `.\gradlew.bat :app:assembleDebug`.
- Full delivery check: `.\gradlew.bat clean :app:testDebugUnitTest :app:lintDebug :app:generarApkDemo`.
- Delivery APK: `.\gradlew.bat :app:generarApkDemo`. It copies `app/build/outputs/apk/debug/app-debug.apk` to `../distribucion/Nexo-Demo.apk`.
- There is no configured formatter, separate typecheck, CI workflow, or `androidTest` suite.

## Architecture

- This is a single Android module (`:app`) and a local-only educational demo. `ui/NexoApp.kt` is only the auth/navigation shell; screens are split across `ui/auth`, `ui/home`, `ui/more`, `ui/transfer`, `ui/qr`, `ui/profile`, `ui/payments`, and `ui/settings`, with shared primitives in `ui/components`. Banking data flows through `NexoViewModel` -> `NexoRepository` -> Room in `data/local`; app preferences use DataStore through `data/preferences/UserPreferencesRepository.kt`. There is no backend or network layer.
- Navigation uses manual Compose state rather than Navigation Compose. Login state is UI-only, and the intentional demo credentials live in `domain/BankingRules.kt`.
- Settings is accessible from the Home gear and More. Maintain the back stack for nested Settings/Profile/Cards routes. Specific `PaymentDestination` shortcuts show only their section; only `OVERVIEW` displays all three payment tabs.
- Demo reset requires confirmation. `NexoRepository.resetDemo()` deletes and reseeds banking data in one Room transaction; DataStore preferences reset separately afterward. DAO delete queries do not change the database schema. App version in Settings comes from BuildConfig.
- Biometric login uses AndroidX `BiometricPrompt` from `ui/auth/LoginScreen.kt` and requires `MainActivity` to remain a `FragmentActivity`. By default it opens once after preferences load whenever the login screen is entered; cancellation must never loop and password fallback must remain. It is only a local demo gate, not bank authentication or data encryption.
- Theme mode, automatic biometric prompting, and initial balance visibility are DataStore preferences. Keep them outside Room, preserve defaults `SYSTEM`/enabled/enabled, and wait for `UserPreferences.loaded` before automatic prompting.
- Keep balance, movement, bill, and savings mutations atomic with Room transactions. Store COP amounts as integer `Long` values, never floating point.
- Payment UI uses `PaymentUiModel`, not Room entities. Keep the legacy credit/service ID rule centralized in `domain/PaymentType.kt`; changing Room just to classify payments requires a migration.
- `NexoRepository.initialize()` seeds the account, movements, and card settings only when no account exists, but backfills each missing demo bill independently. Reset all persisted demo state with `adb shell pm clear com.nexo.banca.demo`.
- Keep default holder, account number, balance, and savings in `domain/DemoData.kt`; UI fallbacks and repository seeds must not duplicate those literals.

## Room And Generated Files

- KSP exports Room schemas to `app/schemas`; unlike normal build output, these JSON files are reviewed source artifacts.
- The database is `nexo_demo.db`, version 1, with no migration or destructive-migration fallback. A schema change requires a version bump, an explicit migration strategy, and review of the regenerated schema.

## Startup

- Keep `com.nexo.banca.MainActivity` registered with `MAIN`/`LAUNCHER` and `android:exported="true"` in the source `app/src/main/AndroidManifest.xml`. A build can succeed without a launchable activity, so do not validate startup from generated merged manifests alone.

## Demo Invariants

- Preserve the explicit educational/demo positioning; the app must not imply real banking or real-money operation.
- Preserve screenshot blocking (`FLAG_SECURE`) and disabled backups unless a requirement explicitly changes them.
- Keep biometric hardware optional in the manifest so devices without enrolled biometrics can use the demo credentials.
- QR transfers use `nexo://transfer` or `nexo://receive`. Accounts contain 6-20 digits, and transfer amounts are limited to `1..5_000_000`.

## Documentation

- Every user-visible behavior or setup change must update `README.md`, `docs/GUIA_DESARROLLO.md`, and all affected editable sources in `docs/src/*.html`; then run `.\banca_movile\scripts\generar-documentos.ps1` from the repository root and review all three regenerated PDFs. Do not consider delivery complete until documentation, PDFs, tests, lint, and `banca_movile/distribucion/Nexo-Demo.apk` are synchronized.
