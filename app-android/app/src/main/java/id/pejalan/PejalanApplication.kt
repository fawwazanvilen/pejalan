package id.pejalan

import android.app.Application
import android.content.Context
import com.mapbox.common.MapboxOptions
import id.pejalan.data.LaporanDb
import id.pejalan.ml.AiMode
import id.pejalan.ml.Classifier
import id.pejalan.ml.ClassificationQueue
import id.pejalan.ml.GeminiClient
import id.pejalan.ml.GemmaClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class PejalanApplication : Application() {

    val gemma: GemmaClient by lazy { GemmaClient(applicationContext) }
    val gemini: GeminiClient by lazy { GeminiClient(BuildConfig.GEMINI_API_KEY) }
    val db: LaporanDb by lazy { LaporanDb.getInstance(applicationContext) }

    // Active inference mode. Defaults to local Gemma. UI toggles via setMode().
    // In-memory only — resets to Lokal on process death.
    private val _mode = MutableStateFlow(AiMode.Lokal)
    val mode: StateFlow<AiMode> = _mode

    fun setMode(newMode: AiMode) {
        _mode.value = newMode
    }

    /** Resolves to the current backend based on mode. Read fresh on every call so
     *  toggles take effect immediately for the next classification. */
    val activeClassifier: Classifier
        get() = when (_mode.value) {
            AiMode.Lokal -> gemma
            AiMode.Cloud -> gemini
        }

    /** Whether the Gemini API key was provided at build time. UI hides the Cloud
     *  toggle entry when this is false. */
    val cloudAvailable: Boolean
        get() = BuildConfig.GEMINI_API_KEY.isNotBlank()

    // Queue uses a function reference so it always picks up the latest classifier.
    val queue: ClassificationQueue by lazy { ClassificationQueue(db) { activeClassifier } }

    override fun onCreate() {
        super.onCreate()
        MapboxOptions.accessToken = BuildConfig.MAPBOX_ACCESS_TOKEN
    }
}

val Context.pejalanApp: PejalanApplication
    get() = applicationContext as PejalanApplication
