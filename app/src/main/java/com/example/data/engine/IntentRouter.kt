package com.example.data.engine

enum class TaxmitraIntent(val label: String, val description: String) {
    ITR_FILING("ITR Filing Workflow", "File Income Tax Return with step-by-step guidance"),
    BUSINESS_SETUP("Business Launch OS", "Start a new business from idea to registration"),
    TAX_CALCULATION("Deterministic Tax Calculator", "Compare Old vs New Regime slabs and savings"),
    TAX_NOTICE("Notice Intelligence", "Analyze and respond to Income Tax / GST notices"),
    CA_MATCHING("CA Marketplace", "Find verified Chartered Accountants with transparent pricing"),
    INVOICE_CREATION("Invoice Generator", "Create GST compliant sales invoice"),
    GST_MANAGEMENT("GST OS", "Check GSTR-1, GSTR-3B status, ITC and returns"),
    COMPLIANCE_CHECK("Compliance Calendar", "Check upcoming tax and statutory deadlines"),
    DOCUMENT_VAULT("AK Vault", "Access and manage secure tax and business documents"),
    TAX_PLANNING("Tax Optimization", "Identify 80C, 80D, NPS and regime tax savings"),
    REFUND_TRACKING("Refund Tracker", "Check status of pending tax refunds from IT department"),
    GENERAL_QUERY("Taxmitra AI Copilot", "General tax, legal, or business inquiry")
}

data class IntentRouteResult(
    val intent: TaxmitraIntent,
    val targetTab: String, // "HOME", "AI", "TAX", "BUSINESS", "MORE"
    val workflowKey: String? = null,
    val initialAiPrompt: String,
    val guidanceTitle: String,
    val guidanceSubtitle: String,
    val actionLabel: String
)

object IntentRouter {

