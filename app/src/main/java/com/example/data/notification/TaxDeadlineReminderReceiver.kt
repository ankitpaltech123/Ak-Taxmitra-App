package com.example.data.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.data.engine.NextBestAction
import com.example.data.model.ComplianceTaskEntity

class TaxDeadlineReminderReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_REMIND_DEADLINE = "com.example.taxmitra.ACTION_REMIND_DEADLINE"
        const val ACTION_REMIND_AI_ACTION = "com.example.taxmitra.ACTION_REMIND_AI_ACTION"

        const val EXTRA_TASK_ID = "task_id"
        const val EXTRA_TITLE = "task_title"
        const val EXTRA_DUE_DATE = "due_date"
        const val EXTRA_AUTHORITY = "authority"
        const val EXTRA_PENALTY = "penalty_warning"

        const val EXTRA_DESCRIPTION = "action_description"
        const val EXTRA_URGENCY = "action_urgency"
        const val EXTRA_BUTTON_TEXT = "action_button_text"
        const val EXTRA_TARGET_TAB = "action_target_tab"
    }

    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            ACTION_REMIND_DEADLINE -> {
                val taskId = intent.getLongExtra(EXTRA_TASK_ID, 0L)
                val title = intent.getStringExtra(EXTRA_TITLE) ?: "Statutory Tax Deadline"
                val dueDate = intent.getStringExtra(EXTRA_DUE_DATE) ?: "Approaching"
                val authority = intent.getStringExtra(EXTRA_AUTHORITY) ?: "Government Portal"
                val penalty = intent.getStringExtra(EXTRA_PENALTY) ?: "Avoid statutory late fees"

                val dummyTask = ComplianceTaskEntity(
                    id = taskId,
                    title = title,
                    category = "INCOME_TAX",
                    dueDate = dueDate,
                    dueTimestamp = System.currentTimeMillis(),
                    frequency = "Statutory",
                    penaltyWarning = penalty,
                    authority = authority,
                    officialUrl = "https://incometax.gov.in"
                )

                TaxNotificationManager.sendDeadlineNotification(context, dummyTask)
            }
            ACTION_REMIND_AI_ACTION -> {
                val title = intent.getStringExtra(EXTRA_TITLE) ?: "Pending AI Recommendation"
                val description = intent.getStringExtra(EXTRA_DESCRIPTION) ?: "You have a pending compliance action."
                val urgency = intent.getStringExtra(EXTRA_URGENCY) ?: "RECOMMENDED"
                val buttonText = intent.getStringExtra(EXTRA_BUTTON_TEXT) ?: "Take Action"
                val targetTab = intent.getStringExtra(EXTRA_TARGET_TAB) ?: "HOME"

                val action = NextBestAction(
                    title = title,
                    description = description,
                    urgencyLevel = urgency,
                    actionButtonText = buttonText,
                    targetTab = targetTab
                )

                TaxNotificationManager.sendNextBestActionNotification(context, action)
            }
        }
    }
}
