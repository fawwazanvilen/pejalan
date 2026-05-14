package id.pejalan.ml

const val PROMPT = """Klasifikasi foto ini sebagai alat audit warga untuk trotoar di Jakarta.

Sebuah trotoar bisa memiliki BEBERAPA pelanggaran sekaligus. Pilih SATU ATAU LEBIH
kategori dari daftar berikut. Balas sebagai array JSON.

- BUKAN_TROTOAR           (foto tidak menampilkan trotoar atau jalur pejalan kaki sama sekali)
- NIHIL                   (trotoar terlihat baik, tidak ada pelanggaran terlihat)
- PARKIR_LIAR             (kendaraan di atas trotoar)
- TROTOAR_RUSAK           (paving retak, berlubang, atau pecah)
- HALANGAN_PERMANEN       (tiang, pohon, gerobak menetap di trotoar)
- UBIN_DIFABEL_BERMASALAH (guiding-block rusak atau hilang)
- TROTOAR_ABSEN           (seharusnya ada trotoar tetapi tidak ada)
- DRAINASE                (got terbuka, manhole hilang)

BUKAN_TROTOAR dan NIHIL bersifat eksklusif — tidak boleh digabungkan dengan
kategori lain.

Nilai severitas (hanya jika ada pelanggaran):
- rendah   — tidak menghalangi jalan
- sedang   — pejalan harus menghindar
- tinggi   — pejalan terpaksa turun ke jalan raya
Jika kategori adalah BUKAN_TROTOAR atau NIHIL, gunakan "rendah".

Nilai juga "kelayakan_pejalan_kaki" sebagai skor kualitas trotoar 1–5:
- 1: Tidak dapat dilalui pejalan kaki sama sekali
- 2: Sangat sulit, banyak halangan, harus menghindar terus
- 3: Bisa dilalui dengan susah payah
- 4: Cukup nyaman untuk berjalan
- 5: Sangat baik, ramah pejalan dan difabel
Jika kategori BUKAN_TROTOAR, gunakan 0 (tidak berlaku).

Balas JSON saja, tanpa pembuka, tanpa markdown, tanpa code fence:
{
  "kategori": ["PARKIR_LIAR", "TROTOAR_RUSAK"],
  "severitas": "tinggi",
  "keyakinan": 0.87,
  "kelayakan_pejalan_kaki": 2,
  "rasional": "satu kalimat bahasa Indonesia menjelaskan apa yang terlihat dan dampaknya",
  "bbox": { "x": 0.0-1.0, "y": 0.0-1.0, "w": 0.0-1.0, "h": 0.0-1.0 }
}"""
