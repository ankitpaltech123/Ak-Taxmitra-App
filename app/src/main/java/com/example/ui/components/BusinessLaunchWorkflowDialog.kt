package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CorporateFare
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ChampagneGoldLight
import com.example.ui.theme.MidnightNavy

@Composable
fun BusinessLaunchWorkflowDialog(
    onDismiss: () -> Unit,
    onCompleteLaunch: (String, String) -> Unit // name, structure
) {
    var step by remember { mutableIntStateOf(1) } // 1: Idea, 2: Structure, 3: Name & Trademark, 4: Founders, 5: Visual Tracker

    var businessIdea by remember { mutableStateOf("AI-powered legal tech platform for small businesses") }
    var selectedStructure by remember { mutableStateOf("PRIVATE_LIMITED") }
    var companyNameInput by remember { mutableStateOf("Taxmitra Technologies") }
    var founderNames by remember { mutableStateOf("Ankit Pal, Co-founder") }

    val progressFraction = step / 5f
    val progressPercent = (progressFraction * 100).toInt()

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .fillMaxHeight(0.90f)
                .clip(RoundedCornerShape(20.dp))
                .background(MidnightNavy)
                .border(1.dp, Color(0xFF223250), RoundedCornerShape(20.dp))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp)
            ) {
                // Top Step Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Business Launch OS",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Box(
                                modifier = Modifier
                                    .background(Color(0xFF1E2D4A), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "$progressPercent% Set Up",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = ChampagneGold
                                )
                            }
                        }
                        Text(
                            text = "Step $step of 5 • ${getBusinessStepTitle(step)}",
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF94A3B8)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                LinearProgressIndicator(
                    progress = { progressFraction },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = ChampagneGold,
                    trackColor = Color(0xFF1B2840)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Scrollable Content
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                ) {
                    when (step) {
                        1 -> Step1Idea(
                            businessIdea = businessIdea,
                            onIdeaChange = { businessIdea = it }
                        )
                        2 -> Step2Structure(
                            selectedStructure = selectedStructure,
                            onSelect = { selectedStructure = it }
                        )
                        3 -> Step3NameAndTrademark(
                            companyName = companyNameInput,
                            onNameChange = { companyNameInput = it }
                        )
                        4 -> Step4Founders(
                            founderNames = founderNames,
                            onFoundersChange = { founderNames = it }
                        )
                        5 -> Step5RegistrationTracker(
                            companyName = companyNameInput,
                            structure = selectedStructure
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Bottom Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (step > 1) {
                        OutlinedButton(
                            onClick = { step-- },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Back", fontSize = 12.sp)
                        }
                    } else {
                        Spacer(modifier = Modifier.width(10.dp))
                    }

                    if (step < 5) {
                        Button(
                            onClick = { step++ },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold)
                        ) {
                            Text("Continue", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.Black)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        }
                    } else {
                        Button(
                            onClick = {
                                onCompleteLaunch(companyNameInput, selectedStructure)
                                onDismiss()
                            },
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold)
                        ) {
                            Text("ACTIVATE REGISTRATION", fontSize = 12.sp, fontWeight = FontWeight.Black, color = Color.Black)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                        }
                    }
                }
            }
        }
    }
}

private fun getBusinessStepTitle(step: Int): String {
    return when (step) {
        1 -> "Business Concept"
        2 -> "Entity Structure Selection"
        3 -> "MCA Name & Trademark Verification"
        4 -> "Founders & Capital"
        5 -> "Incorporation Lifecycle Tracker"
        else -> ""
    }
}

@Composable
private fun Step1Idea(
    businessIdea: String,
    onIdeaChange: (String) -> Unit
) {
    Column {
        Text("WHAT ARE YOU BUILDING?", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ChampagneGold)
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            "Describe your business idea, product, or service in plain words. Taxmitra AI evaluates regulatory requirements and structure suitability.",
            fontSize = 12.sp,
            color = Color(0xFFCBD5E1),
            lineHeight = 17.sp
        )
        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = businessIdea,
            onValueChange = onIdeaChange,
            label = { Text("Business Concept / Industry", color = Color(0xFF94A3B8)) },
            modifier = Modifier.fillMaxWidth(),
            maxLines = 4,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ChampagneGold,
                unfocusedBorderColor = Color(0xFF2B3A55),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131E33)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223250))
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Lightbulb, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("AI Regulatory Preliminary Analysis", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "• GST threshold: ₹40L for goods / ₹20L for services.\n• DPIIT Startup India registration recommended for 3-year Section 80-IAC tax holiday.\n• Zero capital duty on initial authorized capital up to ₹15 Lakhs.",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 16.sp
                )
            }
        }
    }
}

