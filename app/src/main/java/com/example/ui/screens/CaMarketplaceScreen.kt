package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.BusinessCenter
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CaEngagementEntity
import com.example.data.model.CaProfile
import com.example.ui.components.FlowAuthorityType
import com.example.ui.components.OfficialFlowBadge
import com.example.ui.components.PriceBreakdown
import com.example.ui.components.PricingBreakdownDialog
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldLight

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CaMarketplaceScreen(
    cas: List<CaProfile>,
    engagements: List<CaEngagementEntity>,
    isCaWorkspaceActive: Boolean,
    onToggleCaWorkspace: (Boolean) -> Unit,
    onRequestEngagement: (caId: String, caName: String, service: String, mode: String, notes: String) -> Unit,
    onAskAi: (String) -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(if (isCaWorkspaceActive) 1 else 0) } // 0: Find CA, 1: CA Practice Workspace, 2: Active Engagements
    var selectedSpecialization by remember { mutableStateOf("All") }
    var selectedPricingModal by remember { mutableStateOf<PriceBreakdown?>(null) }
    var selectedCaForEngagement by remember { mutableStateOf<CaProfile?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MidnightNavy)
    ) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = Color(0xFF0F172A),
            contentColor = RoyalGold,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                    color = RoyalGold
                )
            }
        ) {
            Tab(
                selected = selectedTab == 0,
                onClick = {
                    selectedTab = 0
                    onToggleCaWorkspace(false)
                },
                text = { Text("Find Verified CA", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 1,
                onClick = {
                    selectedTab = 1
                    onToggleCaWorkspace(true)
                },
                text = { Text("CA Practice Workspace", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
            Tab(
                selected = selectedTab == 2,
                onClick = { selectedTab = 2 },
                text = { Text("My Requests (${engagements.size})", fontSize = 11.sp, fontWeight = FontWeight.Bold) }
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            when (selectedTab) {
                0 -> {
                    // 4 User Modes of Engagement (Rule 37)
                    Text("CHOOSE YOUR FILING & ADVISORY MODE", fontSize = 11.sp, fontWeight = FontWeight.Black, color = RoyalGold)
                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ModeCard(
                            name = "DIY",
                            price = "₹0",
                            desc = "Full self-service with AI validation",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                selectedPricingModal = PriceBreakdown(
                                    serviceName = "ITR e-Filing",
                                    modeName = "DIY Self-Service",
                                    govtFee = 0.0,
                                    caFee = 0.0,
                                    platformFee = 0.0,
                                    taxes = 0.0,
                                    totalAmount = 0.0
                                )
                            }
                        )
                        ModeCard(
                            name = "AI ASSISTED",
                            price = "₹499",
                            desc = "AI document OCR + rule checking",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                selectedPricingModal = PriceBreakdown(
                                    serviceName = "ITR e-Filing",
                                    modeName = "AI Assisted",
                                    govtFee = 0.0,
                                    caFee = 0.0,
                                    platformFee = 422.88,
                                    taxes = 76.12,
                                    totalAmount = 499.0
                                )
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        ModeCard(
                            name = "CA REVIEW",
                            price = "₹1,499",
                            desc = "FCA reviews & signs off your return",
                            isPopular = true,
                            modifier = Modifier.weight(1f),
                            onClick = {
                                selectedPricingModal = PriceBreakdown(
                                    serviceName = "ITR Pre-Submission Check",
                                    modeName = "CA Review",
                                    govtFee = 0.0,
                                    caFee = 1100.0,
                                    platformFee = 170.0,
                                    taxes = 228.60,
                                    totalAmount = 1499.0
                                )
                            }
                        )
                        ModeCard(
                            name = "CA HANDLED",
                            price = "₹3,499",
                            desc = "Dedicated CA handles everything",
                            modifier = Modifier.weight(1f),
                            onClick = {
                                selectedPricingModal = PriceBreakdown(
                                    serviceName = "Complete Tax Preparation",
                                    modeName = "CA Handled",
                                    govtFee = 0.0,
                                    caFee = 2800.0,
                                    platformFee = 165.0,
                                    taxes = 533.70,
                                    totalAmount = 3499.0
                                )
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    // Filters
                    Text("VERIFIED CHARTERED ACCOUNTANTS", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        listOf("All", "ITR-1 to 4", "Pvt Ltd & LLP Formation", "Tax Notice Resolution", "MSME Udyam").forEach { spec ->
                            FilterChip(
                                selected = selectedSpecialization == spec,
                                onClick = { selectedSpecialization = spec },
                                label = { Text(spec, fontSize = 11.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = RoyalGold,
                                    selectedLabelColor = Color.Black
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // CA List
                    val filteredCas = if (selectedSpecialization == "All") cas else cas.filter { it.specializations.contains(selectedSpecialization) }
                    filteredCas.forEach { ca ->
                        CaProfileCard(
                            ca = ca,
                            onBookReview = {
                                selectedCaForEngagement = ca
                                selectedPricingModal = PriceBreakdown(
                                    serviceName = "CA Review with ${ca.name}",
                                    modeName = "CA Review",
                                    govtFee = 0.0,
                                    caFee = ca.startingFee,
                                    platformFee = 199.0,
                                    taxes = (ca.startingFee + 199.0) * 0.18,
                                    totalAmount = ca.startingFee + 199.0 + (ca.startingFee + 199.0) * 0.18
                                )
                            }
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
                1 -> {
                    // CA Practice Workspace (Rule 42 to 45)
                    CaPracticeWorkspaceView()
                }
                2 -> {
                    // Active Engagements List
                    CaEngagementsListView(engagements = engagements)
                }
            }
        }
    }

    selectedPricingModal?.let { breakdown ->
        PricingBreakdownDialog(
            breakdown = breakdown,
            onDismiss = { selectedPricingModal = null },
            onConfirm = {
                val ca = selectedCaForEngagement ?: cas.first()
                onRequestEngagement(
                    ca.id,
                    ca.name,
                    breakdown.serviceName,
                    breakdown.modeName,
                    "Client requested review for FY 2024-25"
                )
                selectedPricingModal = null
                selectedTab = 2 // Go to requests tab
            }
        )
    }
}

@Composable
private fun ModeCard(
    name: String,
    price: String,
    desc: String,
    isPopular: Boolean = false,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .clickable { onClick() }
            .border(
                1.dp,
                if (isPopular) RoyalGold else Color(0xFF23304A),
                RoundedCornerShape(12.dp)
            ),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = if (isPopular) Color(0xFF1E293B) else DarkSurface)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            if (isPopular) {
                Text("MOST POPULAR", fontSize = 8.sp, fontWeight = FontWeight.Black, color = RoyalGold)
            }
            Text(name, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Spacer(modifier = Modifier.height(4.dp))
            Text(price, fontSize = 16.sp, fontWeight = FontWeight.Black, color = if (isPopular) RoyalGold else EmeraldGreen)
            Spacer(modifier = Modifier.height(2.dp))
            Text(desc, fontSize = 10.sp, color = Color(0xFF94A3B8), lineHeight = 14.sp)
        }
    }
}

@Composable
private fun CaProfileCard(ca: CaProfile, onBookReview: () -> Unit) {
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
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                            .border(1.dp, RoyalGold, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            ca.name.take(2).uppercase(),
                            color = RoyalGold,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(ca.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Verified, contentDescription = null, tint = EmeraldGreen, modifier = Modifier.size(16.dp))
                        }
                        Text("${ca.icaiNumber} • ${ca.experienceYears} yrs exp", fontSize = 11.sp, color = Color(0xFF94A3B8))
                        Text(ca.city, fontSize = 10.sp, color = Color(0xFF64748B))
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Star, contentDescription = null, tint = RoyalGold, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(2.dp))
                        Text("${ca.rating} (${ca.reviewCount})", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                    }
                    Spacer(modifier = Modifier.height(2.dp))
                    Text("From ₹${ca.startingFee.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Black, color = RoyalGold)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
            Text(ca.bio, fontSize = 11.sp, color = Color(0xFFCBD5E1), lineHeight = 16.sp)

            Spacer(modifier = Modifier.height(10.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                ca.specializations.take(3).forEach { spec ->
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF131D31), RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(spec, fontSize = 9.sp, color = Color(0xFF94A3B8))
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = onBookReview,
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Book Review (₹${ca.startingFee.toInt()})", color = RoyalGold, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun CaPracticeWorkspaceView() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF0F1A2E)),
        border = androidx.compose.foundation.BorderStroke(1.dp, RoyalGold.copy(alpha = 0.4f))
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("CA PRACTICE OPERATING SYSTEM", fontSize = 11.sp, fontWeight = FontWeight.Black, color = RoyalGold)
                    Text("Partner Portal • Firm Analytics & Client Queue", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                OfficialFlowBadge(
                    authorityType = FlowAuthorityType.AUTHORIZED_INTEGRATION,
                    label = "ICAI REGISTERED CA WORKSPACE"
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Practice Stats
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                StatTile("Active Clients", "38", Modifier.weight(1f))
                StatTile("Returns in Queue", "6", Modifier.weight(1f))
                StatTile("Firm Earnings", "₹1.48L", Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("CLIENT REVIEWS IN QUEUE (REAL-TIME):", fontSize = 11.sp, fontWeight = FontWeight.Black, color = Color.White)
            Spacer(modifier = Modifier.height(8.dp))

            ClientFileRow("Ankit Pal", "ITR-1 (Sahaj) • Form 16 Part A Verified", "Pending CA Stamp", EmeraldGreen)
            ClientFileRow("TrendBros Apparels", "LLP Annual Compliance (Form 11)", "Due in 12 days", RoyalGold)
            ClientFileRow("Deepika Rao", "Sec 143(1) Notice Response Draft", "Ready to File", Color(0xFF38BDF8))
        }
    }
}

@Composable
private fun StatTile(label: String, value: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(Color(0xFF131D31), RoundedCornerShape(10.dp))
            .padding(10.dp)
    ) {
        Column {
            Text(label, fontSize = 9.sp, color = Color(0xFF94A3B8))
            Spacer(modifier = Modifier.height(2.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Black, color = Color.White)
        }
    }
}

@Composable
private fun ClientFileRow(client: String, service: String, status: String, tagColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color(0xFF131D31), RoundedCornerShape(8.dp))
            .padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(client, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
            Text(service, fontSize = 10.sp, color = Color(0xFF94A3B8))
        }
        Box(
            modifier = Modifier
                .background(tagColor.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
            Text(status, fontSize = 9.sp, color = tagColor, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun CaEngagementsListView(engagements: List<CaEngagementEntity>) {
    if (engagements.isEmpty()) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = DarkSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, DarkCardBorder)
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(Icons.Default.BusinessCenter, contentDescription = null, tint = RoyalGold, modifier = Modifier.size(36.dp))
                Spacer(modifier = Modifier.height(10.dp))
                Text("No Active Requests", fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("You haven't requested CA review yet. Choose a verified CA from the marketplace.", fontSize = 12.sp, color = Color(0xFF94A3B8))
            }
        }
    } else {
        engagements.forEach { eng ->
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
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(eng.serviceType, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        Text(eng.status, fontSize = 10.sp, fontWeight = FontWeight.Bold, color = EmeraldGreen)
                    }
                    Text("Assigned CA: ${eng.caName}", fontSize = 11.sp, color = RoyalGoldLight)
                    Text("Total Fee Paid: ₹${String.format("%,.2f", eng.totalFee)}", fontSize = 11.sp, color = Color(0xFF94A3B8))
                }
            }
        }
    }
}
