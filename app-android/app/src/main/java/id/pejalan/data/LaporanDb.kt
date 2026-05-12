package id.pejalan.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Laporan::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class LaporanDb : RoomDatabase() {

    abstract fun laporanDao(): LaporanDao

    companion object {
        @Volatile private var instance: LaporanDb? = null

        fun getInstance(context: Context): LaporanDb =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    LaporanDb::class.java,
                    "pejalan.db",
                ).build().also { instance = it }
            }
    }
}
