# EVOLUNE V2 — Stage 01 Brand Master System (proposal v0.1)

**Current state:** Brand system under review. Product owner selected **Direction A — The Open Path** on 2026-11-11. Original SVG mark prototypes, Android VectorDrawable candidates and actual-size icon preview have now been prepared; **final mark geometry, wordmark typeface, icon integration and brand clearance are not yet signed off**. No V1 app source was changed.

## 1. Identity and positioning

**EVOLUNE** — a gentle personal-development space for intentional practice and reflection.

**Core message:** *A calmer way to become better.*  
**Personality:** grounded, intimate, patient, honest, literary, quietly optimistic.  
**Never:** medical, competitive, streak-obsessed, saccharine, mystical, preachy, childish, aggressively minimalist or visually busy.

- **Stillwater** supplies spacious nature, soft atmosphere, water, stone paths, patient transformation.
- **Atelier** supplies the editorial reading surface, tactile paper, refined typography, accessible controls, warmth.
- **Together:** a hand-crafted place to act *and* reflect, not a meditation wallpaper or a diary-only app.

## 2. Logo / app icon candidate directions

All proposals must read as original **single-color silhouettes** at 24dp and avoid looking like wellness/leaf clip art.

| Concept | Geometry | Distinctiveness / risks | Candidate role |
| --- | --- | --- | --- |
| **A. The Open Path** — recommended for concept testing | A compact rising three-step stone/path motif opening onto a small horizon dot, with enough negative space to remain legible; no closed growth ring | Connects path + future, but avoid resembling a Wi‑Fi signal or generic sunrise | Flagship exploration |
| **B. Living Monogram** | Construct an E from two curved interlocking mineral forms; quiet depth without thin decorative strokes | Ownable letterform possible; could feel too corporate if too symmetrical | Alternative |
| **C. Atelier Seal** | One continuous irregular hand-crafted oval enclosure, with a single forward opening / inlaid stepping mark | Warm and tactile, but complex contours may turn to mush at launcher scale | Alternative |

**Decision:** A is the *selected direction* (not just a recommended option). [Open Path vector source and handoff](assets/open-path/README.md). This does not yet approve the exact small-scale icon geometry. Judge each in actual Android mask crops, monochrome theme, dark wallpaper, 24dp and blurred home screen. No gradients, shadows, illustrations or tiny letters inside the launcher icon. Wordmark may use editorial display type with custom kerning, but only after a full vector review.

### Android adaptive icon delivery contract
- Two 108×108dp layers (foreground/background), centered primary silhouette in the safe **66×66dp** region; separate monochrome single-color layer for theme support.
- Test squircle, circle and OEM masks; include storefront icon, notification small icon, splash mark, app top-bar mark and vector source.
- Do not confuse a raster moodboard image with real editable path/vector brand artwork.
- Check name uniqueness / trademark availability before commercial brand lock.

## 3. Color candidates / semantic palette

Base values are proposals evolved from existing approved app styling. **Contrast readings below were calculated against the actual light surfaces** using the WCAG relative-luminance formula, not visually guessed.

