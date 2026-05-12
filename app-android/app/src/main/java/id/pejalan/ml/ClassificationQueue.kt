package id.pejalan.ml

import android.graphics.BitmapFactory
import android.util.Log
import id.pejalan.data.LaporanDb
import id.pejalan.data.LaporanStatus
import java.util.concurrent.atomic.AtomicBoolean
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

/**
 * In-app background queue that drains PENDING laporan from Room, classifies their photos
 * with Gemma, and updates the row in place.
 *
 * Started once after [GemmaClient.initialize] succeeds. Subsequent enqueues just signal
 * the loop to drain again (it processes everything currently pending each wake-up).
 */
class ClassificationQueue(
    private val db: LaporanDb,
    private val gemma: GemmaClient,
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val wakeUp = MutableSharedFlow<Unit>(extraBufferCapacity = 32)
    private val started = AtomicBoolean(false)

    fun start() {
        if (!started.compareAndSet(false, true)) return
        scope.launch { runLoop() }
    }

    fun enqueue() {
        scope.launch { wakeUp.emit(Unit) }
    }

    private suspend fun runLoop() {
        // Drain anything that was left PENDING from a previous app session.
        processAllPending()
        wakeUp.collect {
            processAllPending()
        }
    }

    private suspend fun processAllPending() {
        val dao = db.laporanDao()
        while (true) {
            val pending = dao.findOnePending() ?: return
            try {
                val bitmap = BitmapFactory.decodeFile(pending.photoPath)
                    ?: error("Could not decode photo at ${pending.photoPath}")
                val classification = gemma.classify(bitmap)
                bitmap.recycle()
                dao.updateClassification(
                    id = pending.id,
                    kategori = classification.kategori,
                    severitas = classification.severitas,
                    keyakinan = classification.keyakinan,
                    walkability = classification.walkability,
                    rasional = classification.rasional,
                    status = LaporanStatus.CLASSIFIED,
                )
                Log.d(TAG, "Classified ${pending.id} as ${classification.kategori}")
            } catch (e: Exception) {
                Log.e(TAG, "Failed to classify ${pending.id}", e)
                dao.updateStatus(pending.id, LaporanStatus.FAILED)
            }
        }
    }

    companion object {
        private const val TAG = "PejalanQueue"
    }
}
