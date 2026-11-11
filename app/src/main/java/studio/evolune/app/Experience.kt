package studio.evolune.app

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.animation.core.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import android.provider.Settings
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import studio.evolune.data.*
import studio.evolune.domain.*
import java.time.*
import java.time.format.DateTimeFormatter

private val Ink = Color(0xFF26392D)
private val Sage = Color(0xFF64816B)
private val Forest = Color(0xFF36553E)
private val Pale = Color(0xFFE7ECE2)
private val Cream = Color(0xFFFAF8F3)
private val Paper = Color(0xFFFFFEFA)
private val Muted = Color(0xFF65746C)
private val Line = Color(0xFFE5E2D9)
private val Gold = Color(0xFFB89A63)

private val appColors = lightColorScheme(
    primary = Forest, onPrimary = Color.White, background = Cream, onBackground = Ink,
    surface = Paper, onSurface = Ink, secondary = Gold, surfaceVariant = Color(0xFFEEF1E9)
)

/** Simple original outline icon set; no emoji, default Material purple or external icon dependency. */
@Composable
private fun NavGlyph(route: String, color: Color = Sage) {
    Canvas(Modifier.size(19.dp)) {
        val k = size.width / 24f
        fun line(x1: Float, y1: Float, x2: Float, y2: Float) {
            drawLine(color, Offset(x1*k,y1*k), Offset(x2*k,y2*k), strokeWidth = 1.65f*k, cap = StrokeCap.Round)
        }
        fun outline(vararg xy: Float) {
            val p = Path()
            if (xy.size >= 4) {
                p.moveTo(xy[0]*k,xy[1]*k)
                var i=2
                while (i < xy.size) { p.lineTo(xy[i]*k,xy[i+1]*k); i+=2 }
                drawPath(p, color, style=Stroke(width=1.65f*k, cap=StrokeCap.Round))
            }
        }
        when(route) {
            "today" -> {
                outline(3f,11f,12f,4f,21f,11f)
                outline(5.5f,10f,5.5f,20f,18.5f,20f,18.5f,10f)
                outline(10f,20f,10f,14f,14f,14f,14f,20f)
            }
            "growth" -> {
                line(12f,21f,12f,10f)
                outline(12f,15f,8f,10f,6f,7f)
                outline(12f,13f,16f,8f,19f,6f)
                drawCircle(color, radius=2f*k, center=Offset(7f*k,6.5f*k), style=Stroke(width=1.4f*k))
                drawCircle(color, radius=2f*k, center=Offset(19f*k,5.5f*k), style=Stroke(width=1.4f*k))
            }
            "challenges" -> {
                outline(4f,8f,12f,3.5f,20f,8f,12f,12.5f,4f,8f)
                outline(4f,12.5f,12f,17f,20f,12.5f)
                outline(4f,17f,12f,21.5f,20f,17f)
            }
            "journal" -> {
                outline(4f,20f,8.5f,19f,19f,8.5f,15f,4.5f,4.5f,15f,4f,20f)
                line(12.9f,6.6f,17f,10.7f)
                line(4f,22f,20f,22f)
            }
            else -> {
                outline(3f,19f,8f,13f,12f,16f,20f,6f)
                line(3f,22f,21f,22f)
            }
        }
    }
}

