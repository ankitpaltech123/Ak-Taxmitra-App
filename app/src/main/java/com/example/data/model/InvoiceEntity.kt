package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "invoices")
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val invoiceNumber: String,
    val clientName: String,
    val clientGst: String = "",
    val itemsSummary: String, // e.g. "Software Consulting & Cloud Setup"
    val subtotal: Double,
    val gstRatePercent: Double = 18.0,
    val gstAmount: Double,
    val totalAmount: Double,
    val issueDate: String,
    val dueDate: String,
    val status: String = "SENT", // DRAFT, SENT, PAID, OVERDUE
    val createdAt: Long = System.currentTimeMillis()
)
