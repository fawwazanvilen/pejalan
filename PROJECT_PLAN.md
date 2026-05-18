# Pejalan — Project Plan

Living document. Updated each working session.

## Goal

A hackathon-quality Android demo that proves civil-society can crowdsource pedestrian-infrastructure violations in Jakarta with a local-first AI assist. The demo loop:

```
1. User points camera at a trotoar (sidewalk) and shoots.
2. Mode Teliti → on-device Gemma classifies in ~30s (or Cloud Gemini in ~3s).
   Mode Cepat → photo saves as a draft; user reviews later.
3. User confirms or corrects the classification (kategori, severitas, walkability, rasional).
4. Row lands in Linimasa (timeline) and Peta (map). Future: aggregate civic dashboard.
```

Beyond the demo, the long-term thesis: an app that pejalan (pedestrians) and civil-society groups actually use to document inaccessible streets, with enough structure that the data is usable by advocacy and city planners.

## Demo path

- Phone: Pixel 7 Pro (or any modern Android with a GPU).
- Gemma model `.task` pushed via `adb push` to internal storage (path is wired in `GemmaClient`).
- Gemini fallback enabled via `gradle.properties` → `GEMINI_API_KEY=...`.
- Mapbox token set in `gradle.properties` for the Peta tab.

## Done (highlights)

Earlier scaffolding (camera, Room, theme, seed data, single-activity Compose, Mapbox map, Linimasa, Profile, AnalyzingOverlay, ResultSheet, DetailScreen edit form) is captured in `ARCHITECTURE.md`. Recent additions:

- **Pluggable classifier** (`Classifier` interface, `GemmaClient` + `GeminiClient`, `AiMode` StateFlow in `PejalanApplication`, mode toggle in Profil).
- **Cepat mode** + **Draft status** end-to-end: Cepat captures save as drafts; ResultSheet dismiss saves as draft (no silent discard); collapsible "Draf" section at the top of Linimasa with Submit-semua; DetailScreen doubles as draft edit surface with "Simpan ke linimasa" CTA.
- **Two-step decision-tree prompt** in `ml/Prompt.kt` to disambiguate `BUKAN_TROTOAR` vs `NIHIL`.
- **AnalyzingOverlay** generalized to "Model sedang menganalisis" with mode-aware cadence (Cloud is fast, Lokal is slow).
- **Bug fixes (last session):**
  - `countAll()` query for ID generation — was using `totalCount()` (excludes DRAFT), so multiple Cepat captures collided on `PJ-DDD-0001` and overwrote each other via REPLACE.
  - `Classification.Empty` for null-classification saves — Cepat drafts no longer inherit `Fallback`'s "Tidak dapat mengklasifikasi" copy; they now read "Belum diklasifikasi" in the DraftCard.
  - Hoisted `onSavedCompleted` above `saveOrPromptForLocation` (Composable forward-ref fix).
  - `LaporanStatus.DRAFT` added to `LaporanCard`'s `when (status)` for exhaustiveness.
- **Mapbox basemap** swapped to `light-v11` for calmer marker contrast.
- **Launcher icon** from the Pejalan logo PNG.

## Pending queue (single list)

Rough priority — top is "do soon", bottom is "post-hackathon".

1. **README** at repo root — what the app is, how to build, screenshots.
2. **Kelurahan reverse-geocoding** (Mapbox Geocoding API) — show neighborhood name in ResultSheet + DetailScreen + LaporanDetailSheet.
3. **SavedScreen redesign** + direct edit button (currently a placeholder "Tersimpan" screen).
4. **ConfirmPhotoOverlay** visual coherence with ResultSheet (typography, divider style, button rhythm).
5. **DetailScreen ↔ ResultSheet** — slide-up transition (vs current fade) + bigger architectural question: should DetailScreen be refactored into ResultSheet for drafts (or vice versa)?
6. **NavBar polish + Scaffold pass.** Also in this pass: Toast → Snackbar globally (the Snackbar host needs a Scaffold), with bulk-undo for "Submit semua (N)" and undo on "Disimpan sebagai draf".
7. **Profile badge** shape uniformity.
8. **Map puck / circle-annotation z-ordering** fix (Mapbox slot system — light-v11 style may or may not support it; needs investigation).
9. **CaptureMode (Teliti / Cepat) persistence** across navigation. Currently a local `var mode by remember { ... }` inside CaptureRoute — resets when leaving the Capture tab and coming back.
10. **Manual location entry** for when GPS is unavailable (fallback path after the LocationTimeout dialog).
11. **Explicit "Batal" affordance** in ResultSheet — currently dismiss-by-tap-outside is the only exit (auto-saves as draft).
12. **Draft discoverability** — peek behavior or a badge on the Linimasa tab so users know drafts live at the top.
13. **ARCHITECTURE.md refresh** — see "Stale docs" below.
14. **(Post-hackathon)** On-device Gemma first-run download flow. Host the `.task` on R2 / B2 / Hugging Face. Gate behind WiFi-only by default, persist to internal storage, show progress UI. ~30 min of work + asset hosting setup.
15. **(Post-hackathon)** Backend proxy for Gemini API key — current build ships the key in BuildConfig (plaintext in dex). Acceptable for hackathon (with low billing cap + rotate after); not safe for public distribution.

## Constraints / known limits

- **Gemma model is 2.5GB and NOT in git** (ignored via `.gitignore: *.task`). Ships via `adb push` for dev. Sharing to a friend = APK + Cloud mode (Gemma model not bundled).
- **Gemini API key in `BuildConfig`** ships as plaintext in the APK. Set a billing cap for demos and rotate after.
- **Toast, not Snackbar** — current "Disimpan sebagai draf" etc. use `android.widget.Toast` because there's no `SnackbarHost` yet. To swap, the app needs a single `Scaffold` at the root with a `SnackbarHost` (queue item 6).
- **LaporanStatus.fromString fallback is `CLASSIFIED`** (not `FAILED`). Defensive choice: unknown status strings most likely come from data drift, not classifier failures, so default to "show normally" rather than "hide behind retry banner".

## Stale docs (ARCHITECTURE.md)

`ARCHITECTURE.md` was written before the multi-classifier refactor. Known gaps:

- §1 (Entry points): `PejalanApplication` now also owns `gemini: GeminiClient` and `_mode: MutableStateFlow<AiMode>` with derived `activeClassifier`. `PejalanApp` is now `PejalanApp(app)` not `PejalanApp(app.gemma, app.db, app.queue)`.
- §2 (Compose tree): `PejalanNav` takes `(app, db, queue, captureRoute)` — `gemma` is no longer a separate param.
- §5 (Data layer): `LaporanStatus` is now `DRAFT | PENDING | CLASSIFIED | FAILED` (DRAFT added).
- §6 (ML layer): needs a paragraph on `Classifier` interface, `AiMode`, `GeminiClient`, and the call-time resolution via `activeClassifier`.
- §7 (State machine): `CaptureRoute` signature includes `aiMode: AiMode`; `CaptureState.Saving` carries `status: LaporanStatus`.

When refreshing, also document the `Classification.Empty` vs `Classification.Fallback` distinction in §6.

## How to pick the next task

1. If the user names a queue item, do that.
2. Otherwise lean toward items that improve the demo story (kelurahan reverse-geocoding, NavBar polish, CaptureMode persistence) over post-hackathon items.
3. Smaller polish items (4, 7, 11) are good when there's an awkward 20-minute slot.
4. Bigger items (5, 6, 8) deserve their own session.
