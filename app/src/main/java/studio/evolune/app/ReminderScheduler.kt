package studio.evolune.app

import android.Manifest
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.room.Room
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import studio.evolune.data.EvoluneDao
import studio.evolune.data.EvoluneDatabase
import studio.evolune.data.RoomEvoluneRepository
import java.time.Instant
import java.time.LocalTime

/**
 * Opt-in, inexact and local-only. No exact-alarm special access, analytics or network calls.
 * Reminder preferences remain disabled after a JSON restore until re-enabled explicitly.
 */
object ReminderScheduler {
    const val ACTION_REMIND = "studio.evolune.app.ACTION_PRACTICE_REMINDER"
    private const val PREF = "evolune_reminders_v1"
    private const val CHANNEL = "gentle_practice"
    private const val ENABLED = "enabled:"
    private const val TIME = "time:"
    private const val TRIGGER = "trigger:"

    private fun settings(context: Context) =
        context.applicationContext.getSharedPreferences(PREF, Context.MODE_PRIVATE)

    fun isOptedIn(context: Context, id: String) =
        settings(context).getBoolean(ENABLED + id, false)

    fun time(context: Context, id: String): LocalTime =
        runCatching {
            LocalTime.parse(settings(context).getString(TIME + id, "09:00"))
        }.getOrDefault(LocalTime.of(9, 0))

    fun canNotify(context: Context): Boolean {
        val manager = context.getSystemService(NotificationManager::class.java)
        return manager.areNotificationsEnabled() &&
            (Build.VERSION.SDK_INT < 33 ||
             context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) ==
                PackageManager.PERMISSION_GRANTED)
    }

    private fun alarm(context: Context, id: String, trigger: Long): PendingIntent {
        val intent = Intent(context, ReminderAlarmReceiver::class.java).apply {
            action = ACTION_REMIND
            data = Uri.parse("evolune://practice/" + Uri.encode(id))
            putExtra("challengeId", id)
            putExtra("triggerAt", trigger)
        }
        return PendingIntent.getBroadcast(context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
    }

    private fun cancelAlarm(context: Context, id: String) {
        // Only identity (action, data, receiver) matters when cancelling; extras are ignored.
        context.getSystemService(AlarmManager::class.java)
            .cancel(alarm(context, id, 0L))
        settings(context).edit().remove(TRIGGER + id).apply()
    }

    fun scheduledAt(context: Context, id: String): Long =
        settings(context).getLong(TRIGGER + id, -1L)

    fun disable(context: Context, id: String) {
        cancelAlarm(context, id)
        settings(context).edit()
            .remove(ENABLED + id)
            .remove(TIME + id)
            .apply()
    }

    fun disableAll(context: Context) {
        val prefs = settings(context)
        val ids = prefs.all.keys.filter { it.startsWith(ENABLED) }.map { it.removePrefix(ENABLED) }
        ids.forEach { cancelAlarm(context, it) }
        prefs.edit().clear().apply()
    }

    /**
     * Explicit user action only; never infer consent from a schedule's cueTime.
     */
    suspend fun enable(context: Context, dao: EvoluneDao, id: String, at: LocalTime): Boolean {
        require(canNotify(context)) { "Notifications are disabled in Android settings" }
        settings(context).edit().putBoolean(ENABLED + id, true)
            .putString(TIME + id, at.toString()).apply()
        reschedule(context, dao, id)
        return scheduledAt(context, id) > 0L
    }

    /**
     * Keeps the preference but cancels any stale alarm when Android permission is revoked.
     * If a challenge is gone or archived, removes the preference entirely.
     */
    suspend fun reschedule(context: Context, dao: EvoluneDao, id: String) {
        cancelAlarm(context, id)
        if (!isOptedIn(context, id)) return
        val challenge = dao.findChallenge(id)
        if (challenge == null || challenge.archivedAtEpochMs != null) {
            disable(context, id)
            return
        }
        if (!canNotify(context)) return
        val snapshot = RoomEvoluneRepository(dao).snapshot(id) ?: run {
            disable(context, id)
            return
        }
        val target = ReminderPlanning.next(snapshot, time(context, id), Instant.now()) ?: return
        val at = target.toEpochMilli()
        context.getSystemService(AlarmManager::class.java).setAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP, at, alarm(context, id, at)
        )
        settings(context).edit().putLong(TRIGGER + id, at).apply()
    }

    suspend fun rescheduleAll(context: Context, dao: EvoluneDao) {
        val ids = settings(context).all.keys
            .filter { it.startsWith(ENABLED) }.map { it.removePrefix(ENABLED) }
        for (id in ids) reschedule(context, dao, id)
    }

    suspend fun receive(context: Context, dao: EvoluneDao, id: String, trigger: Long) {
        // Ignore canceled or superseded in-flight broadcasts.
        if (!isOptedIn(context, id) || !canNotify(context) ||
            scheduledAt(context, id) != trigger) return
        val snapshot = RoomEvoluneRepository(dao).snapshot(id)
        if (snapshot == null || dao.findChallenge(id)?.archivedAtEpochMs != null) {
            disable(context, id)
            return
        }
        val now = Instant.now()
        val expected = ReminderPlanning.next(
            snapshot, time(context, id), Instant.ofEpochMilli(trigger - 1_000L)
        )
        if (expected?.toEpochMilli() == trigger && trigger <= now.toEpochMilli() + 60_000L) {
            val manager = context.getSystemService(NotificationManager::class.java)
            if (Build.VERSION.SDK_INT >= 26) {
                manager.createNotificationChannel(NotificationChannel(
                    CHANNEL, "Gentle practice reminders", NotificationManager.IMPORTANCE_LOW
                ).apply {
                    description = "Optional, quiet reminders chosen for your EVOLUNE practices"
                    setSound(null, null)
                    enableVibration(false)
                })
            }
            val open = PendingIntent.getActivity(
                context, 0, Intent(context, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                }, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            manager.notify(id.hashCode(), Notification.Builder(context, CHANNEL)
                .setSmallIcon(android.R.drawable.ic_popup_reminder)
                .setContentTitle("A gentle reminder")
                .setContentText("Your planned practice is here when you're ready.")
                .setContentIntent(open)
                .setCategory(Notification.CATEGORY_REMINDER)
                .setVisibility(Notification.VISIBILITY_PRIVATE)
                .setAutoCancel(true)
                .build())
        }
        // A single non-repeating alarm; recompute after every delivery/skip.
        reschedule(context, dao, id)
    }
}
