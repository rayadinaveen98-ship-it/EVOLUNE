package studio.evolune.app

import android.Manifest
import android.os.Bundle
import android.os.Build
import android.content.pm.PackageManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.room.Room
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import studio.evolune.data.*
import studio.evolune.domain.*
import java.time.*
import java.util.UUID

/** The only Android entry point. No remote database, account, telemetry or network permission. */
class MainActivity : ComponentActivity() {
    private lateinit var database: EvoluneDatabase
    private val reminderScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

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

    override fun onResume() {
        super.onResume()
        if (::database.isInitialized) {
            reminderScope.launch {
                try {
                    ReminderScheduler.rescheduleAll(applicationContext, database.evoluneDao())
                } catch (_: Exception) {
                    // App remains usable even if Android does not permit reminders.
                }
            }
        }
    }

    override fun onDestroy() {
        reminderScope.cancel()
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
    var pendingImport by remember { mutableStateOf<StoredLocalExport?>(null) }
    var reminderEnabled by remember { mutableStateOf(false) }
    var pendingReminderPermission by remember { mutableStateOf<Pair<String,String>?>(null) }
    val context = LocalContext.current
    // Never show setup to existing V1 users or people restoring a database.
    // A null entry gate represents the brief initial local Room lookup.
    var showOpenPathWelcome by remember { mutableStateOf<Boolean?>(null) }
    var showGuidedSetup by remember { mutableStateOf<Boolean?>(null) }
    var savingSetup by remember { mutableStateOf(false) }
    var setupError by remember { mutableStateOf<String?>(null) }
    LaunchedEffect(Unit) {
        try {
            val hasData = dao.listChallenges().isNotEmpty() ||
                dao.listGrowthAreas().isNotEmpty() || dao.listJournal().isNotEmpty()
            val prefs = context.getSharedPreferences(OpenPathFirstRun.PREF_FILE, android.content.Context.MODE_PRIVATE)
            val qaMode = (context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0 &&
                (prefs.getBoolean(OpenPathFirstRun.FORCE_FULL_MOTION_QA, false) ||
                    prefs.getBoolean(OpenPathFirstRun.FORCE_REDUCED_MOTION_QA, false))
            showOpenPathWelcome = !OpenPathFirstRun.completed(context) && (!hasData || qaMode)
            showGuidedSetup = !GuidedSetupStatus.isComplete(context) && !hasData
        } catch (ex: Exception) {
            showOpenPathWelcome = false
            showGuidedSetup = false
            notice = "Unable to check previous setup. Your saved records were not changed."
        }
    }

    fun turnOnReminder(id: String, time: String) {
        scope.launch {
            try {
                val created = ReminderScheduler.enable(context, dao, id, LocalTime.parse(time))
                if (activeId == id) reminderEnabled = true
                notice = if (created) "Reminder enabled. Android may deliver it later than the chosen time."
                    else "Reminder enabled; there is no upcoming scheduled opportunity yet."
            } catch (ex: Exception) {
                notice = ex.message ?: "Could not enable reminders."
            }
        }
    }

    val reminderPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        val pending = pendingReminderPermission
        pendingReminderPermission = null
        if (granted && pending != null) turnOnReminder(pending.first, pending.second)
        else if (!granted) notice = "Notifications remain off. You can enable them later in Android Settings."
    }

    /** SAF export keeps the sensitive archive on a user-selected local destination. */
    val exportLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.CreateDocument("application/json")
    ) { uri ->
        if (uri != null) {
            scope.launch {
                try {
                    val snapshotRows = dao.exportLocalData()
                    val data = withContext(Dispatchers.Default) {
                        EvoluneExportJson.encode(snapshotRows)
                    }
                    withContext(Dispatchers.IO) {
                        val output = requireNotNull(context.contentResolver.openOutputStream(uri, "w")) {
                            "Selected location could not be opened for writing"
                        }
                        output.bufferedWriter(Charsets.UTF_8).use { it.write(data) }
                    }
                    notice = "Export saved. Keep this private file safe."
                } catch (ex: Exception) {
                    notice = "Export failed. Your app data was not changed. " +
                        (ex.message ?: "Please choose another file location.")
                }
            }
        }
    }

