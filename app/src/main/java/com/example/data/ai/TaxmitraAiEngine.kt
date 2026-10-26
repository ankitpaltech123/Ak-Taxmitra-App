package com.example.data.ai

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

object TaxmitraAiEngine {
    private const val TAG = "TaxmitraAiEngine"
    private val JSON_MEDIA = "application/json; charset=utf-8".toMediaType()

    private val client = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private const val SYSTEM_PROMPT = """
You are AK TAXMITRA AI, India's premier AI-first Business, Tax and Compliance Operating System.
Your motto: "One App. Every Taxpayer. Every CA. Every Business."
Rules:
1. Beginner-first: Explain complex tax/legal terms simply. No unexplained jargon.
2. Accurate & Indian law compliant: Cite relevant sections (Income Tax Act 1961, CGST Act 2017, Companies Act 2013, MSMED Act 2006).
3. Distinguish between official government flows, authorized integrations, and Taxmitra services. Never claim to be the government.
4. Structure recommendations: Compare Proprietorship, Partnership, LLP, OPC, Pvt Ltd objectively.
5. Provide actionable next steps: Always tell the user "Here is what happens next."
6. Offer CA review for complex situations (high turnover, notices, disputes).
7. Transparent pricing: Clearly state government fees vs professional fees. Udyam is 100% free.
"""

    suspend fun queryAi(
        userPrompt: String,
        contextInfo: String = ""
    ): String = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"
                
                val requestJson = JSONObject().apply {
                    val contentsArray = JSONArray()
                    val contentObj = JSONObject().apply {
                        val partsArray = JSONArray()
                        val textWithContext = if (contextInfo.isNotBlank()) {
                            "$SYSTEM_PROMPT\n\n[USER CONTEXT: $contextInfo]\n\nUSER QUESTION: $userPrompt"
                        } else {
                            "$SYSTEM_PROMPT\n\nUSER QUESTION: $userPrompt"
                        }
                        partsArray.put(JSONObject().put("text", textWithContext))
                        put("parts", partsArray)
                    }
                    contentsArray.put(contentObj)
                    put("contents", contentsArray)
                }

                val body = requestJson.toString().toRequestBody(JSON_MEDIA)
                val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .build()

                val response = client.newCall(request).execute()
                val responseBody = response.body?.string()

