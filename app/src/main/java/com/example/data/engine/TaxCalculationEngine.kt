package com.example.data.engine

data class TaxCalculationResult(
    val grossTotalIncome: Double,
    val totalDeductionsOld: Double,
    val taxableIncomeOld: Double,
    val taxBeforeCessOld: Double,
    val rebate87AOld: Double,
    val cessOld: Double,
    val totalTaxOld: Double,

    val totalDeductionsNew: Double,
    val taxableIncomeNew: Double,
    val taxBeforeCessNew: Double,
    val rebate87ANew: Double,
    val cessNew: Double,
    val totalTaxNew: Double,

    val recommendedRegime: String, // "NEW" or "OLD"
    val taxDifference: Double,
    val recommendedItrForm: String, // "ITR-1 (Sahaj)", "ITR-2", "ITR-3", "ITR-4 (Sugam)"
    val itrReason: String,
    val tdsDeducted: Double,
    val advanceTaxPaid: Double,
    val netPayableOrRefund: Double, // Negative means refund, positive means payable
    val isRefund: Boolean
)

object TaxCalculationEngine {
    const val RULE_VERSION = "Income Tax Act 1961 - FY 2024-25 / AY 2025-26 (Finance Act 2024)"

    fun calculate(
        salaryIncome: Double,
        businessIncome: Double,
        rentalIncome: Double,
        capitalGains: Double,
        otherIncome: Double,
        deduction80C: Double,
        deduction80D: Double,
        deductionNps: Double,
        otherDeductions: Double,
        tdsPaid: Double,
        advanceTaxPaid: Double
    ): TaxCalculationResult {
        val grossIncome = salaryIncome + businessIncome + rentalIncome + capitalGains + otherIncome

        // ==========================================
        // 1. OLD REGIME CALCULATION
        // ==========================================
        val standardDeductionOld = if (salaryIncome > 0) minOf(salaryIncome, 50000.0) else 0.0
        val capped80C = minOf(deduction80C, 150000.0)
        val capped80D = minOf(deduction80D, 50000.0)
        val cappedNps = minOf(deductionNps, 50000.0)
        val totalDeductionsOld = standardDeductionOld + capped80C + capped80D + cappedNps + otherDeductions

        val taxableIncomeOld = maxOf(0.0, grossIncome - totalDeductionsOld)

        var taxOld = 0.0
        if (taxableIncomeOld > 1000000.0) {
            taxOld += (taxableIncomeOld - 1000000.0) * 0.30
            taxOld += 500000.0 * 0.20 // 5L to 10L
            taxOld += 250000.0 * 0.05 // 2.5L to 5L
        } else if (taxableIncomeOld > 500000.0) {
            taxOld += (taxableIncomeOld - 500000.0) * 0.20
            taxOld += 250000.0 * 0.05 // 2.5L to 5L
        } else if (taxableIncomeOld > 250000.0) {
            taxOld += (taxableIncomeOld - 250000.0) * 0.05
        }

        // Section 87A Rebate for Old Regime (Max 12,500 if income <= 5L)
        val rebateOld = if (taxableIncomeOld <= 500000.0) taxOld else 0.0
        val taxAfterRebateOld = maxOf(0.0, taxOld - rebateOld)
        val cessOld = taxAfterRebateOld * 0.04
        val totalTaxOld = taxAfterRebateOld + cessOld

        // ==========================================
        // 2. NEW REGIME CALCULATION (Sec 115BAC)
        // ==========================================
        // Finance Act 2024: Standard deduction increased to ₹75,000 for salaried
        val standardDeductionNew = if (salaryIncome > 0) minOf(salaryIncome, 75000.0) else 0.0
        val totalDeductionsNew = standardDeductionNew
        val taxableIncomeNew = maxOf(0.0, grossIncome - totalDeductionsNew)

        var taxNew = 0.0
        if (taxableIncomeNew > 1500000.0) {
            taxNew += (taxableIncomeNew - 1500000.0) * 0.30
            taxNew += 300000.0 * 0.20 // 12L to 15L
            taxNew += 200000.0 * 0.15 // 10L to 12L
            taxNew += 300000.0 * 0.10 // 7L to 10L
            taxNew += 400000.0 * 0.05 // 3L to 7L
        } else if (taxableIncomeNew > 1200000.0) {
            taxNew += (taxableIncomeNew - 1200000.0) * 0.20
            taxNew += 200000.0 * 0.15
            taxNew += 300000.0 * 0.10
            taxNew += 400000.0 * 0.05
        } else if (taxableIncomeNew > 1000000.0) {
            taxNew += (taxableIncomeNew - 1000000.0) * 0.15
            taxNew += 300000.0 * 0.10
            taxNew += 400000.0 * 0.05
        } else if (taxableIncomeNew > 700000.0) {
            taxNew += (taxableIncomeNew - 700000.0) * 0.10
            taxNew += 400000.0 * 0.05
        } else if (taxableIncomeNew > 300000.0) {
            taxNew += (taxableIncomeNew - 300000.0) * 0.05
        }

        // Section 87A Rebate for New Regime (Max 25,000 if taxable income <= 7L)
        val rebateNew = if (taxableIncomeNew <= 700000.0) taxNew else 0.0
        val taxAfterRebateNew = maxOf(0.0, taxNew - rebateNew)
        val cessNew = taxAfterRebateNew * 0.04
        val totalTaxNew = taxAfterRebateNew + cessNew

        // ==========================================
        // 3. COMPARISON & SELECTION
        // ==========================================
        val recommendedRegime = if (totalTaxNew <= totalTaxOld) "NEW" else "OLD"
        val chosenTax = if (recommendedRegime == "NEW") totalTaxNew else totalTaxOld
        val difference = kotlin.math.abs(totalTaxOld - totalTaxNew)

        // ==========================================
        // 4. ITR ROUTING ENGINE (Rule 28)
        // ==========================================
        val (itrForm, itrReason) = when {
            businessIncome > 0 && grossIncome <= 20000000.0 && capitalGains == 0.0 ->
                Pair("ITR-4 (Sugam)", "Presumptive business/profession income under Sec 44AD/44ADA with turnover under ₹2 Cr")
            businessIncome > 0 || grossIncome > 50000000.0 ->
                Pair("ITR-3", "Business/Professional income with detailed books of accounts or turnover > ₹2 Cr")
            capitalGains > 0 || rentalIncome > 0 && grossIncome > 5000000.0 ->
                Pair("ITR-2", "Capital gains from mutual funds/stocks or multiple house properties")
            grossIncome <= 5000000.0 ->
                Pair("ITR-1 (Sahaj)", "Salaried income with single house property & bank interest under ₹50 Lakhs")
            else ->
                Pair("ITR-2", "Total income exceeding ₹50 Lakhs without business profits")
        }

        // ==========================================
        // 5. TAX PAYABLE VS REFUND
        // ==========================================
        val totalTaxPaid = tdsPaid + advanceTaxPaid
        val netDifference = chosenTax - totalTaxPaid
        val isRefund = netDifference < 0

        return TaxCalculationResult(
            grossTotalIncome = grossIncome,
            totalDeductionsOld = totalDeductionsOld,
            taxableIncomeOld = taxableIncomeOld,
            taxBeforeCessOld = taxOld,
            rebate87AOld = rebateOld,
            cessOld = cessOld,
            totalTaxOld = totalTaxOld,

            totalDeductionsNew = totalDeductionsNew,
            taxableIncomeNew = taxableIncomeNew,
            taxBeforeCessNew = taxNew,
            rebate87ANew = rebateNew,
            cessNew = cessNew,
            totalTaxNew = totalTaxNew,

            recommendedRegime = recommendedRegime,
            taxDifference = difference,
            recommendedItrForm = itrForm,
            itrReason = itrReason,
            tdsDeducted = tdsPaid,
            advanceTaxPaid = advanceTaxPaid,
            netPayableOrRefund = kotlin.math.abs(netDifference),
            isRefund = isRefund
        )
    }
}
