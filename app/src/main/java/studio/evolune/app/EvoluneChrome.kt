package studio.evolune.app

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/** Five established destinations; subpages retain their parent destination's selection. */
internal data class EvoluneDestination(val route: String, val title: String, val compactTitle: String)
internal val evoluneDestinations = listOf(
    EvoluneDestination("today", "Today", "Today"),
    EvoluneDestination("growth", "My Growth", "Growth"),
    EvoluneDestination("challenges", "Challenges", "Plans"),
    EvoluneDestination("journal", "Journal", "Journal"),
    EvoluneDestination("insights", "Insights", "Trends")
)

internal fun selectedEvoluneDestination(page: String): String = when (page) {
    "detail", "create", "recovery", "review", "reminders" -> "challenges"
    "data" -> "journal"
    else -> if (evoluneDestinations.any { it.route == page }) page else "today"
}

/** Original consistent 24-unit monoline family, shared by navigation and inline actions. */
@Composable
internal fun EvoluneGlyph(
    kind: String,
    color: Color = EvoluneTokens.Forest,
    modifier: Modifier = Modifier
) {
    Canvas(modifier.size(22.dp)) {
        val k = size.width / 24f
        fun line(x1: Float, y1: Float, x2: Float, y2: Float) =
            drawLine(color, Offset(x1*k,y1*k), Offset(x2*k,y2*k),
                strokeWidth=1.85f*k, cap=StrokeCap.Round)
        fun path(vararg xy: Float) {
            if (xy.size < 4) return
            val p=Path().apply {
                moveTo(xy[0]*k,xy[1]*k)
                var i=2
                while(i<xy.size){lineTo(xy[i]*k,xy[i+1]*k);i+=2}
            }
            drawPath(p,color,style=Stroke(width=1.85f*k,cap=StrokeCap.Round))
        }
        when(kind) {
            "today" -> {
                path(3f,11f,12f,4f,21f,11f)
                path(5.5f,10f,5.5f,20f,18.5f,20f,18.5f,10f)
                path(10f,20f,10f,14f,14f,14f,14f,20f)
            }
            "growth" -> {
                line(12f,21f,12f,10f)
                path(12f,15f,8f,10f,6f,7f)
                path(12f,13f,16f,8f,19f,6f)
                drawCircle(color,2f*k,Offset(7f*k,6.5f*k),style=Stroke(1.6f*k))
                drawCircle(color,2f*k,Offset(19f*k,5.5f*k),style=Stroke(1.6f*k))
            }
            "challenges" -> {
                path(4f,8f,12f,3.5f,20f,8f,12f,12.5f,4f,8f)
                path(4f,12.5f,12f,17f,20f,12.5f)
                path(4f,17f,12f,21.5f,20f,17f)
            }
            "journal" -> {
                path(4f,20f,8.5f,19f,19f,8.5f,15f,4.5f,4.5f,15f,4f,20f)
                line(12.9f,6.6f,17f,10.7f)
                line(4f,22f,20f,22f)
            }
            "insights" -> {
                path(3f,19f,8f,13f,12f,16f,20f,6f)
                line(3f,22f,21f,22f)
            }
            "back" -> {line(20f,12f,4f,12f);path(11f,5f,4f,12f,11f,19f)}
            "close" -> {line(5f,5f,19f,19f);line(19f,5f,5f,19f)}
            "add" -> {line(12f,4f,12f,20f);line(4f,12f,20f,12f)}
            "check" -> {path(4f,12f,10f,18f,20f,6f)}
            "reminder" -> {
                path(6f,16f,7f,14f,7f,9f,9f,6f,12f,5f,15f,6f,17f,9f,17f,14f,18f,16f)
                line(5f,18f,19f,18f);path(10f,20f,12f,22f,14f,20f)
            }
            "privacy" -> {
                path(12f,2f,20f,6f,20f,12f,18f,17f,12f,22f,6f,17f,4f,12f,4f,6f,12f,2f)
                path(9f,12f,11f,14f,16f,9f)
            }
            else -> {drawCircle(color,8f*k,Offset(12f*k,12f*k),style=Stroke(1.85f*k))}
        }
    }
}

/** Shared action contract; enabled/disabled and pressed/focus states are Material-native. */
internal enum class EvoluneActionStyle { Primary, Quiet }

