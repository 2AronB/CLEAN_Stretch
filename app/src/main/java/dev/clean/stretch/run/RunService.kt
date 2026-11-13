package dev.clean.stretch.run

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import dev.clean.stretch.R
import dev.clean.stretch.ui.Side

/**
 * Keeps a routine running with the screen off (D-037, supersedes the keep-screen-on-only limit of
 * D-010). A foreground service of type "media playback", because what it does in the background is
 * play the spoken cues and chimes. Its notification shows the current stretch with a live countdown
 * and Pause / Skip buttons. Nothing leaves the phone.
 */
class RunService : Service() {
    override fun onBind(intent: Intent?): IBinder? = null

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val session = ActiveRun.current
        when (intent?.action) {
            ACTION_TOGGLE -> session?.togglePause()
            ACTION_SKIP -> session?.skip()
        }
        if (session == null || session.closed) {
            stopSelf()
            return START_NOT_STICKY
        }
        val notification = build(this, session)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(NOTIFICATION_ID, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_MEDIA_PLAYBACK)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }
        running = true
        return START_NOT_STICKY
    }

    /** Swiped away from Recents: stop like Android closing the app; Today offers Resume. */
    override fun onTaskRemoved(rootIntent: Intent?) {
        ActiveRun.current?.close()
        stopSelf()
    }

    override fun onDestroy() {
        running = false
        stopForeground(STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }

    companion object {
        private const val TAG = "StretchRun"
        private const val CHANNEL = "routine"
        private const val NOTIFICATION_ID = 2
        private const val ACTION_TOGGLE = "dev.clean.stretch.TOGGLE"
        private const val ACTION_SKIP = "dev.clean.stretch.SKIP"

        @Volatile
        private var running = false

        fun start(context: Context) {
            runCatching { context.startForegroundService(Intent(context, RunService::class.java)) }
                .onFailure { Log.w(TAG, "Could not start background running; the routine needs the screen on", it) }
        }

        fun stop(context: Context) {
            context.stopService(Intent(context, RunService::class.java))
        }

        /** Refreshes the notification after a step change, pause or skip. */
        fun update(context: Context, session: RunSession) {
            if (!running || session.closed) return
            context.getSystemService(NotificationManager::class.java)?.notify(NOTIFICATION_ID, build(context, session))
        }

        private fun build(context: Context, session: RunSession): Notification {
            val manager = context.getSystemService(NotificationManager::class.java)
            manager?.createNotificationChannel(
                NotificationChannel(CHANNEL, "Routine in progress", NotificationManager.IMPORTANCE_LOW).apply {
                    description = "Shows the current stretch while a routine runs with the screen off."
                    setShowBadge(false)
                },
            )
            val e = session.engine
            val step = e.step
            val name = step.stretch.name + if (step.side == Side.Both) "" else " · ${step.side.label.lowercase()}"
            val status = when {
                e.finished -> "Routine complete"
                e.paused -> "Paused"
                step.setup -> "Get into position"
                else -> "Holding"
            }
            val open = context.packageManager.getLaunchIntentForPackage(context.packageName)?.let {
                PendingIntent.getActivity(context, 0, it, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
            }
            val builder = Notification.Builder(context, CHANNEL)
                .setSmallIcon(R.drawable.ic_library)
                .setContentTitle(if (e.finished) session.route.title else name)
                .setContentText(status)
                .setContentIntent(open)
                .setOngoing(!e.finished)
                .setOnlyAlertOnce(true)
                .setCategory(Notification.CATEGORY_PROGRESS)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                builder.setForegroundServiceBehavior(Notification.FOREGROUND_SERVICE_IMMEDIATE)
            }
            if (!e.finished && !e.paused) {
                val leftMs = (e.durationMs - e.elapsed(RunSession.now())).coerceAtLeast(0L)
                builder.setWhen(System.currentTimeMillis() + leftMs).setUsesChronometer(true).setChronometerCountDown(true).setShowWhen(true)
            } else {
                builder.setShowWhen(false)
            }
            if (!e.finished) {
                builder.addAction(action(context, ACTION_TOGGLE, if (e.paused) "Resume" else "Pause", R.drawable.ic_play))
                builder.addAction(action(context, ACTION_SKIP, "Skip", R.drawable.ic_skip_next))
            }
            return builder.build()
        }

        private fun action(context: Context, action: String, label: String, icon: Int): Notification.Action {
            val intent = PendingIntent.getService(
                context, action.hashCode(), Intent(context, RunService::class.java).setAction(action),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )
            return Notification.Action.Builder(android.graphics.drawable.Icon.createWithResource(context, icon), label, intent).build()
        }
    }
}
