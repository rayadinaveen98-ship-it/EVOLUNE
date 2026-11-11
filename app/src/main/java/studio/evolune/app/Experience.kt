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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
private val Muted = Color(0xFF718077)
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
    val selected = if (page in setOf("detail","create","recovery","review")) "challenges" else page
    MaterialTheme(colorScheme = appColors) {
        Scaffold(
            containerColor = Cream,
            bottomBar = {
                Surface(color = Paper, border = BorderStroke(0.5.dp, Line)) {
                    Row(Modifier.fillMaxWidth().height(70.dp).padding(horizontal=5.dp, vertical=5.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly) {
                        listOf("today" to "Today", "growth" to "My Growth", "challenges" to "Challenges",
                            "journal" to "Journal", "insights" to "Insights").forEach { (route,label) ->
                            val isActive = selected == route
                            Column(
                                Modifier.weight(1f).fillMaxHeight().clickable { onPage(route) },
                                horizontalAlignment=Alignment.CenterHorizontally,
                                verticalArrangement=Arrangement.Center
                            ) {
                                Box(
                                    Modifier.size(30.dp).clip(RoundedCornerShape(50)).background(if(isActive) Pale else Color.Transparent),
                                    contentAlignment=Alignment.Center
                                ) { NavGlyph(route, if(isActive) Forest else Color(0xFF96A299)) }
                                Text(label, fontSize=9.sp, lineHeight=12.sp, maxLines=1,
                                    color=if(isActive) Forest else Muted,
                                    fontWeight=if(isActive) FontWeight.Bold else FontWeight.Medium)
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
                        .clickable { onPage("growth") }, contentAlignment=Alignment.Center) {
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
        Text(kicker.uppercase(), fontSize=9.sp, letterSpacing=1.6.sp, color=Color(0xFF829085),
            fontWeight=FontWeight.Bold, lineHeight=13.sp)
        Spacer(Modifier.height(7.dp))
        Text(title, fontFamily=FontFamily.Serif, fontSize=30.sp, lineHeight=32.sp,
            letterSpacing=(-1.15).sp, color=Ink)
        if (subtitle.isNotEmpty()) {
            Spacer(Modifier.height(6.dp))
            Text(subtitle, color=Muted, fontSize=12.sp, lineHeight=18.sp)
        }
    }
}

@Composable
private fun Panel(content: @Composable ColumnScope.() -> Unit) {
    Surface(color=Paper, shape=RoundedCornerShape(20.dp), border=BorderStroke(1.dp,Line),
        modifier=Modifier.fillMaxWidth()) {
        Column(Modifier.padding(17.dp), content=content)
    }
}

@Composable
private fun MainScroll(content: @Composable ColumnScope.() -> Unit) {
    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState())
            .padding(horizontal=20.dp, vertical=10.dp),
        verticalArrangement=Arrangement.spacedBy(15.dp), content=content
    )
}

@Composable
private fun PrimaryAction(text: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Button(onClick=onClick, modifier=modifier.heightIn(min=43.dp),
        shape=RoundedCornerShape(11.dp),
        colors=ButtonDefaults.buttonColors(containerColor=Forest, contentColor=Paper)) {
        Text(text, fontSize=12.sp, fontWeight=FontWeight.SemiBold)
    }
}

@Composable
private fun SecondaryAction(text: String, onClick: () -> Unit) {
    OutlinedButton(onClick=onClick, shape=RoundedCornerShape(11.dp),
        colors=ButtonDefaults.outlinedButtonColors(containerColor=Color(0xFFF3F6F0), contentColor=Forest),
        border=BorderStroke(1.dp,Color(0xFFDDE5D9))) {
        Text(text, fontSize=12.sp, fontWeight=FontWeight.SemiBold)
    }
}

/** Approved Stillwater scene, with the readable editorial overlay used in the HTML baseline. */
@Composable
private fun LandscapeHero(
    label: String = "YOUR GROWTH SPACE",
    overline: String = "STILLWATER × ATELIER",
    headline: String = "Every small step\nbelongs here."
) {
    Box(Modifier.fillMaxWidth().height(195.dp).clip(RoundedCornerShape(19.dp))) {
        Image(painterResource(R.drawable.stillwater_banner),
            contentDescription="Quiet Stillwater landscape with stone steps, a tree and distant hills",
            contentScale=ContentScale.Crop, modifier=Modifier.fillMaxSize())
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

@Composable private fun OutcomeButtons(onLog: (PracticeOutcome)->Unit) {
    Text("Record what happened", color=Muted, fontSize=12.sp)
    Row(horizontalArrangement=Arrangement.spacedBy(8.dp), modifier=Modifier.fillMaxWidth()) {
        PrimaryAction("Completed", { onLog(PracticeOutcome.COMPLETED) }, Modifier.weight(1f))
        SecondaryAction("Partly", { onLog(PracticeOutcome.PARTIAL) })
    }
    Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) {
        SecondaryAction("Rested", { onLog(PracticeOutcome.RESTED) })
        SecondaryAction("Missed", { onLog(PracticeOutcome.MISSED) })
        SecondaryAction("Backup", { onLog(PracticeOutcome.COMPLETED_BACKUP) })
    }
}

@Composable
fun TodayPage(
    s: ChallengeSnapshot?, onCreate: () -> Unit, onOpen: () -> Unit,
    onLog: (PracticeOutcome)->Unit, onAdjust:()->Unit, onJournal:()->Unit
) {
    var showLogChoices by remember(s?.plan?.id) { mutableStateOf(false) }
    MainScroll {
        val today = LocalDate.now()
        val dateLabel = today.format(DateTimeFormatter.ofPattern("EEEE · MMMM d")).uppercase()
        Header(dateLabel + " · YOUR DAY", "A gentle day\nto begin again.",
            "One small step is enough to begin.")
        LandscapeHero()
        Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
            Text("Your one thing",fontFamily=FontFamily.Serif,fontSize=21.sp,color=Ink)
            Spacer(Modifier.weight(1f))
            TextButton(onClick=if(s==null)onCreate else onOpen) {
                Text(if(s==null)"Begin a practice" else "View your path  →",
                    color=Sage,fontSize=11.sp,fontWeight=FontWeight.Bold)
            }
        }
        if(s==null) {
            Panel {
                Text("Begin with one thing",fontFamily=FontFamily.Serif,fontSize=23.sp,color=Ink)
                Spacer(Modifier.height(5.dp))
                Text("Choose a small practice you want to explore. You can change it later.",
                    fontSize=12.sp,lineHeight=18.sp,color=Muted)
                Spacer(Modifier.height(13.dp))
                PrimaryAction("Create your first challenge",onCreate)
            }
        } else {
            val plan=s.plan
            val localToday=LocalDate.now(plan.zone)
            val opportunity=days(s).lastOrNull { it.date == localToday }
            Panel {
                Row(Modifier.fillMaxWidth(),verticalAlignment=Alignment.CenterVertically) {
                    Text("TODAY'S PRACTICE",fontSize=9.sp,fontWeight=FontWeight.Bold,
                        letterSpacing=1.4.sp,color=Color(0xFF99A394))
                    Spacer(Modifier.weight(1f))
                    Surface(color=Pale,shape=RoundedCornerShape(50),
                        border=BorderStroke(1.dp,Color(0xFFDCE5DB))) {
                        Text(localToday.dayOfMonth.toString(),
                            Modifier.padding(horizontal=12.dp,vertical=9.dp),
                            fontSize=11.sp,fontWeight=FontWeight.Bold,color=Sage)
                    }
                }
                Spacer(Modifier.height(6.dp))
                Text(plan.scheduleOn(localToday).action,fontFamily=FontFamily.Serif,
                    fontSize=23.sp,lineHeight=25.sp,color=Ink)
                Spacer(Modifier.height(6.dp))
                Text(plan.title + " · " + (opportunity?.state?.let(::stateLabel) ?: "Milestone finished"),
                    fontSize=11.sp,color=Muted)
                Spacer(Modifier.height(14.dp))
                val canLog=opportunity != null &&
                    opportunity.state != OpportunityState.NOT_SCHEDULED &&
                    opportunity.state != OpportunityState.PAUSED
                if(canLog) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(8.dp)) {
                        PrimaryAction("✓  Log practice",{showLogChoices=!showLogChoices},Modifier.weight(1f))
                        SecondaryAction("Adjust",onAdjust)
                    }
                    if(showLogChoices) {
                        Spacer(Modifier.height(15.dp))
                        Text("What happened today?",fontFamily=FontFamily.Serif,fontSize=17.sp,color=Ink)
                        Spacer(Modifier.height(6.dp))
                        OutcomeButtons { outcome ->
                            showLogChoices=false
                            onLog(outcome)
                        }
                    }
                } else if(opportunity?.state==OpportunityState.PAUSED) {
                    Text("Paused today. Your earlier practice remains exactly as recorded.",
                        fontSize=12.sp,lineHeight=18.sp,color=Muted)
                    Spacer(Modifier.height(7.dp))
                    SecondaryAction("Adjust plan",onAdjust)
                } else {
                    Text("No practice is scheduled for today.",fontSize=12.sp,color=Muted)
                    Spacer(Modifier.height(7.dp))
                    SecondaryAction("Challenge details",onOpen)
                }
                Spacer(Modifier.height(10.dp))
                Text("Your practice record is yours to revise; missing logs are not treated as failures.",
                    color=Muted,fontSize=10.sp,lineHeight=15.sp)
            }
        }
        Row(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp))
                .background(Color(0xFFEAF0E7)).clickable { onJournal() }
                .padding(15.dp),
            verticalAlignment=Alignment.CenterVertically,
            horizontalArrangement=Arrangement.spacedBy(11.dp)
        ) {
            Box(Modifier.size(40.dp).clip(RoundedCornerShape(12.dp)).background(Color(0xFFD9E5D7)),
                contentAlignment=Alignment.Center) { NavGlyph("journal", Sage) }
            Column(Modifier.weight(1f)) {
                Text("A moment to reflect",fontSize=12.sp,color=Ink,fontWeight=FontWeight.Bold)
                Text("Mood and energy are always optional",fontSize=10.sp,color=Muted)
            }
            Text("→",fontSize=20.sp,color=Sage)
        }
        Text("Private and offline in this build. Cloud backup and sync are not enabled.",
            fontSize=10.sp,lineHeight=16.sp,color=Muted)
    }
}

