package studio.evolune.app

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onAllNodes
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.time.LocalDate
import java.time.ZoneId
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import studio.evolune.data.ChallengeEntity
import studio.evolune.data.JournalEntryEntity
import studio.evolune.data.ChallengeSnapshot
import studio.evolune.domain.ChallengePlan
import studio.evolune.domain.MilestoneKind
import studio.evolune.domain.Recurrence
import studio.evolune.domain.ScheduleVersion

/** Synthetic sample screens; no real customer data is read or uploaded. */
@RunWith(AndroidJUnit4::class)
class Stage05ScreenFamilyTest {
    @get:Rule val rule = createComposeRule()

    private fun snapshot(): ChallengeSnapshot {
        val start = LocalDate.now(ZoneId.of("Asia/Kolkata")).minusDays(2)
        return ChallengeSnapshot(
            ChallengePlan("stage05-synthetic", "Read every evening", start,
                ZoneId.of("Asia/Kolkata"), MilestoneKind.CALENDAR_DAYS,
                21, listOf(ScheduleVersion(start, Recurrence.Daily, "Read ten pages"))),
            emptyList(), emptyList(), null
        )
    }

    private fun screenshot(name: String) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "evolune_qa_stage05_" + name + ".png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/EVOLUNE-QA")
        }
        val uri = requireNotNull(context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values))
        val ok = context.contentResolver.openOutputStream(uri)?.use { stream ->
            rule.onRoot().captureToImage().asAndroidBitmap().compress(
                Bitmap.CompressFormat.PNG, 100, stream)
        } ?: false
        assertTrue("Screenshot capture failed for $name", ok)
    }

    @Test fun emptyChallengesInvitesAnActualCreateAction() {
        var created = false
        rule.setContent {
            ChallengeListPage(emptyList(), null,
                onSelect = {}, onCreate = { created = true })
        }
        rule.onNodeWithText("The changes you choose.").assertExists()
        rule.onNodeWithText("Nothing scheduled yet").assertExists()
        screenshot("challenges_empty")
        rule.onNodeWithText("New challenge").performClick()
        assertTrue(created)
    }

    @Test fun chosenChallengeOpensRealDetailRoute() {
        var opened: String? = null
        val ch = ChallengeEntity(
            id = "stage05-synthetic", growthAreaId = null, title = "Read every evening",
            startDate = "2026-11-11", zoneId = "Asia/Kolkata",
            milestoneKind = MilestoneKind.CALENDAR_DAYS.name,
            target = 21, createdAtEpochMs = 0L
        )
        rule.setContent {
            ChallengeListPage(listOf(ch), ch.id,
                onSelect = { opened = it }, onCreate = {})
        }
        rule.onNodeWithText("CURRENTLY SELECTED").assertExists()
        screenshot("challenges_populated")
        rule.onNodeWithText("View active challenge").performScrollTo().performClick()
        assertEquals(ch.id, opened)
    }

    @Test fun detailIncludesExistingReminderAndReviewActions() {
        var reminders = false
        var reviewed = false
        rule.setContent {
            ChallengeDetailPage(snapshot(), onAdjust = {},
                onReview = { reviewed = true }, onBack = {}, onLog = {},
                onReminders = { reminders = true })
        }
        rule.onNodeWithText("YOUR RECORD SO FAR").assertExists()
        screenshot("detail")
        rule.onNodeWithText("Gentle reminders").performScrollTo().performClick()
        assertTrue(reminders)
        rule.onNodeWithText("Milestone review").performScrollTo().performClick()
        assertTrue(reviewed)
    }

    @Test fun recoveryNeverChangesHistoryUntilSaveAndReviewIsFactual() {
        var saved = false
        var paused = false
        rule.setContent {
            RecoveryPage(snapshot(),onBack = {},
                onRevise = { _, _ -> saved = true },
                onPause = { paused = true })
        }
        rule.onNodeWithText("Adjust the path.").assertExists()
        screenshot("recovery")
        assertTrue(!saved && !paused)
        rule.onNodeWithText("Save future plan").performScrollTo().performClick()
        assertTrue(saved)
    }

    @Test fun reviewSeparatesUnknownAndExplicitlyMissedStates() {
        rule.setContent { ReviewPage(snapshot(), onBack = {}) }
        rule.onNodeWithText("Every phase has meaning.").assertExists()
        rule.onNodeWithText("Unreported isn't missed").assertExists()
        screenshot("review")
    }
    @Test fun growthAreaCreationUsesActualCallback() {
        var chosen: String? = null
        rule.setContent {
            GrowthPage(emptyList(),onCreateArea={ chosen=it },onStart={})
        }
        rule.onNodeWithText("Your direction, your pace.").assertExists()
        screenshot("growth_empty")
        rule.onAllNodes(hasSetTextAction())[0].performTextInput("Learning")
        rule.onNodeWithText("Add area").performScrollTo().performClick()
        assertEquals("Learning",chosen)
    }

    @Test fun journalRequiresExplicitConfirmationBeforeDeletion() {
        val entry=JournalEntryEntity(
            id="stage05-private",body="An optional journal note",
            createdAtEpochMs=0L,updatedAtEpochMs=0L
        )
        var deleted=false
        rule.setContent {
            JournalPage(listOf(entry),onSave={_,_,_,_->},
                onDelete={deleted=true},onDataPrivacy={})
        }
        rule.onNodeWithText("An optional journal note").assertExists()
        screenshot("journal")
        rule.onNodeWithText("Delete").performScrollTo().performClick()
        rule.onNodeWithText("Delete this reflection?").assertExists()
        assertFalse(deleted)
        rule.onNodeWithText("Keep reflection").performClick()
        assertFalse(deleted)
        rule.onNodeWithText("Delete").performScrollTo().performClick()
        rule.onNodeWithText("Delete reflection").performClick()
        assertTrue(deleted)
    }

}
