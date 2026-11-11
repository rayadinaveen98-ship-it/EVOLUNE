package studio.evolune.app

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicReference
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import studio.evolune.domain.MilestoneKind
import studio.evolune.domain.Recurrence

/** Synthetic content. Tests native V2 guided UI independently of the user's data. */
@RunWith(AndroidJUnit4::class)
class GuidedSetupTest {
    @get:Rule val rule = createComposeRule()

    private fun saveFrame(name: String) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "evolune_qa_setup_" + name + ".png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/EVOLUNE-QA")
        }
        val uri = requireNotNull(context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values))
        val ok = context.contentResolver.openOutputStream(uri)?.use { output ->
            rule.onRoot().captureToImage().asAndroidBitmap()
                .compress(Bitmap.CompressFormat.PNG, 100, output)
        } ?: false
        assertTrue("Failed to capture guided setup frame", ok)
    }

    @Test fun guidedSetupShowsFourRealStepsAndReturnsExistingEngineChoices() {
        val submission = AtomicReference<GuidedSetupRequest?>(null)
        rule.setContent {
            GuidedSetup(areas = emptyList(), busy = false, error = null,
                onSkip = { fail("Should not skip") },
                onCreate = { submission.set(it) })
        }
        rule.onNodeWithText("Choose where you'd like to grow.").assertExists()
        saveFrame("growth_area")
        rule.onNodeWithText("Learning & craft").performScrollTo().performClick()
        rule.onNodeWithText("Continue").performClick()
        rule.onNodeWithText("Begin with one small practice.").assertExists()
        saveFrame("practice")
        rule.onAllNodes(hasSetTextAction())[0].performTextInput("Read quietly")
        rule.onAllNodes(hasSetTextAction())[1].performTextInput("Read 10 minutes after dinner")
        rule.onNodeWithText("Continue to rhythm").performClick()
        rule.onNodeWithText("Find a rhythm that fits.").assertExists()
        saveFrame("rhythm")
        rule.onNodeWithText("Completed sessions").performScrollTo().performClick()
        rule.onNodeWithText("Weekdays").performScrollTo().performClick()
        rule.onNodeWithText("Add optional details").performScrollTo().performClick()
        rule.onAllNodes(hasSetTextAction())[1].performTextInput("19:30")
        rule.onAllNodes(hasSetTextAction())[2].performTextInput("Read one page")
        rule.onNodeWithText("Review my path").performClick()
        rule.onNodeWithText("Your first path is ready.").assertExists()
        saveFrame("review")
        rule.onNodeWithText("Begin my path").performClick()
        val result = requireNotNull(submission.get()) { "Expected a real setup request" }
        assertEquals("Learning & craft", result.areaName)
        assertEquals("Read quietly", result.title)
        assertEquals("Read 10 minutes after dinner", result.action)
        assertEquals(21, result.target)
        assertEquals(MilestoneKind.COMPLETED_SESSIONS, result.milestoneKind)
        assertEquals(Recurrence.Weekdays, result.recurrence)
        assertEquals("19:30", result.cueTime.toString())
        assertEquals("Read one page", result.fallbackAction)
    }

    @Test fun setupIsVoluntaryAndSkippable() {
        val skipped = AtomicBoolean(false)
        rule.setContent {
            GuidedSetup(emptyList(), false, null,
                onSkip = { skipped.set(true) }, onCreate = { fail("Must not save") })
        }
        rule.onNodeWithText("Explore first").performClick()
        assertTrue(skipped.get())
    }

    @Test fun missingActionCannotAdvanceAndCueDoesNotGrantNotificationConsent() {
        rule.setContent {
            GuidedSetup(emptyList(), false, null,
                onSkip = {}, onCreate = { fail("Must not save") })
        }
        rule.onNodeWithText("Continue").performClick()
        rule.onNodeWithText("Continue to rhythm").performClick()
        rule.onNodeWithText("Add a title and one repeatable action.").assertExists()
        rule.onAllNodes(hasSetTextAction())[0].performTextInput("Small practice")
        rule.onAllNodes(hasSetTextAction())[1].performTextInput("Walk five minutes")
        rule.onNodeWithText("Continue to rhythm").performClick()
        rule.onNodeWithText("Add optional details").performScrollTo().performClick()
        rule.onNodeWithText("A cue time does not turn on notifications.", substring = true).assertExists()
    }
}