@Composable
fun ChallengeListPage(challenges: List<ChallengeEntity>, chosen: String?, onSelect: (String)->Unit, onCreate:()->Unit) {
    MainScroll {
        Header("Challenge studio", "The changes you choose.", "Create your own practice, at your own pace.")
        PrimaryAction("+ New challenge",onCreate)
        if(challenges.isEmpty()) Panel { Text("No challenges yet. Start with a small, repeatable action.", color=Muted) }
        for(ch in challenges) Panel {
            Text(ch.title, fontSize=21.sp,fontFamily=FontFamily.Serif,color=Ink)
            Spacer(Modifier.height(5.dp))
            Text("${ch.target} ${if(ch.milestoneKind == MilestoneKind.CALENDAR_DAYS.name) "calendar days" else "full sessions"} · ${ch.startDate}",fontSize=12.sp,color=Muted)
            Spacer(Modifier.height(9.dp))
            SecondaryAction(if(ch.id==chosen) "View active challenge" else "Open challenge", { onSelect(ch.id) })
        }
    }
}

@Composable
fun ChallengeDetailPage(s: ChallengeSnapshot?, onAdjust:()->Unit, onReview:()->Unit, onBack:()->Unit, onLog:(PracticeOutcome)->Unit) {
    if(s == null) { MainScroll { Text("Choose a challenge to see its details."); SecondaryAction("Back",onBack) }; return }
    val plan = s.plan; val list = days(s); val sum = ChallengeEngine.summarize(list)
    MainScroll {
        Header("Your practice", plan.title,"${plan.target} ${if(plan.milestoneKind==MilestoneKind.CALENDAR_DAYS)"calendar days" else "completed sessions"} · ${plan.startDate}")
        Panel {
            Text("Recorded full actions",color=Muted,fontSize=12.sp)
            Text("${sum.recordedFull} / ${sum.eligibleOpportunities}",fontSize=30.sp,color=Ink,fontFamily=FontFamily.Serif)
            Text("${sum.unreported} unreported · ${sum.partial} partial · ${sum.rested} rest days",fontSize=12.sp,color=Muted)
            Spacer(Modifier.height(10.dp))
            Row(horizontalArrangement=Arrangement.spacedBy(8.dp)) { PrimaryAction("Adjust plan",onAdjust); SecondaryAction("Milestone review",onReview) }
        }
        Panel {
            Text("Recent opportunities",fontSize=18.sp,fontFamily=FontFamily.Serif)
            Spacer(Modifier.height(10.dp))
            if(list.isEmpty()) Text("Your first practice hasn't started yet.",color=Muted)
            list.takeLast(15).reversed().forEach { day ->
                Row(Modifier.fillMaxWidth().padding(vertical=7.dp),horizontalArrangement=Arrangement.SpaceBetween) {
                    Text(day.date.format(DateTimeFormatter.ofPattern("EEE d MMM")),fontSize=13.sp)
                    Text(stateLabel(day.state),fontSize=12.sp,color=Sage)
                }
                HorizontalDivider(color=Line)
            }
        }
        val todayState = list.lastOrNull { it.date == LocalDate.now(plan.zone) }?.state
        if(todayState != null && todayState != OpportunityState.NOT_SCHEDULED && todayState != OpportunityState.PAUSED) Panel { OutcomeButtons(onLog) }
        SecondaryAction("Back to challenges",onBack)
    }
}

