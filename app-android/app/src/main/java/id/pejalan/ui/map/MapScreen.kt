package id.pejalan.ui.map

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mapbox.geojson.Point
import com.mapbox.maps.extension.compose.MapboxMap
import com.mapbox.maps.extension.compose.animation.viewport.rememberMapViewportState
import id.pejalan.data.LaporanDb

@Composable
fun MapScreen(db: LaporanDb) {
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
    )
}
