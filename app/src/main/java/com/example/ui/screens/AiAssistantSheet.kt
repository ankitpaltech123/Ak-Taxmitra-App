package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.WorkspacePremium
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.ai.AiModelPreference
import com.example.data.ai.ChatbotRole
import com.example.data.model.ChatMessage
import com.example.ui.components.FlowAuthorityType
import com.example.ui.components.OfficialFlowBadge
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldLight

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AiAssistantDialog(
    messages: List<ChatMessage>,
    isThinking: Boolean,
    selectedRole: ChatbotRole,
    selectedModelPreference: AiModelPreference,
    onSelectRole: (ChatbotRole) -> Unit,
    onSelectModelPreference: (AiModelPreference) -> Unit,
    onOpenVoiceLive: () -> Unit,
    onClearHistory: () -> Unit,
    onSendMessage: (String) -> Unit,
    onNavigateTab: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding(),
            color = MidnightNavy
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                // Top Header Bar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0B1220))
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(34.dp)
                                .clip(CircleShape)
                                .background(Brush.linearGradient(listOf(RoyalGold, Color(0xFFAA8015)))),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = Color.Black,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Column {
                            Text(
                                text = "AK Taxmitra AI Copilot",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                            Text(
                                text = selectedRole.title,
                                fontSize = 11.sp,
                                color = RoyalGoldLight
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Voice Live Conversation Launch Button
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(16.dp))
                                .background(Color(0xFF1E293B))
                                .border(1.dp, RoyalGold.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                                .clickable { onOpenVoiceLive() }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                                .testTag("btn_launch_voice_live")
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.GraphicEq,
                                    contentDescription = "Voice Mode",
                                    tint = Color(0xFF10B981),
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Voice Live",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(4.dp))

                        IconButton(
                            onClick = onClearHistory,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.DeleteOutline,
                                contentDescription = "Clear Chat",
                                tint = Color(0xFF94A3B8),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Chatbot Role Switcher (System Instruction Customization)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A))
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val availableRoles = listOf(
                        ChatbotRole.TAX_ADVISOR,
                        ChatbotRole.BUSINESS_ARCHITECT,
                        ChatbotRole.GST_NOTICE_COUNSEL
                    )
                    availableRoles.forEach { role ->
                        val isSelected = selectedRole == role
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) RoyalGold else Color(0xFF1E293B))
                                .clickable { onSelectRole(role) }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = role.title,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) Color.Black else Color(0xFFCBD5E1)
                            )
                        }
                    }
                }

                // Gemini Model Switcher (gemini-3.1-pro-preview, gemini-3.5-flash, gemini-3.1-flash-lite)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF111827))
                        .horizontalScroll(rememberScrollState())
                        .padding(horizontal = 12.dp, vertical = 5.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Model:",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Bold
                    )

                    AiModelPreference.entries.forEach { pref ->
                        val isSelected = selectedModelPreference == pref
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Color(0xFF2563EB) else Color(0xFF1F2937))
                                .border(
                                    1.dp,
                                    if (isSelected) Color(0xFF60A5FA) else Color(0xFF374151),
                                    RoundedCornerShape(10.dp)
                                )
                                .clickable { onSelectModelPreference(pref) }
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val icon = when (pref) {
                                    AiModelPreference.LITE_FAST -> Icons.Default.Speed
                                    AiModelPreference.PRO_COMPLEX -> Icons.Default.WorkspacePremium
                                    else -> Icons.Default.Psychology
                                }
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = if (isSelected) Color.White else Color(0xFF9CA3AF),
                                    modifier = Modifier.size(11.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = pref.displayName,
                                    fontSize = 10.sp,
                                    color = if (isSelected) Color.White else Color(0xFFD1D5DB),
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                                )
                            }
                        }
                    }
                }

                // Multi-Turn Chat Message Stream
                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(messages) { message ->
                        ChatBubbleItem(
                            message = message,
                            onActionClicked = { action ->
                                when {
                                    action.contains("Business") -> {
                                        onDismiss()
                                        onNavigateTab("BUSINESS")
                                    }
                                    action.contains("Tax") || action.contains("Regime") -> {
                                        onDismiss()
                                        onNavigateTab("TAX")
                                    }
                                    action.contains("CA") -> {
                                        onDismiss()
                                        onNavigateTab("CA")
                                    }
                                    action.contains("Compliance") -> {
                                        onDismiss()
                                        onNavigateTab("COMPLIANCE")
                                    }
                                    else -> onSendMessage(action)
                                }
                            }
                        )
                    }

                    if (isThinking) {
                        item {
                            Row(
                                modifier = Modifier
                                    .background(Color(0xFF131D31), RoundedCornerShape(14.dp))
                                    .padding(horizontal = 14.dp, vertical = 10.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = RoyalGold
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "Consulting Gemini (${selectedModelPreference.displayName})...",
                                    fontSize = 12.sp,
                                    color = Color(0xFFCBD5E1)
                                )
                            }
                        }
                    }
                }

                // Contextual Role Suggested Prompts
                FlowRow(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val promptSuggestions = when (selectedRole) {
                        ChatbotRole.TAX_ADVISOR -> listOf(
                            "Calculate tax on ₹12L salary",
                            "New vs Old Regime 87A rebate",
                            "How to claim 80CCD(1B) NPS?",
                            "Which ITR form for freelance income?"
                        )
                        ChatbotRole.BUSINESS_ARCHITECT -> listOf(
                            "LLP vs Pvt Ltd with 2 founders",
                            "Is Udyam MSME registration free?",
                            "Class 25 trademark for clothing",
                            "Steps to get GST & Current Account"
                        )
                        ChatbotRole.GST_NOTICE_COUNSEL -> listOf(
                            "Do I need GST for Amazon store?",
                            "Section 143(1) intimation mismatch",
                            "Section 148 reassessment notice",
                            "30 days deadline response guide"
                        )
                        else -> listOf(
                            "Start Business",
                            "Calculate Tax",
                            "Check GST",
                            "Find a CA"
                        )
                    }

                    promptSuggestions.take(3).forEach { chip ->
                        Box(
                            modifier = Modifier
                                .background(Color(0xFF162035), RoundedCornerShape(14.dp))
                                .border(1.dp, Color(0xFF2E3F60), RoundedCornerShape(14.dp))
                                .clickable { onSendMessage(chip) }
                                .padding(horizontal = 9.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = chip,
                                fontSize = 11.sp,
                                color = Color(0xFFE2E8F0),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                // Input Bar with Text, Live Voice Mic, and Send
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF0B1220))
                        .padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Voice conversation mic button
                    IconButton(
                        onClick = onOpenVoiceLive,
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                            .testTag("chat_voice_mic_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = "Voice Conversation",
                            tint = RoyalGoldLight,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    OutlinedTextField(
                        value = inputText,
                        onValueChange = { inputText = it },
                        modifier = Modifier
                            .weight(1f)
                            .testTag("chat_input_textfield"),
                        placeholder = {
                            Text("Ask ${selectedRole.title}...", fontSize = 13.sp, color = Color(0xFF64748B))
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = RoyalGold,
                            unfocusedBorderColor = Color(0xFF2E3F60),
                            focusedContainerColor = Color(0xFF080D1A),
                            unfocusedContainerColor = Color(0xFF080D1A)
                        ),
                        shape = RoundedCornerShape(22.dp),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(6.dp))

                    IconButton(
                        onClick = {
                            if (inputText.isNotBlank()) {
                                onSendMessage(inputText)
                                inputText = ""
                            }
                        },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(RoyalGold)
                            .testTag("chat_send_btn")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.Send,
                            contentDescription = "Send",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChatBubbleItem(
    message: ChatMessage,
    onActionClicked: (String) -> Unit
) {
    val isUser = message.sender == "USER"
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        if (!isUser) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(bottom = 4.dp)
            ) {
                if (message.officialFlowTag != null) {
                    OfficialFlowBadge(
                        authorityType = FlowAuthorityType.TAXMITRA_AI,
                        label = message.officialFlowTag
                    )
                }
                if (message.modelUsed != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "• ${message.modelUsed}",
                        fontSize = 10.sp,
                        color = Color(0xFF64748B),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxWidth(if (isUser) 0.82f else 0.96f)
                .background(
                    if (isUser) Color(0xFF1E3A8A) else Color(0xFF131D31),
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isUser) 16.dp else 4.dp,
                        bottomEnd = if (isUser) 4.dp else 16.dp
                    )
                )
                .border(
                    1.dp,
                    if (isUser) Color(0xFF2563EB) else Color(0xFF23304A),
                    RoundedCornerShape(16.dp)
                )
                .padding(13.dp)
        ) {
            Text(
                text = message.message,
                fontSize = 13.sp,
                lineHeight = 19.sp,
                color = if (isUser) Color.White else Color(0xFFE2E8F0)
            )
        }

        if (!isUser && message.quickActions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(6.dp))
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                message.quickActions.forEach { action ->
                    Box(
                        modifier = Modifier
                            .background(Color(0xFF223250), RoundedCornerShape(12.dp))
                            .border(1.dp, RoyalGold.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .clickable { onActionClicked(action) }
                            .padding(horizontal = 9.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = action,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = RoyalGoldLight
                        )
                    }
                }
            }
        }
    }
}
