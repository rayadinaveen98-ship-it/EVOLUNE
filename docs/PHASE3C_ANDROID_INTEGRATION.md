# EVOLUNE — Phase 3C Android integration (source preview, 2026-11-11)

## Status
**Source implemented, Android APK build not yet verified.** This change adds Gradle modules and an Android Jetpack Compose client. The client is **not** a pixel-identical port of the approved HTML v0.3 design; it is a deliberately scoped native integration shell preserving the Stillwater + Atelier mood, and will require visual QA against the prototype.

## Architecture
- `:domain`: pure Kotlin `ChallengeEngine` and contract tests.
- `:core:data`: Room 2.8.x entities, DAO, repository, codec, Kotlin tests and instrumentation tests.
- `:app`: Android Activity + Compose screens wired to the actual Room repository; local-only, no network permission, no analytics or cloud account.

Pinned baseline: AGP 8.13.2, Kotlin and Compose compiler plugin 2.2.21, KSP 2.2.21-2.0.5, Gradle 8.13, Java 17, compile SDK 36, min SDK 26, Compose BOM 2026.09.00, Room 2.8.5.

These are a **proposed compatibility set** based on published release pages; only a successful Gradle build can validate all resolved dependencies. An Android SDK is not available in the authoring runtime, so it was not possible to run Gradle or produce an APK locally.

## Available native flows in this source
- **Today:** Read currently selected challenge, see factual status, log completed/partial/rested/explicit missed/backup, choose adjustment.
- **Challenges:** See saved challenges, create a challenge with a title, repeatable action, milestone number and type (calendar days vs full sessions), daily/weekdays/weekends recurrence, optional cue, fallback and growth area.
- **Details:** Actual Room + `ChallengeEngine` status for recent practice opportunities.
- **Recovery:** Future schedule revision or three-day pause without rewriting history.
- **Milestone review:** Accurate denominators and no false habit-formation claims.
- **Growth areas:** Add a user-defined area and associate it to a new challenge.
- **Journal:** Optional local reflections, mood/energy labels, edit and delete.
- **Insights:** Descriptive progress derived from latest revisions; unreported remains unknown, not a failure.

## Known deliberate gaps
- No notifications/voice alerts, cloud sync, backup/restore, account, export, calendar widget or health diagnosis.
- Existing v0.3 HTML prototype remains the visual benchmark. Native graphics use a static crop of the **previously generated** Stillwater landscape; interactive vegetation/particles/Blender scenes are not yet implemented.
- No permanent milestone review notes or completed/archived challenge UX yet; review screen is descriptive.
- No on-device migration test yet, no actual runtime performance/visual QA, no Android Accessibility audit.
- Native UI supports one current challenge as the primary focus; a challenge list is available.
- `gradle-wrapper.properties` is included, but wrapper scripts/JAR are not in this source package; CI installs a pinned Gradle release using `setup-gradle`. A developer with Gradle installed can execute `gradle :app:assembleDebug` and generate a standard wrapper with `gradle wrapper`.
- For privacy, cloud backup and device-transfer extraction are disabled. This means reinstall/device loss may lose entries, which needs explicit user disclosure and eventual export strategy before release.

## Verification completed locally
1. `kotlinc` domain checks — **passed**.
2. `kotlinc` storage codec roundtrip and malformed input checks — **passed**.
3. SQLite file restart/referential integrity contract checks — **passed**.
4. Static Android project structure checks — completed; **not equivalent to compilation**.

## Next mandatory verification in GitHub CI / Android Studio
1. Run `.github/workflows/android-ci.yml`: resolve deps, `gradle :domain:compileKotlin`, `gradle :app:assembleDebug`, publish APK artifact only **on success**.
2. Run connected Android Room instrumentation tests in `core/data/src/androidTest`, with an emulator/phone and report results.
3. Install app and verify create → log → force-stop → relaunch → preserve → edit → restart flows on hardware.
4. Compare actual emulator screenshots against HTML v0.3; repair layout and accessibility issues before calling the visual port approved.
5. Generate and commit Room's exported JSON schema after a successful KSP run; plan explicit migrations before schema updates.

## Notes
The repository contains development source rather than an APK. No build success, working Android persistence, or release readiness should be claimed until an Android toolchain compiles, launches, and tests it.
