package com.example.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.ai.GeminiModels
import com.example.data.ai.VoiceState
import com.example.data.model.ChatMessage
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.RoyalGoldLight

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VoiceConversationDialog(
    voiceState: VoiceState,
    audioRms: Float,
    liveTranscript: String,
    messages: List<ChatMessage>,
    onStartListening: () -> Unit,
    onStopListening: () -> Unit,
    onStopSpeaking: () -> Unit,
    onSendPrompt: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var selectedLiveModel by remember { mutableStateOf(GeminiModels.MODEL_LIVE_VOICE) }
    val voiceMessages = remember(messages) { messages.filter { it.isVoiceMessage } }
    val listState = rememberLazyListState()

    val context = LocalContext.current
    var hasMicPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val micPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasMicPermission = isGranted
        if (isGranted) {
            onStartListening()
        }
    }

    LaunchedEffect(voiceMessages.size) {
        if (voiceMessages.isNotEmpty()) {
            listState.animateScrollToItem(voiceMessages.size - 1)
        }
    }

    // Infinite breathing pulse animation for the orb
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (voiceState == VoiceState.LISTENING || voiceState == VoiceState.SPEAKING) 1.25f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            color = MidnightNavy
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Top App Bar
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(10.dp)
                                    .clip(CircleShape)
                                    .background(if (voiceState == VoiceState.LISTENING) Color(0xFF10B981) else RoyalGold)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "TAXMITRA LIVE VOICE",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = RoyalGold,
                                letterSpacing = 1.sp
                            )
                        }
                        Text(
                            text = "Model: $selectedLiveModel",
                            fontSize = 11.sp,
                            color = Color(0xFF94A3B8)
                        )
                    }

                    Row {
                        // Switch between gemini-3.1-flash-live-preview and gemini-3.8-flash
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF1E293B))
                                .clickable {
                                    selectedLiveModel = if (selectedLiveModel == GeminiModels.MODEL_LIVE_VOICE) {
                                        GeminiModels.MODEL_FLASH_38
                                    } else {
                                        GeminiModels.MODEL_LIVE_VOICE
                                    }
                                }
                                .padding(horizontal = 8.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = if (selectedLiveModel == GeminiModels.MODEL_LIVE_VOICE) "Switch: 3.8 Flash" else "Switch: 3.1 Live",
                                fontSize = 10.sp,
                                color = RoyalGoldLight,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.width(6.dp))

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close voice dialog",
                                tint = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Status Banner
                val statusText = when {
                    !hasMicPermission && voiceState == VoiceState.IDLE -> "Microphone permission needed — tap mic to grant access"
                    voiceState == VoiceState.IDLE -> "Tap microphone or a quick topic below to speak"
                    voiceState == VoiceState.LISTENING -> "Listening to your voice in real time..."
                    voiceState == VoiceState.THINKING -> "Consulting Live API (${selectedLiveModel})..."
                    voiceState == VoiceState.SPEAKING -> "Taxmitra is speaking..."
                    else -> "Ready to listen"
                }

                val statusColor = when {
                    !hasMicPermission && voiceState == VoiceState.IDLE -> RoyalGoldLight
                    voiceState == VoiceState.IDLE -> Color(0xFF94A3B8)
                    voiceState == VoiceState.LISTENING -> Color(0xFF34D399)
                    voiceState == VoiceState.THINKING -> RoyalGoldLight
                    voiceState == VoiceState.SPEAKING -> Color(0xFF60A5FA)
                    else -> Color(0xFF94A3B8)
                }

                Text(
                    text = statusText,
                    fontSize = 13.sp,
                    color = statusColor,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Pulsing Audio Visualizer Orb
                Box(
                    modifier = Modifier
                        .size(160.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer ripple ring
                    val rippleScale = pulseScale + (audioRms * 0.35f)
                    Box(
                        modifier = Modifier
                            .size(150.dp)
                            .scale(rippleScale)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        when (voiceState) {
                                            VoiceState.LISTENING -> Color(0x3310B981)
                                            VoiceState.SPEAKING -> Color(0x333B82F6)
                                            VoiceState.THINKING -> Color(0x33F59E0B)
                                            VoiceState.IDLE -> Color(0x1AD4AF37)
                                        },
                                        Color.Transparent
                                    )
                                )
                            )
                    )

                    // Middle ring
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .scale(pulseScale)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(
                                        when (voiceState) {
                                            VoiceState.LISTENING -> Color(0xFF10B981).copy(alpha = 0.3f)
                                            VoiceState.SPEAKING -> Color(0xFF3B82F6).copy(alpha = 0.3f)
                                            else -> RoyalGold.copy(alpha = 0.25f)
                                        },
                                        Color(0xFF0F172A)
                                    )
                                )
                            )
                            .border(
                                2.dp,
                                when (voiceState) {
                                    VoiceState.LISTENING -> Color(0xFF34D399)
                                    VoiceState.SPEAKING -> Color(0xFF60A5FA)
                                    VoiceState.THINKING -> RoyalGoldLight
                                    VoiceState.IDLE -> RoyalGold.copy(alpha = 0.6f)
                                },
                                CircleShape
                            )
                    )

                    // Core icon or spinner
                    if (voiceState == VoiceState.THINKING) {
                        CircularProgressIndicator(
                            color = RoyalGold,
                            strokeWidth = 3.dp,
                            modifier = Modifier.size(44.dp)
                        )
                    } else {
                        Icon(
                            imageVector = when (voiceState) {
                                VoiceState.SPEAKING -> Icons.AutoMirrored.Filled.VolumeUp
                                VoiceState.LISTENING -> Icons.Default.GraphicEq
                                else -> Icons.Default.Mic
                            },
                            contentDescription = null,
                            tint = when (voiceState) {
                                VoiceState.LISTENING -> Color(0xFF10B981)
                                VoiceState.SPEAKING -> Color(0xFF60A5FA)
                                else -> RoyalGold
                            },
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }

                // Live transcript preview
                if (liveTranscript.isNotBlank()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                            .background(Color(0xFF1E293B), RoundedCornerShape(12.dp))
                            .border(1.dp, Color(0xFF334155), RoundedCornerShape(12.dp))
                            .padding(12.dp)
                    ) {
                        Text(
                            text = "“$liveTranscript”",
                            fontSize = 13.sp,
                            color = Color(0xFFF1F5F9),
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Conversation Transcript Stream
                Text(
                    text = "Live Conversation Transcript",
                    fontSize = 11.sp,
                    color = Color(0xFF64748B),
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                )

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .background(Color(0xFF0F172A), RoundedCornerShape(16.dp))
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (voiceMessages.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "Ready to speak.\nTap the mic below or choose a suggested question.",
                                    color = Color(0xFF64748B),
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }

                    items(voiceMessages) { msg ->
                        val isUser = msg.sender == "USER"
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (isUser) "You (Voice)" else "Taxmitra AI",
                                    fontSize = 10.sp,
                                    color = if (isUser) Color(0xFF93C5FD) else RoyalGold,
                                    fontWeight = FontWeight.Bold
                                )
                                if (!isUser && msg.modelUsed != null) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = msg.modelUsed,
                                        fontSize = 9.sp,
                                        color = Color(0xFF64748B)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(3.dp))
                            Box(
                                modifier = Modifier
                                    .clip(
                                        RoundedCornerShape(
                                            topStart = 14.dp,
                                            topEnd = 14.dp,
                                            bottomStart = if (isUser) 14.dp else 2.dp,
                                            bottomEnd = if (isUser) 2.dp else 14.dp
                                        )
                                    )
                                    .background(
                                        if (isUser) Color(0xFF1E3A8A) else Color(0xFF1E293B)
                                    )
                                    .padding(horizontal = 12.dp, vertical = 8.dp)
                            ) {
                                Text(
                                    text = msg.message,
                                    color = Color.White,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Quick Spoken Topic Chips
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    val topics = listOf(
                        "Do I need GST for online sales?",
                        "Calculate tax for ₹12 Lakh salary",
                        "Explain Section 143(1) notice",
                        "LLP vs Private Limited Company"
                    )
                    topics.forEach { topic ->
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(14.dp))
                                .background(Color(0xFF1E293B))
                                .border(1.dp, Color(0xFF334155), RoundedCornerShape(14.dp))
                                .clickable {
                                    onSendPrompt(topic)
                                }
                                .padding(horizontal = 10.dp, vertical = 5.dp)
                        ) {
                            Text(
                                text = topic,
                                fontSize = 11.sp,
                                color = Color(0xFFCBD5E1),
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Bottom Call & Mic Controls
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Speaker Mute / Stop speaking
                    IconButton(
                        onClick = onStopSpeaking,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E293B))
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Stop speaking",
                            tint = Color.White
                        )
                    }

                    // Main Big Mic Button (Push-to-talk or Toggle)
                    Box(
                        modifier = Modifier
                            .size(68.dp)
                            .scale(if (voiceState == VoiceState.LISTENING) pulseScale else 1.0f)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    if (voiceState == VoiceState.LISTENING) {
                                        listOf(Color(0xFFEF4444), Color(0xFFDC2626))
                                    } else {
                                        listOf(RoyalGold, Color(0xFFAA8015))
                                    }
                                )
                            )
                            .clickable {
                                if (voiceState == VoiceState.LISTENING) {
                                    onStopListening()
                                } else {
                                    if (hasMicPermission) {
                                        onStartListening()
                                    } else {
                                        micPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
                                    }
                                }
                            }
                            .testTag("voice_live_mic_button"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = if (voiceState == VoiceState.LISTENING) Icons.Default.MicOff else Icons.Default.Mic,
                            contentDescription = "Toggle Microphone",
                            tint = Color.Black,
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    // End Conversation Button
                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFEF4444).copy(alpha = 0.2f))
                            .border(1.dp, Color(0xFFEF4444), CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CallEnd,
                            contentDescription = "End call",
                            tint = Color(0xFFEF4444)
                        )
                    }
                }
            }
        }
    }
}
