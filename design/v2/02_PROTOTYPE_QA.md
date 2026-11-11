# EVOLUNE V2 — Open Path Welcome browser QA

**Date:** 2026-11-11  
**Test type:** Playwright controlling headless Chromium, local self-contained HTML.  
**Test output:** Existing prototype and screenshots packaged as conversation artifacts; GitHub stores the standalone HTML source.

## Tested behaviors
| Test | Expected | Observed |
|---|---|---|
| Timed intro | Automatically displays Today at ~5.8s | Passed |
| Replay | New intro starts from first frame | Passed |
| Skip | Immediately displays Today | Passed |
| Reduced motion | No stagger/ripples, short static presentation and Today | Passed |
| Returning user | No forced 5.8s replay | Passed |
| Mobile | 400px viewport renders and scrolls | Passed |
| Script errors | None | No JavaScript page errors observed |

## Visual inspection
- Captured phone-sized intro scene during stepping-stone reveal and during wordmark/tagline appearance, plus final Today handoff.
- First handoff preview placed the main practice action under the bottom navigation; **fixed** by reducing hero/card vertical space and retested. Main action now visible above bottom navigation at the tested 814px phone frame.
- The original landscape is a real approved product-art asset converted to an optimized ~79 KB WebP; no third-party hotlink.
- Flat SVG logo remains recognizable in both white-on-image and green-on-paper versions.
- Mobile screenshot confirms the preview layout is responsive; no signoff on system UI bars, screen reader or Android keyboard implied.

## Important limitations
- Actual Compose implementation, platform splash, Android launcher mask, low-end GPU frame time and native TalkBack checks **not executed** by these browser tests.
- Wordmark uses a generic serif fallback, not a locked font license or outlined master wordmark.
- “View your path” / “Record practice” controls in sample Today provide a message describing visual-only scope; no mock data is written to the app.
- This prototype contains only original/previously approved art and synthetic sample practice content.

## Next real QA gate
Install the final approved vector as an Android adaptive icon on the relevant *design-integration branch*, implement Compose first-run skip/replay lifecycle semantics, capture real emulator startup playback and validate 320dp compact / tablet states. **Do not merge this HTML design proposal into V1 app code as if it were a production feature.**
