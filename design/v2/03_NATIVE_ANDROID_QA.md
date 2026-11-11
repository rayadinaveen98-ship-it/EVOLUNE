# EVOLUNE V2 — Stage 03 native Android QA and screen review

**Date:** 2026-11-11  
**Source:** [V2 Guided Setup draft PR #5](https://github.com/rayadinaveen98-ship-it/EVOLUNE/pull/5) (stacked on Native Welcome PR #4, not merged into V1).  
**Result:** [CI build 43017839689](https://github.com/rayadinave98-ship-it/EVOLUNE/actions/runs/43017839689) **success** and [emulator QA 43017839716](https://github.com/rayadinave98-ship-it/EVOLUNE/actions/runs/43017839716) **success**.

## Direct evidence inspected
- XML test results from GitHub emulator artifact **13853248736**: **35 total Android tests** (7 core/Room, 28 app/Compose), **zero failures, zero errors**.
- 21 native PNG screenshots generated; four new Stage 03 1080×2400 captures:
  - `evolune_qa_setup_growth_area.png` (Direction)
  - `evolune_qa_setup_practice.png` (One practice)
  - `evolune_qa_setup_rhythm.png` (Rhythm)
  - `evolune_qa_setup_review.png` (Review)
- Images were opened and inspected side-by-side. All four use consistent editorial heading, 4-step progress, cream/paper surfaces, forest controls, visible persistent footer and scrollable content. Stillwater artwork is retained on the direction screen.
- The rhythm screenshot captured the **default** state, while the interaction test selected Completed sessions and Weekdays; the review screenshot shows actual edited values and optional cue/fallback. This distinction avoids mistaking screenshot and behavior states.

## Verified code/test behaviors
1. User can select growth area or defer it, enter title/action, choose calendar days or completed sessions, recurrence, optional cue/fallback and review.
2. A validation warning prevents continuing without a title/action; a missing area is allowed and no reminders are enabled by entering a cue.
3. Exploring first exits the wizard without invoking creation.
4. New growth-area + challenge + initial schedule inserts via one Room transaction; a mismatched schedule triggers rollback and leaves no orphan area.
5. Existing V1 smoke tests and previous V2 intro tests remain in the emulator suite and pass.
6. The code checks any existing challenges, growth areas or private journal before offering first-run wizard, so historic owners are not *intended* to be re-onboarded. **This guard still needs an explicit real-Activity upgrade test**.

## Remaining defects/risks and release gates
- Stage 03 uses selected-tile visuals but a full TalkBack selection-state announcement, keyboard traversal and accessible focus order require testing.
- The first step contains enough choices to scroll; on a 320dp phone confirm scroll discoverability and the persistent CTA do not trap focus.
- Test setup at Android 140% and 200% font scaling, IME open, tablet landscape, dark mode when introduced.
- Test full app install → welcome → setup → successful Room creation → Today → force stop/relaunch → no setup replay, plus process death during the draft.
- Actual reminder permission choice remains separate by design; notifications are not enabled during setup.
- This is a **native preview/debug build**, not a stable-signed release, nor proof of Stage 10 readiness.

## Observed visual assessment
Overall: coherent, calm, premium foundation, strong visual continuity with Stillwater + Atelier. Potential refinements for the later common component/typography passes: slightly more distinctive compact Open Path mark in header, selection-state semantics, small text consistency and alternate tablet composition.

**Verdict:** Stage 03 native implementation is **emulator-verified** and suitable for visual review. Broader accessibility, lifecycle and real-device quality gates remain open.