    fun route(query: String): IntentRouteResult {
        val q = query.trim().lowercase()

        return when {
            // ITR Filing
            q.contains("file") && (q.contains("tax") || q.contains("itr") || q.contains("return")) ||
            q.contains("itr") && (q.contains("filing") || q.contains("start") || q.contains("form 16")) ||
            q.contains("file my tax") || q.contains("file tax") -> {
                IntentRouteResult(
                    intent = TaxmitraIntent.ITR_FILING,
                    targetTab = "TAX",
                    workflowKey = "WORKFLOW_ITR",
                    initialAiPrompt = "Guide me through filing my ITR for FY 2025-26 step by step.",
                    guidanceTitle = "ITR Guided Filing Workflow",
                    guidanceSubtitle = "6-step guided preparation: Profile, Income, Deductions, Slabs, Verification",
                    actionLabel = "Start ITR Filing"
                )
            }

            // Business Setup / Launch
            q.contains("start a business") || q.contains("start business") || q.contains("start company") ||
            q.contains("register business") || q.contains("register company") || q.contains("new business") ||
            q.contains("pvt ltd") || q.contains("llp") || q.contains("proprietorship") || q.contains("incorporat") ||
            q.contains("startup") && q.contains("register") -> {
                IntentRouteResult(
                    intent = TaxmitraIntent.BUSINESS_SETUP,
                    targetTab = "BUSINESS",
                    workflowKey = "WORKFLOW_BUSINESS_LAUNCH",
                    initialAiPrompt = "I want to start a business. Recommend the best entity structure and guide me through registration.",
                    guidanceTitle = "Business Launch OS",
                    guidanceSubtitle = "From idea to registration, PAN/TAN, GST, MSME and launch checklist",
                    actionLabel = "Launch Business OS"
                )
            }

            // Notice Intelligence
            q.contains("notice") || q.contains("143(1)") || q.contains("139(9)") || q.contains("148") ||
            q.contains("scrutiny") || q.contains("defective return") || q.contains("demand") -> {
                IntentRouteResult(
                    intent = TaxmitraIntent.TAX_NOTICE,
                    targetTab = "TAX",
                    workflowKey = "WORKFLOW_NOTICE",
                    initialAiPrompt = "I received a tax notice. Explain the legal section, penalty risk, and response protocol.",
                    guidanceTitle = "Tax Notice Intelligence",
                    guidanceSubtitle = "Understand statutory demands, assess penalty risks, and draft an official response",
                    actionLabel = "Review Notice"
                )
            }

            // CA Marketplace
            q.contains("ca") || q.contains("chartered accountant") || q.contains("consultant") ||
            q.contains("hire") || q.contains("expert") || q.contains("audit") || q.contains("find ca") -> {
                IntentRouteResult(
                    intent = TaxmitraIntent.CA_MATCHING,
                    targetTab = "MORE",
                    workflowKey = "WORKFLOW_CA_MARKETPLACE",
                    initialAiPrompt = "Find me a verified Chartered Accountant specializing in GST and income tax.",
                    guidanceTitle = "CA Marketplace",
                    guidanceSubtitle = "Connect with verified professionals with transparent, unbundled pricing",
                    actionLabel = "Browse Verified CAs"
                )
            }

            // Invoicing
            q.contains("invoice") || q.contains("bill") || q.contains("create invoice") || q.contains("billing") -> {
                IntentRouteResult(
                    intent = TaxmitraIntent.INVOICE_CREATION,
                    targetTab = "BUSINESS",
                    workflowKey = "WORKFLOW_INVOICE",
                    initialAiPrompt = "How do I make a GST compliant invoice with proper HSN codes?",
                    guidanceTitle = "Accounting & Invoice Generator",
                    guidanceSubtitle = "Generate GST compliant sales invoices with auto CGST/SGST/IGST calculation",
                    actionLabel = "Create Invoice"
                )
            }

            // GST OS
            q.contains("gst") || q.contains("gstr") || q.contains("itc") || q.contains("gstr-3b") ||
            q.contains("gstr-1") || q.contains("input tax credit") -> {
                IntentRouteResult(
                    intent = TaxmitraIntent.GST_MANAGEMENT,
                    targetTab = "MORE",
                    workflowKey = "WORKFLOW_GST",
                    initialAiPrompt = "Check my GST compliance status, upcoming return dates, and input tax credit reconciliation.",
                    guidanceTitle = "GST Operating System",
                    guidanceSubtitle = "GSTR-1, GSTR-3B filings, 2B ITC reconciliation and e-way bill tracker",
                    actionLabel = "Open GST Hub"
                )
            }

            // Compliance & Deadlines
            q.contains("deadline") || q.contains("due date") || q.contains("compliance") ||
            q.contains("calendar") || q.contains("this month") || q.contains("penalty") -> {
                IntentRouteResult(
                    intent = TaxmitraIntent.COMPLIANCE_CHECK,
                    targetTab = "MORE",
                    workflowKey = "WORKFLOW_COMPLIANCE",
                    initialAiPrompt = "What statutory tax and compliance deadlines do I have coming up this month?",
                    guidanceTitle = "Compliance Calendar Center",
                    guidanceSubtitle = "Upcoming ITR, GST, TDS, and ROC statutory deadlines with penalty warnings",
                    actionLabel = "View Compliance"
                )
            }

            // Document Vault
            q.contains("document") || q.contains("vault") || q.contains("pan card") ||
            q.contains("form 16") || q.contains("26as") || q.contains("ais") || q.contains("upload") -> {
                IntentRouteResult(
                    intent = TaxmitraIntent.DOCUMENT_VAULT,
                    targetTab = "MORE",
                    workflowKey = "WORKFLOW_VAULT",
                    initialAiPrompt = "Show my uploaded tax documents and extract key fields from Form 16 / AIS.",
                    guidanceTitle = "AK Vault (Document OS)",
                    guidanceSubtitle = "Bank-grade encrypted storage for PAN, Aadhaar, Form 16, GST & ITR receipts",
                    actionLabel = "Open AK Vault"
                )
            }

            // Tax Planning & Calculation
            q.contains("calculate") || q.contains("calculator") || q.contains("estimate tax") ||
            q.contains("how much tax") || q.contains("slab") -> {
                IntentRouteResult(
                    intent = TaxmitraIntent.TAX_CALCULATION,
                    targetTab = "TAX",
                    workflowKey = "WORKFLOW_CALCULATOR",
                    initialAiPrompt = "Calculate my tax and compare Old vs New Tax Regime with ₹75,000 standard deduction.",
                    guidanceTitle = "Deterministic Tax Engine",
                    guidanceSubtitle = "Official Finance Act slabs with Sec 87A rebate and Old vs New regime comparator",
                    actionLabel = "Open Tax Calculator"
                )
            }

            // Refund Tracker
            q.contains("refund") || q.contains("track refund") || q.contains("where is my refund") -> {
                IntentRouteResult(
                    intent = TaxmitraIntent.REFUND_TRACKING,
                    targetTab = "TAX",
                    workflowKey = "WORKFLOW_REFUND",
                    initialAiPrompt = "How do I track my income tax refund status on the e-filing portal?",
                    guidanceTitle = "ITR Refund Tracker",
                    guidanceSubtitle = "Track processing status from CPC Bengaluru: Filed -> Verified -> Processed -> Credited",
                    actionLabel = "Track Refund Status"
                )
            }

            // Tax Savings / Planning
            q.contains("save tax") || q.contains("80c") || q.contains("80d") || q.contains("deduction") ||
            q.contains("nps") || q.contains("tax saving") -> {
                IntentRouteResult(
                    intent = TaxmitraIntent.TAX_PLANNING,
                    targetTab = "TAX",
                    workflowKey = "WORKFLOW_PLANNING",
                    initialAiPrompt = "Give me tax saving recommendations for FY 2025-26 under Section 80C, 80D, and NPS.",
                    guidanceTitle = "AI Tax Optimization",
                    guidanceSubtitle = "Identify missed deductions and calculate exact tax savings across regimes",
                    actionLabel = "Optimize Tax"
                )
            }

            // Default: General AI Query
            else -> {
                IntentRouteResult(
                    intent = TaxmitraIntent.GENERAL_QUERY,
                    targetTab = "AI",
                    workflowKey = null,
                    initialAiPrompt = query,
                    guidanceTitle = "AK Taxmitra AI Copilot",
                    guidanceSubtitle = "Analyzing Indian tax, business law, and compliance context...",
                    actionLabel = "Consult AI Copilot"
                )
            }
        }
    }
}
