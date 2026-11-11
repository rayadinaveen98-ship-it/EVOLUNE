package studio.evolune.app

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.runBlocking
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import studio.evolune.data.*
import java.time.Instant

/** Actual Android Room/JSON checks: synthetic data, no real private journal content. */
@RunWith(AndroidJUnit4::class)
class DataOwnershipTest {
    private suspend fun seed(dao: EvoluneDao) {
        dao.insertGrowthArea(GrowthAreaEntity("growth-1", "Reading", null, 1L, 1L))
        dao.createChallenge(
            ChallengeEntity(
                id = "challenge-1", growthAreaId = "growth-1",
                title = "Read gently", startDate = "2026-11-11", zoneId = "Asia/Kolkata",
                milestoneKind = "CALENDAR_DAYS", target = 21, createdAtEpochMs = 2L
            ),
            ScheduleVersionEntity(
                challengeId = "challenge-1", effectiveFrom = "2026-11-11",
                recurrenceKind = "DAILY", action = "Read for ten minutes",
                createdAtEpochMs = 2L
            )
        )
        dao.appendPractice("challenge-1", "2026-11-11", "MISSED", 3L, "Correct this later")
        dao.appendPractice("challenge-1", "2026-11-11", "COMPLETED", 4L, "Corrected")
        dao.insertPause(PauseWindowEntity(
            challengeId = "challenge-1", fromDate = "2026-11-12", throughDate = "2026-11-14"
        ))
        dao.createJournal(
            JournalEntryEntity(
                id = "journal-1", challengeId = "challenge-1", growthAreaId = "growth-1",
                body = "My private journal: café & 🌿", mood = "Calm", energy = 3,
                createdAtEpochMs = 5L, updatedAtEpochMs = 5L
            )
        )
    }

    @Test fun exportHasEveryHistoricRevisionAndUtf8Journal() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, EvoluneDatabase::class.java).build()
        try {
            val dao = db.evoluneDao()
            seed(dao)
            val json = JSONObject(EvoluneExportJson.encode(
                dao.exportLocalData(), Instant.parse("2026-11-11T00:00:00Z")
            ))
            assertEquals("EVOLUNE_LOCAL_EXPORT", json.getString("format"))
            assertEquals(1, json.getInt("schemaVersion"))
            assertEquals(1, json.getJSONArray("growthAreas").length())
            assertEquals(1, json.getJSONArray("challenges").length())
            assertEquals(1, json.getJSONArray("scheduleVersions").length())
            assertEquals(2, json.getJSONArray("practiceRecords").length())
            assertEquals("MISSED", json.getJSONArray("practiceRecords").getJSONObject(0).getString("outcome"))
            assertEquals("COMPLETED", json.getJSONArray("practiceRecords").getJSONObject(1).getString("outcome"))
            assertEquals(1, json.getJSONArray("pauseWindows").length())
            val journal = json.getJSONArray("journalEntries").getJSONObject(0)
            assertEquals("My private journal: café & 🌿", journal.getString("body"))
            assertEquals(3, journal.getInt("energy"))
        } finally { db.close() }
    }

    @Test fun deliberateDataEraseClearsAllPrivateTablesAtomically() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val db = Room.inMemoryDatabaseBuilder(context, EvoluneDatabase::class.java).build()
        try {
            val dao = db.evoluneDao()
            seed(dao)
            dao.eraseAllLocalData()
            val rows = dao.exportLocalData()
            assertTrue(rows.growthAreas.isEmpty())
            assertTrue(rows.challenges.isEmpty())
            assertTrue(rows.schedules.isEmpty())
            assertTrue(rows.practices.isEmpty())
            assertTrue(rows.pauses.isEmpty())
            assertTrue(rows.journal.isEmpty())
            assertNull(dao.findJournal("journal-1"))
        } finally { db.close() }
    }
}
