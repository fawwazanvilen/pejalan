package id.pejalan.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import id.pejalan.ml.Kategori
import id.pejalan.ml.Severitas
import kotlinx.coroutines.flow.Flow

@Dao
interface LaporanDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(laporan: Laporan)

    @Query("SELECT COUNT(*) FROM laporan")
    suspend fun totalCount(): Int

    @Query("SELECT COUNT(*) FROM laporan WHERE createdAt >= :startOfDayMs")
    fun observeCountSince(startOfDayMs: Long): Flow<Int>

    @Query("SELECT * FROM laporan ORDER BY createdAt DESC")
    fun observeAll(): Flow<List<Laporan>>

    @Query("SELECT * FROM laporan WHERE status = 'PENDING' ORDER BY createdAt ASC LIMIT 1")
    suspend fun findOnePending(): Laporan?

    @Query("UPDATE laporan SET kategori = :kategori, severitas = :severitas, " +
        "keyakinan = :keyakinan, walkability = :walkability, rasional = :rasional, " +
        "status = :status WHERE id = :id")
    suspend fun updateClassification(
        id: String,
        kategori: Kategori,
        severitas: Severitas,
        keyakinan: Float,
        walkability: Int,
        rasional: String,
        status: LaporanStatus,
    )

    @Query("UPDATE laporan SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: String, status: LaporanStatus)

    @Query("SELECT * FROM laporan WHERE id = :id")
    fun observeById(id: String): Flow<Laporan?>

    @Query(
        "UPDATE laporan SET " +
            "kategori = :kategori, " +
            "severitas = :severitas, " +
            "walkability = :walkability, " +
            "rasional = :rasional, " +
            "userCorrected = 1, " +
            "status = 'CLASSIFIED' " +
            "WHERE id = :id"
    )
    suspend fun updateUserContent(
        id: String,
        kategori: Kategori,
        severitas: Severitas,
        walkability: Int,
        rasional: String,
    )

    @Query("DELETE FROM laporan WHERE id = :id")
    suspend fun deleteById(id: String)
}
