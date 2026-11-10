package studio.evolune.data

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import studio.evolune.domain.ChallengePlan
import studio.evolune.domain.MilestoneKind
import studio.evolune.domain.PauseWindow
import studio.evolune.domain.PracticeOutcome
import studio.evolune.domain.PracticeRecord
import studio.evolune.domain.Recurrence
import studio.evolune.domain.ScheduleVersion

/**
 * Pure JVM codec for the SQLite text/number columns. It deliberately does not
 * depend on Room or Android, so the contract can be tested without an emulator.
 * Enum parsing fails closed on unknown values instead of silently changing history.
 */
object StorageCodec {
    fun kind(recurrence: Recurrence): String = when (recurrence) {
        Recurrence.Daily -> "DAILY"
        Recurrence.Weekdays -> "WEEKDAYS"
        Recurrence.Weekends -> "WEEKENDS"
        is Recurrence.DaysOfWeek -> "DAYS_OF_WEEK"
        is Recurrence.EveryNDays -> "EVERY_N_DAYS"
    }

    fun argument(recurrence: Recurrence): String? = when (recurrence) {
        is Recurrence.DaysOfWeek -> recurrence.days.map { it.value }.sorted().joinToString(",")
        is Recurrence.EveryNDays -> recurrence.interval.toString()
        else -> null
    }

    fun recurrence(kind: String, argument: String?): Recurrence = when (kind) {
        "DAILY" -> { require(argument == null); Recurrence.Daily }
        "WEEKDAYS" -> { require(argument == null); Recurrence.Weekdays }
        "WEEKENDS" -> { require(argument == null); Recurrence.Weekends }
        "DAYS_OF_WEEK" -> {
            val values = requireNotNull(argument).split(',')
            require(values.isNotEmpty() && values.all { it.matches(Regex("[1-7]")) })
            val days = values.map { DayOfWeek.of(it.toInt()) }.toSet()
            require(days.size == values.size) { "Duplicate weekday" }
            Recurrence.DaysOfWeek(days)
        }
        "EVERY_N_DAYS" -> Recurrence.EveryNDays(requireNotNull(argument).toInt())
        else -> error("Unsupported recurrence kind: $kind")
    }

    fun milestone(text: String): MilestoneKind = MilestoneKind.valueOf(text)
    fun outcome(text: String): PracticeOutcome = PracticeOutcome.valueOf(text)

    data class PersistedPlan(
        val id: String,
        val title: String,
        val startDate: String,
        val zoneId: String,
        val milestoneKind: String,
        val target: Int
    )

    data class PersistedSchedule(
        val effectiveFrom: String,
        val recurrenceKind: String,
        val recurrenceArgument: String?,
        val action: String,
        val cueTime: String?,
        val fallbackAction: String?
    )

    data class PersistedLog(
        val challengeId: String,
        val localDate: String,
        val outcome: String,
        val revision: Long,
        val recordedAtEpochMs: Long,
        val note: String?
    )

    fun plan(raw: PersistedPlan, scheduleRows: List<PersistedSchedule>): ChallengePlan = ChallengePlan(
        id = raw.id,
        title = raw.title,
        startDate = LocalDate.parse(raw.startDate),
        zone = ZoneId.of(raw.zoneId),
        milestoneKind = milestone(raw.milestoneKind),
        target = raw.target,
        schedules = scheduleRows.sortedBy { it.effectiveFrom }.map { row ->
            ScheduleVersion(
                effectiveFrom = LocalDate.parse(row.effectiveFrom),
                recurrence = recurrence(row.recurrenceKind, row.recurrenceArgument),
                action = row.action,
                cueTime = row.cueTime?.let(LocalTime::parse),
                fallbackAction = row.fallbackAction
            )
        }
    )

    fun rawPlan(plan: ChallengePlan) = PersistedPlan(
        plan.id, plan.title, plan.startDate.toString(), plan.zone.id,
        plan.milestoneKind.name, plan.target
    )

    fun rawSchedule(version: ScheduleVersion) = PersistedSchedule(
        version.effectiveFrom.toString(), kind(version.recurrence), argument(version.recurrence),
        version.action, version.cueTime?.toString(), version.fallbackAction
    )

    fun record(raw: PersistedLog) = PracticeRecord(
        raw.challengeId, LocalDate.parse(raw.localDate), outcome(raw.outcome),
        raw.revision, Instant.ofEpochMilli(raw.recordedAtEpochMs), raw.note
    )

    fun rawRecord(record: PracticeRecord) = PersistedLog(
        record.challengeId, record.date.toString(), record.outcome.name,
        record.revision, record.recordedAt.toEpochMilli(), record.note
    )

    fun pause(from: String, through: String) = PauseWindow(LocalDate.parse(from), LocalDate.parse(through))
}
