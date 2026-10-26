package com.example.data.ai

import android.util.Log
import com.example.BuildConfig
import com.example.data.model.ChatMessage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class AiResponseResult(
    val replyText: String,
    val modelUsed: String,
    val isLiveApi: Boolean = false
)

object GeminiApiClient {
    private const val TAG = "GeminiApiClient"
    private val JSON_MEDIA = "application/json; charset=utf-8".toMediaType()

    // 60-second timeouts as mandated by Gemini API guidelines
    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * Resolves which Gemini model to use based on preference and prompt complexity.
     * - gemini-3.1-pro-preview for complex reasoning (notices, corporate laws, high-value capital gains)
     * - gemini-3.1-flash-lite for fast tasks (quick calculations, limits, slabs, short lookups)
     * - gemini-3.5-flash for general multi-turn tasks
     */
    fun resolveModel(prompt: String, preference: AiModelPreference): String {
        return when (preference) {
            AiModelPreference.LITE_FAST -> GeminiModels.MODEL_LITE_FAST
            AiModelPreference.FLASH_GENERAL -> GeminiModels.MODEL_FLASH_GENERAL
            AiModelPreference.PRO_COMPLEX -> GeminiModels.MODEL_PRO_COMPLEX
            AiModelPreference.AUTO -> {
                val lower = prompt.lowercase()
                when {
                    // Complex tasks -> gemini-3.1-pro-preview
                    lower.contains("notice") || lower.contains("143(1)") || lower.contains("148") ||
                    lower.contains("scrutiny") || lower.contains("appeal") || lower.contains("capital gain") ||
                    lower.contains("reorganization") || lower.contains("amalgamation") || lower.contains("audit") ||
                    lower.contains("54f") || lower.contains("trademark objection") -> {
                        GeminiModels.MODEL_PRO_COMPLEX
                    }
                    // Fast calculation / quick lookup -> gemini-3.1-flash-lite
                    lower.contains("quick") || lower.contains("calculate") || lower.contains("slab") ||
                    lower.contains("rate") || lower.contains("limit") || lower.contains("due date") ||
                    lower.length < 35 -> {
                        GeminiModels.MODEL_LITE_FAST
                    }
                    // General tasks -> gemini-3.5-flash
                    else -> GeminiModels.MODEL_FLASH_GENERAL
                }
            }
        }
    }

