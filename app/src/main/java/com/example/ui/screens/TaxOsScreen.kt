package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.engine.TaxCalculationEngine
import com.example.data.model.TaxNoticeEntity
import com.example.data.model.TaxProfileEntity
import com.example.ui.components.ConceptExplanation
import com.example.ui.components.FlowAuthorityType
import com.example.ui.components.OfficialFlowBadge
import com.example.ui.components.UniversalExplanationDialog
import com.example.ui.theme.AppColors
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.RoyalBlue
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldLight

@Composable
fun TaxOsScreen(
    taxProfile: TaxProfileEntity?,
    notices: List<TaxNoticeEntity>,
    onRecomputeTax: (
        salary: Double, business: Double, rental: Double, capitalGains: Double, other: Double,
        deduction80C: Double, deduction80D: Double, deductionNps: Double, otherDeductions: Double,
        tdsPaid: Double, advanceTax: Double
    ) -> Unit,
    onSwitchRegime: (String) -> Unit,
    onSubmitItr: (withCaReview: Boolean) -> Unit,
    onUploadNotice: (section: String, summary: String, deadline: String, risk: String, plan: String) -> Unit,
    onAskAi: (String) -> Unit,
    onFindCa: () -> Unit
) {
    var selectedTaxTab by remember { mutableIntStateOf(0) } // 0: Income & Deductions, 1: Regime Comparison, 2: Notices & Refund
    var showExplanation by remember { mutableStateOf<ConceptExplanation?>(null) }

    // Form inputs state
    var salaryStr by remember { mutableStateOf(taxProfile?.salaryIncome?.toLong()?.toString() ?: "1240000") }
    var businessStr by remember { mutableStateOf(taxProfile?.businessIncome?.toLong()?.toString() ?: "0") }
    var rentalStr by remember { mutableStateOf(taxProfile?.rentalIncome?.toLong()?.toString() ?: "0") }
    var capitalGainsStr by remember { mutableStateOf(taxProfile?.capitalGains?.toLong()?.toString() ?: "0") }
    var otherIncomeStr by remember { mutableStateOf(taxProfile?.otherIncome?.toLong()?.toString() ?: "35000") }

    var deduction80CStr by remember { mutableStateOf(taxProfile?.deduction80C?.toLong()?.toString() ?: "150000") }
    var deduction80DStr by remember { mutableStateOf(taxProfile?.deduction80D?.toLong()?.toString() ?: "25000") }
    var deductionNpsStr by remember { mutableStateOf(taxProfile?.deductionNps?.toLong()?.toString() ?: "50000") }
    var otherDeductionsStr by remember { mutableStateOf("0") }

    var tdsPaidStr by remember { mutableStateOf(taxProfile?.tdsPaid?.toLong()?.toString() ?: "72400") }
    var advanceTaxStr by remember { mutableStateOf(taxProfile?.advanceTaxPaid?.toLong()?.toString() ?: "0") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.background)
    ) {
        TabRow(
            selectedTabIndex = selectedTaxTab,
            containerColor = AppColors.cardBackground,
            contentColor = AppColors.goldAccent,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTaxTab]),
                    color = AppColors.goldAccent
                )
            }
        ) {
            Tab(
                selected = selectedTaxTab == 0,
                onClick = { selectedTaxTab = 0 },
                text = {
                    Text(
                        "Income & Deductions",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedTaxTab == 0) AppColors.goldAccent else AppColors.textSecondary
                    )
                }
            )
            Tab(
                selected = selectedTaxTab == 1,
                onClick = { selectedTaxTab = 1 },
                text = {
                    Text(
                        "Regime & ITR Form",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedTaxTab == 1) AppColors.goldAccent else AppColors.textSecondary
                    )
                }
            )
            Tab(
                selected = selectedTaxTab == 2,
                onClick = { selectedTaxTab = 2 },
                text = {
                    Text(
                        "Notices & Refund",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedTaxTab == 2) AppColors.goldAccent else AppColors.textSecondary
                    )
                }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            when (selectedTaxTab) {
                0 -> {
                    // Income Discovery Form
                    IncomeDiscoverySection(
                        salaryStr = salaryStr,
                        onSalaryChange = { salaryStr = it },
                        businessStr = businessStr,
                        onBusinessChange = { businessStr = it },
                        rentalStr = rentalStr,
                        onRentalChange = { rentalStr = it },
                        capitalGainsStr = capitalGainsStr,
                        onCapitalGainsChange = { capitalGainsStr = it },
                        otherIncomeStr = otherIncomeStr,
                        onOtherIncomeChange = { otherIncomeStr = it },
                        deduction80CStr = deduction80CStr,
                        onDeduction80CChange = { deduction80CStr = it },
                        deduction80DStr = deduction80DStr,
                        onDeduction80DChange = { deduction80DStr = it },
                        deductionNpsStr = deductionNpsStr,
                        onDeductionNpsChange = { deductionNpsStr = it },
                        tdsPaidStr = tdsPaidStr,
                        onTdsPaidChange = { tdsPaidStr = it },
                        advanceTaxStr = advanceTaxStr,
                        onAdvanceTaxChange = { advanceTaxStr = it },
                        onCalculate = {
                            val sal = salaryStr.toDoubleOrNull() ?: 0.0
                            val bus = businessStr.toDoubleOrNull() ?: 0.0
                            val ren = rentalStr.toDoubleOrNull() ?: 0.0
                            val cap = capitalGainsStr.toDoubleOrNull() ?: 0.0
                            val oth = otherIncomeStr.toDoubleOrNull() ?: 0.0
                            val d80c = deduction80CStr.toDoubleOrNull() ?: 0.0
                            val d80d = deduction80DStr.toDoubleOrNull() ?: 0.0
                            val dnps = deductionNpsStr.toDoubleOrNull() ?: 0.0
                            val dOth = otherDeductionsStr.toDoubleOrNull() ?: 0.0
                            val tds = tdsPaidStr.toDoubleOrNull() ?: 0.0
                            val adv = advanceTaxStr.toDoubleOrNull() ?: 0.0
                            onRecomputeTax(sal, bus, ren, cap, oth, d80c, d80d, dnps, dOth, tds, adv)
                            selectedTaxTab = 1 // Navigate to comparison
                        },
                        onExplain = { showExplanation = it }
                    )
                }
                1 -> {
                    // Regime Comparison & ITR Routing
                    RegimeComparisonSection(
                        profile = taxProfile,
                        onSwitchRegime = onSwitchRegime,
                        onSubmitItr = onSubmitItr,
                        onExplain = { showExplanation = it },
                        onFindCa = onFindCa
                    )
                }
                2 -> {
                    // Notices Center & Refund Tracker
                    NoticesAndRefundSection(
                        profile = taxProfile,
                        notices = notices,
                        onUploadSampleNotice = {
                            onUploadNotice(
                                "Section 143(1) Intimation",
                                "Demand of ₹4,200 due to mismatch in TDS credit between Form 26AS and filed return.",
                                "30 Days (Due in 18 days)",
                                "MEDIUM",
                                "1. Verify Employer Form 16 Part A against AIS.\n2. Submit rectification request u/s 154 or agree to demand online."
                            )
                        },
                        onAskAi = onAskAi,
                        onFindCa = onFindCa
                    )
                }
            }
        }
    }

    showExplanation?.let {
        UniversalExplanationDialog(explanation = it, onDismiss = { showExplanation = null })
    }
}

