package com.example.data.engine

import com.example.data.model.BusinessEntity
import com.example.data.model.ComplianceTaskEntity
import com.example.data.model.TaxNoticeEntity
import com.example.data.model.TaxProfileEntity

data class NextBestAction(
    val title: String,
    val description: String,
    val urgencyLevel: String, // "CRITICAL", "IMPORTANT", "RECOMMENDED"
    val actionButtonText: String,
    val targetTab: String, // "BUSINESS", "TAX", "DOCUMENTS", "CA", "COMPLIANCE"
    val officialFlowBadge: String? = null
)

object NextBestActionEngine {
    fun compute(
        business: BusinessEntity?,
        taxProfile: TaxProfileEntity?,
        activeTasks: List<ComplianceTaskEntity>,
        notices: List<TaxNoticeEntity>
    ): NextBestAction {
        // 1. Critical: Unresolved Tax Notice with impending deadline
        val pendingNotice = notices.firstOrNull { !it.isResolved }
        if (pendingNotice != null) {
            return NextBestAction(
                title = "Income Tax Notice Response Required",
                description = "${pendingNotice.section}: ${pendingNotice.summary}. Deadline: ${pendingNotice.responseDeadline}.",
                urgencyLevel = "CRITICAL",
                actionButtonText = "Review Action Plan",
                targetTab = "TAX",
                officialFlowBadge = "OFFICIAL GOVERNMENT NOTICE"
            )
        }

        // 2. Urgent: Compliance task due soon
        val overdueTask = activeTasks.firstOrNull { it.status == "OVERDUE" }
        if (overdueTask != null) {
            return NextBestAction(
                title = "Overdue: ${overdueTask.title}",
                description = "Due date ${overdueTask.dueDate} missed. ${overdueTask.penaltyWarning}",
                urgencyLevel = "CRITICAL",
                actionButtonText = "File Now",
                targetTab = "COMPLIANCE",
                officialFlowBadge = "OFFICIAL PORTAL HANDOFF"
            )
        }

        val upcomingTask = activeTasks.firstOrNull { it.status == "UPCOMING" }

        // 3. Tax Filing ready to submit or optimize
        if (taxProfile != null) {
            if (taxProfile.status == "DRAFT" && (taxProfile.salaryIncome > 0 || taxProfile.businessIncome > 0)) {
                return NextBestAction(
                    title = "Compare New vs Old Tax Regime",
                    description = "You can save up to ₹${String.format("%,.0f", taxProfile.taxSavings)} under ${taxProfile.recommendedRegime} Regime for ${taxProfile.financialYear}.",
                    urgencyLevel = "IMPORTANT",
                    actionButtonText = "Optimize Tax",
                    targetTab = "TAX"
                )
            } else if (taxProfile.status == "READY_TO_FILE") {
                return NextBestAction(
                    title = "Pre-filing Health Check & CA Review",
                    description = "Your ${taxProfile.filingRoute} return is computed. Get verified CA stamp or proceed to e-file.",
                    urgencyLevel = "IMPORTANT",
                    actionButtonText = "Review & Submit",
                    targetTab = "TAX",
                    officialFlowBadge = "OFFICIAL AUTHORIZED FLOW"
                )
            }
        }

        // 4. Business setup progression
        if (business != null) {
            if (business.udyamStatus != "REGISTERED") {
                return NextBestAction(
                    title = "Claim Free Udyam MSME Certificate",
                    description = "100% paperless official registration unlocks priority bank lending & collateral-free loans.",
                    urgencyLevel = "IMPORTANT",
                    actionButtonText = "Complete Udyam",
                    targetTab = "BUSINESS",
                    officialFlowBadge = "OFFICIAL GOVERNMENT SERVICE"
                )
            }
            if (business.readinessScore < 80) {
                return NextBestAction(
                    title = "Complete Business Readiness (${business.readinessScore}%)",
                    description = "Set up GST threshold check, business invoicing, and trade license to reach 100%.",
                    urgencyLevel = "RECOMMENDED",
                    actionButtonText = "Boost Score",
                    targetTab = "BUSINESS"
                )
            }
        }

        // 5. Default action if nothing else is urgent
        return if (upcomingTask != null) {
            NextBestAction(
                title = "Upcoming: ${upcomingTask.title}",
                description = "Due on ${upcomingTask.dueDate} (${upcomingTask.frequency}). Keep documents ready.",
                urgencyLevel = "RECOMMENDED",
                actionButtonText = "View Calendar",
                targetTab = "COMPLIANCE"
            )
        } else {
            NextBestAction(
                title = "Check Your Tax Health",
                description = "Analyze your Form 16, interest statements, and TDS credits in 60 seconds.",
                urgencyLevel = "RECOMMENDED",
                actionButtonText = "Start Tax Check",
                targetTab = "TAX"
            )
        }
    }
}