@Composable
fun EvoluneShell(page: String, onPage: (String)->Unit, notice: String?, onDismissNotice: ()->Unit, content: @Composable () -> Unit) {
    val selected = when(page) {
        "detail", "create", "recovery", "review", "reminders" -> "challenges"
        "data" -> "journal"
        else -> page
    }
    val largeText = LocalConfiguration.current.fontScale >= 1.25f
    MaterialTheme(colorScheme = appColors) {
        Scaffold(
            containerColor = Cream,
            bottomBar = {
                Surface(color = Paper, border = BorderStroke(0.5.dp, Line)) {
                    Box(Modifier.fillMaxWidth().navigationBarsPadding(), contentAlignment=Alignment.Center) {
                        Row(Modifier.widthIn(max=760.dp).fillMaxWidth()
                            .height(if (largeText) 96.dp else 73.dp)
                            .padding(horizontal=4.dp, vertical=5.dp),
                            horizontalArrangement = Arrangement.SpaceEvenly) {
                            listOf("today" to "Today", "growth" to "My Growth",
                                "challenges" to "Challenges", "journal" to "Journal",
                                "insights" to "Insights").forEach { (route,label) ->
                                val isActive = selected == route
                                Column(
                                    Modifier.weight(1f).fillMaxHeight()
                                        .semantics { contentDescription = label }
                                        .clickable(role = Role.Tab) { onPage(route) },
                                    horizontalAlignment=Alignment.CenterHorizontally,
                                    verticalArrangement=Arrangement.Center
                                ) {
                                    Box(
                                        Modifier.size(33.dp).clip(RoundedCornerShape(50))
                                            .background(if(isActive) Pale else Color.Transparent),
                                        contentAlignment=Alignment.Center
                                    ) { NavGlyph(route, if(isActive) Forest else Color(0xFF96A299)) }
                                    Text(label, fontSize=10.sp,
                                        lineHeight=if (largeText) 13.sp else 13.sp,
                                        maxLines=if (largeText) 2 else 1,
                                        textAlign=TextAlign.Center,
                                        color=if(isActive) Forest else Muted,
                                        fontWeight=if(isActive) FontWeight.Bold else FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }
        ) { padding ->
            Column(Modifier.fillMaxSize().padding(padding)) {
                Row(
                    Modifier.fillMaxWidth().padding(start=20.dp,end=20.dp,top=12.dp,bottom=12.dp),
                    verticalAlignment=Alignment.CenterVertically
                ) {
                    Text("◒", fontSize=21.sp,color=Sage)
                    Spacer(Modifier.width(8.dp))
                    Text("E V O L U N E", fontSize=11.sp, color=Ink, letterSpacing=1.2.sp,
                        fontWeight=FontWeight.Bold)
                    Spacer(Modifier.weight(1f))
                    Box(Modifier.size(34.dp).clip(RoundedCornerShape(50)).background(Pale)
                        .semantics { contentDescription = "Open My Growth" }
                        .clickable(role = Role.Button) { onPage("growth") },
                        contentAlignment=Alignment.Center) {
                        NavGlyph("growth", Sage)
                    }
                }
                if (notice != null) {
                    Surface(color=Color(0xFFFFEDE1), modifier=Modifier.fillMaxWidth().padding(horizontal=18.dp),
                        shape=RoundedCornerShape(12.dp)) {
                        Row(Modifier.padding(12.dp), verticalAlignment=Alignment.CenterVertically) {
                            Text(notice, color=Ink, modifier=Modifier.weight(1f), fontSize=12.sp)
                            TextButton(onClick=onDismissNotice){Text("Close")}
                        }
                    }
                }
                Box(Modifier.fillMaxSize()) { content() }
            }
        }
    }
}

@Composable
private fun Header(kicker: String, title: String, subtitle: String = "") {
    Column(Modifier.fillMaxWidth().padding(bottom=4.dp)) {
        Text(kicker.uppercase(), fontSize=11.sp, letterSpacing=1.6.sp, color=Color(0xFF829085),
            fontWeight=FontWeight.Bold, lineHeight=13.sp)
        Spacer(Modifier.height(7.dp))
        Text(title, fontFamily=FontFamily.Serif, fontSize=30.sp, lineHeight=32.sp,
            letterSpacing=(-1.15f).sp, color=Ink)
        if (subtitle.isNotEmpty()) {
            Spacer(Modifier.height(6.dp))
            Text(subtitle, color=Muted, fontSize=14.sp, lineHeight=21.sp)
        }
    }
}

@Composable
private fun Panel(content: @Composable ColumnScope.() -> Unit) {
    Surface(color=Paper, shape=RoundedCornerShape(20.dp), border=BorderStroke(1.dp,Line),
        modifier=Modifier.fillMaxWidth()) {
        Column(Modifier.padding(18.dp), content=content)
    }
}

@Composable
private fun MainScroll(content: @Composable ColumnScope.() -> Unit) {
    // A readable editorial column on large screens; unchanged full-width behavior on phones.
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
        Column(
            Modifier.widthIn(max=720.dp).fillMaxWidth().fillMaxHeight()
                .verticalScroll(rememberScrollState())
                .padding(horizontal=20.dp, vertical=10.dp),
            verticalArrangement=Arrangement.spacedBy(16.dp), content=content
        )
    }
}

@Composable
private fun PrimaryAction(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick=onClick, modifier=modifier.heightIn(min=50.dp),
        shape=RoundedCornerShape(11.dp),
        colors=ButtonDefaults.buttonColors(containerColor=Forest, contentColor=Paper)) {
        Text(text, fontSize=14.sp, lineHeight=20.sp, fontWeight=FontWeight.SemiBold)
    }
}

@Composable
private fun SecondaryAction(text: String, onClick: () -> Unit) {
    OutlinedButton(onClick=onClick, modifier=Modifier.heightIn(min=49.dp), shape=RoundedCornerShape(11.dp),
        colors=ButtonDefaults.outlinedButtonColors(containerColor=Color(0xFFF3F6F0), contentColor=Forest),
        border=BorderStroke(1.dp,Color(0xFFDDE5D9))) {
        Text(text, fontSize=14.sp, lineHeight=20.sp, fontWeight=FontWeight.SemiBold)
    }
}

/** Approved Stillwater scene, with the readable editorial overlay used in the HTML baseline. */
@Composable
private fun LandscapeHero(
    label: String = "YOUR GROWTH SPACE",
    overline: String = "STILLWATER × ATELIER",
    headline: String = "Every small step\nbelongs here.",
    artwork: Int = R.drawable.stillwater_banner
) {
    val screenWidth = LocalConfiguration.current.screenWidthDp
    val context = LocalContext.current
    // Respect Android's system-level motion setting. No movement at animator scale 0.
    val motionEnabled = remember(context) {
        Settings.Global.getFloat(context.contentResolver,
            Settings.Global.ANIMATOR_DURATION_SCALE, 1f) > 0f
    }
    val breath = if (motionEnabled) {
        val transition = rememberInfiniteTransition(label="gentle-hero")
        val scale by transition.animateFloat(
            initialValue=1f, targetValue=1.018f,
            animationSpec=infiniteRepeatable(
                tween(durationMillis=8000, easing=FastOutSlowInEasing),
                repeatMode=RepeatMode.Reverse
            ), label="slow-drift"
        )
        scale
    } else 1f
    val heroHeight = when {
        screenWidth >= 700 -> 250.dp
        screenWidth < 350 -> 180.dp
        else -> 205.dp
    }
    Box(Modifier.fillMaxWidth().height(heroHeight).clip(RoundedCornerShape(19.dp))) {
        Image(painterResource(artwork),
            contentDescription=if (artwork==R.drawable.atelier_journal)
                "Atelier garden and open journal with warm afternoon light"
                else "Quiet Stillwater landscape with stone steps, a tree and distant hills",
            contentScale=ContentScale.Crop,
            modifier=Modifier.fillMaxSize().graphicsLayer {
                scaleX = breath
                scaleY = breath
            })
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(
            listOf(Color(0x00112318), Color(0x16112318), Color(0xBD11251A))
        )))
        Text(label,
            modifier=Modifier.align(Alignment.TopEnd).padding(12.dp)
                .clip(RoundedCornerShape(30.dp)).background(Color(0xE8FFFDF6))
                .padding(horizontal=11.dp,vertical=7.dp),
            fontSize=9.sp,color=Forest,fontWeight=FontWeight.Bold)
        Column(Modifier.align(Alignment.BottomStart).padding(start=16.dp,bottom=17.dp)) {
            Text(overline,fontSize=9.sp,color=Paper,
                fontWeight=FontWeight.Bold,letterSpacing=1.3.sp)
            Spacer(Modifier.height(6.dp))
            Text(headline,fontFamily=FontFamily.Serif,
                fontSize=21.sp,lineHeight=23.sp,color=Paper)
        }
    }
}

private fun days(s: ChallengeSnapshot): List<Opportunity> {
    val plan = s.plan
    val today = LocalDate.now(plan.zone)
    val end = maxOf(today, plan.startDate)
    val capped = minOf(end, plan.startDate.plusDays(3650))
    return ChallengeEngine.opportunities(plan, ZonedDateTime.now(plan.zone), capped, s.records, s.pauses)
}

private fun stateLabel(state: OpportunityState) = when(state) {
    OpportunityState.UNREPORTED -> "Not logged"
    OpportunityState.NOT_SCHEDULED -> "Not scheduled"
    OpportunityState.COMPLETED -> "Completed"
    OpportunityState.PARTIAL -> "Partially completed"
    OpportunityState.RESTED -> "Intentionally rested"
    OpportunityState.MISSED -> "Marked missed"
    OpportunityState.COMPLETED_BACKUP -> "Backup action completed"
    OpportunityState.PAUSED -> "Paused"
    OpportunityState.SCHEDULED -> "Scheduled"
}

@OptIn(ExperimentalLayoutApi::class)
@Composable private fun OutcomeButtons(onLog: (PracticeOutcome)->Unit) {
    Text("Record what happened", color=Muted, fontSize=12.sp)
    Row(horizontalArrangement=Arrangement.spacedBy(8.dp), modifier=Modifier.fillMaxWidth()) {
        PrimaryAction("Completed", { onLog(PracticeOutcome.COMPLETED) }, Modifier.weight(1f))
        SecondaryAction("Partly", { onLog(PracticeOutcome.PARTIAL) })
    }
    FlowRow(
        modifier=Modifier.fillMaxWidth(),
        horizontalArrangement=Arrangement.spacedBy(7.dp),
        verticalArrangement=Arrangement.spacedBy(6.dp)
    ) {
        SecondaryAction("Rested", { onLog(PracticeOutcome.RESTED) })
        SecondaryAction("Missed", { onLog(PracticeOutcome.MISSED) })
        SecondaryAction("Backup", { onLog(PracticeOutcome.COMPLETED_BACKUP) })
    }
}

private fun todayStatusText(view: TodayViewState): String = when (view.mode) {
    TodayMode.EMPTY -> "A NEW BEGINNING"
    TodayMode.STARTS_LATER -> "PLANNED"
    TodayMode.SCHEDULED -> "READY WHEN YOU ARE"
    TodayMode.RECORDED -> when (view.outcome) {
        OpportunityState.COMPLETED -> "COMPLETED"
        OpportunityState.COMPLETED_BACKUP -> "BACKUP COMPLETED"
        OpportunityState.PARTIAL -> "PARTLY COMPLETED"
        OpportunityState.RESTED -> "INTENTIONALLY RESTED"
        OpportunityState.MISSED -> "MARKED MISSED"
        else -> "RECORDED"
    }
    TodayMode.REST_DAY -> "NOT SCHEDULED"
    TodayMode.PAUSED -> "PAUSED"
    TodayMode.MILESTONE_ENDED -> "MILESTONE ENDED"
}

@Composable
private fun TodayMainButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Forest, contentColor = Paper)
    ) {
        Text(label, fontSize = 14.sp, lineHeight = 19.sp,
            fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
    }
}

@Composable
private fun TodayQuietButton(label: String, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick, modifier = Modifier.fillMaxWidth().heightIn(min = 49.dp),
        shape = RoundedCornerShape(14.dp),
        colors = ButtonDefaults.outlinedButtonColors(contentColor = Forest),
        border = BorderStroke(1.dp, Color(0xFFC8D6C9))
    ) {
        Text(label, fontSize = 14.sp, lineHeight = 19.sp,
            fontWeight = FontWeight.Medium, textAlign = TextAlign.Center)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun TodayOutcomePicker(onLog: (PracticeOutcome) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        Text("What happened today?", fontFamily = FontFamily.Serif,
            fontSize = 19.sp, color = Ink, lineHeight = 25.sp)
        Text("Choose what actually happened. You can revise this record later.",
            fontSize = 14.sp, lineHeight = 20.sp, color = Muted)
        TodayMainButton("Completed the full practice", { onLog(PracticeOutcome.COMPLETED) })
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            listOf(
                "Did part of it" to PracticeOutcome.PARTIAL,
                "Rested intentionally" to PracticeOutcome.RESTED,
                "Used my backup" to PracticeOutcome.COMPLETED_BACKUP,
                "Mark as missed" to PracticeOutcome.MISSED
            ).forEach { (label, outcome) ->
                OutlinedButton(
                    onClick = { onLog(outcome) },
                    modifier = Modifier.heightIn(min = 49.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(1.dp, Color(0xFFC8D6C9))
                ) {
                    Text(label, color = Forest, fontSize = 13.sp, lineHeight = 19.sp)
                }
            }
        }
    }
}

