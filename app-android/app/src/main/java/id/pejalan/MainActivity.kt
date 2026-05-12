package id.pejalan

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
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
import id.pejalan.ml.Classification
import id.pejalan.ml.GemmaClient
import id.pejalan.nav.PejalanNav
import id.pejalan.ui.camera.CameraScreen
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
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

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
    data class Saving(
        val bitmap: Bitmap,
        val classification: Classification,
        val userCorrected: Boolean,
        val phase: SavingPhase,
    ) : CaptureState
    data class Saved(val laporan: Laporan) : CaptureState
}

@Composable
private fun CaptureRoute(gemma: GemmaClient, db: LaporanDb) {
    var state: CaptureState by remember { mutableStateOf(CaptureState.Camera) }
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
                    scope, context, db, fusedClient, current,
                    setState = { state = it },
                )
            } else {
                state = current.copy(phase = SavingPhase.PermissionDenied)
            }
        }
    }

    fun startSave(bitmap: Bitmap, classification: Classification, userCorrected: Boolean) {
        val saving = CaptureState.Saving(bitmap, classification, userCorrected, SavingPhase.Fetching)
        state = saving
        if (hasLocationPermission(context)) {
            proceedFromPermission(
                scope, context, db, fusedClient, saving,
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

    fun retrySave() {
        val current = state
        if (current is CaptureState.Saving) {
            startSave(current.bitmap, current.classification, current.userCorrected)
        }
    }

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
                if (s.classification.kategori.isViolation) {
                    startSave(s.bitmap, s.classification, userCorrected = false)
                } else {
                    state = CaptureState.Camera
                }
            },
            onSaveAnyway = {
                startSave(s.bitmap, s.classification, userCorrected = true)
            },
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
    fusedClient: FusedLocationProviderClient,
    saving: CaptureState.Saving,
    setState: (CaptureState) -> Unit,
) {
    scope.launch {
        val location = fetchLocation(fusedClient)
        if (location == null) {
            setState(saving.copy(phase = SavingPhase.LocationTimeout))
        } else {
            val saved = saveLaporan(
                context, db, saving.bitmap, saving.classification,
                saving.userCorrected, location,
            )
            setState(CaptureState.Saved(saved))
        }
    }
}

private fun hasLocationPermission(context: Context): Boolean =
    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED ||
    ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) ==
        PackageManager.PERMISSION_GRANTED

@SuppressLint("MissingPermission") // call site has already verified permission
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
    classification: Classification,
    userCorrected: Boolean,
    location: Location,
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

    val laporan = Laporan(
        id = id,
        createdAt = now,
        lat = location.latitude,
        lng = location.longitude,
        accuracyM = location.accuracy,
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
        userCorrected = userCorrected,
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
