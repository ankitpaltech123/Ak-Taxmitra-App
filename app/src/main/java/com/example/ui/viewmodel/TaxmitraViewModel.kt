package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ai.AiModelPreference
import com.example.data.ai.ChatbotRole
import com.example.data.ai.GeminiApiClient
import com.example.data.ai.GeminiModels
import com.example.data.ai.TaxmitraAiEngine
import com.example.data.ai.VoiceAssistantManager
import com.example.data.ai.VoiceState
import com.example.data.engine.BusinessStructureEngine
import com.example.data.engine.NextBestAction
import com.example.data.engine.NextBestActionEngine
import com.example.data.engine.TaxCalculationEngine
import com.example.data.local.AppDatabase
import com.example.data.model.BusinessEntity
import com.example.data.model.CaEngagementEntity
import com.example.data.model.CaProfile
import com.example.data.model.ChatMessage
import com.example.data.model.ComplianceTaskEntity
import com.example.data.model.DocumentEntity
import com.example.data.model.InvoiceEntity
import com.example.data.model.ScannedTaxDocumentEntity
import com.example.data.model.TaxNoticeEntity
import com.example.data.model.TaxProfileEntity
import com.example.data.model.UserRole
import com.example.data.notification.TaxNotificationManager
import com.example.data.repository.TaxmitraRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.UUID

class TaxmitraViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TaxmitraRepository

    init {
        TaxNotificationManager.createNotificationChannels(application)
        val db = AppDatabase.getDatabase(application)
        repository = TaxmitraRepository(db.taxmitraDao(), db.scannedTaxDocumentDao())
        viewModelScope.launch {
            repository.seedInitialDataIfNeeded()
        }
    }

    // Notification State Tracking
    private val _scheduledReminderTaskIds = MutableStateFlow<Set<Long>>(emptySet())
    val scheduledReminderTaskIds: StateFlow<Set<Long>> = _scheduledReminderTaskIds.asStateFlow()

    private val _notificationFeedbackMessage = MutableStateFlow<String?>(null)
    val notificationFeedbackMessage: StateFlow<String?> = _notificationFeedbackMessage.asStateFlow()

    // Role & Navigation State
    private val _currentRole = MutableStateFlow(UserRole.BEGINNER)
    val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

    private val _currentTab = MutableStateFlow("HOME") // HOME, BUSINESS, TAX, DOCUMENTS, CA, COMPLIANCE
    val currentTab: StateFlow<String> = _currentTab.asStateFlow()

    private val _isCaWorkspaceActive = MutableStateFlow(false)
    val isCaWorkspaceActive: StateFlow<Boolean> = _isCaWorkspaceActive.asStateFlow()

    // Data from Room
    val primaryBusiness: StateFlow<BusinessEntity?> = repository.primaryBusiness
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val latestTaxProfile: StateFlow<TaxProfileEntity?> = repository.latestTaxProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allDocuments: StateFlow<List<DocumentEntity>> = repository.allDocuments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allInvoices: StateFlow<List<InvoiceEntity>> = repository.allInvoices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val complianceTasks: StateFlow<List<ComplianceTaskEntity>> = repository.complianceTasks
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val caEngagements: StateFlow<List<CaEngagementEntity>> = repository.caEngagements
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val taxNotices: StateFlow<List<TaxNoticeEntity>> = repository.taxNotices
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val scannedTaxDocuments: StateFlow<List<ScannedTaxDocumentEntity>> = repository.allScannedDocuments
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val verifiedCas: List<CaProfile> = repository.verifiedCas

    // Voice Assistant Manager for Live Audio Conversations
    val voiceAssistant = VoiceAssistantManager(application)
    val voiceState: StateFlow<VoiceState> = voiceAssistant.voiceState
    val audioRms: StateFlow<Float> = voiceAssistant.audioRms
    val liveVoiceTranscript: StateFlow<String> = voiceAssistant.liveTranscript

    // Chatbot Role & Gemini Model Preference
    private val _selectedRole = MutableStateFlow(ChatbotRole.TAX_ADVISOR)
    val selectedChatbotRole: StateFlow<ChatbotRole> = _selectedRole.asStateFlow()

    private val _selectedModelPreference = MutableStateFlow(AiModelPreference.AUTO)
    val selectedModelPreference: StateFlow<AiModelPreference> = _selectedModelPreference.asStateFlow()

    private val _showVoiceLiveModal = MutableStateFlow(false)
    val showVoiceLiveModal: StateFlow<Boolean> = _showVoiceLiveModal.asStateFlow()

    // Dynamic Next Best Action
    val nextBestAction: StateFlow<NextBestAction> = combine(
        primaryBusiness,
        latestTaxProfile,
        complianceTasks,
        taxNotices
    ) { business, tax, tasks, notices ->
        NextBestActionEngine.compute(business, tax, tasks, notices)
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        NextBestAction(
            title = "Welcome to AK Taxmitra AI",
            description = "Start your business or calculate your tax in one tap.",
            urgencyLevel = "RECOMMENDED",
            actionButtonText = "Get Started",
            targetTab = "HOME"
        )
    )

    // Conversational AI State
    private val _chatMessages = MutableStateFlow<List<ChatMessage>>(
        listOf(
            ChatMessage(
                id = "msg_init",
                sender = "TAXMITRA",
                message = "Namaste! I am AK Taxmitra AI.\n\nTell me what you want to do: start a business, file your tax, check GST requirement, or find a verified CA?",
                quickActions = listOf("Start My Business", "File My Tax", "Do I need GST?", "Explain a Notice", "Find a CA")
            )
        )
    )
    val chatMessages: StateFlow<List<ChatMessage>> = _chatMessages.asStateFlow()

    private val _isAiThinking = MutableStateFlow(false)
    val isAiThinking: StateFlow<Boolean> = _isAiThinking.asStateFlow()

    // UI Dialog & Sheet states
    private val _showAiChatModal = MutableStateFlow(false)
    val showAiChatModal: StateFlow<Boolean> = _showAiChatModal.asStateFlow()

    private val _showScannerModal = MutableStateFlow(false)
    val showScannerModal: StateFlow<Boolean> = _showScannerModal.asStateFlow()

    // Appearance & Theme State (Default: Normal Mode / Light Mode as requested by user)
    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    // Authentication / Login State
    // User requested whole image with logo on login page and logo inside app
    private val _isLoggedIn = MutableStateFlow(false)
    val isLoggedIn: StateFlow<Boolean> = _isLoggedIn.asStateFlow()

    private val _userIdentifier = MutableStateFlow("+91 98765 43210")
    val userIdentifier: StateFlow<String> = _userIdentifier.asStateFlow()

    fun login(identifier: String, role: UserRole? = null) {
        _userIdentifier.value = identifier.ifBlank { "+91 98765 43210" }
        if (role != null) {
            _currentRole.value = role
        }
        _isLoggedIn.value = true
        _notificationFeedbackMessage.value = "Welcome to AK Taxmitra AI! Access granted."
    }

    fun logout() {
        _isLoggedIn.value = false
        _notificationFeedbackMessage.value = "Logged out. You are now on the login screen."
    }

    fun toggleDarkMode() {
        _isDarkMode.value = !_isDarkMode.value
        _notificationFeedbackMessage.value = if (_isDarkMode.value) "Dark Mode activated" else "Normal Mode (Light) activated"
    }

    fun setDarkMode(dark: Boolean) {
        _isDarkMode.value = dark
    }

    fun toggleScannerModal(show: Boolean) {
        _showScannerModal.value = show
    }

    // ----------------------------------------------------
    // User Role & Tab Controls
    // ----------------------------------------------------
    fun selectRole(role: UserRole) {
        _currentRole.value = role
    }

    fun navigateToTab(tab: String) {
        _currentTab.value = tab
    }

    fun toggleCaWorkspace(active: Boolean) {
        _isCaWorkspaceActive.value = active
    }

    fun toggleAiChat(show: Boolean) {
        _showAiChatModal.value = show
    }

    fun toggleVoiceLive(show: Boolean) {
        _showVoiceLiveModal.value = show
        if (show) {
            voiceAssistant.onSpeechRecognized = { spokenText ->
                sendVoicePrompt(spokenText)
            }
        } else {
            voiceAssistant.stopListening()
            voiceAssistant.stopSpeaking()
        }
    }

    fun setChatbotRole(role: ChatbotRole) {
        _selectedRole.value = role
    }

    fun setModelPreference(preference: AiModelPreference) {
        _selectedModelPreference.value = preference
    }

    fun clearChatMessages() {
        _chatMessages.value = listOf(
            ChatMessage(
                id = "msg_init_${System.currentTimeMillis()}",
                sender = "TAXMITRA",
                message = "Conversation refreshed. I am ready with the ${_selectedRole.value.title} role.\n\nHow can I help you today?",
                quickActions = listOf("Calculate Tax", "Start a Business", "Check GST Requirement", "Notice Help")
            )
        )
    }

    // ----------------------------------------------------
    // Conversational AI Actions with Gemini API & Multi-Turn
    // ----------------------------------------------------
    fun sendAiPrompt(prompt: String) {
        if (prompt.isBlank()) return

        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            sender = "USER",
            message = prompt.trim()
        )
        _chatMessages.value = _chatMessages.value + userMsg
        _isAiThinking.value = true

        viewModelScope.launch {
            val contextInfo = "Role: ${_currentRole.value.displayName} | Business: ${primaryBusiness.value?.name ?: "None"} | FY 2024-25"
            val aiResult = GeminiApiClient.sendMultiTurnChat(
                conversation = _chatMessages.value.dropLast(1), // prior history
                newPrompt = prompt,
                role = _selectedRole.value,
                modelPreference = _selectedModelPreference.value,
                userContext = contextInfo
            )

            val aiMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                sender = "TAXMITRA",
                message = aiResult.replyText,
                quickActions = listOf("View Business OS", "Calculate Tax", "Find a CA", "Check Compliance"),
                officialFlowTag = "GEMINI • ${aiResult.modelUsed.uppercase()}",
                modelUsed = aiResult.modelUsed,
                roleName = _selectedRole.value.title
            )
            _chatMessages.value = _chatMessages.value + aiMsg
            _isAiThinking.value = false
        }
    }

    fun sendVoicePrompt(spokenText: String) {
        if (spokenText.isBlank()) return

        val userMsg = ChatMessage(
            id = UUID.randomUUID().toString(),
            sender = "USER",
            message = spokenText.trim(),
            isVoiceMessage = true
        )
        _chatMessages.value = _chatMessages.value + userMsg
        _isAiThinking.value = true

        viewModelScope.launch {
            val voiceResult = GeminiApiClient.queryLiveVoiceApi(
                spokenPrompt = spokenText,
                conversationHistory = _chatMessages.value.filter { it.isVoiceMessage }
            )

            val aiMsg = ChatMessage(
                id = UUID.randomUUID().toString(),
                sender = "TAXMITRA",
                message = voiceResult.replyText,
                officialFlowTag = "GEMINI LIVE • ${voiceResult.modelUsed.uppercase()}",
                modelUsed = voiceResult.modelUsed,
                roleName = "Live Voice Companion",
                isVoiceMessage = true
            )
            _chatMessages.value = _chatMessages.value + aiMsg
            _isAiThinking.value = false

            // Speak out response in real-time
            voiceAssistant.speak(voiceResult.replyText)
        }
    }

    fun startVoiceListening() {
        voiceAssistant.startListening()
    }

    fun stopVoiceListening() {
        voiceAssistant.stopListening()
    }

    fun stopVoiceSpeaking() {
        voiceAssistant.stopSpeaking()
    }

    override fun onCleared() {
        super.onCleared()
        voiceAssistant.release()
    }

    // ----------------------------------------------------
    // Business Discovery & Setup
    // ----------------------------------------------------
    fun createOrUpdateBusiness(
        name: String,
        industry: String,
        founders: Int,
        seeksFunding: Boolean,
        wantsLimitedLiability: Boolean,
        turnoverRange: String,
        isOnline: Boolean,
        state: String = "Delhi",
        city: String = "New Delhi"
    ) {
        viewModelScope.launch {
            val recommendation = BusinessStructureEngine.evaluate(
                founderCount = founders,
                seeksInvestment = seeksFunding,
                wantsLimitedLiability = wantsLimitedLiability,
                turnoverRange = turnoverRange,
                isOnline = isOnline
            )

            val gstRequired = isOnline || turnoverRange != "Under 20L"
            val newBusiness = BusinessEntity(
                id = primaryBusiness.value?.id ?: 0L,
                name = name.ifBlank { "My Indian Venture" },
                entityType = recommendation.recommendedEntity,
                industry = industry,
                founderCount = founders,
                turnoverRange = turnoverRange,
                state = state,
                city = city,
                isOnline = isOnline,
                gstRequired = gstRequired,
                readinessScore = 55
            )
            repository.saveBusiness(newBusiness)
        }
    }

    fun completeUdyamRegistration() {
        val current = primaryBusiness.value ?: return
        viewModelScope.launch {
            repository.saveBusiness(
                current.copy(
                    udyamStatus = "REGISTERED",
                    readinessScore = minOf(100, current.readinessScore + 20)
                )
            )
        }
    }

    fun updateGstStatus(newStatus: String) {
        val current = primaryBusiness.value ?: return
        viewModelScope.launch {
            repository.saveBusiness(
                current.copy(
                    gstStatus = newStatus,
                    readinessScore = minOf(100, current.readinessScore + 15)
                )
            )
        }
    }

    // ----------------------------------------------------
    // Deterministic Tax Calculations & Regime Selection
    // ----------------------------------------------------
    fun recomputeTax(
        salary: Double,
        business: Double,
        rental: Double,
        capitalGains: Double,
        other: Double,
        deduction80C: Double,
        deduction80D: Double,
        deductionNps: Double,
        otherDeductions: Double,
        tdsPaid: Double,
        advanceTax: Double
    ) {
        viewModelScope.launch {
            val result = TaxCalculationEngine.calculate(
                salaryIncome = salary,
                businessIncome = business,
                rentalIncome = rental,
                capitalGains = capitalGains,
                otherIncome = other,
                deduction80C = deduction80C,
                deduction80D = deduction80D,
                deductionNps = deductionNps,
                otherDeductions = otherDeductions,
                tdsPaid = tdsPaid,
                advanceTaxPaid = advanceTax
            )

            val currentProfile = latestTaxProfile.value
            val updated = (currentProfile ?: TaxProfileEntity()).copy(
                salaryIncome = salary,
                businessIncome = business,
                rentalIncome = rental,
                capitalGains = capitalGains,
                otherIncome = other,
                deduction80C = deduction80C,
                deduction80D = deduction80D,
                deductionNps = deductionNps,
                otherDeductions = otherDeductions,
                tdsPaid = tdsPaid,
                advanceTaxPaid = advanceTax,
                taxOldRegime = result.totalTaxOld,
                taxNewRegime = result.totalTaxNew,
                recommendedRegime = result.recommendedRegime,
                selectedRegime = result.recommendedRegime,
                taxSavings = result.taxDifference,
                filingRoute = result.recommendedItrForm,
                filingRouteReason = result.itrReason,
                status = "READY_TO_FILE",
                refundAmount = if (result.isRefund) result.netPayableOrRefund else 0.0,
                taxPayable = if (!result.isRefund) result.netPayableOrRefund else 0.0,
                lastUpdated = System.currentTimeMillis()
            )
            repository.saveTaxProfile(updated)
        }
    }

    fun switchTaxRegime(regime: String) {
        val current = latestTaxProfile.value ?: return
        viewModelScope.launch {
            val taxAmount = if (regime == "NEW") current.taxNewRegime else current.taxOldRegime
            val totalPaid = current.tdsPaid + current.advanceTaxPaid
            val diff = taxAmount - totalPaid
            repository.saveTaxProfile(
                current.copy(
                    selectedRegime = regime,
                    refundAmount = if (diff < 0) -diff else 0.0,
                    taxPayable = if (diff > 0) diff else 0.0
                )
            )
        }
    }

    fun submitItr(withCaReview: Boolean) {
        val current = latestTaxProfile.value ?: return
        viewModelScope.launch {
            val newStatus = if (withCaReview) "CA_REVIEW" else "SUBMITTED"
            val ackNo = "ITR" + (100000000000L..999999999999L).random().toString()
            repository.saveTaxProfile(
                current.copy(
                    status = newStatus,
                    acknowledgementNumber = ackNo
                )
            )
        }
    }

    // ----------------------------------------------------
    // Documents & Simulated OCR Intelligence
    // ----------------------------------------------------
    fun uploadDocument(
        title: String,
        category: String,
        docType: String,
        extractedSummary: String
    ) {
        viewModelScope.launch {
            val doc = DocumentEntity(
                title = title,
                category = category,
                docType = docType,
                fileSizeFormatted = "1.8 MB",
                ocrConfidence = 97,
                extractedFieldsJson = extractedSummary,
                status = "VERIFIED"
            )
            repository.saveDocument(doc)
        }
    }

    fun saveScannedReceipt(
        title: String,
        category: String,
        docType: String,
        fileUri: String,
        confidence: Int,
        extractedSummary: String
    ) {
        viewModelScope.launch {
            val doc = DocumentEntity(
                title = title,
                category = category,
                docType = docType,
                fileUri = fileUri,
                fileSizeFormatted = "2.1 MB",
                ocrConfidence = confidence,
                extractedFieldsJson = extractedSummary,
                status = "VERIFIED",
                uploadDate = System.currentTimeMillis()
            )
            repository.saveDocument(doc)

            // Also persist into Room scanned_tax_documents table for document history tracking
            val scannedDoc = ScannedTaxDocumentEntity(
                title = title,
                documentType = docType,
                category = category,
                scanDateTimestamp = System.currentTimeMillis(),
                aiSummary = extractedSummary,
                vendorOrIssuer = title.substringBefore("(").trim(),
                confidenceScore = confidence,
                imageUri = fileUri,
                status = "VERIFIED"
            )
            repository.saveScannedTaxDocument(scannedDoc)

            _notificationFeedbackMessage.value = "Receipt scanned & verified: $title"
        }
    }

    fun saveScannedTaxDocument(
        title: String,
        documentType: String,
        category: String,
        aiSummary: String,
        vendorOrIssuer: String = "",
        invoiceNumber: String = "",
        amount: Double = 0.0,
        taxSection: String = "",
        taxBenefit: String = "",
        confidence: Int = 96
    ) {
        viewModelScope.launch {
            val scanned = ScannedTaxDocumentEntity(
                title = title,
                documentType = documentType,
                category = category,
                scanDateTimestamp = System.currentTimeMillis(),
                aiSummary = aiSummary,
                vendorOrIssuer = vendorOrIssuer,
                invoiceOrRefNumber = invoiceNumber,
                detectedAmount = amount,
                eligibleTaxSection = taxSection,
                taxBenefitDescription = taxBenefit,
                confidenceScore = confidence,
                status = "VERIFIED"
            )
            repository.saveScannedTaxDocument(scanned)
            _notificationFeedbackMessage.value = "Tax document saved to local Room database: $title"
        }
    }

    fun deleteScannedTaxDocument(id: Long) {
        viewModelScope.launch {
            repository.deleteScannedTaxDocument(id)
            _notificationFeedbackMessage.value = "Scanned document deleted from local history."
        }
    }

    fun deleteDocument(id: Long) {
        viewModelScope.launch {
            repository.deleteDocument(id)
        }
    }

    // ----------------------------------------------------
    // Invoicing Engine
    // ----------------------------------------------------
    fun createInvoice(
        clientName: String,
        clientGst: String,
        itemsSummary: String,
        subtotal: Double,
        gstRate: Double
    ) {
        viewModelScope.launch {
            val gstAmount = subtotal * (gstRate / 100.0)
            val total = subtotal + gstAmount
            val invNum = "INV-${(100..999).random()}"
            val inv = InvoiceEntity(
                invoiceNumber = invNum,
                clientName = clientName,
                clientGst = clientGst,
                itemsSummary = itemsSummary,
                subtotal = subtotal,
                gstRatePercent = gstRate,
                gstAmount = gstAmount,
                totalAmount = total,
                issueDate = "Today",
                dueDate = "15 Days",
                status = "SENT"
            )
            repository.saveInvoice(inv)
        }
    }

    // ----------------------------------------------------
    // Compliance Tasks
    // ----------------------------------------------------
    fun markComplianceTaskCompleted(task: ComplianceTaskEntity) {
        viewModelScope.launch {
            repository.updateComplianceTask(task.copy(status = "COMPLETED"))
        }
    }

    // ----------------------------------------------------
    // CA Marketplace & Engagement
    // ----------------------------------------------------
    fun requestCaEngagement(
        caId: String,
        caName: String,
        serviceType: String,
        userMode: String,
        clientNotes: String
    ) {
        viewModelScope.launch {
            val (caFee, govtFee) = when (serviceType) {
                "ITR Review" -> Pair(1499.0, 0.0)
                "GST Filing" -> Pair(999.0, 0.0)
                "Company Registration" -> Pair(4999.0, 2000.0)
                "Notice Resolution" -> Pair(2499.0, 0.0)
                else -> Pair(1499.0, 0.0)
            }
            val platformFee = 199.0
            val taxes = (caFee + platformFee) * 0.18
            val total = caFee + govtFee + platformFee + taxes

            val engagement = CaEngagementEntity(
                caId = caId,
                caName = caName,
                serviceType = serviceType,
                userMode = userMode,
                govtFee = govtFee,
                caFee = caFee,
                platformFee = platformFee,
                taxes = taxes,
                totalFee = total,
                clientNotes = clientNotes,
                status = "ASSIGNED"
            )
            repository.createCaEngagement(engagement)
        }
    }

    // ----------------------------------------------------
    // Tax Notice Engine
    // ----------------------------------------------------
    fun uploadNotice(
        section: String,
        summary: String,
        deadline: String,
        risk: String,
        actionPlan: String
    ) {
        viewModelScope.launch {
            val notice = TaxNoticeEntity(
                noticeNumber = "IT-NOT-${(10000..99999).random()}",
                section = section,
                authority = "Centralized Processing Center (CPC), Bengaluru",
                dateReceived = "Recent",
                responseDeadline = deadline,
                summary = summary,
                riskLevel = risk,
                actionPlan = actionPlan,
                isResolved = false,
                caAssistanceRequested = false
            )
            repository.saveTaxNotice(notice)
        }
    }

    fun markNoticeResolved(notice: TaxNoticeEntity) {
        viewModelScope.launch {
            repository.saveTaxNotice(notice.copy(isResolved = true))
        }
    }

    // ----------------------------------------------------
    // Local Notification & Reminder Support
    // ----------------------------------------------------
    fun dismissNotificationFeedback() {
        _notificationFeedbackMessage.value = null
    }

    fun triggerImmediateDeadlineReminder(task: ComplianceTaskEntity) {
        val context = getApplication<Application>()
        TaxNotificationManager.sendDeadlineNotification(context, task)
        _scheduledReminderTaskIds.value = _scheduledReminderTaskIds.value + task.id
        _notificationFeedbackMessage.value = "Notification sent for ${task.title}"
    }

    fun toggleTaskReminder(task: ComplianceTaskEntity, enable: Boolean) {
        val context = getApplication<Application>()
        if (enable) {
            val triggerTime = if (task.dueTimestamp > System.currentTimeMillis()) {
                task.dueTimestamp
            } else {
                System.currentTimeMillis() + 5000L // 5 seconds for verification
            }
            TaxNotificationManager.scheduleDeadlineReminder(context, task, triggerTime)
            TaxNotificationManager.sendDeadlineNotification(context, task)
            _scheduledReminderTaskIds.value = _scheduledReminderTaskIds.value + task.id
            _notificationFeedbackMessage.value = "Reminder active for ${task.title} (Due: ${task.dueDate})"
        } else {
            TaxNotificationManager.cancelDeadlineReminder(context, task.id)
            _scheduledReminderTaskIds.value = _scheduledReminderTaskIds.value - task.id
            _notificationFeedbackMessage.value = "Reminder cancelled for ${task.title}"
        }
    }

    fun scheduleAllUpcomingDeadlineReminders() {
        val context = getApplication<Application>()
        val upcoming = complianceTasks.value.filter { it.status == "UPCOMING" }
        val newSet = _scheduledReminderTaskIds.value.toMutableSet()
        upcoming.forEach { task ->
            val triggerTime = if (task.dueTimestamp > System.currentTimeMillis()) {
                task.dueTimestamp
            } else {
                System.currentTimeMillis() + 10000L
            }
            TaxNotificationManager.scheduleDeadlineReminder(context, task, triggerTime)
            newSet.add(task.id)
        }
        _scheduledReminderTaskIds.value = newSet
        upcoming.firstOrNull()?.let { firstTask ->
            TaxNotificationManager.sendDeadlineNotification(context, firstTask)
        }
        _notificationFeedbackMessage.value = "Statutory reminders activated for ${upcoming.size} upcoming deadlines"
    }

    fun triggerImmediateAiActionReminder(action: NextBestAction) {
        val context = getApplication<Application>()
        TaxNotificationManager.sendNextBestActionNotification(context, action)
        _notificationFeedbackMessage.value = "Notification dispatched: ${action.title}"
    }

    fun scheduleAiActionReminder(action: NextBestAction, delaySeconds: Long = 10) {
        val context = getApplication<Application>()
        TaxNotificationManager.scheduleAiActionReminder(context, action, delaySeconds * 1000)
        TaxNotificationManager.sendNextBestActionNotification(context, action)
        _notificationFeedbackMessage.value = "AI Task Reminder scheduled for ${action.title}"
    }
}
