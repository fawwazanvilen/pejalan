package id.pejalan.ml

const val PROMPT = """Klasifikasi pelanggaran trotoar pada foto ini sebagai alat audit warga di Jakarta.

Pilih SATU kategori dari daftar berikut:
- PARKIR_LIAR        (kendaraan di atas trotoar)
- TROTOAR_RUSAK      (paving retak, lubang, pecah)
- HALANGAN_PERMANEN  (tiang, pohon, gerobak menetap)
- UBIN_DIFABEL_BERMASALAH (guiding-block rusak/hilang)
- TROTOAR_ABSEN      (tidak ada trotoar)
- DRAINASE           (got terbuka, manhole hilang)

Nilai severitas:
- rendah  — tidak menghalangi jalan
- sedang  — pejalan harus menghindar
- tinggi  — pejalan terpaksa turun ke jalan raya

Balas JSON saja, tanpa pembuka:
{
  "kategori": "PARKIR_LIAR",
  "severitas": "tinggi",
  "keyakinan": 0.87,
  "rasional": "satu kalimat bahasa Indonesia tentang apa yang terlihat dan dampaknya pada pejalan",
  "bbox": { "x": 0.0-1.0, "y": 0.0-1.0, "w": 0.0-1.0, "h": 0.0-1.0 }
}"""
