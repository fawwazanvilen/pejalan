package id.pejalan.ml

import android.graphics.Bitmap
import android.util.Log
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.content
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Cloud-based classifier using Google's Gemini Flash via the generativeai SDK.
 *
 * Photos leave the device — the user must be informed before this is enabled.
 * Used for sharing demos with people who don't want to download the 2GB on-device model.
 */
class GeminiClient(private val apiKey: String) : Classifier {

    override val requiresNetwork: Boolean = true

    private val model by lazy {
        require(apiKey.isNotBlank()) {
            "GEMINI_API_KEY is not set. Add it to ~/.gradle/gradle.properties."
        }
        GenerativeModel(
//            modelName = "gemini-flash-latest",
            modelName = "gemini-3.1-flash-lite",
            apiKey = apiKey,
            generationConfig = generationConfig {
                temperature = 0.4f
                topK = 10
                topP = 0.95f
                maxOutputTokens = 1024
            },
        )
    }

    override suspend fun initialize() {
        // No-op. The HTTP-based model is "ready" without any local setup.
    }

    override suspend fun classify(bitmap: Bitmap): Classification = withContext(Dispatchers.IO) {
        try {
            val response = model.generateContent(
                content {
                    image(bitmap)
                    text(PROMPT)
                }
            )
            val raw = response.text.orEmpty()
            Log.d(TAG, "==== Gemini raw response (${raw.length} chars) ====")
            Log.d(TAG, raw.ifBlank { "<EMPTY>" })
            Log.d(TAG, "==== end raw response ====")
            parseClassification(raw)
        } catch (e: Exception) {
            Log.e(TAG, "Gemini call failed", e)
            Classification.Fallback
        }
    }

    override fun close() {}

    companion object {
        private const val TAG = "PejalanGemini"
    }
}
