# EVOLUNE V2 — Stage 01 review and sign-off checklist

**Stage status:** Under design; **Direction A selected** by the owner (2026-11-11), exact artwork and other tokens *not yet signed off*.  
**Scope:** The design system and real asset prototypes, **not** changes to the V1 challenge engine.

## Sign-off matrix

| Test | Pass evidence | State |
| --- | --- | --- |
| Chosen Open Path mark (direction A) | Original SVG paths, Android XML candidates; previews rendered at 24, 48, 108, 512px and light/dark/monochrome | ◐ Prototypes created; user review pending |
| Logo legibility | Path silhouette measured within 31.84dp radius from 54dp center; circle/squircle/rounded crops mocked | ◐ Offline render checked; real launcher testing pending |
| Brand uniqueness | Manual app store/trademark preflight for selected mark and wordmark | ☐ Pending |
| Palette | Correct WCAG contrast for every text/surface/status combination | ☐ Pending |
| Typography | Licensed fonts, fallback for Telugu/English, visual review at 100/140/200% scales | ☐ Pending |
| Icon consistency | Navigation/status/action sample set, 24dp and 48dp touch targets | ☐ Pending |
| Spacing and shapes | One-page visual spec and actual Compose primitive parity | ☐ Pending |
| Art provenance | Ownership/license recorded and original asset filenames agreed | ☐ Pending |
| Motion | Reduced-motion static version and real cold/warm start constraints | ☐ Pending |
| Cross-device | 320dp, ~393dp, 600–840dp, expanded tablet, landscape/multiwindow | ☐ Pending |
| Decision record | Exact chosen mark + type + color variants + exclusions signed off | ☐ Pending |

## Chosen direction and final artwork criteria
Direction **A — Open Path** has been selected; B and C are archived as alternatives. Score the **actual final A vector variant** 1–5 before final artwork approval:

- Originality / memorability (25%)
- Small-scale clarity (25%)
- Alignment with Stillwater + Atelier (20%)
- Monochrome/adaptive implementation feasibility (15%)
- Long-term brand flexibility (15%)

Do not select the mark by whether its 3D mockup looks beautiful; test the flat silhouette first. Archive rejected concepts and do not copy them into production.

## No-regression contract
- Existing v1 Android draft PR #2 and current reminder reliability work remain untouched by these docs.
- No medical claims, punitive streaks, fictional statistics or coerced onboarding.
- No new onboarding field unless it has useful real behavior or is an existing user choice.
- Every image, font, icon, animation and color becomes a versioned source/design token once approved.
- Do not publish to Play Store or change a release signing key as part of Stage 01.

## First review artifact
[Original Open Path SVG and Android candidate assets](assets/open-path/README.md), with light/dark/monochrome and actual-size PNG review exported separately in this design conversation.

**Owner decision requested after asset review:** confirm or refine the *specific Open Path geometry*, icon colors and wordmark typography. Stage 02 welcome production starts after artwork identity is signed off, not only because Direction A was selected.
