package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.ReceiptLong
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DocumentEntity
import com.example.data.model.ScannedTaxDocumentEntity
import com.example.ui.theme.AppColors
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldLight
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DocumentsScreen(
    documents: List<DocumentEntity>,
    scannedTaxDocuments: List<ScannedTaxDocumentEntity> = emptyList(),
    onOpenScanner: () -> Unit = {},
    onUploadSampleDocument: (title: String, category: String, type: String, summary: String) -> Unit = { _, _, _, _ -> },
    onDeleteDocument: (Long) -> Unit = {},
    onDeleteScannedDocument: (Long) -> Unit = {},
    onSaveNewScannedDocument: (
        title: String,
        documentType: String,
        category: String,
        aiSummary: String,
        vendorOrIssuer: String,
        invoiceNumber: String,
        amount: Double,
        taxSection: String,
        taxBenefit: String
    ) -> Unit = { _, _, _, _, _, _, _, _, _ -> },
    onAskAiAboutDocument: (prompt: String) -> Unit = {}
) {
    var selectedMainTab by remember { mutableStateOf(0) } // 0 = Scanned History (Room DB), 1 = Vault Folders
    var selectedDocTypeFilter by remember { mutableStateOf("ALL") }
    var selectedCategoryFilter by remember { mutableStateOf("ALL") }
    var selectedDocForDetail by remember { mutableStateOf<ScannedTaxDocumentEntity?>(null) }
    var showAddSampleDialog by remember { mutableStateOf(false) }

    val isDark = AppColors.isDark

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppColors.background)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Top Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "TAX DOCUMENT INTELLIGENCE",
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    color = AppColors.goldAccent,
                    letterSpacing = 1.sp
                )
                Text(
                    text = "Local Room DB & Encrypted Vault",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.textPrimary
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Button(
                    onClick = onOpenScanner,
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.goldAccent),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.testTag("scan_document_top_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Scan Document",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "Scan",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Main Tab Row: Scanned History (Room DB) vs Encrypted Vault
        TabRow(
            selectedTabIndex = selectedMainTab,
            containerColor = AppColors.cardBackground,
            contentColor = AppColors.goldAccent,
            indicator = { tabPositions ->
                TabRowDefaults.SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[selectedMainTab]),
                    color = AppColors.goldAccent,
                    height = 3.dp
                )
            },
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, AppColors.cardBorder, RoundedCornerShape(12.dp))
        ) {
            Tab(
                selected = selectedMainTab == 0,
                onClick = { selectedMainTab = 0 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (selectedMainTab == 0) AppColors.goldAccent else AppColors.textMuted
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Scanned History (${scannedTaxDocuments.size})",
                            fontSize = 12.sp,
                            fontWeight = if (selectedMainTab == 0) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedMainTab == 0) AppColors.goldAccent else AppColors.textSecondary
                        )
                    }
                }
            )
            Tab(
                selected = selectedMainTab == 1,
                onClick = { selectedMainTab = 1 },
                text = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Folder,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = if (selectedMainTab == 1) AppColors.goldAccent else AppColors.textMuted
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Vault Files (${documents.size})",
                            fontSize = 12.sp,
                            fontWeight = if (selectedMainTab == 1) FontWeight.Bold else FontWeight.Medium,
                            color = if (selectedMainTab == 1) AppColors.goldAccent else AppColors.textSecondary
                        )
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        if (selectedMainTab == 0) {
            // ==========================================
            // TAB 0: SCANNED TAX DOCUMENTS HISTORY (ROOM DB)
            // ==========================================

            // Quick actions & Add sample button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(EmeraldGreen)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Persistent Room SQLite Storage",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.textSecondary
                    )
                }

                TextButton(
                    onClick = { showAddSampleDialog = true },
                    modifier = Modifier.testTag("add_sample_scanned_doc_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = null,
                        tint = AppColors.goldAccent,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Add Sample Scan",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.goldAccent
                    )
                }
            }

            // Filter Chips for Scanned Documents by Type
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val filters = listOf(
                    "ALL" to "All Types",
                    "FORM_16" to "Form 16/Salary",
                    "SECTION_80D_HEALTH" to "Sec 80D Health",
                    "GST_INVOICE" to "GST Invoices",
                    "RENT_RECEIPT_HRA" to "Rent/HRA",
                    "DEDUCTIONS" to "Other Deductions"
                )

                filters.forEach { (typeKey, label) ->
                    val isSelected = selectedDocTypeFilter == typeKey
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedDocTypeFilter = typeKey },
                        label = { Text(label, fontSize = 11.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AppColors.goldAccent,
                            selectedLabelColor = Color.White,
                            containerColor = AppColors.chipBackground,
                            labelColor = AppColors.textSecondary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            val filteredScans = remember(scannedTaxDocuments, selectedDocTypeFilter) {
                if (selectedDocTypeFilter == "ALL") {
                    scannedTaxDocuments
                } else if (selectedDocTypeFilter == "DEDUCTIONS") {
                    scannedTaxDocuments.filter { it.category == "DEDUCTIONS" }
                } else {
                    scannedTaxDocuments.filter { it.documentType == selectedDocTypeFilter }
                }
            }

            if (filteredScans.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ReceiptLong,
                            contentDescription = null,
                            tint = AppColors.textMuted,
                            modifier = Modifier.size(54.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No Scanned Tax Documents Found",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = "Snap your Form 16, medical bills, rent receipts, or GST invoices using the camera. Extracted summaries and tax sections will be stored here.",
                            fontSize = 12.sp,
                            color = AppColors.textSecondary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onOpenScanner,
                            colors = ButtonDefaults.buttonColors(containerColor = AppColors.goldAccent),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Scan Document Now", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredScans, key = { it.id }) { scan ->
                        ScannedTaxDocumentCard(
                            document = scan,
                            onDelete = { onDeleteScannedDocument(scan.id) },
                            onSelect = { selectedDocForDetail = scan },
                            onAskAi = {
                                onAskAiAboutDocument(
                                    "Explain the tax deduction and compliance implications for this scanned document: ${scan.title}. " +
                                            "Vendor: ${scan.vendorOrIssuer}, Amount: ₹${scan.detectedAmount}, Section: ${scan.eligibleTaxSection}. " +
                                            "AI Extracted Summary: ${scan.aiSummary}"
                                )
                            }
                        )
                    }
                }
            }
        } else {
            // ==========================================
            // TAB 1: VAULT FILES (ENCRYPTED STORAGE)
            // ==========================================

            // CameraX Scanner Banner
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .border(1.dp, AppColors.goldAccent.copy(alpha = 0.5f), RoundedCornerShape(14.dp)),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = AppColors.cardBackground)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = AppColors.goldAccent,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "CameraX Tax & Receipt Scanner",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Black,
                                color = AppColors.goldAccent
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = "Extract PAN, GSTIN, Section 80D, 80C & ITC deductions automatically into Room SQLite database.",
                            fontSize = 11.sp,
                            color = AppColors.textSecondary,
                            lineHeight = 15.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Button(
                        onClick = onOpenScanner,
                        colors = ButtonDefaults.buttonColors(containerColor = AppColors.goldAccent),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Scan Now", color = Color.White, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Categories
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                listOf("ALL", "TAX", "PERSONAL", "BUSINESS").forEach { cat ->
                    FilterChip(
                        selected = selectedCategoryFilter == cat,
                        onClick = { selectedCategoryFilter = cat },
                        label = { Text(cat, fontSize = 11.sp) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AppColors.goldAccent,
                            selectedLabelColor = Color.White,
                            containerColor = AppColors.chipBackground,
                            labelColor = AppColors.textSecondary
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            val filteredDocs = if (selectedCategoryFilter == "ALL") documents else documents.filter { it.category == selectedCategoryFilter }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(filteredDocs, key = { it.id }) { doc ->
                    DocumentCard(doc = doc, onDelete = { onDeleteDocument(doc.id) })
                }
            }
        }
    }

    // Modal: Detail Dialog for Scanned Tax Document
    selectedDocForDetail?.let { doc ->
        ScannedTaxDocumentDetailDialog(
            document = doc,
            onDismiss = { selectedDocForDetail = null },
            onAskAi = {
                selectedDocForDetail = null
                onAskAiAboutDocument(
                    "I want to verify compliance and tax savings for this document:\n" +
                            "Title: ${doc.title}\n" +
                            "Type: ${doc.documentType}\n" +
                            "Date: ${doc.formattedDate}\n" +
                            "Vendor/Issuer: ${doc.vendorOrIssuer}\n" +
                            "Amount: ₹${doc.detectedAmount}\n" +
                            "Tax Section: ${doc.eligibleTaxSection}\n" +
                            "Summary: ${doc.aiSummary}"
                )
            }
        )
    }

    // Modal: Add Sample Scanned Document Dialog
    if (showAddSampleDialog) {
        AddSampleScannedDocumentDialog(
            onDismiss = { showAddSampleDialog = false },
            onAddSample = { title, docType, category, aiSummary, vendor, inv, amount, sec, benefit ->
                onSaveNewScannedDocument(title, docType, category, aiSummary, vendor, inv, amount, sec, benefit)
                showAddSampleDialog = false
            }
        )
    }
}

