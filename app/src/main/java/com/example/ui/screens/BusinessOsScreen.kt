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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.engine.BusinessStructureEngine
import com.example.data.engine.StructureRecommendation
import com.example.data.model.BusinessEntity
import com.example.data.model.InvoiceEntity
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
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldLight

@Composable
fun BusinessOsScreen(
    business: BusinessEntity?,
    invoices: List<InvoiceEntity>,
    onSaveBusiness: (name: String, industry: String, founders: Int, seeksFunding: Boolean, wantsLtd: Boolean, turnover: String, isOnline: Boolean) -> Unit,
    onCompleteUdyam: () -> Unit,
    onUpdateGstStatus: (String) -> Unit,
    onCreateInvoice: (clientName: String, clientGst: String, itemsSummary: String, subtotal: Double, gstRate: Double) -> Unit,
    onAskAi: (String) -> Unit
) {
    var selectedSubTab by remember { mutableIntStateOf(0) } // 0: Overview & Wizard, 1: Structure & Registrations, 2: Invoices & Finance
    var showExplanationDialog by remember { mutableStateOf<ConceptExplanation?>(null) }
    var showCreateInvoiceDialog by remember { mutableStateOf(false) }

    // Wizard Form State
    var businessName by remember { mutableStateOf(business?.name ?: "VastraMitra Apparels") }
    var industry by remember { mutableStateOf(business?.industry ?: "Clothing & Fashion") }
    var founderCount by remember { mutableIntStateOf(business?.founderCount ?: 2) }
    var seeksFunding by remember { mutableStateOf(false) }
    var wantsLimitedLiability by remember { mutableStateOf(true) }
    var turnoverRange by remember { mutableStateOf(business?.turnoverRange ?: "20L - 40L") }
    var isOnline by remember { mutableStateOf(business?.isOnline ?: true) }

    // Recommendation Engine state
    var currentRecommendation by remember {
        mutableStateOf(
            BusinessStructureEngine.evaluate(
                founderCount = founderCount,
                seeksInvestment = seeksFunding,
                wantsLimitedLiability = wantsLimitedLiability,
                turnoverRange = turnoverRange,
                isOnline = isOnline
            )
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.background)
    ) {
        // Sub-tabs
        TabRow(
            selectedTabIndex = selectedSubTab,
            containerColor = AppColors.cardBackground,
            contentColor = AppColors.goldAccent,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedSubTab]),
                    color = AppColors.goldAccent
                )
            }
        ) {
            Tab(
                selected = selectedSubTab == 0,
                onClick = { selectedSubTab = 0 },
                text = {
                    Text(
                        "Launch Wizard",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedSubTab == 0) AppColors.goldAccent else AppColors.textSecondary
                    )
                }
            )
            Tab(
                selected = selectedSubTab == 1,
                onClick = { selectedSubTab = 1 },
                text = {
                    Text(
                        "Registrations & Brand",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedSubTab == 1) AppColors.goldAccent else AppColors.textSecondary
                    )
                }
            )
            Tab(
                selected = selectedSubTab == 2,
                onClick = { selectedSubTab = 2 },
                text = {
                    Text(
                        "Invoices & P&L",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selectedSubTab == 2) AppColors.goldAccent else AppColors.textSecondary
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
            when (selectedSubTab) {
                0 -> {
                    // Business Discovery & Structure Engine
                    BusinessDiscoveryWizard(
                        business = business,
                        businessName = businessName,
                        onNameChange = { businessName = it },
                        industry = industry,
                        onIndustryChange = { industry = it },
                        founderCount = founderCount,
                        onFounderCountChange = {
                            founderCount = it
                            currentRecommendation = BusinessStructureEngine.evaluate(it, seeksFunding, wantsLimitedLiability, turnoverRange, isOnline)
                        },
                        seeksFunding = seeksFunding,
                        onSeeksFundingChange = {
                            seeksFunding = it
                            currentRecommendation = BusinessStructureEngine.evaluate(founderCount, it, wantsLimitedLiability, turnoverRange, isOnline)
                        },
                        wantsLimitedLiability = wantsLimitedLiability,
                        onWantsLtdChange = {
                            wantsLimitedLiability = it
                            currentRecommendation = BusinessStructureEngine.evaluate(founderCount, seeksFunding, it, turnoverRange, isOnline)
                        },
                        turnoverRange = turnoverRange,
                        onTurnoverChange = {
                            turnoverRange = it
                            currentRecommendation = BusinessStructureEngine.evaluate(founderCount, seeksFunding, wantsLimitedLiability, it, isOnline)
                        },
                        isOnline = isOnline,
                        onIsOnlineChange = {
                            isOnline = it
                            currentRecommendation = BusinessStructureEngine.evaluate(founderCount, seeksFunding, wantsLimitedLiability, turnoverRange, it)
                        },
                        recommendation = currentRecommendation,
                        onSave = {
                            onSaveBusiness(businessName, industry, founderCount, seeksFunding, wantsLimitedLiability, turnoverRange, isOnline)
                        },
                        onExplainConcept = { showExplanationDialog = it }
                    )
                }
                1 -> {
                    // Registrations: Udyam, GST, Trademark, Domain
                    BusinessRegistrationsView(
                        business = business,
                        onCompleteUdyam = onCompleteUdyam,
                        onUpdateGst = onUpdateGstStatus,
                        onExplainConcept = { showExplanationDialog = it },
                        onAskAi = onAskAi
                    )
                }
                2 -> {
                    // Invoices & Accounting
                    BusinessInvoicesView(
                        invoices = invoices,
                        onCreateInvoiceClick = { showCreateInvoiceDialog = true },
                        onExplainConcept = { showExplanationDialog = it }
                    )
                }
            }
        }
    }

    // Concept Explanation Dialog
    showExplanationDialog?.let { explanation ->
        UniversalExplanationDialog(
            explanation = explanation,
            onDismiss = { showExplanationDialog = null }
        )
    }

    // Create Invoice Dialog
    if (showCreateInvoiceDialog) {
        CreateInvoiceDialog(
            onDismiss = { showCreateInvoiceDialog = false },
            onCreate = { name, gst, items, subtotal, rate ->
                onCreateInvoice(name, gst, items, subtotal, rate)
                showCreateInvoiceDialog = false
            }
        )
    }
}

