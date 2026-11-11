package studio.evolune.app

import studio.evolune.data.ChallengeSnapshot
import studio.evolune.domain.ChallengeEngine
import studio.evolune.domain.MilestoneKind
import studio.evolune.domain.PracticeOutcome
import java.time.Instant
import java.time.LocalTime
import java.time.temporal.ChronoUnit

/**
 * Pure reminder planning. At most one upcoming scheduled opportunity per challenge.
 * A cueTime is NOT permission to send a notification; caller checks explicit opt-in.
 */
object ReminderPlanning {
    fun next(
        snapshot: ChallengeSnapshot,
        userTime: LocalTime,
        after: Instant
    ): Instant? {
        val p = snapshot.plan
        val today = after.atZone(p.zone).toLocalDate()
        val latest = ChallengeEngine.latestRecords(p, snapshot.records)
        if (p.milestoneKind == MilestoneKind.COMPLETED_SESSIONS &&
            latest.values.count { it.outcome == PracticeOutcome.COMPLETED } >= p.target) return null
        val finish = if (p.milestoneKind == MilestoneKind.CALENDAR_DAYS)
            p.startDate.plusDays(p.target.toLong() - 1L)
            else today.plusDays(365L) // Horizon; next alarm is recalculated from the app and delivery.
        var day = maxOf(today, p.startDate)
        val end = minOf(finish, today.plusDays(365L))
        while (!day.isAfter(end)) {
            val schedule = p.scheduleOn(day)
            if (ChallengeEngine.isScheduled(day, schedule.effectiveFrom, schedule.recurrence) &&
                snapshot.pauses.none { it.includes(day) } &&
                latest[day] == null) {
                // ZonedDateTime resolves DST gaps/overlaps using the challenge's IANA timezone.
                val candidate = day.atTime(userTime).atZone(p.zone).toInstant()
                if (candidate.isAfter(after)) return candidate
            }
            day = day.plusDays(1)
        }
        return null
    }
}
