# EVOLUNE v1 — Safety, privacy and release risks

**Reviewed:** 2026-11-11. Initial feature-level risk register; mitigation states must be verified, not presumed.

| Risk | Severity | Required control / evidence | Status |
| --- | --- | --- | --- |
| Losing private journal or challenge history after uninstall, device loss, database change | High | Explicit user export and **verified restore**, migration and reinstall tests; no misleading “backed up” UI | Export in progress; restore pending |
| Private journal appearing in logs, public GitHub artifacts, screenshots, network or untrusted files | High | App-private Room, no analytics/Internet permission, synthetic screenshots, user export warning, privacy audit | Source restrictions exist; audit pending |
| Deleting a challenge unexpectedly deleting journal text | High | Room FK SET_NULL, relational test, clear deletion confirmations and ownership UI | ORM rules and prior tests exist; independent UI QA pending |
| Auto-calling an unreported day a “failure”, or promising habit formation after 21 days | High | Domain tests; copy review; display exact state semantics | Implemented/automated checks exist |
| Reminders misleading users, firing after pause, or failing quietly around reboot/DST | Medium-high | Opt-in permission, time-zone aware scheduling, notification cancellation/update/reboot tests | Not implemented |
| Mood/energy scales being mistaken for medical diagnosis | Medium-high | Optional subjective fields, clear non-clinical wording and no high-stakes inference | In source; final copy audit pending |
| User feels shame, pressure, manipulative retention tactics | Medium | No punitive streak mechanics, dismissible reflection, opt-in notifications, representative pilot feedback | Behavioral design implemented; pilot pending |
| Inaccessible typography, contrast, keyboard, TalkBack, animation | Medium-high | Accessibility matrix across devices, font scaling, screen-reader and reduced-motion QA | Partial layout screenshots only |
| Malformed imported data, unbounded size, hostile or stale backup | High | Versioned backup schema, size/path/foreign-key checks, atomic restore with validation, migration tests | Not implemented |
| Bad release update or loss of local database after upgrade | High | Frozen release schema, tested migrations, staged rollout and rollback strategy | Not yet demonstrated |

## Release gates (not completed by approval of concept)
1. Privacy/data retention/deletion/export + restore verified.
2. App review with no clinical or scientific effectiveness overclaims.
3. Reminder and notification permission behavior tested for supported Android versions.
4. Screenreader/large-font/reduced-motion and practical UI QA.
5. Multi-user pilot with real voluntary feedback; issues tracked and resolved.
6. Signed production artifact, store disclosures and product-owner publication approval.

No live user content is needed for source-level tests or screenshot artifacts.
