package id.pejalan.ui.map

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
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
import id.pejalan.data.SeedData
import id.pejalan.ml.Severitas
import id.pejalan.ui.theme.Indigo
import id.pejalan.ui.theme.IndigoInk
import id.pejalan.ui.theme.Mute
import id.pejalan.ui.theme.SevRendah
import id.pejalan.ui.theme.SevSedang
import id.pejalan.ui.theme.SevTinggi

@Composable
fun MapScreen(db: LaporanDb) {
    val real by db.laporanDao().observeAll().collectAsState(initial = emptyList())
    val seed = remember { SeedData.entries() }
    val markers = remember(real) {
        (real + seed).filter { it.lat != 0.0 || it.lng != 0.0 }
    }
    val context = LocalContext.current

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
                        textColor = Color.White.toArgb(),
                        textSize = 14.0,
                        circleRadiusExpression = literal(22.0),
                        colorLevels = listOf(
                            10 to IndigoInk.toArgb(),
                            0 to Indigo.toArgb(),
                        ),
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
                if (nearest != null) {
                    val severityPart =
                        if (nearest.kategori.isViolation) " · ${nearest.severitas.label}" else ""
                    Toast.makeText(
                        context,
                        "${nearest.kategori.label}$severityPart",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
                true
            }
            interactionsState.onClusterClicked { cluster ->
                Toast.makeText(
                    context,
                    "${cluster.pointCount} laporan di sini",
                    Toast.LENGTH_SHORT,
                ).show()
                true
            }
        }
    }
}

private fun markerColor(laporan: Laporan): Color {
    if (!laporan.kategori.isViolation) return Mute
    return when (laporan.severitas) {
        Severitas.RENDAH -> SevRendah
        Severitas.SEDANG -> SevSedang
        Severitas.TINGGI -> SevTinggi
    }
}