data class CreateRequest(
    val title: String, val action: String, val target: Int, val kind: MilestoneKind,
    val recurrence: Recurrence, val cueTime: LocalTime?, val fallback: String?, val areaId: String?
)

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
    var warning by remember { mutableStateOf("") }
    MainScroll {
        Header("A fresh beginning", "Start with one thing.", "Define an action you can practice. It doesn't have to be perfect.")
        Panel {
            OutlinedTextField(value=title,onValueChange={title=it},label={Text("Challenge title")},singleLine=true,modifier=Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value=action,onValueChange={action=it},label={Text("My repeatable action")},placeholder={Text("Read for 10 minutes")},modifier=Modifier.fillMaxWidth())
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(value=targetText,onValueChange={targetText=it.filter(Char::isDigit)},label={Text("Milestone number")},singleLine=true,modifier=Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))
            Text("Milestone means",color=Muted,fontSize=12.sp)
            Row(horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                FilterChip(selected=mode==MilestoneKind.CALENDAR_DAYS,onClick={mode=MilestoneKind.CALENDAR_DAYS},label={Text("Calendar days")})
                FilterChip(selected=mode==MilestoneKind.COMPLETED_SESSIONS,onClick={mode=MilestoneKind.COMPLETED_SESSIONS},label={Text("Full sessions")})
            }
            Text("A 21-day period isn't a guarantee that a habit is formed.",fontSize=11.sp,color=Muted)
        }
        Panel {
            Text("Choose practice days",fontSize=17.sp,fontFamily=FontFamily.Serif)
            listOf("Every day" to Recurrence.Daily,"Weekdays" to Recurrence.Weekdays,"Weekends" to Recurrence.Weekends).forEach { (name,r) ->
                Row(verticalAlignment=Alignment.CenterVertically) {
                    RadioButton(selected=recurrence==r,onClick={recurrence=r})
                    Text(name,modifier=Modifier.clickable{recurrence=r},fontSize=14.sp)
                }
            }
            OutlinedTextField(value=cue,onValueChange={cue=it},label={Text("Optional time (HH:mm)")},singleLine=true,modifier=Modifier.fillMaxWidth())
            Spacer(Modifier.height(9.dp))
            OutlinedTextField(value=fallback,onValueChange={fallback=it},label={Text("Smaller backup action (optional)")},modifier=Modifier.fillMaxWidth())
            if(areas.isNotEmpty()) {
                Spacer(Modifier.height(12.dp)); Text("Growth area (optional)",fontSize=12.sp,color=Muted)
                Row(horizontalArrangement=Arrangement.spacedBy(6.dp),modifier=Modifier.horizontalScroll(rememberScrollState())) {
                    FilterChip(selected=areaId==null,onClick={areaId=null},label={Text("None")})
                    areas.forEach { area -> FilterChip(selected=area.id==areaId,onClick={areaId=area.id},label={Text(area.name)}) }
                }
            }
        }
        if(warning.isNotEmpty()) Text(warning,color=Color(0xFF9D4F40),fontSize=12.sp)
        PrimaryAction("Create my challenge",{
            val count=targetText.toIntOrNull()
            val time=if(cue.isBlank()) null else runCatching { LocalTime.parse(cue.trim()) }.getOrNull()
            warning = when {
                title.isBlank() || action.isBlank() -> "Enter a title and one repeatable action."
                count == null || count !in 1..3650 -> "Choose a valid milestone number from 1 to 3650."
                cue.isNotBlank() && time == null -> "Use a valid time such as 07:30."
                else -> ""
            }
            if(warning.isEmpty()) onCreate(CreateRequest(title,action,count!!,mode,recurrence,time,fallback,areaId))
        },Modifier.fillMaxWidth())
        SecondaryAction("Back",onBack)
    }
}

