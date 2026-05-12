package id.pejalan.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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
}
