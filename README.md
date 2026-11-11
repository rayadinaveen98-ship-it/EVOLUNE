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

**Native Android v0.5 development build — verified CI and emulator QA (2026-11-11).** Stillwater + Atelier HTML v0.3 remains the visual reference. Native Compose includes custom challenges, Today, Growth, Recovery, Milestone Review, private Journal, Insights, approved Stillwater/Atelier artwork, and responsive phone/tablet rendering. [Android CI](https://github.com/rayadinave98-ship-it/EVOLUNE/actions/runs/42975113458) and [emulator QA](https://github.com/rayadinave98-ship-it/EVOLUNE/actions/runs/42975112550) **passed** (9 Android tests, 12 screenshots). **Visual approval, export/backup, reminder reliability, wider-device QA and release preparation are still pending.** Debug build only; do not use for irreplaceable private journal entries. Source remains in [draft PR #2](https://github.com/rayadinave98-ship-it/EVOLUNE/pull/2) and is **not merged to main**. See the [current roadmap v0.2](docs/ROADMAP.md).


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
- [Native v0.4 visual parity, emulator evidence and remaining gaps](docs/PHASE3E_NATIVE_VISUAL_PARITY.md)
- [Phase 3C verified build and remaining gaps](docs/PHASE3C_BUILD_VERIFICATION.md)
- [Phase 3D device QA checklist and evidence status](docs/PHASE3D_DEVICE_QA.md)
- [Room persistence source](core/data/src/main/kotlin/studio/evolune/data)
- [Tested Kotlin challenge engine](domain/README.md)
- [Decision log](docs/DECISIONS.md)

## Safety and data principles

EVOLUNE is initially a general wellness and self-development concept, **not** therapy, diagnosis, emergency support, or a medical device. Any clinical features require a separate evidence, regulatory, and safety review. No personal journal, mood, or health data belongs in this public repository.

Copyright © project owner. No license granted by default; license choice pending.