| Token | Hex | Use | Contrast on Cream #FAF8F3 |
| --- | --- | --- | --- |
| \`ink.strong\` | \`#26392D\` | Long-form body and key titles | ~11.6:1 |
| \`ink.brand\` | \`#36553E\` | Action text / primary button fill | ~7.8:1 |
| \`ink.secondary\` | \`#65746C\` | Muted body and helper text | ~4.6:1 |
| \`sage.decorative\` | \`#64816B\` | Borders, decorative icons and large type only unless rechecked | ~4.0:1 — fails 4.5:1 small text |
| \`cream.canvas\` | \`#FAF8F3\` | App canvas | — |
| \`paper.card\` | \`#FFFEFA\` | Cards and readable paper surfaces | — |
| \`pale.sage\` | \`#E7ECE2\` | Soft illustrated backgrounds and selection surfaces | — |
| \`line.soft\` | \`#E5E2D9\` | Nonessential dividers | — |
| \`accent.clay\` | \`#B89A63\` | Tiny non-text warmth / artwork accent only | ~2.5:1 — not usable for small text |
| \`status.attention\` | \`#905C3E\` | Explanatory warning text (not alarm) | ~5.2:1 |

**Contrast rules:** Body text 4.5:1 minimum; large text / non-text UI elements use applicable standards (large text ≥3:1, functional controls/boundaries ≥3:1 where required). Do not rely on color alone for completed/partial/rest/unreported. Audit every text-on-surface *pair* including dark mode; numbers above are only on the Cream surface and are not a full palette audit.

**Dark mode:** Future dedicated palette, not a cheap inversion; maintain warm softness with high text contrast, test OLED and low light. Ship only when each state has reviewed dark assets.

## 4. Typography candidates

**Candidate pairing 1 (recommended for UI trials):** *Lora* editorial serif headings + *Inter* sans body/UI.  
**Candidate pairing 2:** *Fraunces* display serif for short hero text + *Manrope* sans UI.  

Neither font is finalized. Check redistribution license, Android Latin/Telugu fallback, numerals, native font rasterization and 200% system scaling; do not ship unreviewed font binaries. System serif/sans fallback is acceptable if visual and legal requirements aren't met.

| Role | Baseline size | Guideline |
| --- | --- | --- |
| Display / immersive phrase | 34–40sp | Two–three short lines max, responsive; never used for controls |
| Page title | 28–32sp | Editorial, clear line height |
| Section heading | 20–24sp | Calm grouping; no excessive letter spacing |
| Card title | 17–20sp | Compact but strong |
| Body | 15–16sp | 1.35–1.5 line-height, unconstrained at font scale |
| Secondary / meta | 13–14sp | At least 4.5:1 contrast; don't default to 9sp |
| Buttons | 14–16sp | Sentence case; literal, useful verbs |
| Label / overline | 12sp+ | Small caps only for short noncritical descriptors |

Prior v0.5–0.8 used several 9–11sp labels; V2 must systematically remove undersized critical text. Do not hard-code a single exact line count for dynamic type.

## 5. Grid, spacing, corners, strokes, elevation

- **Spacing grid:** 4dp base with typical rhythm 4 / 8 / 12 / 16 / 20 / 24 / 32 / 40 / 48; screen horizontal padding **20dp compact**, 24–32dp medium, adaptive max content widths ~640–760dp where single-column reading is suitable.
- **Cards:** 16–24dp radius; nested elements 10–14dp; never round every rectangular element identically.
- **Outline:** 1dp subtle tonal stroke for cards; avoid opaque contrast-heavy outlines.
- **Elevation:** predominantly 0–2dp surface depth with selective softness; no blurry stack of shadows.
- **Touch:** minimum effective 48×48dp interactive region, with spacing that doesn't cause overlapping targets.
- **Navigation:** compact phone bottom bar with wrapping labels (or deliberate short aliases); expanded tablet navigation rail/list-detail, not a stretched phone navbar. Never hide destination semantics or active state.
- **Page composition:** hero 15–25% first viewport in practice-focused screens, not fixed across devices; one primary next action must remain visible above first scroll when appropriate.

## 6. Iconography system

A single original **rounded geometric monoline** family (~1.75–2dp stroke at 24dp canvas); optical corrections are more important than mathematical rigidity.

System families:
- **Destinations:** Today (shelter/threshold), My Growth (branch/stacked path), Challenges (path marker), Journal (open page/pen), Insights (measured arc/plot).
- **Actions:** add, edit, log, adjust, pause, restore, export, delete, close, back, more, reminder, privacy.
- **Statuses:** completed, backup, partial, rested, explicitly missed, unreported, paused, unscheduled (shape + text; never color-only).

Required states: default, pressed, selected, focused, disabled, warning. Test 16/20/24/32dp glyph rendering as required, keep actual tap area ≥48dp. No emojis as production icons.

## 7. Art language / motion rules

- Original hand-crafted assets: miniature natural landscapes, stone and moss, lived-in sunlit journal surfaces, ceramic warmth, paper texture.
- Artwork is **framing and storytelling**, not a progress meter or accessibility barrier.
- Different scenes for Today, Reflection, Recovery, Growth, Insights; they share color, light direction, materials, atmosphere.
- 2.5D/pre-rendered art prioritized over continuous real-time 3D until profiling warrants it.
- Motion tokens proposed: **quick** 100–160ms, **standard** 180–260ms, **reflective** 350–500ms; never arbitrarily delay app readiness. Reduced-motion static path is mandatory.
- Android system splash behavior, device power/performance, TalkBack and disabled animation scale override decorative motion. Sound always off by default.
- Use a performance budget and actual low-end device screenshot/frame-time evidence before claiming parity.

## 8. Microcopy system

Preferred: “Your next small step”, “Leave unreported”, “Adjust tomorrow”, “Record what happened”, “Rest intentionally”, “Your history stays as it was”.
Avoid: “You failed!”, “Don't break the chain”, “Fix yourself”, “Formed a habit”, “You've lost progress”, “Mandatory check-in”.

All numbers should have visible meaning (what is counted, eligible opportunities, whether calendar days or sessions).

## 9. Stage 01 deliverables / approval boundary

**Prepared:** V2 brand-system plan, selected Open Path direction, original path-based SVGs and Android adaptive-icon XML templates, light/dark/monochrome render tests at 24/48/108/512px, and measured icon safe-zone radius (<33dp). The original board is a preview, not a source vector or visual sign-off.

**To build before Stage 01 is marked done:**
1. **Selected A direction:** Review prepared Open Path source in actual 24/48/108dp and light/dark/monochrome launcher contexts; confirm/refine exact geometry. B/C no longer need production asset work unless A is rejected.
2. Side-by-side screen reference for one common Today card, bottom nav, Journal and Settings using shared tokens.
3. Typography license/fallback and Telugu/English scaling samples; icon family samples.
4. Contrast matrix on *all* surfaces and 100/140/200% text preview.
5. Written owner confirmation of mark and final tokens; commit production asset files only after approval.

Stage 01 is **IN PROGRESS**: *direction A approved; asset geometry and all other design tokens remain in review*. No V2 launcher icon is installed in the Android app.

### Authoritative implementation references
- [Android adaptive launcher icon system](https://developer.android.com/develop/ui/compose/system/icon_design_adaptive)
- [Android window size classes](https://developer.android.com/develop/adaptive-apps/guides/use-window-size-classes)
- [Android accessible touch targets](https://developer.android.com/guide/topics/ui/accessibility/apps)
