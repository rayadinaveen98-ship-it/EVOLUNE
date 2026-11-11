package studio.evolune.app

import androidx.compose.material3.Text
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.test.core.app.ApplicationProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.assertIsNotSelected
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Native semantic/route tests using synthetic content only. */
@RunWith(AndroidJUnit4::class)
class Stage06ChromeTest {
    @get:Rule val rule = createComposeRule()

    private fun saveFrame(name: String) {
        val context=ApplicationProvider.getApplicationContext<Context>()
        val values=ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME,"evolune_qa_stage06_"+name+".png")
            put(MediaStore.Images.Media.MIME_TYPE,"image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH,
                Environment.DIRECTORY_PICTURES+"/EVOLUNE-QA")
        }
        val uri=requireNotNull(context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,values))
        val ok=context.contentResolver.openOutputStream(uri)?.use { stream ->
            rule.onRoot().captureToImage().asAndroidBitmap()
                .compress(Bitmap.CompressFormat.PNG,100,stream)
        } ?: false
        assertTrue("Stage 06 native screenshot failed",ok)
    }

    @Test fun parentTabSelectionRemainsCorrectForEveryNestedScreen() {
        assertEquals("challenges",selectedEvoluneDestination("detail"))
        assertEquals("challenges",selectedEvoluneDestination("create"))
        assertEquals("challenges",selectedEvoluneDestination("recovery"))
        assertEquals("challenges",selectedEvoluneDestination("review"))
        assertEquals("challenges",selectedEvoluneDestination("reminders"))
        assertEquals("journal",selectedEvoluneDestination("data"))
        assertEquals("today",selectedEvoluneDestination("unrecognized"))
    }

    @Test fun bottomNavigationWorksWithRealRouteCallbacksAndSelectedState() {
        var destination by mutableStateOf("today")
        rule.setContent {
            EvoluneChromeShell(
                page=destination,
                onPage={destination=it},
                notice=null,
                onDismissNotice={},
                expandedOverride=false
            ) { Text("Screen: $destination") }
        }
        rule.onNodeWithTag("evolune-nav-today").assertIsSelected()
        saveFrame("phone_tabs")
        rule.onNodeWithTag("evolune-nav-challenges").performClick()
        rule.waitForIdle()
        assertEquals("challenges",destination)
        rule.onNodeWithTag("evolune-nav-challenges").assertIsSelected()
        rule.onNodeWithTag("evolune-nav-today").assertIsNotSelected()
        rule.onNodeWithText("Screen: challenges").assertExists()
        rule.onNodeWithTag("evolune-nav-journal").performClick()
        rule.waitForIdle()
        assertEquals("journal",destination)
    }

    @Test fun tabletRailOffersSameDestinationsAndPreservesSubpageSelection() {
        var chosen by mutableStateOf<String?>(null)
        rule.setContent {
            EvoluneChromeShell(
                page="recovery",
                onPage={chosen=it},
                notice=null,
                onDismissNotice={},
                expandedOverride=true
            ) { Text("Recovery detail") }
        }
        rule.onNodeWithTag("evolune-nav-challenges").assertIsSelected()
        saveFrame("expanded_rail_simulated")
        rule.onNodeWithTag("evolune-nav-growth").performClick()
        assertEquals("growth",chosen)
        rule.onNodeWithText("Recovery detail").assertExists()
    }

    @Test fun headerShortcutAndNoticesRemainActionable() {
        var opened=false
        var dismissed=false
        rule.setContent {
            EvoluneChromeShell(
                page="today",onPage={if(it=="growth")opened=true},
                notice="Could not save. Your existing records were not changed.",
                onDismissNotice={dismissed=true},
                expandedOverride=false
            ) { Text("Today content") }
        }
        rule.onNodeWithTag("evolune-top-growth").performClick()
        rule.onNodeWithText("Close").performClick()
        assertTrue(opened)
        assertTrue(dismissed)
    }

    @Test fun sharedButtonsHaveGenuineEnabledAndDisabledStates() {
        var count=0
        rule.setContent {
            androidx.compose.foundation.layout.Column {
                EvoluneAction("Save practice",onClick={count++})
                EvoluneAction("Cancel",onClick={count++},
                    style=EvoluneActionStyle.Quiet,enabled=false)
            }
        }
        rule.onNodeWithText("Save practice").assertIsEnabled().performClick()
        rule.onNodeWithText("Cancel").assertIsNotEnabled()
        assertEquals(1,count)
    }
    @Test fun sharedInputAndChoiceKeepRealValuesAndSelection() {
        var area by mutableStateOf("")
        var selected by mutableStateOf(false)
        rule.setContent {
            androidx.compose.foundation.layout.Column {
                EvoluneInput(value=area,onValueChange={area=it},label="Area name")
                EvoluneChoice(selected=selected,onClick={selected=!selected},label="Learning")
            }
        }
        rule.onAllNodes(hasSetTextAction())[0].performTextInput("Personal learning")
        assertEquals("Personal learning",area)
        rule.onNodeWithText("Learning").performClick()
        assertTrue(selected)
    }

}
