# EVOLUNE

> **Working title · Personal Development OS · Pre-production**

**Product promise:** A calmer way to become better.

EVOLUNE is an Android-first, personal self-development experience for choosing meaningful changes, practicing them through personalized challenges, reflecting on them, and sustaining progress over time.

**Principles**
- Progress without pressure. Discipline without punishment. Growth without comparison.
- The product adapts to the person; the person need not adapt to the product.
- Mental and physical growth, with privacy, accessibility, and user agency.
- Clear, evidence-informed behavior-change methods; never pretend an app or technique is clinically proven when it is not.
- Beautiful original artwork, calm microinteractions, restrained motion, and strong performance.
- Challenges can be 7/21/30/60/90 days, but completion never guarantees permanent habit formation.
- Missed days should support reflection and recovery, not shame or destructive streak resets.

## Current status

**Phase 3B: Local persistence source committed.** Stillwater + Atelier remains the approved prototype direction. Pure-Kotlin challenge engine tests, independent storage codec tests, and on-disk SQLite restart/revision tests passed locally. Room entities, DAO, and repository source are now in `core/data`. **Room has not been compiled in an Android Gradle project or tested on-device; no APK exists.** Compose screens, platform persistence verification, reminders, artwork integration and device QA are pending.

## Project documents

- [Product brief](docs/PRODUCT_BRIEF.md)
- [Growth architecture](docs/GROWTH_ARCHITECTURE.md)
- [Challenge engine behavior specification](docs/CHALLENGE_ENGINE_SPEC.md)
- [Experience journeys](docs/EXPERIENCE_JOURNEYS.md)
- [Screen & interaction blueprint](docs/SCREEN_BLUEPRINT_V01.md)
- [Three visual concepts](docs/VISUAL_EXPLORATION_V01.md)
- [Scientific evidence register](docs/EVIDENCE_REGISTER.md)
- [Initial competitor scan](docs/COMPETITOR_SCAN.md)
- [Experience and visual direction](docs/DESIGN_DIRECTION.md)
- [Staged roadmap](docs/ROADMAP.md)
- [Android architecture v0.1](docs/ANDROID_ARCHITECTURE_V01.md)
- [Phase 3B Room persistence source, tests and verification gaps](docs/PHASE3B_PERSISTENCE.md)
- [Room persistence source](core/data/src/main/kotlin/studio/evolune/data)
- [Tested Kotlin challenge engine](domain/README.md)
- [Decision log](docs/DECISIONS.md)

## Safety and data principles

EVOLUNE is initially a general wellness and self-development concept, **not** therapy, diagnosis, emergency support, or a medical device. Any clinical features require a separate evidence, regulatory, and safety review. No personal journal, mood, or health data belongs in this public repository.

Copyright © project owner. No license granted by default; license choice pending.

## Phase 3C Android source (2026-11-11)

The repository now contains a **Kotlin + Compose Android source project** with `app`, `domain`, and `core:data` modules, a Room-backed local data layer, instrumented test source and a GitHub Actions build workflow. The native UI is an integration shell and **not yet a final visual match** to the approved Stillwater + Atelier HTML v0.3 prototype.

- [Phase 3C implementation and limitations](docs/PHASE3C_ANDROID_INTEGRATION.md)
- [GitHub Android CI](.github/workflows/android-ci.yml)

**Important:** An Android APK or working Android persistence is only confirmed after CI builds the app and on-device tests pass. No user data or secrets are stored in this public repository.
