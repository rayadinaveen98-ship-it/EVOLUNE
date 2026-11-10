# EVOLUNE — visual direction exploration v0.1

**Status:** Three unapproved art/experience directions prepared for owner review on 2026-11-11. Concept illustrations produced in the conversation as mood/art explorations, not source assets for an app or implemented Android screens. No official palette, logo, typography, or motion system is locked.

**Shared product constraint:** All directions preserve the same challenge engine, onboarding decisions, privacy controls, progress semantics, and gentle recovery behavior. Visual choice must not alter functionality.

## A — Stillwater / Living Landscape
**Emotional promise:** Make growth feel spacious, natural, patient.  
**Visual system:** Sculpted natural terrain, stepping stones, fluid water, growing trees, tactile mineral surfaces, warm daylight and distant atmospheric layers; restrained off-white and sage surfaces.
**Today:** One focused action in a clean high-contrast card. A compact landscape vignette responds to completing or intentionally resting. Vital controls remain visible independent of the artwork.
**Challenge:** A chronological path through the landscape maps *days or sessions accurately*, never conflating them. Milestone markers are subtle and explanatory.
**Recovery:** Weather quiets, the trail remains, and the user sees easy practical choices; no dead plant, broken landscape, or loss of previously earned scenery.
**Motion:** Low-amplitude water ripple, gentle leaf sway, soft parallax on scroll, reduced-motion static option.
**Risks:** High-fidelity realtime 3D could hurt app performance; avoid implying that garden growth is a scientific indicator of mental improvement. Ensure graphics do not obstruct action.
**Production feasibility:** Favor optimized Blender-authored 2.5D/short prerenders, lightweight particles and vector layers after Android perf testing.

## B — Atelier / The Living Journal
**Emotional promise:** Your growth is a meaningful collection of small, personal moments.  
**Visual system:** Sunlit stone, textured paper, hand-made ceramics, soft shadows, journal-like editorial typography, small 3D vignettes. More quiet and minimal than gamified.
**Today:** Editorial page hierarchy: one large action, optional reflection and a concise factual progress summary. Subdued scene illustration lives around rather than behind interactive text.
**Challenge:** Week-by-week or session-by-session journal pages, beautiful progress details and annotations; no decorative clutter in entry forms.
**Recovery:** A blank space remains a valid part of the record. Give resume/adjust choices with compassionate, neutral copy.
**Motion:** Page-like reveals, tactile card transitions, minute highlight changes; reduced-motion fallback.
**Risks:** Too much skeuomorphism might consume screen space; paper textures need contrast checks; mood could feel like a diary rather than an active OS.
**Production feasibility:** Best lightweight candidate; pre-rendered original assets + standard native layout and effects.

## C — Moon Atlas / Quiet Constellations
**Emotional promise:** A private sanctuary for introspection and deliberate personal change.  
**Visual system:** Night blues, moonlight, emerald seedlings, copper lantern light, deep contrast, glass-like but readable surfaces; cinematic quiet rather than gamification.
**Today:** Dark immersive header and a strongly separated readable action card; clear activity statuses, no luminous UI gimmicks.
**Challenge:** A minimal lunar timeline with distinct planned/completed/partial/unreported states via both text and glyphs. The moon must not claim a 21-day cycle is a validated habit-forming rule.
**Recovery:** A small anchor of light remains through pauses, along with practical next-step options. No visual punishment.
**Motion:** Slow lighting drift, stars barely moving, shadow transitions; optional ambient sound strictly opt-in and not a required interaction.
**Risks:** Contrast failures on dark surfaces, overdramatic mood for everyday use, battery cost if extensive animation, and inappropriate mood associations.
**Production feasibility:** Render scenes efficiently, keep UI mostly native, test in daylight and on entry-level Android devices.

## Evaluation (provisional, qualitative; not research results)
| Criterion | Stillwater | Atelier | Moon Atlas |
| --- | --- | --- | --- |
| Emotional atmosphere | Open, restorative | Personal, grounded | Immersive, reflective |
| Readability potential | High with isolated cards | High with sufficient paper contrast | High if strong text contrast |
| Art production complexity | Medium–high | Low–medium | Medium–high |
| Small-screen risk | Scenery can dominate | Decorative margins may waste area | Dark effects can obscure controls |
| Possible flagship metaphor | Landscape evolves | Personal story accumulates | Phases of a journey |

## Visual test matrix before lock
- Today: first-time, scheduled action, all done, no actions today.
- Challenge: 21 calendar days vs 21 sessions; partial, intentional rest, unreported, paused, resumed.
- Create challenge: one activity, time/frequency, backup, reminder off.
- Recovery: no guilt or fake record repair.
- Review: numbers with numerators and denominators, optional reflections, continue/finish.
- User accessibility: light/dark, reduced motion, color blindness, screen readers, small and large text, budget Android GPU.
- Privacy: no sensitive reflections visible in previews/notifications without consent.

## Quality gate and next artifact
1. Owner reviews three art directions, chooses one or a specific hybrid.
2. Produce **real UI mockups** for Today, Challenge, Recovery and Milestone Review.
3. Confirm motion and artwork interaction targets.
4. Then implement a navigable Android prototype; only afterwards consider final asset production.

Do not silently switch to an alternative style or change challenge functions because of aesthetic exploration.
