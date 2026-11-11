package studio.evolune.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.time.LocalDate
import java.time.ZoneId
import java.util.UUID
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import studio.evolune.domain.*

/** Runs on a real Android emulator/device; this is NOT the desktop SQLite contract test. */
@RunWith(AndroidJUnit4::class)
class RoomPersistenceTest {
    @Test fun reopenRetainsActivityHistoryAndPrivateJournal() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val file = "evolune-test-${UUID.randomUUID()}.db"
        val date = LocalDate.of(2026, 11, 11)
        val plan = ChallengePlan(
            id = "test", title = "Ten-minute reading", startDate = date,
            zone = ZoneId.of("Asia/Kolkata"), milestoneKind = MilestoneKind.CALENDAR_DAYS,
            target = 21, schedules = listOf(ScheduleVersion(date, Recurrence.Daily, "Read ten minutes"))
        )
        val db = Room.databaseBuilder(context, EvoluneDatabase::class.java, file).build()
        try {
            val repository = RoomEvoluneRepository(db.evoluneDao())
            repository.createChallenge(plan)
            db.evoluneDao().appendPractice("test", date.toString(), "MISSED", 42L, null)
            db.evoluneDao().appendPractice("test", date.toString(), "COMPLETED", 43L, "Forgot to log")
            db.evoluneDao().createJournal(JournalEntryEntity(
                id = "journal", challengeId = "test", body = "I tried something small.",
                createdAtEpochMs = 44L, updatedAtEpochMs = 44L
            ))
        } finally { db.close() }
        val reopened = Room.databaseBuilder(context, EvoluneDatabase::class.java, file).build()
        try {
            val snapshot = RoomEvoluneRepository(reopened.evoluneDao()).snapshot("test")!!
            assertEquals(2, snapshot.records.size)
            assertEquals(PracticeOutcome.COMPLETED, ChallengeEngine.latestRecords(plan, snapshot.records)[date]?.outcome)
            assertEquals("I tried something small.", reopened.evoluneDao().findJournal("journal")?.body)
            reopened.evoluneDao().deleteChallenge("test")
            assertNull(reopened.evoluneDao().findChallenge("test"))
            assertEquals("I tried something small.", reopened.evoluneDao().findJournal("journal")?.body)
        } finally {
            reopened.close()
            context.deleteDatabase(file)
        }
    }
}
