package com.example.data.model

/**
 * Structured model representing an extracted tax receipt or invoice
 * processed by the AK Taxmitra AI scanner.
 */
data class ScannedReceiptData(
    val vendorName: String,
    val documentType: String, // TAX_RECEIPT, GST_INVOICE, MEDICAL_80D, RENT_RECEIPT, DONATION_80G, BUSINESS_EXPENSE
    val invoiceNumber: String,
    val date: String,
    val totalAmount: Double,
    val taxableAmount: Double,
    val gstRatePercent: Double = 18.0,
    val cgstAmount: Double = 0.0,
    val sgstAmount: Double = 0.0,
    val igstAmount: Double = 0.0,
    val gstin: String = "",
    val category: String = "TAX", // TAX, BUSINESS, PERSONAL
    val taxBenefitSummary: String,
    val eligibleSection: String, // "Section 80D", "Section 80C", "Section 16 ITC", "Section 37", "HRA 10(13A)", "Section 80G"
    val confidenceScore: Int = 96,
    val imageUri: String = ""
)
