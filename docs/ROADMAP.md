# EVOLUNE development roadmap — v0.1

## Status key
- ✅ Done: confirmed in repository or research.
- ◐ In progress: planning/review.
- ○ Not started: no implementation claims.

| Stage | Workstream | Status | Exit criterion |
| --- | --- | --- | --- |
| 00 | Repository and working name | ✅ | GitHub repository found and initialized with foundation docs |
| 01 | Discovery and product charter | ◐ | Growth architecture, challenge behavioral rules and first journeys **drafted**; target user and scope still to be validated |
| 02 | Evidence + safety framework | ◐ | Initial register expanded to COM-B, SDT, planning, monitoring; complete feature-level risk review pending |
| 03 | UX journeys and visual art direction | ◐ | Stillwater + Atelier chosen; HTML v0.3 interactive flows tested, real Android screenshots and accessibility review pending |
| 04 | Technical design | ◐ | Room v1 entities/DAO/repository sources added; Android compile, generated schema, privacy/backup policy and build pinning pending |
| 05 | MVP implementation | ◐ | Kotlin engine and persistence sources committed, JVM + SQLite contract checks passed; no Android app compile, Room runtime test, Compose screens or APK yet |
| 06 | Reflection & personalization | ○ | User-governed optional check-ins, difficulty adjustments, privacy controls |
| 07 | Premium visual system | ○ | Original Blender/mobile assets, motion, accessibility and performance |
| 08 | Integrated QA | ○ | End-to-end behavior, data reliability, accessibility, device matrix, safety |
| 09 | Small pilot | ○ | Real-user usability and effectiveness feedback; iterate |
| 10 | Release readiness | ○ | Security/privacy review, store listing, release process |

## Completed pre-production artifacts (not implementation)
- [Growth architecture](GROWTH_ARCHITECTURE.md)
- [Challenge engine behavior contract](CHALLENGE_ENGINE_SPEC.md)
- [Experience journeys](EXPERIENCE_JOURNEYS.md)
- [Screen & interaction blueprint](SCREEN_BLUEPRINT_V01.md)
- [Visual exploration v0.1](VISUAL_EXPLORATION_V01.md)

## New technical artifact (2026-11-11)
- [Android architecture and limitations](ANDROID_ARCHITECTURE_V01.md)
- [ChallengeEngine Kotlin source and checks](../domain/README.md)
- [Phase 3B source, SQLite restart contract and verification gaps](PHASE3B_PERSISTENCE.md)
- HTML interactive prototype v0.3 was provided in the conversation; this is not native Android code.

## Initial MVP candidate (not yet locked)
A user can select a meaningful domain, set up **one repeatable challenge**, plan daily action with optional if-then cue, see a simple Today screen, record completion/partial/rest/missed accurately, recover after interruption, review at a milestone, and choose whether to continue.

**Deliberate exclusions:** therapy, complex social feed, marketplace, broad AI coach, full workout catalogue, excessive gamification.

## Next work in order
1. Set up reproducible Android Gradle + compatible Kotlin/KSP/Room versions, and compile committed Room source (not yet verified).
2. Export Room's generated schema and run Room integration/instrumentation tests for persistence, revisions, restart and deletion. Draft SQLite contract alone is not a runtime test.
3. Connect tested engine and repository to ViewModels and native Compose Today/Challenge/Recovery/Review screens.
4. Port Growth, private Journal and Insights with export/delete controls.
5. Test real Android screenshots, accessibility, device restart/reminders and full integrated functionality. User approves visual adjustments before finalizing.

**Change control:** New features enter the backlog, not the active stage, until their scope, benefit, evidence, and cost are reviewed. Confirm shipped versus proposed work in every status update.
