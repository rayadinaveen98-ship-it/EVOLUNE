package studio.evolune.app

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.assertExists
import androidx.compose.ui.test.captureToImage
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import studio.evolune.data.ChallengeSnapshot
import studio.evolune.domain.ChallengePlan
import studio.evolune.domain.MilestoneKind
import studio.evolune.domain.OpportunityState
import studio.evolune.domain.PauseWindow
import studio.evolune.domain.PracticeOutcome
import studio.evolune.domain.PracticeRecord
import studio.evolune.domain.Recurrence
import studio.evolune.domain.ScheduleVersion

@RunWith(AndroidJUnit4::class)
class TodayStage04Test {
    @get:Rule val rule = createComposeRule()
    private val zone = ZoneId.of("Asia/Kolkata")
    private val fixedDate = LocalDate.of(2026, 11, 11)
    private val fixedNow = ZonedDateTime.of(2026, 11, 11, 12, 0, 0, 0, zone)

    private fun snapshot(
        start: LocalDate = fixedDate.minusDays(2),
        recurrence: Recurrence = Recurrence.Daily,
        kind: MilestoneKind = MilestoneKind.CALENDAR_DAYS,
        target: Int = 21,
        outcomes: Map<LocalDate, PracticeOutcome> = emptyMap(),
        pauses: List<PauseWindow> = emptyList()
    ): ChallengeSnapshot {
        val plan = ChallengePlan(
            id = "synthetic-v2-today", title = "Read each evening",
            startDate = start, zone = zone,
            milestoneKind = kind, target = target,
            schedules = listOf(ScheduleVersion(start, recurrence, "Read ten pages"))
        )
        val records = outcomes.entries.map { (day, outcome) ->
            PracticeRecord(plan.id, day, outcome, 1,
                Instant.parse("2026-11-11T05:00:00Z"))
        }
        return ChallengeSnapshot(plan, records, pauses, null)
    }

    @Test fun emptyAndScheduledStatesDoNotInventProgress() {
        assertEquals(TodayMode.EMPTY, presentToday(null, fixedNow).mode)
        val view = presentToday(snapshot(), fixedNow)
        assertEquals(TodayMode.SCHEDULED, view.mode)
        assertEquals(OpportunityState.SCHEDULED, view.outcome)
        assertEquals(2, view.unreportedEarlier)
        assertEquals("Day 3 of 21 calendar days", view.milestone)
        assertEquals(fixedDate.plusDays(1), view.nextScheduled)
    }

    @Test fun allRecordedOutcomesRemainDistinctAndRevisable() {
        val expected = mapOf(
            PracticeOutcome.COMPLETED to OpportunityState.COMPLETED,
            PracticeOutcome.PARTIAL to OpportunityState.PARTIAL,
            PracticeOutcome.RESTED to OpportunityState.RESTED,
            PracticeOutcome.MISSED to OpportunityState.MISSED,
            PracticeOutcome.COMPLETED_BACKUP to OpportunityState.COMPLETED_BACKUP
        )
        expected.forEach { (outcome, state) ->
            val view = presentToday(snapshot(outcomes = mapOf(fixedDate to outcome)), fixedNow)
            assertEquals(TodayMode.RECORDED, view.mode)
            assertEquals(state, view.outcome)
            assertEquals(2, view.unreportedEarlier)
        }
    }

    @Test fun pausesNonScheduledAndEndedMilestonesAreNotLoggable() {
        val paused = presentToday(snapshot(pauses = listOf(
            PauseWindow(fixedDate, fixedDate.plusDays(1)))), fixedNow)
        assertEquals(TodayMode.PAUSED, paused.mode)
        assertEquals(fixedDate.plusDays(2), paused.nextScheduled)

        val restDay = presentToday(snapshot(recurrence = Recurrence.Weekends), fixedNow)
        assertEquals(TodayMode.REST_DAY, restDay.mode)
        assertEquals(fixedDate.plusDays(3), restDay.nextScheduled)

        val ended = presentToday(snapshot(start = fixedDate.minusDays(5), target = 3), fixedNow)
        assertEquals(TodayMode.MILESTONE_ENDED, ended.mode)
        assertNull(ended.nextScheduled)

        val upcoming = presentToday(snapshot(start = fixedDate.plusDays(2)), fixedNow)
        assertEquals(TodayMode.STARTS_LATER, upcoming.mode)
        assertEquals(fixedDate.plusDays(2), upcoming.nextScheduled)
    }

    @Test fun sessionMilestoneCountsOnlyFullLoggedSessions() {
        val view = presentToday(snapshot(kind = MilestoneKind.COMPLETED_SESSIONS,
            outcomes = mapOf(
                fixedDate.minusDays(2) to PracticeOutcome.COMPLETED_BACKUP,
                fixedDate.minusDays(1) to PracticeOutcome.COMPLETED
            )), fixedNow)
        assertEquals("1 of 21 full sessions recorded", view.milestone)
    }

    private fun saveFrame(label: String) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val values = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, "evolune_qa_today_v2_" + label + ".png")
            put(MediaStore.Images.Media.MIME_TYPE, "image/png")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/EVOLUNE-QA")
        }
        val uri = requireNotNull(context.contentResolver.insert(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values
        ))
        val saved = context.contentResolver.openOutputStream(uri)?.use { stream ->
            rule.onRoot().captureToImage().asAndroidBitmap()
                .compress(Bitmap.CompressFormat.PNG, 100, stream)
        } ?: false
        assertTrue("Today capture failed", saved)
    }

    @Test fun realTodayActionsOpenRecordEditorAndRouteToJournal() {
        var log: PracticeOutcome? = null
        var journalOpened = false
        rule.setContent {
            TodayPage(snapshot(start = LocalDate.now(zone)),
                onCreate = {}, onOpen = {}, onLog = { log = it },
                onAdjust = {}, onJournal = { journalOpened = true })
        }
        rule.onNodeWithText("A gentle day", substring = true).assertExists()
        rule.onNodeWithText("Record today's practice").assertExists()
        saveFrame("scheduled")
        rule.onNodeWithText("Record today's practice").performScrollTo().performClick()
        rule.onNodeWithText("Did part of it").performScrollTo().performClick()
        assertEquals(PracticeOutcome.PARTIAL, log)
        rule.onNodeWithText("A moment to reflect").performScrollTo().performClick()
        assertTrue(journalOpened)
    }

    @Test fun recordedAndEmptyTodayScreensHaveHonestCallsToAction() {
        rule.setContent {
            TodayPage(null, onCreate = {}, onOpen = {},
                onLog = {}, onAdjust = {}, onJournal = {})
        }
        rule.onNodeWithText("Create your first challenge").assertExists()
        saveFrame("empty")
    }
}