@Composable
internal fun EvoluneAction(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    style: EvoluneActionStyle = EvoluneActionStyle.Primary,
    enabled: Boolean = true
) {
    if (style == EvoluneActionStyle.Primary) {
        Button(
            onClick=onClick,
            enabled=enabled,
            modifier=modifier.heightIn(min=52.dp),
            shape=RoundedCornerShape(13.dp),
            colors=ButtonDefaults.buttonColors(
                containerColor=EvoluneTokens.Forest, contentColor=EvoluneTokens.Paper,
                disabledContainerColor=EvoluneTokens.Pale, disabledContentColor=EvoluneTokens.Muted
            )
        ) {
            Text(text,style=EvoluneType.typography.labelLarge,
                textAlign=TextAlign.Center,softWrap=true)
        }
    } else {
        OutlinedButton(
            onClick=onClick,
            enabled=enabled,
            modifier=modifier.heightIn(min=50.dp),
            shape=RoundedCornerShape(13.dp),
            colors=ButtonDefaults.outlinedButtonColors(
                containerColor=EvoluneTokens.Paper, contentColor=EvoluneTokens.Forest,
                disabledContentColor=EvoluneTokens.Muted
            ),
            border=BorderStroke(1.dp, if(enabled) Color(0xFFC5D2C5) else EvoluneTokens.Line)
        ) {
            Text(text,style=EvoluneType.typography.labelLarge.copy(
                    fontWeight=FontWeight.Medium),
                textAlign=TextAlign.Center,softWrap=true)
        }
    }
}

