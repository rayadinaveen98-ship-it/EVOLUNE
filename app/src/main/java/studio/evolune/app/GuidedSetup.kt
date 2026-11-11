package studio.evolune.app

import android.content.Context
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import studio.evolune.data.GrowthAreaEntity
import studio.evolune.domain.MilestoneKind
import studio.evolune.domain.Recurrence
import java.time.LocalTime

internal object GuidedSetupStatus {
    const val PREF = "evolune_v2_setup"
    const val COMPLETED = "setup_completed"
    fun isComplete(context: Context) =
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE).getBoolean(COMPLETED, false)
    fun finish(context: Context) {
        context.getSharedPreferences(PREF, Context.MODE_PRIVATE)
            .edit().putBoolean(COMPLETED, true).apply()
    }
}

internal data class GuidedSetupRequest(
    val areaName: String?,
    val title: String,
    val action: String,
    val target: Int,
    val milestoneKind: MilestoneKind,
    val recurrence: Recurrence,
    val cueTime: LocalTime?,
    val fallbackAction: String?
)

private val Ink = Color(0xFF26392D)
private val Forest = Color(0xFF36553E)
private val Secondary = Color(0xFF65746C)
private val Paper = Color(0xFFFFFEFA)
private val Cream = Color(0xFFFAF8F3)
private val Pale = Color(0xFFE7ECE2)
private val Line = Color(0xFFE5E2D9)

private val defaults = listOf(
    "Body & energy", "Mind & awareness", "Daily foundations",
    "Learning & craft", "Connection", "Confidence & courage"
)

@Composable
private fun SetupTitle(n: Int, headline: String, subtitle: String) {
    Column(verticalArrangement=Arrangement.spacedBy(9.dp)) {
        EvoluneEyebrow("STEP " + (n + 1) + " OF 4")
        EvolunePageHeading(headline)
        Text(subtitle,style=EvoluneType.typography.bodyLarge,
            color=Secondary,softWrap=true)
    }
}

@Composable
private fun SetupCard(content: @Composable ColumnScope.() -> Unit) {
    EvolunePanel(content)
}

@Composable
private fun SetupChoice(label: String, selected: Boolean, onClick: () -> Unit) {
    Surface(
        Modifier.fillMaxWidth().heightIn(min = 56.dp)
            .selectable(selected = selected, role = Role.RadioButton, onClick = onClick),
        color = if (selected) Pale else Paper,
        shape = RoundedCornerShape(14.dp),
        border = BorderStroke(if (selected) 1.5.dp else 1.dp,
            if (selected) Forest else Line)
    ) {
        Row(Modifier.fillMaxWidth().padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(19.dp).clip(RoundedCornerShape(12.dp))
                .background(if (selected) Forest else Pale),
                contentAlignment = Alignment.Center) {
                if (selected) Box(Modifier.size(7.dp).clip(RoundedCornerShape(7.dp))
                    .background(Paper))
            }
            Spacer(Modifier.width(12.dp))
            Text(label, fontSize = 15.sp, lineHeight = 21.sp, color = Ink,
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                modifier = Modifier.weight(1f))
        }
    }
}

