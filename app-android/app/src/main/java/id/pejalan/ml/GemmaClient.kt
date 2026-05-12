package id.pejalan.ml

import android.content.Context
import android.graphics.Bitmap
import com.google.ai.edge.litertlm.Backend
import com.google.ai.edge.litertlm.Content
import com.google.ai.edge.litertlm.Contents
import com.google.ai.edge.litertlm.Conversation
import com.google.ai.edge.litertlm.ConversationConfig
import com.google.ai.edge.litertlm.Engine
import com.google.ai.edge.litertlm.EngineConfig
import com.google.ai.edge.litertlm.Message
import com.google.ai.edge.litertlm.MessageCallback
import com.google.ai.edge.litertlm.SamplerConfig
import java.io.ByteArrayOutputStream
import java.io.File
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import org.json.JSONObject

class GemmaClient(private val context: Context) {

    @Volatile private var engine: Engine? = null

    val modelExists: Boolean get() = File(MODEL_PATH).exists()

    suspend fun initialize() = withContext(Dispatchers.IO) {
        if (engine != null) return@withContext
        check(modelExists) {
            "Model file not found at $MODEL_PATH. Run: adb push gemma-4-E2B-it.litertlm $MODEL_PATH"
        }
        // LiteRT-LM writes scratch state during init. /data/local/tmp isn't writable by the app,
        // so route the cache to our private external dir when the model lives there.
        val cacheDir = if (MODEL_PATH.startsWith("/data/local/tmp")) {
            context.getExternalFilesDir(null)?.absolutePath
        } else null

        val config = EngineConfig(
            modelPath = MODEL_PATH,
            backend = Backend.GPU(),
            visionBackend = Backend.GPU(),
            audioBackend = null,
            maxNumTokens = 512,
            cacheDir = cacheDir,
        )
        val newEngine = Engine(config)
        newEngine.initialize()
        engine = newEngine
    }

    suspend fun classify(bitmap: Bitmap): Classification = withContext(Dispatchers.Default) {
        val eng = engine ?: error("GemmaClient not initialized — call initialize() first.")

        val conversation = eng.createConversation(
            ConversationConfig(
                samplerConfig = SamplerConfig(
                    topK = 10,
                    topP = 0.95,
                    temperature = 0.4,
                ),
            )
        )
        try {
            val raw = sendAndCollect(conversation, PROMPT, bitmap)
            parseClassification(raw)
        } finally {
            runCatching { conversation.close() }
        }
    }

    private suspend fun sendAndCollect(
        conversation: Conversation,
        prompt: String,
        bitmap: Bitmap,
    ): String = suspendCancellableCoroutine { cont ->
        val accumulated = StringBuilder()
        val contents = Contents.of(
            listOf(
                Content.ImageBytes(bitmap.toPngByteArray()),
                Content.Text(prompt),
            )
        )
        conversation.sendMessageAsync(
            contents,
            object : MessageCallback {
                override fun onMessage(message: Message) {
                    accumulated.append(message.toString())
                }
                override fun onDone() {
                    if (cont.isActive) cont.resume(accumulated.toString())
                }
                override fun onError(throwable: Throwable) {
                    if (cont.isActive) cont.resumeWithException(throwable)
                }
            },
            emptyMap(),
        )
        cont.invokeOnCancellation { runCatching { conversation.cancelProcess() } }
    }

    fun close() {
        engine?.close()
        engine = null
    }

    companion object {
        const val MODEL_PATH = "/data/local/tmp/llm/gemma4_e2b.litertlm"
    }
}

private fun Bitmap.toPngByteArray(): ByteArray {
    val stream = ByteArrayOutputStream()
    compress(Bitmap.CompressFormat.PNG, 100, stream)
    return stream.toByteArray()
}

internal fun parseClassification(raw: String): Classification {
    val start = raw.indexOf('{')
    val end = raw.lastIndexOf('}')
    if (start < 0 || end <= start) return Classification.Fallback

    return try {
        val json = JSONObject(raw.substring(start, end + 1))
        Classification(
            kategori  = Kategori.fromString(json.optString("kategori")),
            severitas = Severitas.fromString(json.optString("severitas")),
            keyakinan = json.optDouble("keyakinan", 0.0).toFloat().coerceIn(0f, 1f),
            rasional  = json.optString("rasional", ""),
            bbox      = json.optJSONObject("bbox")?.let {
                BBox(
                    x = it.optDouble("x", 0.0).toFloat().coerceIn(0f, 1f),
                    y = it.optDouble("y", 0.0).toFloat().coerceIn(0f, 1f),
                    w = it.optDouble("w", 1.0).toFloat().coerceIn(0f, 1f),
                    h = it.optDouble("h", 1.0).toFloat().coerceIn(0f, 1f),
                )
            } ?: BBox.Full,
        )
    } catch (_: Exception) {
        Classification.Fallback
    }
}
