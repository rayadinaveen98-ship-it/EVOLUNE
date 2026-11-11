# EVOLUNE V2 — Open Path native welcome integration

**Date:** 2026-11-11 · **Branch:** `feature/v2-open-path-native-intro`  
**Review:** [Draft PR #4](https://github.com/rayadinaveen98-ship-it/EVOLUNE/pull/4), based on the V1 Android development branch.  
**Design:** [Stage 02 storyboard](https://github.com/rayadinaveen98-ship-it/EVOLUNE/blob/design/v2-experience-excellence/design/v2/02_WELCOME_EXPERIENCE_SPEC.md) and interactive HTML v0.1.

## What was implemented
- Stage 01 Open Path SVG geometry converted to a Compose Canvas with three separately animated stones and one brass horizon dot; the same vector also appears in the Android 12+ **short system splash**.
- The approved existing Stillwater landscape is used as the background; the in-app sequence gradually reveals the mark, EVOLUNE wordmark and tagline before showing the existing Room-backed Today screen.
- Intro playback runs on first launch only (approximately 5.8 seconds). Return visits open Today without an artificial delay.
- **Skip intro** and system Back finish immediately; Android system Remove Animations uses a short, static presentation.
- A locally persisted monotonic starting timestamp resumes progress across Activity recreation; completed state stays in private SharedPreferences.
- Source integration changes only the Android entry-layer and startup resources. No changes to challenge engine, reminders, Room schema, journal, export or restore.
- Internal full-motion override is used **only on debuggable builds** so the emulator can capture a staged frame when its test runner has animations disabled.

## Verification
- [Android CI run 43012872864](https://github.com/rayadinave98-ship-it/EVOLUNE/actions/runs/43012872864): **SUCCESS** after an API compatibility correction.
- [Android emulator QA run 43012872857](https://github.com/rayadinave98-ship-it/EVOLUNE/actions/runs/43012872857): **SUCCESS**, 31 total Android tests (6 core data + 25 app), 0 failures/errors. Native welcome and existing Room/navigation journeys pass.
- Native integration tests added for first-run Skip/returning-user bypass, reduced motion and recreation progress. Existing V1 smoke tests auto-dismiss welcome during normal scenario QA.
- The initial native screenshot was captured too early. The corrected [Android emulator run 43013838539](https://github.com/rayadinave98-ship-it/EVOLUNE/actions/runs/43013838539) **PASSED** (31/31 tests, 0 failures/errors) and captured the **2.3s stone reveal** and **4.45s wordmark reveal**. The native screenshots were opened and visually inspected next to the approved HTML v0.1 reference.

## Known acceptance limits
- Current logo **direction A** is selected; exact geometry, commercial wordmark typography and OEM adaptive icon masks are not fully signed off.
- Android 12+ brief system splash and the 5.8s in-app introduction are intentionally distinct surfaces. The launcher-icon integration is deferred until final logo approval.
- Only basic native UI semantics are covered; TalkBack, 200% font scale, actual system Back, power/performance, cold-start latency and low-memory trials still require separate verification.
- A debug build and passing tests are not a release candidate.
- User data (including journal) remains local. Test fixtures are synthetic.

**Status:** Native first-run behavior and the two animation frames are verified in real emulator output. [Android CI 43013838641](https://github.com/rayadinave98-ship-it/EVOLUNE/actions/runs/43013838641) **success**; [emulator QA 43013838539](https://github.com/rayadinave98-ship-it/EVOLUNE/actions/runs/43013838539) **success**, 31 tests / 0 failures, 17 native screenshots including the two timed intro frames. Browser/reference and native wordmark typography differ, so final visual signoff is still pending.
