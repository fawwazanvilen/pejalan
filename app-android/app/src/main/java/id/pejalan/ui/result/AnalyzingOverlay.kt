package id.pejalan.ui.result

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.pejalan.ui.theme.HiVis
import kotlin.random.Random
import kotlinx.coroutines.delay

private val Steps = listOf(
    "Deteksi objek",
    "Klasifikasi kategori",
    "Penilaian severitas",
    "Menulis rationale",
)

@Composable
fun AnalyzingOverlay(bitmap: android.graphics.Bitmap? = null) {
    var stepIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        // Each step takes 2.5–4 seconds with jitter so the cadence doesn't feel
        // mechanical. The labels are aesthetic — the actual classification work
        // happens in parallel and may finish before or after the timer.
        while (stepIndex < Steps.size - 1) {
            delay(Random.nextLong(2500L, 4000L))
            stepIndex++
        }
    }
    val progress = ((stepIndex + 1).toFloat() / Steps.size).coerceIn(0f, 1f)
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        animationSpec = tween(durationMillis = 600, easing = LinearEasing),
        label = "progress",
    )

    Surface(modifier = Modifier.fillMaxSize(), color = Color.Black) {
        Box(modifier = Modifier.fillMaxSize()) {
            if (bitmap != null) {
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop,
                    alpha = 0.35f,
                )
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.CenterStart)
                    .padding(horizontal = 28.dp),
            ) {
                Text(
                    "Gemma 4 sedang berjalan di perangkat",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.75f),
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    "Membaca trotoar.",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    fontSize = 32.sp,
                    lineHeight = 36.sp,
                    letterSpacing = (-0.5).sp,
                )
                Text(
                    "Tahap ${stepIndex + 1} dari ${Steps.size}: ${Steps[stepIndex]}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = HiVis,
                    fontWeight = FontWeight.SemiBold,
                )

                Spacer(Modifier.height(20.dp))

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .background(Color.White.copy(alpha = 0.18f)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedProgress)
                            .background(HiVis),
                    )
                }

                Spacer(Modifier.height(18.dp))

                Steps.forEachIndexed { i, label ->
                    val symbol = when {
                        i < stepIndex -> "✓"
                        i == stepIndex -> "→"
                        else -> "◯"
                    }
                    val color = when {
                        i < stepIndex -> Color.White.copy(alpha = 0.8f)
                        i == stepIndex -> HiVis
                        else -> Color.White.copy(alpha = 0.35f)
                    }
                    Row(
                        modifier = Modifier.padding(vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            symbol,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 12.sp,
                            color = color,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            label,
                            style = MaterialTheme.typography.bodyMedium,
                            color = color,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }
        }
    }
}