@Composable
fun RecoveryPage(s: ChallengeSnapshot?, onBack:()->Unit, onRevise:(String,Recurrence)->Unit, onPause:()->Unit) {
    if(s==null){MainScroll{Text("No challenge selected.");SecondaryAction("Back",onBack)};return}
    var action by remember(s.plan.id){mutableStateOf(s.plan.schedules.last().action)}
    var recurrence by remember(s.plan.id){mutableStateOf(s.plan.schedules.last().recurrence)}
    MainScroll {
        Header("A gentle restart", "Adjust the path.", "Your history stays exactly as you recorded it.")
        LandscapeHero()
        Panel {
            Text("Smaller steps still matter",fontSize=20.sp,fontFamily=FontFamily.Serif)
            OutlinedTextField(value=action,onValueChange={action=it},label={Text("Next action (from tomorrow)")},modifier=Modifier.fillMaxWidth())
            Spacer(Modifier.height(10.dp))
            Text("Practice days",fontSize=12.sp,color=Muted)
            listOf("Daily" to Recurrence.Daily,"Weekdays" to Recurrence.Weekdays,"Weekends" to Recurrence.Weekends).forEach { (label,r) ->
                Row(verticalAlignment=Alignment.CenterVertically){RadioButton(selected=recurrence==r,onClick={recurrence=r});Text(label)}
            }
            PrimaryAction("Save future plan", { if(action.isNotBlank()) onRevise(action,recurrence) },Modifier.fillMaxWidth())
        }
        Panel {
            Text("Need some space?",fontFamily=FontFamily.Serif,fontSize=19.sp)
            Text("Pause the next three days. Nothing is erased, and this isn't counted as completing them.",color=Muted,fontSize=12.sp)
            Spacer(Modifier.height(8.dp)); SecondaryAction("Pause next three days",onPause)
        }
        SecondaryAction("Back without changes",onBack)
    }
}

