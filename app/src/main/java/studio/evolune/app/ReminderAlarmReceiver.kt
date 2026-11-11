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

/**
 * Receiver only trusts an existing opt-in and the exact stored trigger token.
 * No journal content is ever placed into a notification.
 */
class ReminderAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != ReminderScheduler.ACTION_REMIND) return
        val id = intent.getStringExtra("challengeId") ?: return
        val at = intent.getLongExtra("triggerAt", -1L)
        if (at < 0L) return
        val result = goAsync()
        val appContext = context.applicationContext
        CoroutineScope(SupervisorJob() + Dispatchers.IO).launch {
            val db = Room.databaseBuilder(appContext, EvoluneDatabase::class.java, "evolune_v1.db").build()
            try {
                ReminderScheduler.receive(appContext, db.evoluneDao(), id, at)
            } catch (_: Exception) {
                // No private user data is logged by background reminder processing.
            } finally {
                db.close()
                result.finish()
            }
        }
    }
}
