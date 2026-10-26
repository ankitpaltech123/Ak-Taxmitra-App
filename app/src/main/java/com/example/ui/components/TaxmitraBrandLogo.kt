package com.example.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.MidnightNavy

enum class LogoSize {
    COMPACT,
    STANDARD,
    HERO
}

@Composable
fun TaxmitraBrandLogo(
    modifier: Modifier = Modifier,
    size: LogoSize = LogoSize.STANDARD,
    showTagline: Boolean = true,
    isDarkBackground: Boolean = true
) {
    val iconSize: Dp = when (size) {
        LogoSize.COMPACT -> 32.dp
        LogoSize.STANDARD -> 48.dp
        LogoSize.HERO -> 72.dp
    }

    val titleFontSize = when (size) {
        LogoSize.COMPACT -> 16.sp
        LogoSize.STANDARD -> 20.sp
        LogoSize.HERO -> 28.sp
    }

    val taglineFontSize = when (size) {
        LogoSize.COMPACT -> 9.sp
        LogoSize.STANDARD -> 11.sp
        LogoSize.HERO -> 13.sp
    }

    val primaryTextColor = if (isDarkBackground) Color.White else MidnightNavy
    val taglineColor = if (isDarkBackground) Color(0xFF94A3B8) else Color(0xFF64748B)

    val aiGradientBrush = Brush.linearGradient(
        colors = listOf(ElectricBlue, ElectricViolet)
    )

    when (size) {
        LogoSize.COMPACT -> {
            Row(
                modifier = modifier,
                verticalAlignment = Alignment.CenterVertically
            ) {
                EmblemIcon(size = iconSize, isDark = isDarkBackground)
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = buildAnnotatedString {
                            withStyle(SpanStyle(color = primaryTextColor, fontWeight = FontWeight.Bold)) {
                                append("AK Taxmitra ")
                            }
                            withStyle(
                                SpanStyle(
                                    brush = aiGradientBrush,
                                    fontWeight = FontWeight.Black
                                )
                            ) {
                                append("AI")
                            }
                        },
                        fontSize = titleFontSize,
                        letterSpacing = (-0.3).sp
                    )
                    if (showTagline) {
                        Text(
                            text = "One App. Every Taxpayer. Every CA. Every Business.",
                            fontSize = taglineFontSize,
                            color = taglineColor,
                            fontWeight = FontWeight.Normal,
                            maxLines = 1
                        )
                    }
                }
            }
        }
        LogoSize.STANDARD, LogoSize.HERO -> {
            Column(
                modifier = modifier,
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                EmblemIcon(size = iconSize, isDark = isDarkBackground)
                Spacer(modifier = Modifier.height(if (size == LogoSize.HERO) 12.dp else 8.dp))
                Text(
                    text = buildAnnotatedString {
                        withStyle(SpanStyle(color = primaryTextColor, fontWeight = FontWeight.Bold)) {
                            append("AK Taxmitra ")
                        }
                        withStyle(
                            SpanStyle(
                                brush = aiGradientBrush,
                                fontWeight = FontWeight.Black
                            )
                        ) {
                            append("AI")
                        }
                    },
                    fontSize = titleFontSize,
                    letterSpacing = (-0.5).sp
                )
                if (showTagline) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "One App. Every Taxpayer. Every CA. Every Business.",
                        fontSize = taglineFontSize,
                        color = taglineColor,
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.2.sp
                    )
                }
            }
        }
    }
}

@Composable
fun EmblemIcon(
    size: Dp,
    modifier: Modifier = Modifier,
    isDark: Boolean = false
) {
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(if (isDark) Color(0xFF0F172A) else Color.White)
            .border(
                width = 1.dp,
                color = if (isDark) ChampagneGold.copy(alpha = 0.4f) else Color(0xFFE2E8F0),
                shape = CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_taxmitra_emblem),
            contentDescription = "AK Taxmitra AI Official Logo",
            modifier = Modifier.size(size * 0.92f)
        )
    }
}

/**
 * Renders the exact complete logo visual matching the user's uploaded brand image:
 * Golden ring emblem with 4-pointed sparkle star, stylized blue "AK" monogram,
 * golden ascending arrow, "AK Taxmitra AI" typography and the official tagline
 * "One App. Every Taxpayer. Every CA. Every Business."
 */
@Composable
fun WholeAppLogoView(
    modifier: Modifier = Modifier,
    emblemSize: Dp = 120.dp,
    showTagline: Boolean = true,
    isDarkCanvas: Boolean = false
) {
    val titleColor = if (isDarkCanvas) Color.White else Color(0xFF0B192C)
    val taglineColor = if (isDarkCanvas) Color(0xFF94A3B8) else Color(0xFF334155)
    val aiGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF2563EB), Color(0xFF4F46E5), Color(0xFF7C3AED))
    )

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Prominent Golden Ring Emblem with AK Monogram and Star
        Box(
            modifier = Modifier
                .size(emblemSize)
                .clip(CircleShape)
                .background(if (isDarkCanvas) Color(0xFF0F1A2E) else Color.White)
                .border(
                    width = 1.5.dp,
                    brush = Brush.linearGradient(
                        listOf(Color(0xFFFDE68A), Color(0xFFD4AF37), Color(0xFFB45309))
                    ),
                    shape = CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.ic_taxmitra_emblem),
                contentDescription = "AK Taxmitra AI Emblem",
                modifier = Modifier.size(emblemSize * 0.92f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Brand Typography: "AK Taxmitra AI"
        Text(
            text = buildAnnotatedString {
                withStyle(
                    SpanStyle(
                        color = titleColor,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    )
                ) {
                    append("AK Taxmitra ")
                }
                withStyle(
                    SpanStyle(
                        brush = aiGradient,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.5).sp
                    )
                ) {
                    append("AI")
                }
            },
            fontSize = 24.sp,
            lineHeight = 28.sp
        )

        if (showTagline) {
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "One App. Every Taxpayer. Every CA. Every Business.",
                fontSize = 11.5.sp,
                color = taglineColor,
                fontWeight = FontWeight.Medium,
                letterSpacing = 0.2.sp
            )
        }
    }
}

