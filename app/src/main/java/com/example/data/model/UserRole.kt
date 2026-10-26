package com.example.data.model

enum class UserRole(val displayName: String, val subtitle: String) {
    BEGINNER("Beginner / First-timer", "Simple questions, no legal jargon, guided step-by-step"),
    SALARIED("Salaried Individual", "Form 16 import, ITR-1, tax regime optimizer, refund tracker"),
    FREELANCER("Freelancer / Professional", "Sec 44ADA, quarterly advance tax, invoicing, expense tracking"),
    BUSINESS_OWNER("Business Owner", "Business OS, GST, Udyam MSME, compliance calendar, P&L"),
    CA("Chartered Accountant", "CA Workspace, client management, AI Copilot, verification")
}