/** V2 Stage 04. Every label is derived from the saved challenge and practice history. */
@Composable
fun TodayPage(
    s: ChallengeSnapshot?, onCreate: () -> Unit, onOpen: () -> Unit,
    onLog: (PracticeOutcome)->Unit, onAdjust:()->Unit, onJournal:()->Unit
) {
    val view = remember(s) { presentToday(s) }
    var showLogChoices by remember(s?.plan?.id, view.outcome) { mutableStateOf(false) }
    val dateLabel = view.date.format(DateTimeFormatter.ofPattern("EEEE · MMMM d")).uppercase()

    MainScroll {
        Header(dateLabel + " · YOUR DAY", "A gentle day\nto begin again.",
            "One meaningful step. At a pace you can sustain.")

        Panel {
            Text("YOUR NEXT SMALL STEP", fontSize = 11.sp, fontWeight = FontWeight.Bold,
                letterSpacing = 1.1.sp, color = Forest, lineHeight = 16.sp)
            Spacer(Modifier.height(12.dp))
            Surface(
                color = Pale, shape = RoundedCornerShape(11.dp),
                border = BorderStroke(0.7.dp, Color(0xFFDAE3D9))
            ) {
                Text(todayStatusText(view), color = Forest, fontSize = 11.sp,
                    fontWeight = FontWeight.Bold, letterSpacing = 0.4.sp,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
            }
            Spacer(Modifier.height(13.dp))

            when (view.mode) {
                TodayMode.EMPTY -> {
                    Text("Begin with one thing", fontFamily = FontFamily.Serif,
                        fontSize = 25.sp, lineHeight = 30.sp, color = Ink)
                    Spacer(Modifier.height(8.dp))
                    Text("Choose something small and repeatable. There is no required streak or deadline.",
                        fontSize = 15.sp, lineHeight = 22.sp, color = Muted)
                    Spacer(Modifier.height(18.dp))
                    TodayMainButton("Create your first challenge", onCreate)
                }
                else -> {
                    Text(view.challengeTitle.orEmpty(), fontSize = 13.sp,
                        fontWeight = FontWeight.Medium, color = Muted, lineHeight = 19.sp)
                    Spacer(Modifier.height(6.dp))
                    val focus = when (view.mode) {
                        TodayMode.SCHEDULED, TodayMode.RECORDED ->
                            view.action ?: "Your planned practice"
                        TodayMode.STARTS_LATER -> "A path to look forward to"
                        TodayMode.REST_DAY -> "Space between practices"
                        TodayMode.PAUSED -> "Today is intentionally paused"
                        TodayMode.MILESTONE_ENDED -> "This chapter has reached its milestone"
                        TodayMode.EMPTY -> ""
                    }
                    Text(focus, fontFamily = FontFamily.Serif, fontSize = 24.sp,
                        lineHeight = 29.sp, color = Ink)
                    Spacer(Modifier.height(9.dp))
                    Text(view.milestone.orEmpty(), fontSize = 13.sp,
                        color = Forest, fontWeight = FontWeight.SemiBold, lineHeight = 19.sp)
                    if (view.cueTime != null && view.mode == TodayMode.SCHEDULED) {
                        Spacer(Modifier.height(4.dp))
                        Text("Planned cue: " + view.cueTime.toString() +
                            " · This does not mean notifications are enabled",
                            fontSize = 12.sp, lineHeight = 18.sp, color = Muted)
                    }
                    Spacer(Modifier.height(13.dp))
                    when (view.mode) {
                        TodayMode.SCHEDULED -> {
                            Text("Scheduled for today. Nothing has been recorded yet.",
                                fontSize = 14.sp, lineHeight = 21.sp, color = Muted)
                            Spacer(Modifier.height(12.dp))
                            TodayMainButton("Record today's practice", { showLogChoices = !showLogChoices })
                            Spacer(Modifier.height(8.dp))
                            TodayQuietButton("Adjust future plan", onAdjust)
                        }
                        TodayMode.RECORDED -> {
                            val message = when (view.outcome) {
                                OpportunityState.COMPLETED -> "You recorded the full practice today."
                                OpportunityState.COMPLETED_BACKUP -> "You recorded your smaller backup action."
                                OpportunityState.PARTIAL -> "You recorded part of the practice today."
                                OpportunityState.RESTED -> "You chose intentional rest today."
                                OpportunityState.MISSED -> "You explicitly marked this practice as missed."
                                else -> "Today's practice was recorded."
                            }
                            Text(message + " Your earlier record remains in the history if you update it.",
                                fontSize = 14.sp, lineHeight = 21.sp, color = Muted)
                            Spacer(Modifier.height(12.dp))
                            TodayMainButton("Update today's record", { showLogChoices = !showLogChoices })
                            Spacer(Modifier.height(8.dp))
                            TodayQuietButton("View challenge details", onOpen)
                        }
                        TodayMode.REST_DAY -> {
                            Text("There is no practice on today's schedule. A day without a planned action is not a missed day.",
                                fontSize = 14.sp, lineHeight = 21.sp, color = Muted)
                            Spacer(Modifier.height(12.dp))
                            TodayQuietButton("View your path", onOpen)
                        }
                        TodayMode.PAUSED -> {
                            Text("Your plan is paused today. Existing records have not changed.",
                                fontSize = 14.sp, lineHeight = 21.sp, color = Muted)
                            Spacer(Modifier.height(12.dp))
                            TodayQuietButton("Adjust future plan", onAdjust)
                        }
                        TodayMode.MILESTONE_ENDED -> {
                            Text("The calendar period has ended. Your practice history is preserved; no new entry is due today.",
                                fontSize = 14.sp, lineHeight = 21.sp, color = Muted)
                            Spacer(Modifier.height(12.dp))
                            TodayQuietButton("View milestone and history", onOpen)
                        }
                        TodayMode.STARTS_LATER -> {
                            Text("This practice begins on " + view.startDate.toString() +
                                ". There is nothing to log before it starts.",
                                fontSize = 14.sp, lineHeight = 21.sp, color = Muted)
                            Spacer(Modifier.height(12.dp))
                            TodayQuietButton("View planned challenge", onOpen)
                        }
                        TodayMode.EMPTY -> Unit
                    }

                    if (showLogChoices &&
                        (view.mode == TodayMode.SCHEDULED || view.mode == TodayMode.RECORDED)) {
                        Spacer(Modifier.height(16.dp))
                        Divider(color = Line)
                        Spacer(Modifier.height(16.dp))
                        TodayOutcomePicker { outcome ->
                            showLogChoices = false
                            onLog(outcome)
                        }
                    }
                }
            }
        }

        // Artwork supports the practice; it no longer pushes the primary action
        // beneath a large header and the first viewport.
        LandscapeHero(label = "A SPACE TO RETURN TO",
            overline = "STILLWATER × ATELIER",
            headline = "A quiet place\nfor real progress.")

        if (s != null && view.nextScheduled != null) {
            Panel {
                Text("COMING UP", color = Forest, fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp, fontSize = 11.sp)
                Spacer(Modifier.height(7.dp))
                Text(view.nextScheduled.format(DateTimeFormatter.ofPattern("EEEE · MMM d")),
                    fontFamily = FontFamily.Serif, color = Ink,
                    fontSize = 20.sp, lineHeight = 25.sp)
                Spacer(Modifier.height(5.dp))
                Text(s.plan.scheduleOn(view.nextScheduled).action, fontSize = 14.sp,
                    lineHeight = 21.sp, color = Muted)
            }
        }

        if (s != null && view.unreportedEarlier > 0) {
            Panel {
                Text("Some days are still unreported", fontFamily = FontFamily.Serif,
                    fontSize = 20.sp, lineHeight = 25.sp, color = Ink)
                Spacer(Modifier.height(7.dp))
                val count = view.unreportedEarlier
                Text(count.toString() + if (count == 1)
                    " earlier scheduled day has no entry. Unreported never means missed."
                    else " earlier scheduled days have no entry. Unreported never means missed.",
                    fontSize = 14.sp, lineHeight = 21.sp, color = Muted)
                Spacer(Modifier.height(11.dp))
                TodayQuietButton("View practice history", onOpen)
            }
        }

        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp))
                .background(Color(0xFFEAF0E7))
                .clickable(role = Role.Button, onClick = onJournal)
                .heightIn(min = 70.dp).padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(Modifier.size(43.dp).clip(RoundedCornerShape(13.dp))
                .background(Color(0xFFD9E5D7)), contentAlignment = Alignment.Center) {
                NavGlyph("journal", Forest)
            }
            Column(Modifier.weight(1f)) {
                Text("A moment to reflect", color = Ink, fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp, lineHeight = 21.sp)
                Text("Optional, private, and always yours", color = Muted,
                    fontSize = 13.sp, lineHeight = 19.sp)
            }
            Text("Open", color = Forest, fontSize = 12.sp, fontWeight = FontWeight.Medium)
        }
        Text("Your entries stay on this device unless you explicitly export them. No cloud sync is enabled.",
            fontSize = 12.sp, lineHeight = 18.sp, color = Muted)
    }
}

