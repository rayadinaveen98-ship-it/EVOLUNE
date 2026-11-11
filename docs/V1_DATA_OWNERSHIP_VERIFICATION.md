# EVOLUNE v1 — User-controlled data ownership, build and QA

**Verified:** 2026-11-11. **Status:** source + emulator-tested; additional manual SAF, reinstall, upgrade and backup UX QA remain before release.
**Review branch:** `feature/android-phase3d-qa`, [draft PR #2](https://github.com/rayadinaveen98-ship-it/EVOLUNE/pull/2).

## Implemented
- Journal → **Data & privacy**, without adding clutter to the five main navigation tabs.
- Android **Storage Access Framework** export: user chooses the JSON file destination; no network upload or hidden automatic backup.
- Export v1 includes growth areas, challenges, ALL schedule versions, ALL practice revisions (not only latest), pause windows, and private journal entries with optional mood/energy and references.
- A single Room `@Transaction` collects consistent export data.
- Restore from a locally chosen version-1 EVOLUNE JSON file with **10 MB input limit**, strict field/relationship/date/recurrence validation, conflict/duplicate checks, and a preview. **No database write occurs during file selection/validation**.
- Restoration requires user to type **REPLACE** before applying. Room replacement is atomic, and failed inserts roll back rather than leaving a half-restored database.
- Local erasure requires typing **DELETE**, clears all app-owned Room tables atomically, and does not silently delete external export files.
- UI warns that exported JSON is sensitive and **not encrypted** by EVOLUNE. Restore replaces existing device data; user should export the current state first.

## Evidence
- [Android CI 42992160733](https://github.com/rayadinaveen98-ship-it/EVOLUNE/actions/runs/42992160733) — **SUCCESS** for the implementation source.
- [Android emulator QA 42992160422](https://github.com/rayadinave98-ship-it/EVOLUNE/actions/runs/42992160422) — **SUCCESS**, 15 total instrumented tests: 6 Room/data + 9 app/Compose, 0 errors and 0 failures.
- Verified XML test results in `evolune-emulator-evidence` artifact `13841429976`.
- Data tests include Unicode journal export, full correction history and schedule/paused records, export/decode/replacement, deliberate erase, unsupported schema/invalid references, and transaction rollback preserving prior data.
- Compose smoke test confirms Data & privacy is reachable and deletion has a typed confirmation gate.

## Not yet verified / remaining
- End-to-end **real file picker** on an actual phone and a second-device restore.
- Uninstall/reinstall + restore; Android upgrade and database migration; low-storage interruption and cloud-provider document edge cases.
- Large exports beyond 10 MB require design review; do not promise unlimited archive import.
- Explicit in-app “save backup reminder”, automatic backup, encryption and cloud sync are **not** implemented.
- A stable public release must not treat this source/emulator evidence as full data-loss protection without manual QA.

## User-data handling
No real user journal contents were read, uploaded, committed or used in tests. CI fixtures are synthetic.
