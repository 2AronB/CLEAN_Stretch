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
import dev.clean.stretch.plan.ReminderPlan
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Up to three daily reminders (R-007, D-044), each at its own time and opening its own routine.
 * Each is a one-shot alarm with a 10-minute delivery window (no special permission; Android may
 * deliver it up to ~10 minutes late to save battery). Each time one fires it schedules the next
 * day's, and all are re-armed whenever a reminder setting changes, when the app opens, and after a
 * phone restart or app update ([BootReceiver], RECEIVE_BOOT_COMPLETED, D-043). Inexact repeating
 * alarms were tried first and proved unreliable on the owner's phone.
 */
object Reminders {
    private const val CHANNEL = "reminders"
    private const val TAG = "StretchReminders"
    private const val WINDOW_MS = 10 * 60 * 1000L
    /** Intent extra: which reminder (ReminderPlan.id) fired or was tapped. */
    const val EXTRA_PLAN = "dev.clean.stretch.REMINDER_PLAN"

    fun canNotify(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            context.checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    /** Turns each reminder's alarm on or off to match the saved settings. Safe to call often. */
    fun sync(context: Context, prefs: AppPreferences) {
        val alarms = context.getSystemService(AlarmManager::class.java) ?: return
        (1..ReminderPlan.MAX).forEach { alarms.cancel(alarmIntent(context, it)) }
        // The single reminder from before D-044 used request code 0.
        alarms.cancel(PendingIntent.getBroadcast(context, 0, Intent(context, ReminderReceiver::class.java), PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE))
        val now = LocalDateTime.now()
        prefs.reminders.forEach { plan ->
            var next = now.toLocalDate().atStartOfDay().plusMinutes(plan.time.toLong())
            if (!next.isAfter(now)) next = next.plusDays(1)
            val at = next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
            alarms.setWindow(AlarmManager.RTC_WAKEUP, at, WINDOW_MS, alarmIntent(context, plan.id))
            Log.i(TAG, "Reminder ${plan.id} set for $next")
        }
    }

    private fun alarmIntent(context: Context, id: Int): PendingIntent = PendingIntent.getBroadcast(
        context, id, Intent(context, ReminderReceiver::class.java).putExtra(EXTRA_PLAN, id),
        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
    )

    fun show(context: Context, id: Int) {
        if (!canNotify(context)) {
            Log.w(TAG, "Reminder due but notifications are not allowed")
            return
        }
        val manager = context.getSystemService(NotificationManager::class.java) ?: return
        manager.createNotificationChannel(NotificationChannel(CHANNEL, "Reminders", NotificationManager.IMPORTANCE_DEFAULT))
        val plan = AppPreferences(context).reminders.firstOrNull { it.id == id }
        val body = plan?.let { "Your ${it.label.lowercase()} routine is ready." } ?: "Your routine is ready whenever you are."
        // Tapping opens Today with this reminder's routine (MainActivity reads EXTRA_PLAN).
        val open = PendingIntent.getActivity(
            context, id,
            Intent(context, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra(EXTRA_PLAN, id),
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        val notification = Notification.Builder(context, CHANNEL)
            .setSmallIcon(R.drawable.ic_library)
            .setContentTitle("Time to stretch")
            .setContentText(body)
            .setContentIntent(open)
            .setAutoCancel(true)
            .build()
        manager.notify(id, notification)
    }
}

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Reminders.show(context, intent.getIntExtra(Reminders.EXTRA_PLAN, 0))
        // One-shot alarms: arm tomorrow's.
        Reminders.sync(context, AppPreferences(context))
    }
}

/**
 * Re-arms the daily reminders after the phone restarts or the app is updated (both clear alarms).
 * Only the system can send these broadcasts. With the reminder off it does nothing.
 */
class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED || intent.action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            Reminders.sync(context, AppPreferences(context))
        }
    }
}
