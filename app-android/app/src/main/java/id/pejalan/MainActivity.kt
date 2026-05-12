package id.pejalan

import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import id.pejalan.data.Laporan
import id.pejalan.data.LaporanDb
import id.pejalan.ml.Classification
import id.pejalan.ml.GemmaClient
import id.pejalan.nav.PejalanNav
import id.pejalan.ui.camera.CameraScreen
import id.pejalan.ui.result.AnalyzingOverlay
import id.pejalan.ui.result.ResultSheet
import id.pejalan.ui.saved.SavedScreen
import id.pejalan.ui.theme.PejalanTheme
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : ComponentActivity() {

    private lateinit var gemma: GemmaClient
    private lateinit var db: LaporanDb

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        gemma = GemmaClient(applicationContext)
        db = LaporanDb.getInstance(applicationContext)
        setContent {
            PejalanTheme {
                PejalanApp(gemma, db)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gemma.close()
    }
}

private sealed interface InitState {
    object Loading : InitState
    object Ready : InitState
    data class Error(val message: String) : InitState
}

@Composable
private fun PejalanApp(gemma: GemmaClient, db: LaporanDb) {
    var initState by remember { mutableStateOf<InitState>(InitState.Loading) }

    LaunchedEffect(Unit) {
        initState = try {
            gemma.initialize()
            InitState.Ready
        } catch (e: Exception) {
            InitState.Error(e.message ?: e::class.simpleName ?: "Unknown error")
        }
    }

    when (val s = initState) {
        InitState.Loading -> SplashScreen(message = "Memuat model Gemma…")
        is InitState.Error -> SplashScreen(message = s.message, isError = true)
        InitState.Ready -> PejalanNav(
            gemma = gemma,
            db = db,
            captureRoute = { CaptureRoute(gemma, db) },
        )
    }
}

private sealed interface CaptureState {
    object Camera : CaptureState
    data class Analyzing(val bitmap: Bitmap) : CaptureState
    data class Result(val bitmap: Bitmap, val classification: Classification) : CaptureState
    data class Saved(val laporan: Laporan) : CaptureState
}

@Composable
private fun CaptureRoute(gemma: GemmaClient, db: LaporanDb) {
    var state: CaptureState by remember { mutableStateOf(CaptureState.Camera) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    when (val s = state) {
        CaptureState.Camera -> CameraScreen(onCapture = { bitmap ->
            state = CaptureState.Analyzing(bitmap)
            scope.launch {
                val classification = try {
                    gemma.classify(bitmap)
                } catch (_: Exception) {
                    Classification.Fallback
                }
                state = CaptureState.Result(bitmap, classification)
            }
        })
        is CaptureState.Analyzing -> AnalyzingOverlay()
        is CaptureState.Result -> ResultSheet(
            classification = s.classification,
            onDismiss = { state = CaptureState.Camera },
            onConfirm = {
                scope.launch {
                    val saved = saveLaporan(context, db, s.bitmap, s.classification)
                    state = CaptureState.Saved(saved)
                }
            },
        )
        is CaptureState.Saved -> SavedScreen(
            laporan = s.laporan,
            todayCountFlow = db.laporanDao().observeCountSince(startOfToday()),
            onDone = { state = CaptureState.Camera },
        )
    }
}

private suspend fun saveLaporan(
    context: Context,
    db: LaporanDb,
    bitmap: Bitmap,
    classification: Classification,
): Laporan = withContext(Dispatchers.IO) {
    val dao = db.laporanDao()
    val now = System.currentTimeMillis()
    val cal = Calendar.getInstance().apply { timeInMillis = now }
    val dayOfYear = cal.get(Calendar.DAY_OF_YEAR)
    val countBefore = dao.totalCount()
    val id = "PJ-${dayOfYear.toString().padStart(3, '0')}-${(countBefore + 1).toString().padStart(4, '0')}"

    val photoDir = File(context.filesDir, "laporan").apply { mkdirs() }
    val photoFile = File(photoDir, "$id.jpg")
    FileOutputStream(photoFile).use { out ->
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, out)
    }

    // GPS stubbed at JL. Sabang for now; real location wiring comes later.
    val laporan = Laporan(
        id = id,
        createdAt = now,
        lat = -6.1768,
        lng = 106.8252,
        accuracyM = 0f,
        photoPath = photoFile.absolutePath,
        kategori = classification.kategori,
        severitas = classification.severitas,
        keyakinan = classification.keyakinan,
        rasional = classification.rasional,
        bboxX = classification.bbox.x,
        bboxY = classification.bbox.y,
        bboxW = classification.bbox.w,
        bboxH = classification.bbox.h,
        memoPath = null,
        userCorrected = false,
        syncedAt = null,
    )
    dao.insert(laporan)
    laporan
}

private fun startOfToday(): Long {
    val cal = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return cal.timeInMillis
}

@Composable
private fun SplashScreen(message: String, isError: Boolean = false) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (!isError) {
                CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            }
            Text(
                message,
                style = MaterialTheme.typography.bodyLarge,
                color = if (isError) MaterialTheme.colorScheme.error
                        else MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 24.dp),
            )
        }
    }
}