@Composable
private fun Step2Structure(
    selectedStructure: String,
    onSelect: (String) -> Unit
) {
    Column {
        Text("SELECT RECOMMENDED ENTITY", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ChampagneGold)
        Spacer(modifier = Modifier.height(6.dp))
        Text("Side-by-side legal comparison of Indian business structures.", fontSize = 12.sp, color = Color(0xFFCBD5E1))
        Spacer(modifier = Modifier.height(14.dp))

        StructureCard(
            title = "Private Limited Company (Pvt Ltd)",
            recommendedBadge = "MOST POPULAR FOR STARTUPS",
            liability = "Limited to unpaid share capital",
            taxRate = "22% base + surcharge (Section 115BAA)",
            compliance = "Moderate-High (Annual ROC AOC-4, MGT-7, Statutory Audit)",
            funding = "High (Can issue equity, ESOPs, SAFEs)",
            cost = "₹4,999 - ₹7,999",
            isSelected = selectedStructure == "PRIVATE_LIMITED",
            onClick = { onSelect("PRIVATE_LIMITED") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        StructureCard(
            title = "Limited Liability Partnership (LLP)",
            recommendedBadge = "BEST FOR PROFESSIONAL SERVICES",
            liability = "Limited to agreed contribution",
            taxRate = "30% flat tax",
            compliance = "Low-Moderate (No audit if turnover < ₹40L & capital < ₹25L)",
            funding = "Low (Cannot issue equity shares)",
            cost = "₹3,999 - ₹5,999",
            isSelected = selectedStructure == "LLP",
            onClick = { onSelect("LLP") }
        )

        Spacer(modifier = Modifier.height(10.dp))

        StructureCard(
            title = "Sole Proprietorship",
            recommendedBadge = "FASTEST TO LAUNCH",
            liability = "Unlimited personal liability",
            taxRate = "Individual slab rates (up to 30%)",
            compliance = "Very Low (No ROC filings, only personal ITR-3/4)",
            funding = "None (Single owner personal debt)",
            cost = "₹999 - ₹1,999",
            isSelected = selectedStructure == "PROPRIETORSHIP",
            onClick = { onSelect("PROPRIETORSHIP") }
        )
    }
}

@Composable
private fun StructureCard(
    title: String,
    recommendedBadge: String,
    liability: String,
    taxRate: String,
    compliance: String,
    funding: String,
    cost: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color(0xFF1B2844) else Color(0xFF10192A)
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (isSelected) ChampagneGold else Color(0xFF202E48)
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                if (isSelected) {
                    Icon(Icons.Default.Check, contentDescription = null, tint = ChampagneGold, modifier = Modifier.size(18.dp))
                }
            }
            Spacer(modifier = Modifier.height(4.dp))
            Box(
                modifier = Modifier
                    .background(Color(0xFF223455), RoundedCornerShape(4.dp))
                    .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
                Text(recommendedBadge, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = ChampagneGoldLight)
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text("• Liability: $liability", fontSize = 11.sp, color = Color(0xFFCBD5E1))
            Text("• Tax Rate: $taxRate", fontSize = 11.sp, color = Color(0xFFCBD5E1))
            Text("• Compliance: $compliance", fontSize = 11.sp, color = Color(0xFFCBD5E1))
            Text("• Funding Suitability: $funding", fontSize = 11.sp, color = Color(0xFFCBD5E1))
            Text("• Approx Setup Cost: $cost", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ChampagneGoldLight)
        }
    }
}

@Composable
private fun Step3NameAndTrademark(
    companyName: String,
    onNameChange: (String) -> Unit
) {
    Column {
        Text("MCA NAME & TRADEMARK CHECK", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ChampagneGold)
        Spacer(modifier = Modifier.height(6.dp))
        Text("Verify ministry availability and trademark class conflicts.", fontSize = 12.sp, color = Color(0xFFCBD5E1))
        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = companyName,
            onValueChange = onNameChange,
            label = { Text("Proposed Company Name", color = Color(0xFF94A3B8)) },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search", tint = ChampagneGold)
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ChampagneGold,
                unfocusedBorderColor = Color(0xFF2B3A55),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223250))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("MCA SPICe+ Name Check: AVAILABLE", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF34D399))
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "No conflicting identical company registered in Ministry of Corporate Affairs database.",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Verified, contentDescription = null, tint = Color(0xFF10B981), modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Trademark Class 42 & 35: CLEAR", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color(0xFF34D399))
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "No identical active word marks registered in IP India Trade Marks Registry.",
                    fontSize = 11.sp,
                    color = Color(0xFF94A3B8)
                )
            }
        }
    }
}

