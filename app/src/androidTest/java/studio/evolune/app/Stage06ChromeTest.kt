package studio.evolune.app

import androidx.compose.material3.Text
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
}
