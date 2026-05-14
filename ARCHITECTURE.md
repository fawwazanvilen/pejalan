# Pejalan — Architecture

A reading map for the codebase. Updated alongside major refactors.

---

## 1. Entry points

```
PejalanApplication.kt     Application class. One instance per process,
                          owned by Android. Lives as long as the app is
                          in memory. Owns the singletons:
                            - gemma:  GemmaClient        (the LLM)
                            - db:     LaporanDb          (Room database)
                            - queue:  ClassificationQueue (background processor)

MainActivity.kt           The single Activity. Hosts the Compose root.
                            override fun onCreate(...) {
                                setContent {
                                    PejalanTheme {
                                        PejalanApp(app.gemma, app.db, app.queue)
                                    }
                                }
                            }
```

**Note on naming**: `PejalanApp` is a `@Composable` function (the UI root),
not an instance of `PejalanApplication`. Despite the similar name. The
composable is just the top of the Compose tree.

## 2. Compose tree structure

```
PejalanApp(gemma, db, queue)                    MainActivity.kt
└─ PejalanNav(gemma, db, queue, captureRoute)   nav/PejalanNav.kt
   └─ Scaffold {
        bottomBar = NavigationBar  (4 tabs)
        content   = NavHost
        }
   └─ NavHost(startDestination = "capture") {
        composable("capture")      { CaptureRoute(...) }
        composable("feed")         { FeedScreen(...) }
        composable("map")          { MapScreen(...) }
        composable("profile")      { ProfileScreen(...) }
        composable("detail/{id}")  { DetailScreen(...) }
      }
```

Every visible screen has a `@Composable` function in `ui/<feature>/`.
Find the screen, find the file.

## 3. Two navigation systems

- **NavController** (Jetpack Navigation Compose) for top-level tabs and detail
  routes. State is held in the `NavHost` backstack.
- **Sealed-interface state machine** inside the Capture tab. The `CaptureRoute`
  composable holds a single `var state: CaptureState` and dispatches via
  `when (state) { ... }`. Each variant renders a different sub-screen
  (Camera, ConfirmPhoto, Analyzing, Result, Saving, Saved).

Why two? NavController is overkill for the linear capture flow but right for
free navigation across tabs.

## 4. Feature folders

```
ui/theme/         Theme.kt              MaterialTheme wrapper + ColorScheme
                  Color.kt              Paper / Ink / Indigo / SevTinggi / etc.
                  Type.kt               Plus Jakarta Sans + Barlow downloadable

ui/camera/        CameraScreen.kt       CameraX preview, mode toggle, reticle, shutter
                  ConfirmPhotoOverlay   "Pakai foto ini?" overlay between shutter and analyze
                  CaptureMode.kt        enum Teliti | Cepat

ui/result/        ResultSheet.kt        Bottom sheet — kondisi / kategori / severitas
                  AnalyzingOverlay.kt   "Membaca trotoar." with step animation

ui/saving/        SavingScreen.kt       "Mengambil lokasi..." + permission/timeout dialogs

ui/saved/         SavedScreen.kt        "Tersimpan" confirmation after Teliti save

ui/feed/          FeedScreen.kt         Linimasa list

ui/map/           MapScreen.kt          Mapbox map + marker detail sheet

ui/detail/        DetailScreen.kt       Full-screen edit form for one laporan

ui/profile/       ProfileScreen.kt      Profil tab

ui/common/        WalkabilityBar.kt     Shared 5-segment rating component
```

## 5. Data layer (Room database)

```
data/Laporan.kt          Entity. The schema of one audit-report row.
                         The fields in this data class become columns in
                         the laporan table.

data/LaporanStatus.kt    Enum: PENDING | CLASSIFIED | FAILED
                         Stored as a text column via Converters.

data/LaporanDao.kt       Interface of queries. Annotations tell Room what
                         SQL to generate. Examples:
                           @Insert suspend fun insert(...)
                           @Query("SELECT * FROM laporan ...")
                           fun observeAll(): Flow<List<Laporan>>

data/LaporanDb.kt        Database singleton. Lists entities, version,
                         migrations. Created lazily via Room.databaseBuilder.

data/Converters.kt       Tells Room how to store enums as text.

data/SeedData.kt         6 fake Jakarta laporan, mixed into the feed/map
                         alongside the user's real captures.
```

### Data flow pattern (the one trick to learn)

Reactive data, no manual refresh:

```
1. UI calls db.laporanDao().observeAll()           returns Flow<List<Laporan>>
2. Compose calls .collectAsState(initial = ...)    Flow → Compose State
3. UI reads the State value as a normal var        e.g.  val real by ...
4. When a row is inserted/updated/deleted,
   Room emits a new list into the Flow             Compose's State changes
5. Any @Composable that read the State recomposes  UI updates automatically
```

You never call "refresh". The framework wires up the dependency.

## 6. ML layer

