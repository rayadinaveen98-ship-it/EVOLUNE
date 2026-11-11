# EVOLUNE V2 — Stage 03: Guided Setup & First Personal Path

**Status:** Native Compose implementation committed in [stacked draft PR #5](https://github.com/rayadinaveen98-ship-it/EVOLUNE/pull/5), based on the Stage 02 Android branch. **Build/emulator/visual signoff pending.**  
**Visual direction:** Stillwater atmosphere + Atelier editorial paper; consistent forest/cream color and original artwork.  
**Principle:** *A guided beginning, not a form marathon.*

## Entry, return and skip semantics
- Fresh install with no existing personal records: brief Android splash → first-run Open Path experience (skippable) → optional four-step setup → saved Today.
- Returning users with any saved **challenge, growth area, or private journal entry** bypass setup. Users who explicitly skipped or completed it are never forced back into the wizard.
- **Explore first** is always available, dismisses setup and opens the working Today screen. It creates **zero** challenges and never logs a practice on behalf of the user.
- System Back moves to the previous step. Back on first step exits setup. Activity recreation restores step and editable fields through Compose saveable state; future full process-death testing remains required.
- No mandatory name, gender, medical data, mood diagnosis, social account, sign-in, payment, or notification permission.

## Four reviewed screen contracts

| Step | Primary question | UI / contents | Saved effect |
| --- | --- | --- | --- |
| **01 — Direction** | Where would you like to grow? | Approved Stillwater landscape with editorial heading, selectable Body & energy / Mind & awareness / Daily foundations / Learning & craft / Connection / Confidence & courage, existing saved growth areas, and **I'll decide later** | Selects optional growth-area label only; does not write during browsing |
| **02 — One practice** | What's one repeatable action? | Short challenge title; specific action, concise example, nonjudgmental microcopy | Proposes existing ChallengePlan title/action |
| **03 — Rhythm** | How do you want to practice? | Calendar days **or** completed sessions; 1–3650 milestone; Every day, Weekdays, or Weekends. Optional cue HH:mm and fallback action hidden under disclosure | Maps to existing recurrence/milestone/schedule parameters |
| **04 — Confirmation** | Does this path feel right? | Factual summary, exact calendar-vs-session label, optional area/time/fallback, local-first privacy explanation and disclosure that no reminder is active | Creates exactly one real challenge via Room, optionally with one new growth area, in a single transaction |

## Visual composition
- Natural scenic illustration only on first step; **readable paper cards** carry all input/content in other steps.
- Forest `#36553E` action color; cream `#FAF8F3` canvas; paper `#FFFEFA` cards; secondary foreground `#65746C`; gentle sage selected surface `#E7ECE2`.
- Typography: editorial page title ~30sp, clear 15–16sp body and 12sp minimum for overlines; system font scaling must remain supported.
- Four-stage progress indicator represents *setup steps*, **never** user productivity or personal worth.
- Actions pinned below scrollable content; min 54dp primary button, selectable options min 54dp; consider keyboard/IME, system bars, compact phone, tablet max width ~680dp.
- Empty and unselected growth area states remain usable; no guilt, pressure or scoring. Do not require a 21-day goal: 21 is an **editable starting example**, not a fixed biological rule.

## Engineering boundary
- Setup is **presentation over the existing ChallengePlan + RoomEvoluneRepository**; existing challenge-creation UI remains available.
- New growth area and initial challenge are inserted in one Room transaction, with rollback if any record fails; no new database tables or schema version.
- Required title/action input, valid milestone, valid HH:mm when specified and trimmed fallback are validated before persistence.
- Display no false confirmation if database saving fails; keep the wizard open with a useful error.
- A cue time is **not opt-in for Android notifications**. Existing reminder feature remains a separate user action after creation.
- Source includes no analytics, remote API, private data fixtures or externally transmitted text.

## Stage 03 quality gates
- [ ] Native Android app builds on exact integration commit.
- [ ] End-to-end on emulator: step 1–4, validation, skip, Back and creating real challenge.
- [ ] Room in-memory test proves combined area/challenge insert and rollback.
- [ ] Four native Android screenshots captured (Direction, One Practice, Rhythm, Review) and reviewed at phone size.
- [ ] Real device/rotation/process death, keyboard, TalkBack, large-font (140%/200%), screen-reader selection state.
- [ ] Confirm returning users with **journal only** remain undisrupted and setup creation isn't repeated.
- [ ] Approve Stage 03 visual parity; no V1 database or reminder regression.

**Stage 03 is not a separate functionality-expansion release.** It presents the already-supported practice choices with a stronger, calmer first-run experience.
