package studio.evolune.data

import androidx.room.Database
import androidx.room.RoomDatabase

/** Version 1, schema export required for future explicit migration tests. */
@Database(
    entities = [
        GrowthAreaEntity::class, ChallengeEntity::class, ScheduleVersionEntity::class,
        PracticeRecordEntity::class, PauseWindowEntity::class, JournalEntryEntity::class
    ],
    version = 1,
    exportSchema = true
)
abstract class EvoluneDatabase : RoomDatabase() {
    abstract fun evoluneDao(): EvoluneDao
}
