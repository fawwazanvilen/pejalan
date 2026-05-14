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

/** True if any kategori in the set is a violation. */
val Set<Kategori>.isViolation: Boolean
    get() = any { it.isViolation }

/** The most "representative" kategori for single-display contexts (markers, list rows).
 *  Uses enum declaration order, so PARKIR_LIAR wins over TROTOAR_RUSAK, etc. */
val Set<Kategori>.primary: Kategori
    get() = Kategori.entries.firstOrNull { it in this } ?: Kategori.LAINNYA

/** Toggle a kategori in the set with the multi-select rules:
 *  - Tapping a non-violation (NIHIL / BUKAN_TROTOAR / LAINNYA) clears everything else.
 *  - Tapping a violation while the set is in a non-violation state replaces with just the new pick.
 *  - Tapping an already-selected violation removes it (but keeps at least one element).
 *  - Tapping a new violation adds it to the set. */
fun Set<Kategori>.toggle(tap: Kategori): Set<Kategori> {
    val isCurrentlyNonViolation = any { !it.isViolation }
    return when {
        !tap.isViolation -> setOf(tap)
        isCurrentlyNonViolation -> setOf(tap)
        tap in this -> (this - tap).ifEmpty { setOf(Kategori.LAINNYA) }
        else -> this + tap
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
    val kategori: Set<Kategori>,
    val severitas: Severitas,
    val keyakinan: Float,
    val walkability: Int,
    val rasional: String,
    val bbox: BBox,
) {
    val meter: Int get() = ((keyakinan * 5).toInt() + 1).coerceIn(1, 5)

    companion object {
        val Fallback = Classification(
            kategori = setOf(Kategori.LAINNYA),
            severitas = Severitas.RENDAH,
            keyakinan = 0f,
            walkability = 0,
            rasional = "Tidak dapat mengklasifikasi otomatis. Coba foto ulang dengan sudut berbeda.",
            bbox = BBox.Full,
        )
    }
}
