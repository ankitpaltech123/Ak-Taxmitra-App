package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.ui.platform.testTag
import com.example.data.model.UserRole
import com.example.ui.theme.AppColors
import com.example.ui.theme.ChampagneGoldLight
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldLight

@Composable
fun TaxmitraTopBar(
    currentRole: UserRole,
    onRoleSelected: (UserRole) -> Unit,
    onOpenAiChat: () -> Unit,
    onOpenVoiceLive: () -> Unit = {},
    isDarkMode: Boolean = false,
    onToggleTheme: () -> Unit = {}
) {
    var showRoleMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AppColors.topBarBackground)
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Official AK Taxmitra AI Brand Logo & Tagline
            TaxmitraBrandLogo(
                size = LogoSize.COMPACT,
                showTagline = true,
                isDarkBackground = AppColors.isDark,
                modifier = Modifier.weight(1f, fill = false)
            )

            // Quick Actions: Role Selector, Theme Switcher, Voice Live, AI Trigger
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Role Selector Chip
                Box {
                    Row(
                        modifier = Modifier
                            .background(AppColors.chipBackground, RoundedCornerShape(20.dp))
                            .border(1.dp, AppColors.chipBorder, RoundedCornerShape(20.dp))
                            .clickable { showRoleMenu = true }
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = when (currentRole) {
                                UserRole.BEGINNER -> "Beginner"
                                UserRole.SALARIED -> "Salaried"
                                UserRole.FREELANCER -> "Freelancer"
                                UserRole.BUSINESS_OWNER -> "Business"
                                UserRole.CA -> "CA Mode"
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = if (AppColors.isDark) RoyalGoldLight else Color(0xFFB45309)
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = if (AppColors.isDark) RoyalGoldLight else Color(0xFFB45309),
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showRoleMenu,
                        onDismissRequest = { showRoleMenu = false },
                        modifier = Modifier
                            .background(AppColors.surface)
                            .border(1.dp, AppColors.cardBorder, RoundedCornerShape(8.dp))
                    ) {
                        UserRole.values().forEach { role ->
                            DropdownMenuItem(
                                text = {
                                    Column {
                                        Text(
                                            role.displayName,
                                            fontWeight = FontWeight.Bold,
                                            color = AppColors.textPrimary,
                                            fontSize = 13.sp
                                        )
                                        Text(role.subtitle, color = AppColors.textSecondary, fontSize = 10.sp)
                                    }
                                },
                                leadingIcon = if (role == currentRole) {
                                    { Icon(Icons.Default.Check, contentDescription = null, tint = AppColors.goldAccent) }
                                } else null,
                                onClick = {
                                    onRoleSelected(role)
                                    showRoleMenu = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Theme Switcher: Normal Mode (☀️) vs Dark Mode (🌙)
                IconButton(
                    onClick = onToggleTheme,
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            AppColors.chipBackground,
                            CircleShape
                        )
                        .border(1.dp, AppColors.chipBorder, CircleShape)
                        .testTag("theme_toggle_button")
                ) {
                    Icon(
                        imageVector = if (AppColors.isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                        contentDescription = if (AppColors.isDark) "Switch to Normal Mode" else "Switch to Dark Mode",
                        tint = if (AppColors.isDark) ChampagneGoldLight else Color(0xFFB45309),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // Voice Live Conversation Trigger Button
                IconButton(
                    onClick = onOpenVoiceLive,
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            if (AppColors.isDark) Color(0xFF1E293B) else Color(0xFFECFDF5),
                            CircleShape
                        )
                        .border(1.dp, Color(0xFF10B981).copy(alpha = 0.6f), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Voice Live Conversation",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(18.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                // AI Sparkle Assistant Button
                IconButton(
                    onClick = onOpenAiChat,
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            Brush.linearGradient(
                                if (AppColors.isDark) listOf(RoyalGold, Color(0xFFAA8015))
                                else listOf(Color(0xFFD97706), Color(0xFFB45309))
                            ),
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Ask Taxmitra AI",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
