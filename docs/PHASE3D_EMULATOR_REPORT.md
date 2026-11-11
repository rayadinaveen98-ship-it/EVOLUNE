# EVOLUNE Phase 3D — Verified emulator report and visual audit

**Date:** 2026-11-11  
**Branch:** `feature/android-phase3d-qa` · Draft PR #2  
**Verified source revision:** `32772935e05d77ff76a25e772be81b59e2193dd4`  
**Approved visual baseline:** Stillwater + Atelier HTML prototype v0.3

## Verified Android results

- [Android CI — pass, run 42961296200](https://github.com/rayadinaveen98-ship-it/EVOLUNE/actions/runs/42961296200): JVM/domain checks and debug APK build completed successfully.
- [Android emulator QA — pass, run 42961296348](https://github.com/rayadinaveen98-ship-it/EVOLUNE/actions/runs/42961296348): Android API 35 emulator, x86_64, Pixel 6 profile; **8 tests, 0 failures, 0 errors, 0 skipped**.
- [Native screenshots and XML test reports, artifact 13829666182](https://github.com/rayadinaveen98-ship-it/EVOLUNE/actions/runs/42961296348/artifacts/13829666182): 5 genuine PNGs, each 1080 × 2400 pixels. Files use the prefix `evolune_qa_` and are captured from the native Compose UI (not the HTML prototype).

### Actual instrumented test coverage (8/8)

`core:data` — **6/6 pass**:
1. `PersistenceInstrumentedTest.challengeAndCorrectionsSurviveDatabaseRestart`
2. `PersistenceInstrumentedTest.challengeRemovalUnlinksButDoesNotDeleteJournal`
3. `RepositoryGuardTest.pausedDaysCannotBeRecordedButValidPastDaysCan`
4. `RepositoryGuardTest.pauseCannotRewriteEarlierOpportunityHistory`
5. `RepositoryGuardTest.calendarMilestoneCannotAddRecordsAfterEnd`
6. `RoomPersistenceTest.reopenRetainsActivityHistoryAndPrivateJournal`

`app` — **2/2 pass**:
1. `NativeJourneyTest.mainDestinationsAreNavigable`
2. `NativeJourneyTest.privateJournalEntrySurvivesActivityRecreation`

**Interpretation:** These verify specific persistence rules, main navigation, and Activity-recreation journal survival. They are NOT proof of all workflows, process death/reboot behavior, notification reliability, accessibility, or release readiness.

### Screenshot provenance and integrity

The first screenshot pipeline incorrectly read from app-private files after instrumentation cleanup, causing invalid pseudo-PNG text files. A separate launcher ANR dialog appeared in its post-restart system screenshot, making that image unsuitable for visual review. Both problems were excluded from visual approval. The final passing run stores synthetic captures through MediaStore and validates PNG signatures, producing five genuine native screen images.

## First-pass visual comparison: v0.3 HTML vs native

| Dimension | Finding | Priority |
|---|---|---|
| Art direction | Stillwater landscape asset and cream/green colors are present; promising baseline | Preserve |
| Navigation | Native uses Unicode symbols and purple/lavender selected indicators; approved v0.3 uses more integrated editorial green navigation | High |
| Typography | Native display titles are oversized and often wrap differently; editorial rhythm and subtitles differ from v0.3 | High |
| Screen hierarchy | Large empty areas and generic cards make native feel less immersive than approved v0.3 | High |
| Today's hero | Same artwork, different crop and plain overlaid label instead of the richer prototype hero treatment | High |
| Growth | Native shows a basic growth-area form and repeats the same banner; prototype offers a more layered exploratory journey | Medium–high |
| Journal | Controls and boxes look noticeably more Material-default than the Atelier journal composition | High |
| Challenges / Insights | Empty states rendered; detail and populated/progress states not yet captured for visual approval | Medium–high |
| Motion and touch effects | Not yet objectively captured/reviewed; no parity claim | Pending |

**Critical comparison qualification:** The native screenshots show a fresh, empty install. The HTML v0.3 Today reference was populated with a sample reading challenge. Their content differences must not be mistaken for data loss or missing implementation. Separate populated-state screenshots are required.

## Gates still pending

- Capture & compare **populated Today**, challenge creation, details, recovery, milestone review, and a populated journal.
- Native 320dp and tablet visual-layout tests; check navigation, long labels, touch targets, font scaling and keyboard inset.
- Lifecycle/process-death/reboot persistence (beyond Activity recreation), and user-controlled export/delete/backup policy.
- Reminder scheduling/permission/reliability and accessibility/reduced motion review.
- Visual parity polish and explicit approval before treating design as final.

## Next scope (no new feature expansion)

1. Lock tokens and typography/layout scale to the approved v0.3 design.
2. Replace temporary Unicode navigation symbols and lavender selection treatment with accessible matching icons and sage states.
3. Refine Today hero, practice card, Journal and Growth to restore Stillwater + Atelier hierarchy.
4. Produce real emulator screenshots for each revised state and compare them side by side.
5. Only after visual signoff: complete the remaining Android reliability and release gates.

**Status:** Verified engineering milestone; **visual parity and release readiness are not approved**. Keep PR #2 draft. No real private journal data or user photos appear in screenshot artifacts.
