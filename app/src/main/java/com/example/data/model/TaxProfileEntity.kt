package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tax_profiles")
data class TaxProfileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val financialYear: String = "FY 2024-25 (AY 2025-26)",
    val salaryIncome: Double = 0.0,
    val businessIncome: Double = 0.0,
    val rentalIncome: Double = 0.0,
    val capitalGains: Double = 0.0,
    val otherIncome: Double = 0.0,
    // Deductions
    val deduction80C: Double = 0.0, // Max 1,50,000 in Old Regime
    val deduction80D: Double = 0.0, // Health insurance
    val deductionNps: Double = 0.0, // 80CCD(1B) up to 50,000
    val otherDeductions: Double = 0.0,
    // Taxes paid
    val tdsPaid: Double = 0.0,
    val advanceTaxPaid: Double = 0.0,
    // Calculation outputs
    val taxOldRegime: Double = 0.0,
    val taxNewRegime: Double = 0.0,
    val selectedRegime: String = "NEW", // "NEW" or "OLD"
    val recommendedRegime: String = "NEW",
    val taxSavings: Double = 0.0,
    val filingRoute: String = "ITR-1", // ITR-1, ITR-2, ITR-3, ITR-4
    val filingRouteReason: String = "Salaried individual with single house property and other interest income",
    val status: String = "DRAFT", // DRAFT, READY_TO_FILE, CA_REVIEW, SUBMITTED, E_VERIFIED, REFUND_ISSUED
    val refundAmount: Double = 0.0,
    val taxPayable: Double = 0.0,
    val acknowledgementNumber: String = "",
    val lastUpdated: Long = System.currentTimeMillis()
)
