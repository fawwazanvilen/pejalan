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
import id.pejalan.ui.theme.Ink
import id.pejalan.ui.theme.PaperHi
import kotlinx.coroutines.delay

private data class Step(val label: String)

private val Steps = listOf(
    Step("DETEKSI OBJEK"),
    Step("KLASIFIKASI KATEGORI"),
    Step("PENILAIAN SEVERITAS"),
    Step("MENULIS RASIONAL"),
)

/**
 * Midfi-style analyzing overlay. Pure aesthetic — the step progression is
 * driven by a fake timer, not real Gemma callbacks. Cycles ~7s total, which
 * roughly matches typical inference time on a Pixel 7 Pro for this prompt.
 */
@Composable
fun AnalyzingOverlay(bitmap: android.graphics.Bitmap? = null) {
    var stepIndex by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        // Roughly 1.8s per step. Holds at the last step until classification
        // actually completes and the parent swaps us out.
        while (stepIndex < Steps.size - 1) {
            delay(1800L)
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
                    "STEP ${(stepIndex + 1).toString().padStart(2, '0')} / 04 — ${Steps[stepIndex].label}",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.White.copy(alpha = 0.7f),
                    letterSpacing = 2.sp,
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    "Membaca trotoar.",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = Color.White,
                    fontSize = 32.sp,
                    lineHeight = 36.sp,
                    letterSpacing = (-0.6).sp,
                )
                Text(
                    "Gemma 4 berjalan di perangkat…",
                    style = MaterialTheme.typography.titleLarge,
                    color = HiVis,
                    fontWeight = FontWeight.SemiBold,
                )

                Spacer(Modifier.height(20.dp))

                // Progress bar
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

                // Step list
                Steps.forEachIndexed { i, step ->
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
                            fontSize = 11.sp,
                            color = color,
                            fontWeight = FontWeight.Bold,
                        )
                        Spacer(Modifier.width(10.dp))
                        Text(
                            step.label,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 11.sp,
                            color = color,
                            letterSpacing = 1.4.sp,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }

            // Top "live" indicator
            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(start = 28.dp, top = 56.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                LiveDot()
                Spacer(Modifier.width(8.dp))
                Text(
                    "GEMMA 4 · ON-DEVICE",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 10.sp,
                    color = Color.White,
                    letterSpacing = 1.6.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun LiveDot() {
    // Animated blink — opacity oscillates fast.
    val alpha by animateFloatAsState(
        targetValue = 1f,
        animationSpec = tween(durationMillis = 400, easing = LinearEasing),
        label = "blink",
    )
    Box(
        modifier = Modifier
            .size(6.dp)
            .background(HiVis.copy(alpha = alpha)),
    )
}
