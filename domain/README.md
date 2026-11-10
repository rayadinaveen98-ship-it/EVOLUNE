# EVOLUNE native Kotlin foundation

This folder is the **pure Kotlin domain layer**, not a standalone Android application.

## Files

- [ChallengeEngine.kt](src/main/kotlin/studio/evolune/domain/ChallengeEngine.kt) — challenge schedule, status, history, pauses, milestone calculations.
- [ChallengeEngineChecks.kt](src/test/kotlin/studio/evolune/domain/ChallengeEngineChecks.kt) — runnable, dependency-free JVM checks.

## Local test command

With JDK 17+ and the Kotlin command line compiler installed, from the `domain` directory:

```bash
kotlinc src/main/kotlin/studio/evolune/domain/ChallengeEngine.kt src/test/kotlin/studio/evolune/domain/ChallengeEngineChecks.kt -include-runtime -d domain-checks.jar
java -jar domain-checks.jar
```

These checks passed in the task environment on 2026-11-11 with Kotlin JVM 1.9.0 and OpenJDK 21. This does **not** mean the Android app has been compiled. JUnit/Gradle CI, Room storage and Compose integrations are next.

## Behavioral invariant

Unreported is unknown, not failure. Partial and backup completion are distinct from full completion. Historical records and schedule versions are preserved across edits. Timezone boundaries use the challenge-selected IANA timezone.

See [Android architecture](../../docs/ANDROID_ARCHITECTURE_V01.md) and [full product behavior specification](../../docs/CHALLENGE_ENGINE_SPEC.md).
