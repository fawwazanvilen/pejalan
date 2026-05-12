package id.pejalan.ml

enum class Kategori(val label: String) {
    PARKIR_LIAR("Parkir liar"),
    TROTOAR_RUSAK("Trotoar rusak"),
    HALANGAN_PERMANEN("Halangan permanen"),
    UBIN_DIFABEL_BERMASALAH("Ubin difabel bermasalah"),
    TROTOAR_ABSEN("Trotoar absen"),
    DRAINASE("Drainase"),
    LAINNYA("Lainnya");

    companion object {
        fun fromString(s: String?): Kategori =
            values().firstOrNull { it.name == s?.trim()?.uppercase() } ?: LAINNYA
    }
}

enum class Severitas(val label: String) {
    RENDAH("Rendah"),
    SEDANG("Sedang"),
    TINGGI("Tinggi");

    companion object {
        fun fromString(s: String?): Severitas =
            values().firstOrNull { it.name == s?.trim()?.uppercase() } ?: SEDANG
    }
}

data class BBox(val x: Float, val y: Float, val w: Float, val h: Float) {
    companion object {
        val Full = BBox(0f, 0f, 1f, 1f)
    }
}

data class Classification(
    val kategori: Kategori,
    val severitas: Severitas,
    val keyakinan: Float,
    val rasional: String,
    val bbox: BBox,
) {
    // Maps keyakinan 0..1 to a 1..5 meter, per HANDOFF §2
    val meter: Int get() = ((keyakinan * 5).toInt() + 1).coerceIn(1, 5)

    companion object {
        val Fallback = Classification(
            kategori = Kategori.LAINNYA,
            severitas = Severitas.SEDANG,
            keyakinan = 0f,
            rasional = "Tidak dapat mengklasifikasi otomatis.",
            bbox = BBox.Full,
        )
    }
}
