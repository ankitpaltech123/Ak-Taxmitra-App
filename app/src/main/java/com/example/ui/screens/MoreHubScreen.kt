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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserEntityContext
import com.example.ui.components.LogoSize
import com.example.ui.components.TaxmitraBrandLogo
import com.example.ui.theme.AppColors
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.MidnightNavy

@Composable
fun MoreHubScreen(
    currentEntity: UserEntityContext,
    onNavigateDestination: (String) -> Unit, // "GST", "ACCOUNTING", "DOCUMENTS", "COMPLIANCE", "CA", "PRICING", "SECURITY"
    onOpenAiWithPrompt: (String) -> Unit,
    isDarkMode: Boolean = false,
    onToggleTheme: () -> Unit = {},
    onLogout: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    val isDark = AppColors.isDark

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(AppColors.background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Top Brand Header
        TaxmitraBrandLogo(
            size = LogoSize.STANDARD,
            showTagline = true,
            isDarkBackground = isDark,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Operating Entity Summary Badge
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = AppColors.cardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.cardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(AppColors.goldAccent.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null,
                        tint = AppColors.goldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = currentEntity.name,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.textPrimary
                    )
                    Text(
                        text = "${currentEntity.entityType} • ${currentEntity.taxYear}",
                        fontSize = 11.sp,
                        color = AppColors.textSecondary
                    )
                }
                Text(
                    text = "ACTIVE",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isDark) Color(0xFF10B981) else Color(0xFF059669),
                    modifier = Modifier
                        .background(
                            if (isDark) Color(0xFF10B981).copy(alpha = 0.15f) else Color(0xFFD1FAE5),
                            RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // THEME PREFERENCE CARD (LIGHT / NORMAL MODE TOGGLE)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onToggleTheme() },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = AppColors.cardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.cardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(AppColors.goldAccent.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                        contentDescription = null,
                        tint = AppColors.goldAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isDarkMode) "Dark Mode Active" else "Normal (Light) Mode Active",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.textPrimary
                    )
                    Text(
                        text = if (isDarkMode) "Switch to clean, high-contrast light mode" else "Switch to dark midnight mode",
                        fontSize = 10.sp,
                        color = AppColors.textSecondary
                    )
                }
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { onToggleTheme() },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = AppColors.goldAccent,
                        uncheckedThumbColor = AppColors.goldAccent,
                        uncheckedTrackColor = Color(0xFFE2E8F0)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // AUTHENTICATION & LOGIN PAGE CARD
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onLogout() },
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = AppColors.cardBackground),
            border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.cardBorder)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF2563EB).copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = null,
                        tint = Color(0xFF2563EB),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Sign In / Switch Account",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.textPrimary
                    )
                    Text(
                        text = "View Login Page featuring the complete brand logo",
                        fontSize = 10.5.sp,
                        color = AppColors.textSecondary
                    )
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = null,
                    tint = AppColors.goldAccent,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        // SECTION: FINANCIAL & COMPLIANCE MODULES
        Text(
            text = "OPERATING SYSTEM MODULES",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.sectionHeader,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        MoreMenuTile(
            icon = Icons.Default.Receipt,
            iconTint = Color(0xFF2563EB),
            title = "GST Operating System",
            subtitle = "GSTR-1, GSTR-3B filings, ITC reconciliation, e-invoices & notice tracker",
            badge = "GSTN",
            onClick = { onNavigateDestination("GST") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        MoreMenuTile(
            icon = Icons.Default.AccountBalance,
            iconTint = if (isDark) Color(0xFF10B981) else Color(0xFF059669),
            title = "Accounting & Sales Invoices",
            subtitle = "Simple & Advanced P&L, sales ledger, cash flow, and GST invoices",
            badge = "FINANCIALS",
            onClick = { onNavigateDestination("ACCOUNTING") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        MoreMenuTile(
            icon = Icons.Default.Folder,
            iconTint = AppColors.goldAccent,
            title = "AK Vault (Document OS)",
            subtitle = "Bank-grade encrypted storage for PAN, Aadhaar, Form 16, AIS, notices",
            badge = "SECURE",
            onClick = { onNavigateDestination("DOCUMENTS") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        MoreMenuTile(
            icon = Icons.Default.CalendarMonth,
            iconTint = Color(0xFFD97706),
            title = "Compliance Calendar Center",
            subtitle = "Statutory due dates, advance tax alerts, ROC filings & penalty warnings",
            badge = "STATUTORY",
            onClick = { onNavigateDestination("COMPLIANCE") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        MoreMenuTile(
            icon = Icons.Default.Verified,
            iconTint = Color(0xFF7C3AED),
            title = "CA Marketplace & Advisory",
            subtitle = "Verified Chartered Accountants for audit, scrutiny representation, and filing",
            badge = "VERIFIED",
            onClick = { onNavigateDestination("CA") }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // SECTION: TRUST, PRICING & PRIVACY
        Text(
            text = "TRANSPARENCY & SECURITY",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = AppColors.sectionHeader,
            letterSpacing = 0.5.sp
        )

        Spacer(modifier = Modifier.height(10.dp))

        MoreMenuTile(
            icon = Icons.Default.MonetizationOn,
            iconTint = AppColors.goldAccent,
            title = "Transparent Pricing Breakdown",
            subtitle = "Zero hidden fees: AK Taxmitra Fee + Government Fee + Professional Fee",
            badge = "NO SURPRISES",
            onClick = { onNavigateDestination("PRICING") }
        )

        Spacer(modifier = Modifier.height(8.dp))

        MoreMenuTile(
            icon = Icons.Default.Security,
            iconTint = if (isDark) Color(0xFF10B981) else Color(0xFF059669),
            title = "Security & Data Privacy Center",
            subtitle = "256-bit encryption, active sessions, consent revocations & zero data sharing",
            badge = "ENCRYPTED",
            onClick = { onNavigateDestination("SECURITY") }
        )

        Spacer(modifier = Modifier.height(24.dp))

        // App Version & Trust Guarantee
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "AK Taxmitra AI • Version 3.0",
                fontSize = 11.sp,
                color = AppColors.textMuted,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Built strictly compliant with Indian Income Tax Act, 1961 & GST Act, 2017",
                fontSize = 9.sp,
                color = AppColors.textMuted
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun MoreMenuTile(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    badge: String,
    onClick: () -> Unit
) {
    val isDark = AppColors.isDark
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .border(1.dp, AppColors.cardBorder, RoundedCornerShape(12.dp)),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.cardBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(iconTint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconTint,
                    modifier = Modifier.size(20.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = title,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppColors.textPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                        modifier = Modifier
                            .background(
                                if (isDark) Color(0xFF1E2D47) else Color(0xFFFEF3C7),
                                RoundedCornerShape(4.dp)
                            )
                            .padding(horizontal = 5.dp, vertical = 1.dp)
                    ) {
                        Text(
                            text = badge,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppColors.goldAccent
                        )
                    }
                }
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = AppColors.textSecondary,
                    lineHeight = 14.sp
                )
            }
            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = null,
                tint = AppColors.textMuted,
                modifier = Modifier.size(16.dp)
            )
        }
    }
}

