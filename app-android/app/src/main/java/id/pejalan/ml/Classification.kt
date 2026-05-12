package id.pejalan.ml

enum class Kategori(val label: String, val isViolation: Boolean) {
    PARKIR_LIAR("Parkir liar", true),
    TROTOAR_RUSAK("Trotoar rusak", true),
    HALANGAN_PERMANEN("Halangan permanen", true),
    UBIN_DIFABEL_BERMASALAH("Ubin difabel bermasalah", true),
    TROTOAR_ABSEN("Trotoar absen", true),
    DRAINASE("Drainase", true),
    NIHIL("Tidak ada pelanggaran", false),
    BUKAN_TROTOAR("Bukan foto trotoar", false),
    LAINNYA("Tidak dapat ditentukan", false);

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
            severitas = Severitas.RENDAH,
            keyakinan = 0f,
            rasional = "Tidak dapat mengklasifikasi otomatis. Coba foto ulang dengan sudut berbeda.",
            bbox = BBox.Full,
        )
    }
}
