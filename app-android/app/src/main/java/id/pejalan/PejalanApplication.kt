package id.pejalan

import android.app.Application
import com.mapbox.common.MapboxOptions

class PejalanApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Set Mapbox access token before any MapView is inflated.
        MapboxOptions.accessToken = BuildConfig.MAPBOX_ACCESS_TOKEN
    }
}
