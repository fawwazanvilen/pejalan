package id.pejalan.ui.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotationGroup
import com.mapbox.maps.extension.style.expressions.dsl.generated.literal
import com.mapbox.maps.plugin.annotation.AnnotationConfig
import com.mapbox.maps.plugin.annotation.AnnotationSourceOptions
import com.mapbox.maps.plugin.annotation.ClusterOptions
import com.mapbox.maps.plugin.annotation.generated.CircleAnnotationOptions
import id.pejalan.data.Laporan
import id.pejalan.data.LaporanDb
import id.pejalan.data.LaporanStatus
import id.pejalan.data.SeedData
import id.pejalan.ml.Kategori
import id.pejalan.ml.Severitas
import id.pejalan.ml.isViolation
import id.pejalan.ml.primary
import id.pejalan.ui.common.WalkabilityBar
import id.pejalan.ui.theme.Indigo
import id.pejalan.ui.theme.IndigoTint
import id.pejalan.ui.theme.Ink
import id.pejalan.ui.theme.Mute
import id.pejalan.ui.theme.PaperHi
import id.pejalan.ui.theme.SevRendah
import id.pejalan.ui.theme.SevSedang
import id.pejalan.ui.theme.SevTinggi
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(db: LaporanDb, onOpenDetail: (String) -> Unit) {
    val real by db.laporanDao().observeAll().collectAsState(initial = emptyList())
    val seed = remember { SeedData.entries() }
    val markers = remember(real) {
        (real + seed).filter { it.lat != 0.0 || it.lng != 0.0 }
    }

    var selected: Laporan? by remember { mutableStateOf(null) }

    val viewportState = rememberMapViewportState {
        setCameraOptions {
            zoom(11.5)
            center(Point.fromLngLat(106.8456, -6.2088))
            pitch(0.0)
            bearing(0.0)
        }
    }

    val annotations = remember(markers) {
        markers.map { laporan ->
            CircleAnnotationOptions()
                .withPoint(Point.fromLngLat(laporan.lng, laporan.lat))
                .withCircleRadius(9.0)
                .withCircleColor(markerColor(laporan).toArgb())
                .withCircleStrokeColor(Color.White.toArgb())
                .withCircleStrokeWidth(2.5)
        }
    }

    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = viewportState,
    ) {
        CircleAnnotationGroup(
            annotations = annotations,
            annotationConfig = AnnotationConfig(
                annotationSourceOptions = AnnotationSourceOptions(
                    clusterOptions = ClusterOptions(
                        clusterRadius = 25,
                        clusterMaxZoom = 13,
                        textColor = Indigo.toArgb(),
                        textSize = 13.0,
                        circleRadiusExpression = literal(16.0),
                        colorLevels = listOf(0 to IndigoTint.toArgb()),
                    ),
                ),
            ),
        ) {
            interactionsState.onClicked { annotation ->
                val nearest = markers.minByOrNull { l ->
                    val p = annotation.point
                    val dLat = l.lat - p.latitude()
                    val dLng = l.lng - p.longitude()
                    dLat * dLat + dLng * dLng
                }
                if (nearest != null) selected = nearest
                true
            }
            interactionsState.onClusterClicked {
                // No-op: the cluster is dense enough that a sheet doesn't make sense;
                // user can pinch-zoom to see individual markers (clusters disappear at zoom > 13).
                true
            }
        }
    }

    val current = selected
    if (current != null) {
        LaporanDetailSheet(
            laporan = current,
            onDismiss = { selected = null },
            onOpenDetail = {
                selected = null
                onOpenDetail(current.id)
            },
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LaporanDetailSheet(
    laporan: Laporan,
    onDismiss: () -> Unit,
    onOpenDetail: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = PaperHi,
        contentColor = Ink,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
        ) {
            // Audit code header — matches Linimasa card and ResultSheet
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    laporan.id,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Mute,
                    letterSpacing = 0.8.sp,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    relativeTime(laporan.createdAt),
                    style = MaterialTheme.typography.labelSmall,
                    color = Mute,
                )
                Spacer(Modifier.weight(1f))
                if (laporan.kategori.any { it.isViolation }) {
                    Box(
                        modifier = Modifier
                            .background(severityColor(laporan.severitas))
                            .padding(horizontal = 8.dp, vertical = 3.dp),
                    ) {
                        Text(
                            laporan.severitas.label,
                            color = Color.White,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                    }
                }
            }
            Box(modifier = Modifier.fillMaxWidth().height(1.5.dp).background(Ink))
            Spacer(Modifier.height(18.dp))

            Row(verticalAlignment = Alignment.Top) {
                DetailThumbnail(
                    photoPath = laporan.photoPath,
                    fallbackTint = markerColor(laporan),
                    label = laporan.kategori.primary.label,
                )
                Spacer(Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    val ordered = Kategori.entries.filter { it in laporan.kategori }
                    Text(
                        ordered.joinToString("\n") { displayName(it) },
                        fontSize = 24.sp,
                        lineHeight = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp,
                        color = Ink,
                    )
                    if (laporan.walkability > 0) {
                        Spacer(Modifier.height(10.dp))
                        WalkabilityBar(score = laporan.walkability, showLabel = true)
                    }
                }
            }

            if (laporan.rasional.isNotBlank()) {
                Spacer(Modifier.height(20.dp))
                Text(
                    laporan.rasional,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = Ink,
                    ),
                )
            }

            Spacer(Modifier.height(24.dp))

            Button(
                onClick = onOpenDetail,
                modifier = Modifier.fillMaxWidth().height(52.dp),
                shape = RoundedCornerShape(2.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Ink,
                    contentColor = PaperHi,
                ),
            ) {
                Text("Lihat & sunting", style = MaterialTheme.typography.labelLarge)
            }
            Spacer(Modifier.height(4.dp))
            TextButton(
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    "Tutup",
                    color = Mute,
                    style = MaterialTheme.typography.labelLarge,
                )
            }
        }
    }
}

