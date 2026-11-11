# EVOLUNE v1 — Gentle reminder implementation and verified QA

**Updated:** 2026-11-11 · **Review branch:** `feature/android-phase3d-qa` (draft PR #2).  
**Feature:** Optional, per-challenge, approximate local notifications. No paid service, no online account, no clinical claims.

## Implemented in native Android
- User explicitly opens a challenge's **Gentle reminders** screen and chooses an HH:mm time. The challenge's `cueTime` alone does **not** enable a notification.
- Android 13+ asks for `POST_NOTIFICATIONS` permission **only after the user opts in**. Denial leaves the reminder disabled.
- Private per-challenge enable state/time persisted in app-private SharedPreferences. The data is **not** silently added to exported JSON backups. Upon restore/erase, previously scheduled notifications are disabled; the user can re-enable them.
- `AlarmManager.setAndAllowWhileIdle()` uses an **inexact** local alarm for the next opportunity only; no `USE_EXACT_ALARM` or `SCHEDULE_EXACT_ALARM` permission. Timing may be delayed by Android's power restrictions.
- Uses the challenge's IANA timezone, recurrence rule and actual future scheduled opportunity. No alerts on paused, completed, partial, rested or explicitly missed logged days; no reminder after a finished calendar challenge or reached session target.
- Schedule revisions, new logs, pauses, disabled notifications, archived/deleted challenges and import/reset operations recalculate or cancel stale reminders.
- App foreground, boot, app upgrade, system clock and timezone changes attempt to recreate opted-in alarms from app-private state; broadcast processing does not log private data.
- Low-importance notification channel with neutral copy and private visibility: **never includes mood, journal text or sensitive private data**.

## Verified evidence
- [GitHub Android CI run 42995670955](https://github.com/rayadinaveen98-ship-it/EVOLUNE/actions/runs/42995670955): **success** — Kotlin/Room/native debug build compiled.
- [GitHub emulator QA run 42995670616](https://github.com/rayadinave98-ship-it/EVOLUNE/actions/runs/42995670616): **success** — 6 core data tests + 16 app tests = **22 tests, 0 failures and 0 errors** (verified from downloaded XML reports).
- ReminderPlanning tests: challenge timezone vs UTC, after-time next day, completed/paused exclusions, weekdays/weekends, finished calendar/session milestones, and New York DST.
- ReminderScheduler test: no alarm without explicit opt-in; real Android AlarmManager scheduled a future alarm following QA-only permission grant; disabling it canceled the stored trigger.
- Compose journey covers navigation into the reminder screen and **off by default** state. Native screenshot `evolune_qa_reminders_disabled.png` verified.

## Not yet complete / blockers to call reminders release-ready
1. **Real-device timed delivery and re-delivery:** confirmation of actual notification showing at the user-chosen approximate time across battery-optimization modes and multiple days.
2. **Denied and revoked permission paths** and notification-channel blocked status; manual Android Settings transition/return.
3. **Real reboot, device timezone change and app-update** trials with opted-in alarms, including cancellation of reminders after plan revision/pause/restore.
4. **Multiple simultaneous challenges** and long-running Android battery saver/Doze reliability.
5. Device-font/accessibility checks, neutral notification privacy preview review, and no duplicate/stale notification assurance.

## Interpretation
`22/22` passing automated tests establishes a meaningful engineering milestone, **not** full real-world delivery, battery reliability or public release readiness. The signed release, pilot and Stage 10 are not complete. Never equate the scheduled local cue with a precise alarm-clock guarantee.
