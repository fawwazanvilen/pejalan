package id.pejalan

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
import androidx.compose.ui.unit.dp
import id.pejalan.ml.Classification
import id.pejalan.ml.GemmaClient
import id.pejalan.ui.camera.CameraScreen
import id.pejalan.ui.result.AnalyzingOverlay
import id.pejalan.ui.result.ResultSheet
import id.pejalan.ui.theme.PejalanTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var gemma: GemmaClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        gemma = GemmaClient(applicationContext)
        setContent {
            PejalanTheme {
                PejalanApp(gemma)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gemma.close()
    }
}

private sealed interface AppState {
    object Camera : AppState
    data class Analyzing(val bitmap: Bitmap) : AppState
    data class Result(val bitmap: Bitmap, val classification: Classification) : AppState
}

@Composable
private fun PejalanApp(gemma: GemmaClient) {
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
        InitState.Ready -> ReadyApp(gemma)
    }
}

private sealed interface InitState {
    object Loading : InitState
    object Ready : InitState
    data class Error(val message: String) : InitState
}

@Composable
private fun ReadyApp(gemma: GemmaClient) {
    var state: AppState by remember { mutableStateOf(AppState.Camera) }
    val scope = rememberCoroutineScope()

    when (val s = state) {
        AppState.Camera -> CameraScreen(onCapture = { bitmap ->
            state = AppState.Analyzing(bitmap)
            scope.launch {
                val classification = try {
                    gemma.classify(bitmap)
                } catch (_: Exception) {
                    Classification.Fallback
                }
                state = AppState.Result(bitmap, classification)
            }
        })
        is AppState.Analyzing -> AnalyzingOverlay()
        is AppState.Result -> ResultSheet(
            classification = s.classification,
            onDismiss = { state = AppState.Camera },
            onConfirm = { state = AppState.Camera },
        )
    }
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
