package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tax_notices")
data class TaxNoticeEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val noticeNumber: String,
    val section: String, // "Section 143(1)", "Section 139(9)", "Section 148", "Section 142(1)"
    val authority: String = "Centralized Processing Center (CPC), Income Tax Dept",
    val dateReceived: String,
    val responseDeadline: String,
    val summary: String,
    val riskLevel: String = "MEDIUM", // LOW, MEDIUM, HIGH
    val actionPlan: String,
    val isResolved: Boolean = false,
    val caAssistanceRequested: Boolean = false
)

data class ChatMessage(
    val id: String,
    val sender: String, // "USER" or "TAXMITRA"
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val quickActions: List<String> = emptyList(),
    val officialFlowTag: String? = null, // e.g. "OFFICIAL GOVERNMENT FLOW" or "TAXMITRA AI"
    val modelUsed: String? = null, // e.g. "gemini-3.1-pro-preview", "gemini-3.5-flash", "gemini-3.1-flash-lite", "gemini-3.1-flash-live-preview"
    val roleName: String? = null,
    val isVoiceMessage: Boolean = false
)
