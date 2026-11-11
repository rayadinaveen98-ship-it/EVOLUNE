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
| Future Room schema migration | Version 1 schema exported in emulator 42998280747 artifact (14,714-byte JSON); actual v1→v2 migration still nonexistent | Baseline verified; migration pending |
| Complete delete, including pending reminders | UI + DAO + Android reminder checks; manual Settings review | Partial, manual gate |

## Gentle reminders

| Test | Source of evidence | Current status |
|---|---|---|
| Off by default, schedule and cancel using AlarmManager | [emulator run 42995670616](https://github.com/rayadinave98-ship-it/EVOLUNE/actions/runs/42995670616) | Verified |
| Weekday, interval, timezone, DST and milestone planning | `ReminderPlanningTest` from run 42995670616 | Verified |
| Suppress canceled, paused, already-recorded or old-day reminder deliveries | [Emulator 42998280747](https://github.com/rayadinaveen98-ship-it/EVOLUNE/actions/runs/42998280747), deterministic delivery-window tests | Verified |
| Reconstruct opted-in alarm after Room disk reopen; cancel if challenge deleted | Emulator 42998280747; test uses reopened persistent Room database | Verified |
| Actual *system* callback fires, creates neutral notification at/after requested time | Physical device/extended emulator timing study | Manual gate |
| Permission denied/revoked, channel blocked, app upgrade, actual reboot/timezone change | Android Settings / system event trials | Manual gate |
| Multiple independent alarms isolate scheduling and cancellation | Multi-challenge AlarmManager emulator test [42999466755](https://github.com/rayadinave98-ship-it/EVOLUNE/actions/runs/42999466755) | Verified |\n| Doze/battery saver, actual timed delivery and no duplicate late-night alerts | Long-run Android battery/device matrix | Manual gate |

## Accessibility and layout

| Test | Source of evidence | Current status |
|---|---|---|
| 320dp phone, 853dp tablet screenshot renders | Emulator 42998280747: 15 actual PNGs incl. phone, 140% font, tablet, wide tablet | Verified |
| 140% Android system font on 320dp phone | Retest [emulator 42999466755](https://github.com/rayadinave98-ship-it/EVOLUNE/actions/runs/42999466755) after adaptive two-line nav fix | Rendering verified; awkward long-word wrap and TalkBack remain |
| 1280dp wide tablet layout | Emulator 42998280747 native PNG captured and manually opened | Render verified; content composition needs review |
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
