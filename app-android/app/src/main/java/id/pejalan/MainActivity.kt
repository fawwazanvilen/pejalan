package id.pejalan

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.core.content.ContextCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import id.pejalan.data.Laporan
import id.pejalan.data.LaporanDb
import id.pejalan.data.LaporanStatus
import id.pejalan.ml.Classification
import id.pejalan.ml.ClassificationQueue
import id.pejalan.ml.GemmaClient
import id.pejalan.nav.PejalanNav
import id.pejalan.ui.camera.CameraScreen
import id.pejalan.ui.camera.CaptureMode
import id.pejalan.ui.camera.ConfirmPhotoOverlay
import id.pejalan.ui.result.AnalyzingOverlay
import id.pejalan.ui.result.ResultSheet
import id.pejalan.ui.saved.SavedScreen
import id.pejalan.ui.saving.SavingPhase
import id.pejalan.ui.saving.SavingScreen
import id.pejalan.ui.theme.PejalanTheme
import java.io.File
import java.io.FileOutputStream
import java.util.Calendar
import kotlin.coroutines.resume
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val app = pejalanApp
        setContent {
            PejalanTheme {
                PejalanApp(app.gemma, app.db, app.queue)
            }
        }
    }
}

private sealed interface InitState {
    object Loading : InitState
    object Ready : InitState
    data class Error(val message: String) : InitState
}

@Composable
private fun PejalanApp(gemma: GemmaClient, db: LaporanDb, queue: ClassificationQueue) {
    var initState by remember { mutableStateOf<InitState>(InitState.Loading) }

    LaunchedEffect(Unit) {
        initState = try {
            gemma.initialize()
            queue.start()
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
            captureRoute = { CaptureRoute(gemma, db, queue) },
        )
    }
}

private sealed interface CaptureState {
    object Camera : CaptureState
    // Mode Teliti only
    data class Analyzing(val bitmap: Bitmap) : CaptureState
    data class Result(val bitmap: Bitmap, val classification: Classification, val location: Location?) : CaptureState
    // Mode Cepat only
    data class ConfirmPhoto(val bitmap: Bitmap) : CaptureState
    // Shared
    data class Saving(
        val bitmap: Bitmap,
        val classification: Classification?, // null = Cepat (save as PENDING)
        val userCorrected: Boolean,
        val phase: SavingPhase,
    ) : CaptureState
    data class Saved(val laporan: Laporan) : CaptureState
}

@Composable
private fun CaptureRoute(gemma: GemmaClient, db: LaporanDb, queue: ClassificationQueue) {
    var state: CaptureState by remember { mutableStateOf(CaptureState.Camera) }
    var mode by remember { mutableStateOf(CaptureMode.Teliti) }
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val fusedClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val permLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
    ) { results ->
        val granted = results.values.any { it }
        val current = state
        if (current is CaptureState.Saving) {
            if (granted) {
                proceedFromPermission(
                    scope, context, db, queue, fusedClient, current,
                    setState = { state = it },
                )
            } else {
                state = current.copy(phase = SavingPhase.PermissionDenied)
            }
        }
    }

    // Pro-actively request location permission as soon as the Camera screen is shown.
    LaunchedEffect(state is CaptureState.Camera) {
        if (state is CaptureState.Camera && !hasLocationPermission(context)) {
            permLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )
        }
    }

    fun startSaveWithLocationFlow(
        bitmap: Bitmap,
        classification: Classification?,
        userCorrected: Boolean,
    ) {
        val saving = CaptureState.Saving(bitmap, classification, userCorrected, SavingPhase.Fetching)
        state = saving
        if (hasLocationPermission(context)) {
            proceedFromPermission(
                scope, context, db, queue, fusedClient, saving,
                setState = { state = it },
            )
        } else {
            permLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                )
            )
        }
    }

    fun saveOrPromptForLocation(
        bitmap: Bitmap,
        classification: Classification?,
        cachedLocation: Location?,
        userCorrected: Boolean,
    ) {
        if (cachedLocation != null) {
            scope.launch {
                val saved = saveLaporan(
                    context, db, bitmap, classification, userCorrected, cachedLocation,
                    status = if (classification == null) LaporanStatus.PENDING else LaporanStatus.CLASSIFIED,
                )
                if (classification == null) {
                    queue.enqueue()
                    Toast.makeText(context, "Antri diproses", Toast.LENGTH_SHORT).show()
                    state = CaptureState.Camera
                } else {
                    state = CaptureState.Saved(saved)
                }
            }
        } else {
            startSaveWithLocationFlow(bitmap, classification, userCorrected)
        }
    }

    fun retrySave() {
        val current = state
        if (current is CaptureState.Saving) {
            startSaveWithLocationFlow(current.bitmap, current.classification, current.userCorrected)
        }
    }

    when (val s = state) {
        CaptureState.Camera -> CameraScreen(
            mode = mode,
            onModeChange = { mode = it },
            onCapture = { bitmap ->
                // Both modes go through the confirmation overlay first.
                state = CaptureState.ConfirmPhoto(bitmap)
            },
        )
        is CaptureState.Analyzing -> AnalyzingOverlay()
        is CaptureState.Result -> ResultSheet(
            classification = s.classification,
            onDismiss = { state = CaptureState.Camera },
            onConfirm = {
                if (s.classification.kategori.isViolation) {
                    saveOrPromptForLocation(s.bitmap, s.classification, s.location, false)
                } else {
                    state = CaptureState.Camera
                }
            },
            onSaveAnyway = {
                saveOrPromptForLocation(s.bitmap, s.classification, s.location, true)
            },
        )
        is CaptureState.ConfirmPhoto -> ConfirmPhotoOverlay(
            bitmap = s.bitmap,
            onUse = {
                when (mode) {
                    CaptureMode.Teliti -> {
                        state = CaptureState.Analyzing(s.bitmap)
                        scope.launch {
                            val classificationJob = async {
                                try { gemma.classify(s.bitmap) } catch (_: Exception) { Classification.Fallback }
                            }
                            val locationJob = async {
                                if (hasLocationPermission(context)) fetchLocation(fusedClient) else null
                            }
                            state = CaptureState.Result(
                                bitmap = s.bitmap,
                                classification = classificationJob.await(),
                                location = locationJob.await(),
                            )
                        }
                    }
                    CaptureMode.Cepat -> {
                        scope.launch {
                            val location = if (hasLocationPermission(context)) fetchLocation(fusedClient) else null
                            saveOrPromptForLocation(s.bitmap, null, location, false)
                        }
                    }
                }
            },
            onRetake = { state = CaptureState.Camera },
        )
        is CaptureState.Saving -> SavingScreen(
            phase = s.phase,
            onRetry = { retrySave() },
            onCancel = { state = CaptureState.Camera },
        )
        is CaptureState.Saved -> SavedScreen(
            laporan = s.laporan,
            todayCountFlow = db.laporanDao().observeCountSince(startOfToday()),
            onDone = { state = CaptureState.Camera },
        )
    }
}

