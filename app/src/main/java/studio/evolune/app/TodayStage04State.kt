package studio.evolune.app

import java.time.LocalDate
import java.time.LocalTime
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import studio.evolune.data.ChallengeSnapshot
import studio.evolune.domain.ChallengeEngine
import studio.evolune.domain.MilestoneKind
import studio.evolune.domain.OpportunityState

/**
 * Presentation-only classification of an existing local challenge.
 * Does not write practice records, mutate a schedule, enable notifications, or infer failure.
 */
internal enum class TodayMode {
    EMPTY, STARTS_LATER, SCHEDULED, RECORDED, REST_DAY, PAUSED, MILESTONE_ENDED
}

internal data class TodayViewState(
    val date: LocalDate,
    val mode: TodayMode,
    val challengeTitle: String? = null,
    val action: String? = null,
    val outcome: OpportunityState? = null,
    val cueTime: LocalTime? = null,
    val milestone: String? = null,
    val unreportedEarlier: Int = 0,
    val nextScheduled: LocalDate? = null,
    val startDate: LocalDate? = null
)

/** The caller may supply a fixed clock instant for tests; challenge dates use its saved timezone. */
internal fun presentToday(
    snapshot: ChallengeSnapshot?,
    at: ZonedDateTime = ZonedDateTime.now()
): TodayViewState {
    if (snapshot == null) return TodayViewState(at.toLocalDate(), TodayMode.EMPTY)
    val plan = snapshot.plan
    val now = at.withZoneSameInstant(plan.zone)
    val today = now.toLocalDate()
    val finalCalendarDay = if (plan.milestoneKind == MilestoneKind.CALENDAR_DAYS)
        plan.startDate.plusDays(plan.target.toLong() - 1L) else null
    val lastEvaluated = minOf(today, plan.startDate.plusDays(3650))
    val opportunities = if (today.isBefore(plan.startDate)) emptyList()
        else ChallengeEngine.opportunities(
            plan, now, lastEvaluated, snapshot.records, snapshot.pauses
        )
    val current = opportunities.lastOrNull { it.date == today }
    val priorUnreported = opportunities.count {
        it.date.isBefore(today) && it.state == OpportunityState.UNREPORTED
    }
    val mode = when {
        today.isBefore(plan.startDate) -> TodayMode.STARTS_LATER
        finalCalendarDay != null && today.isAfter(finalCalendarDay) -> TodayMode.MILESTONE_ENDED
        current?.state == OpportunityState.PAUSED -> TodayMode.PAUSED
        current?.state == OpportunityState.NOT_SCHEDULED -> TodayMode.REST_DAY
        current?.state in setOf(
            OpportunityState.COMPLETED, OpportunityState.PARTIAL,
            OpportunityState.RESTED, OpportunityState.MISSED,
            OpportunityState.COMPLETED_BACKUP
        ) -> TodayMode.RECORDED
        current?.state == OpportunityState.SCHEDULED ||
            current?.state == OpportunityState.UNREPORTED -> TodayMode.SCHEDULED
        else -> TodayMode.REST_DAY
    }

    // A date is upcoming only if it really is scheduled, not paused, and in the
    // calendar milestone's window. The scan covers uncommon EveryNDays(365) plans.
    val searchFrom = maxOf(today.plusDays(1), plan.startDate)
    val next = (0L..365L).asSequence()
        .map { searchFrom.plusDays(it) }
        .takeWhile { finalCalendarDay == null || !it.isAfter(finalCalendarDay) }
        .firstOrNull { day ->
            val version = plan.scheduleOn(day)
            ChallengeEngine.isScheduled(day, version.effectiveFrom, version.recurrence) &&
                snapshot.pauses.none { it.includes(day) }
        }

    val milestone = if (plan.milestoneKind == MilestoneKind.CALENDAR_DAYS) {
        val elapsed = if (today.isBefore(plan.startDate)) 0L
            else ChronoUnit.DAYS.between(plan.startDate, today) + 1L
        if (finalCalendarDay != null && today.isAfter(finalCalendarDay))
            "${plan.target} calendar-day milestone ended"
        else "Day ${elapsed.coerceAtMost(plan.target.toLong())} of ${plan.target} calendar days"
    } else {
        val full = opportunities.count { it.state == OpportunityState.COMPLETED }
        "$full of ${plan.target} full sessions recorded"
    }

    return TodayViewState(
        date = today,
        mode = mode,
        challengeTitle = plan.title,
        action = current?.action,
        outcome = current?.state,
        cueTime = current?.cueTime,
        milestone = milestone,
        unreportedEarlier = priorUnreported,
        nextScheduled = next,
        startDate = plan.startDate
    )
}
