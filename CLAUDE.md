# Pejalan — Notes for Claude Code

Civil-society Android app for auditing pedestrian infrastructure (trotoar) in Jakarta. Demo loop: photo of a sidewalk → on-device Gemma or cloud Gemini classifies → user reviews / corrects / saves → row appears in Linimasa (timeline) and Peta (map).

The user is new to Android and Kotlin. Explain Android/Compose/Room/Kotlin idioms when relevant — don't assume familiarity with the framework.

## Repo layout

```
app-android/      Android module (single-activity Compose app)
design/           Figma exports and design references
ARCHITECTURE.md   Code-reading map — read this before touching cross-cutting changes
PROJECT_PLAN.md   Demo goal, what's done, what's pending — read this when picking the next task
```

`ARCHITECTURE.md` is a deep code map (entry points, module graph, data flow, state machine). It's lightly stale — see PROJECT_PLAN.md's "Stale docs" section for known gaps.

## Build

```
cd app-android
./gradlew assembleDebug          # build APK (slow — ~5 min cold, ~30s warm)
./gradlew installDebug           # build + push to attached device
```

User runs the build from Android Studio on Windows. **Don't run gradle yourself** unless asked — it's slow and the user prefers to drive builds.

Path note: the user works on WSL; same tree shows up at `/mnt/c/Users/fawwa/Codes/pejalan` (WSL) and `C:\Users\fawwa\Codes\pejalan` (Windows). Use the WSL path for tool calls.

## Conventions

**Commits.** Conventional Commits, single line, atomic. No `Co-Authored-By` footer. One logical change per commit — split unrelated work into multiple commits.

**UI copy.** Natural Indonesian. No "claudisms" (no "Sip!", "Lho", etc. unless natural). No dev-facing strings (no "GEMINI_API_KEY missing in gradle.properties") in user-visible UI — soften to user-meaningful phrasing.

**Queue.** One consolidated list in PROJECT_PLAN.md. No parking-lot subsections.

**Kotlin / Compose:**
- When adding a value to an enum or sealed class, grep every `when (...)` site over that type and add the new branch BEFORE committing — Kotlin's exhaustiveness check via property paths (`when (laporan.status)`) is easy to miss until build time.
- Local `fun` declarations inside a `@Composable` body are NOT hoisted — declare helpers before any caller, or hoist to top-level `private fun`.

## Critical caveats

**Gemma model is NOT in git.** It's a 2.5GB `.task` file ignored via `.gitignore`. Ships separately via `adb push` (dev) or eventually a first-run download (future). If a friend installs the APK without the model, they get a soft block on Capture in Lokal mode — they can switch to Cloud in Profil and use Gemini instead.

**Gemini API key ships baked into BuildConfig.** `BuildConfig.GEMINI_API_KEY` is a plaintext String literal in the dex. `jadx`/`strings` extracts it trivially. For demo: set a low billing cap and rotate post-hackathon. Real distribution needs a proxy server.

**Pluggable classifier.** `Classifier` interface in `ml/`. Two implementations: `GemmaClient` (on-device, LiteRT-LM) and `GeminiClient` (cloud, `generativeai:0.9.0`). `PejalanApplication` holds an `AiMode` StateFlow (Lokal | Cloud); `activeClassifier` resolves at call-time so mode toggles take effect on the next classify call (in-flight calls finish on the original backend).

**LaporanStatus.** `DRAFT | PENDING | CLASSIFIED | FAILED`. Stored as TEXT via Room converters, so adding enum values is schema-compatible (no migration needed). Drafts are filtered out of feed/map/stat queries via `WHERE status != 'DRAFT'` in the DAO.

## Memory

Long-term preferences and lessons-learned live in `~/.claude/projects/-home-fawwaz-Codes-pejalan/memory/`. The index is `MEMORY.md`. Topics covered: commit style, copy style, single-queue rule, enum-exhaustiveness lesson, Compose forward-refs lesson, user role.
