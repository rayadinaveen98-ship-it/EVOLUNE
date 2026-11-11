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

**Android v1 development milestone — Data ownership (2026-11-11).** The existing Stillwater + Atelier native v0.5 visual baseline is preserved. A versioned local JSON export, strict validated import/restore with explicit REPLACE confirmation, and atomic deletion with explicit DELETE confirmation have been added to the review branch. [Android CI 42992160733](https://github.com/rayadinaveen98-ship-it/EVOLUNE/actions/runs/42992160733) and [Android emulator QA 42992160422](https://github.com/rayadinaveen98-ship-it/EVOLUNE/actions/runs/42992160422) **passed** (15 Android instrumented tests, zero failures). This is **not** release completion: manual document-picker/reinstall tests, notifications, accessibility, wider QA, consenting pilot and signed release remain. Source is still in [draft PR #2](https://github.com/rayadinaveen98-ship-it/EVOLUNE/pull/2), unmerged.


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
- [Locked first-version scope](docs/MVP_SCOPE_V1.md)
- [Versioned local data ownership and Android verification](docs/V1_DATA_OWNERSHIP_VERIFICATION.md)
- [Privacy and release-risk register](docs/V1_SAFETY_AND_RELEASE_RISKS.md)
- [Reminder scheduling contract](docs/V1_REMINDER_DELIVERY_CONTRACT.md)
- [Limited pilot protocol (not yet conducted)](docs/V1_LIMITED_PILOT_PROTOCOL.md)
- [Native v0.4 visual parity, emulator evidence and remaining gaps](docs/PHASE3E_NATIVE_VISUAL_PARITY.md)
- [Phase 3C verified build and remaining gaps](docs/PHASE3C_BUILD_VERIFICATION.md)
- [Phase 3D device QA checklist and evidence status](docs/PHASE3D_DEVICE_QA.md)
- [Room persistence source](core/data/src/main/kotlin/studio/evolune/data)
- [Tested Kotlin challenge engine](domain/README.md)
- [Decision log](docs/DECISIONS.md)

## Safety and data principles

EVOLUNE is initially a general wellness and self-development concept, **not** therapy, diagnosis, emergency support, or a medical device. Any clinical features require a separate evidence, regulatory, and safety review. No personal journal, mood, or health data belongs in this public repository.

Copyright © project owner. No license granted by default; license choice pending.
