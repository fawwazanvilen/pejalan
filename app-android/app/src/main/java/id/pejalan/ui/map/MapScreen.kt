package id.pejalan.ui.map

import android.widget.Toast
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import com.mapbox.maps.extension.compose.annotation.generated.CircleAnnotation
import id.pejalan.data.Laporan
import id.pejalan.data.LaporanDb
import id.pejalan.data.SeedData
import id.pejalan.ml.Severitas
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
            center(Point.fromLngLat(106.8456, -6.2088)) // Jakarta center
            pitch(0.0)
            bearing(0.0)
        }
    }

    MapboxMap(
        modifier = Modifier.fillMaxSize(),
        mapViewportState = viewportState,
    ) {
        markers.forEach { laporan ->
            CircleAnnotation(
                point = Point.fromLngLat(laporan.lng, laporan.lat),
            ) {
                circleRadius = 9.0
                circleColor = markerColor(laporan)
                circleStrokeColor = Color.White
                circleStrokeWidth = 2.5

                interactionsState.onClicked {
                    val severityPart =
                        if (laporan.kategori.isViolation) " · ${laporan.severitas.label}" else ""
                    Toast.makeText(
                        context,
                        "${laporan.kategori.label}$severityPart",
                        Toast.LENGTH_SHORT,
                    ).show()
                    true
                }
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