@Composable
private fun IncomeDiscoverySection(
    salaryStr: String, onSalaryChange: (String) -> Unit,
    businessStr: String, onBusinessChange: (String) -> Unit,
    rentalStr: String, onRentalChange: (String) -> Unit,
    capitalGainsStr: String, onCapitalGainsChange: (String) -> Unit,
    otherIncomeStr: String, onOtherIncomeChange: (String) -> Unit,
    deduction80CStr: String, onDeduction80CChange: (String) -> Unit,
    deduction80DStr: String, onDeduction80DChange: (String) -> Unit,
    deductionNpsStr: String, onDeductionNpsChange: (String) -> Unit,
    tdsPaidStr: String, onTdsPaidChange: (String) -> Unit,
    advanceTaxStr: String, onAdvanceTaxChange: (String) -> Unit,
    onCalculate: () -> Unit,
    onExplain: (ConceptExplanation) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("INCOME DISCOVERY", fontSize = 11.sp, fontWeight = FontWeight.Black, color = RoyalGold)
                    Text("Tell us what happened with your money this year", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                OfficialFlowBadge(
                    authorityType = FlowAuthorityType.OFFICIAL_GOVERNMENT,
                    label = "FY 2024-25 (AY 2025-26)"
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            TaxInputField(label = "1. Gross Salary (Form 16 Part B)", value = salaryStr, onValueChange = onSalaryChange)
            Spacer(modifier = Modifier.height(8.dp))
            TaxInputField(label = "2. Business / Freelance Profits (Sec 44AD/44ADA)", value = businessStr, onValueChange = onBusinessChange)
            Spacer(modifier = Modifier.height(8.dp))
            TaxInputField(label = "3. Rental Income from House Property", value = rentalStr, onValueChange = onRentalChange)
            Spacer(modifier = Modifier.height(8.dp))
            TaxInputField(label = "4. Capital Gains (Stocks, Mutual Funds, Property)", value = capitalGainsStr, onValueChange = onCapitalGainsChange)
            Spacer(modifier = Modifier.height(8.dp))
            TaxInputField(label = "5. Other Income (Savings Interest, FD, Dividends)", value = otherIncomeStr, onValueChange = onOtherIncomeChange)

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = Color(0xFF23304A))
            Spacer(modifier = Modifier.height(14.dp))

            Text("CHAPTER VI-A DEDUCTIONS (FOR OLD REGIME)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = RoyalGoldLight)
            Spacer(modifier = Modifier.height(10.dp))

            TaxInputField(label = "Section 80C (EPF, PPF, ELSS, LIC - Max 1.5L)", value = deduction80CStr, onValueChange = onDeduction80CChange)
            Spacer(modifier = Modifier.height(8.dp))
            TaxInputField(label = "Section 80D (Health Insurance Mediclaim - Max 50k)", value = deduction80DStr, onValueChange = onDeduction80DChange)
            Spacer(modifier = Modifier.height(8.dp))
            TaxInputField(label = "Section 80CCD(1B) (NPS Voluntary - Extra 50k)", value = deductionNpsStr, onValueChange = onDeductionNpsChange)

            Spacer(modifier = Modifier.height(18.dp))
            HorizontalDivider(color = Color(0xFF23304A))
            Spacer(modifier = Modifier.height(14.dp))

            Text("TAXES ALREADY PAID (AS PER 26AS / AIS)", fontSize = 11.sp, fontWeight = FontWeight.Black, color = EmeraldGreen)
            Spacer(modifier = Modifier.height(10.dp))

            TaxInputField(label = "TDS Deducted by Employers / Clients", value = tdsPaidStr, onValueChange = onTdsPaidChange)
            Spacer(modifier = Modifier.height(8.dp))
            TaxInputField(label = "Advance Tax / Self-Assessment Tax Paid", value = advanceTaxStr, onValueChange = onAdvanceTaxChange)

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = onCalculate,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Compute & Compare New vs Old Regime", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun TaxInputField(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label, fontSize = 11.sp) },
        prefix = { Text("₹ ", color = RoyalGold, fontWeight = FontWeight.Bold) },
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Color.White,
            unfocusedTextColor = Color.White,
            focusedBorderColor = RoyalGold,
            unfocusedBorderColor = DarkCardBorder
        ),
        shape = RoundedCornerShape(10.dp),
        singleLine = true
    )
}

