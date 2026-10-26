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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.SmartToy
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.data.ai.ChatbotRole
import com.example.data.engine.IntentRouteResult
import com.example.data.engine.IntentRouter
import com.example.data.model.ChatMessage
import com.example.data.model.UserEntityContext
import com.example.ui.components.LogoSize
import com.example.ui.components.TaxmitraBrandLogo
import com.example.ui.theme.AiGradientEnd
import com.example.ui.theme.AiGradientStart
import com.example.ui.theme.ChampagneGold
import com.example.ui.theme.ChampagneGoldLight
import com.example.ui.theme.DarkCardBorder
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.ElectricViolet
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.RoyalNavy

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiCommandCenterScreen(
    currentEntity: UserEntityContext,
    messages: List<ChatMessage>,
    isThinking: Boolean,
    selectedRole: ChatbotRole,
    onSelectRole: (ChatbotRole) -> Unit,
    onSendMessage: (String) -> Unit,
    onOpenVoiceLive: () -> Unit,
    onNavigateWorkflow: (String, String?) -> Unit, // tab, workflowKey
    modifier: Modifier = Modifier
) {
    var inputText by remember { mutableStateOf("") }
    var activeIntentRoute by remember { mutableStateOf<IntentRouteResult?>(null) }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val quickIntents = listOf(
        "File my tax" to "ITR Guided Filing",
        "Start my business" to "Business Launch OS",
        "Calculate my tax" to "Old vs New Regimes",
        "Explain this notice" to "Notice Intelligence",
        "Find me a CA" to "CA Marketplace",
        "Create an invoice" to "GST Sales Invoice",
        "Check my GST" to "GSTR-1 & 3B Status",
        "What do I need to do this month?" to "Compliance Deadlines",
        "How can I save tax?" to "80C & Regime Optimizers",
        "Show my tax documents" to "AK Vault"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MidnightNavy)
    ) {
        // AI Command Center Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        listOf(Color(0xFF0F1A2E), MidnightNavy)
                    )
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TaxmitraBrandLogo(
                        size = LogoSize.COMPACT,
                        showTagline = false
                    )

                    // Live Voice Quick Trigger
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color(0xFF10B981).copy(alpha = 0.15f))
                            .border(1.dp, Color(0xFF10B981), RoundedCornerShape(20.dp))
                            .clickable { onOpenVoiceLive() }
                            .padding(horizontal = 10.dp, vertical = 5.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.GraphicEq,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Live Voice",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF10B981)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Specialized Agent Role Selector Chips
                Text(
                    text = "SPECIALIZED INTELLIGENCE AGENTS",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    ChatbotRole.entries.forEach { role ->
                        val isSelected = role == selectedRole
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(if (isSelected) ChampagneGold else Color(0xFF152238))
                                .border(
                                    1.dp,
                                    if (isSelected) ChampagneGoldLight else Color(0xFF2A3A58),
                                    RoundedCornerShape(16.dp)
                                )
                                .clickable { onSelectRole(role) }
                                .padding(horizontal = 10.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = role.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.Black else Color(0xFFCBD5E1)
                            )
                        }
                    }
                }
            }
        }

        // Active Intent Router Card (When intent is detected)
        AnimatedVisibility(visible = activeIntentRoute != null) {
            activeIntentRoute?.let { route ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .border(
                            1.dp,
                            Brush.horizontalGradient(listOf(ChampagneGold, ElectricBlue)),
                            RoundedCornerShape(14.dp)
                        ),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF131D31))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(Brush.linearGradient(listOf(ElectricBlue, ElectricViolet))),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.AutoAwesome,
                                        contentDescription = null,
                                        tint = Color.White,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = route.guidanceTitle,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                            Text(
                                text = "WORKFLOW",
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold,
                                color = ChampagneGold,
                                modifier = Modifier
                                    .background(Color(0xFF223049), RoundedCornerShape(4.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = route.guidanceSubtitle,
                            fontSize = 12.sp,
                            color = Color(0xFF94A3B8),
                            lineHeight = 16.sp
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End
                        ) {
                            Button(
                                onClick = {
                                    onNavigateWorkflow(route.targetTab, route.workflowKey)
                                    activeIntentRoute = null
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = ChampagneGold),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = route.actionLabel,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Conversation Messages & Orchestrated Cards
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Introductory Prompt Guidance
            item {
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF10192A))
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = ChampagneGold,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "How can Taxmitra help you today?",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Operating Entity: ${currentEntity.name} (${currentEntity.entityType}) • ${currentEntity.taxYear}",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Intent Chips
                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            quickIntents.forEach { (promptText, badge) ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFF18263E))
                                        .border(1.dp, Color(0xFF283A5A), RoundedCornerShape(12.dp))
                                        .clickable {
                                            val routed = IntentRouter.route(promptText)
                                            activeIntentRoute = routed
                                            onSendMessage(promptText)
                                        }
                                        .padding(horizontal = 10.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = promptText,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Medium,
                                        color = Color(0xFFE2E8F0)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Message stream
            items(messages) { msg ->
                val isUser = msg.sender == "USER"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    if (!isUser) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(ElectricBlue, ElectricViolet))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.SmartToy,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                    }

                    Card(
                        modifier = Modifier.widthIn(max = 310.dp),
                        shape = RoundedCornerShape(
                            topStart = 14.dp,
                            topEnd = 14.dp,
                            bottomStart = if (isUser) 14.dp else 2.dp,
                            bottomEnd = if (isUser) 2.dp else 14.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = if (isUser) Color(0xFF1E3A8A) else Color(0xFF142036)
                        ),
                        border = if (!isUser) androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF243450)) else null
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = msg.message,
                                fontSize = 13.sp,
                                color = Color.White,
                                lineHeight = 19.sp
                            )

                            if (!msg.quickActions.isNullOrEmpty()) {
                                Spacer(modifier = Modifier.height(10.dp))
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    msg.quickActions.forEach { actionText ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedCornerShape(10.dp))
                                                .background(Color(0xFF223455))
                                                .border(1.dp, ChampagneGold.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                                                .clickable {
                                                    val routed = IntentRouter.route(actionText)
                                                    activeIntentRoute = routed
                                                    onSendMessage(actionText)
                                                }
                                                .padding(horizontal = 8.dp, vertical = 4.dp)
                                        ) {
                                            Text(
                                                text = actionText,
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = ChampagneGoldLight
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Thinking State Indicator
            if (isThinking) {
                item {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(start = 36.dp, top = 4.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = ChampagneGold,
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Taxmitra AI is analyzing tax rules & entity context...",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }
                }
            }
        }

        // Bottom Universal Input Bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MidnightNavy)
                .padding(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color(0xFF141F33), RoundedCornerShape(24.dp))
                    .border(1.dp, Color(0xFF253755), RoundedCornerShape(24.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Voice input shortcut
                IconButton(
                    onClick = onOpenVoiceLive,
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Mic,
                        contentDescription = "Voice Input",
                        tint = Color(0xFF10B981),
                        modifier = Modifier.size(20.dp)
                    )
                }

                OutlinedTextField(
                    value = inputText,
                    onValueChange = {
                        inputText = it
                        if (it.length > 3) {
                            activeIntentRoute = IntentRouter.route(it)
                        }
                    },
                    placeholder = {
                        Text(
                            text = "Ask Taxmitra anything... (ITR, Business, GST, Notice)",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    },
                    modifier = Modifier.weight(1f),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    maxLines = 2
                )

                IconButton(
                    onClick = {
                        if (inputText.isNotBlank()) {
                            val text = inputText.trim()
                            activeIntentRoute = IntentRouter.route(text)
                            onSendMessage(text)
                            inputText = ""
                        }
                    },
                    modifier = Modifier
                        .size(36.dp)
                        .clip(CircleShape)
                        .background(
                            if (inputText.isNotBlank()) ChampagneGold else Color(0xFF223049)
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = if (inputText.isNotBlank()) Color.Black else Color.Gray,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
