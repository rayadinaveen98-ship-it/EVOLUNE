# EVOLUNE V2 — Stage 02: Open Path welcome experience

**Status:** Interactive browser design prototype v0.1 created and tested, **not native Android implementation**.  
**Dependency:** Owner selected **Open Path (direction A)**. Exact logo shape, typesetting and palette remain in Stage 01 final identity review.  
**Prototype:** [Open the actual standalone HTML concept](prototypes/open-path-welcome-v0.1.html). It embeds the already-approved Stillwater illustration and uses the same Open Path SVG geometry as the Stage 01 source.

## Core premise
The product opens like a quiet place, not a splash advertisement: natural landscape emerges, three stones settle into a deliberate path, a horizon dot appears, then **EVOLUNE** and *A calmer way to become better* are revealed before transitioning into the already-established Today screen.

This is not a notification, game, video ad, or promise that growth has occurred. The progress line indicates only the intro duration.

## Timing / storyboard (~5.8 seconds for explicit first-run/replay)

| Time | Visual | Interaction / narrative | Technical guidance |
| --- | --- | --- | --- |
| 0.00–0.80 s | Deep forest dark -> approved Stillwater art appears in warm light | Stillness, calm, breathing room | Single optimized pre-rendered image, no video dependency |
| 0.80–1.45 s | Broad stepping stone appears, subtle water ripple | First deliberate step | Original SVG mask, translateY + fade, no continuous expensive blur |
| 1.40–2.15 s | Middle stepping stone arrives | Small steps build a path | Same vector geometry, staggered easing |
| 1.94–2.85 s | Upper stone settles | Moving forward without racing | No literal “streak days” metaphor |
| 2.42–3.55 s | Brass horizon dot rises | An open destination, not a finish line | No separate raster symbol |
| 3.10–4.35 s | Editorial EVOLUNE wordmark and fine rule | Brand arrives quietly | Use vector or final licensed font after Stage 01 lock |
| 3.85–5.30 s | Tagline, subtle atmospheric motion | Purpose rather than achievements | Text contrast and reduced-motion fallback mandatory |
| ~5.8 s | Crossfade into Today | “A gentle day to begin again” and one actionable practice | Do not discard saved user state or force full reload |

### Return / accessibility / cancellation
- **First-ever opening:** Play introduction once after real app readiness; user may skip at any time.
- **Returning user:** Android platform splash -> real app content without forced 5.8-second wait. Settings may provide an explicit **Replay welcome** action.
- **Reduced motion:** No water ripple, parallax, scale or stagger; static mark and immediate/brief transition.
- **System back, Skip or TalkBack Skip action:** Exit promptly without data loss or re-enrollment.
- **Sound:** Off; do not add mandatory music, vibration or noisy autoplay.
- **Low-memory startup:** Show legible static brand then real content; never wait for a remote image or Lottie download.
- **Errors/loading:** If user data cannot be read, render a genuine in-app error state, not a permanently animating intro.
- **Orientation/recreation:** A rotation or activity recreation must not replay the intro inadvertently.

### Android integration boundary
- Implement system splash correctly via Android platform splash API. **The ~5.8 second animation is an optional first-run in-app intro**, not a forced system splash; no artificial startup latency on every launch.
- The Open Path icon source must become Android vector/adaptive icon, splash vector and monochrome variant only after optical and small-mask review.
- Compose should use state transitions and visible destination content, with lifecycle-safe and testable first-run state; avoid timers that can leak or duplicate callbacks on configuration changes.
- Existing V1 Room challenge data, reminder behavior and backup/restore semantics are untouched by this browser prototype.

## Browser proof, Nov 11 2026
The 229 KB self-contained HTML prototype was exercised in local headless Chromium using Playwright with no JavaScript errors:
1. Intro automatically transitions to Today after 5.8 seconds.
2. Replay re-triggers the intro cleanly.
3. Skip immediately displays Today.
4. Reduced-motion demo bypasses decorative animation and reaches Today after a brief static hold.
5. “Preview returning user” goes directly to Today.
6. 400px mobile viewport renders; the handoff Today card was refined so the main action sits above bottom navigation.

**This is browser testing only**, not a native Android splash, device animation, frame-time or accessibility certification. Main Today practice is presentation-only and intentionally not wired to V1 database.

## Stage 02 acceptance checklist
- [x] User-chosen Open Path geometry used consistently in intro and Today branding.
- [x] Full storyboard and timings locked as the **v0.1 review target**.
- [x] Skippable, replayable and reduced-motion browser behaviors implemented and browser-verified.
- [x] Same original Stillwater art appears in intro and Today handoff.
- [x] No external APIs, music, fonts or account dependence.
- [ ] Exact final mark and commercial wordmark approval / font licensing.
- [ ] Final Android system-splash plus separate Compose first-run intro implementation.
- [ ] Real TalkBack, orientation, 200% font, reduced motion and slow-device startup QA.
- [ ] Native/emulator screenshots and timing/performance report; owner visual approval.

**Stage 02 remains in progress.** Do not label this self-contained HTML file an APK or Play Store-ready build.
