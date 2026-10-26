package com.example.data.notification

import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.MainActivity
import com.example.R
import com.example.data.engine.NextBestAction
import com.example.data.model.ComplianceTaskEntity

object TaxNotificationManager {

    const val CHANNEL_TAX_DEADLINES = "tax_deadlines_channel"
    const val CHANNEL_AI_ACTIONS = "ai_action_channel"

    const val EXTRA_TARGET_TAB = "extra_target_tab"
    const val EXTRA_AI_PROMPT = "extra_ai_prompt"
    const val EXTRA_TASK_ID = "extra_task_id"
    const val EXTRA_NOTIFICATION_TYPE = "extra_notification_type"

    const val TYPE_DEADLINE = "TYPE_DEADLINE"
    const val TYPE_AI_ACTION = "TYPE_AI_ACTION"

    /**
     * Initializes notification channels required for Android 8.0 (API 26) and above.
     */
    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Channel 1: Indian Statutory & Tax Deadlines
            val deadlineChannel = NotificationChannel(
                CHANNEL_TAX_DEADLINES,
                "Tax & Statutory Deadlines",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Critical alerts for Income Tax, GST, TDS, and MCA filing deadlines & penalty warnings"
                enableLights(true)
                enableVibration(true)
            }

            // Channel 2: AI Engine Next Best Action alerts
            val aiActionChannel = NotificationChannel(
                CHANNEL_AI_ACTIONS,
                "AI Copilot Action Items",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Proactive reminders for pending AI-identified tax savings and compliance actions"
                enableLights(true)
                enableVibration(true)
            }

