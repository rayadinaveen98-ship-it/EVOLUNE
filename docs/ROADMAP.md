# EVOLUNE — development roadmap v0.2

**Reviewed:** 2026-11-11 · **Working branch:** `feature/android-phase3d-qa` · **PR:** [#2](https://github.com/rayadinaveen98-ship-it/EVOLUNE/pull/2) (draft, unmerged)  
**Current build:** native Android **v0.5 debug**, visually inspired by approved Stillwater + Atelier HTML v0.3.  
**Status terminology:** ✅ Verified deliverable · ◐ Implemented partially / needs signoff · ○ Not yet begun.

> Product promise: **A calmer way to become better.** Progress without pressure; discipline without punishment; growth without comparison.

## Live stage-by-stage roadmap

| Stage | Workstream | Status | Evidence and remaining exit criteria |
|---|---|---|---|
| **00** | Repo and working identity | ✅ | `EVOLUNE` repo and source/docs exist; brand trademark/release identity not legally cleared |
| **01** | Discovery and product charter | ◐ | Product-owner approved v1 direction and [locked feature boundary](MVP_SCOPE_V1.md); target-user validation and pilot evidence remain |
| **02** | Evidence and safety | ◐ | Evidence register, [feature safety register](V1_SAFETY_AND_RELEASE_RISKS.md), and [reminder policy contract](V1_REMINDER_DELIVERY_CONTRACT.md) established; privacy/accessibility review pending |
| **03** | UX / prototypes | ✅ | Product owner approved Stillwater + Atelier and the existing UX/visual direction, with HTML v0.3 and native v0.5 prototypes. Final device accessibility and interaction QA remain in Stages 07–08 |
| **04** | Native architecture and data | ◐ | Kotlin/Room/Compose and CI working; [versioned local export, validated restore and confirmed erase](V1_DATA_OWNERSHIP_VERIFICATION.md) emulator-tested. Manual document-picker/reinstall, Room migrations and reboot/process-death tests remain |
| **05** | Core Android MVP | ◐ | Native Today, challenges, truthful logging, pauses, review, Growth, Journal, Insights, data controls and **opt-in inexact reminders** exist and pass focused Android checks; onboarding completeness, end-to-end reliability and final device QA remain |
| **06** | Reflection and personalization | ◐ | Private journal create/edit/delete, optional mood/energy, growth areas, recurrence/cues/fallbacks present. Rich personalized guidance, controls, cross-challenge insights and user-managed data are unfinished |
| **07** | Premium Stillwater + Atelier | ◐ | Landscape + approved Atelier journal asset in native app, consistent greener navigation, gentle motion, responsive phone/tablet rendering. Tablet composition, touch/animation fidelity, reduced-motion and accessibility reviews remain |
| **08** | Integrated QA and stabilization | ◐ | [Android CI 42995670955](https://github.com/rayadinave98-ship-it/EVOLUNE/actions/runs/42995670955) and [emulator QA 42995670616](https://github.com/rayadinave98-ship-it/EVOLUNE/actions/runs/42995670616) passed — **22 tests, 0 failures**. Manual reminder delivery/reboot/permission/Doze, device matrix, migration and reinstall checks remain |
| **09** | Limited pilot | ○ | [Consent-first pilot protocol prepared](V1_LIMITED_PILOT_PROTOCOL.md); no volunteers tested, no findings recorded |
| **10** | Release readiness | ○ | Final privacy/security review, data ownership controls, signed release APK/AAB, store requirements and listing, rollout/rollback plan; no public release yet |

## Completed technical milestones (verifiable)

- Preproduction: scientific evidence register, challenge architecture, UX journeys, Stillwater + Atelier visual exploration and interactive HTML v0.3.
- Domain: pure Kotlin ChallengeEngine distinguishes **unreported** versus explicitly **missed**, supports partial/rested/paused and calendar-day versus completed-session milestones; no universal “21-day habit formation” claim.
- Persistence: local Room v1 storage, correction history, journal records and schedule revisions; automated tests run on an Android emulator.
- Android: debug APK builds via GitHub Actions with native Compose UI and premium art integration.
- v0.5 visual QA: 320dp simulated phone and 853dp simulated tablet screenshots, Journal Atelier artwork, green navigation, baseline motion. **These screenshots are evidence of rendering, not final user approval.**
- Latest verified Android milestone: [CI 42995670955](https://github.com/rayadinave98-ship-it/EVOLUNE/actions/runs/42995670955) and [emulator QA 42995670616](https://github.com/rayadinave98-ship-it/EVOLUNE/actions/runs/42995670616) **passed**: 22 Android tests, 0 failures, including an inexact alarm schedule/cancel test. [Reminder report](V1_REMINDER_IMPLEMENTATION_REPORT.md). Earlier visual QA captured 320dp phone and tablet renderings.

## The remaining path — no extra phases added

1. **Visual and interaction approval:** v0.5 device review; improve unused tablet space, consistency of Challenge/Recovery/Review, large fonts, touch targets and reduced-motion behavior. Compare with HTML v0.3. Do not replace approved art or established challenge logic.
2. **Finish essential product reliability:** Export, validated restore and confirmed erase are implemented/emulator-tested; finish real document-provider round trip, reinstall and upgrade/migration tests, process death/reboot checks and data-loss audit.
3. **Reminders and schedule delivery:** per-challenge opt-in, Android permission, inexact alarms, cancellation/revisions and reboot/timezone receiver code are implemented and partially emulator-tested; complete real timed-delivery, permission-revocation, multi-device, reboot and Doze tests before claiming reliability.
4. **Accessibility and device QA:** 320dp, typical phones, tablets/foldables; portrait and landscape; keyboard/IME; TalkBack, larger fonts, contrast, system animation disabled, performance. Run realistic multi-day and multiple-challenge tests, not only fresh-install tests.
5. **Pilot → release gate:** after user visual signoff and reliability criteria, merge reviewed source, run a small pilot, fix findings, and only then prepare signed AAB/APK and store compliance.

## Explicit non-goals for initial release
No therapy/diagnosis, medical efficacy claim, public social feeds, marketplace, large generative AI coach, automatic punishment/streak shame, or health-data collection by default.

## Change control / truthfulness
- “Compiled”, “emulator-tested”, “visually approved”, and “released” are **different** statuses.
- **PR #2 stays a draft**; `main` does not include v0.5 visual refinements.
- A debug APK is not a signed public-release package; do not use real irreplaceable journal data until export/backup is available.
- Keep user data out of public GitHub files and screenshot fixtures.