/**
 * Dedicated Card component displaying a Room-persisted Scanned Tax Document
 * showing Date, Document Type, and AI-Extracted Summary.
 */
@Composable
private fun ScannedTaxDocumentCard(
    document: ScannedTaxDocumentEntity,
    onDelete: () -> Unit,
    onSelect: () -> Unit,
    onAskAi: () -> Unit
) {
    val currency = remember { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN")) }

    val typeIcon = when (document.documentType) {
        "FORM_16", "FORM_26AS" -> Icons.Default.Description
        "SECTION_80D_HEALTH" -> Icons.Default.LocalHospital
        "GST_INVOICE" -> Icons.Default.Receipt
        "RENT_RECEIPT_HRA" -> Icons.Default.Home
        else -> Icons.AutoMirrored.Filled.ReceiptLong
    }

    val typeBadgeColor = when (document.documentType) {
        "FORM_16" -> RoyalGold
        "FORM_26AS" -> Color(0xFF6366F1)
        "SECTION_80D_HEALTH" -> EmeraldGreen
        "GST_INVOICE" -> Color(0xFF0284C7)
        "RENT_RECEIPT_HRA" -> Color(0xFFD97706)
        else -> AppColors.goldAccent
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .border(1.dp, AppColors.cardBorder, RoundedCornerShape(14.dp))
            .clickable { onSelect() }
            .testTag("scanned_doc_card_${document.id}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.cardBackground)
    ) {
        Column(
            modifier = Modifier.padding(14.dp)
        ) {
            // Header Row: Type Badge + Scan Date & Delete
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(typeBadgeColor.copy(alpha = 0.15f))
                            .border(1.dp, typeBadgeColor.copy(alpha = 0.5f), RoundedCornerShape(6.dp))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = document.documentType.replace("_", " "),
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = typeBadgeColor
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = AppColors.textMuted,
                            modifier = Modifier.size(11.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = document.formattedDate,
                            fontSize = 10.sp,
                            color = AppColors.textSecondary
                        )
                    }
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete scanned document",
                        tint = AppColors.textMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Document Title & Main Metadata
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(typeBadgeColor.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = typeIcon,
                        contentDescription = null,
                        tint = typeBadgeColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = document.title,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.textPrimary
                    )

                    if (document.vendorOrIssuer.isNotBlank()) {
                        Text(
                            text = "Issuer: ${document.vendorOrIssuer}" +
                                    if (document.invoiceOrRefNumber.isNotBlank()) " • Ref: ${document.invoiceOrRefNumber}" else "",
                            fontSize = 11.sp,
                            color = AppColors.textSecondary
                        )
                    }

                    if (document.detectedAmount > 0) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "Detected Amount: ${currency.format(document.detectedAmount)}",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = EmeraldGreen
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // AI-EXTRACTED SUMMARY BOX (Core Feature)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(AppColors.inputBackground)
                    .border(1.dp, AppColors.goldAccent.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Column {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = AppColors.goldAccent,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "AI-EXTRACTED SUMMARY",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Black,
                                color = AppColors.goldAccent,
                                letterSpacing = 0.5.sp
                            )
                        }

                        if (document.confidenceScore > 0) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = EmeraldGreen,
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "${document.confidenceScore}% Confidence",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = EmeraldGreen
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = document.aiSummary,
                        fontSize = 11.sp,
                        color = AppColors.textPrimary,
                        lineHeight = 16.sp
                    )

                    if (document.eligibleTaxSection.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = null,
                                tint = EmeraldGreen,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "Eligible Section: ${document.eligibleTaxSection}" +
                                        if (document.taxBenefitDescription.isNotBlank()) " • ${document.taxBenefitDescription}" else "",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = EmeraldGreen
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Bottom Action Bar: Ask AI & View Details
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedButton(
                    onClick = onAskAi,
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = AppColors.goldAccent,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Consult AI",
                        fontSize = 10.sp,
                        color = AppColors.goldAccent,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onSelect,
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.goldAccent),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(32.dp)
                ) {
                    Text(
                        text = "Details",
                        fontSize = 10.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Detailed Dialog for inspecting a Scanned Tax Document's full Room record
 */
@Composable
private fun ScannedTaxDocumentDetailDialog(
    document: ScannedTaxDocumentEntity,
    onDismiss: () -> Unit,
    onAskAi: () -> Unit
) {
    val currency = remember { NumberFormat.getCurrencyInstance(Locale.forLanguageTag("en-IN")) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Description,
                        contentDescription = null,
                        tint = AppColors.goldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Scanned Tax Document Record",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.textPrimary
                    )
                }
                Text(
                    text = "Room SQLite Database Record #${document.id}",
                    fontSize = 11.sp,
                    color = AppColors.textSecondary
                )
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                DetailRow(label = "Title", value = document.title)
                DetailRow(label = "Document Type", value = document.documentType)
                DetailRow(label = "Category", value = document.category)
                DetailRow(label = "Scan Date & Time", value = document.formattedDate)

                if (document.vendorOrIssuer.isNotBlank()) {
                    DetailRow(label = "Vendor / Issuer", value = document.vendorOrIssuer)
                }
                if (document.invoiceOrRefNumber.isNotBlank()) {
                    DetailRow(label = "Reference / Invoice #", value = document.invoiceOrRefNumber)
                }
                if (document.detectedAmount > 0) {
                    DetailRow(label = "Detected Amount", value = currency.format(document.detectedAmount))
                }
                if (document.eligibleTaxSection.isNotBlank()) {
                    DetailRow(label = "Eligible Tax Section", value = document.eligibleTaxSection)
                }
                if (document.taxBenefitDescription.isNotBlank()) {
                    DetailRow(label = "Tax Benefit", value = document.taxBenefitDescription)
                }
                DetailRow(label = "OCR Confidence", value = "${document.confidenceScore}%")
                DetailRow(label = "Status", value = document.status)

                Spacer(modifier = Modifier.height(4.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppColors.inputBackground, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Text(
                            text = "AI-EXTRACTED COMPLIANCE SUMMARY:",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Black,
                            color = AppColors.goldAccent
                        )
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = document.aiSummary,
                            fontSize = 11.sp,
                            color = AppColors.textPrimary,
                            lineHeight = 15.sp
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onAskAi,
                colors = ButtonDefaults.buttonColors(containerColor = AppColors.goldAccent)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Ask AI About This", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = AppColors.textSecondary)
            }
        },
        containerColor = AppColors.cardBackground
    )
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 11.sp, color = AppColors.textSecondary)
        Text(text = value, fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AppColors.textPrimary)
    }
}

private data class SampleScannedTemplate(
    val title: String,
    val docType: String,
    val category: String,
    val summary: String,
    val vendor: String,
    val amount: Double,
    val section: String,
    val benefit: String
)

/**
 * Dialog to add a sample scanned tax document into the local Room database
 */
@Composable
private fun AddSampleScannedDocumentDialog(
    onDismiss: () -> Unit,
    onAddSample: (
        title: String,
        documentType: String,
        category: String,
        aiSummary: String,
        vendorOrIssuer: String,
        invoiceNumber: String,
        amount: Double,
        taxSection: String,
        taxBenefit: String
    ) -> Unit
) {
    val sampleTemplates = listOf(
        SampleScannedTemplate(
            title = "Form 26AS Annual Tax Credit",
            docType = "FORM_26AS",
            category = "INCOME_TAX",
            summary = "Income Tax Department • Annual statement of tax deducted at source (TDS). Gross TDS deposited: ₹72,400 across Q1-Q4. Validated against CPC portal.",
            vendor = "Income Tax Department",
            amount = 72400.0,
            section = "Sec 192",
            benefit = "Full TDS credit against total tax payable"
        ),
        SampleScannedTemplate(
            title = "Apollo Pharmacy Medical Bills",
            docType = "SECTION_80D_HEALTH",
            category = "DEDUCTIONS",
            summary = "Apollo Hospitals • Prescribed medical tests and consultations for senior citizen parents. Total: ₹14,500. Eligible under Section 80D.",
            vendor = "Apollo Hospitals",
            amount = 14500.0,
            section = "Sec 80D",
            benefit = "Deduction for senior citizen medical expenditure"
        ),
        SampleScannedTemplate(
            title = "HDFC ELSS Tax Saver Mutual Fund",
            docType = "SECTION_80C_INVESTMENT",
            category = "DEDUCTIONS",
            summary = "HDFC Mutual Fund • ELSS Tax Saver NAV statement. Investment amount: ₹50,000. 3-year statutory lock-in. Eligible under Section 80C.",
            vendor = "HDFC AMC",
            amount = 50000.0,
            section = "Sec 80C",
            benefit = "Qualifies for Section 80C deduction up to ₹1.5L cap"
        ),
        SampleScannedTemplate(
            title = "AWS Cloud Infrastructure GST Invoice",
            docType = "GST_INVOICE",
            category = "GST",
            summary = "Amazon Web Services India • GSTIN: 29AABCA1298K1ZY • Cloud computing server services. Amount: ₹18,000 + 18% GST (₹3,240). Total: ₹21,240.",
            vendor = "AWS India",
            amount = 21240.0,
            section = "Sec 16 (ITC)",
            benefit = "Eligible for Input Tax Credit (ITC) of ₹3,240"
        )
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Add Sample Scanned Tax Document",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.textPrimary
            )
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "Select a realistic tax document to test Room local storage with metadata and AI summary:",
                    fontSize = 12.sp,
                    color = AppColors.textSecondary
                )

                sampleTemplates.forEach { template ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onAddSample(
                                    template.title,
                                    template.docType,
                                    template.category,
                                    template.summary,
                                    template.vendor,
                                    "REF-${System.currentTimeMillis() % 10000}",
                                    template.amount,
                                    template.section,
                                    template.benefit
                                )
                            },
                        shape = RoundedCornerShape(10.dp),
                        colors = CardDefaults.cardColors(containerColor = AppColors.inputBackground),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.cardBorder)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(template.title, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = AppColors.textPrimary)
                                Text(template.section, fontSize = 10.sp, fontWeight = FontWeight.Black, color = EmeraldGreen)
                            }
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(template.summary, fontSize = 10.sp, color = AppColors.textSecondary, maxLines = 2)
                        }
                    }
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = AppColors.textSecondary)
            }
        },
        containerColor = AppColors.cardBackground
    )
}

@Composable
private fun DocumentCard(doc: DocumentEntity, onDelete: () -> Unit) {
    val isDark = AppColors.isDark
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.cardBackground),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.cardBorder)
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
                            .size(36.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(AppColors.chipBackground),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Description,
                            contentDescription = null,
                            tint = AppColors.goldAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(doc.title, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AppColors.textPrimary)
                        Text("${doc.docType} • ${doc.fileSizeFormatted}", fontSize = 10.sp, color = AppColors.textSecondary)
                    }
                }

                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete",
                        tint = AppColors.textMuted,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            if (doc.extractedFieldsJson.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppColors.inputBackground, RoundedCornerShape(8.dp))
                        .padding(10.dp)
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Verified,
                                contentDescription = null,
                                tint = if (isDark) EmeraldGreen else Color(0xFF059669),
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                "OCR Confidence: ${doc.ocrConfidence}%",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (isDark) EmeraldGreen else Color(0xFF059669)
                            )
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            doc.extractedFieldsJson,
                            fontSize = 11.sp,
                            color = AppColors.textPrimary,
                            lineHeight = 16.sp
                        )
                    }
                }
            }
        }
    }
}