@Composable
private fun BusinessDiscoveryWizard(
    business: BusinessEntity?,
    businessName: String,
    onNameChange: (String) -> Unit,
    industry: String,
    onIndustryChange: (String) -> Unit,
    founderCount: Int,
    onFounderCountChange: (Int) -> Unit,
    seeksFunding: Boolean,
    onSeeksFundingChange: (Boolean) -> Unit,
    wantsLimitedLiability: Boolean,
    onWantsLtdChange: (Boolean) -> Unit,
    turnoverRange: String,
    onTurnoverChange: (String) -> Unit,
    isOnline: Boolean,
    onIsOnlineChange: (Boolean) -> Unit,
    recommendation: StructureRecommendation,
    onSave: () -> Unit,
    onExplainConcept: (ConceptExplanation) -> Unit
) {
    // Readiness Score Card
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
                Column {
                    Text("BUSINESS READINESS SCORE", fontSize = 11.sp, fontWeight = FontWeight.Black, color = RoyalGold)
                    Text(
                        text = "${business?.readinessScore ?: 45}% Complete",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
                OfficialFlowBadge(
                    authorityType = FlowAuthorityType.TAXMITRA_AI,
                    label = "AI BUSINESS OS"
                )
            }
            Spacer(modifier = Modifier.height(10.dp))
            LinearProgressIndicator(
                progress = { (business?.readinessScore ?: 45) / 100f },
                modifier = Modifier.fillMaxWidth().height(8.dp),
                color = RoyalGold,
                trackColor = Color(0xFF1E293B)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Structure analyzed • Free Udyam ready to claim • GST applicability calculated",
                fontSize = 11.sp,
                color = Color(0xFF94A3B8)
            )
        }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // Discovery Form
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = DarkSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "BUSINESS DISCOVERY",
                fontSize = 13.sp,
                fontWeight = FontWeight.Black,
                color = Color.White
            )
            Text(
                text = "Tell Taxmitra about your venture. We determine the exact legal structure.",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8),
                modifier = Modifier.padding(top = 2.dp, bottom = 14.dp)
            )

            OutlinedTextField(
                value = businessName,
                onValueChange = onNameChange,
                label = { Text("Proposed Business Name") },
                modifier = Modifier.fillMaxWidth(),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedBorderColor = RoyalGold,
                    unfocusedBorderColor = DarkCardBorder
                ),
                shape = RoundedCornerShape(12.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Industry
            Text("Industry / Activity", fontSize = 12.sp, color = Color(0xFFCBD5E1), fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("Clothing & Fashion", "Tech SaaS", "Retail", "Consulting").forEach { ind ->
                    FilterChip(
                        selected = industry == ind,
                        onClick = { onIndustryChange(ind) },
                        label = { Text(ind, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RoyalGold,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Founders Slider
            Text("Number of Founders / Partners: $founderCount", fontSize = 12.sp, color = Color(0xFFCBD5E1), fontWeight = FontWeight.SemiBold)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 6.dp)) {
                listOf(1, 2, 3, 4).forEach { count ->
                    FilterChip(
                        selected = founderCount == count,
                        onClick = { onFounderCountChange(count) },
                        label = { Text(if (count == 1) "Just Me (1)" else "$count Founders", fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = RoyalGold,
                            selectedLabelColor = Color.Black
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Liability & Online toggles
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Limited Personal Liability", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                    Text("Protects personal house & savings from business debt", fontSize = 11.sp, color = Color(0xFF94A3B8))
                }
                Switch(
                    checked = wantsLimitedLiability,
                    onCheckedChange = onWantsLtdChange,
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = RoyalGold)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Online E-Commerce / App", fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                    Text("Selling interstate via Amazon, Meesho, or Shopify", fontSize = 11.sp, color = Color(0xFF94A3B8))
                }
                Switch(
                    checked = isOnline,
                    onCheckedChange = onIsOnlineChange,
                    colors = SwitchDefaults.colors(checkedThumbColor = Color.Black, checkedTrackColor = RoyalGold)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onSave,
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Save & Apply Recommendations", color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }
    }

    Spacer(modifier = Modifier.height(18.dp))

    // AI Recommendation Result Card (Rule 10)
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, RoyalGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A2E))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("AI STRUCTURE RECOMMENDATION", fontSize = 11.sp, fontWeight = FontWeight.Black, color = RoyalGold)
                OfficialFlowBadge(
                    authorityType = FlowAuthorityType.OFFICIAL_GOVERNMENT,
                    label = "COMPANIES ACT / MCA"
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = recommendation.recommendedEntity,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )

            Text(
                text = recommendation.rationale,
                fontSize = 13.sp,
                color = Color(0xFFCBD5E1),
                modifier = Modifier.padding(vertical = 6.dp),
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Column {
                    Text("ESTIMATED GOVT FEE", fontSize = 10.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                    Text(recommendation.estimatedGovtFee, fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                }
                Column {
                    Text("TIMELINE", fontSize = 10.sp, color = Color(0xFF94A3B8), fontWeight = FontWeight.Bold)
                    Text(recommendation.estimatedTimeline, fontSize = 13.sp, color = Color.White, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text("Required Setup Checklist:", fontSize = 12.sp, color = RoyalGold, fontWeight = FontWeight.Bold)
            recommendation.initialRegistrations.forEach { reg ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = EmeraldGreen,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(reg, fontSize = 12.sp, color = Color(0xFFE2E8F0))
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = {
                    onExplainConcept(
                        ConceptExplanation(
                            title = recommendation.recommendedEntity,
                            whatIsIt = "A legally recognized corporate entity in India governed under MCA rules.",
                            whyItMatters = "Provides credibility, permits business bank accounts, and protects personal wealth.",
                            doINeedIt = "Yes, if you plan to hire, sign commercial leases, or sell online.",
                            costEstimate = recommendation.estimatedGovtFee,
                            whoHandlesIt = "Registrar of Companies (ROC) via MCA V3 portal.",
                            whatHappensNext = "Taxmitra prepares SPICe+ / FiLLiP draft forms for digital signature (DSC).",
                            canIDoItMyself = "Yes via MCA portal, or with 1-click CA review on Taxmitra.",
                            whenToAskCa = "Recommended when drafting complex profit-sharing clauses or investor rights."
                        )
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp)
            ) {
                Icon(imageVector = Icons.Default.Info, contentDescription = null, tint = RoyalGold, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Why this structure? (Deep Explanation)", color = RoyalGold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun BusinessRegistrationsView(
    business: BusinessEntity?,
    onCompleteUdyam: () -> Unit,
    onUpdateGst: (String) -> Unit,
    onExplainConcept: (ConceptExplanation) -> Unit,
    onAskAi: (String) -> Unit
) {
    // 1. Udyam MSME Card (Rule 19: 100% Free Official portal)
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
                    label = "MINISTRY OF MSME, GOVT OF INDIA"
                )
                Text("FREE & PAPERLESS", fontSize = 10.sp, fontWeight = FontWeight.Black, color = EmeraldGreen)
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Udyam MSME Registration", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(
                text = "Official government certificate for micro/small businesses. Zero government fee. Unlocks Section 43B(h) 45-day payment guarantee and bank loan interest subsidies.",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8),
                modifier = Modifier.padding(vertical = 4.dp),
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                if (business?.udyamStatus == "REGISTERED") {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Registered & Active", color = EmeraldGreen, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    }
                } else {
                    Button(
                        onClick = onCompleteUdyam,
                        colors = ButtonDefaults.buttonColors(containerColor = EmeraldGreen),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Claim Free Certificate", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }

                OutlinedButton(
                    onClick = {
                        onExplainConcept(
                            ConceptExplanation(
                                title = "Udyam MSME Certificate",
                                whatIsIt = "A 12-digit permanent identity issued by the Ministry of MSME.",
                                whyItMatters = "Buyers MUST pay you within 15-45 days under Sec 43B(h) or face non-deductible tax disallowance.",
                                doINeedIt = "All micro & small enterprises should register.",
                                costEstimate = "₹0 (100% Free officially on udyamregistration.gov.in)",
                                whoHandlesIt = "Ministry of MSME.",
                                whatHappensNext = "Immediate e-certificate with QR code verification.",
                                canIDoItMyself = "Yes, completely online with Aadhaar & PAN.",
                                whenToAskCa = "Only if turnover crosses ₹50 Crores."
                            )
                        )
                    },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Learn Benefits", color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 2. GST Registration Card (Rule 18)
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
                    label = "GSTN / CBIC PORTAL"
                )
                Text(
                    text = if (business?.gstRequired == true) "MANDATORY" else "VOLUNTARY / EXEMPT",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Black,
                    color = if (business?.gstRequired == true) RoyalGold else EmeraldGreen
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("GST Registration & Input Tax Credit", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(
                text = "Turnover threshold: ₹40L for goods, ₹20L for services. Mandatory if selling interstate or through e-commerce portals (Amazon, Flipkart).",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8),
                modifier = Modifier.padding(vertical = 4.dp),
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                Button(
                    onClick = { onUpdateGst("APPLIED") },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Apply for GSTIN", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = { onAskAi("Do I need GST registration for my specific business?") },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Check Threshold", color = Color.White, fontSize = 12.sp)
                }
            }
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // 3. Trademark & Brand Protection (Rule 12)
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
                    label = "IP INDIA (TRADEMARK REGISTRY)"
                )
                Text("CLASS 25 (CLOTHING)", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF38BDF8))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text("Trademark Screening & Class Guidance", fontSize = 17.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(
                text = "Protect your brand name and logo across India. Udyam MSME holders get 50% discount on official government filing fees (₹4,500 instead of ₹9,000).",
                fontSize = 12.sp,
                color = Color(0xFF94A3B8),
                modifier = Modifier.padding(vertical = 4.dp),
                lineHeight = 17.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = { onAskAi("Screen my brand name for trademark conflicts in Class 25") },
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                shape = RoundedCornerShape(10.dp)
            ) {
                Text("Run Trademark Similarity Scan", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun BusinessInvoicesView(
    invoices: List<InvoiceEntity>,
    onCreateInvoiceClick: () -> Unit,
    onExplainConcept: (ConceptExplanation) -> Unit
) {
    // Top Action Bar
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text("INVOICES & ACCOUNTS", fontSize = 13.sp, fontWeight = FontWeight.Black, color = Color.White)
            Text("GST-compliant bills with instant PDF & payment tracking", fontSize = 11.sp, color = Color(0xFF94A3B8))
        }
        Button(
            onClick = onCreateInvoiceClick,
            colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
            shape = RoundedCornerShape(10.dp)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text("New Invoice", color = Color.Black, fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }
    }

    Spacer(modifier = Modifier.height(14.dp))

    // Invoice list
    if (invoices.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Icon(Icons.Default.Receipt, contentDescription = null, tint = RoyalGold, modifier = Modifier.size(40.dp))
                Spacer(modifier = Modifier.height(10.dp))
                Text("No Invoices Created Yet", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("Create your first GST invoice in 30 seconds.", fontSize = 12.sp, color = Color(0xFF94A3B8))
                Spacer(modifier = Modifier.height(14.dp))
                Button(
                    onClick = onCreateInvoiceClick,
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalGold),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Create First Invoice", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    } else {
        invoices.forEach { inv ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = DarkSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(inv.invoiceNumber, fontSize = 14.sp, fontWeight = FontWeight.Black, color = RoyalGold)
                        Box(
                            modifier = Modifier
                                .background(
                                    if (inv.status == "PAID") Color(0xFF064E3B) else Color(0xFF1E3A8A),
                                    RoundedCornerShape(6.dp)
                                )
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            Text(inv.status, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(inv.clientName, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    Text(inv.itemsSummary, fontSize = 12.sp, color = Color(0xFF94A3B8))

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Column {
                            Text("Subtotal: ₹${String.format("%,.0f", inv.subtotal)}", fontSize = 11.sp, color = Color(0xFF94A3B8))
                            Text("GST (${inv.gstRatePercent.toInt()}%): ₹${String.format("%,.0f", inv.gstAmount)}", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        }
                        Text(
                            text = "₹${String.format("%,.0f", inv.totalAmount)}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black,
                            color = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CreateInvoiceDialog(
    onDismiss: () -> Unit,
    onCreate: (clientName: String, clientGst: String, itemsSummary: String, subtotal: Double, gstRate: Double) -> Unit
) {
    var clientName by remember { mutableStateOf("") }
    var clientGst by remember { mutableStateOf("") }
    var itemsSummary by remember { mutableStateOf("") }
    var subtotalStr by remember { mutableStateOf("50000") }
    var gstRate by remember { mutableDoubleStateOf(18.0) }

    androidx.compose.ui.window.Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("Create GST Invoice", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = clientName,
                    onValueChange = { clientName = it },
                    label = { Text("Customer / Company Name") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = clientGst,
                    onValueChange = { clientGst = it },
                    label = { Text("Customer GSTIN (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = itemsSummary,
                    onValueChange = { itemsSummary = it },
                    label = { Text("Products or Services Description") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = subtotalStr,
                    onValueChange = { subtotalStr = it },
                    label = { Text("Subtotal Amount (₹)") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text("GST Tax Rate: ${gstRate.toInt()}%", fontSize = 12.sp, color = Color.White)
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 4.dp)) {
                    listOf(0.0, 5.0, 12.0, 18.0, 28.0).forEach { rate ->
                        FilterChip(
                            selected = gstRate == rate,
                            onClick = { gstRate = rate },
                            label = { Text("${rate.toInt()}%", fontSize = 11.sp) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = RoyalGold,
                                selectedLabelColor = Color.Black
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(onClick = onDismiss, modifier = Modifier.weight(1f)) {
                        Text("Cancel", color = Color.White)
                    }
                    Button(
                        onClick = {
                            val subtotal = subtotalStr.toDoubleOrNull() ?: 50000.0
                            onCreate(
                                clientName.ifBlank { "Client Corp" },
                                clientGst,
                                itemsSummary.ifBlank { "Consulting Services" },
                                subtotal,
                                gstRate
                            )
                        },
                        modifier = Modifier.weight(1.2f),
                        colors = ButtonDefaults.buttonColors(containerColor = RoyalGold)
                    ) {
                        Text("Issue Invoice", color = Color.Black, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