```
ml/GemmaClient.kt          Wrapper around LiteRT-LM's Engine.
                           - initialize(): suspend, ~20s, loads the 2GB model.
                           - classify(bitmap): suspend, ~30s on Pixel 7 Pro.
                           - close()
                           One singleton owned by PejalanApplication. A Mutex
                           inside classify() serializes concurrent calls so
                           Mode Teliti foreground and Mode Cepat queue cannot
                           trample each other through the GPU.

ml/Prompt.kt               The Indonesian prompt sent verbatim to Gemma.

ml/Classification.kt       Kategori + Severitas enums; BBox + Classification
                           data classes — the parsed JSON output of Gemma.

ml/ClassificationQueue.kt  In-app background worker for Mode Cepat.
                           Uses a SharedFlow as a wake-up signal. On wake,
                           processes all PENDING laporan one at a time:
                           load JPEG from disk → bitmap → classify → update row.
                           Started by MainActivity AFTER gemma.initialize()
                           succeeds.
```

## 7. The state machine (CaptureRoute, MainActivity.kt)

```kotlin
private sealed interface CaptureState {
    object Camera                                                              : CaptureState
    data class ConfirmPhoto(val bitmap: Bitmap)                                : CaptureState
    data class Analyzing(val bitmap: Bitmap)                                   : CaptureState
    data class Result(val bitmap, val classification, val location)            : CaptureState
    data class Saving(val bitmap, val classification, ..., val phase)          : CaptureState
    data class Saved(val laporan: Laporan)                                     : CaptureState
}

@Composable
fun CaptureRoute(gemma, db, queue, initState) {
    var state by remember { mutableStateOf<CaptureState>(CaptureState.Camera) }
    when (val s = state) {
        CaptureState.Camera        -> CameraScreen(onCapture = { state = ConfirmPhoto(it) })
        is CaptureState.ConfirmPhoto -> ConfirmPhotoOverlay(onUse = { ... })
        is CaptureState.Analyzing  -> AnalyzingOverlay(s.bitmap)
        is CaptureState.Result     -> ResultSheet(s.bitmap, s.classification, ...)
        is CaptureState.Saving     -> SavingScreen(s.phase, ...)
        is CaptureState.Saved      -> SavedScreen(s.laporan, ...)
    }
}
```

A transition is just `state = NewVariant(...)`. Compose re-renders the
appropriate sub-screen.

## 8. Reading order for a newcomer

1. **`PejalanApplication.kt`** — three lazy singletons
2. **`MainActivity.kt`** — Compose entry + the CaptureRoute state machine
3. **`nav/PejalanNav.kt`** — bottom nav + NavHost routes
4. **`ui/profile/ProfileScreen.kt`** — small, demonstrates the Flow → state pattern
5. **`data/Laporan.kt` + `data/LaporanDao.kt`** — see Room queries
6. **`ml/ClassificationQueue.kt`** — see how the worker pulls from Room and pushes to Gemma
7. **`ui/result/ResultSheet.kt`** — see how local correction state flows back via callbacks

## 9. Screen ↔ file map

| You see on the phone | File |
|---|---|
| Bottom tab bar | `nav/PejalanNav.kt` |
| Live camera + reticle + mode toggle | `ui/camera/CameraScreen.kt` |
| "Pakai foto ini?" after shutter | `ui/camera/ConfirmPhotoOverlay.kt` |
| "Membaca trotoar." analyzing screen | `ui/result/AnalyzingOverlay.kt` |
| Result bottom sheet (kondisi / kategori / severitas) | `ui/result/ResultSheet.kt` |
| "Mengambil lokasi..." overlay | `ui/saving/SavingScreen.kt` |
| "Tersimpan" + audit ID | `ui/saved/SavedScreen.kt` |
| Linimasa list of cards | `ui/feed/FeedScreen.kt` |
| Map of Jakarta | `ui/map/MapScreen.kt` |
| Marker tap → bottom sheet | `ui/map/MapScreen.kt` (`LaporanDetailSheet`) |
| Card tap → full screen edit form | `ui/detail/DetailScreen.kt` |
| Profil tab | `ui/profile/ProfileScreen.kt` |
| 5-segment rating bar (anywhere) | `ui/common/WalkabilityBar.kt` |
| Colors & font | `ui/theme/Color.kt` + `ui/theme/Type.kt` |

## 10. Build configuration touchpoints

```
app-android/build.gradle.kts                        Plugin block (top-level)
app-android/settings.gradle.kts                     Mapbox Maven repo with auth
app-android/gradle/libs.versions.toml               Version catalog — change versions here
app-android/app/build.gradle.kts                    The actual app module
app-android/app/src/main/AndroidManifest.xml        Permissions, Application class
C:\Users\fawwa\.gradle\gradle.properties            MAPBOX_DOWNLOADS_TOKEN, MAPBOX_ACCESS_TOKEN
```
