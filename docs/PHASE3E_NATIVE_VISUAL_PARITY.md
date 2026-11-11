# EVOLUNE — Native visual parity pass 1 (2026-11-11)

**Review branch:** `feature/android-phase3d-qa` · [Draft PR #2](https://github.com/rayadinaveen98-ship-it/EVOLUNE/pull/2).  
**Design authority:** Approved Stillwater + Atelier HTML interactive prototype v0.3.  
**Release status:** Native development build; not visually approved or release-ready.

## Actual changes in Android source
- Replaced generic Material navigation indicators and Unicode placeholder labels with an original five-destination outline icon navigation, green active treatment and accessibility-friendly tap targets.
- Updated warm cream/paper/sage color tokens, type hierarchy, subtle borders, card radius and primary/secondary buttons.
- Reused the approved Stillwater landscape artwork as an actual native hero image, with an editorial gradient and white type overlay.
- Rebuilt Today around the approved “A gentle day to begin again” structure, a focused practice card, expandable outcome logging and a working Journal shortcut. No practice outcome or calendar rule was altered.
- Reworked Journal landing with a calm hero, optional editor reveal, private entries and honest local-data copy. Create/edit/delete remain Room-backed.
- Reworked Insights with real full/partial/unreported/rest counts and explicit non-diagnostic, non-shaming explanations.
- Reworked Challenge Detail with real first-seven-day status markers, factual progress cards, revision/review links and its existing history and outcome actions.

## Verification evidence
- [Android UI + persistence emulator run 42964354746](https://github.com/rayadinaveen98-ship-it/EVOLUNE/actions/runs/42964354746): **PASS**, 6 Room/data tests and 3 Compose UI tests, zero failures/errors. Includes screenshots of a newly created synthetic challenge, Today, Growth, Journal, Insights, Detail, Recovery and Review. This run predates the final **Challenge Detail visual refinement**.
- [Android build run 42965272744](https://github.com/rayadinaveen98-ship-it/EVOLUNE/actions/runs/42965272744): **PASS**, builds the final Challenge Detail visual refinement at source commit `7fb42848525bb489fd05fd5805744fef8b25fa9c`.
- [Latest emulator run 42965272820](https://github.com/rayadinaveen98-ship-it/EVOLUNE/actions/runs/42965272820): **not yet verified** at the time this report was written. Do not infer pass based on successful compilation.
- APK from successful build was extracted and ZIP integrity checked; a compiled debug APK does not verify runtime functionality.

## Current visual-parity findings
**Improved:** Today, navigation, typography, warm/pale color system, landscape hero placement, progressive Journal, factual Insights, and Challenge Detail information hierarchy.

**Still not exact:**
1. The Journal currently reuses the Stillwater landscape instead of the unique **Atelier journal/tea/stationery artwork** in v0.3. The approved source art must be integrated as an optimized Android image asset before visual signoff.
2. The challenge path's first-week strip needs review at narrow widths and against the v0.3 calendar tile behavior.
3. Android transitions, background motion/reduced-motion handling, dynamic visual elements and photo treatment are not yet at full v0.3 fidelity.
4. Tablet and small-device layout screenshots have not been verified.
5. The currently selected challenge's Insights are shown; this is not a full multi-challenge aggregate.
6. Real reminders, cloud backup, and export remain outside this native visual parity pass.
7. User visual approval and device persistence/interaction QA beyond the scripted cases are still needed.

## Constraints preserved
- No auto-marking unreported opportunities as missed.
- No claim that 21 days establishes a permanent habit.
- Pauses/schedule changes preserve historical records; journal remains device-private.
- No personal data, logs, journal writing or account credentials committed to the public repository.
- No merge to `main` until user approves native appearance and required Android QA is green.
