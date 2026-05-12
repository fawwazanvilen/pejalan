package id.pejalan.ml

const val PROMPT = """Klasifikasi foto ini sebagai alat audit warga untuk trotoar di Jakarta.

Pertama tentukan kategori. Pilih SATU dari daftar:
- BUKAN_TROTOAR           (foto tidak menampilkan trotoar atau jalur pejalan kaki sama sekali)
- NIHIL                   (trotoar terlihat baik, tidak ada pelanggaran terlihat)
- PARKIR_LIAR             (kendaraan di atas trotoar)
- TROTOAR_RUSAK           (paving retak, berlubang, atau pecah)
- HALANGAN_PERMANEN       (tiang, pohon, gerobak menetap di trotoar)
- UBIN_DIFABEL_BERMASALAH (guiding-block rusak atau hilang)
- TROTOAR_ABSEN           (seharusnya ada trotoar tetapi tidak ada)
- DRAINASE                (got terbuka, manhole hilang)

Nilai severitas (hanya jika kategori adalah pelanggaran):
- rendah   — tidak menghalangi jalan
- sedang   — pejalan harus menghindar
- tinggi   — pejalan terpaksa turun ke jalan raya
Jika kategori BUKAN_TROTOAR atau NIHIL, gunakan "rendah".

Balas JSON saja, tanpa pembuka, tanpa markdown, tanpa code fence:
{
  "kategori": "PARKIR_LIAR",
  "severitas": "tinggi",
  "keyakinan": 0.87,
  "rasional": "satu kalimat bahasa Indonesia menjelaskan apa yang terlihat dan dampaknya",
  "bbox": { "x": 0.0-1.0, "y": 0.0-1.0, "w": 0.0-1.0, "h": 0.0-1.0 }
}"""
