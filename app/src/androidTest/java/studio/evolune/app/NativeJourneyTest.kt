package studio.evolune.app

import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onAllNodes
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/** Native UI smoke checks, intended for an Android emulator, not a desktop JVM. */
@RunWith(AndroidJUnit4::class)
class NativeJourneyTest {
    @get:Rule val rule = createAndroidComposeRule<MainActivity>()

    @Test fun mainDestinationsAreNavigable() {
        rule.onNodeWithText("Make room for growth.").assertIsDisplayed()

        rule.onNodeWithText("Challenges").performClick()
        rule.onNodeWithText("The changes you choose.").assertExists()

        rule.onNodeWithText("Growth").performClick()
        rule.onNodeWithText("Your direction, your pace.").assertExists()

        rule.onNodeWithText("Insights").performClick()
        rule.onNodeWithText("Look at what is real.").assertExists()

        rule.onNodeWithText("Journal").performClick()
        rule.onNodeWithText("The living journal.").assertExists()

        rule.onNodeWithText("Today").performClick()
        rule.onNodeWithText("Make room for growth.").assertExists()
    }

    @Test fun privateJournalEntrySurvivesActivityRecreation() {
        val marker = "EVOLUNE device QA " + System.nanoTime()
        rule.onNodeWithText("Journal").performClick()
        rule.onAllNodes(hasSetTextAction())[0].performTextInput(marker)
        rule.onNodeWithText("Save reflection").performScrollTo().performClick()

        rule.waitUntil(timeoutMillis = 15000) {
            // The journal list re-renders only after the local database write completes.
            rule.onAllNodesWithText(marker).fetchSemanticsNodes().isNotEmpty()
        }
        rule.activityRule.scenario.recreate()
        rule.waitUntil(timeoutMillis = 15000) {
            rule.onAllNodesWithText("The living journal.").fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithText(marker).assertExists()
    }
}
