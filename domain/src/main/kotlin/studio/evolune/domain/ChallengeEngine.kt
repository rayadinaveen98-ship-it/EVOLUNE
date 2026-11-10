package studio.evolune.domain

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

/** Pure Kotlin / java.time. No Android, database, internet, or UI dependency. */
enum class MilestoneKind { CALENDAR_DAYS, COMPLETED_SESSIONS }
enum class PracticeOutcome { COMPLETED, PARTIAL, RESTED, MISSED, COMPLETED_BACKUP }
enum class OpportunityState {
    NOT_SCHEDULED, SCHEDULED, UNREPORTED, COMPLETED, PARTIAL, RESTED, MISSED,
    COMPLETED_BACKUP, PAUSED
}

sealed interface Recurrence {
    data object Daily : Recurrence
    data object Weekdays : Recurrence
    data object Weekends : Recurrence
    data class DaysOfWeek(val days: Set<DayOfWeek>) : Recurrence {
        init { require(days.isNotEmpty()) { "Choose at least one day" } }
    }
    data class EveryNDays(val interval: Int) : Recurrence {
        init { require(interval in 1..365) { "Interval must be 1..365" } }
    }
}

data class ScheduleVersion(
    val effectiveFrom: LocalDate,
    val recurrence: Recurrence,
    val action: String,
    val cueTime: LocalTime? = null,
    val fallbackAction: String? = null
) {
    init { require(action.isNotBlank()) { "An action is required" } }
}

data class ChallengePlan(
    val id: String,
    val title: String,
    val startDate: LocalDate,
    val zone: ZoneId,
    val milestoneKind: MilestoneKind,
    val target: Int,
    val schedules: List<ScheduleVersion>
) {
    init {
        require(id.isNotBlank() && title.isNotBlank())
        require(target in 1..3650) { "Target must be 1..3650" }
        require(schedules.isNotEmpty())
        require(schedules.first().effectiveFrom == startDate) { "Initial schedule must start on startDate" }
        require(schedules.map { it.effectiveFrom }.distinct().size == schedules.size) { "Duplicate effective dates" }
        require(schedules.zipWithNext().all { (a,b) -> a.effectiveFrom.isBefore(b.effectiveFrom) }) {
            "Schedule revisions must be ordered by date"
        }
    }

    fun scheduleOn(date: LocalDate): ScheduleVersion =
        schedules.lastOrNull { !it.effectiveFrom.isAfter(date) } ?: schedules.first()

    fun withScheduleRevision(version: ScheduleVersion): ChallengePlan {
        require(version.effectiveFrom.isAfter(schedules.last().effectiveFrom)) {
            "Revisions must be later than existing schedules"
        }
        return copy(schedules = schedules + version)
    }
}

/** Append-only revisions: a later revision for a date supersedes the prior one without deleting history. */
data class PracticeRecord(
    val challengeId: String,
    val date: LocalDate,
    val outcome: PracticeOutcome,
    val revision: Long,
    val recordedAt: Instant,
    val note: String? = null
) {
    init { require(revision > 0) }
}

data class PauseWindow(val from: LocalDate, val through: LocalDate) {
    init { require(!through.isBefore(from)) }
    fun includes(date: LocalDate) = !date.isBefore(from) && !date.isAfter(through)
}

data class Opportunity(
    val date: LocalDate,
    val state: OpportunityState,
    val action: String,
    val cueTime: LocalTime?,
    val revision: Long? = null
)

data class PracticeSummary(
    val recordedFull: Int,
    val recordedBackup: Int,
    val partial: Int,
    val rested: Int,
    val explicitlyMissed: Int,
    val unreported: Int,
    val scheduledUpcoming: Int,
    val paused: Int,
    val notScheduled: Int,
    val eligibleOpportunities: Int
) {
    /** Descriptive logged completions, not a clinical adherence or self-worth score. */
    val fullLoggedFraction: Double?
        get() = if (eligibleOpportunities == 0) null
        else recordedFull.toDouble() / eligibleOpportunities
}

