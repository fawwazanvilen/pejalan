package id.pejalan

import android.app.Application
import android.content.Context
import com.mapbox.common.MapboxOptions
import id.pejalan.data.LaporanDb
import id.pejalan.ml.ClassificationQueue
import id.pejalan.ml.GemmaClient

class PejalanApplication : Application() {

    val gemma: GemmaClient by lazy { GemmaClient(applicationContext) }
    val db: LaporanDb by lazy { LaporanDb.getInstance(applicationContext) }
    val queue: ClassificationQueue by lazy { ClassificationQueue(db, gemma) }

    override fun onCreate() {
        super.onCreate()
        MapboxOptions.accessToken = BuildConfig.MAPBOX_ACCESS_TOKEN
    }
}

val Context.pejalanApp: PejalanApplication
    get() = applicationContext as PejalanApplication