@Composable
fun ChallengeListPage(challenges: List<ChallengeEntity>, chosen: String?, onSelect: (String)->Unit, onCreate:()->Unit) {
    MainScroll {
        Header("Challenge studio", "The changes you choose.",
            "A home for your practices, not a leaderboard.")
        Panel {
            Text("YOUR PRACTICES", fontSize=11.sp, fontWeight=FontWeight.Bold,
                color=Forest, letterSpacing=1.1.sp)
            Spacer(Modifier.height(8.dp))
            Text(if(challenges.isEmpty()) "A place to begin"
                 else "${challenges.size} " + if(challenges.size==1) "personal path" else "personal paths",
                fontFamily=FontFamily.Serif, fontSize=25.sp, lineHeight=31.sp, color=Ink)
            Spacer(Modifier.height(7.dp))
            Text("Choose a small action, make it yours, and adjust the plan as life changes.",
                fontSize=14.sp,lineHeight=21.sp,color=Muted)
            Spacer(Modifier.height(16.dp))
            TodayMainButton("New challenge",onCreate)
        }
        if(challenges.isEmpty()) {
            Panel {
                Text("Nothing scheduled yet",fontFamily=FontFamily.Serif,
                    fontSize=21.sp,lineHeight=27.sp,color=Ink)
                Spacer(Modifier.height(6.dp))
                Text("There's nothing to catch up on. Your first practice starts whenever you choose.",
                    fontSize=14.sp,lineHeight=21.sp,color=Muted)
            }
        } else {
            Text("YOUR CHALLENGE LIBRARY",fontSize=11.sp,color=Forest,
                fontWeight=FontWeight.Bold,letterSpacing=1.sp)
            challenges.forEach { ch ->
                Panel {
                    if(ch.id==chosen) {
                        Text("CURRENTLY SELECTED",fontSize=11.sp,
                            color=Forest,fontWeight=FontWeight.Bold,letterSpacing=1.sp)
                        Spacer(Modifier.height(7.dp))
                    }
                    Text(ch.title, fontSize=22.sp,lineHeight=28.sp,
                        fontFamily=FontFamily.Serif,color=Ink)
                    Spacer(Modifier.height(8.dp))
                    Text("${ch.target} "+
                        (if(ch.milestoneKind==MilestoneKind.CALENDAR_DAYS.name)
                            "calendar days" else "full recorded sessions"),
                        fontSize=14.sp,lineHeight=21.sp,color=Forest,
                        fontWeight=FontWeight.SemiBold)
                    Text("Started ${ch.startDate}",fontSize=13.sp,
                        lineHeight=19.sp,color=Muted)
                    Spacer(Modifier.height(14.dp))
                    TodayQuietButton(if(ch.id==chosen)"View active challenge" else "Open challenge",
                        {onSelect(ch.id)})
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ChallengeDetailPage(
    s: ChallengeSnapshot?, onAdjust:()->Unit, onReview:()->Unit,
    onBack:()->Unit, onLog:(PracticeOutcome)->Unit,
    onReminders:()->Unit
) {
    if(s==null) {
        MainScroll {
            Header("Your practice","Choose your path.",
                "Select a challenge to see its details.")
            TodayQuietButton("Back to challenges",onBack)
        }
        return
    }
    val plan=s.plan
    val list=days(s)
    val sum=ChallengeEngine.summarize(list)
    val view=presentToday(s)
    val previewThrough=plan.startDate.plusDays(6)
    val preview=ChallengeEngine.opportunities(
        plan,ZonedDateTime.now(plan.zone),previewThrough,s.records,s.pauses)
    MainScroll {
        Header("Your practice",plan.title,
            "${plan.target} "+
            (if(plan.milestoneKind==MilestoneKind.CALENDAR_DAYS) "calendar days" else "completed sessions")+
            " · from "+plan.startDate.toString())

        Panel {
            Text("TODAY ON THIS PATH",fontSize=11.sp,color=Forest,
                letterSpacing=1.sp,fontWeight=FontWeight.Bold)
            Spacer(Modifier.height(9.dp))
            Text(todayStatusText(view),fontSize=12.sp,
                color=Forest,fontWeight=FontWeight.SemiBold)
            Spacer(Modifier.height(7.dp))
            Text(when(view.mode) {
                TodayMode.SCHEDULED,TodayMode.RECORDED ->
                    view.action ?: "Your planned practice"
                TodayMode.STARTS_LATER -> "Your practice begins soon"
                TodayMode.REST_DAY -> "No practice scheduled today"
                TodayMode.PAUSED -> "Paused for today"
                TodayMode.MILESTONE_ENDED -> "Calendar milestone complete"
                TodayMode.EMPTY -> "Choose a small action"
            },fontSize=22.sp,lineHeight=28.sp,fontFamily=FontFamily.Serif,color=Ink)
            Spacer(Modifier.height(6.dp))
            Text(view.milestone.orEmpty(),fontSize=14.sp,lineHeight=21.sp,color=Muted)
            if(view.mode==TodayMode.SCHEDULED || view.mode==TodayMode.RECORDED) {
                Spacer(Modifier.height(15.dp))
                Text("Your record is editable without erasing its revision history.",
                    fontSize=13.sp,lineHeight=19.sp,color=Muted)
                Spacer(Modifier.height(11.dp))
                OutcomeButtons(onLog)
            }
        }

        LandscapeHero(label="YOUR CURRENT PATH",overline="STILLWATER × ATELIER",
            headline="A little progress\nis still progress.")

        Text("YOUR RECORD SO FAR",fontSize=11.sp,color=Forest,
            fontWeight=FontWeight.Bold,letterSpacing=1.sp)
        FlowRow(
            modifier=Modifier.fillMaxWidth(),
            horizontalArrangement=Arrangement.spacedBy(8.dp),
            verticalArrangement=Arrangement.spacedBy(8.dp)
        ) {
            listOf("Full records" to sum.recordedFull,
                "Partial" to sum.partial,
                "Unreported" to sum.unreported,
                "Intentional rest" to sum.rested).forEach { (label,count) ->
                Surface(color=Pale,shape=RoundedCornerShape(15.dp),
                    modifier=Modifier.widthIn(min=130.dp)) {
                    Column(Modifier.padding(14.dp)) {
                        Text(count.toString(),fontSize=29.sp,
                            fontFamily=FontFamily.Serif,color=Ink,lineHeight=34.sp)
                        Spacer(Modifier.height(5.dp))
                        Text(label,fontSize=13.sp,lineHeight=19.sp,color=Forest)
                    }
                }
            }
        }
        Panel {
            Text("Your first seven days",fontFamily=FontFamily.Serif,
                fontSize=21.sp,color=Ink,lineHeight=26.sp)
            Spacer(Modifier.height(7.dp))
            Text("Each day has its own state. Unreported is never assumed missed.",
                fontSize=13.sp,lineHeight=19.sp,color=Muted)
            Spacer(Modifier.height(15.dp))
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(5.dp)) {
                preview.forEach { day ->
                    val tone=when(day.state) {
                        OpportunityState.COMPLETED -> Color(0xFFD2E4D3)
                        OpportunityState.PARTIAL,OpportunityState.COMPLETED_BACKUP -> Color(0xFFF0E5C9)
                        OpportunityState.PAUSED -> Color(0xFFE7E6E1)
                        OpportunityState.RESTED -> Color(0xFFDDEAE8)
                        OpportunityState.MISSED -> Color(0xFFEADDD5)
                        else -> Color(0xFFF0F3ED)
                    }
                    Surface(color=tone,shape=RoundedCornerShape(11.dp),
                        modifier=Modifier.weight(1f)
                            .semantics { contentDescription =
                                "${day.date}: ${stateLabel(day.state)}" }) {
                        Column(Modifier.heightIn(min=55.dp).padding(vertical=8.dp),
                            horizontalAlignment=Alignment.CenterHorizontally,
                            verticalArrangement=Arrangement.Center) {
                            Text(day.date.dayOfMonth.toString(),
                                fontSize=13.sp,color=Forest,fontWeight=FontWeight.SemiBold)
                            Text(when(day.state) {
                                OpportunityState.COMPLETED -> "✓"
                                OpportunityState.MISSED -> "×"
                                else -> "·"
                            },fontSize=13.sp,color=Forest)
                        }
                    }
                }
            }
            Spacer(Modifier.height(11.dp))
            Text("Calendar dates are shown above; the full state names appear in history.",
                fontSize=12.sp,lineHeight=18.sp,color=Muted)
        }
        Panel {
            Text("Adjust, reflect or set reminders",fontFamily=FontFamily.Serif,
                fontSize=21.sp,lineHeight=27.sp,color=Ink)
            Spacer(Modifier.height(8.dp))
            Text("You stay in control of your pace and notifications.",
                color=Muted,fontSize=14.sp,lineHeight=20.sp)
            Spacer(Modifier.height(15.dp))
            TodayMainButton("Adjust plan",onAdjust)
            Spacer(Modifier.height(8.dp))
            TodayQuietButton("Milestone review",onReview)
            Spacer(Modifier.height(8.dp))
            TodayQuietButton("Gentle reminders",onReminders)
        }
        Panel {
            Text("Recent opportunities",fontSize=21.sp,fontFamily=FontFamily.Serif,
                color=Ink,lineHeight=26.sp)
            Spacer(Modifier.height(12.dp))
            if(list.isEmpty()) Text("Your first practice hasn't started yet.",
                fontSize=14.sp,color=Muted)
            list.takeLast(7).reversed().forEach { day ->
                Column(Modifier.fillMaxWidth().padding(vertical=8.dp)) {
                    Text(day.date.format(DateTimeFormatter.ofPattern("EEE d MMM")),
                        fontSize=13.sp,color=Muted)
                    Text(stateLabel(day.state),fontSize=14.sp,lineHeight=21.sp,
                        fontWeight=FontWeight.SemiBold,color=Forest)
                }
                HorizontalDivider(color=Line)
            }
        }
        TodayQuietButton("Back to challenges",onBack)
    }
}

data class CreateRequest(
    val title: String, val action: String, val target: Int, val kind: MilestoneKind,
    val recurrence: Recurrence, val cueTime: LocalTime?, val fallback: String?, val areaId: String?
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CreateChallengePage(areas: List<GrowthAreaEntity>, onBack:()->Unit, onCreate:(CreateRequest)->Unit) {
    var title by remember { mutableStateOf("") }
    var action by remember { mutableStateOf("") }
    var targetText by remember { mutableStateOf("21") }
    var mode by remember { mutableStateOf(MilestoneKind.CALENDAR_DAYS) }
    var recurrence by remember { mutableStateOf<Recurrence>(Recurrence.Daily) }
    var cue by remember { mutableStateOf("") }
    var fallback by remember { mutableStateOf("") }
    var areaId by remember { mutableStateOf<String?>(null) }
    var showOptional by remember { mutableStateOf(false) }
    var warning by remember { mutableStateOf("") }
    MainScroll {
        Header("A fresh beginning", "Start with one thing.",
            "Choose a repeatable practice. You can adjust future plans later.")
        Panel {
            Text("01 · YOUR PRACTICE",fontSize=11.sp,fontWeight=FontWeight.Bold,
                letterSpacing=1.sp,color=Forest)
            Spacer(Modifier.height(10.dp))
            Text("Begin with an action",fontSize=23.sp,lineHeight=29.sp,
                fontFamily=FontFamily.Serif,color=Ink)
            Spacer(Modifier.height(8.dp))
            Text("Small and specific works better than vague pressure.",
                fontSize=14.sp,lineHeight=21.sp,color=Muted)
            Spacer(Modifier.height(15.dp))
            OutlinedTextField(value=title,onValueChange={title=it},
                label={Text("Challenge title")},singleLine=true,
                modifier=Modifier.fillMaxWidth())
            Spacer(Modifier.height(13.dp))
            OutlinedTextField(value=action,onValueChange={action=it},
                label={Text("My repeatable action")},
                placeholder={Text("Read for 10 minutes")},
                minLines=2,maxLines=4,modifier=Modifier.fillMaxWidth())
        }
        Panel {
            Text("02 · YOUR RHYTHM",fontSize=11.sp,fontWeight=FontWeight.Bold,
                letterSpacing=1.sp,color=Forest)
            Spacer(Modifier.height(9.dp))
            Text("Choose how to count",fontSize=22.sp,lineHeight=28.sp,
                fontFamily=FontFamily.Serif,color=Ink)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value=targetText,
                onValueChange={targetText=it.filter(Char::isDigit)},
                label={Text("Milestone number")},singleLine=true,
                modifier=Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))
            Text("Milestone means",fontSize=14.sp,color=Ink)
            FlowRow(horizontalArrangement=Arrangement.spacedBy(8.dp),
                verticalArrangement=Arrangement.spacedBy(6.dp)) {
                FilterChip(selected=mode==MilestoneKind.CALENDAR_DAYS,
                    onClick={mode=MilestoneKind.CALENDAR_DAYS},label={Text("Calendar days")})
                FilterChip(selected=mode==MilestoneKind.COMPLETED_SESSIONS,
                    onClick={mode=MilestoneKind.COMPLETED_SESSIONS},label={Text("Full sessions")})
            }
            Spacer(Modifier.height(8.dp))
            Text("A 21-day period does not prove a permanent habit has formed.",
                fontSize=13.sp,lineHeight=20.sp,color=Muted)
            Spacer(Modifier.height(13.dp))
            Text("Practice days",fontSize=14.sp,color=Forest,
                fontWeight=FontWeight.SemiBold)
            listOf("Every day" to Recurrence.Daily,
                "Weekdays" to Recurrence.Weekdays,
                "Weekends" to Recurrence.Weekends).forEach { (name,r) ->
                Row(Modifier.fillMaxWidth().heightIn(min=50.dp)
                    .clickable(role=Role.RadioButton){recurrence=r},
                    verticalAlignment=Alignment.CenterVertically) {
                    RadioButton(selected=recurrence==r,onClick={recurrence=r})
                    Text(name,fontSize=15.sp,lineHeight=22.sp,color=Ink)
                }
            }
        }
        Panel {
            Text("03 · PERSONAL DETAILS",fontSize=11.sp,color=Forest,
                fontWeight=FontWeight.Bold,letterSpacing=1.sp)
            Spacer(Modifier.height(8.dp))
            Text("Keep it your own",fontSize=22.sp,lineHeight=28.sp,
                fontFamily=FontFamily.Serif,color=Ink)
            Spacer(Modifier.height(10.dp))
            if(areas.isNotEmpty()) {
                Text("Growth area (optional)",fontSize=14.sp,color=Muted)
                FlowRow(horizontalArrangement=Arrangement.spacedBy(7.dp),
                    verticalArrangement=Arrangement.spacedBy(5.dp)) {
                    FilterChip(selected=areaId==null,onClick={areaId=null},
                        label={Text("None")})
                    areas.forEach { area ->
                        FilterChip(selected=areaId==area.id,
                            onClick={areaId=area.id},label={Text(area.name)})
                    }
                }
                Spacer(Modifier.height(10.dp))
            }
            TodayQuietButton(if(showOptional)"Hide optional details" else "Add optional details",{
                showOptional=!showOptional
            })
            if(showOptional) {
                Spacer(Modifier.height(14.dp))
                OutlinedTextField(value=cue,onValueChange={cue=it},
                    label={Text("Optional time (HH:mm)")},
                    singleLine=true,modifier=Modifier.fillMaxWidth())
                Spacer(Modifier.height(11.dp))
                OutlinedTextField(value=fallback,onValueChange={fallback=it},
                    label={Text("Smaller backup action (optional)")},
                    modifier=Modifier.fillMaxWidth())
                Spacer(Modifier.height(8.dp))
                Text("A cue time does not enable notifications. Reminders stay opt-in.",
                    fontSize=13.sp,lineHeight=20.sp,color=Muted)
            }
        }
        if(warning.isNotEmpty()) Text(warning,color=Color(0xFF905C3E),
            fontSize=14.sp,lineHeight=21.sp)
        TodayMainButton("Create my challenge", {
            val count=targetText.toIntOrNull()
            val time=if(cue.isBlank()) null else runCatching { LocalTime.parse(cue.trim()) }.getOrNull()
            warning = when {
                title.isBlank() || action.isBlank() -> "Enter a title and one repeatable action."
                count == null || count !in 1..3650 -> "Choose a valid milestone number from 1 to 3650."
                cue.isNotBlank() && time == null -> "Use a valid time such as 07:30."
                else -> ""
            }
            if(warning.isEmpty()) onCreate(CreateRequest(
                title.trim(),action.trim(),count!!,mode,recurrence,time,
                fallback.trim().ifEmpty{null},areaId
            ))
        })
        TodayQuietButton("Back",onBack)
    }
}

@Composable
fun RecoveryPage(s: ChallengeSnapshot?, onBack:()->Unit, onRevise:(String,Recurrence)->Unit, onPause:()->Unit) {
    if(s==null) {
        MainScroll {
            Header("Your plan","No challenge selected.","Choose a challenge to adjust its future schedule.")
            TodayQuietButton("Back without changes",onBack)
        }
        return
    }
    val latest=s.plan.schedules.last()
    var action by remember(s.plan.id,latest.effectiveFrom) { mutableStateOf(latest.action) }
    var recurrence by remember(s.plan.id,latest.effectiveFrom) { mutableStateOf(latest.recurrence) }
    val ended = presentToday(s).mode == TodayMode.MILESTONE_ENDED
    MainScroll {
        Header("A gentle restart", "Adjust the path.",
            "Keep what you have learned. Change what comes next.")
        Panel {
            Text("YOUR CURRENT PRACTICE",fontSize=11.sp,letterSpacing=1.sp,
                color=Forest,fontWeight=FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(s.plan.title,fontFamily=FontFamily.Serif,
                fontSize=23.sp,lineHeight=29.sp,color=Ink)
            Spacer(Modifier.height(8.dp))
            Text("Edits take effect on the next scheduled day, not in the history you already recorded.",
                fontSize=14.sp,lineHeight=21.sp,color=Muted)
        }
        Panel {
            Text("A sustainable next step",fontFamily=FontFamily.Serif,
                fontSize=23.sp,lineHeight=29.sp,color=Ink)
            Spacer(Modifier.height(8.dp))
            Text("Choose a practice that feels realistic for your next chapter.",
                fontSize=14.sp,lineHeight=21.sp,color=Muted)
            Spacer(Modifier.height(15.dp))
            OutlinedTextField(value=action,onValueChange={ action=it },
                label={Text("Next action (from tomorrow)")},
                minLines=2,maxLines=4,modifier=Modifier.fillMaxWidth())
            Spacer(Modifier.height(16.dp))
            Text("Practice days",color=Forest,fontSize=14.sp,
                fontWeight=FontWeight.SemiBold)
            listOf("Daily" to Recurrence.Daily,
                "Weekdays" to Recurrence.Weekdays,
                "Weekends" to Recurrence.Weekends).forEach { (label,r) ->
                Row(
                    Modifier.fillMaxWidth().heightIn(min=51.dp)
                        .clickable(role=Role.RadioButton){recurrence=r},
                    verticalAlignment=Alignment.CenterVertically
                ) {
                    RadioButton(selected=recurrence==r,onClick={recurrence=r})
                    Text(label,color=Ink,fontSize=15.sp,lineHeight=22.sp)
                }
            }
            Spacer(Modifier.height(12.dp))
            if(action.isBlank()) {
                Text("Enter one specific next action before saving.",
                    color=Color(0xFF905C3E),fontSize=13.sp,lineHeight=19.sp)
                Spacer(Modifier.height(8.dp))
            }
            if(ended) {
                Text("This calendar milestone has ended. Review its history before starting a new challenge.",
                    color=Muted,fontSize=14.sp,lineHeight=21.sp)
            } else {
                TodayMainButton("Save future plan", {
                    if(action.isNotBlank()) onRevise(action.trim(),recurrence)
                })
            }
        }
        if(!ended) {
            Panel {
                Text("Need a little space?",fontFamily=FontFamily.Serif,
                    fontSize=22.sp,lineHeight=28.sp,color=Ink)
                Spacer(Modifier.height(8.dp))
                Text("Pause the next three days. It won't erase your history or count these days as completed.",
                    color=Muted,fontSize=14.sp,lineHeight=21.sp)
                Spacer(Modifier.height(14.dp))
                TodayQuietButton("Pause next three days",onPause)
            }
        }
        LandscapeHero(label="YOUR OWN RHYTHM",
            headline="Rest belongs\non the path.")
        TodayQuietButton("Back without changes",onBack)
    }
}

@Composable
fun ReviewPage(s: ChallengeSnapshot?, onBack:()->Unit) {
    if(s==null) {
        MainScroll {
            Header("Your reflection","No challenge selected.","Choose a challenge to see your recorded history.")
            TodayQuietButton("Back to challenge",onBack)
        }
        return
    }
    val summary=ChallengeEngine.summarize(days(s))
    val plan=s.plan
    MainScroll {
        Header("Your reflection","Every phase has meaning.",
            "A factual record of this journey so far, never a judgment.")
        Panel {
            Text("YOUR PATH",fontSize=11.sp,fontWeight=FontWeight.Bold,
                letterSpacing=1.sp,color=Forest)
            Spacer(Modifier.height(10.dp))
            Text(plan.title,fontFamily=FontFamily.Serif,
                fontSize=25.sp,lineHeight=31.sp,color=Ink)
            Spacer(Modifier.height(7.dp))
            Text("${plan.target} " +
                (if(plan.milestoneKind==MilestoneKind.CALENDAR_DAYS)
                    "calendar-day milestone" else "full-session milestone")+
                " · started ${plan.startDate}",fontSize=14.sp,
                lineHeight=21.sp,color=Muted)
            Spacer(Modifier.height(18.dp))
            Surface(color=Pale,shape=RoundedCornerShape(15.dp),
                modifier=Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text(summary.recordedFull.toString(),fontFamily=FontFamily.Serif,
                        fontSize=43.sp,lineHeight=48.sp,color=Forest)
                    Text("Full practices explicitly recorded",fontSize=14.sp,
                        lineHeight=21.sp,color=Ink)
                    Text("Backup actions are counted separately, not as full sessions.",
                        fontSize=12.sp,lineHeight=18.sp,color=Muted)
                }
            }
        }
        Panel {
            Text("The full picture",fontFamily=FontFamily.Serif,
                fontSize=22.sp,lineHeight=27.sp,color=Ink)
            Spacer(Modifier.height(11.dp))
            listOf(
                "Full practices" to summary.recordedFull,
                "Partial practices" to summary.partial,
                "Backup practices" to summary.recordedBackup,
                "Intentional rest" to summary.rested,
                "Explicitly marked missed" to summary.explicitlyMissed,
                "Unreported" to summary.unreported,
                "Paused opportunities" to summary.paused,
                "Eligible opportunities through today" to summary.eligibleOpportunities
            ).forEachIndexed { index, (label,value) ->
                Row(Modifier.fillMaxWidth().padding(vertical=10.dp),
                    verticalAlignment=Alignment.CenterVertically) {
                    Text(label,modifier=Modifier.weight(1f),fontSize=14.sp,
                        lineHeight=21.sp,color=Ink)
                    Spacer(Modifier.width(12.dp))
                    Text(value.toString(),fontFamily=FontFamily.Serif,
                        fontSize=22.sp,color=Forest)
                }
                if(index<7) HorizontalDivider(color=Line)
            }
        }
        Panel {
            Text("Unreported isn't missed",fontFamily=FontFamily.Serif,
                fontSize=21.sp,color=Ink)
            Spacer(Modifier.height(8.dp))
            Text("A day with no entry remains unknown until you decide to record it. Nothing is inferred about your effort.",
                fontSize=14.sp,lineHeight=21.sp,color=Muted)
            Spacer(Modifier.height(13.dp))
            Text("A milestone is a point for reflection—not proof that a permanent habit has formed.",
                fontSize=14.sp,lineHeight=21.sp,color=Ink)
        }
        TodayQuietButton("Back to challenge",onBack)
    }
}

@Composable
fun GrowthPage(areas:List<GrowthAreaEntity>,onCreateArea:(String)->Unit,onStart:()->Unit) {
    var title by remember { mutableStateOf("") }
    MainScroll {
        Header("My growth", "Your direction, your pace.",
            "Choose the parts of life that feel meaningful to you.")
        Panel {
            Text("YOUR GROWTH SPACES",fontSize=11.sp,color=Forest,
                letterSpacing=1.sp,fontWeight=FontWeight.Bold)
            Spacer(Modifier.height(8.dp))
            Text(if(areas.isEmpty()) "A place to explore"
                else "${areas.size} " + if(areas.size==1)"area of growth" else "areas of growth",
                fontFamily=FontFamily.Serif,fontSize=24.sp,lineHeight=30.sp,color=Ink)
            Spacer(Modifier.height(7.dp))
            Text("Name an area that matters to you. These are personal labels, not ratings or achievement scores.",
                fontSize=14.sp,lineHeight=21.sp,color=Muted)
            Spacer(Modifier.height(14.dp))
            OutlinedTextField(value=title,onValueChange={title=it},
                label={Text("Area name")},singleLine=true,
                modifier=Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))
            TodayMainButton("Add area",{
                if(title.isNotBlank()){onCreateArea(title.trim());title=""}
            })
        }
        if(areas.isEmpty()) {
            Panel {
                Text("Not sure where to start?",fontFamily=FontFamily.Serif,
                    fontSize=21.sp,lineHeight=27.sp,color=Ink)
                Spacer(Modifier.height(7.dp))
                Text("A few ideas: learning, body and energy, relationships, or everyday foundations. You can make your own.",
                    color=Muted,fontSize=14.sp,lineHeight=21.sp)
            }
        } else {
            Text("YOUR CHOSEN AREAS",fontSize=11.sp,color=Forest,
                fontWeight=FontWeight.Bold,letterSpacing=1.sp)
            areas.forEach { area ->
                Panel {
                    Text(area.name,fontFamily=FontFamily.Serif,
                        fontSize=22.sp,lineHeight=28.sp,color=Ink)
                    Spacer(Modifier.height(8.dp))
                    Text("Your challenges can follow this direction, but an area doesn't require daily action.",
                        color=Muted,fontSize=14.sp,lineHeight=21.sp)
                    Spacer(Modifier.height(12.dp))
                    TodayQuietButton("Create a challenge",onStart)
                }
            }
        }
        LandscapeHero(label="YOUR OWN DIRECTION",
            headline="Growth is not\na straight line.")
    }
}

@Composable
fun JournalPage(
    entries: List<JournalEntryEntity>,
    onSave:(JournalEntryEntity?,String,String?,Int?)->Unit,
    onDelete:(JournalEntryEntity)->Unit,
    onDataPrivacy:()->Unit
) {
    var editing by remember { mutableStateOf<JournalEntryEntity?>(null) }
    var showingEditor by remember { mutableStateOf(false) }
    var body by remember { mutableStateOf("") }
    var mood by remember { mutableStateOf<String?>(null) }
    var energy by remember { mutableStateOf<Int?>(null) }
    var pendingDelete by remember { mutableStateOf<JournalEntryEntity?>(null) }
    fun clear() { editing=null;body="";mood=null;energy=null;showingEditor=false }
    val deletion = pendingDelete
    if(deletion!=null) {
        AlertDialog(
            onDismissRequest={pendingDelete=null},
            title={Text("Delete this reflection?",color=Ink)},
            text={Text("This entry will be removed from this device. This cannot be undone. Other reflections are unchanged.",
                color=Muted,fontSize=14.sp,lineHeight=21.sp)},
            confirmButton={
                TextButton(onClick={
                    onDelete(deletion)
                    if(editing?.id==deletion.id) clear()
                    pendingDelete=null
                }) { Text("Delete reflection",color=Color(0xFF995646)) }
            },
            dismissButton={
                TextButton(onClick={pendingDelete=null}) { Text("Keep reflection",color=Forest) }
            },
            containerColor=Paper,shape=RoundedCornerShape(20.dp)
        )
    }
    MainScroll {
        Header("Private reflections · On this device", "Room to feel.\nSpace to think.",
            "Write as much or as little as you like. No mood check-in is ever required.")
        Panel {
            Text("YOUR PRIVATE PAGE",fontSize=11.sp,color=Forest,
                fontWeight=FontWeight.Bold,letterSpacing=1.sp)
            Spacer(Modifier.height(8.dp))
            Text("Write when you want to.",fontFamily=FontFamily.Serif,
                fontSize=22.sp,lineHeight=28.sp,color=Ink)
            Spacer(Modifier.height(7.dp))
            Text("A reflection, a thought, or a few words. Mood and energy are optional inside each entry.",
                fontSize=14.sp,lineHeight=21.sp,color=Muted)
            Spacer(Modifier.height(15.dp))
            TodayMainButton("New reflection", {
                clear()
                showingEditor=true
            })
        }
        LandscapeHero(label="A QUIET PAGE",overline="YOUR LIVING JOURNAL",
            headline="Every day has\nits own story.",
            artwork=R.drawable.atelier_journal)
        if(showingEditor) {
            Panel {
                Text(if(editing==null)"New reflection" else "Edit reflection",
                    fontFamily=FontFamily.Serif,fontSize=21.sp,color=Ink)
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(value=body,onValueChange={body=it},
                    label={Text("What would you like to remember?")},
                    minLines=3,shape=RoundedCornerShape(12.dp),
                    colors=OutlinedTextFieldDefaults.colors(
                        focusedBorderColor=Sage,unfocusedBorderColor=Line,
                        focusedContainerColor=Paper,unfocusedContainerColor=Paper
                    ), modifier=Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                Text("How are you feeling? (optional)",fontSize=14.sp,color=Muted)
                Row(Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                    listOf("Calm","Hopeful","Tired","Worried").forEach { m ->
                        FilterChip(selected=mood==m,onClick={mood=if(mood==m)null else m},
                            label={Text(m)},shape=RoundedCornerShape(10.dp))
                    }
                }
                Spacer(Modifier.height(9.dp))
                Text("Energy (optional)",fontSize=14.sp,color=Muted)
                Row(Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                    (1..5).forEach { n ->
                        FilterChip(selected=energy==n,onClick={energy=if(energy==n)null else n},
                            label={Text(n.toString())},shape=RoundedCornerShape(10.dp))
                    }
                }
                Spacer(Modifier.height(12.dp))
                PrimaryAction(if(editing==null)"Save reflection" else "Save changes", {
                    if(body.isNotBlank()){onSave(editing,body,mood,energy);clear()}
                },Modifier.fillMaxWidth())
                TextButton(onClick={clear()}) { Text("Cancel",fontSize=12.sp,color=Sage) }
            }
        }
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
            Text("Your entries",fontFamily=FontFamily.Serif,fontSize=21.sp,color=Ink)
            Spacer(Modifier.weight(1f))
            Text(entries.size.toString()+" PRIVATE",fontSize=12.sp,color=Muted,
                fontWeight=FontWeight.Bold,letterSpacing=1.sp)
        }
        if(entries.isEmpty()) {
            Panel {
                Text("A quiet page for whenever you're ready.",fontFamily=FontFamily.Serif,
                    fontSize=17.sp,color=Ink)
                Text("Your thoughts can be brief, detailed or left unwritten. This space is yours.",
                    fontSize=12.sp,color=Muted,lineHeight=19.sp)
            }
        }
        entries.forEach { entry ->
            Panel {
                Text(Instant.ofEpochMilli(entry.createdAtEpochMs).atZone(ZoneId.systemDefault())
                    .toLocalDate().format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                    fontSize=10.sp,color=Muted)
                Spacer(Modifier.height(9.dp))
                Text(entry.body,fontFamily=FontFamily.Serif,fontSize=17.sp,
                    lineHeight=25.sp,color=Ink)
                if(entry.mood!=null || entry.energy!=null) {
                    Spacer(Modifier.height(7.dp))
                    Text(listOfNotNull(entry.mood,entry.energy?.let{"Energy "+it+"/5"})
                        .joinToString(" · "),color=Muted,fontSize=11.sp)
                }
                Row(horizontalArrangement=Arrangement.spacedBy(9.dp)) {
                    TextButton(onClick={
                        editing=entry;body=entry.body;mood=entry.mood;energy=entry.energy
                        showingEditor=true
                    }) { Text("Edit",color=Forest,fontSize=14.sp) }
                    TextButton(onClick={pendingDelete=entry}) {
                        Text("Delete",color=Color(0xFF905C3E),fontSize=14.sp)
                    }
                }
            }
        }
        Surface(color=Color(0xFFEAF0E7),shape=RoundedCornerShape(17.dp),
            border=BorderStroke(1.dp,Line),modifier=Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp)) {
                Text("PRIVACY, CLEARLY",fontSize=10.sp,color=Sage,fontWeight=FontWeight.Bold,
                    letterSpacing=1.5.sp)
                Spacer(Modifier.height(7.dp))
                Text("Your journal is stored in this device's private app database. No cloud sync or account is enabled in this build.",
                    color=Muted,fontSize=14.sp,lineHeight=21.sp)
                Spacer(Modifier.height(10.dp))
                SecondaryAction("Export or manage my data",onDataPrivacy)
            }
        }
    }
}