            notificationManager.createNotificationChannel(deadlineChannel)
            notificationManager.createNotificationChannel(aiActionChannel)
        }
    }

    /**
     * Checks whether notifications are allowed by the user.
     */
    fun areNotificationsEnabled(context: Context): Boolean {
        return NotificationManagerCompat.from(context).areNotificationsEnabled()
    }

    /**
     * Sends an immediate local notification for an upcoming tax deadline.
     */
    fun sendDeadlineNotification(
        context: Context,
        task: ComplianceTaskEntity
    ) {
        createNotificationChannels(context)

        // Main Tap Intent -> Opens Compliance Calendar
        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_TARGET_TAB, "COMPLIANCE")
            putExtra(EXTRA_TASK_ID, task.id)
        }
        val openPendingIntent = PendingIntent.getActivity(
            context,
            ("deadline_" + task.id).hashCode(),
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // AI Portal Guide Action Intent
        val aiGuideIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_TARGET_TAB, "HOME")
            putExtra(EXTRA_AI_PROMPT, "How do I file ${task.title}? Explain the statutory procedure and avoid penalties.")
        }
        val aiGuidePendingIntent = PendingIntent.getActivity(
            context,
            ("ai_guide_" + task.id).hashCode(),
            aiGuideIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notificationId = (1000 + task.id).toInt()

        val notification = NotificationCompat.Builder(context, CHANNEL_TAX_DEADLINES)
            .setSmallIcon(R.drawable.ic_notification_tax)
            .setContentTitle("Tax Deadline Reminder: ${task.title}")
            .setContentText("Due on ${task.dueDate} (${task.authority}). ${task.penaltyWarning}")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        "📌 Due Date: ${task.dueDate}\n" +
                        "🏛 Authority: ${task.authority}\n" +
                        "⚠️ Penalty Warning: ${task.penaltyWarning}\n\n" +
                        "Tap to open your Compliance Calendar or consult the AI Assistant for step-by-step guidance."
                    )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(openPendingIntent)
            .addAction(R.drawable.ic_notification_tax, "View Calendar", openPendingIntent)
            .addAction(R.drawable.ic_notification_tax, "Ask AI Copilot", aiGuidePendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(notificationId, notification)
        } catch (e: SecurityException) {
            // Permission not granted
        }
    }

    /**
     * Sends an immediate local notification for an AI Next Best Action.
     */
    fun sendNextBestActionNotification(
        context: Context,
        action: NextBestAction
    ) {
        createNotificationChannels(context)

        val openIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_TARGET_TAB, action.targetTab)
        }
        val openPendingIntent = PendingIntent.getActivity(
            context,
            action.title.hashCode(),
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val askAiIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
            putExtra(EXTRA_TARGET_TAB, "HOME")
            putExtra(EXTRA_AI_PROMPT, "Help me complete this pending AI action: ${action.title} - ${action.description}")
        }
        val askAiPendingIntent = PendingIntent.getActivity(
            context,
            (action.title + "_ai").hashCode(),
            askAiIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val urgencyIcon = when (action.urgencyLevel) {
            "CRITICAL" -> "🚨"
            "IMPORTANT" -> "⚡"
            else -> "✨"
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_AI_ACTIONS)
            .setSmallIcon(R.drawable.ic_notification_tax)
            .setContentTitle("$urgencyIcon AI Action: ${action.title}")
            .setContentText(action.description)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(
                        "${action.description}\n\n" +
                        "Priority: ${action.urgencyLevel} • Target: ${action.targetTab}\n" +
                        "Tap to execute this recommendation now."
                    )
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContentIntent(openPendingIntent)
            .addAction(R.drawable.ic_notification_tax, action.actionButtonText, openPendingIntent)
            .addAction(R.drawable.ic_notification_tax, "Ask AI Copilot", askAiPendingIntent)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(2001, notification)
        } catch (e: SecurityException) {
            // Permission not granted
        }
    }

    /**
     * Schedules a local notification reminder using AlarmManager.
     */
    fun scheduleDeadlineReminder(
        context: Context,
        task: ComplianceTaskEntity,
        triggerAtMillis: Long
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val intent = Intent(context, TaxDeadlineReminderReceiver::class.java).apply {
            action = TaxDeadlineReminderReceiver.ACTION_REMIND_DEADLINE
            putExtra(TaxDeadlineReminderReceiver.EXTRA_TASK_ID, task.id)
            putExtra(TaxDeadlineReminderReceiver.EXTRA_TITLE, task.title)
            putExtra(TaxDeadlineReminderReceiver.EXTRA_DUE_DATE, task.dueDate)
            putExtra(TaxDeadlineReminderReceiver.EXTRA_AUTHORITY, task.authority)
            putExtra(TaxDeadlineReminderReceiver.EXTRA_PENALTY, task.penaltyWarning)
        }

        val requestCode = (3000 + task.id).toInt()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * Cancels a scheduled deadline reminder.
     */
    fun cancelDeadlineReminder(context: Context, taskId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, TaxDeadlineReminderReceiver::class.java).apply {
            action = TaxDeadlineReminderReceiver.ACTION_REMIND_DEADLINE
        }
        val requestCode = (3000 + taskId).toInt()
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    /**
     * Schedules an AI Next Best Action reminder after a delay in milliseconds.
     */
    fun scheduleAiActionReminder(
        context: Context,
        action: NextBestAction,
        delayMillis: Long
    ) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        val triggerAtMillis = System.currentTimeMillis() + delayMillis

        val intent = Intent(context, TaxDeadlineReminderReceiver::class.java).apply {
            setAction(TaxDeadlineReminderReceiver.ACTION_REMIND_AI_ACTION)
            putExtra(TaxDeadlineReminderReceiver.EXTRA_TITLE, action.title)
            putExtra(TaxDeadlineReminderReceiver.EXTRA_DESCRIPTION, action.description)
            putExtra(TaxDeadlineReminderReceiver.EXTRA_URGENCY, action.urgencyLevel)
            putExtra(TaxDeadlineReminderReceiver.EXTRA_BUTTON_TEXT, action.actionButtonText)
            putExtra(TaxDeadlineReminderReceiver.EXTRA_TARGET_TAB, action.targetTab)
        }

        val requestCode = 4001
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
