# EVOLUNE — Phase 3C Android build gate (2026-11-11)

**Working branch:** `feature/android-phase3c`  
**Baseline:** Existing native Compose screens + Room/engine from main (preserved, not rewritten).  
**Decision:** Repair the actual Android CI build before expanding screen features.
**Update 2026-11-11:** Verified GitHub Actions run [42950543150](https://github.com/rayadinaveen98-ship-it/EVOLUNE/actions/runs/42950543150) completed successfully for domain/data JVM tests, Android compilation and debug APK upload. Android emulator/device execution is a separate, unverified gate.

## Audit

The public GitHub Actions workflow was configured with:
- Android Gradle Plugin **8.13.2**, Kotlin **2.2.21**, KSP **2.2.21-2.0.5**;
- Android `compileSdk = 36`, Gradle **8.13**;
- Compose BOM **2026.09.00**.

The previous CI run failed AAR metadata checks because Compose **1.12.1** from that BOM requires `compileSdk >= 37` and AGP **9.1+**. An image preview or JVM-only Kotlin check is not an APK build.

## Changes made on this review branch

1. Pin Compose BOM to **2025.10.01** while retaining the already established SDK 36 / AGP 8.13.2 / Kotlin 2.2.21 stack.
2. Add JUnit test dependencies and test entrypoints for the existing pure Kotlin domain and codec checks.
3. Add `RoomPersistenceTest.kt`, an Android instrumented test source covering close/reopen, append-only corrected practice logs, and journal preservation after challenge deletion.
4. Run `gradle :domain:test :core:data:testDebugUnitTest` as part of CI.
5. Keep Android source and official Stillwater + Atelier imagery intact; do not replace existing UI with an alternate implementation.
6. Update SDK setup to include Build Tools 36.0.0.

## Verification checklist

- [x] Local JVM domain verification (standalone compiler)
- [x] Local storage codec round trip verification
- [x] Local SQLite schema/integrity/reopen contract verification
- [x] Remote CI JVM/Room compile and app-debug APK build — verified in run [42950543150](https://github.com/rayadinaveen98-ship-it/EVOLUNE/actions/runs/42950543150)
- [ ] Emulator / device Room instrumentation test — workflow configured in [Phase 3D QA](PHASE3D_DEVICE_QA.md), actual run result not verified
- [ ] Native Android screenshots and visual parity check — screenshot automation configured for first-launch state; output not yet verified
- [ ] Notification reliability and private-data export/delete review
- [ ] Final installable APK handoff

## Why this is a review branch

Android-specific source is not equivalent to compiled, installed, and device-tested software. The branch may contain build fixes that still require CI correction. Merge only after the build and the relevant functional checks are green.

## Build references

- Official Compose BOM guidance: https://developer.android.com/develop/ui/compose/bom
- AGP / Gradle / API-level compatibility: https://developer.android.com/build/releases/about-agp
- Room releases: https://developer.android.com/jetpack/androidx/releases/room
- CI: `.github/workflows/android-ci.yml`
