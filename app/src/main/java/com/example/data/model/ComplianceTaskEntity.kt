package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "compliance_tasks")
data class ComplianceTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val category: String, // INCOME_TAX, GST, TDS, ROC, MSME
    val dueDate: String,
    val dueTimestamp: Long,
    val frequency: String, // Monthly, Quarterly, Annual
    val penaltyWarning: String, // e.g. "₹5,000 late fee under Sec 234F + 1% interest/mo"
    val authority: String, // Income Tax Department, GSTN Portal, MCA Govt of India
    val officialUrl: String,
    val status: String = "UPCOMING", // UPCOMING, COMPLETED, OVERDUE
    val isAutoCalculated: Boolean = true
)
