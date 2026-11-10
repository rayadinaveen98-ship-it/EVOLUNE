# Challenge engine functional specification — v0.1

**Status:** Behavioral contract proposal, **not production code**. A detailed specification prevents future UI work from accidentally changing challenge rules.

## Fundamental objects
1. **GrowthArea:** id, user title, user description, optional visual theme.
2. **Goal:** id, growth_area_id, user-stated purpose and desired direction (optional, not a numeric 'wellness score').
3. **Challenge:** id, goal_id, user-chosen title, one repeatable **action**, metric (binary / duration / count / optional), start local date, milestone mode (calendar days OR scheduled sessions), milestone quantity, recurrence, optional cue and fallback action, timezone, privacy.
4. **ScheduledOpportunity:** id, challenge_id, scheduled local date/time, revision version, status, resolved/created timestamps.
5. **PracticeLog:** id, opportunity_id, outcome, optional measured value, note, timestamp, source (manual), edit history.
6. **Review:** id, challenge_id, period, personal observation, keep/adjust/pause/finish decision.
7. **PlanRevision:** id, challenge_id, effective_from date, changed fields, why (optional); old records remain auditable.
8. **ReflectionEntry:** id, user-chosen labels or note, attached growth area/challenge (optional), privacy controls.

These are *logical entities*, not a database schema or a justification to collect extra personal data.

## One repeatable action per challenge
Example: 'Read for 10 minutes.' The challenge can have alternate lower-effort versions ('Read one page') without silently counting the alternative as the original full action. If a user wants an entire morning routine, model as separate practices or a future composite feature, rather than changing this principle unseen.

## Duration vs sessions: explicit semantics
- **21-day challenge:** default means **21 calendar days** from the user-selected start, with practice opportunities only on scheduled days. E.g., Mon–Fri across 21 days normally yields 15 due opportunities, not 21.
- **21-session challenge:** means completing 21 scheduled practices, regardless of elapsed calendar days; target date is estimated, never guaranteed.
- A calendar-day milestone remains 21 elapsed days unless explicitly postponed by user; a pause does not rewrite the calendar. The milestone review can include due days, paused days and completed practices.
- A challenge can continue after a milestone; the milestone is not a habit-certification claim.
- End/start-of-day follow the challenge's chosen IANA timezone and local date, not device UTC boundaries. Travel timezone handling remains a design decision.

## Outcome statuses (per scheduled opportunity)
- **Pending:** scheduled but not due/resolved.
- **Completed:** full user-defined action occurred.
- **Partial:** some action occurred, but full target not achieved.
- **Missed:** due opportunity passed without user action or user explicitly marks missed.
- **Rested:** user intentionally chose rest; preserved as a distinct truthful status, not hidden completion.
- **Paused:** opportunity excluded from practice while challenge intentionally suspended.
- **Not scheduled:** display state only, not a recorded failure.
Optional: **Completed via backup** if a fallback action is defined; surface separately unless the user explicitly defines equivalence.

Logs are editable with revisions tracked to prevent accidental history loss; users can correct a forgotten check-in. No fabricated completion from notifications, phone motion, or time elapsed.

## State transitions (high-level)
DRAFT -> SCHEDULED -> ACTIVE -> (PAUSED <-> ACTIVE) -> MILESTONE_REVIEW -> (CONTINUING | FINISHED).
ARCHIVED is a non-destructive user choice from finished or stopped/paused states. Cancellation preserves an optional history; delete permanently according to user instructions.

## Daily algorithm (conceptual)
1. Generate opportunities from recurrence rules for local dates in a safe bounded horizon.
2. Show only due/selected upcoming items on Today; the user can explore all.
3. At scheduled time, **offer** an opt-in reminder; no reminder means the plan is still valid.
4. User records status and optional journal/effort; do not force mood capture.
5. On missed days, offer: 'Continue as planned', 'Try a smaller version', 'Change schedule', 'Pause', 'Reflect' (all optional).
6. Weekly/milestone review summarizes facts, leaves interpretation and next choice to user.

## Example calendar challenge
- Start: November 12, 2026; length 21 calendar days, Mon–Fri at 7:30 AM (India local time).
- There are ~15 scheduled opportunities in the 21-day window, depending on start weekday; this must be computed, not hardcoded.
- If 9 complete, 2 partial, 1 rested and 3 missed, show those counts separately.
- Example completion rate **9 / 15 = 60% of planned opportunities**. If rest was intentionally inserted as a plan revision, additionally show eligible-denominator numbers clearly; no unlabelled 'success score'.

## Metrics and truthfulness
- **Adherence:** completed / scheduled opportunities during eligible period, with numerator and denominator always visible.
- **Practice frequency:** count of logged full actions in a selected period; separate partial/backup.
- **Return after interruption:** number of days between interruption and next logged practice; descriptive, not virtue judgment.
- **Self-reported mood, energy, confidence:** trends only when enough user-entered data exists; non-causal and non-diagnostic.
- **No 'motivation score', habit formed badge, or mental-health diagnosis** from checkboxes.
- Insights explicitly distinguish a calendar-day duration from actual scheduled sessions.

## Non-negotiable edge cases to test
- DST change / crossing timezones / device date changes.
- Weekends, custom weekdays, every-N-days recurrence, past-start challenges.
- Duplicate logs and rapid repeated taps; offline edits later syncing.
- Missed reminder vs missed behavior; notification permission refused.
- Pause mid-week and resume; milestone reached while paused.
- Changing frequency/target after records already exist.
- Deleting challenge vs preserving reflections, data export, edit history.
- 0 due opportunities, 0 scheduled days, incomplete historical data.
- Accessibility, low-bandwidth/offline and device reinstalls.

## Evidence link
The action-planning and self-monitoring concepts are supported in relevant research, but a specific schedule/state implementation is **our unvalidated design**. Source and caveat register: [EVIDENCE_REGISTER.md](EVIDENCE_REGISTER.md).
