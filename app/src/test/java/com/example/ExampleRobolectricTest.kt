package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.engine.BusinessStructureEngine
import com.example.data.engine.TaxCalculationEngine
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("AK Taxmitra AI", appName)
    }

    @Test
    fun `test tax calculation new regime standard deduction and 87A rebate`() {
        // Gross Salary ₹7,00,000 should pay ₹0 tax under New Regime with ₹75,000 standard deduction + 87A rebate
        val result = TaxCalculationEngine.calculate(
            salaryIncome = 700000.0,
            businessIncome = 0.0,
            rentalIncome = 0.0,
            capitalGains = 0.0,
            otherIncome = 0.0,
            deduction80C = 0.0,
            deduction80D = 0.0,
            deductionNps = 0.0,
            otherDeductions = 0.0,
            tdsPaid = 0.0,
            advanceTaxPaid = 0.0
        )

        assertEquals("NEW", result.recommendedRegime)
        assertEquals(0.0, result.totalTaxNew, 0.01)
        assertEquals("ITR-1 (Sahaj)", result.recommendedItrForm)
    }

    @Test
    fun `test business structure clothing business with brother`() {
        val recommendation = BusinessStructureEngine.evaluate(
            founderCount = 2,
            seeksInvestment = false,
            wantsLimitedLiability = true,
            turnoverRange = "20L - 40L",
            isOnline = true
        )

        assertEquals("Limited Liability Partnership (LLP)", recommendation.recommendedEntity)
        assertTrue(recommendation.initialRegistrations.any { it.contains("Udyam") })
        assertTrue(recommendation.initialRegistrations.any { it.contains("GST") })
    }

    @Test
    fun `test gemini model selection resolves pro for complex, lite for fast, flash for general`() {
        // Complex notice scrutiny should route to gemini-3.1-pro-preview
        val complexModel = com.example.data.ai.GeminiApiClient.resolveModel(
            prompt = "Received Section 148 notice for high value capital gains reassessment",
            preference = com.example.data.ai.AiModelPreference.AUTO
        )
        assertEquals(com.example.data.ai.GeminiModels.MODEL_PRO_COMPLEX, complexModel)
        assertEquals("gemini-3.1-pro-preview", complexModel)

        // Fast calculation lookup should route to gemini-3.1-flash-lite
        val fastModel = com.example.data.ai.GeminiApiClient.resolveModel(
            prompt = "Quick slab rate for 10 lakh",
            preference = com.example.data.ai.AiModelPreference.AUTO
        )
        assertEquals(com.example.data.ai.GeminiModels.MODEL_LITE_FAST, fastModel)
        assertEquals("gemini-3.1-flash-lite", fastModel)

        // General business inquiry should route to gemini-3.5-flash
        val generalModel = com.example.data.ai.GeminiApiClient.resolveModel(
            prompt = "What are the compliance requirements for hiring employees in Karnataka?",
            preference = com.example.data.ai.AiModelPreference.AUTO
        )
        assertEquals(com.example.data.ai.GeminiModels.MODEL_FLASH_GENERAL, generalModel)
        assertEquals("gemini-3.5-flash", generalModel)
    }

    @Test
    fun `test live voice model constants and chatbot roles`() {
        assertEquals("gemini-3.1-flash-live-preview", com.example.data.ai.GeminiModels.MODEL_LIVE_VOICE)
        assertEquals("gemini-3.8-flash", com.example.data.ai.GeminiModels.MODEL_FLASH_38)

        // Verify system instructions for specialized chatbot roles
        for (role in com.example.data.ai.ChatbotRole.entries) {
            assertTrue(role.systemInstruction.isNotBlank())
            assertTrue(role.title.isNotBlank())
        }
    }

    @Test
    fun `test tax notification manager channel creation and immediate notification triggers`() {
        val context = ApplicationProvider.getApplicationContext<Context>()

        // 1. Channel initialization
        com.example.data.notification.TaxNotificationManager.createNotificationChannels(context)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as android.app.NotificationManager
        val deadlineChannel = notificationManager.getNotificationChannel(com.example.data.notification.TaxNotificationManager.CHANNEL_TAX_DEADLINES)
        val aiChannel = notificationManager.getNotificationChannel(com.example.data.notification.TaxNotificationManager.CHANNEL_AI_ACTIONS)

        assertNotNull(deadlineChannel)
        assertNotNull(aiChannel)
        assertEquals("Tax & Statutory Deadlines", deadlineChannel.name)
        assertEquals("AI Copilot Action Items", aiChannel.name)

        // 2. Trigger immediate deadline reminder notification
        val sampleTask = com.example.data.model.ComplianceTaskEntity(
            id = 101L,
            title = "GSTR-3B Return Filing Due",
            category = "GST",
            dueDate = "2026-09-20",
            dueTimestamp = System.currentTimeMillis() + 86400000L,
            frequency = "Monthly",
            penaltyWarning = "Late fee ₹50/day up to max ₹10,000",
            authority = "GST Council / CBIC",
            officialUrl = "https://services.gst.gov.in",
            status = "UPCOMING",
            isAutoCalculated = true
        )
        com.example.data.notification.TaxNotificationManager.sendDeadlineNotification(
            context = context,
            task = sampleTask
        )

        // 3. Trigger immediate AI action reminder notification
        val sampleAction = com.example.data.engine.NextBestAction(
            title = "Opt for New Tax Regime FY 2025-26",
            description = "Save ₹45,000 based on your current salary income deductions profile.",
            actionButtonText = "Switch Regime",
            targetTab = "TAX",
            urgencyLevel = "IMPORTANT"
        )
        com.example.data.notification.TaxNotificationManager.sendNextBestActionNotification(
            context = context,
            action = sampleAction
        )

        // 4. Test notification status utility
        val isEnabled = com.example.data.notification.TaxNotificationManager.areNotificationsEnabled(context)
        assertNotNull(isEnabled)
    }
}