@Composable
fun ReviewPage(s: ChallengeSnapshot?, onBack:()->Unit) {
    if(s==null){MainScroll{Text("Choose a challenge first.");SecondaryAction("Back",onBack)};return}
    val summary=ChallengeEngine.summarize(days(s))
    MainScroll {
        Header("Your reflection", "Every phase has meaning.","A factual summary of what's been recorded so far.")
        Panel {
            Text(s.plan.title,fontFamily=FontFamily.Serif,fontSize=20.sp)
            Spacer(Modifier.height(10.dp))
            listOf(
                "Recorded full actions" to summary.recordedFull,
                "Partial sessions" to summary.partial,
                "Backup sessions" to summary.recordedBackup,
                "Intentional rest" to summary.rested,
                "Explicitly marked missed" to summary.explicitlyMissed,
                "Not logged" to summary.unreported,
                "Paused" to summary.paused,
                "Eligible scheduled opportunities" to summary.eligibleOpportunities
            ).forEach { (label,value)-> Row(Modifier.fillMaxWidth().padding(vertical=5.dp),horizontalArrangement=Arrangement.SpaceBetween){Text(label,fontSize=12.sp,color=Muted);Text("$value",fontSize=13.sp,fontWeight=FontWeight.SemiBold)} }
        }
        Panel {
            Text("A milestone is a moment to reflect—not proof of permanent habit formation.",fontSize=13.sp,color=Ink)
            Text("You can continue, adjust the plan, or take a pause. Your choice.",fontSize=12.sp,color=Muted)
        }
        SecondaryAction("Back to challenge",onBack)
    }
}

