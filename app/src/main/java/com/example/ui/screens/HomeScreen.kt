package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.engine.IntentRouter
import com.example.data.engine.NextBestAction
import com.example.data.model.BusinessEntity
import com.example.data.model.TaxProfileEntity
import com.example.data.model.UserRole
import com.example.ui.components.BusinessLaunchWorkflowDialog
import com.example.ui.components.ConceptExplanation
import com.example.ui.components.ItrGuidedWorkflowDialog
import com.example.ui.components.LogoSize
import com.example.ui.components.NextBestActionCard
import com.example.ui.components.TaxmitraBrandLogo
import com.example.ui.components.UniversalExplanationDialog
import com.example.ui.theme.AppColors
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ChampagneGoldLight
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.RoyalBlue
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldLight

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(
    currentRole: UserRole,
    business: BusinessEntity?,
    taxProfile: TaxProfileEntity?,
    nextAction: NextBestAction,
    onNavigateTab: (String) -> Unit,
    onOpenAiChatWithPrompt: (String) -> Unit,
    onOpenReceiptScanner: () -> Unit = {},
    onOpenVoiceLive: () -> Unit = {},
    onRemindAction: (NextBestAction) -> Unit = {}
) {
    var quickCommandText by remember { mutableStateOf("") }
    var showItrWorkflow by remember { mutableStateOf(false) }
    var showBusinessWorkflow by remember { mutableStateOf(false) }
    var activeExplanation by remember { mutableStateOf<ConceptExplanation?>(null) }
    val isDark = AppColors.isDark

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // ----------------------------------------------------
        // HERO: Brand Logo & Intelligent Greeting
        // ----------------------------------------------------
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentAlignment = Alignment.Center
        ) {
            TaxmitraBrandLogo(
                size = LogoSize.STANDARD,
                showTagline = true,
                isDarkBackground = isDark
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ----------------------------------------------------
        // AI COMMAND BAR: "How can Taxmitra help you today?"
        // ----------------------------------------------------
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .border(
                    1.dp,
                    Brush.horizontalGradient(
                        if (isDark) listOf(ChampagneGold.copy(alpha = 0.7f), ElectricBlue.copy(alpha = 0.6f))
                        else listOf(Color(0xFFD97706).copy(alpha = 0.5f), Color(0xFF2563EB).copy(alpha = 0.5f))
                    ),
                    RoundedCornerShape(16.dp)
                ),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = AppColors.cardBackground)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Good morning, Taxpayer",
                            fontSize = 12.sp,
                            color = AppColors.textSecondary
                        )
                        Text(
                            text = "How can Taxmitra help you today?",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.textPrimary
                        )
                    }

                    // Live Voice Trigger Pill
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(if (isDark) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFD1FAE5))
                            .border(1.dp, Color(0xFF10B981), RoundedCornerShape(20.dp))
                            .clickable { onOpenVoiceLive() }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = if (isDark) Color(0xFF10B981) else Color(0xFF047857),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Voice",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color(0xFF10B981) else Color(0xFF047857)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Input Bar with Action Submit
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppColors.inputBackground, RoundedCornerShape(12.dp))
                        .border(1.dp, AppColors.inputBorder, RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = AppColors.goldAccent,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    OutlinedTextField(
                        value = quickCommandText,
                        onValueChange = { quickCommandText = it },
                        placeholder = {
                            Text(
                                text = "Ask Taxmitra anything... (ITR, Business, GST)",
                                fontSize = 12.sp,
                                color = AppColors.textMuted
                            )
                        },
                        modifier = Modifier.weight(1f),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color.Transparent,
                            unfocusedBorderColor = Color.Transparent,
                            focusedTextColor = AppColors.textPrimary,
                            unfocusedTextColor = AppColors.textPrimary
                        ),
                        singleLine = true
                    )
                    IconButton(
                        onClick = {
                            if (quickCommandText.isNotBlank()) {
                                val query = quickCommandText.trim()
                                val route = IntentRouter.route(query)
                                onOpenAiChatWithPrompt(query)
                                quickCommandText = ""
                            }
                        },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(if (quickCommandText.isNotBlank()) AppColors.goldAccent else AppColors.chipBackground)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Execute",
                            tint = if (quickCommandText.isNotBlank()) Color.White else AppColors.textMuted,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Suggestion Chips
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf(
                        "File my ITR" to { showItrWorkflow = true },
                        "Start my business" to { showBusinessWorkflow = true },
                        "Explain my tax" to { onOpenAiChatWithPrompt("Explain my tax breakdown and whether Old or New regime saves more money.") },
                        "Check my GST" to { onOpenAiChatWithPrompt("Do I need GST registration for my business?") },
                        "Find a CA" to { onNavigateTab("CA") },
                        "Create invoice" to { onNavigateTab("BUSINESS") }
                    ).forEach { (label, action) ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(AppColors.chipBackground)
                                .border(1.dp, AppColors.chipBorder, RoundedCornerShape(10.dp))
                                .clickable { action() }
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = label,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = AppColors.textPrimary
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // ----------------------------------------------------
        // PRIORITY 1: Critical Action / Next Best Action
        // ----------------------------------------------------
        NextBestActionCard(
            action = nextAction,
            onExecuteAction = { tab -> onNavigateTab(tab) },
            onRemindAction = onRemindAction
        )

        Spacer(modifier = Modifier.height(16.dp))

        // ----------------------------------------------------
        // PRIORITY 2: Active Structured Workflows
        // ----------------------------------------------------
        Text(
            text = "ACTIVE STRUCTURED WORKFLOWS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.sectionHeader,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Workflow 1: ITR Guided Flow Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .clickable { showItrWorkflow = true }
                .border(1.dp, AppColors.cardBorder, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AppColors.cardBackground)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(AppColors.goldAccent.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AccountBalance, contentDescription = null, tint = AppColors.goldAccent, modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("ITR-1 Guided Preparation", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AppColors.textPrimary)
                            Text("AY 2026-27 • New Regime Optimized", fontSize = 10.sp, color = AppColors.textSecondary)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .background(
                                if (isDark) Color(0xFF1C2C48) else Color(0xFFFEF3C7),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("62% COMPLETE", fontSize = 9.sp, fontWeight = FontWeight.Black, color = AppColors.goldAccent)
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { 0.62f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = AppColors.goldAccent,
                    trackColor = if (isDark) Color(0xFF1F2F4B) else Color(0xFFE2E8F0)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Draft saved • Ready for verification & review",
                        fontSize = 11.sp,
                        color = AppColors.textSecondary
                    )
                    Text(
                        text = "Resume >",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.goldAccent
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Workflow 2: Business Registration OS Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .clickable { showBusinessWorkflow = true }
                .border(1.dp, AppColors.cardBorder, RoundedCornerShape(14.dp)),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = AppColors.cardBackground)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(Color(0xFF38BDF8).copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = Color(0xFF0284C7), modifier = Modifier.size(18.dp))
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = business?.name ?: "Business Launch OS (SPICe+)",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = AppColors.textPrimary
                            )
                            Text("Pvt Ltd / LLP • SPICe+ Lifecycle", fontSize = 10.sp, color = AppColors.textSecondary)
                        }
                    }
                    Box(
                        modifier = Modifier
                            .background(
                                if (isDark) Color(0xFF1A2F48) else Color(0xFFE0F2FE),
                                RoundedCornerShape(6.dp)
                            )
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text("STEP 4 OF 8", fontSize = 9.sp, fontWeight = FontWeight.Black, color = Color(0xFF0284C7))
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { 0.50f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = Color(0xFF0284C7),
                    trackColor = if (isDark) Color(0xFF1F2F4B) else Color(0xFFE2E8F0)
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Name Approved • MOA/AOA Documents Ready",
                        fontSize = 11.sp,
                        color = AppColors.textSecondary
                    )
                    Text(
                        text = "Track >",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF0284C7)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ----------------------------------------------------
        // PRIORITY 3: Financial Status & Quick Calculations
        // ----------------------------------------------------
        Text(
            text = "FINANCIAL STATUS & TAX CALCULATIONS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.sectionHeader,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Estimated Tax & Regime Comparison
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable { onNavigateTab("TAX") },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.cardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.cardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("ESTIMATED TAX", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = AppColors.goldAccent)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (taxProfile != null) "₹${String.format("%,.0f", taxProfile.taxNewRegime)}" else "₹22,500",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = AppColors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "₹75,000 Std Ded applied",
                        fontSize = 10.sp,
                        color = if (isDark) Color(0xFF10B981) else Color(0xFF059669)
                    )
                }
            }

            // Refund Tracker Card
            Card(
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        onOpenAiChatWithPrompt("Track my income tax refund status on e-filing portal.")
                    },
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.cardBackground),
                border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.cardBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text("REFUND TRACKER", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = if (isDark) Color(0xFF10B981) else Color(0xFF059669))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "₹14,500",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Black,
                        color = AppColors.textPrimary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = if (isDark) Color(0xFF10B981) else Color(0xFF059669), modifier = Modifier.size(12.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Processed by CPC",
                            fontSize = 10.sp,
                            color = AppColors.textSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // ----------------------------------------------------
        // PRIORITY 4: Fast Workflow Launch Tiles
        // ----------------------------------------------------
        Text(
            text = "FAST TOOLS & ASSISTANTS",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.sectionHeader,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(8.dp))

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            QuickToolChip(
                label = "Scan Receipt (AI)",
                icon = Icons.Default.CameraAlt,
                onClick = onOpenReceiptScanner
            )
            QuickToolChip(
                label = "ITR Filing",
                icon = Icons.Default.AccountBalance,
                onClick = { showItrWorkflow = true }
            )
            QuickToolChip(
                label = "Launch Business",
                icon = Icons.Default.RocketLaunch,
                onClick = { showBusinessWorkflow = true }
            )
            QuickToolChip(
                label = "Do I Need GST?",
                icon = Icons.Default.Receipt,
                onClick = { onOpenAiChatWithPrompt("Do I need GST registration for my business turnover?") }
            )
            QuickToolChip(
                label = "Create Invoice",
                icon = Icons.Default.Receipt,
                onClick = { onNavigateTab("BUSINESS") }
            )
            QuickToolChip(
                label = "Compliance Calendar",
                icon = Icons.Default.CalendarMonth,
                onClick = { onNavigateTab("COMPLIANCE") }
            )
            QuickToolChip(
                label = "AK Vault (Documents)",
                icon = Icons.Default.Folder,
                onClick = { onNavigateTab("DOCUMENTS") }
            )
            QuickToolChip(
                label = "Find a CA",
                icon = Icons.Default.Verified,
                onClick = { onNavigateTab("CA") }
            )
            QuickToolChip(
                label = "Explain a Notice",
                icon = Icons.Default.Notifications,
                onClick = { onOpenAiChatWithPrompt("I received an Income Tax Section 143(1) intimation notice. Please explain it.") }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // ----------------------------------------------------
    // DIALOGS: Interactive Guided Workflows
    // ----------------------------------------------------
    if (showItrWorkflow) {
        ItrGuidedWorkflowDialog(
            onDismiss = { showItrWorkflow = false },
            onSubmitComplete = { withCa ->
                showItrWorkflow = false
                onNavigateTab("TAX")
            },
            onExplainResult = { title, what ->
                activeExplanation = ConceptExplanation(
                    title = title,
                    whatIsIt = what,
                    whyItMatters = "Ensures you do not pay excess tax or miss eligible statutory rebates.",
                    doINeedIt = "Essential for all Indian salaried individuals and business owners.",
                    costEstimate = "Free automated computation via AK Taxmitra deterministic engine.",
                    whoHandlesIt = "Direct e-filing or verified ICAI Chartered Accountant.",
                    whatHappensNext = "Form is submitted to ITD portal and verified via Aadhaar OTP.",
                    canIDoItMyself = "Yes, simple 6-step guided walkthrough.",
                    whenToAskCa = "Ask a CA if you have capital gains, foreign assets, or audit requirements."
                )
            }
        )
    }

    if (showBusinessWorkflow) {
        BusinessLaunchWorkflowDialog(
            onDismiss = { showBusinessWorkflow = false },
            onCompleteLaunch = { name, structure ->
                showBusinessWorkflow = false
                onNavigateTab("BUSINESS")
            }
        )
    }

    activeExplanation?.let { explanation ->
        UniversalExplanationDialog(
            explanation = explanation,
            onDismiss = { activeExplanation = null }
        )
    }
}

@Composable
private fun QuickToolChip(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .background(AppColors.chipBackground)
            .border(1.dp, AppColors.chipBorder, RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = AppColors.goldAccent,
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = AppColors.textPrimary
        )
    }
}
