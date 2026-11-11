# EVOLUNE V2 — Open Path logo asset candidate

**Direction:** A — **The Open Path**, explicitly chosen by the product owner on 2026-11-11.  
**Source status:** Original custom path-based **vector design prototype**, not a finished, legally cleared commercial brand mark.  
**Production integration status:** **NOT integrated into the Android app**; the V1 development/reliability branch is unchanged.

## File inventory

| File | Intended use |
|---|---|
| `open-path-mark-light.svg` | Forest stone silhouette and brass horizon dot on cream/light surfaces |
| `open-path-mark-dark.svg` | Paper stone silhouette and brass horizon dot on deep forest/dark surfaces |
| `open-path-mark-monochrome.svg` | Pure single-color mark (app theming/print masks) |
| `open-path-adaptive-foreground.svg` | Transparent adaptive icon foreground SVG (regular) |
| `open-path-adaptive-monochrome.svg` | Transparent single-color foreground SVG |
| `open-path-adaptive-background.svg` | Forest flat background |
| `android/ic_evolune_foreground.xml` | Candidate Android VectorDrawable foreground |
| `android/ic_evolune_monochrome.xml` | Candidate Android monochrome vector |
| `android/ic_evolune_background.xml` | Candidate Android VectorDrawable forest background |
| `android/ic_evolune_launcher.template.xml` | Illustrative adaptive launcher XML template; **not installed** in app resources |
| `open-path-wordmark-lockup-prototype.svg` | Typesetting preview with font-family fallback; **not an outlined/font-independent commercial wordmark** |

**Source of truth:** The 108×108 SVG geometry is the canonical source. The XML translations are Android **candidates** until they compile and render correctly on devices.

## Geometry and visual rules

- Three asymmetrical, stone-like paths decrease in size toward one isolated horizon dot.
- Viewport is `108×108`; design center `54×54`.
- All visible path pixels fit within **31.84dp** radial distance from center in a 108dp canvas (measured at 10x raster resolution), inside the assumed conservative **33dp circular safe zone**.
- Avoid adding outlines, untested gradients, mini lettering, extraneous leaf imagery, aggressive drop shadows, or splash-only effects to the icon.
- Use the exact same geometry for launcher mark, top-bar mark, splash and single-color themed appearance.
- Existing color candidates: forest `#36553E`, paper `#FFFEFA`, brass `#B89A63`; the mark remains fully legible with the horizon dot recolored to the single monocolor.
- Logo acts as **brand**; stone count is never equated to streaks, literal days or personal worth.

## Review / proof

- SVG files were rendered successfully by CairoSVG locally at 24px, 48px, 108px and 512px.
- Comparison previews were produced for light, dark, monochrome and mocked circle/squircle/rounded icon crops.
- Small-scale review at 24px is exceptionally strict: the symbol survives as 3 stone bands plus a dot but is intended primarily for launcher-mark and branded surfaces, not tiny action buttons.
- The early preview is an **image**, not proof of OEM mask or Android adaptive-icon integration. Review real emulator masks once installed.
- Font preview uses an available serif fallback; final wordmark outlines require type licensing/review and exact custom kerning.
- Before final selection: verify against lookalike marks/trademarks, print backgrounds, 200% UI scale and real Android launchers.

## Implementation handoff (not yet done)
After visual approval:
1. Commit a finalized outlined wordmark and audited colors.
2. Copy Android vectors to resource paths, configure `mipmap-anydpi-v26` adaptive icon and themed icon; preserve manifest launcher semantics.
3. Run Android builds and take masked launcher screenshots from multiple simulated/real launchers.
4. Ensure Android cold start and splash icon match the same mark; never force multi-second launch delay.
5. Create vector/color/monochrome, splash, notification (separate Android small glyph), and marketing exports with versioned provenance.
