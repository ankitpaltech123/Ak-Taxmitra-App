package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Entity(tableName = "scanned_tax_documents")
data class ScannedTaxDocumentEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val documentType: String, // "FORM_16", "FORM_26AS", "GST_INVOICE", "SECTION_80D_HEALTH", "SECTION_80C_INVESTMENT", "RENT_RECEIPT_HRA", "PAN_CARD", "AADHAAR"
    val category: String, // "INCOME_TAX", "DEDUCTIONS", "GST", "BUSINESS_EXPENSE", "IDENTITY"
    val scanDateTimestamp: Long = System.currentTimeMillis(),
    val formattedDate: String = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault()).format(Date()),
    val aiSummary: String, // Detailed AI-extracted summary with financial & compliance breakdown
    val vendorOrIssuer: String = "",
    val invoiceOrRefNumber: String = "",
    val detectedAmount: Double = 0.0,
    val eligibleTaxSection: String = "", // e.g. "Sec 80D", "Sec 80C", "Sec 10(13A)", "Sec 16(ia)", "Sec 16 (ITC)"
    val taxBenefitDescription: String = "",
    val confidenceScore: Int = 95, // 0 - 100
    val imageUri: String = "",
    val status: String = "VERIFIED" // "SCANNED", "VERIFIED", "ARCHIVED"
)
