package studio.evolune.app

import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.time.Instant
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import studio.evolune.data.ChallengeSnapshot
import studio.evolune.domain.*

/** Tests schedule behavior without Android alarms or any notification permission. */
@RunWith(AndroidJUnit4::class)
class ReminderPlanningTest {
    private val start = LocalDate.of(2026, 11, 11)
    private val zone = ZoneId.of("Asia/Kolkata")
    private fun snapshot(
        recurrence: Recurrence = Recurrence.Daily,
        kind: MilestoneKind = MilestoneKind.CALENDAR_DAYS,
        target: Int = 21,
        records: List<PracticeRecord> = emptyList(),
        pauses: List<PauseWindow> = emptyList()
    ) = ChallengeSnapshot(
        ChallengePlan("practice","Read gently",start,zone,kind,target,
            listOf(ScheduleVersion(start,recurrence,"Read ten minutes"))),
        records,pauses,null
    )

    @Test fun localTimeIsInChallengeZoneNotUtc() {
        val next = ReminderPlanning.next(
            snapshot(),LocalTime.of(9,0), Instant.parse("2026-11-11T02:00:00Z"))
        assertEquals(Instant.parse("2026-11-11T03:30:00Z"),next)
    }

    @Test fun afterTodayReminderSkipsToNextScheduledDate() {
        val next = ReminderPlanning.next(
            snapshot(),LocalTime.of(9,0), Instant.parse("2026-11-11T04:00:00Z"))
        assertEquals(Instant.parse("2026-11-12T03:30:00Z"),next)
    }

    @Test fun recordedAndPausedDaysAreNotNotified() {
        val now=Instant.parse("2026-11-11T02:00:00Z")
        val completed = PracticeRecord("practice", start, PracticeOutcome.COMPLETED,
            1L, now)
        val reminder=ReminderPlanning.next(
            snapshot(records=listOf(completed),
                pauses=listOf(PauseWindow(start.plusDays(1), start.plusDays(2)))),
            LocalTime.of(9,0),now)
        assertEquals(Instant.parse("2026-11-14T03:30:00Z"),reminder)
    }

    @Test fun weekdayChallengeSkipsWeekend() {
        val reminder=ReminderPlanning.next(
            snapshot(recurrence=Recurrence.Weekdays),
            LocalTime.of(18,0),Instant.parse("2026-11-13T13:00:00Z"))
        assertEquals(Instant.parse("2026-11-16T12:30:00Z"),reminder)
    }

    @Test fun calendarEndAndSessionCompletionStopReminders() {
        val tomorrow=Instant.parse("2026-11-12T06:00:00Z")
        assertNull(ReminderPlanning.next(
            snapshot(target=1),LocalTime.of(9,0),tomorrow))
        val record=PracticeRecord("practice",start,PracticeOutcome.COMPLETED,1, tomorrow)
        assertNull(ReminderPlanning.next(
            snapshot(kind=MilestoneKind.COMPLETED_SESSIONS,target=1,
                records=listOf(record)),LocalTime.of(9,0),tomorrow))
    }

    @Test fun selectedTimeRespectsDstInChallengeZone() {
        val ny = ZoneId.of("America/New_York")
        val date=LocalDate.of(2026,3,7)
        val s=ChallengeSnapshot(
            ChallengePlan("dst","Read",date,ny,MilestoneKind.CALENDAR_DAYS,4,
                listOf(ScheduleVersion(date,Recurrence.Daily,"Read"))),
            emptyList(),emptyList(),null)
        val result=ReminderPlanning.next(s,LocalTime.of(9,0),
            Instant.parse("2026-03-08T01:00:00Z"))
        assertEquals(Instant.parse("2026-03-08T13:00:00Z"),result)
    }
}
