package id.pejalan.data

import id.pejalan.ml.Kategori
import id.pejalan.ml.Severitas

private const val MIN_MS = 60_000L
private const val HOUR_MS = 60L * MIN_MS
private const val DAY_MS = 24L * HOUR_MS

object SeedData {

    fun entries(now: Long = System.currentTimeMillis()): List<Laporan> = listOf(
        seed(
            id = "PJ-130-0247",
            ageMs = 28 * MIN_MS,
            now = now,
            lat = -6.1845, lng = 106.8333,
            kategori = Kategori.PARKIR_LIAR,
            severitas = Severitas.TINGGI,
            keyakinan = 0.92f,
            walkability = 2,
            rasional = "Beberapa motor parkir menutup separuh trotoar di Jalan Sabang. " +
                "Pejalan kaki harus turun ke jalan raya.",
        ),
        seed(
            id = "PJ-130-0241",
            ageMs = 2 * HOUR_MS + 14 * MIN_MS,
            now = now,
            lat = -6.2087, lng = 106.8214,
            kategori = Kategori.TROTOAR_RUSAK,
            severitas = Severitas.SEDANG,
            keyakinan = 0.78f,
            walkability = 3,
            rasional = "Paving block pecah-pecah di sepanjang Sudirman, beberapa berlubang " +
                "cukup dalam untuk menyandung kaki.",
        ),
        seed(
            id = "PJ-130-0238",
            ageMs = 4 * HOUR_MS + 45 * MIN_MS,
            now = now,
            lat = -6.1939, lng = 106.8233,
            kategori = Kategori.HALANGAN_PERMANEN,
            severitas = Severitas.SEDANG,
            keyakinan = 0.83f,
            walkability = 3,
            rasional = "Gerobak PKL menetap di trotoar Thamrin, menyisakan jalur sempit " +
                "untuk dua orang yang berpapasan.",
        ),
        seed(
            id = "PJ-129-0227",
            ageMs = DAY_MS + 3 * HOUR_MS,
            now = now,
            lat = -6.2419, lng = 106.7986,
            kategori = Kategori.UBIN_DIFABEL_BERMASALAH,
            severitas = Severitas.TINGGI,
            keyakinan = 0.88f,
            walkability = 1,
            rasional = "Guiding block kuning di Kebayoran terputus dan banyak yang hilang, " +
                "tidak aman untuk teman tuna netra.",
        ),
        seed(
            id = "PJ-129-0219",
            ageMs = DAY_MS + 8 * HOUR_MS,
            now = now,
            lat = -6.2655, lng = 106.8170,
            kategori = Kategori.DRAINASE,
            severitas = Severitas.TINGGI,
            keyakinan = 0.81f,
            walkability = 2,
            rasional = "Manhole terbuka di Kemang Raya tanpa pengaman. Berbahaya saat malam.",
        ),
        seed(
            id = "PJ-128-0203",
            ageMs = 2 * DAY_MS + 5 * HOUR_MS,
            now = now,
            lat = -6.1939, lng = 106.8307,
            kategori = Kategori.TROTOAR_ABSEN,
            severitas = Severitas.SEDANG,
            keyakinan = 0.76f,
            walkability = 2,
            rasional = "Tidak ada trotoar sepanjang 40 meter di Menteng, pejalan harus " +
                "berbagi jalur dengan kendaraan.",
        ),
    )

    private fun seed(
        id: String,
        ageMs: Long,
        now: Long,
        lat: Double,
        lng: Double,
        kategori: Kategori,
        severitas: Severitas,
        keyakinan: Float,
        walkability: Int,
        rasional: String,
    ): Laporan = Laporan(
        id = id,
        createdAt = now - ageMs,
        lat = lat,
        lng = lng,
        accuracyM = 0f,
        photoPath = "",
        kategori = kategori,
        severitas = severitas,
        keyakinan = keyakinan,
        walkability = walkability,
        rasional = rasional,
        bboxX = 0f, bboxY = 0f, bboxW = 1f, bboxH = 1f,
        memoPath = null,
        userCorrected = false,
        syncedAt = null,
    )
}
