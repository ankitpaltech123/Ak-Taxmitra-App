package com.example.data.engine

data class StructureComparison(
    val entityName: String,
    val owners: String,
    val liability: String,
    val complianceCost: String,
    val fundingPotential: String,
    val annualFilings: String,
    val pros: List<String>,
    val cons: List<String>
)

data class StructureRecommendation(
    val recommendedEntity: String,
    val alternativeEntity: String,
    val rationale: String,
    val initialRegistrations: List<String>,
    val estimatedGovtFee: String,
    val estimatedTimeline: String,
    val comparison: List<StructureComparison>
)

object BusinessStructureEngine {
    fun evaluate(
        founderCount: Int,
        seeksInvestment: Boolean,
        wantsLimitedLiability: Boolean,
        turnoverRange: String,
        isOnline: Boolean
    ): StructureRecommendation {
        val (rec, alt, why) = when {
            founderCount == 1 && !seeksInvestment && !wantsLimitedLiability && turnoverRange == "Under 20L" -> {
                Triple(
                    "Sole Proprietorship",
                    "One Person Company (OPC)",
                    "Lowest initial cost (₹0 government fee), zero ROC filing burden, and simple income tax under individual slab rates."
                )
            }
            founderCount == 1 && (wantsLimitedLiability || seeksInvestment || turnoverRange != "Under 20L") -> {
                Triple(
                    "One Person Company (OPC)",
                    "Private Limited Company",
                    "Single founder protection with limited liability, separate legal entity status, while maintaining sole managerial control."
                )
            }
            founderCount >= 2 && seeksInvestment -> {
                Triple(
                    "Private Limited Company (Pvt Ltd)",
                    "Limited Liability Partnership (LLP)",
                    "Required by venture capital & angel investors to issue ESOPs and equity shares. Gives strong credibility and separate corporate identity."
                )
            }
            founderCount >= 2 && wantsLimitedLiability && !seeksInvestment -> {
                Triple(
                    "Limited Liability Partnership (LLP)",
                    "Partnership Firm",
                    "Protects personal assets with limited liability while avoiding heavy corporate audit burdens unless turnover exceeds ₹40 Lakhs or capital > ₹25 Lakhs."
                )
            }
            founderCount >= 2 && !wantsLimitedLiability -> {
                Triple(
                    "Partnership Firm",
                    "Limited Liability Partnership (LLP)",
                    "Simple partnership deed on non-judicial stamp paper, fast registration, but partners share joint & several personal liability."
                )
            }
            else -> {
                Triple(
                    "Private Limited Company (Pvt Ltd)",
                    "Limited Liability Partnership (LLP)",
                    "Gold standard for scalable businesses, digital startups, and long-term brand equity."
                )
            }
        }

        val initialRegistrations = mutableListOf<String>()
        initialRegistrations.add("PAN & TAN in entity name")
        initialRegistrations.add("Business Current Bank Account")
        initialRegistrations.add("Udyam MSME Registration (100% Free & Paperless)")
        if (isOnline || turnoverRange != "Under 20L") {
            initialRegistrations.add("GST Registration (Mandatory for interstate e-commerce)")
        }
        initialRegistrations.add("Shop & Establishment / Trade License")
        if (seeksInvestment) {
            initialRegistrations.add("Startup India DPIIT Recognition & Tax Exemption")
        }

        val (govtFee, timeline) = when (rec) {
            "Sole Proprietorship" -> Pair("₹0 (Only state stamp duty for notary)", "1 - 2 Business Days")
            "Partnership Firm" -> Pair("₹500 - ₹2,000 (State stamp duty on deed)", "3 - 5 Business Days")
            "Limited Liability Partnership (LLP)" -> Pair("₹1,000 - ₹2,500 (MCA Filing fees)", "7 - 10 Business Days")
            "One Person Company (OPC)" -> Pair("₹1,500 - ₹3,000 (MCA SPICe+ fees)", "5 - 7 Business Days")
            "Private Limited Company (Pvt Ltd)" -> Pair("₹1,000 - ₹4,000 (MCA SPICe+ + Stamp duty)", "5 - 8 Business Days")
            else -> Pair("₹2,000", "5 - 7 Days")
        }

        val allComparisons = listOf(
            StructureComparison(
                entityName = "Sole Proprietorship",
                owners = "1 Owner",
                liability = "Unlimited Personal",
                complianceCost = "Very Low (~₹1,000/yr)",
                fundingPotential = "No Equity / Bank loans only",
                annualFilings = "Individual ITR-3/ITR-4",
                pros = listOf("Zero government setup fees", "Full privacy & total control", "Simple closure"),
                cons = listOf("Personal assets at risk", "Difficult to raise outside funding")
            ),
            StructureComparison(
                entityName = "Partnership Firm",
                owners = "2 to 20 Partners",
                liability = "Unlimited Joint Liability",
                complianceCost = "Low (~₹3,000/yr)",
                fundingPotential = "Partner Capital / Debt",
                annualFilings = "ITR-5 (30% flat tax rate)",
                pros = listOf("Easy to draft & register", "Flexible profit sharing agreements"),
                cons = listOf("Each partner can bind others to unlimited liability", "No separate legal entity")
            ),
            StructureComparison(
                entityName = "Limited Liability Partnership (LLP)",
                owners = "2 to Unlimited Partners",
                liability = "Limited to Capital Contribution",
                complianceCost = "Moderate (~₹8,000/yr)",
                fundingPotential = "Debt / Partner investment",
                annualFilings = "Form 8, Form 11, ITR-5",
                pros = listOf("Personal assets safe", "No mandatory audit under 40L turnover", "Separate legal entity"),
                cons = listOf("Cannot issue ESOPs or equity shares", "Flat 30% tax on profit")
            ),
            StructureComparison(
                entityName = "Private Limited Company",
                owners = "2 to 200 Shareholders",
                liability = "Limited to Share Value",
                complianceCost = "Higher (~₹15,000 - ₹25,000/yr)",
                fundingPotential = "High (Angel, VC, ESOPs)",
                annualFilings = "AOC-4, MGT-7, ITR-6, Statutory Audit",
                pros = listOf("Favorite of investors & banks", "22% corporate tax rate", "Perpetual succession"),
                cons = listOf("Mandatory CA audit every year", "Strict MCA compliance rules")
            )
        )

        return StructureRecommendation(
            recommendedEntity = rec,
            alternativeEntity = alt,
            rationale = why,
            initialRegistrations = initialRegistrations,
            estimatedGovtFee = govtFee,
            estimatedTimeline = timeline,
            comparison = allComparisons
        )
    }
}
