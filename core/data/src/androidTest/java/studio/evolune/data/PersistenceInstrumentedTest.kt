package studio.evolune.data

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import android.content.Context
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import studio.evolune.domain.*
import java.time.*

/** Requires an Android emulator/device. This test is NOT part of the local JVM test suite. */
@RunWith(AndroidJUnit4::class)
class PersistenceInstrumentedTest {
    private val context: Context = ApplicationProvider.getApplicationContext()
    private val databaseName = "evolune-test-room.db"
    private lateinit var db: EvoluneDatabase

    @Before fun setUp() {
        context.deleteDatabase(databaseName)
        db = Room.databaseBuilder(context, EvoluneDatabase::class.java, databaseName).build()
    }
    @After fun cleanUp() {
        db.close()
        context.deleteDatabase(databaseName)
    }

    @Test fun challengeAndCorrectionsSurviveDatabaseRestart() = runBlocking {
        val today = LocalDate.now(ZoneId.of("Asia/Kolkata"))
        val id = "challenge-1"
        val plan = ChallengePlan(id, "Read a little", today, ZoneId.of("Asia/Kolkata"),
            MilestoneKind.CALENDAR_DAYS, 21,
            listOf(ScheduleVersion(today, Recurrence.Daily, "Read for 10 minutes")))
        val repo = RoomEvoluneRepository(db.evoluneDao())
        repo.createChallenge(plan)
        repo.recordPractice(id, today, PracticeOutcome.PARTIAL)
        repo.recordPractice(id, today, PracticeOutcome.COMPLETED)
        db.close()
        db = Room.databaseBuilder(context, EvoluneDatabase::class.java, databaseName).build()
        val restored = RoomEvoluneRepository(db.evoluneDao()).snapshot(id)
        assertNotNull(restored)
        assertEquals(2, restored!!.records.size)
        assertEquals(PracticeOutcome.COMPLETED,
            ChallengeEngine.latestRecords(restored.plan, restored.records)[today]?.outcome)
        assertEquals("Read for 10 minutes", restored.plan.scheduleOn(today).action)
    }

    @Test fun challengeRemovalUnlinksButDoesNotDeleteJournal() = runBlocking {
        val today = LocalDate.now()
        val id="challenge-2"
        val repo = RoomEvoluneRepository(db.evoluneDao())
        repo.createChallenge(ChallengePlan(id,"Morning walk",today,ZoneId.systemDefault(),
            MilestoneKind.CALENDAR_DAYS,7,listOf(ScheduleVersion(today,Recurrence.Daily,"Walk"))))
        val now=Instant.now().toEpochMilli()
        repo.createReflection(JournalEntryEntity("journal-1", challengeId=id, body="Something private",createdAtEpochMs=now,updatedAtEpochMs=now))
        db.evoluneDao().deleteChallenge(id)
        val saved=db.evoluneDao().findJournal("journal-1")
        assertNotNull(saved)
        assertNull(saved!!.challengeId)
        assertEquals("Something private",saved.body)
    }
}