@Composable
internal fun GuidedSetup(
    areas: List<GrowthAreaEntity>,
    busy: Boolean,
    error: String?,
    onSkip: () -> Unit,
    onCreate: (GuidedSetupRequest) -> Unit
) {
    var step by rememberSaveable { mutableIntStateOf(0) }
    var area by rememberSaveable { mutableStateOf("") }
    var title by rememberSaveable { mutableStateOf("") }
    var action by rememberSaveable { mutableStateOf("") }
    var target by rememberSaveable { mutableStateOf("21") }
    var mode by rememberSaveable { mutableStateOf(MilestoneKind.CALENDAR_DAYS.name) }
    var cadence by rememberSaveable { mutableStateOf("DAILY") }
    var optional by rememberSaveable { mutableStateOf(false) }
    var cue by rememberSaveable { mutableStateOf("") }
    var fallback by rememberSaveable { mutableStateOf("") }
    var warning by rememberSaveable { mutableStateOf("") }
    val options = remember(areas) {
        (defaults + areas.map { it.name.trim() }).filter { it.isNotBlank() }
            .distinctBy { it.lowercase() }
    }

    fun next() {
        warning = ""
        when (step) {
            0 -> step = 1
            1 -> {
                if (title.isBlank() || action.isBlank()) warning = "Add a title and one repeatable action."
                else if (title.length > 120 || action.length > 500) warning = "Please shorten your practice."
                else step = 2
            }
            2 -> {
                if (target.toIntOrNull()?.let { it in 1..3650 } != true)
                    warning = "Choose a milestone from 1 to 3650."
                else if (cue.isNotBlank() && runCatching { LocalTime.parse(cue.trim()) }.isFailure)
                    warning = "Use a time such as 07:30, or leave it blank."
                else if (fallback.length > 500) warning = "Please shorten the backup action."
                else step = 3
            }
            3 -> if (!busy) onCreate(GuidedSetupRequest(
                area.ifBlank { null }, title.trim(), action.trim(), target.toInt(),
                MilestoneKind.valueOf(mode),
                when (cadence) {
                    "WEEKDAYS" -> Recurrence.Weekdays
                    "WEEKENDS" -> Recurrence.Weekends
                    else -> Recurrence.Daily
                },
                cue.trim().ifBlank { null }?.let(LocalTime::parse),
                fallback.trim().ifBlank { null }
            ))
        }
    }

    BackHandler {
        if (!busy) {
            if (step > 0) { warning = ""; step-- }
            else onSkip()
        }
    }
    Column(Modifier.fillMaxSize().background(Cream).systemBarsPadding().imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally) {
        Column(Modifier.widthIn(max = 680.dp).fillMaxWidth()) {
            Row(Modifier.fillMaxWidth().padding(start = 20.dp, end = 12.dp, top = 7.dp),
                verticalAlignment = Alignment.CenterVertically) {
                Text("EVOLUNE", fontFamily = FontFamily.Serif, fontSize = 19.sp,
                    color = Forest, fontWeight = FontWeight.SemiBold, letterSpacing = 1.sp)
                Spacer(Modifier.weight(1f))
                TextButton(onClick = onSkip, enabled = !busy,
                    modifier = Modifier.heightIn(min = 48.dp)) {
                    Text("Explore first", color = Secondary, fontSize = 14.sp)
                }
            }
            Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                repeat(4) { i ->
                    Box(Modifier.weight(1f).height(4.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(if (i <= step) Forest else Line))
                }
            }
        }

        Column(Modifier.weight(1f).widthIn(max = 680.dp).fillMaxWidth()
            .verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 9.dp),
            verticalArrangement = Arrangement.spacedBy(17.dp)) {
            when (step) {
                0 -> {
                    SetupTitle(0, "Choose where you'd like to grow.",
                        "There is no perfect starting point. Choose one area, or decide later.")
                    Box(Modifier.fillMaxWidth().height(
                        if(androidx.compose.ui.platform.LocalConfiguration.current.fontScale>=1.6f) 225.dp else 154.dp
                    )
                        .clip(RoundedCornerShape(20.dp))) {
                        Image(painterResource(R.drawable.stillwater_banner),
                            contentDescription = null, contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize())
                        Box(Modifier.fillMaxSize().background(Brush.horizontalGradient(
                            listOf(Forest.copy(alpha = 0.85f), Forest.copy(alpha = 0.10f)))))
                        Column(Modifier.align(Alignment.CenterStart).padding(18.dp)) {
                            Text("A living path", color = Paper,
                                style=EvoluneType.typography.headlineMedium)
                            Text("One meaningful step at a time.", color = Paper,
                                style=EvoluneType.typography.bodySmall,softWrap=true)
                        }
                    }
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        SetupChoice("I'll decide later", area.isBlank()) { area = "" }
                        options.forEach { name ->
                            SetupChoice(name, area == name) { area = name }
                        }
                    }
                }
                1 -> {
                    SetupTitle(1, "Begin with one small practice.",
                        "Choose something specific and repeatable. You can adjust it later.")
                    SetupCard {
                        Text("What will you call your path?", color = Ink,
                            fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        Spacer(Modifier.height(12.dp))
                        OutlinedTextField(title, { title = it.take(121) },
                            label = { Text("Challenge title") },
                            placeholder = { Text("A little more reading") },
                            singleLine = true, modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(16.dp))
                        Text("What would the action look like?", color = Ink,
                            fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        Spacer(Modifier.height(10.dp))
                        OutlinedTextField(action, { action = it.take(501) },
                            label = { Text("My repeatable action") },
                            placeholder = { Text("Read 10 minutes after dinner") },
                            minLines = 2, maxLines = 4, modifier = Modifier.fillMaxWidth())
                    }
                    Text("One difficult day never erases what you've already practiced.",
                        color = Secondary, fontSize = 13.sp, lineHeight = 20.sp)
                }
                2 -> {
                    SetupTitle(2, "Find a rhythm that fits.",
                        "A milestone is a moment to reflect, not proof of permanent habit formation.")
                    SetupCard {
                        Text("What does your milestone count?", color = Ink,
                            fontWeight = FontWeight.SemiBold, fontSize = 16.sp)
                        Spacer(Modifier.height(11.dp))
                        SetupChoice("Calendar days",
                            mode == MilestoneKind.CALENDAR_DAYS.name) {
                            mode = MilestoneKind.CALENDAR_DAYS.name
                        }
                        Spacer(Modifier.height(8.dp))
                        SetupChoice("Completed sessions",
                            mode == MilestoneKind.COMPLETED_SESSIONS.name) {
                            mode = MilestoneKind.COMPLETED_SESSIONS.name
                        }
                        Spacer(Modifier.height(10.dp))
                        OutlinedTextField(target, { target = it.filter(Char::isDigit).take(4) },
                            label = { Text("Milestone number") }, singleLine = true,
                            modifier = Modifier.fillMaxWidth())
                        Spacer(Modifier.height(15.dp))
                        Text("Practice days", color = Ink, fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp)
                        Spacer(Modifier.height(9.dp))
                        listOf("DAILY" to "Every day", "WEEKDAYS" to "Weekdays",
                            "WEEKENDS" to "Weekends").forEachIndexed { index, pair ->
                            if (index > 0) Spacer(Modifier.height(8.dp))
                            SetupChoice(pair.second, cadence == pair.first) { cadence = pair.first }
                        }
                    }
                    SetupCard {
                        TextButton(onClick = { optional = !optional },
                            modifier = Modifier.fillMaxWidth().heightIn(min = 48.dp)) {
                            Text(if (optional) "Hide optional details" else "Add optional details",
                                color = Forest, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
                        }
                        if (optional) {
                            OutlinedTextField(cue, { cue = it.take(5) },
                                label = { Text("Preferred cue time · HH:mm") },
                                placeholder = { Text("19:30") }, singleLine = true,
                                modifier = Modifier.fillMaxWidth())
                            Spacer(Modifier.height(12.dp))
                            OutlinedTextField(fallback, { fallback = it.take(501) },
                                label = { Text("Smaller backup action") },
                                placeholder = { Text("Read one page instead") },
                                modifier = Modifier.fillMaxWidth())
                            Spacer(Modifier.height(10.dp))
                            Text("A cue time does not turn on notifications. Reminders are a separate choice.",
                                color = Secondary, fontSize = 12.sp, lineHeight = 19.sp)
                        }
                    }
                }
                else -> {
                    SetupTitle(3, "Your first path is ready.",
                        "Review your choice. Nothing starts until you save.")
                    SetupCard {
                        Text("YOUR INTENTION", color = Forest, fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold, letterSpacing = 0.8.sp)
                        Spacer(Modifier.height(9.dp))
                        Text(title.trim(), fontFamily = FontFamily.Serif, fontSize = 24.sp,
                            lineHeight = 31.sp, color = Ink)
                        Spacer(Modifier.height(8.dp))
                        Text(action.trim(), color = Secondary, fontSize = 15.sp, lineHeight = 22.sp)
                        HorizontalDivider(Modifier.padding(vertical = 15.dp), color = Line)
                        Text(area.ifBlank { "No growth area selected" }, color = Ink, fontSize = 14.sp)
                        Spacer(Modifier.height(8.dp))
                        Text(target + " " +
                            (if (mode == MilestoneKind.CALENDAR_DAYS.name)
                                "calendar days" else "completed sessions") + " · " +
                            (when (cadence) {
                                "WEEKDAYS" -> "Weekdays"
                                "WEEKENDS" -> "Weekends"
                                else -> "Every day"
                            }), color = Ink, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        if (cue.isNotBlank()) {
                            Spacer(Modifier.height(7.dp))
                            Text("Preferred cue: " + cue.trim(), color = Secondary, fontSize = 13.sp)
                        }
                        if (fallback.isNotBlank()) {
                            Spacer(Modifier.height(7.dp))
                            Text("Backup: " + fallback.trim(), color = Secondary, fontSize = 13.sp)
                        }
                    }
                    SetupCard {
                        Text("Private by design", color = Ink, fontWeight = FontWeight.SemiBold,
                            fontSize = 16.sp)
                        Spacer(Modifier.height(7.dp))
                        Text("Your information stays in this device's private storage. " +
                            "Export or manage it from Journal → Data & privacy.",
                            color = Secondary, fontSize = 13.sp, lineHeight = 20.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("No reminders are enabled during setup.",
                            color = Secondary, fontSize = 12.sp)
                    }
                }
            }
            if (warning.isNotBlank()) Text(warning, color = Color(0xFF905C3E),
                fontSize = 13.sp, lineHeight = 19.sp)
            if (!error.isNullOrBlank()) Text(error, color = Color(0xFF905C3E),
                fontSize = 13.sp, lineHeight = 19.sp)
            Spacer(Modifier.height(4.dp))
        }
        Surface(color = Paper, shadowElevation = 3.dp) {
            Column(Modifier.widthIn(max = 680.dp).fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(onClick = ::next, enabled = !busy,
                    modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Forest, contentColor = Paper)) {
                    Text(when (step) {
                        0 -> "Continue"
                        1 -> "Continue to rhythm"
                        2 -> "Review my path"
                        else -> if (busy) "Saving your path…" else "Begin my path"
                    }, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
                if (step > 0) {
                    TextButton(onClick = { warning = ""; step-- }, enabled = !busy,
                        modifier = Modifier.align(Alignment.CenterHorizontally).heightIn(min = 44.dp)) {
                        Text("Back", color = Secondary, fontSize = 14.sp)
                    }
                } else Text("Everything can be adjusted later.",
                    color = Secondary, fontSize = 12.sp, textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
