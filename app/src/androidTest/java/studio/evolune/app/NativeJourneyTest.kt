package studio.evolune.app

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onRoot
import java.io.File
import java.io.FileOutputStream
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

    private fun captureNative(name: String) {
        val dir = File(rule.activity.filesDir, "evolune-qa")
        check(dir.exists() || dir.mkdirs()) { "Failed to create QA screenshot directory" }
        val file = File(dir, "$name.png")
        FileOutputStream(file).use { out ->
            check(rule.onRoot().captureToImage().asAndroidBitmap()
                .compress(Bitmap.CompressFormat.PNG, 100, out)) { "Failed to capture $name" }
        }
    }

    @Test fun mainDestinationsAreNavigable() {
        rule.onNodeWithText("Make room for growth.").assertIsDisplayed()
        captureNative("today")

        rule.onNodeWithText("Challenges").performClick()
        rule.onNodeWithText("The changes you choose.").assertExists()
        captureNative("challenges")

        rule.onNodeWithText("Growth").performClick()
        rule.onNodeWithText("Your direction, your pace.").assertExists()
        captureNative("growth")

        rule.onNodeWithText("Insights").performClick()
        rule.onNodeWithText("Look at what is real.").assertExists()
        captureNative("insights")

        rule.onNodeWithText("Journal").performClick()
        rule.onNodeWithText("The living journal.").assertExists()
        captureNative("journal")

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
