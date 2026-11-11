# EVOLUNE V2 — Stage 04: Today & Daily Guide

**Status:** Native implementation on `feature/v2-today-daily-guide`, stacked on V2 guided setup. Draft review only, not merged or released.
**Fixed programme:** [V2 ten-stage roadmap](https://github.com/rayadinaveen98-ship-it/EVOLUNE/blob/design/v2-experience-excellence/design/v2/00_EXPERIENCE_EXCELLENCE_ROADMAP.md).
**Scope:** Improve the existing Today presentation and information hierarchy. No new domain behavior, reminders, journal storage, account, AI, tracking backend or gamification.

## Shipped in this source branch
- Introduced `presentToday()`, a deterministic view classifier using the immutable `ChallengeSnapshot`, `ChallengeEngine.opportunities()`, scheduled revisions, pauses, milestone kind and saved time zone.
- State-specific presentation: empty, future start, scheduled, recorded full/partial/backup/rest/explicit miss, unscheduled rest day, paused, ended calendar milestone.
- Primary action above artwork: real logging for scheduled/previously recorded opportunities, real challenge creation, plan adjustment and existing detail/history navigation.
- Edit of today's existing outcome appends a revision through the **unchanged** repository API; no deletion or mutation of prior records.
- Shows only recorded full sessions toward completed-session targets; calendar-day labels use the elapsed local calendar window. No invented streak, health score, behavioral prediction or "21 days forms a habit" claim.
- Distinguishes earlier **unreported** opportunities from explicit misses, with a real link to challenge history; never changes records or marks an unreported day missed.
- Finds the next actual recurrence date (excluding pauses and dates after calendar milestones), using the stored recurrence/time zone and future schedule revisions. Does not silently create reminders from a planning cue.
- Reorders approved Stillwater artwork below the high-priority practice card; preserves editorial Atelier surfaces, local privacy copy and optional reflection route.

## Existing functionality deliberately unchanged
- No changes to `RoomEvoluneRepository`, `EvoluneDao`, `ChallengeEngine`, `MainActivity`, notification scheduler, Room schema, import/export, journal or growth areas.
- Stillwater original hero resource is reused, not replaced.
- All other navigation destinations are still the existing V1 UI.
- The session-milestone engine only counts `COMPLETED` toward its target; a completed backup action remains a distinct honest state.

## Automated test plan
- `TodayStage04Test`: deterministic classifier matrix, five recorded outcomes, unreported history, calendar end, pause, weekday/weekend recurrence, future start, session milestone semantics.
- Compose instrumentation: actual record-editor click and journal route, plus synthetic native Today screenshots (empty and scheduled).
- Retain existing Phase 3D and V2 Stage 02/03 Android tests, including first-run, Room, reminder planning and guided setup.
- **Evidence must be checked from the exact branch SHA before marking build/emulator PASS.**

## Exit gates not yet signed off
1. Real 320dp / 360dp / 393dp phone, 200% font, tablet / landscape / keyboard state and scroll discoverability.
2. TalkBack focus order, status comprehension, outcome selection semantics, touch target and reduced-motion QA.
3. Native screenshots compared to previously approved Stillwater + Atelier and owner visual signoff.
4. Real-device change of outcome followed by process death / relaunch to verify persisted revision, pause/rest behavior and no regression.
5. Earlier Stage 01–03 wordmark and setup visual approvals remain open; no merging until all stacked branches are reconciled.

**Release status:** Not a production APK or Play Store build. This pass remains V2 Stage 04 only.
