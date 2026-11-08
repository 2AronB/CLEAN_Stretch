package dev.clean.stretch.settings

import android.Manifest
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import dev.clean.stretch.MainActivity
import dev.clean.stretch.R
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Daily reminder at the user's chosen time (R-007). Uses an inexact repeating alarm, which needs
 * no special permission; Android may deliver it a few minutes late to save battery.
 * Not yet handled: rescheduling after a phone restart needs RECEIVE_BOOT_COMPLETED (not approved),
 * so the reminder is rescheduled every time the app opens instead.
 */
object Reminders {
    private const val CHANNEL = "reminders"
    private const val NOTIFICATION_ID = 1
    private const val TAG = "StretchReminders"

    fun canNotify(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    /** Turns the alarm on or off to match the saved settings. Safe to call often. */
    fun sync(context: Context, prefs: AppPreferences) {
        val alarms = context.getSystemService(AlarmManager::class.java) ?: return
        val pending = alarmIntent(context)
        alarms.cancel(pending)
        if (!prefs.reminderOn) return
        val now = LocalDateTime.now()
        var next = now.toLocalDate().atStartOfDay().plusMinutes(prefs.reminderTime.toLong())
        if (!next.isAfter(now)) next = next.plusDays(1)
        val at = next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        alarms.setInexactRepeating(AlarmManager.RTC_WAKEUP, at, AlarmManager.INTERVAL_DAY, pending)
        Log.i(TAG, "Daily reminder set for $next")
    }

    private fun alarmIntent(context: Context): PendingIntent = PendingIntent.getBroadcast(
        context, 0, Intent(context, ReminderReceiver::class.java),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    fun show(context: Context) {
        if (!canNotify(context)) {
            Log.w(TAG, "Reminder due but notifications are not allowed")
            return
        }
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        manager.createNotificationChannel(NotificationChannel(CHANNEL, "Reminders", NotificationManager.IMPORTANCE_DEFAULT))
        val prefs = AppPreferences(context)
        val body = if (prefs.planCue == PlanCue.SetTime) {
            "Your routine is ready whenever you are."
        } else {
            "Your plan: stretch ${prefs.planCue.reminderText}. Your routine is ready."
        }
        val open = PendingIntent.getActivity(
            context, 0, Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = Notification.Builder(context, CHANNEL)
            .setSmallIcon(R.drawable.ic_library)
            .setContentTitle("Time to stretch")
            .setContentText(body)
            .setContentIntent(open)
            .setAutoCancel(true)
            .build()
        manager.notify(NOTIFICATION_ID, notification)
    }
}

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) = Reminders.show(context)
}
