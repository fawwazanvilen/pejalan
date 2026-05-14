package id.pejalan.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import id.pejalan.ui.theme.HiVis
import id.pejalan.ui.theme.Ink

@Composable
fun CameraScreen(
    mode: CaptureMode,
    onModeChange: (CaptureMode) -> Unit,
    onCapture: (Bitmap) -> Unit,
    gemmaReady: Boolean = true,
    onShutterBlocked: () -> Unit = {},
) {
    val context = LocalContext.current

    var hasPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED
        )
    }
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted -> hasPermission = granted }

    if (!hasPermission) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                "Pejalan butuh akses kamera untuk memotret pelanggaran trotoar.",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Button(
                onClick = { permissionLauncher.launch(Manifest.permission.CAMERA) },
                modifier = Modifier.padding(top = 24.dp),
            ) {
                Text("Beri izin kamera")
            }
        }
        return
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    val controller = remember {
        LifecycleCameraController(context).apply {
            setEnabledUseCases(LifecycleCameraController.IMAGE_CAPTURE)
        }
    }
    DisposableEffect(lifecycleOwner) {
        controller.bindToLifecycle(lifecycleOwner)
        onDispose { controller.unbind() }
    }

    Box(modifier = Modifier.fillMaxSize().background(Color.Black)) {
        AndroidView(
            factory = { ctx ->
                PreviewView(ctx).apply {
                    this.controller = controller
                    scaleType = PreviewView.ScaleType.FILL_CENTER
                }
            },
            modifier = Modifier.fillMaxSize(),
        )

        // Viewfinder reticle (HiVis corner brackets + center crosshair)
        ViewfinderReticle(modifier = Modifier.fillMaxSize())

        // Top hint + mode toggle stack
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 48.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            TopHint(mode)
            androidx.compose.foundation.layout.Spacer(Modifier.size(12.dp))
            ModeToggle(mode = mode, onChange = onModeChange)
        }

        // Status pill: model loading state
        if (!gemmaReady && mode == CaptureMode.Teliti) {
            ModelLoadingPill(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 160.dp),
            )
        }

        Shutter(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp),
            dimmed = !gemmaReady && mode == CaptureMode.Teliti,
            onClick = {
                if (!gemmaReady && mode == CaptureMode.Teliti) {
                    onShutterBlocked()
                    return@Shutter
                }
                val executor = ContextCompat.getMainExecutor(context)
                controller.takePicture(
                    executor,
                    object : ImageCapture.OnImageCapturedCallback() {
                        override fun onCaptureSuccess(image: ImageProxy) {
                            val rotated = image.toBitmap()
                                .rotated(image.imageInfo.rotationDegrees)
                            image.close()
                            onCapture(rotated)
                        }
                        override fun onError(exception: ImageCaptureException) {
                            // Silent fail for now; CameraX logs the exception itself.
                        }
                    }
                )
            },
        )
    }
}

@Composable
private fun ViewfinderReticle(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val w = size.width
        val h = size.height
        val bracketLen = 30.dp.toPx()
        val inset = 20.dp.toPx()
        val topInset = 130.dp.toPx()  // make room for hint + toggle
        val bottomInset = 200.dp.toPx() // make room for shutter
        val strokeW = 2.5.dp.toPx()

        // Top-left
        drawLine(HiVis, Offset(inset, topInset), Offset(inset + bracketLen, topInset), strokeW, StrokeCap.Square)
        drawLine(HiVis, Offset(inset, topInset), Offset(inset, topInset + bracketLen), strokeW, StrokeCap.Square)
        // Top-right
        drawLine(HiVis, Offset(w - inset, topInset), Offset(w - inset - bracketLen, topInset), strokeW, StrokeCap.Square)
        drawLine(HiVis, Offset(w - inset, topInset), Offset(w - inset, topInset + bracketLen), strokeW, StrokeCap.Square)
        // Bottom-left
        drawLine(HiVis, Offset(inset, h - bottomInset), Offset(inset + bracketLen, h - bottomInset), strokeW, StrokeCap.Square)
        drawLine(HiVis, Offset(inset, h - bottomInset), Offset(inset, h - bottomInset - bracketLen), strokeW, StrokeCap.Square)
        // Bottom-right
        drawLine(HiVis, Offset(w - inset, h - bottomInset), Offset(w - inset - bracketLen, h - bottomInset), strokeW, StrokeCap.Square)
        drawLine(HiVis, Offset(w - inset, h - bottomInset), Offset(w - inset, h - bottomInset - bracketLen), strokeW, StrokeCap.Square)

        // Center crosshair
        val cx = w / 2f
        val cy = h / 2f - 50.dp.toPx() // shift up slightly
        val cross = 10.dp.toPx()
        val crossStroke = 1.2.dp.toPx()
        val white = Color.White.copy(alpha = 0.7f)
        drawLine(white, Offset(cx, cy - cross), Offset(cx, cy + cross), crossStroke)
        drawLine(white, Offset(cx - cross, cy), Offset(cx + cross, cy), crossStroke)
    }
}

@Composable
private fun TopHint(mode: CaptureMode) {
    val (top, sub) = when (mode) {
        CaptureMode.Teliti -> "Arahkan ke trotoar yang bermasalah." to "JARAK 1–3 METER · CAHAYA CUKUP"
        CaptureMode.Cepat -> "Bidik cepat, klasifikasi belakangan." to "FOTO LANGSUNG MASUK ANTRIAN"
    }
    Column(
        modifier = Modifier
            .background(Color.Black.copy(alpha = 0.65f))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            top,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyMedium,
        )
        Text(
            sub,
            color = HiVis,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.4.sp,
        )
    }
}

@Composable
private fun ModelLoadingPill(modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.75f))
            .padding(horizontal = 14.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(12.dp),
            strokeWidth = 2.dp,
            color = HiVis,
        )
        androidx.compose.foundation.layout.Spacer(Modifier.size(10.dp))
        Text(
            "GEMMA 4 BELUM SIAP · COBA MODE CEPAT",
            color = Color.White,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.4.sp,
        )
    }
}

@Composable
private fun ModeToggle(
    mode: CaptureMode,
    onChange: (CaptureMode) -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(24.dp))
            .background(Color.Black.copy(alpha = 0.55f))
            .padding(4.dp),
    ) {
        CaptureMode.entries.forEach { entry ->
            ModeChip(
                label = entry.label,
                selected = mode == entry,
                onClick = { onChange(entry) },
            )
        }
    }
}

@Composable
private fun ModeChip(label: String, selected: Boolean, onClick: () -> Unit) {
    val bg = if (selected) Color.White else Color.Transparent
    val fg = if (selected) Ink else Color.White
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bg)
            .clickable { onClick() }
            .padding(horizontal = 18.dp, vertical = 8.dp),
    ) {
        Text(
            label,
            color = fg,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
        )
    }
}

@Composable
private fun Shutter(modifier: Modifier = Modifier, dimmed: Boolean = false, onClick: () -> Unit) {
    val ringColor = if (dimmed) Color.White.copy(alpha = 0.45f) else Color.White
    val fillColor = if (dimmed) Color.White.copy(alpha = 0.35f) else Color.White
    Box(
        modifier = modifier
            .size(84.dp)
            .clip(CircleShape)
            .border(4.dp, ringColor, CircleShape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(fillColor),
        )
    }
}

private fun Bitmap.rotated(degrees: Int): Bitmap {
    if (degrees == 0) return this
    val m = Matrix().apply { postRotate(degrees.toFloat()) }
    return Bitmap.createBitmap(this, 0, 0, width, height, m, true)
}
