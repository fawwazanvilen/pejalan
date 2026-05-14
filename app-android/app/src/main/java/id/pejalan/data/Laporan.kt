package id.pejalan.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import id.pejalan.ml.Kategori
import id.pejalan.ml.Severitas

@Entity(tableName = "laporan")
data class Laporan(
    @PrimaryKey val id: String,
    val createdAt: Long,
    val lat: Double,
    val lng: Double,
    val accuracyM: Float,
    val photoPath: String,
    val kategori: Set<Kategori>,
    val severitas: Severitas,
    val keyakinan: Float,
    val walkability: Int,
    val rasional: String,
    val bboxX: Float,
    val bboxY: Float,
    val bboxW: Float,
    val bboxH: Float,
    val memoPath: String?,
    val userCorrected: Boolean,
    val syncedAt: Long?,
    val status: LaporanStatus = LaporanStatus.CLASSIFIED,
)