                if (response.isSuccessful && !responseBody.isNullOrBlank()) {
                    val root = JSONObject(responseBody)
                    val candidates = root.optJSONArray("candidates")
                    if (candidates != null && candidates.length() > 0) {
                        val firstCandidate = candidates.getJSONObject(0)
                        val content = firstCandidate.optJSONObject("content")
                        val parts = content?.optJSONArray("parts")
                        if (parts != null && parts.length() > 0) {
                            val text = parts.getJSONObject(0).optString("text")
                            if (text.isNotBlank()) {
                                return@withContext text.trim()
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Gemini API call failed, falling back to Taxmitra Knowledge Engine: ${e.message}")
            }
        }

        // Smart Local Expert Rule & Knowledge Engine (Guarantees 100% reliability offline or when key is not configured)
        return@withContext fallbackTaxmitraEngine(userPrompt)
    }

    fun fallbackTaxmitraEngine(prompt: String): String {
        val query = prompt.lowercase()
        return when {
            query.contains("clothing") || (query.contains("brother") && query.contains("business")) || (query.contains("start") && query.contains("business")) -> {
                """
Hello! Starting a clothing business with your brother is an exciting journey. Here is Taxmitra's tailored roadmap:

1. Recommended Business Structure:
• If testing locally with low capital: A Partnership Firm or Limited Liability Partnership (LLP).
• LLP is strongly recommended because it protects your and your brother's personal assets (Limited Liability) while offering high credibility for wholesale suppliers and e-commerce platforms.

2. Registrations You Will Need:
• PAN & TAN for the firm.
• GST Registration: Required if selling online across states (e.g. Amazon/Flipkart/Myntra) or if offline turnover exceeds ₹40 Lakhs (goods).
• Udyam MSME Registration: 100% Free on the official portal. Gives priority lending, 45-day payment protection under Sec 43B(h), and bank interest subsidies.
• Shop & Establishment License: From your local state municipal corporation.
• Current Bank Account: In your firm's name.

3. Government Fees vs Professional:
• Udyam: ₹0 (Free & paperless)
• Partnership Deed: Stamp duty ₹500 - ₹2,000 depending on state.
• Taxmitra DIY filing: ₹0 | CA Partner Review: Transparent ₹1,499.

👉 Next Best Action: Tap 'Start Business Discovery' in the Business tab to generate your custom launch checklist!
                """.trimIndent()
            }
            query.contains("gst") || query.contains("need gst") -> {
                """
Do you need GST Registration? Here is the official rule under the CGST Act 2017:

1. Turnover Thresholds:
• For Goods (Manufacturing / Trading): Mandatory only if annual turnover exceeds ₹40 Lakhs (₹20 Lakhs in Special Category North-East States).
• For Services: Mandatory only if annual turnover exceeds ₹20 Lakhs (₹10 Lakhs in Special Category States).

2. Mandatory Registration (Irrespective of Turnover):
• Selling goods via E-commerce platforms (Amazon, Flipkart, Meesho, Shopify interstate).
• Making Interstate supplies of goods (selling from one state to another).
• Liable under Reverse Charge Mechanism (RCM).
• Non-resident taxable persons or casual taxable persons.

3. Voluntarily Taking GST:
You can register voluntarily to claim Input Tax Credit (ITC) on your raw materials, inventory, and capital equipment.

👉 Next Best Action: If your turnover is below ₹40L and you only sell within your state offline, you are exempt!
                """.trimIndent()
            }
            query.contains("notice") || query.contains("143(1)") || query.contains("139(9)") -> {
                """
Don't panic! Receiving an Income Tax notice is very common. Here is what you need to know:

1. Section 143(1) - Intimation:
This is an automated system calculation from the Centralized Processing Center (CPC). It compares the figures in your filed return with records in AIS (Annual Information Statement) and Form 26AS.
• If refund shown: CPC is issuing your refund!
• If demand shown: Usually due to mismatched TDS credit, missed interest income, or disallowed deduction.

2. What should you do?
• Check the deadline: You usually have 30 days from receipt to agree or disagree with the demand.
• Do not ignore it: Unpaid demands incur 1% monthly interest under Section 220.

👉 Next Best Action: Upload the notice PDF to Taxmitra Document Vault. Our OCR will extract the demand breakdown and match you with a verified CA if required.
                """.trimIndent()
            }
            query.contains("file tax") || query.contains("itr") || query.contains("salaried") || query.contains("regime") -> {
                """
Welcome to Taxmitra Tax OS! Filing your Income Tax Return is simple and jargon-free:

1. New Tax Regime (Section 115BAC) - Budget 2024 Highlights:
• Standard Deduction increased to ₹75,000 for salaried employees.
• Full tax rebate u/s 87A: Income up to ₹7,75,000 pays ₹0 tax!
• Lowest slab rates with zero paperwork needed for investments.

2. Old Tax Regime:
• Better only if you have large deductions (80C up to ₹1.5L, 80D Mediclaim up to ₹50k, HRA rent receipt exemption, Home loan interest u/s 24(b) up to ₹2L).

3. Which ITR Form?
• ITR-1 (Sahaj): For salaried individuals with income up to ₹50 Lakhs and one house property.
• ITR-4 (Sugam): For freelancers and small businesses under Presumptive Taxation (Sec 44AD / 44ADA).

👉 Next Best Action: Go to the 'Tax OS' tab and enter your gross income. Taxmitra will calculate both regimes side-by-side!
                """.trimIndent()
            }
            query.contains("udyam") || query.contains("msme") -> {
                """
About Udyam MSME Registration:

• Official Authority: Ministry of Micro, Small and Medium Enterprises, Govt of India.
• Official Cost: 100% FREE. The government portal never charges any fee. Beware of fake third-party portals charging ₹1,500 - ₹3,000!
• Documents Required: Only Aadhaar number and PAN. No physical documents or certificates to upload.
• Benefits:
  1. Priority sector bank loans at subsidized interest rates.
  2. Strict protection under Section 43B(h) of Income Tax Act: Buyers must pay you within 15 to 45 days.
  3. 50% government subsidy on Trademark and Patent filing fees.
  4. Exemption on government tender earnest money deposits (EMD).

👉 Taxmitra provides direct guided handoff to the official udyamregistration.gov.in portal.
                """.trimIndent()
            }
            query.contains("ca") || query.contains("find ca") -> {
                """
Looking for a Chartered Accountant?

Taxmitra operates a transparent, merit-based CA Marketplace:
• Verified Professionals: Every CA holds an active ICAI Membership Number verified by our compliance team.
• Transparent Pricing:
  - DIY (Do-It-Yourself): ₹0
  - AI-Assisted Filing: ₹499
  - CA Review (Expert checks your draft before submission): ₹1,499
  - CA Handled (End-to-end dedicated filing & filing warranty): ₹3,499
• Direct Secure Workspace: Share only the necessary tax records with end-to-end encryption. You retain ownership of all documents.

👉 Next Best Action: Tap 'Find CA' to browse top-rated CAs by specialization, language, and city.
                """.trimIndent()
            }
            query.contains("invoice") || query.contains("bill") -> {
                """
Create Professional Invoices with Taxmitra Invoice Engine:

• GST Compliant: Automatically calculates CGST + SGST (Intrastate) or IGST (Interstate).
• One-Tap Sharing: Download high-resolution PDF or share payment links via WhatsApp/Email.
• Payment Reminders: Automated gentle follow-ups for overdue receivables.
• Accounting Sync: Every invoice immediately updates your Profit & Loss and Cash Flow dashboards!

👉 Next Best Action: Switch to the 'Business OS' tab and tap 'Create First Invoice'.
                """.trimIndent()
            }
            else -> {
                """
Hi! I am your AK Taxmitra AI assistant.

I can help you with:
• Starting a business (choosing between Pvt Ltd, LLP, OPC, or Proprietorship)
• Checking if you need GST or Udyam MSME registration
• Optimizing your income tax (New vs Old Regime comparison with ₹75,000 standard deduction)
• Reading and explaining any Income Tax or GST notice
• Creating GST-compliant invoices and tracking business P&L
• Connecting with verified Chartered Accountants for review

Tell me: What would you like to achieve today?
                """.trimIndent()
            }
        }
    }
}
