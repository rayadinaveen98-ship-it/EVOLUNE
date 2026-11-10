package studio.evolune.data

import java.time.DayOfWeek
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import studio.evolune.domain.*

/** Can be executed with kotlinc, without Room or Android SDK. */
fun main() {
    val date = LocalDate.of(2026, 11, 12)
    val recurrences = listOf(
        Recurrence.Daily, Recurrence.Weekdays, Recurrence.Weekends,
        Recurrence.DaysOfWeek(setOf(DayOfWeek.FRIDAY, DayOfWeek.MONDAY)),
        Recurrence.EveryNDays(7)
    )
    recurrences.forEach { r ->
        check(StorageCodec.recurrence(StorageCodec.kind(r), StorageCodec.argument(r)) == r)
    }
    fun invalid(block: () -> Unit) {
        check(runCatching(block).isFailure) { "Malformed persisted data was accepted" }
    }
    invalid { StorageCodec.recurrence("DAILY", "unexpected") }
    invalid { StorageCodec.recurrence("DAYS_OF_WEEK", "1,1") }
    invalid { StorageCodec.recurrence("DAYS_OF_WEEK", "8") }
    invalid { StorageCodec.recurrence("EVERY_N_DAYS", "0") }
    invalid { StorageCodec.recurrence("UNKNOWN", null) }
    invalid { StorageCodec.outcome("INFERRED_FAILURE") }
    invalid { StorageCodec.pause("2026-11-12", "2026-11-11") }

    val original = ChallengePlan(
        "read", "Read for 10 minutes", date, ZoneId.of("Asia/Kolkata"),
        MilestoneKind.COMPLETED_SESSIONS, 21,
        listOf(ScheduleVersion(date, Recurrence.Weekdays, "Read 10 minutes", LocalTime.of(21, 30), "Read one page"),
            ScheduleVersion(date.plusDays(7), Recurrence.DaysOfWeek(setOf(DayOfWeek.MONDAY, DayOfWeek.THURSDAY)), "Read 15 minutes"))
    )
    val roundtrip = StorageCodec.plan(
        StorageCodec.rawPlan(original), original.schedules.map(StorageCodec::rawSchedule)
    )
    check(original == roundtrip) { "Plan failed roundtrip" }
    val corrected = PracticeRecord("read", date, PracticeOutcome.COMPLETED_BACKUP, 2, Instant.parse("2026-11-13T04:20:00Z"), "I managed one page")
    check(StorageCodec.record(StorageCodec.rawRecord(corrected)) == corrected)
    val days = ChallengeEngine.opportunities(roundtrip, java.time.ZonedDateTime.of(2026,11,14,12,0,0,0,roundtrip.zone), date.plusDays(5), listOf(corrected))
    check(days.first().state == OpportunityState.COMPLETED_BACKUP)
    check(days[1].state == OpportunityState.UNREPORTED)
    check(days[2].state == OpportunityState.NOT_SCHEDULED)
    check(StorageCodec.milestone("CALENDAR_DAYS") == MilestoneKind.CALENDAR_DAYS)
    println("EVOLUNE storage codec checks passed: recurrence, malformed input, plan & log roundtrip, domain integration.")
}
