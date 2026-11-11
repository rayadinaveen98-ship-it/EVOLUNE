# EVOLUNE v1 — Android accessibility / responsive-layout evidence

**Review date:** 2026-11-11 · **Reference:** approved Stillwater + Atelier visual language.

## First expanded emulator evidence
- [Android emulator run 42998280747](https://github.com/rayadinaveen98-ship-it/EVOLUNE/actions/runs/42998280747) **passed**, 6 Room and 21 app instrumented tests, **27 tests / 0 failures**.
- Artifact **13844632954** contains 15 valid PNG captures and generated Room version 1 schema.
- Tested renderer sizes: 320dp phone (regular text), 320dp phone with 140% Android system text, 853dp tablet portrait, 1280dp wide tablet viewport.
- Actual images were opened and inspected, not inferred from source.

## Issues observed
1. **Confirmed defect:** At 320dp + 140% text, navigation labels **My Growth** and **Challenges** were clipped because tabs only allowed one line in a fixed 73dp bar.
2. **Observed:** Compact Today heading and adjacent “Begin a practice” action were cramped at large fonts.
3. **Observed:** Wide tablet layout renders but has substantial whitespace; tablet editorial column remains deliberate and readable, not proof of a fully bespoke landscape layout.
4. **Not verified:** TalkBack semantic navigation and reading order, touch targets on device, IME/keyboard, high-contrast appearance, localization, 200% fonts or screen magnification.

## Code correction / retest
Commit `9c9aee6e95387c09ea621f8a656e429d1e3971fe`:
- Expanded the bottom navigation bar when the Android font scale is at least 1.25, allowing two centered label lines.
- Added tab roles and explicit accessible navigation descriptions.
- Added a label to the My Growth shortcut.
- Made the Today heading/action layout stack vertically on narrow screens with large text.

**Retest verified:** [Android CI 42999466754](https://github.com/rayadinave98-ship-it/EVOLUNE/actions/runs/42999466754) **passed**, and [Android emulator QA 42999466755](https://github.com/rayadinave98-ship-it/EVOLUNE/actions/runs/42999466755) **passed**, with **28 tests and zero failures/errors** (6 data + 22 app). QA artifact **13845567852** includes the new 320dp/140% and wide-tablet captures. Those PNGs were opened and reviewed: the navigation labels are now all visible and Today adapts its heading/action layout; the long “Challenges” label has an awkward two-line break, so refinement remains desirable. This is rendering evidence, **not** a TalkBack, touch-target, color-contrast or full a11y signoff.

## Remaining Stage 07/08 accessibility checklist
- [x] All tab labels rendered at 320dp / 140% after fix; [ ] improve long-word wrapping and verify 200% text.
- Inspect Journal, Challenge Create, Recovery, Review, Data & Privacy and Reminders at font scale 1.4 and 2.0.
- TalkBack focus/readout order, accessibility labels and touch target sizing.
- Keyboard navigation and Android IME overlap with entry fields, especially sensitive `DELETE` and `REPLACE` confirmations.
- Reduced motion setting, accessibility animation scale off and contrast.
- Real phone and tablet manual UX signoff before the limited pilot.
