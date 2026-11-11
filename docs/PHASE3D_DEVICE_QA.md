# EVOLUNE — Phase 3D device QA gate
Date: 2026-11-11
Branch: `feature/android-phase3d-qa` · PR #1
Status: **Emulator workflow and native UI/Room test sources updated; no successful device run verified yet.**

## What is verified
- The Android debug APK **compiled** in GitHub Actions run [42950543150](https://github.com/rayadinaveen98-ship-it/EVOLUNE/actions/runs/42950543150).
- JVM challenge-engine and Room module unit-test compilation passed in the same build.
- Separate standalone Kotlin tests and Python SQLite reopen/integrity tests passed locally.
- The Room instrumentation test source exists, and `core/data` now declares the AndroidJUnitRunner.

## New emulator gate
Workflow: [android-device-qa.yml](../.github/workflows/android-device-qa.yml)

Expected steps:
1. Create a clean Android API 35 x86_64 emulator using the CI runner.
2. Execute `:core:data:connectedDebugAndroidTest` including `RoomPersistenceTest` and `RepositoryGuardTest`.
3. Execute `:app:connectedDebugAndroidTest` for Compose navigation and journal survival after Activity recreation; install/open the debug app.
4. Capture screenshots for Today, Challenges, Growth, Journal, Insights, and the first launch after a forced restart; use only synthetic QA content.
5. Upload the screenshot and instrumented-test reports as a workflow artifact.

The workflow configuration and committed tests are **not** evidence that steps 1–5 have succeeded. Check the actual GitHub Actions run and reports, then update this document only with verified results.

## Additional source fixes committed in this phase

- `RoomEvoluneRepository.recordPractice` now blocks records on paused dates and beyond calendar-day milestones.
- `addPause` blocks retroactive pauses and pauses after the calendar challenge has ended.
- `MainActivity` closes its Room instance on destruction and remembers DAO/repository per Compose lifetime.
- New `core/data/src/androidTest/.../RepositoryGuardTest.kt` checks pause, milestone and backdated-pause rejection using a deterministic clock.
- New `app/src/androidTest/.../NativeJourneyTest.kt` checks main-destination navigation and that private journal entries survive Activity recreation.
- Native screenshots are written during Compose UI tests and collected by the device-QA workflow.

**Offline recheck (2026-11-11):** Kotlin domain checks, Kotlin codec checks and Python SQLite reopen/referential-integrity contract checks all passed. These do not compile or execute the new Room/Compose test code.

**Current CI visibility:** GitHub commit-to-PR workflow lookup did not expose a completed emulator run in this session. Do not infer failure or success from that absence. The prior Android assemble run [42950543150](https://github.com/rayadinaveen98-ship-it/EVOLUNE/actions/runs/42950543150) still verifies only the older build commit.

## Manual quality gates before design approval
| Test | Expected behavior |
|---|---|
| Fresh install | No crash; Today's empty state visibly invites starting one challenge |
| Create | A 21-calendar-day weekday challenge accurately schedules only weekdays |
| Log correction | Record explicit missed then full completion for same day; latest displays completed, previous revision preserved |
| App restart | Same challenge, journal and history after normal close/reopen and force stop |
| Pause | Future dates paused; earlier records unchanged |
| Revise | New action applies from tomorrow, older history unchanged |
| No report | An unlogged due date shows **Unreported**, never auto-marked Missed |
| Journal | Create/edit/delete; mood and energy optional; linked entry preserved on challenge deletion |
| Sessions vs calendar | 21 full sessions distinct from 21 elapsed calendar days |
| Privacy | No account/telemetry/network permissions; backup disabled until reviewed |

## Visual parity against HTML v0.3
**Approved direction:** Stillwater + Atelier. Comparison must use native emulator screenshots and HTML v0.3 at comparable viewport sizes.

Screens to inspect: Today (empty and active), Challenge creation, Challenge detail, Recovery, Review, Growth, Journal, Insights. Verify artwork crop/fidelity, typography hierarchy, card padding, control spacing, color contrast, 320dp width, tablet adaptation, motion accessibility, and interaction fidelity.

**Known limitation:** The native project has a simplified Compose treatment and image hero. A successful compile or empty-state screenshot alone does not establish exact visual parity. No changes to the approved product behavior should be made solely to improve a screenshot.

## Release conditions
Do not merge this development PR or call the app release-ready until:
- Emulator Room and native UI tests pass;
- Native screenshot and touch-flow QA are complete;
- Persistence across process death is verified on actual Android;
- Backups/export/deletion policy is reviewed;
- User approves the resulting native visuals.

No personal journal data is used in QA; all examples are synthetic.
