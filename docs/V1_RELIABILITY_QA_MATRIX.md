# EVOLUNE v1 — Reliability & accessibility test matrix

**Last reviewed:** 2026-11-11 · Current source: `feature/android-phase3d-qa` · This file records **evidence**, not presumed quality.

## Test status key
- **Verified**: corresponding evidence exists in a completed CI/test run.
- **Running**: committed test is queued/running and must not count as a pass until the result is checked.
- **Manual gate**: requires observation on actual Android devices/with human testers; source compilation or screenshots are not substitutes.

## Data integrity and recovery

| Test | Source of evidence | Current status |
|---|---|---|
| Full challenge/journal history round-trip and correction revisions | Room emulator tests in [run 42992160422](https://github.com/rayadinaveen98-ship-it/EVOLUNE/actions/runs/42992160422) | Verified |
| Reject unsupported/invalid backups with no mutation | Room emulator tests in run 42992160422 | Verified |
| Roll back failed database replacement | Room emulator tests in run 42992160422 | Verified |
| Preserve records across closing/reopening an on-disk database | `RoomPersistenceTest` in emulator QA | Verified |
| Archive and export data on device; restore through actual Android file picker | Two-device manual procedure with test data | Manual gate |
| Reinstall from a **stable signed** package, restore archive and verify records | Manual full install/upgrade test | Manual gate |
| Future Room schema migration | Version 1 Room schema exported in new CI artifact for future fixtures | Running |
| Complete delete, including pending reminders | UI + DAO + Android reminder checks; manual Settings review | Partial, manual gate |

## Gentle reminders

| Test | Source of evidence | Current status |
|---|---|---|
| Off by default, schedule and cancel using AlarmManager | [emulator run 42995670616](https://github.com/rayadinave98-ship-it/EVOLUNE/actions/runs/42995670616) | Verified |
| Weekday, interval, timezone, DST and milestone planning | `ReminderPlanningTest` from run 42995670616 | Verified |
| Suppress canceled, paused, already-recorded or old-day reminder deliveries | New deterministic `isValidDelivery` tests | Running |
| Reconstruct opted-in alarm after Room disk reopen; cancel if challenge deleted | New `ReminderSchedulerTest` | Running |
| Actual *system* callback fires, creates neutral notification at/after requested time | Physical device/extended emulator timing study | Manual gate |
| Permission denied/revoked, channel blocked, app upgrade, actual reboot/timezone change | Android Settings / system event trials | Manual gate |
| Doze/battery saver and many simultaneous reminders; no duplicates or late-night alerts | Long-run Android battery/device matrix | Manual gate |

## Accessibility and layout

| Test | Source of evidence | Current status |
|---|---|---|
| 320dp phone, 853dp tablet screenshot renders | [emulator QA 42995670616](https://github.com/rayadinave98-ship-it/EVOLUNE/actions/runs/42995670616) | Verified |
| 140% Android system font on 320dp phone | New CI screenshot gate | Running |
| 1280dp wide tablet layout | New CI screenshot gate | Running |
| Contrast, keyboard/IME, TalkBack reading order, control focus and hit targets | Human-assisted a11y testing and screenshots | Manual gate |
| Reduced motion and Android disabled-animation configuration | Programmatic checks and actual visual experience review | Partial |
| No visual regressions across Today, Journal, Challenge, Recovery and Review | Native screenshot comparison against approved HTML v0.3 | Partial |

## Pilot and release

| Test | Source of evidence | Current status |
|---|---|---|
| Consent-based pilot with real adults | [Pilot protocol](V1_LIMITED_PILOT_PROTOCOL.md); no real results yet | Not started |
| Signed update-proof artifact, keystore handling, privacy disclosures, Play Store policy | Release checklist/real store setup | Not started |
| Final user go-ahead to publish | Explicit owner confirmation after Stage 10 evidence | Not requested |

**Interpretation:** A passing CI artifact is necessary but insufficient for release. Do not fabricate a reboot test from a reschedule function call, an accessibility pass from a screenshot, or an end-to-end notification delivery from the fact that AlarmManager accepted a PendingIntent.
