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
- [Android emulator QA run 43012872857](https://github.com/rayadinave98-ship-it/EVOLUNE/actions/runs/43012872857): **Pending when this note was written**.
- Native integration tests added for first-run Skip/returning-user bypass, reduced motion and recreation progress. Existing V1 smoke tests auto-dismiss welcome during normal scenario QA.
- Emulator screenshot collection now requires `evolune_qa_welcome_open_path_first_run.png`.

## Known acceptance limits
- Current logo **direction A** is selected; exact geometry, commercial wordmark typography and OEM adaptive icon masks are not fully signed off.
- Android 12+ brief system splash and the 5.8s in-app introduction are intentionally distinct surfaces. The launcher-icon integration is deferred until final logo approval.
- Only basic native UI semantics are covered; TalkBack, 200% font scale, actual system Back, power/performance, cold-start latency and low-memory trials still require separate verification.
- A debug build and passing tests are not a release candidate.
- User data (including journal) remains local. Test fixtures are synthetic.

**Status:** Native source committed and build verified; emulator screenshot/behavior acceptance is the next gate.
