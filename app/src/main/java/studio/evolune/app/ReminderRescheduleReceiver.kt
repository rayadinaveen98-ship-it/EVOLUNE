package studio.evolune.app

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import studio.evolune.data.EvoluneDatabase

/** Reconstructs only user-approved future reminders after reboot, time or app changes. */
class ReminderRescheduleReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action !in setOf(
                Intent.ACTION_BOOT_COMPLETED,
                Intent.ACTION_MY_PACKAGE_REPLACED,
                Intent.ACTION_TIME_CHANGED,
                Intent.ACTION_TIMEZONE_CHANGED
            )) return
        val result = goAsync()
        val appContext = context.applicationContext
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            val db = Room.databaseBuilder(appContext, EvoluneDatabase::class.java, "evolune_v1.db").build()
            try {
                ReminderScheduler.rescheduleAll(appContext, db.evoluneDao())
            } catch (_: Exception) {
                // Best effort; next app foreground will retry without exposing sensitive content.
            } finally {
                db.close()
                result.finish()
            }
        }
    }
}
