# EVOLUNE — development roadmap v0.2

**Reviewed:** 2026-11-11 · **Working branch:** `feature/android-phase3d-qa` · **PR:** [#2](https://github.com/rayadinaveen98-ship-it/EVOLUNE/pull/2) (draft, unmerged)  
**Current build:** native Android **v0.5 debug**, visually inspired by approved Stillwater + Atelier HTML v0.3.  
**Status terminology:** ✅ Verified deliverable · ◐ Implemented partially / needs signoff · ○ Not yet begun.

> Product promise: **A calmer way to become better.** Progress without pressure; discipline without punishment; growth without comparison.

## Live stage-by-stage roadmap

| Stage | Workstream | Status | Evidence and remaining exit criteria |
|---|---|---|---|
| **00** | Repo and working identity | ✅ | `EVOLUNE` repo and source/docs exist; brand trademark/release identity not legally cleared |
| **01** | Discovery and product charter | ◐ | Growth architecture, key personas and journeys documented; target-user interviews and final MVP scope validation remain |
| **02** | Evidence and safety | ◐ | Evidence register and defensible habit/challenge behavior implemented; finish feature-level risk, privacy, and accessibility reviews. No clinical/therapy claim |
| **03** | UX / prototypes | ◐ | Approved Stillwater + Atelier direction; clickable HTML v0.3; native v0.5 screenshots available. User signoff on native look and interactions remains |
| **04** | Native architecture and data | ◐ | Kotlin domain, Room models/DAO/repository, Compose app, CI, and emulator instrumentation operational. Export/delete, backup policy, schema migration coverage and process-death/reboot resilience remain |
| **05** | Core Android MVP | ◐ | Today, create/customize challenges, challenge detail, practice logging, pauses/revisions, milestone review, Growth, Journal and Insights exist in native source; reliability/empty states and onboarding completeness still need manual review |
| **06** | Reflection and personalization | ◐ | Private journal create/edit/delete, optional mood/energy, growth areas, recurrence/cues/fallbacks present. Rich personalized guidance, controls, cross-challenge insights and user-managed data are unfinished |
| **07** | Premium Stillwater + Atelier | ◐ | Landscape + approved Atelier journal asset in native app, consistent greener navigation, gentle motion, responsive phone/tablet rendering. Tablet composition, touch/animation fidelity, reduced-motion and accessibility reviews remain |
| **08** | Integrated QA and stabilization | ◐ | Latest [Android CI](https://github.com/rayadinaveen98-ship-it/EVOLUNE/actions/runs/42975113458) and [emulator QA](https://github.com/rayadinave98-ship-it/EVOLUNE/actions/runs/42975112550) passed; 9 Android instrumented tests, 12 screenshots. Long-run use, device matrix, migration/export, reminders and install/update scenarios still untested |
| **09** | Limited pilot | ○ | Test with consenting real users on supported Android phones; gather usability, retention and reliability feedback; no efficacy claims |
| **10** | Release readiness | ○ | Final privacy/security review, data ownership controls, signed release APK/AAB, store requirements and listing, rollout/rollback plan; no public release yet |

## Completed technical milestones (verifiable)

- Preproduction: scientific evidence register, challenge architecture, UX journeys, Stillwater + Atelier visual exploration and interactive HTML v0.3.
- Domain: pure Kotlin ChallengeEngine distinguishes **unreported** versus explicitly **missed**, supports partial/rested/paused and calendar-day versus completed-session milestones; no universal “21-day habit formation” claim.
- Persistence: local Room v1 storage, correction history, journal records and schedule revisions; automated tests run on an Android emulator.
- Android: debug APK builds via GitHub Actions with native Compose UI and premium art integration.
- v0.5 visual QA: 320dp simulated phone and 853dp simulated tablet screenshots, Journal Atelier artwork, green navigation, baseline motion. **These screenshots are evidence of rendering, not final user approval.**
- Latest verification: [Android CI 42975113458](https://github.com/rayadinave98-ship-it/EVOLUNE/actions/runs/42975113458) **success** and [Android emulator QA 42975112550](https://github.com/rayadinave98-ship-it/EVOLUNE/actions/runs/42975112550) **success**; current automated scope = 9 tests, 12 native screenshot files.

## The remaining path — no extra phases added

1. **Visual and interaction approval:** v0.5 device review; improve unused tablet space, consistency of Challenge/Recovery/Review, large fonts, touch targets and reduced-motion behavior. Compare with HTML v0.3. Do not replace approved art or established challenge logic.
2. **Finish essential product reliability:** real user-controlled **export/delete**, backup/restore policy, upgrade/migration tests, process death and reboot continuity. Confirm no private journal data is logged or exposed.
3. **Reminders and schedule delivery:** design and implement consent-based on-device notifications (only if included in MVP), permission handling, time zone/DST changes, reboot rescheduling, cancellation/edit/pause behavior, and a documented reliability test matrix. Current v0.5 does **not** establish working scheduled notifications.
4. **Accessibility and device QA:** 320dp, typical phones, tablets/foldables; portrait and landscape; keyboard/IME; TalkBack, larger fonts, contrast, system animation disabled, performance. Run realistic multi-day and multiple-challenge tests, not only fresh-install tests.
5. **Pilot → release gate:** after user visual signoff and reliability criteria, merge reviewed source, run a small pilot, fix findings, and only then prepare signed AAB/APK and store compliance.

## Explicit non-goals for initial release
No therapy/diagnosis, medical efficacy claim, public social feeds, marketplace, large generative AI coach, automatic punishment/streak shame, or health-data collection by default.

## Change control / truthfulness
- “Compiled”, “emulator-tested”, “visually approved”, and “released” are **different** statuses.
- **PR #2 stays a draft**; `main` does not include v0.5 visual refinements.
- A debug APK is not a signed public-release package; do not use real irreplaceable journal data until export/backup is available.
- Keep user data out of public GitHub files and screenshot fixtures.
