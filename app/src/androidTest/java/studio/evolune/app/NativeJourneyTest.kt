package studio.evolune.app

import android.graphics.Bitmap
import android.content.ContentValues
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
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

    /** MediaStore survives Android test cleanup, unlike app-private filesDir. QA has only synthetic data. */
    private fun captureNative(name: String) {
        check(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            "MediaStore QA screenshot export requires Android 10 or later"
        }
        val context = rule.activity
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "evolune_qa_$name.png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/EVOLUNE-QA")
        }
        val uri = requireNotNull(context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)) {
            "MediaStore refused screenshot destination for $name"
        }
        val written = context.contentResolver.openOutputStream(uri)?.use { stream ->
            rule.onRoot().captureToImage().asAndroidBitmap()
                .compress(Bitmap.CompressFormat.PNG, 100, stream)
        } ?: false
        check(written) { "Screenshot write failed for $name" }
    }

    @Test fun mainDestinationsAreNavigable() {
        rule.onNodeWithText("A gentle day", substring = true).assertIsDisplayed()
        captureNative("today")

        rule.onNodeWithText("Challenges").performClick()
        rule.onNodeWithText("The changes you choose.").assertExists()
        captureNative("challenges")

        rule.onNodeWithText("My Growth").performClick()
        rule.onNodeWithText("Your direction, your pace.").assertExists()
        captureNative("growth")

        rule.onNodeWithText("Insights").performClick()
        rule.onNodeWithText("See what", substring=true).assertExists()
        captureNative("insights")

        rule.onNodeWithText("Journal").performClick()
        rule.onNodeWithText("Room to feel.", substring=true).assertExists()
        captureNative("journal")

        rule.onNodeWithText("Today").performClick()
        rule.onNodeWithText("A gentle day", substring = true).assertExists()
    }


    @Test fun newChallengeCreatesRealDetailRecoveryReviewAndTodayScreens() {
        val marker = "Visual QA breathing " + System.nanoTime()
        rule.onNodeWithText("Challenges").performClick()
        rule.onNodeWithText("New challenge", substring=true).performClick()
        captureNative("challenge_create")
        // First two editable fields on this view are title and action.
        rule.onAllNodes(hasSetTextAction())[0].performTextInput(marker)
        rule.onAllNodes(hasSetTextAction())[1].performTextInput("Breathe slowly for three minutes")
        rule.onNodeWithText("Create my challenge").performScrollTo().performClick()
        rule.waitUntil(timeoutMillis=15000) {
            rule.onAllNodesWithText(marker,substring=true).fetchSemanticsNodes().isNotEmpty()
        }
        captureNative("challenge_detail_populated")
        rule.onNodeWithText("Adjust plan").performScrollTo().performClick()
        rule.onNodeWithText("Adjust the path.").assertExists()
        captureNative("recovery_populated")
        rule.onNodeWithText("Back without changes").performScrollTo().performClick()
        rule.onNodeWithText("Milestone review").performScrollTo().performClick()
        rule.onNodeWithText("Every phase has meaning.").assertExists()
        captureNative("review_populated")
        rule.onNodeWithText("Today").performClick()
        rule.onNodeWithText("A gentle day",substring=true).assertExists()
        captureNative("today_populated")
    }


    @Test fun privateJournalEntrySurvivesActivityRecreation() {
        val marker = "EVOLUNE device QA " + System.nanoTime()
        rule.onNodeWithText("Journal").performClick()
        rule.onNodeWithText("New reflection",substring=true).performClick()
        rule.onAllNodes(hasSetTextAction())[0].performTextInput(marker)
        rule.onNodeWithText("Save reflection").performScrollTo().performClick()

        rule.waitUntil(timeoutMillis = 15000) {
            // The journal list re-renders only after the local database write completes.
            rule.onAllNodesWithText(marker).fetchSemanticsNodes().isNotEmpty()
        }
        rule.activityRule.scenario.recreate()
        rule.waitUntil(timeoutMillis = 15000) {
            rule.onAllNodesWithText("Room to feel.", substring=true).fetchSemanticsNodes().isNotEmpty()
        }
        rule.onNodeWithText(marker).assertExists()
    }
}
