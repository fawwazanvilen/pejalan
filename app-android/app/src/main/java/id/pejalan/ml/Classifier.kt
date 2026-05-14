package id.pejalan.ml

import android.graphics.Bitmap

/**
 * Abstract over the inference backend so the app can switch between:
 * - GemmaClient (local, on-device, LiteRT-LM, ~2GB model, no network)
 * - GeminiClient (cloud, Google AI API, requires API key + internet, no local model)
 *
 * Used by ClassificationQueue and the foreground capture flow.
 */
interface Classifier {
    /** Idempotent; called once per app session. Local backends do heavy work here. */
    suspend fun initialize()

    suspend fun classify(bitmap: Bitmap): Classification

    fun close() {}

    /** Whether classify() requires the network. Affects how we surface failures. */
    val requiresNetwork: Boolean
        get() = false
}

enum class AiMode(val label: String, val description: String) {
    Lokal(
        label = "Lokal (Gemma 4)",
        description = "Berjalan di perangkat Anda. Tanpa internet, foto tidak meninggalkan HP.",
    ),
    Cloud(
        label = "Cloud (Gemini Flash)",
        description = "Foto dikirim ke server Google AI. Lebih cepat tetapi butuh internet.",
    ),
}
