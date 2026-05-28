package com.example.data

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.GET
import retrofit2.http.Header
import java.util.concurrent.TimeUnit
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

// --- Gemini REST API Data Classes ---

data class GeminiPart(val text: String)

data class GeminiContent(val parts: List<GeminiPart>)

data class GeminiGenerateRequest(
    val contents: List<GeminiContent>,
    val systemInstruction: GeminiContent? = null
)

data class GeminiTextCandidate(val content: GeminiContent)

data class GeminiGenerateResponse(
    val candidates: List<GeminiTextCandidate>?
)

// --- Retrofit Interface ---

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GeminiGenerateRequest
    ): GeminiGenerateResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(GeminiApiService::class.java)
    }
}

object AiManager {
    private const val TAG = "AiManager"

    // Helper to get Gemini API Key safely
    private fun getApiKey(): String {
        return try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Call Gemini API to translate / generate customer support auto responses.
     */
    suspend fun generateAutoReply(
        incomingMessage: String,
        ruleKeyword: String,
        template: String,
        tone: String
    ): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "Gemini API key is not configured. Falling back to rule template reply.")
            return@withContext "🤖 [Smart AI Reply] Thank you for asking about '$ruleKeyword'. $template"
        }

        val systemPrompt = """
            You are a professional customer support chat assistant for ChatFlow AI (WhatsApp Automation SaaS).
            Your tone is: $tone.
            If the customer asks about '$ruleKeyword', use the following base template information to formulate a response:
            "$template"
            Keep the response short, conversational, friendly, elegant and under 80 words. Answer directly. Do not include hashtags or markdown formatting, keep it plain text.
        """.trimIndent()

        val requestBody = GeminiGenerateRequest(
            contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = incomingMessage)))),
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt)))
        )

        try {
            val response = GeminiClient.service.generateContent(apiKey, requestBody)
            val resultText = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text
            if (!resultText.isNullOrBlank()) {
                resultText.trim()
            } else {
                "🤖 [Auto] Thank you! We received your message regarding '$ruleKeyword'."
            }
        } catch (e: Exception) {
            Log.e(TAG, "Gemini generation failed: ${e.message}", e)
            "🤖 [Standard Auto-Response] Thanks for your message. Regarding '$ruleKeyword': $template"
        }
    }

    /**
     * Determines sentiment of message: Positive, Neutral, or Negative.
     */
    suspend fun analyzeSentiment(messageText: String): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            // Local fallback analyzer
            val text = messageText.lowercase()
            return@withContext when {
                text.contains("love") || text.contains("great") || text.contains("thank") || text.contains("awesome") || text.contains("good") -> "Positive"
                text.contains("bad") || text.contains("hate") || text.contains("broke") || text.contains("error") || text.contains("scam") || text.contains("slow") -> "Negative"
                else -> "Neutral"
            }
        }

        val systemPrompt = "Analyze the sentiment of this text. Respond with EXACTLY one of these three words: Positive, Neutral, Negative."
        val requestBody = GeminiGenerateRequest(
            contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = messageText)))),
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt)))
        )

        try {
            val response = GeminiClient.service.generateContent(apiKey, requestBody)
            val result = response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim() ?: "Neutral"
            if (result == "Positive" || result == "Negative" || result == "Neutral") {
                result
            } else {
                "Neutral"
            }
        } catch (e: Exception) {
            Log.e(TAG, "Sentiment analysis failed", e)
            "Neutral"
        }
    }

    /**
     * Smart translation.
     */
    suspend fun translateMessage(messageText: String, targetLanguage: String = "Spanish"): String = withContext(Dispatchers.IO) {
        val apiKey = getApiKey()
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext "[Translated to $targetLanguage] $messageText"
        }

        val systemPrompt = "You are a professional translator. Translate the user's message to $targetLanguage. Output ONLY the plain translation text, nothing else."
        val requestBody = GeminiGenerateRequest(
            contents = listOf(GeminiContent(parts = listOf(GeminiPart(text = messageText)))),
            systemInstruction = GeminiContent(parts = listOf(GeminiPart(text = systemPrompt)))
        )

        try {
            val response = GeminiClient.service.generateContent(apiKey, requestBody)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text?.trim() ?: messageText
        } catch (e: Exception) {
            Log.e(TAG, "Translation failed", e)
            messageText
        }
    }
}

// --- WhatsApp Backend Express Integration ---

data class BackendAuthRequest(
    val email: String,
    val password: String = "password",
    val name: String? = null
)

data class BackendUser(
    val id: Int,
    val email: String,
    val name: String,
    val plan: String
)

data class BackendAuthResponse(
    val token: String,
    val user: BackendUser
)

data class WhatsappStatusResponse(
    val connected: Boolean,
    val status: String,
    val qr: String? = null,
    val message: String? = null
)

data class ConnectResponse(
    val status: String,
    val message: String
)

interface ChatFlowBackendApi {
    @POST("api/auth/login")
    suspend fun login(
        @Body request: BackendAuthRequest
    ): BackendAuthResponse

    @POST("api/auth/register")
    suspend fun register(
        @Body request: BackendAuthRequest
    ): BackendAuthResponse

    @GET("api/whatsapp/status")
    suspend fun getStatus(
        @Header("Authorization") authHeader: String
    ): WhatsappStatusResponse

    @POST("api/whatsapp/connect")
    suspend fun connectWhatsApp(
        @Header("Authorization") authHeader: String
    ): ConnectResponse
}

object BackendClient {
    private var currentBaseUrl = "http://10.0.2.2:5000/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
        .readTimeout(10, java.util.concurrent.TimeUnit.SECONDS)
        .build()

    private var cachedService: ChatFlowBackendApi? = null

    fun getService(baseUrl: String): ChatFlowBackendApi {
        val trimmed = baseUrl.trim()
        val formattedUrl = when {
            trimmed.isEmpty() -> "http://10.0.2.2:5000/"
            trimmed.endsWith("/") -> trimmed
            else -> "$trimmed/"
        }
        if (cachedService == null || currentBaseUrl != formattedUrl) {
            currentBaseUrl = formattedUrl
            cachedService = try {
                Retrofit.Builder()
                    .baseUrl(formattedUrl)
                    .client(okHttpClient)
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .build()
                    .create(ChatFlowBackendApi::class.java)
            } catch (e: Exception) {
                Log.e("BackendClient", "Failed to build Retrofit service for URL: $formattedUrl", e)
                fallbackService
            }
        }
        return cachedService ?: fallbackService
    }

    private val fallbackService = object : ChatFlowBackendApi {
        override suspend fun login(request: BackendAuthRequest): BackendAuthResponse {
            throw IllegalStateException("Invalid backend base url configured.")
        }
        override suspend fun register(request: BackendAuthRequest): BackendAuthResponse {
            throw IllegalStateException("Invalid backend base url configured.")
        }
        override suspend fun getStatus(authHeader: String): WhatsappStatusResponse {
            throw IllegalStateException("Invalid backend base url configured.")
        }
        override suspend fun connectWhatsApp(authHeader: String): ConnectResponse {
            throw IllegalStateException("Invalid backend base url configured.")
        }
    }
}

