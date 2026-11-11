package studio.evolune.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.room.Room
import kotlinx.coroutines.launch
import studio.evolune.data.*
import studio.evolune.domain.*
import java.time.*
import java.util.UUID

/** The only Android entry point. No remote database, account, telemetry or network permission. */
class MainActivity : ComponentActivity() {
    private lateinit var database: EvoluneDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        database = Room.databaseBuilder(applicationContext, EvoluneDatabase::class.java, "evolune_v1.db")
            .build() // Never use destructive migration for private user records.
        setContent {
            val dao = remember(database) { database.evoluneDao() }
            val repository = remember(dao) { RoomEvoluneRepository(dao) }
            EvoluneRoot(dao, repository)
        }
    }

    override fun onDestroy() {
        // Avoid keeping a stale Room instance open across rotation/activity recreation.
        if (::database.isInitialized) database.close()
        super.onDestroy()
    }
}

@Composable
private fun EvoluneRoot(dao: EvoluneDao, repository: RoomEvoluneRepository) {
    val scope = rememberCoroutineScope()
    val challenges by dao.observeChallenges().collectAsState(initial = emptyList())
    val journal by dao.observeJournal().collectAsState(initial = emptyList())
    val growthAreas by dao.observeGrowthAreas().collectAsState(initial = emptyList())
    var activeId by rememberSaveable { mutableStateOf<String?>(null) }
    var snapshot by remember { mutableStateOf<ChallengeSnapshot?>(null) }
    var page by rememberSaveable { mutableStateOf("today") }
    var notice by remember { mutableStateOf<String?>(null) }

    suspend fun refresh() {
        snapshot = activeId?.let { repository.snapshot(it) }
    }
    fun execute(action: suspend () -> Unit) {
        scope.launch {
            try {
                notice = null
                action()
                refresh()
            } catch (ex: Exception) {
                notice = ex.message ?: "Unable to save your changes."
            }
        }
    }

    LaunchedEffect(challenges) {
        if (activeId == null && challenges.isNotEmpty()) activeId = challenges.first().id
        if (activeId != null && challenges.none { it.id == activeId }) activeId = challenges.firstOrNull()?.id
    }
    LaunchedEffect(activeId) { refresh() }

    EvoluneShell(
        page = page,
        onPage = { page = it },
        notice = notice,
        onDismissNotice = { notice = null }
    ) {
        when (page) {
            "today" -> TodayPage(snapshot,
                onCreate = { page = "create" }, onOpen = { page = "detail" },
                onLog = { outcome ->
                    val s = snapshot ?: return@TodayPage
                    execute { repository.recordPractice(s.plan.id, LocalDate.now(s.plan.zone), outcome) }
                }, onAdjust = { page = "recovery" }
            )
            "challenges" -> ChallengeListPage(challenges, activeId,
                onSelect = { activeId = it; page = "detail" }, onCreate = { page = "create" }
            )
            "detail" -> ChallengeDetailPage(snapshot,
                onAdjust = { page = "recovery" }, onReview = { page = "review" },
                onBack = { page = "challenges" }, onLog = { outcome ->
                    val s = snapshot ?: return@ChallengeDetailPage
                    execute { repository.recordPractice(s.plan.id, LocalDate.now(s.plan.zone), outcome) }
                }
            )
            "create" -> CreateChallengePage(growthAreas,
                onBack = { page = "challenges" },
                onCreate = { request ->
                    execute {
                        val now = LocalDate.now()
                        val zone = ZoneId.systemDefault()
                        val id = UUID.randomUUID().toString()
                        val initial = ScheduleVersion(
                            effectiveFrom = now,
                            recurrence = request.recurrence,
                            action = request.action.trim(),
                            cueTime = request.cueTime,
                            fallbackAction = request.fallback?.trim()?.ifEmpty { null }
                        )
                        val plan = ChallengePlan(
                            id = id, title = request.title.trim(), startDate = now, zone = zone,
                            milestoneKind = request.kind, target = request.target, schedules = listOf(initial)
                        )
                        repository.createChallenge(plan, request.areaId)
                        activeId = id
                        page = "detail"
                    }
                }
            )
            "recovery" -> RecoveryPage(snapshot,
                onBack = { page = "detail" },
                onRevise = { nextAction, nextRecurrence ->
                    val s = snapshot ?: return@RecoveryPage
                    execute {
                        repository.reviseSchedule(s.plan.id,
                            ScheduleVersion(LocalDate.now(s.plan.zone).plusDays(1),
                                nextRecurrence, nextAction, s.plan.schedules.last().cueTime,
                                s.plan.schedules.last().fallbackAction))
                        page = "detail"
                    }
                },
                onPause = {
                    val s = snapshot ?: return@RecoveryPage
                    execute {
                        val tomorrow = LocalDate.now(s.plan.zone).plusDays(1)
                        repository.addPause(s.plan.id, PauseWindow(tomorrow, tomorrow.plusDays(2)))
                        page = "detail"
                    }
                }
            )
            "review" -> ReviewPage(snapshot, onBack = { page = "detail" })
            "growth" -> GrowthPage(growthAreas,
                onCreateArea = { title ->
                    execute {
                        val t = Instant.now().toEpochMilli()
                        dao.insertGrowthArea(GrowthAreaEntity(UUID.randomUUID().toString(), title.trim(), null, t, t))
                    }
                }, onStart = { page = "create" }
            )
            "journal" -> JournalPage(journal, onSave = { existing, body, mood, energy ->
                execute {
                    val now = Instant.now().toEpochMilli()
                    if (existing == null) repository.createReflection(
                        JournalEntryEntity(id = UUID.randomUUID().toString(),
                            body = body, mood = mood, energy = energy,
                            createdAtEpochMs = now, updatedAtEpochMs = now)
                    ) else repository.editReflection(existing.copy(
                        body = body, mood = mood, energy = energy, updatedAtEpochMs = now
                    ))
                }
            }, onDelete = { entry -> execute { repository.deleteReflection(entry.id) } })
            "insights" -> InsightsPage(snapshot)
            else -> TodayPage(snapshot, { page = "create" }, { page = "detail" }, {}, { page = "recovery" })
        }
    }
}
