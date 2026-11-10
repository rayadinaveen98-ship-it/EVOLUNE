# EVOLUNE — screen and interaction blueprint v0.1

**Status:** Navigation and content hierarchy proposal only; no UI/code implemented. Defines the minimum journeys to visualize once a direction is selected.

## Conceptual information architecture
- **Today** — primary daily action, small context, next scheduled opportunities, optional reflection, easy access to adjustment.
- **My Growth** — user-chosen domains and meaningful goals; current and previous challenges; no global virtue/mental-health score.
- **Challenges** — list, detail, creation, calendar/session view, revisions, milestone review.
- **Reflection** — private optional journal; selective mood/energy labels; export/delete in settings when supported by engineering.
- **Insights** — plain-language progress summaries with visible denominators and missing-data explanations.
- **Library** — optional evidence-labeled guides and examples, not mandatory onboarding.
- **Settings** — reminders, accessibility, privacy, timezone/data, account/sync if implemented.

These are destinations, **not a mandated tab count**. The nav pattern must be decided in a prototype.

## First-run wireflow
WELCOME -> CHOOSE ONE GROWTH AREA OR CUSTOM -> WRITE OPTIONAL WHY -> CHOOSE ONE REPEATABLE ACTION -> FREQUENCY & MILESTONE -> OPTIONAL WHEN/IF-THEN/BACKUP -> PREVIEW SCHEDULE -> TODAY.

- Back buttons preserve choices.
- All fields are editable.
- No health questions or forced notifications/account.
- The schedule preview explains whether 21 refers to *calendar days* or *completed practices*.
- A user can skip suggested categories and input a custom action.

## Today states
- Scheduled action due: clear task, duration, optional 'Start' only if an actual useful flow, 'Log' and 'Adjust'.
- Completed: acknowledge fact without pretending self-worth changed.
- Partial: preserve what happened, optionally select smaller next action.
- Rested: reflect intentional rest without treating it as a failure or full completion.
- Unreported: invite user to log retrospectively, with 'not sure / leave unreported'; never mark failed automatically.
- No planned practices: neutral encouragement to rest, plan, or explore.

## Challenge Detail
- Name, user-given reason, type (21 calendar days / 21 sessions), start date, frequency and time, backup plan, reminder settings.
- Clear activity timeline with separate readable labels for completed, partial, explicitly missed, rested, paused and unreported.
- Schedule version history for edits; careful edit affordance.
- Upcoming action, optional notes, evidence/insight access where applicable.

## Recovery
Input: a return after an interruption, or user taps 'Adjust'.
Choices: Continue as planned, Try smaller version, Move reminder/schedule, Pause, Mark past actions (optional), Finish.
- Any adjustment must affect upcoming plans only unless the user explicitly edits a past log.
- Never delete or rewrite missed, unreported, rested or partial history to artificially inflate progress.

## Milestone review
- Honest summary: elapsed calendar days, scheduled opportunities, full completions, partial, explicit misses, rest, unreported, paused.
- Plain-language statement that this milestone does **not** prove permanent habit formation.
- Optional 'What helped?' and 'What should change?'
- Continue, adjust, start another, mark finished; none preselected coercively.

## Prototype acceptance checks
1. Each major user task has one dominant action and an obvious back path.
2. Users can recover from interruptions without resetting their identity or history.
3. A 21-day weekday challenge visibly differs from a 21-session challenge.
4. Empty states, offline/unknown data and planned rest are visibly distinct.
5. Every artwork-heavy screen works with reduced motion, large text, and screen-reader descriptions.
6. All conceptual screen art/animations remain proposals until owner approves.
