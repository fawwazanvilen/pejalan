package id.pejalan.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

private val MIGRATION_1_2 = object : Migration(1, 2) {
    override fun migrate(db: SupportSQLiteDatabase) {
        db.execSQL("ALTER TABLE laporan ADD COLUMN walkability INTEGER NOT NULL DEFAULT 0")
    }
}

@Database(entities = [Laporan::class], version = 2, exportSchema = false)
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
                ).addMigrations(MIGRATION_1_2)
                    .build()
                    .also { instance = it }
            }
    }
}