@Composable
fun GrowthPage(areas:List<GrowthAreaEntity>,onCreateArea:(String)->Unit,onStart:()->Unit) {
    var title by remember { mutableStateOf("") }
    MainScroll {
        Header("My growth", "Your direction, your pace.", "Choose the parts of life that feel meaningful to you.")
        LandscapeHero()
        Panel {
            Text("Create a growth area",fontFamily=FontFamily.Serif,fontSize=19.sp)
            OutlinedTextField(value=title,onValueChange={title=it},label={Text("Area name")},modifier=Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp));PrimaryAction("Add area",{if(title.isNotBlank()){onCreateArea(title);title=""}})
        }
        if(areas.isEmpty()) Text("Examples: Learning, Mind & awareness, Daily foundations, Connection, Body & energy.",color=Muted,fontSize=12.sp)
        areas.forEach { area -> Panel {Text(area.name,fontFamily=FontFamily.Serif,fontSize=20.sp);Spacer(Modifier.height(7.dp));SecondaryAction("Start a related challenge",onStart)} }
    }
}

@Composable
fun JournalPage(
    entries: List<JournalEntryEntity>,
    onSave:(JournalEntryEntity?,String,String?,Int?)->Unit,
    onDelete:(JournalEntryEntity)->Unit
) {
    var editing by remember { mutableStateOf<JournalEntryEntity?>(null) }
    var showingEditor by remember { mutableStateOf(false) }
    var body by remember { mutableStateOf("") }
    var mood by remember { mutableStateOf<String?>(null) }
    var energy by remember { mutableStateOf<Int?>(null) }
    fun clear() { editing=null;body="";mood=null;energy=null;showingEditor=false }
    MainScroll {
        Header("Private reflections · On this device", "Room to feel.\nSpace to think.",
            "Write as much or as little as you like. No mood check-in is ever required.")
        LandscapeHero(label="A QUIET PAGE",overline="YOUR LIVING JOURNAL",
            headline="Every day has\nits own story.")
        Row(Modifier.fillMaxWidth(), horizontalArrangement=Arrangement.spacedBy(9.dp)) {
            PrimaryAction("✎  New reflection", {
                clear()
                showingEditor=true
            },Modifier.weight(1f))
            SecondaryAction("Mood & energy",{
                if(!showingEditor) {
                    clear()
                    showingEditor=true
                }
            })
        }
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
                Text("How are you feeling? (optional)",fontSize=12.sp,color=Muted)
                Row(Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement=Arrangement.spacedBy(6.dp)) {
                    listOf("Calm","Hopeful","Tired","Worried").forEach { m ->
                        FilterChip(selected=mood==m,onClick={mood=if(mood==m)null else m},
                            label={Text(m)},shape=RoundedCornerShape(10.dp))
                    }
                }
                Spacer(Modifier.height(9.dp))
                Text("Energy (optional)",fontSize=12.sp,color=Muted)
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
            Text(entries.size.toString()+" PRIVATE",fontSize=10.sp,color=Muted,
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
                Text(entry.body,fontFamily=FontFamily.Serif,fontSize=16.sp,
                    lineHeight=23.sp,color=Ink)
                if(entry.mood!=null || entry.energy!=null) {
                    Spacer(Modifier.height(7.dp))
                    Text(listOfNotNull(entry.mood,entry.energy?.let{"Energy "+it+"/5"})
                        .joinToString(" · "),color=Muted,fontSize=11.sp)
                }
                Row(horizontalArrangement=Arrangement.spacedBy(9.dp)) {
                    TextButton(onClick={
                        editing=entry;body=entry.body;mood=entry.mood;energy=entry.energy
                        showingEditor=true
                    }) { Text("✎ Edit",color=Sage,fontSize=12.sp) }
                    TextButton(onClick={onDelete(entry);if(editing?.id==entry.id)clear()}) {
                        Text("Delete",color=Muted,fontSize=12.sp)
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
                    color=Muted,fontSize=12.sp,lineHeight=18.sp)
            }
        }
    }
}

