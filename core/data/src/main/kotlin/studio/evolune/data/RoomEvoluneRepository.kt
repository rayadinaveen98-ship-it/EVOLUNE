package studio.evolune.data

import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import studio.evolune.domain.ChallengeEngine
import studio.evolune.domain.ChallengePlan
import studio.evolune.domain.PauseWindow
import studio.evolune.domain.PracticeOutcome
import studio.evolune.domain.PracticeRecord
import studio.evolune.domain.ScheduleVersion

/** Domain-facing immutable snapshot. UI uses this + ChallengeEngine, not raw SQL rows. */
data class ChallengeSnapshot(
    val plan: ChallengePlan,
    val records: List<PracticeRecord>,
    val pauses: List<PauseWindow>,
    val growthAreaId: String?
)

/**
 * Local-only Room adapter. This source needs Room + Android to compile; standalone
 * Kotlin codec and SQLite contract are checked independently in this phase.
 */
class RoomEvoluneRepository(
    private val dao: EvoluneDao,
    private val clock: Clock = Clock.systemUTC()
) {
    suspend fun createChallenge(plan: ChallengePlan, growthAreaId: String? = null) {
        require(plan.schedules.size == 1) { "Create with one initial schedule; append revisions separately" }
        val now = Instant.now(clock).toEpochMilli()
        val initial = StorageCodec.rawSchedule(plan.schedules.single())
        val planRaw = StorageCodec.rawPlan(plan)
        dao.createChallenge(
            ChallengeEntity(
                id = planRaw.id, growthAreaId = growthAreaId, title = planRaw.title,
                startDate = planRaw.startDate, zoneId = planRaw.zoneId,
                milestoneKind = planRaw.milestoneKind, target = planRaw.target,
                createdAtEpochMs = now
            ),
            ScheduleVersionEntity(
                challengeId = plan.id, effectiveFrom = initial.effectiveFrom,
                recurrenceKind = initial.recurrenceKind,
                recurrenceArgument = initial.recurrenceArgument,
                action = initial.action, cueTime = initial.cueTime,
                fallbackAction = initial.fallbackAction, createdAtEpochMs = now
            )
        )
    }

    suspend fun snapshot(id: String): ChallengeSnapshot? {
        val rows = dao.loadChallengeRows(id) ?: return null
        val p = rows.challenge
        val plan = StorageCodec.plan(
            StorageCodec.PersistedPlan(p.id, p.title, p.startDate, p.zoneId, p.milestoneKind, p.target),
            rows.schedules.map { s -> StorageCodec.PersistedSchedule(
                s.effectiveFrom, s.recurrenceKind, s.recurrenceArgument,
                s.action, s.cueTime, s.fallbackAction
            ) }
        )
        return ChallengeSnapshot(
            plan = plan,
            records = rows.history.map { r -> StorageCodec.record(StorageCodec.PersistedLog(
                r.challengeId, r.localDate, r.outcome, r.revision, r.recordedAtEpochMs, r.note
            )) },
            pauses = rows.pauses.map { StorageCodec.pause(it.fromDate, it.throughDate) },
            growthAreaId = p.growthAreaId
        )
    }

    suspend fun recordPractice(challengeId: String, day: LocalDate, outcome: PracticeOutcome, note: String? = null) {
        val snapshot = requireNotNull(snapshot(challengeId)) { "Unknown challenge" }
        val plan = snapshot.plan
        require(!day.isBefore(plan.startDate)) { "Cannot log before challenge start" }
        val today = Instant.now(clock).atZone(plan.zone).toLocalDate()
        require(!day.isAfter(today)) { "Cannot log future activities" }
        val version = plan.scheduleOn(day)
        require(ChallengeEngine.isScheduled(day, version.effectiveFrom, version.recurrence)) {
            "Cannot log a day that was not scheduled"
        }
        dao.appendPractice(challengeId, day.toString(), outcome.name, Instant.now(clock).toEpochMilli(), note)
    }

    suspend fun reviseSchedule(challengeId: String, version: ScheduleVersion) {
        val snapshot = requireNotNull(snapshot(challengeId)) { "Unknown challenge" }
        val plan = snapshot.plan
        val today = Instant.now(clock).atZone(plan.zone).toLocalDate()
        require(version.effectiveFrom.isAfter(today)) { "Changes apply from tomorrow or later" }
        plan.withScheduleRevision(version) // validates ordering before database write
        val raw = StorageCodec.rawSchedule(version)
        dao.appendSchedule(ScheduleVersionEntity(
            challengeId = challengeId, effectiveFrom = raw.effectiveFrom,
            recurrenceKind = raw.recurrenceKind, recurrenceArgument = raw.recurrenceArgument,
            action = raw.action, cueTime = raw.cueTime,
            fallbackAction = raw.fallbackAction, createdAtEpochMs = Instant.now(clock).toEpochMilli()
        ))
    }

    suspend fun addPause(challengeId: String, pause: PauseWindow) {
        requireNotNull(snapshot(challengeId)) { "Unknown challenge" }
        dao.insertPause(PauseWindowEntity(
            challengeId = challengeId, fromDate = pause.from.toString(), throughDate = pause.through.toString()
        ))
    }

    suspend fun createReflection(entry: JournalEntryEntity) {
        validateReflection(entry)
        dao.createJournal(entry)
    }

    suspend fun editReflection(entry: JournalEntryEntity) {
        validateReflection(entry)
        val existing = requireNotNull(dao.findJournal(entry.id)) { "Unknown journal entry" }
        require(existing.createdAtEpochMs == entry.createdAtEpochMs) { "Creation date cannot be edited" }
        require(entry.updatedAtEpochMs >= existing.updatedAtEpochMs) { "New version must not be older" }
        check(dao.updateJournal(entry) == 1) { "Journal edit was not saved" }
    }

    suspend fun deleteReflection(id: String) { dao.deleteJournal(id) }

    private fun validateReflection(entry: JournalEntryEntity) {
        require(entry.id.isNotBlank())
        require(entry.energy == null || entry.energy in 1..5)
        require(entry.body.length <= 100_000) { "Journal entry too long" }
        require(entry.updatedAtEpochMs >= entry.createdAtEpochMs)
    }
}