@Composable
fun ReminderSettingsPage(
    s: ChallengeSnapshot?,
    enabled: Boolean,
    permissionAvailable: Boolean,
    selectedTime: String,
    nextAt: Long,
    onEnable: (String)->Unit,
    onDisable: ()->Unit,
    onBack: ()->Unit
) {
    var timeValue by remember(s?.plan?.id,selectedTime) { mutableStateOf(selectedTime) }
    val parsed = remember(timeValue) {
        runCatching { java.time.LocalTime.parse(timeValue.trim()) }.getOrNull()
    }
    val status = when {
        !enabled -> "OFF · Notifications are not enabled for this challenge"
        !permissionAvailable -> "BLOCKED · Android notification permission is unavailable"
        nextAt > 0L -> "ON · Approximate next reminder: "+
            java.time.Instant.ofEpochMilli(nextAt)
                .atZone(s?.plan?.zone ?: java.time.ZoneId.systemDefault())
                .format(DateTimeFormatter.ofPattern("EEE d MMM · h:mm a"))
        else -> "ON · No scheduled opportunity is currently due"
    }
    MainScroll {
        Header("Optional · Quiet · Your choice","Gentle reminders",
            "A useful nudge if you want one. Never a measure of discipline.")
        Panel {
            Text("CURRENT STATUS",fontSize=11.sp,color=Forest,
                fontWeight=FontWeight.Bold,letterSpacing=1.sp)
            Spacer(Modifier.height(9.dp))
            Text(s?.plan?.title ?: "Choose a challenge first",
                fontFamily=FontFamily.Serif,fontSize=22.sp,
                lineHeight=28.sp,color=Ink)
            Spacer(Modifier.height(13.dp))
            Surface(color=Pale,shape=RoundedCornerShape(13.dp),
                modifier=Modifier.fillMaxWidth()) {
                Text(status,modifier=Modifier.padding(14.dp),
                    fontSize=14.sp,lineHeight=21.sp,color=Forest,
                    fontWeight=FontWeight.SemiBold)
            }
            Spacer(Modifier.height(17.dp))
            OutlinedTextField(value=timeValue,
                onValueChange={timeValue=it.take(5)},
                label={Text("Preferred time · 24-hour HH:mm")},
                placeholder={Text("09:00")},singleLine=true,
                modifier=Modifier.fillMaxWidth())
            Spacer(Modifier.height(9.dp))
            Text("The time follows this challenge's timezone. Android may deliver inexact reminders later due to battery restrictions.",
                color=Muted,fontSize=14.sp,lineHeight=21.sp)
            if(parsed==null) {
                Spacer(Modifier.height(9.dp))
                Text("Enter a valid time such as 07:30 or 20:00.",
                    color=Color(0xFF905C3E),fontSize=13.sp,lineHeight=19.sp)
            }
            Spacer(Modifier.height(17.dp))
            Button(
                onClick={ if(parsed!=null) onEnable(parsed.toString()) },
                enabled=parsed!=null && s!=null,
                modifier=Modifier.fillMaxWidth().heightIn(min=52.dp),
                shape=RoundedCornerShape(14.dp),
                colors=ButtonDefaults.buttonColors(
                    containerColor=Forest,contentColor=Paper)
            ) {
                Text(if(enabled)"Update reminder" else "Enable reminder",
                    fontSize=14.sp,lineHeight=20.sp,
                    fontWeight=FontWeight.SemiBold)
            }
            if(enabled) {
                Spacer(Modifier.height(10.dp))
                TodayQuietButton("Turn off reminders",onDisable)
            }
        }
        Panel {
            Text("You stay in control",fontFamily=FontFamily.Serif,
                fontSize=21.sp,lineHeight=27.sp,color=Ink)
            Spacer(Modifier.height(9.dp))
            Text("Reminders are off unless you choose to enable them. Permission is requested only when needed, and your private journal or mood never appears in a notification.",
                fontSize=14.sp,lineHeight=21.sp,color=Muted)
            Spacer(Modifier.height(8.dp))
            Text("Turning reminders off never deletes your challenge or its records.",
                fontSize=13.sp,lineHeight=20.sp,color=Muted)
        }
        TodayQuietButton("Back to challenge",onBack)
    }
}