@Composable
fun InsightsPage(s: ChallengeSnapshot?) {
    MainScroll {
        Header("Patterns, not verdicts", "See what\nis changing.",
            "Every number has a story. You decide what it means.")
        if(s==null) {
            Surface(color=Pale, shape=RoundedCornerShape(20.dp),modifier=Modifier.fillMaxWidth()) {
                Column(Modifier.padding(19.dp)) {
                    Text("YOUR FIRST INSIGHT",fontSize=10.sp,color=Sage,
                        fontWeight=FontWeight.Bold,letterSpacing=1.4.sp)
                    Spacer(Modifier.height(10.dp))
                    Text("0",fontFamily=FontFamily.Serif,fontSize=47.sp,color=Forest)
                    Text("Recorded full practices so far",fontSize=12.sp,color=Muted)
                    Spacer(Modifier.height(10.dp))
                    Text("Create a challenge and record a practice to reveal patterns over time.",
                        fontSize=12.sp,lineHeight=19.sp,color=Muted)
                }
            }
        } else {
            val list=days(s)
            val summary=ChallengeEngine.summarize(list)
            Surface(color=Pale,shape=RoundedCornerShape(21.dp),
                modifier=Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp)) {
                    Text("RECORDED PRACTICES",fontSize=10.sp,color=Sage,
                        fontWeight=FontWeight.Bold,letterSpacing=1.5.sp)
                    Spacer(Modifier.height(7.dp))
                    Text(summary.recordedFull.toString(),
                        fontSize=52.sp,fontFamily=FontFamily.Serif,color=Forest)
                    Text("Full actions you explicitly recorded",fontSize=12.sp,color=Muted)
                }
            }
            Row(Modifier.fillMaxWidth(),horizontalArrangement=Arrangement.spacedBy(7.dp)) {
                listOf("Partial" to summary.partial,
                       "Unreported" to summary.unreported,
                       "Rest" to summary.rested).forEach { (name,n) ->
                    Surface(color=Pale,shape=RoundedCornerShape(13.dp),
                        modifier=Modifier.weight(1f)) {
                        Column(Modifier.padding(vertical=13.dp),horizontalAlignment=Alignment.CenterHorizontally) {
                            Text(n.toString(),fontFamily=FontFamily.Serif,fontSize=23.sp,color=Ink)
                            Text(name,fontSize=10.sp,color=Muted)
                        }
                    }
                }
            }
            Text("By challenge",fontFamily=FontFamily.Serif,fontSize=21.sp,color=Ink)
            Panel {
                Text(s.plan.title,fontWeight=FontWeight.SemiBold,color=Ink,fontSize=13.sp)
                Spacer(Modifier.height(5.dp))
                Text(summary.recordedFull.toString()+" full records out of "+
                    summary.eligibleOpportunities.toString()+" eligible opportunities so far",
                    fontSize=11.sp,color=Muted,lineHeight=16.sp)
                Spacer(Modifier.height(6.dp))
                Text("Only the currently selected challenge is shown in this foundation build.",
                    fontSize=10.sp,color=Muted,lineHeight=16.sp)
            }
            Surface(color=Pale,shape=RoundedCornerShape(16.dp),
                modifier=Modifier.fillMaxWidth()) {
                Column(Modifier.padding(16.dp)) {
                    Text("REFLECTIVE NOTE",color=Sage,fontWeight=FontWeight.Bold,
                        letterSpacing=1.4.sp,fontSize=9.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("An unreported day is not evidence of failure. Consider what made a practice easier or harder.",
                        color=Ink,fontSize=13.sp,lineHeight=19.sp)
                }
            }
        }
        Text("These are descriptions of recorded behavior, never scores for health, worth or habit formation.",
            color=Muted,fontSize=11.sp,lineHeight=17.sp)
    }
}
