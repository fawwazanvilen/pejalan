package id.pejalan.data

import androidx.room.TypeConverter
import id.pejalan.ml.Kategori
import id.pejalan.ml.Severitas

class Converters {
    // Kategori is now stored as a comma-separated string (e.g. "PARKIR_LIAR,TROTOAR_RUSAK").
    // Old single-value rows ("PARKIR_LIAR") parse cleanly as a 1-element set — no SQL migration needed.
    @TypeConverter
    fun fromKategoriSet(value: Set<Kategori>): String =
        value.joinToString(",") { it.name }

    @TypeConverter
    fun toKategoriSet(value: String): Set<Kategori> =
        value.split(",")
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .mapNotNull { name -> Kategori.entries.firstOrNull { it.name == name } }
            .toSet()
            .ifEmpty { setOf(Kategori.LAINNYA) }

    @TypeConverter fun fromSeveritas(value: Severitas): String = value.name
    @TypeConverter fun toSeveritas(value: String): Severitas = Severitas.fromString(value)

    @TypeConverter fun fromStatus(value: LaporanStatus): String = value.name
    @TypeConverter fun toStatus(value: String): LaporanStatus = LaporanStatus.fromString(value)
}
