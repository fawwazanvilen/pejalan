# Pejalan · Developer Handoff

> Civil-society pedestrian-infrastructure audit tool for Jakarta.
> Hackathon target: working capture loop with on-device Gemma classification.

This package is built for a Claude Code session inside this project folder. Open the HTML canvas (`Pejalan Mid-Fi Flows.html`) in a browser tab while you code — that is the visual source of truth.

---

## 1 · Stack (hackathon-fast)

| Layer | Pick | Notes |
|---|---|---|
| UI | **Kotlin + Jetpack Compose** | Material 3 base, custom theme below |
| Min SDK | **API 26 (Android 8.0)** | Covers Gemma-capable devices |
| Camera | **CameraX** (`androidx.camera:camera-camera2`, `-lifecycle`, `-view`) | Compose binding via `AndroidView` |
| ML | **MediaPipe LLM Inference API** + **Gemma 3n** (`.task` file in `assets/`) | Multimodal vision+text in one call |
| Storage | **Room** for laporan, **DataStore** for prefs | Offline-first |
| Map | **Google Maps Compose** (`com.google.maps.android:maps-compose`) | Or `osmdroid` if offline tiles needed |
| Audio | **MediaRecorder** | 30-sec voice memo, AAC |
| DI | Manual `object` containers | Skip Hilt for hackathon |
| Build | **Gradle Kotlin DSL** | Single-module |

---

## 2 · Gemma — what to wire up

### Model
- Use **Gemma 3n E2B** (smaller, faster — ~2GB on-device) or **E4B** if quality matters more than speed.
- Download `.task` file from Google AI Edge / Kaggle, ship in `app/src/main/assets/gemma3n_e2b.task` (or load from external storage on first run to keep APK small).
- Library: `com.google.mediapipe:tasks-genai`

### Setup
```kotlin
val options = LlmInferenceOptions.builder()
    .setModelPath(modelFile.absolutePath)
    .setMaxTokens(512)
    .setMaxNumImages(1)
    .build()
val llm = LlmInference.createFromOptions(context, options)

val session = LlmInferenceSession.createFromOptions(
    llm,
    LlmInferenceSessionOptions.builder()
        .setGraphOptions(GraphOptions.builder().setEnableVisionModality(true).build())
        .build()
)
session.addImage(BitmapImageBuilder(photoBitmap).build())
session.addQueryChunk(PROMPT)
val response = session.generateResponse()
```

### Prompt (paste verbatim)
```
Klasifikasi pelanggaran trotoar pada foto ini sebagai alat audit warga di Jakarta.

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
}
```

### Output parsing
- Gemma will sometimes wrap JSON in markdown fences — strip ` ```json ` and ` ``` `.
- `keyakinan` maps to the 5-step meter: `floor(k * 5) + 1`, clamped 1–5.
- Bbox coords are 0–1 normalized; multiply by photo pixel dims to draw.
- If JSON parse fails → fall back to `kategori: "LAINNYA"`, route to manual picker (screen 07).

---

## 3 · Design tokens — paste into `Theme.kt`

```kotlin
object PejalanColors {
    val Paper      = Color(0xFFECE4D2)
    val PaperHi    = Color(0xFFF4ECD9)
    val PaperLo    = Color(0xFFDCD2BB)
    val Ink        = Color(0xFF1A1410)
    val InkSoft    = Color(0xFF322A23)
    val Mute       = Color(0xFF7B6C52)
    val MuteLo     = Color(0xFFB5A98A)

    // Primary accent — biru tarum (indigo)
    val Indigo     = Color(0xFF1C2A52)
    val IndigoInk  = Color(0xFF0A1230)
    val IndigoTint = Color(0xFFD6DAE9)

    // Severity tinggi — deep marun (red reserved for highest tier only)
    val SevTinggi    = Color(0xFF7A1F17)
    val SevTinggiTint= Color(0xFFECD0CB)

    // Severity sedang — sogan (Indonesian batik brown)
    val SevSedang    = Color(0xFF8A5A2B)
    val SevSedangTint= Color(0xFFE8D6B8)

    // Severity rendah — muted olive
    val SevRendah    = Color(0xFF5E6B3A)
    val SevRendahTint= Color(0xFFDEE0C6)

    val HiVis      = Color(0xFFF3C100)  // bbox + camera reticle
}

object PejalanType {
    // Plus Jakarta Sans — display + body
    // Barlow Semi Condensed — utility labels (signage register)
    // Load via Compose downloadable fonts or bundle .ttf in res/font/
}

object PejalanSpace {
    val Rule = 1.5.dp
    val RuleHeavy = 3.dp
    val TapTarget = 48.dp   // WCAG AA floor
    val ChipPad = 12.dp
}
```

---

## 4 · Screens → files mapping

