# EVOLUNE V2 — Stage 06: Navigation & Shared Components

**Status:** Native source integrated on `feature/v2-navigation-components`, stacked on Stage 05. Design/implementation draft. Android build, emulator, owner visual, real-device and TalkBack evidence must be checked before approval.

## Scope and delivered source
- **Responsive destination system:** existing Today, My Growth, Challenges, Journal, Insights routes preserved. Compact/medium screens use a five-item bottom bar; expanded widths (≥700dp) use a left-side navigation rail. Both map nested Challenge detail/create/recovery/review/reminders to Challenges, and Data & Privacy to Journal.
- **Semantic navigation:** native `selectable(role=Tab)` with selected and state description, and stable `evolune-nav-*` test tags. Short visible aliases are used only in cramped/large-text phone layouts while accessible names remain the full destination names.
- **Consistent icons:** one original 24-unit rounded monoline SVG-like Canvas family for five navigation destinations plus Back, Close, Add, Check, Reminders, and Privacy. No production emojis or stock icons.
- **Header:** replaces placeholder Unicode mark with the *same underlying Open Path vector drawing* already used in the first-run Compose welcome; retains Stage 01 art direction. The brand's final logo geometry/wordmark remain unapproved and are not silently finalized.
- **Shared primary/secondary controls:** backed by Material button semantics, 50–52dp minimum touch surfaces, accessible contrast-aware enabled/disabled states; existing callsites retained via simple helpers.
- **Shared notices:** live-region polite announcements, readable copy and a working dismiss action with 48dp target; no invented success state.
- **Navigation state:** continues using existing `MainActivity` page selection and callbacks. No new screens, data model, event tracking, network service or permission changes.

## Source files
- `app/src/main/java/studio/evolune/app/EvoluneChrome.kt`: route mapping, navigation/rail, icon family, shared controls and app chrome.
- `app/src/main/java/studio/evolune/app/Experience.kt`: existing app shell and button functions delegate to the new implementation.
- `app/src/main/java/studio/evolune/app/OpenPathWelcome.kt`: reuse exact original selected mark geometry in a light background.
- `app/src/androidTest/java/studio/evolune/app/Stage06ChromeTest.kt`: navigation selection, actions and enabled/disabled semantics.
- Existing Stage 03/04/05 tests and all V1 challenge/Room/reminder logic remain in suite.

## Stage 06 approval gates
1. Exact-branch Android CI and instrumented tests, including full regression suite.
2. Inspect actual native screenshots on 320/360dp phones, 700/853/1280dp expanded contexts; review keyboard, 140–200% fonts, landscape and split-screen.
3. TalkBack traversal: each destination announced once, selection read clearly, order reachable, focus visible; minimum 48dp effective targets and dark-mode contrast reviewed separately.
4. Shared state catalogue covers loading, enabled/disabled/focus/error, text fields, chips, dialogs and confirmations without introducing dead buttons. Existing Material controls are retained; broad Android accessibility validation is still Stage 10.
5. Owner signoff on icon proportions, navigation scale, header mark and transition parity. Do not merge into main until Stage 01–06 stacked draft gates pass.

## Cross-cutting constraints
Stillwater + Atelier; progress without shame; factual records and private local data; no new features or functionality regression; no claims of final Play Store release readiness.