object ChallengeEngine {
    /** Date-only recurrence avoids UTC/day-boundary errors; timezone is applied by [opportunities]. */
    fun isScheduled(date: LocalDate, start: LocalDate, recurrence: Recurrence): Boolean {
        if (date.isBefore(start)) return false
        return when (recurrence) {
            Recurrence.Daily -> true
            Recurrence.Weekdays -> date.dayOfWeek !in setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
            Recurrence.Weekends -> date.dayOfWeek in setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
            is Recurrence.DaysOfWeek -> date.dayOfWeek in recurrence.days
            is Recurrence.EveryNDays -> ChronoUnit.DAYS.between(start, date) % recurrence.interval == 0L
        }
    }

    fun latestRecords(plan: ChallengePlan, history: List<PracticeRecord>): Map<LocalDate, PracticeRecord> {
        val chosen = mutableMapOf<LocalDate, PracticeRecord>()
        for (r in history) {
            if (r.challengeId != plan.id) continue
            val old = chosen[r.date]
            if (old == null || r.revision > old.revision ||
                (r.revision == old.revision && r.recordedAt.isAfter(old.recordedAt))) chosen[r.date] = r
        }
        return chosen
    }

    /** Does not reinterpret an unreported (unlogged) day as a known failure. */
    fun opportunities(
        plan: ChallengePlan,
        at: ZonedDateTime,
        through: LocalDate,
        history: List<PracticeRecord> = emptyList(),
        pauses: List<PauseWindow> = emptyList()
    ): List<Opportunity> {
        require(!through.isBefore(plan.startDate))
        require(ChronoUnit.DAYS.between(plan.startDate, through) <= 3660) { "Window is too large" }
        val today = at.withZoneSameInstant(plan.zone).toLocalDate()
        val last = if (plan.milestoneKind == MilestoneKind.CALENDAR_DAYS)
            minOf(through, plan.startDate.plusDays(plan.target.toLong() - 1)) else through
        val latest = latestRecords(plan, history)
        return generateSequence(plan.startDate) { it.plusDays(1) }.takeWhile { !it.isAfter(last) }.map { date ->
            val version = plan.scheduleOn(date)
            val state = when {
                !isScheduled(date, version.effectiveFrom, version.recurrence) -> OpportunityState.NOT_SCHEDULED
                latest[date] != null -> when (latest.getValue(date).outcome) {
                    PracticeOutcome.COMPLETED -> OpportunityState.COMPLETED
                    PracticeOutcome.PARTIAL -> OpportunityState.PARTIAL
                    PracticeOutcome.RESTED -> OpportunityState.RESTED
                    PracticeOutcome.MISSED -> OpportunityState.MISSED
                    PracticeOutcome.COMPLETED_BACKUP -> OpportunityState.COMPLETED_BACKUP
                }
                pauses.any { it.includes(date) } -> OpportunityState.PAUSED
                date.isBefore(today) -> OpportunityState.UNREPORTED
                else -> OpportunityState.SCHEDULED
            }
            Opportunity(date, state, version.action, version.cueTime, latest[date]?.revision)
        }.toList()
    }

    fun summarize(days: List<Opportunity>): PracticeSummary {
        val c = days.groupingBy { it.state }.eachCount()
        fun n(state: OpportunityState) = c[state] ?: 0
        val eligible = days.count { it.state != OpportunityState.NOT_SCHEDULED && it.state != OpportunityState.PAUSED }
        return PracticeSummary(
            recordedFull = n(OpportunityState.COMPLETED),
            recordedBackup = n(OpportunityState.COMPLETED_BACKUP),
            partial = n(OpportunityState.PARTIAL),
            rested = n(OpportunityState.RESTED),
            explicitlyMissed = n(OpportunityState.MISSED),
            unreported = n(OpportunityState.UNREPORTED),
            scheduledUpcoming = n(OpportunityState.SCHEDULED),
            paused = n(OpportunityState.PAUSED),
            notScheduled = n(OpportunityState.NOT_SCHEDULED),
            eligibleOpportunities = eligible
        )
    }

    fun milestoneReached(plan: ChallengePlan, at: ZonedDateTime, days: List<Opportunity>): Boolean {
        return when (plan.milestoneKind) {
            MilestoneKind.CALENDAR_DAYS ->
                at.withZoneSameInstant(plan.zone).toLocalDate().isAfter(plan.startDate.plusDays(plan.target.toLong() - 1))
            MilestoneKind.COMPLETED_SESSIONS ->
                days.count { it.state == OpportunityState.COMPLETED } >= plan.target
        }
    }
}
