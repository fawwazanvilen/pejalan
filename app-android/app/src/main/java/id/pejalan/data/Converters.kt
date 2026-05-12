package id.pejalan.data

import androidx.room.TypeConverter
import id.pejalan.ml.Kategori
import id.pejalan.ml.Severitas

class Converters {
    @TypeConverter fun fromKategori(value: Kategori): String = value.name
    @TypeConverter fun toKategori(value: String): Kategori = Kategori.fromString(value)

    @TypeConverter fun fromSeveritas(value: Severitas): String = value.name
    @TypeConverter fun toSeveritas(value: String): Severitas = Severitas.fromString(value)
}
