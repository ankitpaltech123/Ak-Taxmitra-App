package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.EmeraldGreen
import com.example.ui.theme.RoyalBlue
import com.example.ui.theme.RoyalGold

enum class FlowAuthorityType {
    OFFICIAL_GOVERNMENT,
    AUTHORIZED_INTEGRATION,
    TAXMITRA_AI,
    THIRD_PARTY
}

@Composable
fun OfficialFlowBadge(
    authorityType: FlowAuthorityType = FlowAuthorityType.OFFICIAL_GOVERNMENT,
    label: String = "OFFICIAL GOVERNMENT FLOW",
    modifier: Modifier = Modifier
) {
    val (bgColor, borderColor, textColor, icon) = when (authorityType) {
        FlowAuthorityType.OFFICIAL_GOVERNMENT -> Quadruple(
            Color(0xFF1E293B),
            RoyalGold,
            RoyalGold,
            Icons.Default.AccountBalance
        )
        FlowAuthorityType.AUTHORIZED_INTEGRATION -> Quadruple(
            Color(0xFF0F233A),
            RoyalBlue,
            Color(0xFF93C5FD),
            Icons.Default.Verified
        )
        FlowAuthorityType.TAXMITRA_AI -> Quadruple(
            Color(0xFF1E1B2E),
            Color(0xFFA855F7),
            Color(0xFFE9D5FF),
            Icons.Default.AutoAwesome
        )
        FlowAuthorityType.THIRD_PARTY -> Quadruple(
            Color(0xFF182230),
            Color(0xFF64748B),
            Color(0xFFCBD5E1),
            Icons.Default.Security
        )
    }

    Row(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(20.dp))
            .border(1.dp, borderColor.copy(alpha = 0.6f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = textColor,
            modifier = Modifier.size(12.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = label,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp,
            color = textColor
        )
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
