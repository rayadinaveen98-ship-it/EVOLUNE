# EVOLUNE V2 — Experience Excellence / Master Roadmap

**Status:** Approved *programme direction*, design specification in development.  
**Design foundation:** Stillwater + Atelier (already selected by product owner).  
**Reference:** HTML v0.3 and native Android v0.5–v0.8 visual/reliability evidence, not an approved release build.  
**Working branch:** \`design/v2-experience-excellence\` — **design-only**. Do not rewrite or merge the active V1 Android reliability branch during exploration.

## Scope and release boundary

This is a **visual, language, brand, onboarding presentation and interaction-quality update**. The next separately planned major update will focus on deeper functionality. No unrelated habit engines, gamification, subscriptions, AI coaching, social features, or new backend services in this pass. Where a current screen is incomplete, expose that gap rather than inventing an interactive façade.

**Ten finite stages**. Each stage has a frozen deliverable and exit gate; no extra phases appended without written change control.

| Stage | Output / deliverables | Exit gate |
| --- | --- | --- |
| **01 — Brand Master System** | Logo concepts, wordmark, adaptive launcher icon / monochrome variant, color, type, shapes, spacing, grid, icon family, semantic tokens, mood/asset rules | Three scalable mark options compared; one selected; token contrast audited; vector/adaptive specs reviewed |
| **02 — Welcome & Entry** | Android splash-compatible brand reveal, first-launch welcome, return-user entry, static/reduced-motion path, loading states | Measured warm/cold start; no fake delay; screen reader and skip/system-back; no visible flicker |
| **03 — Guided Setup** | Visually unified intro, growth-area choice, intention, one activity, calendar vs sessions, optional cue/fallback/reminders, schedule preview and privacy | Full first-run path actually navigable; clear optional/skippable fields; state restores after rotation/process recreation |
| **04 — Today & Daily Guide** | Empty/active/partial/rested/paused/completed/unreported daily states, primary action hierarchy, rich but restrained hero, current challenge, reflection entry | Every state grounded in real saved data; primary action discoverable within first viewport; no fabricated progress |
| **05 — Screen Family** | My Growth, Challenges, Create, Detail, Recovery, Milestone Review, Journal, Insights, Reminders, Data & Privacy, confirmations, errors | Same design tokens; each critical screen has reviewed narrow/typical/tablet screenshots and complete back navigation |
| **06 — Navigation & Components** | Bottom navigation / large-screen rail, top app bar, all states of buttons/inputs/chips/cards/sheets/dialogs, original iconography, component library | Native accessible component catalogue; 48dp targets; no clipped labels; focus and semantic states |
| **07 — Type, Content & Formatting** | Editorial type pairing, hierarchical type scale, localized dates/numbers, inclusive copy system, factual progress labels | 100–200% font-scale review, wrapping/truncation policy, reading order and contrast checks |
| **08 — Art & Motion** | Original Stillwater/Atelier hero/illustration family, daytime palettes, texture, transitions, optional ambient micro-motion, reduced-motion/static assets | Assets provenance documented; optimized for memory and frame time; no content obscured; no punitive growth metaphor |
| **09 — Settings & Preferences Presentation** | Appearance/accessibility, reminder, experience, data & privacy, About, replay-guide controls; only genuinely functional controls exposed | Every displayed control has real backing behavior or is explicitly absent; preview/restore/cancel states supported |
| **10 — Master Visual QA & Integration** | Complete Android integration, screenshots/video walkthrough, animation/performance QA, small phone, regular phone, tablet, accessibility and copy signoff | Reviewed side-by-side with baseline and owner approval; tested release candidate remains subject to V1 security, pilot and signing gates |

### Current delivery status — 2026-11-11

| Work | Verified state | Next gate |
| --- | --- | --- |
| Stage 01: Brand | Direction **A / Open Path selected**, original SVG mark and Android icon templates committed | Exact geometry + brand type approval, Android OEM mask and palette QA |
| Stage 02: Welcome | **Interactive 5.8s first-run HTML v0.1** committed at [prototype](prototypes/open-path-welcome-v0.1.html); browser QA, replay/skip/reduced motion and handoff tested | Native Android Compose implementation and device/accessibility/performance QA |
| Stages 03–10 | Roadmap established, not implemented in V2 branch | Proceed sequentially after Stage 01 identity lock and Stage 02 native gate |

The runnable browser study is not equivalent to a native splash screen or completed visual release. Current V1 Android PR remains unaffected.

### Delivery order without rework
- **Design system:** Stage 01. Icons, type, color and spacing are governed before screen rewrite.
- **Core presentation:** Stages 02–05. Welcome → setup → Today → all screen families.
- **Consistency & production assets:** Stages 06–09. Components, typography, artwork/motion, settings.
- **Consolidation:** Stage 10. One comprehensive design QA cycle and final parity review.

### Android launch policy for Stage 02
The Android system splash is a platform-controlled startup surface, **not** a forced cinematic video. Android's own guidance recommends an animated splash icon of approximately **1 second or less**. Any longer atmospheric “welcome experience” belongs **after** the app is ready, as a first-run, user-skippable introduction; returning users must not be delayed artificially.

### Cross-cutting constraints
- Preserve Stillwater atmosphere (living path, water, daylit landscape) and Atelier readability (paper, warm tactile material, editorial content).
- One strong primary action per task; no non-functional “Start” buttons.
- Never call unreported days failed or imply 21-day milestones permanently form habits.
- Preserve Room-backed local privacy, schedule history and previously approved reminder semantics.
- No unapproved access to real user journal data; demo/test screenshots synthetic only.
- Responsive to dynamic window-width classes, IME, multi-window, large fonts, screen readers, reduced motion.
- Separate **design approved** from **source implemented**, **emulator tested**, **device reviewed**, **publicly released**.
- Do not call visual Stage 10 equivalent to public Play Store release readiness; overall V1 security, migration, pilot and signed package remain distinct gates.

## Immediate next checkpoint
**Direction A — The Open Path has been selected**. Created original scalable mark prototypes and Android adaptive icon templates in [assets/open-path](assets/open-path/README.md). Next: review exact A geometry at small sizes and launcher masks; finalize wordmark, typefaces, accessible tokens and brand lock-up; then proceed to welcome. Do not pretend these design files are installed in Android. Only after selection move to the welcome animation production brief. Do not silently finalize a logo from this written shortlist.
