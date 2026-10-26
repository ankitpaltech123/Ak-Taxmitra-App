package com.example

import android.Manifest
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalance
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.data.model.UserEntityContext
import com.example.data.notification.TaxNotificationManager
import com.example.ui.components.CameraDocumentScannerDialog
import com.example.ui.components.GlobalContextBar
import com.example.ui.components.PriceBreakdown
import com.example.ui.components.PricingBreakdownDialog
import com.example.ui.components.TaxmitraTopBar
import com.example.ui.screens.AiAssistantDialog
import com.example.ui.screens.AiCommandCenterScreen
import com.example.ui.screens.BusinessOsScreen
import com.example.ui.screens.CaMarketplaceScreen
import com.example.ui.screens.ComplianceCalendarScreen
import com.example.ui.screens.DocumentsScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.MoreHubScreen
import com.example.ui.screens.TaxOsScreen
import com.example.ui.screens.VoiceConversationDialog
import com.example.ui.theme.AppColors
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.MidnightNavy
import com.example.ui.theme.RoyalGold
import com.example.ui.theme.TaxmitraTheme
import com.example.ui.viewmodel.TaxmitraViewModel

class MainActivity : ComponentActivity() {

    private val activityIntentState = mutableStateOf<Intent?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activityIntentState.value = intent
        enableEdgeToEdge()
        setContent {
            val viewModel: TaxmitraViewModel = viewModel()
            val isDarkMode by viewModel.isDarkMode.collectAsState()

            TaxmitraTheme(darkTheme = isDarkMode) {
                TaxmitraApp(
                    viewModel = viewModel,
                    incomingIntent = activityIntentState.value
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        activityIntentState.value = intent
    }
}

@Composable
fun TaxmitraApp(
    viewModel: TaxmitraViewModel = viewModel(),
    incomingIntent: Intent? = null
) {
    val context = LocalContext.current

    // Request Notification Permission on Android 13+ (API 33+)
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { _ -> }

    LaunchedEffect(Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (!TaxNotificationManager.areNotificationsEnabled(context)) {
                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }

    // Handle Deep Links from Notifications
    LaunchedEffect(incomingIntent) {
        incomingIntent?.let { intent ->
            val targetTab = intent.getStringExtra(TaxNotificationManager.EXTRA_TARGET_TAB)
            val aiPrompt = intent.getStringExtra(TaxNotificationManager.EXTRA_AI_PROMPT)

            if (!targetTab.isNullOrBlank()) {
                viewModel.navigateToTab(targetTab)
            }
            if (!aiPrompt.isNullOrBlank()) {
                viewModel.sendAiPrompt(aiPrompt)
                viewModel.toggleAiChat(true)
            }
        }
    }

    val currentRole by viewModel.currentRole.collectAsState()
    val currentTab by viewModel.currentTab.collectAsState()
    val isDarkMode by viewModel.isDarkMode.collectAsState()
    val isCaWorkspaceActive by viewModel.isCaWorkspaceActive.collectAsState()
    val showAiChatModal by viewModel.showAiChatModal.collectAsState()
    val showVoiceLiveModal by viewModel.showVoiceLiveModal.collectAsState()
    val showScannerModal by viewModel.showScannerModal.collectAsState()
    val voiceState by viewModel.voiceState.collectAsState()
    val audioRms by viewModel.audioRms.collectAsState()
    val liveVoiceTranscript by viewModel.liveVoiceTranscript.collectAsState()
    val selectedChatbotRole by viewModel.selectedChatbotRole.collectAsState()
    val selectedModelPreference by viewModel.selectedModelPreference.collectAsState()

    val primaryBusiness by viewModel.primaryBusiness.collectAsState()
    val latestTaxProfile by viewModel.latestTaxProfile.collectAsState()
    val allDocuments by viewModel.allDocuments.collectAsState()
    val scannedTaxDocuments by viewModel.scannedTaxDocuments.collectAsState()
    val allInvoices by viewModel.allInvoices.collectAsState()
    val complianceTasks by viewModel.complianceTasks.collectAsState()
    val caEngagements by viewModel.caEngagements.collectAsState()
    val taxNotices by viewModel.taxNotices.collectAsState()
    val nextBestAction by viewModel.nextBestAction.collectAsState()

    val scheduledReminderTaskIds by viewModel.scheduledReminderTaskIds.collectAsState()
    val notificationFeedbackMessage by viewModel.notificationFeedbackMessage.collectAsState()

    val chatMessages by viewModel.chatMessages.collectAsState()
    val isAiThinking by viewModel.isAiThinking.collectAsState()

    val isLoggedIn by viewModel.isLoggedIn.collectAsState()

    // Entity Context Management
    var currentEntity by remember { mutableStateOf(UserEntityContext.DEFAULT_ENTITIES.first()) }
    var showPricingDialog by remember { mutableStateOf(false) }

    // If user is not logged in, present LoginScreen displaying the complete brand logo
    if (!isLoggedIn) {
        LoginScreen(
            onLoginSuccess = { identifier, role ->
                viewModel.login(identifier, role)
            },
            onSkipToApp = {
                viewModel.login("Guest Taxpayer", currentRole)
            },
            isDarkMode = isDarkMode,
            onToggleTheme = { viewModel.toggleDarkMode() }
        )
        return
    }

    // System Back Press Navigation: Pop open modals or return to Home screen
    BackHandler(
        enabled = currentTab != "HOME" || showAiChatModal || showVoiceLiveModal ||
                showScannerModal || showPricingDialog
    ) {
        when {
            showPricingDialog -> showPricingDialog = false
            showScannerModal -> viewModel.toggleScannerModal(false)
            showVoiceLiveModal -> viewModel.toggleVoiceLive(false)
            showAiChatModal -> viewModel.toggleAiChat(false)
            currentTab != "HOME" -> viewModel.navigateToTab("HOME")
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            Column {
                TaxmitraTopBar(
                    currentRole = currentRole,
                    onRoleSelected = { viewModel.selectRole(it) },
                    onOpenAiChat = { viewModel.toggleAiChat(true) },
                    onOpenVoiceLive = { viewModel.toggleVoiceLive(true) },
                    isDarkMode = isDarkMode,
                    onToggleTheme = { viewModel.toggleDarkMode() }
                )
                GlobalContextBar(
                    currentEntity = currentEntity,
                    allEntities = UserEntityContext.DEFAULT_ENTITIES,
                    onSelectEntity = { currentEntity = it }
                )
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = AppColors.bottomBarBackground,
                contentColor = AppColors.goldAccent
            ) {
                // Section 15 Mobile Navigation: HOME, AI, TAX, BUSINESS, MORE
                val navItems = listOf(
                    Triple("HOME", "Home", Icons.Default.Home),
                    Triple("AI", "AI", Icons.Default.AutoAwesome),
                    Triple("TAX", "Tax OS", Icons.Default.AccountBalance),
                    Triple("BUSINESS", "Business", Icons.Default.Business),
                    Triple("MORE", "More", Icons.Default.MoreHoriz)
                )

                navItems.forEach { (tabKey, label, icon) ->
                    val isSelected = currentTab == tabKey ||
                        (tabKey == "MORE" && (currentTab == "DOCUMENTS" || currentTab == "CA" || currentTab == "COMPLIANCE"))
                    NavigationBarItem(
                        selected = isSelected,
                        onClick = { viewModel.navigateToTab(tabKey) },
                        icon = {
                            Icon(
                                imageVector = icon,
                                contentDescription = label,
                                modifier = Modifier.size(20.dp)
                            )
                        },
                        label = {
                            Text(
                                text = label,
                                fontSize = 10.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = Color.White,
                            selectedTextColor = AppColors.goldAccent,
                            indicatorColor = AppColors.goldAccent,
                            unselectedIconColor = AppColors.navItemUnselected,
                            unselectedTextColor = AppColors.navItemUnselected
                        )
                    )
                }
            }
        },
        floatingActionButton = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                // Voice Live quick trigger FAB
                FloatingActionButton(
                    onClick = { viewModel.toggleVoiceLive(true) },
                    containerColor = if (AppColors.isDark) Color(0xFF1E293B) else Color(0xFFECFDF5),
                    contentColor = Color(0xFF10B981),
                    shape = CircleShape,
                    modifier = Modifier
                        .size(46.dp)
                        .border(1.5.dp, Color(0xFF10B981), CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.GraphicEq,
                        contentDescription = "Live Voice Conversation",
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // AI Chatbot FAB
                FloatingActionButton(
                    onClick = {
                        viewModel.navigateToTab("AI")
                    },
                    containerColor = AppColors.goldAccent,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(54.dp)
                        .border(2.dp, AppColors.cardBorder, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Ask Taxmitra AI",
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(AppColors.background)
        ) {
            when (currentTab) {
                "HOME" -> HomeScreen(
                    currentRole = currentRole,
                    business = primaryBusiness,
                    taxProfile = latestTaxProfile,
                    nextAction = nextBestAction,
                    onNavigateTab = { viewModel.navigateToTab(it) },
                    onOpenAiChatWithPrompt = { prompt ->
                        viewModel.sendAiPrompt(prompt)
                        viewModel.navigateToTab("AI")
                    },
                    onOpenReceiptScanner = { viewModel.toggleScannerModal(true) },
                    onOpenVoiceLive = { viewModel.toggleVoiceLive(true) },
                    onRemindAction = { action -> viewModel.scheduleAiActionReminder(action, 10) }
                )
                "AI" -> AiCommandCenterScreen(
                    currentEntity = currentEntity,
                    messages = chatMessages,
                    isThinking = isAiThinking,
                    selectedRole = selectedChatbotRole,
                    onSelectRole = { viewModel.setChatbotRole(it) },
                    onSendMessage = { prompt ->
                        viewModel.sendAiPrompt(prompt)
                    },
                    onOpenVoiceLive = { viewModel.toggleVoiceLive(true) },
                    onNavigateWorkflow = { tab, workflowKey ->
                        viewModel.navigateToTab(tab)
                    }
                )
                "TAX" -> TaxOsScreen(
                    taxProfile = latestTaxProfile,
                    notices = taxNotices,
                    onRecomputeTax = { salary, business, rental, cap, other, d80c, d80d, dnps, dOth, tds, adv ->
                        viewModel.recomputeTax(salary, business, rental, cap, other, d80c, d80d, dnps, dOth, tds, adv)
                    },
                    onSwitchRegime = { viewModel.switchTaxRegime(it) },
                    onSubmitItr = { withCa -> viewModel.submitItr(withCa) },
                    onUploadNotice = { section, summary, deadline, risk, plan ->
                        viewModel.uploadNotice(section, summary, deadline, risk, plan)
                    },
                    onAskAi = { prompt ->
                        viewModel.sendAiPrompt(prompt)
                        viewModel.navigateToTab("AI")
                    },
                    onFindCa = { viewModel.navigateToTab("CA") }
                )
                "BUSINESS" -> BusinessOsScreen(
                    business = primaryBusiness,
                    invoices = allInvoices,
                    onSaveBusiness = { name, industry, founders, seeksFunding, wantsLtd, turnover, isOnline ->
                        viewModel.createOrUpdateBusiness(name, industry, founders, seeksFunding, wantsLtd, turnover, isOnline)
                    },
                    onCompleteUdyam = { viewModel.completeUdyamRegistration() },
                    onUpdateGstStatus = { viewModel.updateGstStatus(it) },
                    onCreateInvoice = { name, gst, items, subtotal, rate ->
                        viewModel.createInvoice(name, gst, items, subtotal, rate)
                    },
                    onAskAi = { prompt ->
                        viewModel.sendAiPrompt(prompt)
                        viewModel.navigateToTab("AI")
                    }
                )
                "MORE" -> MoreHubScreen(
                    currentEntity = currentEntity,
                    onNavigateDestination = { destination ->
                        when (destination) {
                            "GST" -> viewModel.navigateToTab("TAX")
                            "ACCOUNTING" -> viewModel.navigateToTab("BUSINESS")
                            "DOCUMENTS" -> viewModel.navigateToTab("DOCUMENTS")
                            "COMPLIANCE" -> viewModel.navigateToTab("COMPLIANCE")
                            "CA" -> viewModel.navigateToTab("CA")
                            "PRICING" -> { showPricingDialog = true }
                            "SECURITY" -> {
                                viewModel.sendAiPrompt("Explain AK Taxmitra AI security standards, data encryption, and privacy compliance under Indian laws.")
                                viewModel.navigateToTab("AI")
                            }
                            else -> viewModel.navigateToTab(destination)
                        }
                    },
                    onOpenAiWithPrompt = { prompt ->
                        viewModel.sendAiPrompt(prompt)
                        viewModel.navigateToTab("AI")
                    },
                    isDarkMode = isDarkMode,
                    onToggleTheme = { viewModel.toggleDarkMode() },
                    onLogout = { viewModel.logout() }
                )
                "DOCUMENTS" -> DocumentsScreen(
                    documents = allDocuments,
                    scannedTaxDocuments = scannedTaxDocuments,
                    onOpenScanner = { viewModel.toggleScannerModal(true) },
                    onUploadSampleDocument = { title, cat, type, summary ->
                        viewModel.uploadDocument(title, cat, type, summary)
                    },
                    onDeleteDocument = { viewModel.deleteDocument(it) },
                    onDeleteScannedDocument = { viewModel.deleteScannedTaxDocument(it) },
                    onSaveNewScannedDocument = { title, docType, cat, summary, vendor, inv, amount, sec, benefit ->
                        viewModel.saveScannedTaxDocument(title, docType, cat, summary, vendor, inv, amount, sec, benefit)
                    },
                    onAskAiAboutDocument = { prompt ->
                        viewModel.sendAiPrompt(prompt)
                        viewModel.navigateToTab("AI")
                    }
                )
                "CA" -> CaMarketplaceScreen(
                    cas = viewModel.verifiedCas,
                    engagements = caEngagements,
                    isCaWorkspaceActive = isCaWorkspaceActive,
                    onToggleCaWorkspace = { viewModel.toggleCaWorkspace(it) },
                    onRequestEngagement = { caId, caName, service, mode, notes ->
                        viewModel.requestCaEngagement(caId, caName, service, mode, notes)
                    },
                    onAskAi = { prompt ->
                        viewModel.sendAiPrompt(prompt)
                        viewModel.navigateToTab("AI")
                    }
                )
                "COMPLIANCE" -> ComplianceCalendarScreen(
                    tasks = complianceTasks,
                    scheduledReminderTaskIds = scheduledReminderTaskIds,
                    onMarkCompleted = { viewModel.markComplianceTaskCompleted(it) },
                    onToggleReminder = { task, enable -> viewModel.toggleTaskReminder(task, enable) },
                    onScheduleAllReminders = { viewModel.scheduleAllUpcomingDeadlineReminders() },
                    onTestNotification = { task -> viewModel.triggerImmediateDeadlineReminder(task) },
                    onAskAi = { prompt ->
                        viewModel.sendAiPrompt(prompt)
                        viewModel.navigateToTab("AI")
                    }
                )
            }

            // Notification Feedback Banner
            notificationFeedbackMessage?.let { feedbackText ->
                Box(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(12.dp)
                        .background(Color(0xFF0F172A), RoundedCornerShape(12.dp))
                        .border(1.dp, RoyalGold, RoundedCornerShape(12.dp))
                        .padding(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(
                            imageVector = Icons.Default.NotificationsActive,
                            contentDescription = null,
                            tint = Color(0xFF10B981),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = feedbackText,
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Dismiss",
                            tint = Color.Gray,
                            modifier = Modifier
                                .size(16.dp)
                                .clickable { viewModel.dismissNotificationFeedback() }
                        )
                    }
                }
            }
        }
    }

    // Modal Sheet 1: Conversational AI Overlay (when toggled via top bar or deep link)
    if (showAiChatModal) {
        AiAssistantDialog(
            messages = chatMessages,
            isThinking = isAiThinking,
            selectedRole = selectedChatbotRole,
            selectedModelPreference = selectedModelPreference,
            onSelectRole = { viewModel.setChatbotRole(it) },
            onSelectModelPreference = { viewModel.setModelPreference(it) },
            onOpenVoiceLive = { viewModel.toggleVoiceLive(true) },
            onClearHistory = { viewModel.clearChatMessages() },
            onSendMessage = { viewModel.sendAiPrompt(it) },
            onNavigateTab = { viewModel.navigateToTab(it) },
            onDismiss = { viewModel.toggleAiChat(false) }
        )
    }

    // Modal Sheet 2: Gemini 3.1 Flash Live Voice Assistant
    if (showVoiceLiveModal) {
        VoiceConversationDialog(
            voiceState = voiceState,
            audioRms = audioRms,
            liveTranscript = liveVoiceTranscript,
            messages = chatMessages,
            onStartListening = { viewModel.startVoiceListening() },
            onStopListening = { viewModel.stopVoiceListening() },
            onStopSpeaking = { viewModel.stopVoiceSpeaking() },
            onSendPrompt = { viewModel.sendVoicePrompt(it) },
            onDismiss = {
                viewModel.stopVoiceListening()
                viewModel.stopVoiceSpeaking()
                viewModel.toggleVoiceLive(false)
            }
        )
    }

    // Modal Sheet 3: Transparent Pricing Dialog
    if (showPricingDialog) {
        PricingBreakdownDialog(
            breakdown = PriceBreakdown(
                serviceName = "Comprehensive Tax & Business Operating System",
                modeName = "Transparent Unbundled Pricing",
                govtFee = 0.0,
                caFee = 0.0,
                platformFee = 499.0,
                taxes = 89.82,
                totalAmount = 588.82,
                isZeroGovtFeeNotice = true
            ),
            onDismiss = { showPricingDialog = false },
            onConfirm = { showPricingDialog = false }
        )
    }

    // Modal Sheet 4: CameraX AI Document & Receipt Scanner
    if (showScannerModal) {
        CameraDocumentScannerDialog(
            onDismiss = { viewModel.toggleScannerModal(false) },
            onSaveToVault = { title, category, docType, fileUri, confidence, extractedSummary ->
                viewModel.saveScannedReceipt(title, category, docType, fileUri, confidence, extractedSummary)
            },
            onAskAiAboutReceipt = { prompt ->
                viewModel.sendAiPrompt(prompt)
                viewModel.navigateToTab("AI")
            }
        )
    }
}
