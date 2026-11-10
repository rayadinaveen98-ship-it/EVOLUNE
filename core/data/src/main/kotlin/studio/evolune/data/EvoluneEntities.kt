package studio.evolune.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** All private user content stays in application-private local storage. No shared/public export by default. */
@Entity(tableName = "growth_areas")
data class GrowthAreaEntity(
    @PrimaryKey val id: String,
    val name: String,
    val description: String? = null,
    val createdAtEpochMs: Long,
    val updatedAtEpochMs: Long
)

@Entity(
    tableName = "challenges",
    foreignKeys = [ForeignKey(
        entity = GrowthAreaEntity::class,
        parentColumns = ["id"], childColumns = ["growthAreaId"], onDelete = ForeignKey.SET_NULL
    )],
    indices = [Index("growthAreaId")]
)
data class ChallengeEntity(
    @PrimaryKey val id: String,
    val growthAreaId: String?,
    val title: String,
    val startDate: String, // ISO yyyy-MM-dd, challenge's local date
    val zoneId: String, // IANA zone ID, e.g. Asia/Kolkata
    val milestoneKind: String, // enum MilestoneKind name
    val target: Int,
    val createdAtEpochMs: Long,
    val archivedAtEpochMs: Long? = null
)

@Entity(
    tableName = "schedule_versions",
    foreignKeys = [ForeignKey(
        entity = ChallengeEntity::class,
        parentColumns = ["id"], childColumns = ["challengeId"], onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["challengeId", "effectiveFrom"], unique = true)]
)
data class ScheduleVersionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val challengeId: String,
    val effectiveFrom: String, // ISO yyyy-MM-dd; immutable once inserted
    val recurrenceKind: String,
    val recurrenceArgument: String? = null,
    val action: String,
    val cueTime: String? = null, // local ISO HH:mm[:ss]
    val fallbackAction: String? = null,
    val createdAtEpochMs: Long
)

@Entity(
    tableName = "practice_records",
    foreignKeys = [ForeignKey(
        entity = ChallengeEntity::class,
        parentColumns = ["id"], childColumns = ["challengeId"], onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["challengeId", "localDate", "revision"], unique = true)]
)
data class PracticeRecordEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val challengeId: String,
    val localDate: String,
    val outcome: String, // explicit user report only; never infer MISSED
    val revision: Long,
    val recordedAtEpochMs: Long,
    val note: String? = null
)

@Entity(
    tableName = "pause_windows",
    foreignKeys = [ForeignKey(
        entity = ChallengeEntity::class,
        parentColumns = ["id"], childColumns = ["challengeId"], onDelete = ForeignKey.CASCADE
    )],
    indices = [Index(value = ["challengeId", "fromDate", "throughDate"], unique = true)]
)
data class PauseWindowEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val challengeId: String,
    val fromDate: String,
    val throughDate: String
)

/** Deleting a challenge/area unlinks a private journal entry instead of deleting it unexpectedly. */
@Entity(
    tableName = "journal_entries",
    foreignKeys = [
        ForeignKey(entity = ChallengeEntity::class, parentColumns = ["id"], childColumns = ["challengeId"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(entity = GrowthAreaEntity::class, parentColumns = ["id"], childColumns = ["growthAreaId"], onDelete = ForeignKey.SET_NULL)
    ],
    indices = [Index("challengeId"), Index("growthAreaId")]
)
data class JournalEntryEntity(
    @PrimaryKey val id: String,
    val challengeId: String? = null,
    val growthAreaId: String? = null,
    val body: String,
    val mood: String? = null, // subjective user-chosen label, not diagnosis
    val energy: Int? = null, // optional self-report 1..5, not health assessment
    val createdAtEpochMs: Long,
    val updatedAtEpochMs: Long
)
