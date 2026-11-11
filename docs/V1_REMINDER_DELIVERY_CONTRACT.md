# EVOLUNE v1 — Local reminder delivery contract

**Decision date:** 2026-11-11. This is the **implementation contract**, not evidence that reminders work in Android v0.5/v0.6.

## Product intent
- Reminders are **opt-in** per challenge and can be disabled at any time.
- A time-of-day cue is not silently equivalent to consent for a notification.
- Show the user that reminder timing is **approximate** under Android battery management; this app is not an alarm clock and must not promise minute-perfect delivery.
- No remote server, tracking pixel, or analytics to deliver notifications.

## Android policy/security direction
- Use **inexact** scheduling for user-chosen practice nudges wherever possible (e.g. AlarmManager setAndAllowWhileIdle or a suitable WorkManager job), not restricted USE_EXACT_ALARM.
- Request **POST_NOTIFICATIONS** only after the user opts into notifications on Android 13+, and handle denial by displaying a clear “not active” state.
- If notifications are unavailable, keep the practice schedule useful in the app without misleading the user.
- Channel supports silent/quiet behavior; no forced alarms or repeated disruptive audio.
- Avoid private journal contents, mood data or other sensitive text in notification previews; title should be user-controllable/neutral by default.
- Persist opt-in and scheduling state on-device so it can be audited and reset.

Official policy references:
- https://developer.android.com/develop/background-work/services/alarms
- https://developer.android.com/about/versions/14/changes/schedule-exact-alarms
- https://developer.android.com/develop/ui/views/notifications/notification-permission
- https://support.google.com/googleplay/android-developer/answer/16558241

## Behavior acceptance matrix
1. Opt in -> if notification permission granted, schedule next eligible opportunity; if declined, show disabled and do not misreport success.
2. Practice completed, paused, archived, or challenge deleted -> do not deliver stale alerts.
3. Edited cue/time, recurrence or timezone -> cancel old pending trigger and compute next opportunity.
4. Reboot, app update, clock or timezone change -> reconstruct from local database (after unlock if needed), avoid duplicate alerts.
5. Battery saving, Doze and notification restrictions -> show realistic timing limitations; no silent reliance on exact alarms.
6. Fall-back action/cue changes -> present current action, never outdated schedule.
7. All notification content is non-clinical, non-judgmental and privacy-respecting.
8. QA must include permissions denied, permissions revoked, reboot, zone change, short/long recurrences, inactive challenge and multiple challenges.

## Current status
Planned, **not implemented or user-verified**. Do not treat the existing optional cueTime field as notification scheduling.
