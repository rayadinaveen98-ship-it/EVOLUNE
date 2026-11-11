package studio.evolune.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
abstract class EvoluneDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun insertGrowthArea(area: GrowthAreaEntity)

    @Update
    abstract suspend fun updateGrowthArea(area: GrowthAreaEntity): Int

    @Query("SELECT * FROM growth_areas ORDER BY createdAtEpochMs, id")
    abstract fun observeGrowthAreas(): Flow<List<GrowthAreaEntity>>
    
    @Query("SELECT * FROM growth_areas ORDER BY createdAtEpochMs, id")
    abstract suspend fun listGrowthAreas(): List<GrowthAreaEntity>

    @Insert(onConflict = OnConflictStrategy.ABORT)
    protected abstract suspend fun insertChallenge(challenge: ChallengeEntity)

    @Insert(onConflict = OnConflictStrategy.ABORT)
    protected abstract suspend fun insertInitialSchedule(version: ScheduleVersionEntity)

    /** New challenge and its initial schedule must either both persist or neither persist. */
    @Transaction
    open suspend fun createChallenge(challenge: ChallengeEntity, initial: ScheduleVersionEntity) {
        require(challenge.id == initial.challengeId)
        require(challenge.startDate == initial.effectiveFrom)
        require(initial.action.isNotBlank())
        insertChallenge(challenge)
        insertInitialSchedule(initial)
    }

    @Query("SELECT * FROM challenges WHERE id = :id LIMIT 1")
    abstract suspend fun findChallenge(id: String): ChallengeEntity?

    @Query("SELECT * FROM challenges ORDER BY createdAtEpochMs DESC, id")
    abstract fun observeChallenges(): Flow<List<ChallengeEntity>>

    @Query("SELECT * FROM challenges ORDER BY createdAtEpochMs DESC, id")
    abstract suspend fun listChallenges(): List<ChallengeEntity>

    @Transaction
    open suspend fun loadChallengeRows(id: String): StoredChallengeRows? {
        val challenge = findChallenge(id) ?: return null
        return StoredChallengeRows(challenge, schedulesFor(id), historyFor(id), pausesFor(id))
    }

    @Query("SELECT * FROM schedule_versions WHERE challengeId = :id ORDER BY effectiveFrom ASC")
    abstract suspend fun schedulesFor(id: String): List<ScheduleVersionEntity>

    @Query("SELECT * FROM practice_records WHERE challengeId = :id ORDER BY localDate ASC, revision ASC")
    abstract suspend fun historyFor(id: String): List<PracticeRecordEntity>

    @Query("SELECT * FROM pause_windows WHERE challengeId = :id ORDER BY fromDate ASC, throughDate ASC")
    abstract suspend fun pausesFor(id: String): List<PauseWindowEntity>

    @Query("SELECT MAX(effectiveFrom) FROM schedule_versions WHERE challengeId = :id")
    protected abstract suspend fun latestScheduleDate(id: String): String?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    protected abstract suspend fun insertRevision(version: ScheduleVersionEntity)

    /** Preserves historic schedules and never silently rewrites previously due days. */
    @Transaction
    open suspend fun appendSchedule(version: ScheduleVersionEntity) {
        val previous = requireNotNull(latestScheduleDate(version.challengeId)) { "Unknown challenge or missing initial schedule" }
        require(version.effectiveFrom > previous) { "Schedule revision must take effect after the latest version" }
        require(version.action.isNotBlank())
        insertRevision(version)
    }

    @Query("SELECT COALESCE(MAX(revision), 0) FROM practice_records WHERE challengeId = :challengeId AND localDate = :day")
    protected abstract suspend fun lastPracticeRevision(challengeId: String, day: String): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    protected abstract suspend fun insertPractice(record: PracticeRecordEntity): Long

    /** Append-only correction. SQLite transaction serializes the revision lookup and insert. */
    @Transaction
    open suspend fun appendPractice(challengeId: String, day: String, outcome: String, at: Long, note: String?): Long {
        requireNotNull(findChallenge(challengeId)) { "Unknown challenge" }
        require(outcome in setOf("COMPLETED", "PARTIAL", "RESTED", "MISSED", "COMPLETED_BACKUP"))
        require(day.matches(Regex("\\d{4}-\\d{2}-\\d{2}")))
        val revision = lastPracticeRevision(challengeId, day) + 1
        return insertPractice(PracticeRecordEntity(
            challengeId = challengeId, localDate = day, outcome = outcome,
            revision = revision, recordedAtEpochMs = at, note = note
        ))
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    abstract suspend fun insertPause(pause: PauseWindowEntity): Long

    @Insert(onConflict = OnConflictStrategy.ABORT)
    abstract suspend fun createJournal(entry: JournalEntryEntity)

    @Update
    abstract suspend fun updateJournal(entry: JournalEntryEntity): Int

    @Query("SELECT * FROM journal_entries ORDER BY updatedAtEpochMs DESC, id")
    abstract fun observeJournal(): Flow<List<JournalEntryEntity>>
    
    @Query("SELECT * FROM journal_entries ORDER BY updatedAtEpochMs DESC, id")
    abstract suspend fun listJournal(): List<JournalEntryEntity>

    @Query("SELECT * FROM journal_entries WHERE id = :id LIMIT 1")
    abstract suspend fun findJournal(id: String): JournalEntryEntity?

    @Query("DELETE FROM journal_entries WHERE id = :id")
    abstract suspend fun deleteJournal(id: String): Int

    @Query("DELETE FROM challenges WHERE id = :id")
    abstract suspend fun deleteChallenge(id: String): Int

    @Query("DELETE FROM growth_areas WHERE id = :id")
    abstract suspend fun deleteGrowthArea(id: String): Int

    /**
     * A read-consistent local export: all linked private data is collected in one
     * Room transaction, so a concurrent journal/challenge edit cannot split the snapshot.
     */
    @Transaction
    open suspend fun exportLocalData(): StoredLocalExport {
        val challenges = listChallenges()
        return StoredLocalExport(
            growthAreas = listGrowthAreas(),
            challenges = challenges,
            schedules = challenges.flatMap { schedulesFor(it.id) },
            practices = challenges.flatMap { historyFor(it.id) },
            pauses = challenges.flatMap { pausesFor(it.id) },
            journal = listJournal()
        )
    }

    @Query("DELETE FROM journal_entries")
    protected abstract suspend fun deleteAllJournalEntries()

    @Query("DELETE FROM challenges")
    protected abstract suspend fun deleteAllChallenges()

    @Query("DELETE FROM growth_areas")
    protected abstract suspend fun deleteAllGrowthAreas()

    /** Explicitly user-confirmed reset, one atomic transaction. */
    @Transaction
    open suspend fun eraseAllLocalData() {
        deleteAllJournalEntries()
        deleteAllChallenges()
        deleteAllGrowthAreas()
    }
}

/** Complete local snapshot, including historic schedule and outcome revisions. */
data class StoredLocalExport(
    val growthAreas: List<GrowthAreaEntity>,
    val challenges: List<ChallengeEntity>,
    val schedules: List<ScheduleVersionEntity>,
    val practices: List<PracticeRecordEntity>,
    val pauses: List<PauseWindowEntity>,
    val journal: List<JournalEntryEntity>
)

/** Atomic, consistent read for the domain engine; no UI should independently join these tables. */
data class StoredChallengeRows(
    val challenge: ChallengeEntity,
    val schedules: List<ScheduleVersionEntity>,
    val history: List<PracticeRecordEntity>,
    val pauses: List<PauseWindowEntity>
)
