# EVOLUNE — Phase 3B: Durable Local Data Foundation

**Date:** 2026-11-11. **Status:** Android/Room source prepared, SQLite and JVM contracts tested; Android Room compiler, emulator, and APK **not** run.

## Completed in this phase
- A Room 2.x / SQLite data model for user-owned growth areas, challenges, append-only schedule versions, append-only practice corrections, pauses, and private journal entries.
- Transactional challenge creation and practice revision insertion. No SQL REPLACE on user history.
- Atomic challenge snapshot reads and a repository bridge to the existing tested `ChallengeEngine`.
- A JVM-only codec for IANA timezones, local dates, weekday recurrence, counters, and states; unknown kinds fail explicitly.
- SQLite on-disk restart checks and revision/cascade/journal preservation checks.
- All functionality remains local; no network/database services, telemetry, API keys, or account dependency.

## Files
- `core/data/src/main/kotlin/studio/evolune/data/EvoluneEntities.kt`: Room schema definition (version 1)
- `.../EvoluneDao.kt`: transactional inserts, reads, updates and queries
- `.../EvoluneDatabase.kt`: versioned RoomDatabase
- `.../RoomEvoluneRepository.kt`: domain-facing persistence access and validation
- `.../StorageCodec.kt`: platform-independent persisted data codec
- `core/data/src/test/kotlin/studio/evolune/data/StorageCodecChecks.kt`: independent JVM codec contract
- `core/data/src/test/sqlite_contract_checks.py`: real SQLite file restart test (not Room runtime)
- `core/data/sql/sqlite_contract_v1.sql`: independently tested schema contract, **not** a Room migration or generated schema

## Storage guarantees designed
- A **missing check-in** has no database row; it is **unreported**, never inferred as missed.
- Reported `MISSED` must be an explicit practice outcome. Corrections append with increasing revisions. Latest revision wins in domain engine; earlier records remain.
- Every schedule revision has a distinct effective local date. No update/upsert pathway overwrites past schedule versions.
- Challenge is created in one transaction with the first schedule. A partial failure rolls back.
- Pauses and records are distinct; a later pause does not erase a previous record.
- Journal is personal and optional; deleting a challenge or growth area unlinks journal entries rather than silently deleting them. Deleting a specific journal entry is explicit.
- Challenge record dates are ISO local dates, with a pinned IANA zone for calculations.

## Verification executed locally
```sh
# Run inside the extracted package root
kotlinc domain/src/main/kotlin/studio/evolune/domain/ChallengeEngine.kt \
  domain/src/test/kotlin/studio/evolune/domain/ChallengeEngineChecks.kt \
  -include-runtime -d /tmp/evolune-engine.jar
java -jar /tmp/evolune-engine.jar

kotlinc domain/src/main/kotlin/studio/evolune/domain/ChallengeEngine.kt \
  core/data/src/main/kotlin/studio/evolune/data/StorageCodec.kt \
  core/data/src/test/kotlin/studio/evolune/data/StorageCodecChecks.kt \
  -include-runtime -d /tmp/evolune-codec.jar
java -jar /tmp/evolune-codec.jar

python3 core/data/src/test/sqlite_contract_checks.py
```

## Next mandatory integration gates
1. Create/configure the actual Android Gradle project and Room compiler (KSP); pin **compatible** Kotlin/AGP/KSP versions and Room library versions.
2. Compile Room DAOs and run Android instrumentation/in-memory Room tests. JVM + SQLite contract checks are not a substitute.
3. Enable and verify exported Room JSON schema; compare with SQL contract. Test schema migrations before any production database upgrade; avoid destructive migration fallback.
4. Implement application-private database initialization and an explicitly defined backup/export/delete policy, with sensitive-data protection assessed before storing user entries on device.
5. Connect Today + Challenge creation/logging UI and run restart, process-death, edit, and offline tests on a real emulator/device.
6. Only after actual Android builds/runs claim APK or UI persistence works.

## Room version note
Use the `androidx.room` 2.x code path for this source (`androidx.room` annotations). Room 2.8.5 was the latest stable 2.x version as of this phase; Room 3.0.3 is a **separate** `androidx.room3` library, requiring API/import migration. Version compatibility and successful app build must be checked when wiring Gradle.

Official references: https://developer.android.com/jetpack/androidx/releases/room and https://developer.android.com/jetpack/androidx/releases/room3
