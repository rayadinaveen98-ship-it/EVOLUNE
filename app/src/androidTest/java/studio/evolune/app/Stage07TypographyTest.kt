package studio.evolune.app

import android.content.ContentValues
import android.content.Context
import android.content.res.Configuration
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.time.LocalDate
import java.time.LocalTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Synthetic QA: forces a logical 320dp content column and a 100%-200% system
 * font-scale configuration including Compose LocalDensity scaling. Real physical
 * device and TalkBack still require
 * Stage 10 verification; this does not claim to simulate OEM rendering exactly.
 */
@RunWith(AndroidJUnit4::class)
class Stage07TypographyTest {
    @get:Rule val rule=createComposeRule()

    private fun config(width: Int, scale: Float) =
        Configuration(ApplicationProvider.getApplicationContext<Context>()
            .resources.configuration).apply {
            screenWidthDp=width
            screenHeightDp=650
            fontScale=scale
        }

    private fun capture(name: String) {
        val context=ApplicationProvider.getApplicationContext<Context>()
        val metadata=ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME,"evolune_qa_stage07_$name.png")
            put(MediaStore.Images.Media.MIME_TYPE,"image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES+"/EVOLUNE-QA")
        }
        val uri=requireNotNull(context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,metadata))
        val ok=context.contentResolver.openOutputStream(uri)?.use { stream ->
            rule.onRoot().captureToImage().asAndroidBitmap()
                .compress(Bitmap.CompressFormat.PNG,100,stream)
        } ?: false
        assertTrue("Missing Stage07 native screenshot $name",ok)
    }

    @Test fun narrowPhone100PercentTitleAndActionRemainUsable() {
        var created=false
        val cfg=config(320,1f)
        rule.setContent {
            val nativeDensity=LocalDensity.current
            CompositionLocalProvider(
                LocalConfiguration provides cfg,
                LocalDensity provides Density(nativeDensity.density,1f)
            ) {
                Box(Modifier.width(320.dp).height(650.dp)) {
                    EvoluneChromeShell("today",onPage={},notice=null,
                        onDismissNotice={},expandedOverride=false) {
                        TodayPage(null,onCreate={created=true},
                            onOpen={},onLog={},onAdjust={},onJournal={})
                    }
                }
            }
        }
        rule.onNodeWithText("A gentle day",substring=true).assertIsDisplayed()
        rule.onNodeWithTag("evolune-nav-today").assertIsSelected()
        capture("320dp_font100")
        rule.onNodeWithText("Create your first challenge").performScrollTo().performClick()
        assertTrue(created)
    }

    @Test fun narrowPhone200PercentKeepsMainContentAndAllNavigationReachable() {
        var chosen: String?=null
        var created=false
        val cfg=config(320,2f)
        rule.setContent {
            val nativeDensity=LocalDensity.current
            CompositionLocalProvider(
                LocalConfiguration provides cfg,
                LocalDensity provides Density(nativeDensity.density,2f)
            ) {
                Box(Modifier.width(320.dp).height(650.dp)) {
                    EvoluneChromeShell("today",onPage={chosen=it},
                        notice=null,onDismissNotice={},expandedOverride=false) {
                        TodayPage(null,onCreate={created=true},
                            onOpen={},onLog={},onAdjust={},onJournal={})
                    }
                }
            }
        }
        rule.onNodeWithText("A gentle day",substring=true).assertIsDisplayed()
        rule.onNodeWithTag("evolune-nav-today").assertIsSelected()
        rule.onNodeWithTag("evolune-nav-insights").assertIsDisplayed()
        capture("320dp_font200")
        rule.onNodeWithTag("evolune-nav-insights").performClick()
        assertEquals("insights",chosen)
        rule.onNodeWithText("Create your first challenge").performScrollTo().performClick()
        assertTrue(created)
    }

    @Test fun expandedRailAndTextKeepSameSemanticDestinationNames() {
        var route:String?=null
        val cfg=config(853,1.4f)
        rule.setContent {
            val nativeDensity=LocalDensity.current
            CompositionLocalProvider(
                LocalConfiguration provides cfg,
                LocalDensity provides Density(nativeDensity.density,1.4f)
            ) {
                Box(Modifier.width(392.dp).height(700.dp)) {
                    EvoluneChromeShell(page="recovery",
                        onPage={route=it},notice=null,
                        onDismissNotice={},expandedOverride=true) {
                        Text("Future plan screen")
                    }
                }
            }
        }
        rule.onNodeWithTag("evolune-nav-challenges").assertIsSelected()
        rule.onNodeWithTag("evolune-nav-growth").assertIsDisplayed()
        capture("rail_font140_simulated")
        rule.onNodeWithTag("evolune-nav-growth").performClick()
        assertEquals("growth",route)
    }

    @Test fun formattingUsesCalendarDatesAndARealClockWithoutAlteringStoredData() {
        val date=LocalDate.of(2026,11,11)
        val time=LocalTime.of(19,30)
        assertEquals("2026-11-11",EvoluneFormatting.date(date,"yyyy-MM-dd"))
        assertTrue(EvoluneFormatting.clock(time).isNotBlank())
        assertEquals("A PATH",EvoluneFormatting.uppercase("a path"))
    }
}
