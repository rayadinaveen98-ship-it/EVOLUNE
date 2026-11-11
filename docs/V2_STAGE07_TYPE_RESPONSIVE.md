# EVOLUNE V2 — Stage 07: Typography, Formatting & Responsive Layout

**Status:** Native source implementation in draft `feature/v2-typography-responsive`, stacked on the Stage 06 navigation PR. Exact-head CI, emulator screenshots, owner visual and accessibility approvals required before merge.

## Locked brief / non-goals
This is **the existing Stage 07** from the ten-stage V2 experience roadmap. No additional domain features, accounts, remote models, monetization, storage migrations, reminders behavior, notification permissions, data exports, or challenge mechanics. Preserve Stillwater + Atelier, the previously chosen Open Path silhouette, and truthful/non-shaming content.

## Implemented presentation contracts

| Surface | Design / implementation |
|---|---|
| System-wide | `EvoluneType.typography`: Android-native serif for display/reflection, sans-serif for controls and data; 11/12 through 36sp type ladder with generous line heights. No external font file or license risk. |
| Screen headings | `EvolunePageHeading`, `EvoluneEyebrow`, and `EvoluneBody` unify semantic and visual hierarchy. At narrow 320dp + large fonts, headings use a more compact still-scalable 23sp base; **system fontScale is never suppressed**. |
| Navigation | Stage 06 five-destination semantics retained; compact visible labels and bounded 86–146dp bar heights support 1.0–2.0 font scaling without fixed one-line truncation. Full accessible names stay unchanged. |
| Editorial content | Existing scrollable phone/tablet column retained; horizontal padding contracts to 16dp on narrow or high-scale devices; no text ellipsis on core practice/record content. |
| Art caption | Stillwater hero preserves original artwork, with higher-contrast fade and dynamically taller image/caption area for large text. Original motion/reduced-motion behavior unchanged. |
| First-run | Guided setup uses the shared type hierarchy and native RadioButton selection semantics. Original welcome uses compact brand lettering at 200% instead of clipping the letterspaced wordmark. |
| Formatting | Display dates/times use `EvoluneFormatting` with the current system locale; persisted schedule dates/Room records/time zones are unchanged. This is **locale-aware formatting**, not localization into additional languages. |
| Small labels | Journal dates, stage labels, backup/privacy notices and card metadata are raised to at least 12sp in screen content (navigation has tighter compact exceptions). |

## Verification sources / gates

- New **Stage07TypographyTest** uses a constrained 320dp Compose column with synthetic 100% and 200% system-text configurations; verifies Today heading/action, all destination semantics and navigation callbacks; also checks simulated expanded rail at 140%, plus date/clock helpers. Creates native screenshots under `EVOLUNE-QA`.
- Existing **Stage03–06 instrumented regression tests** remain unchanged and must all pass on the final branch SHA.
- Device emulator workflow now captures **actual system font scale 2.0 at 320dp**, in addition to 1.0 and 1.4 and tablet/landscape widths. For diagnostic clarity it writes synthetic **debug-only** first-run-completed flags into private prefs after APK installation so those screenshots show the Today/navigation screen, not the setup gate. No real user data is accessed; these QA artifacts must never be confused with production onboarding behavior.
- Actual 320dp/200% and tablet captures must be **visually inspected** for clipping, headline wrapping, nav taps, bottom CTA overlap, data dialogs and Journal; screenshot file existence is only a pipeline check.
- Manual device gates remain: text and TalkBack at 100, 140, 200%; keyboard/IME; RTL direction; scripts beyond English; hardware tablets and small phones; adaptation to landscape and split-screen; user visual signoff.

## Exit conditions
1. Exact Stage 07 head: Android CI and Room/Compose emulator QA both green.
2. Visual comparison of real, post-onboarding 320dp/1.0, 1.4, 2.0 and tablet/landscape captures; no important text clipped, hidden or truncated.
3. Confirm active/selectable controls and error messages remain accessible at 200%; no regressions in navigation, challenge create, private journal, recovery, reminders or Data & Privacy.
4. Owner approval of typography proportions and art-caption text. No source merge or production APK until stacked Stage 01–07 visual gates are closed.

**Remaining roadmap:** Stage 08 original artwork/motion; Stage 09 Settings/preferences presentation; Stage 10 master integrated visual/device QA. Real Play Store release criteria remain separate.
