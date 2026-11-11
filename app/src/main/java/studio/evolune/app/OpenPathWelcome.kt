package studio.evolune.app

import android.content.Context
import android.os.SystemClock
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalMotionDurationScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

/**
 * Open Path first-run state. The Android platform splash remains independent and quick.
 * A monotonic start timestamp persists across Activity recreation so a rotation doesn't
 * replay the 5.8s intro from frame zero. No remote loading, telemetry or private data.
 */
internal object OpenPathFirstRun {
    const val PREF_FILE = "evolune_open_path_entry_v1"
    const val KEY_COMPLETED = "intro_completed"
    const val KEY_STARTED_UPTIME = "started_elapsed_realtime"
    const val FORCE_REDUCED_MOTION_QA = "force_reduced_motion_qa"
    const val FORCE_FULL_MOTION_QA = "force_full_motion_qa"
    const val DURATION_MS = 5_800L

    fun completed(context: Context): Boolean =
        context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
            .getBoolean(KEY_COMPLETED, false)

    fun startAt(context: Context): Long {
        val prefs = context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE)
        val now = SystemClock.elapsedRealtime()
        val stored = prefs.getLong(KEY_STARTED_UPTIME, -1)
        if (stored >= 0 && stored <= now) return stored
        prefs.edit().putLong(KEY_STARTED_UPTIME, now).apply()
        return now
    }

    fun finish(context: Context) {
        context.getSharedPreferences(PREF_FILE, Context.MODE_PRIVATE).edit()
            .putBoolean(KEY_COMPLETED, true).remove(KEY_STARTED_UPTIME).apply()
    }
}

private val ForestDark = Color(0xFF182D23)
private val Paper = Color(0xFFFFFEFA)
private val Brass = Color(0xFFB89A63)

private fun entrance(remainingMs: Long, startMs: Long, durationMs: Long): Float =
    ((remainingMs - startMs).toFloat() / durationMs).coerceIn(0f, 1f)

private fun Path.stoneOne() {
    moveTo(32f, 74f)
    cubicTo(34f,71f,42f,70f,50f,70f)
    cubicTo(61f,69f,72f,70f,77f,73f)
    cubicTo(79f,74.6f,77f,77.5f,71f,79f)
    cubicTo(63f,81f,48f,81f,39f,79f)
    cubicTo(34f,78f,31f,76f,32f,74f)
    close()
}
private fun Path.stoneTwo() {
    moveTo(39f,59f)
    cubicTo(41f,56f,47f,55f,53f,54.5f)
    cubicTo(62f,53.5f,68f,54.5f,72f,56.5f)
    cubicTo(74f,59.2f,69f,61.6f,62f,62.5f)
    cubicTo(51f,64f,42f,63.2f,39f,61.5f)
    cubicTo(38.2f,60.6f,38.4f,60f,39f,59f)
    close()
}
private fun Path.stoneThree() {
    moveTo(46.5f,45f)
    cubicTo(48f,43f,52f,42f,57f,41.5f)
    cubicTo(63f,40.7f,67f,42f,68f,44f)
    cubicTo(69f,46.5f,64f,48.4f,58f,49f)
    cubicTo(52f,49.4f,47f,48f,46.5f,46.5f)
    close()
}

/** Original Stage 01 geometry, not an independently redrawn approximation. */
@Composable
private fun AnimatedOpenPath(progressMs: Long, static: Boolean, modifier: Modifier = Modifier) {
    val stones = remember {
        listOf(Path().apply { stoneOne() }, Path().apply { stoneTwo() }, Path().apply { stoneThree() })
    }
    val starts = remember { listOf(800L, 1_400L, 1_940L) }
    Canvas(modifier.clearAndSetSemantics { }) {
        val scaleFactor = size.minDimension / 108f
        stones.forEachIndexed { index, path ->
            val fraction = if (static) 1f else entrance(progressMs, starts[index], 630L)
            withTransform({
                scale(scaleFactor, scaleFactor, pivot = Offset.Zero)
                translate(top = (1f - fraction) * 8f)
            }) {
                drawPath(path, Paper.copy(alpha = fraction))
            }
        }
        val fraction = if (static) 1f else entrance(progressMs, 2_420L, 850L)
        withTransform({ scale(scaleFactor, scaleFactor, pivot = Offset.Zero) }) {
            drawCircle(Brass.copy(alpha = fraction), radius = 5.1f,
                center = Offset(57.5f, 30.5f))
        }
    }
}

/**
 * No artificial system splash delay. This in-app cinematic is first-run only
 * (or later accessible via a dedicated replay action in V2 Settings).
 * Android "Remove animations" disables the stagger and parallax.
 */