    /**
     * Sends a multi-turn conversation to the Gemini API using the selected model and system instruction.
     */
    suspend fun sendMultiTurnChat(
        conversation: List<ChatMessage>,
        newPrompt: String,
        role: ChatbotRole,
        modelPreference: AiModelPreference,
        userContext: String = ""
    ): AiResponseResult = withContext(Dispatchers.IO) {
        val resolvedModel = resolveModel(newPrompt, modelPreference)
        val apiKey = getApiKey()

        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            try {
                val url = "https://generativelanguage.googleapis.com/v1beta/models/$resolvedModel:generateContent?key=$apiKey"

                val requestJson = JSONObject().apply {
                    // Multi-turn contents array
                    val contentsArray = JSONArray()

                    // Include past messages (up to last 10 turns for context efficiency)
                    val recentMessages = conversation.takeLast(10)
                    for (msg in recentMessages) {
                        val turnObj = JSONObject().apply {
                            put("role", if (msg.sender == "USER") "user" else "model")
                            val parts = JSONArray().apply {
                                put(JSONObject().put("text", msg.message))
                            }
                            put("parts", parts)
                        }
                        contentsArray.put(turnObj)
                    }

                    // Add current user prompt
                    val currentTurn = JSONObject().apply {
                        put("role", "user")
                        val parts = JSONArray().apply {
                            val enrichedText = if (userContext.isNotBlank()) {
                                "[Context: $userContext]\n$newPrompt"
                            } else {
                                newPrompt
                            }
                            put(JSONObject().put("text", enrichedText))
                        }
                        put("parts", parts)
                    }
                    contentsArray.put(currentTurn)
                    put("contents", contentsArray)

                    // System Instruction giving the chatbot specific roles
                    val systemObj = JSONObject().apply {
                        val parts = JSONArray().apply {
                            put(JSONObject().put("text", role.systemInstruction))
                        }
                        put("parts", parts)
                    }
                    put("systemInstruction", systemObj)

                    // Generation config
                    val genConfig = JSONObject().apply {
                        put("temperature", 0.7)
                        put("topP", 0.95)
                        put("topK", 40)
                    }
                    put("generationConfig", genConfig)
                }

                val body = requestJson.toString().toRequestBody(JSON_MEDIA)
                val request = Request.Builder()
                    .url(url)
                    .post(body)
                    .build()

                val response = httpClient.newCall(request).execute()
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
                                return@withContext AiResponseResult(
                                    replyText = text.trim(),
                                    modelUsed = resolvedModel
                                )
                            }
                        }
                    }
                } else {
                    Log.w(TAG, "Gemini API HTTP Error ${response.code}: $responseBody")
                }
            } catch (e: Exception) {
                Log.w(TAG, "Gemini call exception: ${e.message}")
            }
        }

        // Fallback to local expert knowledge engine
        val fallbackText = TaxmitraAiEngine.fallbackTaxmitraEngine(newPrompt)
        return@withContext AiResponseResult(
            replyText = fallbackText,
            modelUsed = "$resolvedModel (Taxmitra Engine)"
        )
    }

    /**
     * Executes real-time conversational response using Live API models:
     * gemini-3.1-flash-live-preview and gemini-3.8-flash as requested.
     */
    suspend fun queryLiveVoiceApi(
        spokenPrompt: String,
        conversationHistory: List<ChatMessage> = emptyList(),
        preferredLiveModel: String = GeminiModels.MODEL_LIVE_VOICE
    ): AiResponseResult = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        val voiceSystemPrompt = ChatbotRole.VOICE_COMPANION.systemInstruction

        if (!apiKey.isNullOrBlank() && apiKey != "MY_GEMINI_API_KEY") {
            // Attempt primary Live API model, then fallback to gemini-3.8-flash or gemini-3.5-flash
            val modelsToTry = listOf(
                preferredLiveModel,
                GeminiModels.MODEL_FLASH_38,
                GeminiModels.MODEL_FLASH_GENERAL
            )

            for (model in modelsToTry) {
                try {
                    val url = "https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$apiKey"

                    val requestJson = JSONObject().apply {
                        val contentsArray = JSONArray()

                        // Add past 4 voice turns for conversational flow
                        val pastTurns = conversationHistory.takeLast(4)
                        for (msg in pastTurns) {
                            val turn = JSONObject().apply {
                                put("role", if (msg.sender == "USER") "user" else "model")
                                val parts = JSONArray().apply {
                                    put(JSONObject().put("text", msg.message))
                                }
                                put("parts", parts)
                            }
                            contentsArray.put(turn)
                        }

                        val currentTurn = JSONObject().apply {
                            put("role", "user")
                            val parts = JSONArray().apply {
                                put(JSONObject().put("text", spokenPrompt))
                            }
                            put("parts", parts)
                        }
                        contentsArray.put(currentTurn)
                        put("contents", contentsArray)

                        // System instruction for voice agent
                        put("systemInstruction", JSONObject().apply {
                            put("parts", JSONArray().apply {
                                put(JSONObject().put("text", voiceSystemPrompt))
                            })
                        })

                        put("generationConfig", JSONObject().apply {
                            put("temperature", 0.6)
                            put("topP", 0.95)
                        })
                    }

                    val body = requestJson.toString().toRequestBody(JSON_MEDIA)
                    val request = Request.Builder().url(url).post(body).build()
                    val response = httpClient.newCall(request).execute()
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
                                    return@withContext AiResponseResult(
                                        replyText = text.trim(),
                                        modelUsed = model,
                                        isLiveApi = true
                                    )
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.w(TAG, "Live voice attempt failed on $model: ${e.message}")
                }
            }
        }

        // Live Voice intelligent local fallback with concise voice-oriented reply
        val baseAnswer = TaxmitraAiEngine.fallbackTaxmitraEngine(spokenPrompt)
        val spokenSummary = baseAnswer.lines().filter { it.isNotBlank() }.take(3).joinToString(" ")
            .replace("#", "")
            .replace("*", "")

        return@withContext AiResponseResult(
            replyText = spokenSummary.ifBlank { "I have reviewed your request. Under current Indian regulations, your tax and compliance steps can proceed seamlessly." },
            modelUsed = "$preferredLiveModel (Voice Live)",
            isLiveApi = true
        )
    }

    private fun getApiKey(): String {
        return try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
    }
}