/** 48dp minimum target, a single selected-state source, and stable semantics across form factors. */
@Composable
private fun ChromeDestination(
    destination: EvoluneDestination,
    active: Boolean,
    compact: Boolean,
    showShortName: Boolean,
    modifier: Modifier,
    onPage: (String) -> Unit
) {
    val label = if(showShortName) destination.compactTitle else destination.title
    Column(
        modifier.selectable(selected=active,role=Role.Tab) { onPage(destination.route) }
            .semantics {
                contentDescription=destination.title
                stateDescription=if(active)"Selected" else "Not selected"
            }
            .testTag("evolune-nav-${destination.route}")
            .heightIn(min=52.dp).padding(horizontal=2.dp,vertical=5.dp),
        horizontalAlignment=Alignment.CenterHorizontally,
        verticalArrangement=Arrangement.Center
    ) {
        Box(Modifier.size(36.dp).clip(RoundedCornerShape(13.dp))
            .background(if(active)EvoluneTokens.Pale else Color.Transparent),
            contentAlignment=Alignment.Center) {
            EvoluneGlyph(destination.route,if(active)EvoluneTokens.Forest else EvoluneTokens.Muted,
                Modifier.size(23.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text(label,
            style=EvoluneType.typography.labelMedium.copy(
                fontSize=if(compact)11.sp else 12.sp,
                lineHeight=if(compact)16.sp else 18.sp
            ),
            maxLines=3,softWrap=true,
            textAlign=TextAlign.Center,
            fontWeight=if(active)FontWeight.SemiBold else FontWeight.Medium,
            color=if(active)EvoluneTokens.Forest else EvoluneTokens.Muted)
    }
}

@Composable
internal fun EvoluneBottomNav(
    page: String,
    onPage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val config=LocalConfiguration.current
    val compact=config.screenWidthDp < 370 || config.fontScale >= 1.35f
    val shortNames=compact && config.fontScale >= 1.3f
    // The bottom bar MUST have a bounded height: child fillMaxHeight would
    // otherwise grow a min-height-only Row to the full window and hide screens.
    val navHeight=when {
        config.fontScale>=1.85f -> 124.dp
        config.fontScale>=1.65f -> 116.dp
        config.fontScale>=1.3f -> 102.dp
        else -> 86.dp
    }
    Surface(color=EvoluneTokens.Paper,modifier=modifier.fillMaxWidth(),
        border=BorderStroke(0.5.dp,EvoluneTokens.Line)) {
        Box(Modifier.fillMaxWidth().navigationBarsPadding(),
            contentAlignment=Alignment.Center) {
            Row(
                Modifier.widthIn(max=760.dp).fillMaxWidth()
                    .height(navHeight)
                    .padding(horizontal=4.dp,vertical=4.dp),
                horizontalArrangement=Arrangement.SpaceEvenly,
                verticalAlignment=Alignment.CenterVertically
            ) {
                val selected=selectedEvoluneDestination(page)
                evoluneDestinations.forEach { dest ->
                    ChromeDestination(dest,selected==dest.route,compact,shortNames,
                        Modifier.weight(1f).fillMaxHeight(),onPage)
                }
            }
        }
    }
}

@Composable
internal fun EvoluneSideRail(
    page: String,
    onPage: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val selected=selectedEvoluneDestination(page)
    Surface(color=EvoluneTokens.Paper,modifier=modifier.width(116.dp).fillMaxHeight(),
        border=BorderStroke(0.5.dp,EvoluneTokens.Line)) {
        Column(Modifier.fillMaxHeight().systemBarsPadding()
            .verticalScroll(rememberScrollState()).padding(horizontal=9.dp,vertical=14.dp),
            verticalArrangement=Arrangement.spacedBy(6.dp)) {
            Text("EXPLORE",color=EvoluneTokens.Forest,
                style=EvoluneType.typography.labelSmall,
                letterSpacing=0.7.sp,
                modifier=Modifier.padding(start=10.dp,top=8.dp,bottom=8.dp))
            evoluneDestinations.forEach { dest ->
                ChromeDestination(dest, selected==dest.route,
                    compact=false,showShortName=false,
                    modifier=Modifier.fillMaxWidth().heightIn(min=76.dp),onPage=onPage)
            }
        }
    }
}

@Composable
internal fun EvoluneTopBar(onGrowth: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().heightIn(min=62.dp)
            .padding(start=17.dp,end=15.dp,top=6.dp,bottom=6.dp),
        verticalAlignment=Alignment.CenterVertically
    ) {
        // The same vector path as the approved Open Path welcome, rendered static.
        Box(Modifier.size(40.dp),contentAlignment=Alignment.Center) {
            OpenPathSmallMark(Modifier.size(40.dp))
        }
        Spacer(Modifier.width(8.dp))
        Text("EVOLUNE",
            style=EvoluneType.typography.titleMedium.copy(
                letterSpacing=1.1.sp
            ),
            color=EvoluneTokens.Ink,softWrap=true)
        Spacer(Modifier.weight(1f))
        Surface(
            onClick=onGrowth,
            modifier=Modifier.size(48.dp)
                .semantics { contentDescription="Open My Growth" }
                .testTag("evolune-top-growth"),
            shape=RoundedCornerShape(16.dp),
            color=EvoluneTokens.Pale
        ) {
            Box(Modifier.fillMaxSize(),contentAlignment=Alignment.Center) {
                EvoluneGlyph("growth",EvoluneTokens.Forest,Modifier.size(23.dp))
            }
        }
    }
}

@Composable
internal fun EvoluneNotice(message: String, onDismiss: () -> Unit) {
    Surface(
        color=Color(0xFFFFF1E8),shape=RoundedCornerShape(14.dp),
        modifier=Modifier.fillMaxWidth().padding(horizontal=18.dp)
            .semantics { liveRegion=LiveRegionMode.Polite }
    ) {
        Row(Modifier.padding(start=14.dp,top=10.dp,bottom=10.dp,end=5.dp),
            verticalAlignment=Alignment.CenterVertically) {
            Text(message,color=EvoluneTokens.Ink,
                style=EvoluneType.typography.bodyMedium,
                modifier=Modifier.weight(1f),softWrap=true)
            Spacer(Modifier.width(4.dp))
            TextButton(onClick=onDismiss,modifier=Modifier.heightIn(min=48.dp)) {
                Text("Close",color=EvoluneTokens.Forest,
                    style=EvoluneType.typography.labelLarge)
            }
        }
    }
}

@Composable
private fun EvoluneScreenArea(
    onPage: (String)->Unit,
    notice: String?,
    onDismissNotice: ()->Unit,
    content: @Composable ()->Unit
) {
    // Same column/remaining-page structure as the Stage 05 phone shell.
    Column(Modifier.fillMaxSize()) {
        EvoluneTopBar(onGrowth={onPage("growth")})
        if(notice!=null) EvoluneNotice(notice,onDismissNotice)
        Box(Modifier.fillMaxSize()) { content() }
    }
}

/**
 * Keep the proven compact phone layout separate from the wide-screen rail.
 * This avoids an unnecessary weighted Row wrapper around phone page content.
 */
@Composable
internal fun EvoluneChromeShell(
    page: String, onPage: (String) -> Unit,
    notice: String?, onDismissNotice: () -> Unit,
    expandedOverride: Boolean? = null,
    content: @Composable () -> Unit
) {
    val expanded = expandedOverride ?: (LocalConfiguration.current.screenWidthDp >= 700)
    MaterialTheme(
        colorScheme=EvoluneTokens.appColors,
        typography=EvoluneType.typography
    ) {
        Scaffold(
            modifier=Modifier.fillMaxSize(),
            containerColor=EvoluneTokens.Cream,
            bottomBar={if(!expanded) EvoluneBottomNav(page,onPage)}
        ) { padding ->
            if(expanded) {
                Row(Modifier.fillMaxSize().padding(padding)) {
                    EvoluneSideRail(page,onPage)
                    Box(Modifier.weight(1f).fillMaxHeight()) {
                        EvoluneScreenArea(onPage,notice,onDismissNotice,content)
                    }
                }
            } else {
                // Deliberately match the previously tested Scaffold > Column hierarchy.
                Box(Modifier.fillMaxSize().padding(padding)) {
                    EvoluneScreenArea(onPage,notice,onDismissNotice,content)
                }
            }
        }
    }
}
