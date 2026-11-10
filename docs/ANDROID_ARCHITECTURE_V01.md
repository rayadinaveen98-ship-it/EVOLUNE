# EVOLUNE — Android architecture v0.1

Status: Phase 3A foundation in progress; updated 2026-11-11. This is architecture and tested Kotlin logic, NOT an Android APK.

## Approved product baseline
Stillwater + Atelier visual direction. Preserve v0.3: Today, Challenges, My Growth, optional Journal, Insights, Recovery, and Milestone Review. Keep one repeatable action per challenge, honest missing-data labels, no shame-based streaks, no clinical claims.

## Chosen architecture
- Native Android: Kotlin + Jetpack Compose, adaptive for phone and tablet; not an HTML WebView wrapper.
- MVVM and unidirectional state; immutable UI state, ViewModel, coroutines and StateFlow.
- Local-first repository as source of truth, Room/SQLite for challenge records and revisions, DataStore for small preferences.
- Offline use with no required account, paid API, analytics SDK, or cloud connection in the initial release.
- Private journal data, export/delete, explicit backup and encryption decisions before production.
- Reminder permissions only after explicit opt-in. Prefer inexact reminders unless strict timing is essential; Android and Play limit exact-alarm access.
- Original Stillwater + Atelier art only after Android design review; reduced motion and accessibility are essential.

## Domain engine — initial tested implementation
A pure Kotlin/java.time engine has been written and its standalone JVM checks passed locally. Test cases: weekday schedules, 21 calendar-days vs session milestones, explicit missing/partial/rested status, corrections with record history, pause/resume, future-effective schedule revisions and timezone boundaries. Kotlin source and runnable checks are provided in EVOLUNE_Android_Foundation_v0.1.zip in this conversation.

Important: The engine has not yet been connected to Room, Compose, notifications or actual device scheduling.

## Proposed module boundaries
app = Android activity, navigation, lifecycle, dependency wiring.
core:domain = Kotlin challenge rules and history calculations.
core:data = Room entities/DAO, repositories, migrations, data export/delete.
core:designsystem = native components and original assets.
feature:today, feature:challenges, feature:growth, feature:journal, feature:insights.
core:notifications = opt-in reminders after reliability and policy review.

## Ordered work
3A — Foundation: architecture and standalone engine (test pass), repository integration.
3B — Persistent records: database, migrations and process-restart testing.
3C — Compose Today and Challenge Creation/Detail/Recovery/Review.
3D — Growth, Journal, Insights, settings, export/delete.
3E — Responsive polish, artwork integration, optional reminders, accessibility and full-device QA before APK.

## Verification gaps
No Android SDK or Gradle was available here. The Kotlin logic passed local JVM checks, but an APK, emulator run, or native screenshots are NOT verified. Pin and test compatible dependencies during Android build setup.

## Official references
- https://android-developers.googleblog.com/2026/05/android-ui-development-is-compose-first.html
- https://developer.android.com/topic/architecture/data-layer/offline-first
- https://developer.android.com/jetpack/androidx/releases/room
- https://developer.android.com/develop/background-work/services/alarms
- https://support.google.com/googleplay/android-developer/answer/16909972