@Composable
internal fun OpenPathWelcome(onFinished: () -> Unit) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val preference = remember(context) {
        context.getSharedPreferences(OpenPathFirstRun.PREF_FILE, Context.MODE_PRIVATE)
    }
    val animationScale = LocalMotionDurationScale.current.scaleFactor
    val fullMotionTestOverride =
        (context.applicationInfo.flags and android.content.pm.ApplicationInfo.FLAG_DEBUGGABLE) != 0 &&
            preference.getBoolean(OpenPathFirstRun.FORCE_FULL_MOTION_QA, false)
    val reducedMotion = !fullMotionTestOverride &&
        (animationScale == 0f ||
            preference.getBoolean(OpenPathFirstRun.FORCE_REDUCED_MOTION_QA, false))
    val startedAt = remember { OpenPathFirstRun.startAt(context) }
    var progressMs by remember(startedAt) {
        mutableLongStateOf(if (reducedMotion) OpenPathFirstRun.DURATION_MS else
            (SystemClock.elapsedRealtime() - startedAt).coerceIn(0L, OpenPathFirstRun.DURATION_MS))
    }
    var finished by remember { mutableStateOf(false) }
    val finishOnce = {
        if (!finished) {
            finished = true
            onFinished()
        }
    }

    BackHandler(enabled = !finished) { finishOnce() }

    LaunchedEffect(startedAt, reducedMotion) {
        if (reducedMotion) {
            progressMs = OpenPathFirstRun.DURATION_MS
            delay(140L)
            finishOnce()
        } else {
            while (true) {
                val elapsed = (SystemClock.elapsedRealtime() - startedAt).coerceAtLeast(0L)
                progressMs = elapsed.coerceAtMost(OpenPathFirstRun.DURATION_MS)
                if (elapsed >= OpenPathFirstRun.DURATION_MS) break
                delay(32L)
            }
            finishOnce()
        }
    }

    val config = LocalConfiguration.current
    val compact = config.screenHeightDp < 670 || config.screenWidthDp < 345
    val symbolSize = if (compact) 148.dp else 174.dp
    val sceneFraction = if (reducedMotion) 1f else entrance(progressMs, 0L, 800L)
    val wordmarkFraction = if (reducedMotion) 1f else entrance(progressMs, 3_100L, 1_140L)
    val taglineFraction = if (reducedMotion) 1f else entrance(progressMs, 3_850L, 1_150L)

    Box(Modifier.fillMaxSize().background(ForestDark)) {
        Image(
            painter = painterResource(R.drawable.stillwater_banner),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().graphicsLayer {
                alpha = sceneFraction
                val scale = if (reducedMotion) 1f else 1.03f - 0.03f * (progressMs / 5_800f)
                scaleX = scale
                scaleY = scale
            }
        )
        Box(Modifier.fillMaxSize().background(Brush.verticalGradient(
            listOf(
                ForestDark.copy(alpha = 0.72f),
                ForestDark.copy(alpha = 0.40f),
                ForestDark.copy(alpha = 0.88f)
            )
        )))
        Column(
            Modifier.fillMaxSize().systemBarsPadding().padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                TextButton(
                    onClick = { finishOnce() },
                    modifier = Modifier.sizeIn(minWidth = 80.dp, minHeight = 48.dp)
                ) {
                    Text("Skip intro", color = Paper, fontSize = 14.sp)
                }
            }
            Spacer(Modifier.weight(1f))
            AnimatedOpenPath(
                progressMs = progressMs,
                static = reducedMotion,
                modifier = Modifier.size(symbolSize)
            )
            Spacer(Modifier.height(if (compact) 2.dp else 8.dp))
            Text(
                "E V O L U N E",
                color = Paper.copy(alpha = wordmarkFraction),
                fontFamily = FontFamily.Serif,
                fontSize = if (compact) 28.sp else 33.sp,
                letterSpacing = 1.2.sp,
                maxLines = 1,
                modifier = Modifier.graphicsLayer {
                    translationY = (1f - wordmarkFraction) * 12f
                }
            )
            Spacer(Modifier.height(11.dp))
            Text(
                "A calmer way to become better.",
                textAlign = TextAlign.Center,
                color = Paper.copy(alpha = taglineFraction),
                fontSize = 14.sp,
                lineHeight = 21.sp,
                modifier = Modifier.graphicsLayer {
                    translationY = (1f - taglineFraction) * 10f
                }
            )
            Spacer(Modifier.weight(1.1f))
            Box(
                Modifier.width(126.dp).height(2.dp)
                    .clip(RoundedCornerShape(50)).background(Paper.copy(alpha = 0.24f))
            ) {
                Box(
                    Modifier.fillMaxWidth(
                        if (reducedMotion) 1f else
                            (progressMs.toFloat() / OpenPathFirstRun.DURATION_MS).coerceIn(0f, 1f)
                    ).fillMaxHeight().background(Paper.copy(alpha = 0.9f))
                )
            }
            Spacer(Modifier.height(24.dp))
        }
    }
}
