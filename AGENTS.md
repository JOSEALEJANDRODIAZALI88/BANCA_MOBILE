# Nexo Banca Agent Notes

## Project Boundary

- This file governs the entire `banca mobile/` tree. The actual Gradle root is `banca_movile/android-app`; open that directory in Android Studio and run all Gradle commands there, never from the outer directory or `app/` alone.
- The enclosing Git repository is the whole `C:\Users\DIAZ` profile, while this project is currently untracked. Scope Git commands to `Desktop/banca mobile` to avoid unrelated files and permission warnings.

## Toolchain And Verification

- Use the checked-in Windows wrapper (`.\gradlew.bat`), JDK 17+, and Android SDK 37. The app's Java/Kotlin bytecode target remains Java 11; that is separate from Gradle's runtime JDK.
- Unit tests: `.\gradlew.bat :app:testDebugUnitTest`.
- Current test class only: `.\gradlew.bat :app:testDebugUnitTest --tests "com.nexo.banca.domain.BankingRulesTest"`.
- Static analysis: `.\gradlew.bat :app:lintDebug`.
- Debug build: `.\gradlew.bat :app:assembleDebug`.
- Full delivery check: `.\gradlew.bat clean :app:testDebugUnitTest :app:lintDebug :app:assembleDebug`.
- Delivery APK: `.\gradlew.bat :app:generarApkDemo`. It copies `app/build/outputs/apk/debug/app-debug.apk` to `../distribucion/Nexo-Demo.apk`.
- There is no separate formatter, typecheck task, CI workflow, or instrumented test suite.

## Architecture

- This is a single Android module (`:app`) and a local-only educational demo. The execution flow is `MainActivity` -> Compose UI in `ui/NexoApp.kt` -> `NexoViewModel` -> `NexoRepository` -> Room in `data/local`; there is no backend or network layer.
- Navigation uses manual Compose state rather than Navigation Compose. Login state is UI-only, and the intentional demo credentials live in `domain/BankingRules.kt`.
- Keep balance, movement, bill, and savings mutations atomic with Room transactions. Store COP amounts as integer `Long` values, never floating point.
- `NexoRepository.initialize()` seeds data only when no account exists. Reset the demo with `adb shell pm clear com.nexo.banca.demo`.

## Room And Generated Files

- KSP exports Room schemas to `app/schemas`. These JSON files are reviewed source artifacts, not disposable build output.
- The database is `nexo_demo.db`, version 1, with no migrations. A schema change requires a version bump, an explicit migration decision, and review of the regenerated schema.
- Never commit `local.properties`, `.gradle/`, `build/`, IDE state, or generated APKs.
- Do not use existing APKs or `app/build` contents as proof that current source works; clean builds can expose stale generated state.

## Startup

- Keep `com.nexo.banca.MainActivity` registered with `MAIN`/`LAUNCHER` and `android:exported="true"` in the source `app/src/main/AndroidManifest.xml`. A build can succeed without a launchable activity, so do not validate startup from generated merged manifests alone.

## Demo Invariants

- Preserve the explicit educational/demo positioning; the app must not imply real banking or real-money operation.
- Preserve screenshot blocking (`FLAG_SECURE`) and disabled backups unless a requirement explicitly changes them.
- QR transfers use `nexo://transfer` or `nexo://receive`. Accounts contain 6-20 digits, and transfer amounts are limited to `1..5_000_000`.