| # | Screen | Compose file | Drives |
|---|---|---|---|
| 01 | Pre-capture viewfinder | `CameraScreen.kt` | CameraX preview + shutter |
| 02 | Analyzing | `AnalyzingOverlay.kt` | Gemma call in progress |
| 03 | Result (high-conf) | `ResultSheet.kt` | Gemma JSON → UI |
| 04 | Result (med-conf) | `ResultSheet.kt` (state: confidence ≤ 3) | Promotes correction list |
| 05 | Voice memo | `VoiceMemoSheet.kt` | MediaRecorder |
| 06 | Detail penalaran | `DetailScreen.kt` | Full reasoning |
| 07 | Manual picker | `CategoryPickerScreen.kt` | When AI is wrong |
| 08 | Saved | `SavedScreen.kt` | Room insert + tally |
| 09 | Linimasa | `FeedScreen.kt` | Local query (seed with fake data) |
| 10 | Peta | `MapScreen.kt` | Google Maps + Room markers |
| 11 | Profil | `ProfileScreen.kt` | DataStore stats |
| 12 | Lencana | `BadgesScreen.kt` | Derived from Room |

---

## 5 · Data shapes (Room)

```kotlin
@Entity
data class Laporan(
    @PrimaryKey val id: String,        // "PJ-024-0247"
    val createdAt: Long,
    val lat: Double, val lng: Double,
    val accuracyM: Float,
    val photoPath: String,
    val kategori: Kategori,            // enum, 6 values + LAINNYA
    val severitas: Severitas,          // rendah/sedang/tinggi
    val keyakinan: Float,              // 0..1
    val rasional: String,
    val bboxX: Float, val bboxY: Float, val bboxW: Float, val bboxH: Float,
    val memoPath: String?,             // null if no voice memo
    val userCorrected: Boolean,        // did the user override AI's kategori?
    val syncedAt: Long?                // null = local only
)
```

---

## 6 · File tree (minimum viable)

```
app/
├── src/main/
│   ├── assets/gemma3n_e2b.task           # or download on first run
│   ├── java/id/pejalan/
│   │   ├── MainActivity.kt
│   │   ├── ui/
│   │   │   ├── theme/                    # Theme.kt + tokens above
│   │   │   ├── camera/CameraScreen.kt
│   │   │   ├── result/ResultSheet.kt
│   │   │   ├── result/AnalyzingOverlay.kt
│   │   │   ├── result/DetailScreen.kt
│   │   │   ├── picker/CategoryPickerScreen.kt
│   │   │   ├── voice/VoiceMemoSheet.kt
│   │   │   ├── saved/SavedScreen.kt
│   │   │   ├── feed/FeedScreen.kt
│   │   │   ├── map/MapScreen.kt
│   │   │   ├── profile/ProfileScreen.kt
│   │   │   └── badges/BadgesScreen.kt
│   │   ├── ml/
│   │   │   ├── GemmaClient.kt            # MediaPipe wrapper
│   │   │   └── Prompt.kt                 # the prompt above as const
│   │   ├── data/
│   │   │   ├── LaporanDao.kt
│   │   │   ├── LaporanDb.kt
│   │   │   └── SeedData.kt               # fake feed/map entries
│   │   └── nav/PejalanNav.kt             # NavHost
│   └── res/font/                         # PlusJakartaSans + BarlowSemiCondensed
└── build.gradle.kts
```

---

## 7 · Hackathon cut list (drop in this order if behind)

1. ⬇ Voice memo (05) — text only, skip MediaRecorder
2. ⬇ Detail penalaran (06) — show inline only
3. ⬇ Lencana (12) — hide the tab
4. ⬇ Profil (11) — hide the tab
5. ⬇ Linimasa real data — keep with `SeedData.kt` hardcoded entries
6. ⬇ Peta real markers — keep with `SeedData.kt`

**Never cut:** camera → Gemma → result sheet → save. That's the demo.

---

## 8 · Demo-day script

1. Open app at JL. Sabang or similar messy sidewalk
2. Aim at a parked motorbike → tap shutter
3. ~0.8s on-device inference → result sheet slides up with `PARKIR_LIAR · tinggi · 5/5`
4. Tap **Lanjutkan** → saved confirmation with today's tally
5. Switch to Peta → show pin appear; switch to Linimasa → show your laporan at top
6. Show Profil → "your audit included in 3 KPK advocacy documents"

**Total demo: ~90 seconds.**

---

## 9 · Open design questions (decide before / during dev)

- [ ] Streak counter on/off? (pressure risk vs habit-forming)
- [ ] Walker photos visible on map pins, or just severity dots? (privacy + brigading)
- [ ] Lencana — org-verified (KPK/ITDP issues them) or auto-awarded?
- [ ] Bbox: trust Gemma's coords, or run a second pass with a TFLite detector for tighter boxes?

---

## 10 · References in this project

- `Pejalan Mid-Fi Flows.html` — open in browser; the canvas is your visual spec
- `midfi-kit.jsx` — design tokens & primitives, ported to Kotlin in §3 above
- `assets/mobil-trotoar.png` — test photo for prompt tuning

Built fast. Cut anything that isn't the demo. Selamat hacking.
