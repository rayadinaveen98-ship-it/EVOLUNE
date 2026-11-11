package studio.evolune.data

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.time.Clock
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZoneOffset
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import studio.evolune.domain.*

/**
 * Real Android/Room tests for persistence-layer guards.
 * These guard against misleading records even if a future UI accidentally enables a log action.
 */
@RunWith(AndroidJUnit4::class)
class RepositoryGuardTest {
    private val zone = ZoneId.of("Asia/Kolkata")
    private val start = LocalDate.of(2026, 11, 11)

    private fun clock(on: String): Clock =
        Clock.fixed(Instant.parse(on), ZoneOffset.UTC)

    private fun plan(id: String, target: Int = 21) = ChallengePlan(
        id = id, title = "Walk gently", startDate = start, zone = zone,
        milestoneKind = MilestoneKind.CALENDAR_DAYS, target = target,
        schedules = listOf(ScheduleVersion(start, Recurrence.Daily, "Walk ten minutes"))
    )

    private suspend fun expectRejection(block: suspend () -> Unit) {
        try {
            block()
            fail("Expected invalid practice/schedule to be rejected")
        } catch (expected: IllegalArgumentException) {
            assertTrue(expected.message?.isNotBlank() == true)
        }
    }

    @Test fun pausedDaysCannotBeRecordedButValidPastDaysCan() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, EvoluneDatabase::class.java).build()
        try {
            val before = RoomEvoluneRepository(db.evoluneDao(), clock("2026-11-11T06:00:00Z"))
            before.createChallenge(plan("pause-guard"))
            before.addPause("pause-guard", PauseWindow(start.plusDays(1), start.plusDays(3)))
            val after = RoomEvoluneRepository(db.evoluneDao(), clock("2026-11-13T06:00:00Z"))
            expectRejection { after.recordPractice("pause-guard", start.plusDays(2), PracticeOutcome.COMPLETED) }
            after.recordPractice("pause-guard", start, PracticeOutcome.COMPLETED)
            val snapshot = requireNotNull(after.snapshot("pause-guard"))
            assertTrue(snapshot.records.any { it.date == start && it.outcome == PracticeOutcome.COMPLETED })
            assertTrue(snapshot.records.none { it.date == start.plusDays(2) })
        } finally {
            db.close()
        }
    }

    @Test fun calendarMilestoneCannotAddRecordsAfterEnd() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, EvoluneDatabase::class.java).build()
        try {
            val after = RoomEvoluneRepository(db.evoluneDao(), clock("2026-11-13T06:00:00Z"))
            after.createChallenge(plan("period-guard", target = 2))
            expectRejection { after.recordPractice("period-guard", start.plusDays(2), PracticeOutcome.COMPLETED) }
            // A real past opportunity inside the original period can still be corrected.
            after.recordPractice("period-guard", start.plusDays(1), PracticeOutcome.PARTIAL)
            assertTrue(requireNotNull(after.snapshot("period-guard")).records.size == 1)
        } finally {
            db.close()
        }
    }

    @Test fun pauseCannotRewriteEarlierOpportunityHistory() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, EvoluneDatabase::class.java).build()
        try {
            val after = RoomEvoluneRepository(db.evoluneDao(), clock("2026-11-13T06:00:00Z"))
            after.createChallenge(plan("backdate-guard"))
            expectRejection { after.addPause("backdate-guard", PauseWindow(start, start.plusDays(1))) }
            assertTrue(requireNotNull(after.snapshot("backdate-guard")).pauses.isEmpty())
        } finally {
            db.close()
        }
    }
}
