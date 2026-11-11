# EVOLUNE — Phase 3D device QA gate
Date: 2026-11-11
Branch: `feature/android-phase3c` · PR #1
Status: **emulator workflow configured, execution result not verified in this document**.

## What is verified
- The Android debug APK **compiled** in GitHub Actions run [42950543150](https://github.com/rayadinaveen98-ship-it/EVOLUNE/actions/runs/42950543150).
- JVM challenge-engine and Room module unit-test compilation passed in the same build.
- Separate standalone Kotlin tests and Python SQLite reopen/integrity tests passed locally.
- The Room instrumentation test source exists, and `core/data` now declares the AndroidJUnitRunner.

## New emulator gate
Workflow: [android-device-qa.yml](../.github/workflows/android-device-qa.yml)

Expected steps:
1. Create a clean Android API 35 x86_64 emulator using the CI runner.
2. Execute `:core:data:connectedDebugAndroidTest` including `RoomPersistenceTest`.
3. Install and open the debug app to detect launch failures.
4. Capture a screenshot of the freshly launched Today screen with no real user data.
5. Upload the screenshot and instrumented-test reports as a workflow artifact.

The workflow configuration is **not** evidence that steps 1–5 have succeeded. Check the actual GitHub Actions run and reports, then update this document only with verified results.

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
- Emulator Room test passes;
- Native screenshot and touch-flow QA are complete;
- Persistence across process death is verified on actual Android;
- Backups/export/deletion policy is reviewed;
- User approves the resulting native visuals.

No personal journal data is used in QA; all examples are synthetic.
