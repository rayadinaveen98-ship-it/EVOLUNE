package studio.evolune.domain

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime

/** Standalone, no-dependency checks runnable by kotlinc. CI/JUnit wiring comes after Android setup. */
fun main() {
    val india = ZoneId.of("Asia/Kolkata")
    val nov12 = LocalDate.of(2026,11,12)
    val plan = ChallengePlan("walk", "Morning movement", nov12, india,
        MilestoneKind.CALENDAR_DAYS, 21,
        listOf(ScheduleVersion(nov12, Recurrence.Weekdays, "Walk 15 minutes", LocalTime.of(7,30))))
    val atNov13 = ZonedDateTime.of(2026, 11, 13, 12, 0, 0, 0, india)
    val full = ChallengeEngine.opportunities(plan, atNov13, nov12.plusDays(20))
    check(full.size == 21)
    check(full.count { it.state != OpportunityState.NOT_SCHEDULED } == 15)
    check(full[0].state == OpportunityState.UNREPORTED) { "Yesterday without log isn't necessarily missed" }
    check(full[1].state == OpportunityState.SCHEDULED) { "Today is not auto-missed" }
    check(full[2].state == OpportunityState.NOT_SCHEDULED) { "Saturday is not scheduled" }

    val record = PracticeRecord("walk", nov12, PracticeOutcome.MISSED, 1, Instant.parse("2026-11-12T17:00:00Z"))
    val corrected = PracticeRecord("walk", nov12, PracticeOutcome.COMPLETED, 2, Instant.parse("2026-11-13T04:30:00Z"))
    val withHistory = ChallengeEngine.opportunities(plan, atNov13, nov12.plusDays(20), listOf(record,corrected))
    check(withHistory[0].state == OpportunityState.COMPLETED)
    check(ChallengeEngine.latestRecords(plan, listOf(record, corrected))[nov12] == corrected)
    check(record.outcome == PracticeOutcome.MISSED) { "Previous revision must remain immutable" }

    val historicalDuringPause = ChallengeEngine.opportunities(plan, atNov13, nov12, listOf(corrected), listOf(PauseWindow(nov12,nov12)))
    check(historicalDuringPause.single().state == OpportunityState.COMPLETED) { "A later pause cannot erase a previous practice" }

    val paused = ChallengeEngine.opportunities(plan, atNov13, nov12.plusDays(20), pauses=listOf(PauseWindow(nov12,nov12.plusDays(1))))
    check(paused.take(2).all { it.state == OpportunityState.PAUSED })
    check(ChallengeEngine.summarize(paused).eligibleOpportunities == 13)

    val newPlan = plan.withScheduleRevision(ScheduleVersion(nov12.plusDays(4), Recurrence.Daily, "Walk 5 minutes"))
    val revised = ChallengeEngine.opportunities(newPlan, atNov13, nov12.plusDays(20))
    check(revised.first().action == "Walk 15 minutes")
    check(revised[4].action == "Walk 5 minutes")
    check(revised.count { it.state != OpportunityState.NOT_SCHEDULED } == 19)

    val sessionPlan = ChallengePlan("read", "Read", nov12, india, MilestoneKind.COMPLETED_SESSIONS, 3,
        listOf(ScheduleVersion(nov12, Recurrence.Daily, "Read ten minutes")))
    val statuses = listOf(PracticeOutcome.COMPLETED, PracticeOutcome.PARTIAL, PracticeOutcome.COMPLETED_BACKUP, PracticeOutcome.COMPLETED, PracticeOutcome.COMPLETED)
    val records = statuses.mapIndexed { i,s -> PracticeRecord("read", nov12.plusDays(i.toLong()), s, 1, Instant.parse("2026-11-16T20:00:00Z")) }
    val days = ChallengeEngine.opportunities(sessionPlan, ZonedDateTime.of(2026, 11, 17, 20, 0,0,0,india), nov12.plusDays(4), records)
    check(ChallengeEngine.summarize(days).recordedFull == 3)
    check(ChallengeEngine.summarize(days).recordedBackup == 1)
    check(ChallengeEngine.milestoneReached(sessionPlan, ZonedDateTime.of(2026,11,17,20,0,0,0,india), days))
    check(!ChallengeEngine.milestoneReached(sessionPlan.copy(target=4), ZonedDateTime.now(india), days))

    check(ChallengeEngine.isScheduled(nov12.plusDays(7), nov12, Recurrence.EveryNDays(7)))
    check(!ChallengeEngine.isScheduled(nov12.plusDays(6), nov12, Recurrence.EveryNDays(7)))
    check(ChallengeEngine.isScheduled(LocalDate.of(2026,11,14), nov12, Recurrence.DaysOfWeek(setOf(DayOfWeek.SATURDAY))))

    // 2026-11-12 00:15 IST is still Nov 11 UTC: record should follow chosen timezone.
    val utcNight = ZonedDateTime.of(2026,11,11,18,45,0,0,ZoneId.of("UTC"))
    val localDay = ChallengeEngine.opportunities(plan, utcNight, nov12).single()
    check(localDay.state == OpportunityState.SCHEDULED)

    check(!ChallengeEngine.milestoneReached(plan, ZonedDateTime.of(2026,12,2,23,59,0,0,india), full))
    check(ChallengeEngine.milestoneReached(plan, ZonedDateTime.of(2026,12,3,0,0,0,0,india), full))
    println("EVOLUNE domain checks passed: recurrence, honest logs, pause, history, schedule revision, session milestones, timezones.")
}