private fun displayName(kategori: id.pejalan.ml.Kategori): String = when (kategori) {
    id.pejalan.ml.Kategori.PARKIR_LIAR -> "parkir liar."
    id.pejalan.ml.Kategori.TROTOAR_RUSAK -> "trotoar rusak."
    id.pejalan.ml.Kategori.HALANGAN_PERMANEN -> "halangan permanen."
    id.pejalan.ml.Kategori.UBIN_DIFABEL_BERMASALAH -> "ubin difabel bermasalah."
    id.pejalan.ml.Kategori.TROTOAR_ABSEN -> "trotoar absen."
    id.pejalan.ml.Kategori.DRAINASE -> "drainase."
    id.pejalan.ml.Kategori.NIHIL -> "tidak ada pelanggaran."
    id.pejalan.ml.Kategori.BUKAN_TROTOAR -> "bukan trotoar."
    id.pejalan.ml.Kategori.LAINNYA -> "lainnya."
}

@Composable
private fun DetailThumbnail(photoPath: String, fallbackTint: Color, label: String) {
    val hasFile = photoPath.isNotEmpty() && File(photoPath).exists()
    Box(
        modifier = Modifier
            .size(88.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(fallbackTint),
        contentAlignment = Alignment.Center,
    ) {
        if (hasFile) {
            AsyncImage(
                model = File(photoPath),
                contentDescription = label,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Text(
                label.take(1).uppercase(),
                color = Color.White,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun SeverityChipSmall(severity: Severitas) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(severityColor(severity))
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            severity.label,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

private fun markerColor(laporan: Laporan): Color {
    if (laporan.status != LaporanStatus.CLASSIFIED) return Mute
    if (!laporan.kategori.isViolation) return Mute
    return when (laporan.severitas) {
        Severitas.RENDAH -> SevRendah
        Severitas.SEDANG -> SevSedang
        Severitas.TINGGI -> SevTinggi
    }
}

private fun severityColor(s: Severitas) = when (s) {
    Severitas.RENDAH -> SevRendah
    Severitas.SEDANG -> SevSedang
    Severitas.TINGGI -> SevTinggi
}

private fun relativeTime(
    ms: Long,
    now: Long = System.currentTimeMillis(),
): String {
    val diff = (now - ms).coerceAtLeast(0)
    val minutes = diff / 60_000
    val hours = diff / 3_600_000
    val days = diff / 86_400_000
    return when {
        minutes < 1 -> "Baru saja"
        minutes < 60 -> "$minutes menit lalu"
        hours < 24 -> "$hours jam lalu"
        days < 2 -> "Kemarin"
        days < 7 -> "$days hari lalu"
        else -> "${days / 7} minggu lalu"
    }
}
