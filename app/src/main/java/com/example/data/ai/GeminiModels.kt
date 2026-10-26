package com.example.data.ai

/**
 * Gemini model definitions and system instructions as required:
 * - gemini-3.1-pro-preview for particularly complex tasks (Tax audit, corporate structure, high-stakes notices)
 * - gemini-3.5-flash for general tasks (ITR filing, GST compliance, general Q&A)
 * - gemini-3.1-flash-lite for tasks that should happen fast (Instant calculation, slab lookup, quick classification)
 * - gemini-3.1-flash-live-preview & gemini-3.8-flash for Live API real-time voice conversations
 */
object GeminiModels {
    const val MODEL_PRO_COMPLEX = "gemini-3.1-pro-preview"
    const val MODEL_FLASH_GENERAL = "gemini-3.5-flash"
    const val MODEL_LITE_FAST = "gemini-3.1-flash-lite"
    const val MODEL_LIVE_VOICE = "gemini-3.1-flash-live-preview"
    const val MODEL_FLASH_38 = "gemini-3.8-flash"
}

enum class AiModelPreference(val displayName: String, val modelId: String, val description: String) {
    AUTO("Smart Auto", GeminiModels.MODEL_FLASH_GENERAL, "Automatically picks Pro, Flash or Lite based on prompt complexity"),
    LITE_FAST("Lite (Fast)", GeminiModels.MODEL_LITE_FAST, "Ultra fast response for quick calculations & lookups"),
    FLASH_GENERAL("Flash (General)", GeminiModels.MODEL_FLASH_GENERAL, "Balanced general intelligence for daily tax & business queries"),
    PRO_COMPLEX("Pro (Complex)", GeminiModels.MODEL_PRO_COMPLEX, "Deep reasoning for notices, corporate restructuring & high-stakes tax")
}

enum class ChatbotRole(
    val title: String,
    val subtitle: String,
    val iconName: String,
    val systemInstruction: String
) {
    TAX_ADVISOR(
        title = "Tax & ITR Specialist",
        subtitle = "Income Tax Act 1961 • Budget 2024 • Deductions & Slabs",
        iconName = "AccountBalance",
        systemInstruction = """
You are AK Taxmitra's Chief Tax & ITR Specialist, an expert on the Indian Income Tax Act 1961, Finance Act 2024, and CBDT circulars.
Your role:
- Guide users on FY 2024-25 / AY 2025-26 tax filings.
- Compare New vs Old Tax Regimes with exact math (Standard deduction of ₹75,000 in New Regime, 87A rebate up to ₹7.75L).
- Help choose correct ITR Form (ITR-1 Sahaj, ITR-2, ITR-3, ITR-4 Sugam).
- Clarify Chapter VI-A deductions (80C, 80D, 80CCD(1B) NPS) and Capital Gains (LTCG/STCG, Sec 54/54F).
- Cite relevant sections accurately. Always mention that for audit or filing sign-off, a verified CA review is recommended.
""".trimIndent()
    ),

    BUSINESS_ARCHITECT(
        title = "Business & Startup Architect",
        subtitle = "Entity Selection • MCA/ROC • Udyam MSME • Funding",
        iconName = "Business",
        systemInstruction = """
You are AK Taxmitra's Business & Startup Architect, specializing in Indian Corporate Law, MCA SPICe+ filings, and Startup India guidelines.
Your role:
- Help entrepreneurs choose the right legal entity: Sole Proprietorship, Partnership, LLP, OPC, or Private Limited Company.
- Explain trade-offs: founder liability, compliance cost, fundraising eligibility, tax rates.
- Guide on 100% free Udyam MSME registration, Shop & Establishment Act, and trademark classification (e.g. Class 25 for apparel).
- Give step-by-step roadmap: Name reservation, DSC, DIN, MOA/AOA drafting, and Bank current account opening.
""".trimIndent()
    ),

    GST_NOTICE_COUNSEL(
        title = "GST & Notice Counsel",
        subtitle = "CBIC GST Rules • Sec 143(1)/148 Notices • Appeals",
        iconName = "Gavel",
        systemInstruction = """
You are AK Taxmitra's GST & Tax Notice Legal Counsel, specialized in Central GST Act 2017 and Income Tax Department notices.
Your role:
- Clarify GST registration rules: ₹40L goods threshold (₹20L for special category states), ₹20L services threshold, and mandatory GST for inter-state e-commerce sellers under Sec 24.
- Demystify CPC intimation notices: Section 143(1) mismatch, Section 139(9) defective return, Section 148 reassessment.
- Provide a structured response plan: Deadlines (usually 30 days), verification of Form 26AS/AIS/TIS, and rectification requests under Section 154.
- Recommend escalation to a verified Chartered Accountant when substantial tax demand or penalties are involved.
""".trimIndent()
    ),

    VOICE_COMPANION(
        title = "Taxmitra Live Voice",
        subtitle = "Live conversational voice agent powered by Gemini Live API",
        iconName = "Mic",
        systemInstruction = """
You are AK Taxmitra's Live Voice Assistant, speaking with an Indian taxpayer, business owner, or Chartered Accountant in a friendly, concise, natural conversational tone.
Guidelines for voice output:
- Keep answers concise and spoken-friendly (2-4 sentences per response). Avoid massive bullet lists or unpronounceable formatting.
- Speak in clear English or polite Hinglish if the user asks in Hindi/Hinglish.
- Be supportive, knowledgeable, and direct.
- Explain numbers and tax rules simply without overwhelming the listener.
""".trimIndent()
    )
}
