package studio.evolune.app

import android.Manifest
import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.Instant
import java.util.UUID
import studio.evolune.data.EvoluneDatabase
import studio.evolune.data.RoomEvoluneRepository
import studio.evolune.domain.*

/** Actual AlarmManager scheduling and cancellation; synthetic challenge, permission grant for QA only. */
@RunWith(AndroidJUnit4::class)
class ReminderSchedulerTest {
    @Test fun explicitOptInSchedulesAndDisableCancels() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val id = UUID.randomUUID().toString()
        val db = Room.inMemoryDatabaseBuilder(context,EvoluneDatabase::class.java).build()
        try {
            val zone = ZoneId.of("Asia/Kolkata")
            val now = Instant.now().atZone(zone)
            val start = LocalDate.now(zone)
            RoomEvoluneRepository(db.evoluneDao()).createChallenge(
                ChallengePlan(id,"Gentle read",start,zone,
                    MilestoneKind.CALENDAR_DAYS,21,
                    listOf(ScheduleVersion(start,Recurrence.Daily,"Read quietly")))
            )
            ReminderScheduler.reschedule(context,db.evoluneDao(),id)
            assertFalse(ReminderScheduler.isOptedIn(context,id))
            assertEquals(-1L,ReminderScheduler.scheduledAt(context,id))

            InstrumentationRegistry.getInstrumentation().uiAutomation
                .grantRuntimePermission(context.packageName,Manifest.permission.POST_NOTIFICATIONS)
            val chosen = LocalTime.of(20,45)
            val scheduled=ReminderScheduler.enable(context,db.evoluneDao(),id,chosen)
            assertTrue("Future practice should produce a scheduled inexact alarm",scheduled)
            assertTrue(ReminderScheduler.isOptedIn(context,id))
            assertEquals(chosen,ReminderScheduler.time(context,id))
            assertTrue(ReminderScheduler.scheduledAt(context,id)>System.currentTimeMillis())

            ReminderScheduler.disable(context,id)
            assertFalse(ReminderScheduler.isOptedIn(context,id))
            assertEquals(-1L,ReminderScheduler.scheduledAt(context,id))
        } finally {
            ReminderScheduler.disable(context,id)
            db.close()
        }
    }
    @Test fun optedInAlarmReconstructsAfterDatabaseReopenAndStopsAfterChallengeDeletion() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val id = UUID.randomUUID().toString()
        val databaseName = "evolune-reminder-restart-$id.db"
        val zone = ZoneId.of("Asia/Kolkata")
        val date = LocalDate.now(zone)
        val chosen = LocalTime.now(zone).plusHours(3).withSecond(0).withNano(0)
        InstrumentationRegistry.getInstrumentation().uiAutomation
            .grantRuntimePermission(context.packageName,Manifest.permission.POST_NOTIFICATIONS)
        var db = Room.databaseBuilder(context,EvoluneDatabase::class.java,databaseName).build()
        try {
            RoomEvoluneRepository(db.evoluneDao()).createChallenge(
                ChallengePlan(id,"Restart reminder check",date,zone,
                    MilestoneKind.CALENDAR_DAYS,21,
                    listOf(ScheduleVersion(date,Recurrence.Daily,"Practice gently")))
            )
            assertTrue(ReminderScheduler.enable(context,db.evoluneDao(),id,chosen))
            val first=ReminderScheduler.scheduledAt(context,id)
            assertTrue(first > System.currentTimeMillis())
            db.close()

            db = Room.databaseBuilder(context,EvoluneDatabase::class.java,databaseName).build()
            ReminderScheduler.rescheduleAll(context,db.evoluneDao())
            assertTrue(ReminderScheduler.isOptedIn(context,id))
            assertTrue(ReminderScheduler.scheduledAt(context,id)>System.currentTimeMillis())

            db.evoluneDao().deleteChallenge(id)
            ReminderScheduler.rescheduleAll(context,db.evoluneDao())
            assertFalse(ReminderScheduler.isOptedIn(context,id))
            assertEquals(-1L,ReminderScheduler.scheduledAt(context,id))
        } finally {
            ReminderScheduler.disable(context,id)
            db.close()
            context.deleteDatabase(databaseName)
        }
    }

    @Test fun twoChallengesRemainIndependentAndResetRemovesAllOptIns() = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val ids = listOf(UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val db = Room.inMemoryDatabaseBuilder(context,EvoluneDatabase::class.java).build()
        val zone = ZoneId.of("Asia/Kolkata")
        val start = LocalDate.now(zone)
        try {
            InstrumentationRegistry.getInstrumentation().uiAutomation
                .grantRuntimePermission(context.packageName,Manifest.permission.POST_NOTIFICATIONS)
            ids.forEach { id ->
                RoomEvoluneRepository(db.evoluneDao()).createChallenge(
                    ChallengePlan(id,"Independent reminder",start,zone,
                        MilestoneKind.CALENDAR_DAYS,21,
                        listOf(ScheduleVersion(start,Recurrence.Daily,"Do one small thing")))
                )
            }
            val chosen = LocalTime.now(zone).plusHours(3).withSecond(0).withNano(0)
            ids.forEach { id ->
                assertTrue(ReminderScheduler.enable(context,db.evoluneDao(),id,chosen))
            }
            assertTrue(ids.all { ReminderScheduler.isOptedIn(context,it) })
            assertTrue(ids.all { ReminderScheduler.scheduledAt(context,it)>System.currentTimeMillis() })

            ReminderScheduler.disable(context,ids.first())
            assertFalse(ReminderScheduler.isOptedIn(context,ids.first()))
            assertTrue(ReminderScheduler.isOptedIn(context,ids.last()))
            assertTrue(ReminderScheduler.scheduledAt(context,ids.last())>System.currentTimeMillis())

            ReminderScheduler.disableAll(context)
            assertTrue(ids.none { ReminderScheduler.isOptedIn(context,it) })
            assertTrue(ids.all { ReminderScheduler.scheduledAt(context,it)==-1L })
        } finally {
            ids.forEach { ReminderScheduler.disable(context,it) }
            db.close()
        }
    }

}