    /** Read an untrusted file with a size cap, then validate BEFORE any database write. */
    val importLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument()
    ) { uri ->
        if (uri != null) {
            scope.launch {
                pendingImport = null
                try {
                    val contents = withContext(Dispatchers.IO) {
                        val source = requireNotNull(context.contentResolver.openInputStream(uri)) {
                            "Selected backup could not be opened"
                        }
                        source.use { input ->
                            val bytes = java.io.ByteArrayOutputStream()
                            val buffer = ByteArray(8192)
                            var total = 0
                            while (true) {
                                val read = input.read(buffer)
                                if (read < 0) break
                                total += read
                                require(total <= EvoluneImportJson.MAX_FILE_BYTES) {
                                    "Backup is larger than 10 MB"
                                }
                                bytes.write(buffer, 0, read)
                            }
                            String(bytes.toByteArray(), Charsets.UTF_8)
                        }
                    }
                    val validated = withContext(Dispatchers.Default) {
                        EvoluneImportJson.decode(contents)
                    }
                    pendingImport = validated
                    notice = "Backup validated. Nothing was changed. Type REPLACE to restore it."
                    page = "data"
                } catch (ex: Exception) {
                    pendingImport = null
                    notice = "Backup could not be loaded. Your current data is safe. " +
                        (ex.message ?: "Please check that this is an EVOLUNE export.")
                }
            }
        }
    }

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
    LaunchedEffect(activeId) {
        refresh()
        reminderEnabled = activeId?.let { ReminderScheduler.isOptedIn(context, it) } ?: false
    }

    if (showOpenPathWelcome == null || showGuidedSetup == null) {
        // No fake intro while local data is checked: stable and accessible holding surface.
        androidx.compose.material3.Surface(
            modifier = androidx.compose.ui.Modifier.fillMaxSize(),
            color = androidx.compose.ui.graphics.Color(0xFFFAF8F3)
        ) {
            androidx.compose.foundation.layout.Box(
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                androidx.compose.material3.Text(
                    "Opening your path…",
                    color = androidx.compose.ui.graphics.Color(0xFF36553E)
                )
            }
        }
    } else if (showOpenPathWelcome == true) {
        OpenPathWelcome(onFinished = {
            OpenPathFirstRun.finish(context)
            showOpenPathWelcome = false
        })
    } else if (showGuidedSetup == true) {
        GuidedSetup(
            areas = growthAreas,
            busy = savingSetup,
            error = setupError,
            onSkip = {
                GuidedSetupStatus.finish(context)
                showGuidedSetup = false
                page = "today"
            },
            onCreate = { request ->
                if (!savingSetup) {
                    savingSetup = true
                    setupError = null
                    scope.launch {
                        try {
                            val zone = ZoneId.systemDefault()
                            val today = LocalDate.now(zone)
                            val existing = request.areaName?.let { chosen ->
                                dao.listGrowthAreas().firstOrNull {
                                    it.name.trim().equals(chosen.trim(), ignoreCase = true)
                                }
                            }
                            val newArea = if (request.areaName != null && existing == null) {
                                val time = Instant.now().toEpochMilli()
                                GrowthAreaEntity(
                                    id = UUID.randomUUID().toString(),
                                    name = request.areaName.trim(),
                                    createdAtEpochMs = time, updatedAtEpochMs = time
                                )
                            } else null
                            val id = UUID.randomUUID().toString()
                            val plan = ChallengePlan(
                                id = id, title = request.title, startDate = today,
                                zone = zone, milestoneKind = request.milestoneKind,
                                target = request.target,
                                schedules = listOf(ScheduleVersion(
                                    effectiveFrom = today, recurrence = request.recurrence,
                                    action = request.action, cueTime = request.cueTime,
                                    fallbackAction = request.fallbackAction
                                ))
                            )
                            repository.createChallenge(
                                plan = plan, growthAreaId = existing?.id ?: newArea?.id,
                                newArea = newArea
                            )
                            activeId = id
                            snapshot = repository.snapshot(id)
                            GuidedSetupStatus.finish(context)
                            showGuidedSetup = false
                            page = "today"
                        } catch (ex: Exception) {
                            setupError = ex.message ?: "Unable to save your path. Nothing was lost."
                        } finally {
                            savingSetup = false
                        }
                    }
                }
            }
        )
    } else EvoluneShell(
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
                    execute { repository.recordPractice(s.plan.id, LocalDate.now(s.plan.zone), outcome)
                        ReminderScheduler.reschedule(context, dao, s.plan.id) }
                }, onAdjust = { page = "recovery" },
                onJournal = { page = "journal" }
            )
            "challenges" -> ChallengeListPage(challenges, activeId,
                onSelect = { activeId = it; page = "detail" }, onCreate = { page = "create" }
            )
            "detail" -> ChallengeDetailPage(snapshot,
                onAdjust = { page = "recovery" }, onReview = { page = "review" },
                onBack = { page = "challenges" },
                onReminders = { page = "reminders" },
                onLog = { outcome ->
                    val s = snapshot ?: return@ChallengeDetailPage
                    execute { repository.recordPractice(s.plan.id, LocalDate.now(s.plan.zone), outcome)
                        ReminderScheduler.reschedule(context, dao, s.plan.id) }
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
                        ReminderScheduler.reschedule(context, dao, s.plan.id)
                        page = "detail"
                    }
                },
                onPause = {
                    val s = snapshot ?: return@RecoveryPage
                    execute {
                        val tomorrow = LocalDate.now(s.plan.zone).plusDays(1)
                        repository.addPause(s.plan.id, PauseWindow(tomorrow, tomorrow.plusDays(2)))
                        ReminderScheduler.reschedule(context, dao, s.plan.id)
                        page = "detail"
                    }
                }
            )
            "reminders" -> ReminderSettingsPage(
                s = snapshot,
                enabled = reminderEnabled,
                permissionAvailable = ReminderScheduler.canNotify(context),
                selectedTime = activeId?.let { ReminderScheduler.time(context,it).toString() } ?: "09:00",
                nextAt = activeId?.let { ReminderScheduler.scheduledAt(context,it) } ?: -1L,
                onBack = { page = "detail" },
                onEnable = { at ->
                    val id = activeId
                    if (id == null) notice = "Choose a challenge before enabling reminders."
                    else if (Build.VERSION.SDK_INT >= 33 &&
                        context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                        pendingReminderPermission = id to at
                        reminderPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                    } else if (!ReminderScheduler.canNotify(context)) {
                        notice = "Notifications are blocked in Android Settings. Enable EVOLUNE notifications there."
                    } else turnOnReminder(id,at)
                },
                onDisable = {
                    activeId?.let { id ->
                        ReminderScheduler.disable(context,id)
                        reminderEnabled = false
                        notice = "Gentle reminder turned off."
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
            }, onDelete = { entry -> execute { repository.deleteReflection(entry.id) } },
                onDataPrivacy = { page = "data" })
            "data" -> DataPrivacyPage(
                onBack = { page = "journal" },
                onExport = {
                    val name = "evolune-export-" + LocalDate.now().toString() + ".json"
                    exportLauncher.launch(name)
                },
                onPickBackup = {
                    importLauncher.launch(arrayOf("application/json", "text/plain", "application/octet-stream"))
                },
                importPreview = pendingImport?.let {
                    "Contains " + it.challenges.size + " challenges, " +
                        it.practices.size + " historic practice records, " +
                        it.journal.size + " journal entries and " +
                        it.growthAreas.size + " growth areas."
                },
                onRestore = {
                    val verified = pendingImport
                    if (verified != null) {
                        execute {
                            dao.replaceAllLocalData(verified)
                            ReminderScheduler.disableAll(context)
                            reminderEnabled = false
                            activeId = null
                            snapshot = null
                            pendingImport = null
                            page = "today"
                            notice = "Restore completed. All current device records were replaced with the selected backup."
                        }
                    }
                },
                onCancelRestore = { pendingImport = null },
                onErase = {
                    execute {
                        dao.eraseAllLocalData()
                        ReminderScheduler.disableAll(context)
                        reminderEnabled = false
                        pendingImport = null
                        activeId = null
                        snapshot = null
                        page = "today"
                        notice = "All EVOLUNE local data was erased. Export files outside the app were not touched."
                    }
                }
            )
            "insights" -> InsightsPage(snapshot)
            else -> TodayPage(snapshot, { page = "create" }, { page = "detail" }, {}, { page = "recovery" }, { page = "journal" })
        }
    }
}
