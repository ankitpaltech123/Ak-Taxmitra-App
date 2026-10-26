package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.engine.NextBestAction
import com.example.ui.theme.AppColors
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.RoyalGold

@Composable
fun NextBestActionCard(
    action: NextBestAction,
    onExecuteAction: (targetTab: String) -> Unit,
    onRemindAction: ((NextBestAction) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val isDark = AppColors.isDark
    val (badgeBg, badgeText, badgeColor, icon) = when (action.urgencyLevel) {
        "CRITICAL" -> Quad(
            if (isDark) Color(0xFF450A0A) else Color(0xFFFEF2F2),
            "CRITICAL ACTION",
            if (isDark) Color(0xFFEF4444) else Color(0xFFDC2626),
            Icons.Default.Warning
        )
        "IMPORTANT" -> Quad(
            if (isDark) Color(0xFF451A03) else Color(0xFFFFFBEB),
            "NEXT BEST ACTION",
            if (isDark) Color(0xFFF59E0B) else Color(0xFFD97706),
            Icons.Default.NotificationsActive
        )
        else -> Quad(
            if (isDark) Color(0xFF064E3B) else Color(0xFFECFDF5),
            "RECOMMENDED NEXT",
            if (isDark) Color(0xFF10B981) else Color(0xFF059669),
            Icons.Default.Star
        )
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .border(
                1.5.dp,
                Brush.horizontalGradient(
                    listOf(
                        badgeColor.copy(alpha = if (isDark) 0.8f else 0.5f),
                        AppColors.goldAccent.copy(alpha = 0.4f)
                    )
                ),
                RoundedCornerShape(18.dp)
            ),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = AppColors.cardBackground)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .background(badgeBg, RoundedCornerShape(12.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = badgeColor,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = badgeText,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Black,
                        letterSpacing = 0.5.sp,
                        color = badgeColor
                    )
                }

                if (action.officialFlowBadge != null) {
                    OfficialFlowBadge(
                        authorityType = FlowAuthorityType.OFFICIAL_GOVERNMENT,
                        label = action.officialFlowBadge
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = action.title,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                color = AppColors.textPrimary
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = action.description,
                fontSize = 13.sp,
                color = AppColors.textSecondary,
                lineHeight = 18.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { onExecuteAction(action.targetTab) },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = AppColors.goldAccent),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = action.actionButtonText,
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                if (onRemindAction != null) {
                    androidx.compose.material3.OutlinedButton(
                        onClick = { onRemindAction(action) },
                        shape = RoundedCornerShape(12.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.goldAccent.copy(alpha = 0.6f))
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = "Remind Me",
                            tint = AppColors.goldAccent,
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Remind",
                            color = AppColors.goldAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }
        }
    }
}

private data class Quad<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