@Composable
private fun Step4Founders(
    founderNames: String,
    onFoundersChange: (String) -> Unit
) {
    Column {
        Text("FOUNDERS & DIRECTORS", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ChampagneGold)
        Spacer(modifier = Modifier.height(6.dp))
        Text("Provide details of initial directors (Min 2 for Pvt Ltd, 1 for OPC).", fontSize = 12.sp, color = Color(0xFFCBD5E1))
        Spacer(modifier = Modifier.height(14.dp))

        OutlinedTextField(
            value = founderNames,
            onValueChange = onFoundersChange,
            label = { Text("Founders / Directors (Comma separated)", color = Color(0xFF94A3B8)) },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = ChampagneGold,
                unfocusedBorderColor = Color(0xFF2B3A55),
                focusedTextColor = Color.White,
                unfocusedTextColor = Color.White
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223250))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text("Pre-filled Statutory Inclusions:", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Spacer(modifier = Modifier.height(6.dp))
                Text("• Digital Signature Certificates (DSC - Class 3) for 2 Directors", fontSize = 11.sp, color = Color(0xFFCBD5E1))
                Text("• Director Identification Numbers (DIN) via SPICe+ Part B", fontSize = 11.sp, color = Color(0xFFCBD5E1))
                Text("• Name reservation RUN / SPICe+ Part A", fontSize = 11.sp, color = Color(0xFFCBD5E1))
                Text("• PAN & TAN allocation simultaneous with Certificate of Incorporation", fontSize = 11.sp, color = Color(0xFFCBD5E1))
            }
        }
    }
}

@Composable
private fun Step5RegistrationTracker(
    companyName: String,
    structure: String
) {
    Column {
        Text("INCORPORATION LIFECYCLE TRACKER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = ChampagneGold)
        Spacer(modifier = Modifier.height(6.dp))
        Text("Live tracking timeline from SPICe+ submission to Bank Account.", fontSize = 12.sp, color = Color(0xFFCBD5E1))
        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31)),
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF223250))
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Text(
                    text = "Application: $companyName ($structure)",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                TrackerTimelineItem("Business Type Chosen", "Pvt Ltd selected", isCompleted = true)
                TrackerTimelineItem("Name Approved", "$companyName verified on MCA", isCompleted = true)
                TrackerTimelineItem("Founders & DSC", "Class 3 DSC tokens generated", isCompleted = true)
                TrackerTimelineItem("Documents Uploaded", "MOA, AOA, INC-9 declarations pending signing", isCurrent = true)
                TrackerTimelineItem("PAN & TAN Allocation", "Issued with Certificate of Incorporation", isPending = true)
                TrackerTimelineItem("GST Registration", "Auto-routed via AGILE-PRO-S", isPending = true)
                TrackerTimelineItem("MSME (Udyam) Registration", "Priority sector lending certificate", isPending = true)
                TrackerTimelineItem("Current Bank Account", "Pre-approved ICICI / HDFC / Axis zero-balance", isPending = true)
            }
        }
    }
}

@Composable
private fun TrackerTimelineItem(
    title: String,
    subtitle: String,
    isCompleted: Boolean = false,
    isCurrent: Boolean = false,
    isPending: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .background(
                    when {
                        isCompleted -> Color(0xFF10B981)
                        isCurrent -> ChampagneGold
                        else -> Color(0xFF253450)
                    }
                ),
            contentAlignment = Alignment.Center
        ) {
            if (isCompleted) {
                Icon(Icons.Default.Check, contentDescription = null, tint = Color.Black, modifier = Modifier.size(13.dp))
            } else if (isCurrent) {
                Text("●", fontSize = 10.sp, color = Color.Black, fontWeight = FontWeight.Bold)
            }
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = title,
                fontSize = 12.sp,
                fontWeight = if (isCurrent || isCompleted) FontWeight.Bold else FontWeight.Medium,
                color = if (isCurrent) ChampagneGoldLight else if (isCompleted) Color.White else Color(0xFF94A3B8)
            )
            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}