@Composable
private fun RegimeComparisonSection(
    profile: TaxProfileEntity?,
    onSwitchRegime: (String) -> Unit,
    onSubmitItr: (withCaReview: Boolean) -> Unit,
    onExplain: (ConceptExplanation) -> Unit,
    onFindCa: () -> Unit
) {
    if (profile == null) {
        Text("Please enter your income details in the first tab to calculate tax.", color = Color.White)
        return
    }

    // Recommendation Banner (Rule 30: Compare side-by-side)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.5.dp, RoyalGold, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A2E))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OfficialFlowBadge(
                    authorityType = FlowAuthorityType.TAXMITRA_AI,
                    label = "AI TAX OPTIMIZER"
                )
                Text(
                    text = "SAVE ₹${String.format("%,.0f", profile.taxSavings)}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Black,
                    color = EmeraldGreen
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "We Recommend: ${profile.recommendedRegime} TAX REGIME",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = if (profile.recommendedRegime == "NEW")
                    "Under Budget 2024 revised slabs, the Standard Deduction is increased to ₹75,000 with 0% tax up to ₹7,75,000 gross salary!"
                else
                    "Your high 80C, 80D, and NPS deductions make the Old Regime more beneficial for you.",
                fontSize = 12.sp,
                color = Color(0xFFCBD5E1),
                modifier = Modifier.padding(vertical = 6.dp)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // Side-by-side Table
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // New Regime Card
                RegimeOptionCard(
                    regimeName = "NEW REGIME (115BAC)",
                    standardDeduction = "₹75,000",
                    taxAmount = profile.taxNewRegime,
                    isSelected = profile.selectedRegime == "NEW",
                    isRecommended = profile.recommendedRegime == "NEW",
                    onSelect = { onSwitchRegime("NEW") },
                    modifier = Modifier.weight(1f)
                )

                // Old Regime Card
                RegimeOptionCard(
                    regimeName = "OLD REGIME",
                    standardDeduction = "₹50,000 + 80C/80D",
                    taxAmount = profile.taxOldRegime,
                    isSelected = profile.selectedRegime == "OLD",
                    isRecommended = profile.recommendedRegime == "OLD",
                    onSelect = { onSwitchRegime("OLD") },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // ITR Route & Refund Card (Rule 28)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("ITR FORM ROUTING ENGINE", fontSize = 11.sp, fontWeight = FontWeight.Black, color = RoyalGold)
                Text(profile.filingRoute, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(profile.filingRouteReason, fontSize = 12.sp, color = Color(0xFF94A3B8))

            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color(0xFF23304A))
            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Total Taxes Paid (TDS + Adv)", fontSize = 11.sp, color = Color(0xFF94A3B8))
                    Text("₹${String.format("%,.0f", profile.tdsPaid + profile.advanceTaxPaid)}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Column(horizontalAlignment = Alignment.End) {
                    if (profile.refundAmount > 0) {
                        Text("ESTIMATED REFUND", fontSize = 11.sp, fontWeight = FontWeight.Black, color = EmeraldGreen)
                        Text("₹${String.format("%,.0f", profile.refundAmount)}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = EmeraldGreen)
                    } else {
                        Text("TAX PAYABLE", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color(0xFFEF4444))
                        Text("₹${String.format("%,.0f", profile.taxPayable)}", fontSize = 18.sp, fontWeight = FontWeight.Black, color = Color(0xFFEF4444))
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onFindCa,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Get CA Review (₹1,499)", color = RoyalGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }

                Button(
                    onClick = { onSubmitItr(false) },
                    modifier = Modifier.weight(1.1f),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text("Proceed to E-File", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@Composable
private fun RegimeOptionCard(
    regimeName: String,
    standardDeduction: String,
    taxAmount: Double,
    isSelected: Boolean,
    isRecommended: Boolean,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .clickable { onSelect() }
            .border(
                1.5.dp,
                if (isSelected) RoyalGold else Color(0xFF23304A),
                RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (isSelected) Color(0xFF1E293B) else DarkSurface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            if (isRecommended) {
                Box(
                    modifier = Modifier
                        .background(EmeraldGreen, RoundedCornerShape(4.dp))
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text("RECOMMENDED", fontSize = 8.sp, fontWeight = FontWeight.Black, color = Color.Black)
                }
                Spacer(modifier = Modifier.height(4.dp))
            }
            Text(regimeName, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text("Std Ded: $standardDeduction", fontSize = 10.sp, color = Color(0xFF94A3B8))
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "₹${String.format("%,.0f", taxAmount)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.Black,
                color = if (isSelected) RoyalGold else Color.White
            )
        }
    }
}

@Composable
private fun NoticesAndRefundSection(
    profile: TaxProfileEntity?,
    notices: List<TaxNoticeEntity>,
    onUploadSampleNotice: () -> Unit,
    onAskAi: (String) -> Unit,
    onFindCa: () -> Unit
) {
    // 1. Refund Tracker (Rule 36)
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OfficialFlowBadge(
                    authorityType = FlowAuthorityType.OFFICIAL_GOVERNMENT,
                    label = "ITR STATUS & REFUND TRACKER"
                )
                Text(
                    text = profile?.status ?: "DRAFT",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    color = EmeraldGreen
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text("AY 2025-26 Return Lifecycle", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(
                text = "Track your return from preparation to bank refund credit via official CPC handshake.",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8)
            )

            Spacer(modifier = Modifier.height(14.dp))

            // 4 Step Tracker
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                TrackerStep(label = "1. Filed", isDone = true)
                TrackerStep(label = "2. e-Verified", isDone = true)
                TrackerStep(label = "3. CPC Processed", isDone = false, isActive = true)
                TrackerStep(label = "4. Refund Sent", isDone = false)
            }
        }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // 2. Tax Notice Center (Rule 35 & 141)
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("TAX NOTICE CENTER", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color.White)
            Text("Don't panic. AI breaks down demands & action plans.", fontSize = 11.sp, color = Color(0xFF94A3B8))
        }
        Button(
            onClick = onUploadSampleNotice,
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
            shape = RoundedCornerShape(10.dp)
        ) {
            Text("Upload Notice PDF", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 11.sp)
        }
    }

    Spacer(modifier = Modifier.height(12.dp))

    if (notices.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A2E)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF1E293B))
        ) {
            Column(modifier = Modifier.padding(18.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.Security, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(36.dp))
                Spacer(modifier = Modifier.height(6.dp))
                Text("Zero Active Tax Notices", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 14.sp)
                Text("Your PAN is fully compliant with no pending CPC demands.", color = Color(0xFF94A3B8), fontSize = 12.sp)
            }
        }
    } else {
        notices.forEach { notice ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF161F33)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(notice.section, fontSize = 14.sp, fontWeight = FontWeight.Black, color = RoyalGold)
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF451A03), RoundedCornerShape(6.dp))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text("RISK: ${notice.riskLevel}", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFFF59E0B))
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(notice.summary, fontSize = 13.sp, color = Color.White, lineHeight = 18.sp)

                    Spacer(modifier = Modifier.height(6.dp))
                    Text("Response Deadline: ${notice.responseDeadline}", fontSize = 11.sp, color = Color(0xFFEF4444), fontWeight = FontWeight.Bold)

                    Spacer(modifier = Modifier.height(8.dp))
                    Text("AI ACTION PLAN:", fontSize = 10.sp, fontWeight = FontWeight.Black, color = RoyalGoldLight)
                    Text(notice.actionPlan, fontSize = 11.sp, color = Color(0xFFCBD5E1), lineHeight = 16.sp)

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = onFindCa,
                            colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Escalate to Verified CA", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                        }
                        OutlinedButton(
                            onClick = { onAskAi("How do I respond to this ${notice.section} notice?") },
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text("Ask Taxmitra", color = Color.White, fontSize = 11.sp)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TrackerStep(label: String, isDone: Boolean, isActive: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isDone -> EmeraldGreen
                        isActive -> RoyalGold
                        else -> Color(0xFF1E293B)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isDone) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
            } else {
                Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(if (isActive) Color.Black else Color.Gray))
            }
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 9.sp,
            fontWeight = FontWeight.Medium,
            color = if (isDone || isActive) Color.White else Color.Gray
        )
    }
}