@Composable
fun DataPrivacyPage(
    onBack:()->Unit,
    onExport:()->Unit,
    onPickBackup:()->Unit,
    importPreview:String?,
    onRestore:()->Unit,
    onCancelRestore:()->Unit,
    onErase:()->Unit
) {
    var deletionText by remember { mutableStateOf("") }
    var replaceText by remember(importPreview) { mutableStateOf("") }
    MainScroll {
        Header("Your information · Your choice", "Data & privacy",
            "Keep your personal writing and challenge history under your control.")
        Panel {
            Text("Export my data",fontFamily=FontFamily.Serif,fontSize=21.sp,color=Ink)
            Spacer(Modifier.height(8.dp))
            Text("Save a JSON copy of your growth areas, challenges, all practice revisions, pauses and private journal entries. You choose where it is stored.",
                color=Muted,fontSize=12.sp,lineHeight=19.sp)
            Spacer(Modifier.height(10.dp))
            Text("Important: exports include private reflections and are not encrypted by EVOLUNE. Store them securely.",
                color=Color(0xFF905C3E),fontSize=11.sp,lineHeight=17.sp)
            Spacer(Modifier.height(13.dp))
            PrimaryAction("Choose export location",onExport,Modifier.fillMaxWidth())
        }
        Panel {
            Text("Restore from an export",fontFamily=FontFamily.Serif,fontSize=21.sp,color=Ink)
            Spacer(Modifier.height(8.dp))
            Text("Choose an EVOLUNE version 1 JSON export. We validate every record before offering to restore it. Restoring replaces all current on-device data, including journal entries.",
                color=Muted,fontSize=12.sp,lineHeight=19.sp)
            Spacer(Modifier.height(11.dp))
            SecondaryAction("Choose backup JSON",onPickBackup)
            if(importPreview!=null) {
                Spacer(Modifier.height(15.dp))
                Surface(color=Pale,shape=RoundedCornerShape(12.dp)) {
                    Column(Modifier.padding(13.dp)) {
                        Text("VALIDATED BACKUP",color=Sage,fontSize=9.sp,
                            letterSpacing=1.1.sp,fontWeight=FontWeight.Bold)
                        Spacer(Modifier.height(5.dp))
                        Text(importPreview,fontSize=12.sp,lineHeight=18.sp,color=Ink)
                    }
                }
                Spacer(Modifier.height(11.dp))
                Text("Only continue if you intend to overwrite this device's existing EVOLUNE information. Export your current data first.",
                    color=Color(0xFF905C3E),fontSize=11.sp,lineHeight=17.sp)
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value=replaceText,onValueChange={replaceText=it.take(32)},
                    label={Text("Type REPLACE to restore")},
                    singleLine=true,modifier=Modifier.fillMaxWidth())
                Spacer(Modifier.height(9.dp))
                Button(onClick={
                    if(replaceText=="REPLACE") { replaceText="";onRestore() }
                },enabled=replaceText=="REPLACE",
                    colors=ButtonDefaults.buttonColors(containerColor=Forest),
                    shape=RoundedCornerShape(12.dp),
                    modifier=Modifier.fillMaxWidth()) {
                    Text("Replace this device's data from backup")
                }
                TextButton(onClick={replaceText="";onCancelRestore()}) {
                    Text("Cancel restore",color=Muted,fontSize=12.sp)
                }
            }
        }
        Panel {
            Text("Erase local data",fontFamily=FontFamily.Serif,fontSize=21.sp,color=Ink)
            Spacer(Modifier.height(8.dp))
            Text("Permanently remove all growth areas, challenge and schedule history, and private journal entries from this installation. This cannot be undone. Export first if you need a copy.",
                color=Muted,fontSize=12.sp,lineHeight=19.sp)
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value=deletionText,
                onValueChange={ deletionText=it.take(32) },
                label={Text("Type DELETE to confirm")},
                shape=RoundedCornerShape(12.dp),
                singleLine=true,
                modifier=Modifier.fillMaxWidth(),
                colors=OutlinedTextFieldDefaults.colors(
                    focusedBorderColor=Color(0xFFAA665A),
                    unfocusedBorderColor=Line
                ))
            Spacer(Modifier.height(12.dp))
            Button(onClick={
                    if(deletionText=="DELETE") {
                        deletionText=""
                        onErase()
                    }
                },enabled=deletionText=="DELETE",
                colors=ButtonDefaults.buttonColors(
                    containerColor=Color(0xFF995646),contentColor=Color.White),
                shape=RoundedCornerShape(12.dp),modifier=Modifier.fillMaxWidth()) {
                Text("Permanently erase this device's data")
            }
        }
        Text("No account or server backup is configured. Uninstalling the app before exporting may permanently lose your records.",
            fontSize=11.sp,color=Muted,lineHeight=17.sp)
        SecondaryAction("Back to Journal",onBack)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InsightsPage(s: ChallengeSnapshot?) {
    MainScroll {
        Header("Patterns, not verdicts", "See what\nis changing.",
            "A calm view of what you have actually recorded.")
        if(s==null) {
            Panel {
                Text("YOUR FIRST INSIGHT",fontSize=11.sp,color=Forest,
                    fontWeight=FontWeight.Bold,letterSpacing=1.sp)
                Spacer(Modifier.height(11.dp))
                Text("Your story starts with a record.",fontFamily=FontFamily.Serif,
                    fontSize=24.sp,lineHeight=30.sp,color=Ink)
                Spacer(Modifier.height(8.dp))
                Text("There is no challenge activity to summarize yet. Create a practice and log what actually happened to see meaningful patterns.",
                    fontSize=14.sp,lineHeight=21.sp,color=Muted)
            }
        } else {
            val list=days(s)
            val summary=ChallengeEngine.summarize(list)
            Panel {
                Text("THIS CHALLENGE",fontSize=11.sp,color=Forest,
                    fontWeight=FontWeight.Bold,letterSpacing=1.sp)
                Spacer(Modifier.height(8.dp))
                Text(s.plan.title,fontFamily=FontFamily.Serif,
                    fontSize=23.sp,lineHeight=28.sp,color=Ink)
                Spacer(Modifier.height(15.dp))
                Surface(color=Pale,shape=RoundedCornerShape(17.dp),
                    modifier=Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(18.dp)) {
                        Text(summary.recordedFull.toString(),
                            fontSize=49.sp,lineHeight=55.sp,
                            fontFamily=FontFamily.Serif,color=Forest)
                        Spacer(Modifier.height(5.dp))
                        Text("Full practices explicitly recorded",
                            fontSize=15.sp,lineHeight=22.sp,color=Ink)
                        Spacer(Modifier.height(8.dp))
                        Text("Out of ${summary.eligibleOpportunities} eligible opportunities through today. This number describes records, not your worth.",
                            fontSize=13.sp,lineHeight=20.sp,color=Muted)
                    }
                }
            }
            Text("MORE OF THE PICTURE",fontSize=11.sp,color=Forest,
                fontWeight=FontWeight.Bold,letterSpacing=1.sp)
            FlowRow(
                modifier=Modifier.fillMaxWidth(),
                horizontalArrangement=Arrangement.spacedBy(8.dp),
                verticalArrangement=Arrangement.spacedBy(8.dp)
            ) {
                listOf(
                    "Partial" to summary.partial,
                    "Backup action" to summary.recordedBackup,
                    "Intentional rest" to summary.rested,
                    "Unreported" to summary.unreported,
                    "Explicitly missed" to summary.explicitlyMissed,
                    "Paused" to summary.paused
                ).forEach { (label,count) ->
                    Surface(color=Pale,shape=RoundedCornerShape(14.dp),
                        modifier=Modifier.widthIn(min=135.dp)) {
                        Column(Modifier.padding(15.dp)) {
                            Text(count.toString(),fontFamily=FontFamily.Serif,
                                fontSize=29.sp,lineHeight=34.sp,color=Ink)
                            Spacer(Modifier.height(5.dp))
                            Text(label,fontSize=14.sp,lineHeight=20.sp,color=Forest)
                        }
                    }
                }
            }
            Panel {
                Text("How to read your history",fontFamily=FontFamily.Serif,
                    fontSize=22.sp,lineHeight=28.sp,color=Ink)
                Spacer(Modifier.height(10.dp))
                Text("Unreported means no entry was made—not that you missed the practice. A backup action is shown separately from a full action.",
                    fontSize=14.sp,lineHeight=21.sp,color=Muted)
                Spacer(Modifier.height(11.dp))
                Text("Only your currently selected challenge is summarized here. This is not an overall wellbeing score.",
                    fontSize=14.sp,lineHeight=21.sp,color=Muted)
            }
        }
        Text("Insights describe your recorded behavior. They do not diagnose health, predict future results, or prove that a habit is permanent.",
            fontSize=13.sp,lineHeight=20.sp,color=Muted)
    }
}