private fun proceedFromPermission(
    scope: CoroutineScope,
    context: Context,
    db: LaporanDb,
    queue: ClassificationQueue,
    fusedClient: FusedLocationProviderClient,
    saving: CaptureState.Saving,
    setState: (CaptureState) -> Unit,
) {
    scope.launch {
        val location = fetchLocation(fusedClient)
        if (location == null) {
            setState(saving.copy(phase = SavingPhase.LocationTimeout))
        } else {
            val status = if (saving.classification == null) LaporanStatus.PENDING else LaporanStatus.CLASSIFIED
            val saved = saveLaporan(
                context, db, saving.bitmap, saving.classification,
                saving.userCorrected, location, status,
            )
            if (saving.classification == null) {
                queue.enqueue()
                Toast.makeText(context, "Antri diproses", Toast.LENGTH_SHORT).show()
                setState(CaptureState.Camera)
            } else {
                setState(CaptureState.Saved(saved))
            }
        }
    }
}

private fun hasLocationPermission(context: Context): Boolean =
    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED ||
    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED

@SuppressLint("MissingPermission")
private suspend fun fetchLocation(client: FusedLocationProviderClient): Location? =
    withTimeoutOrNull(5000L) {
        suspendCancellableCoroutine<Location?> { cont ->
            val token = CancellationTokenSource()
            client.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, token.token)
                .addOnSuccessListener { location ->
                    if (cont.isActive) cont.resume(location)
                }
                .addOnFailureListener { _ ->
                    if (cont.isActive) cont.resume(null)
                }
            cont.invokeOnCancellation { token.cancel() }
        }
    }

private suspend fun saveLaporan(
    context: Context,
    db: LaporanDb,
    bitmap: Bitmap,
    classification: Classification?, // null = pending (Mode Cepat)
    userCorrected: Boolean,
    location: Location,
    status: LaporanStatus,
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

    val effective = classification ?: Classification.Fallback
    val laporan = Laporan(
        id = id,
        createdAt = now,
        lat = location.latitude,
        lng = location.longitude,
        accuracyM = location.accuracy,
        photoPath = photoFile.absolutePath,
        kategori = effective.kategori,
        severitas = effective.severitas,
        keyakinan = effective.keyakinan,
        walkability = effective.walkability,
        rasional = effective.rasional,
        bboxX = effective.bbox.x,
        bboxY = effective.bbox.y,
        bboxW = effective.bbox.w,
        bboxH = effective.bbox.h,
        memoPath = null,
        userCorrected = userCorrected,
        syncedAt = null,
        status = status,
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
