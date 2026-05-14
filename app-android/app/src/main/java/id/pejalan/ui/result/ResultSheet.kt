package id.pejalan.ui.result

import android.graphics.Bitmap
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.pejalan.ml.BBox
import id.pejalan.ml.Classification
import id.pejalan.ml.Kategori
import id.pejalan.ml.Severitas
import id.pejalan.ml.isViolation
import id.pejalan.ml.primary
import id.pejalan.ml.toggle
import id.pejalan.ui.common.WalkabilityBar
import id.pejalan.ui.theme.HiVis
import id.pejalan.ui.theme.Ink
import id.pejalan.ui.theme.Mute
import id.pejalan.ui.theme.PaperHi
import id.pejalan.ui.theme.SevRendah
import id.pejalan.ui.theme.SevSedang
import id.pejalan.ui.theme.SevTinggi
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ResultSheet(
    bitmap: Bitmap,
    classification: Classification,
    onDismiss: () -> Unit,
    onConfirm: (Classification, Boolean) -> Unit,
    onSaveAnyway: (Classification, Boolean) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var currentKategori by remember(classification) { mutableStateOf(classification.kategori) }
    var currentSeveritas by remember(classification) { mutableStateOf(classification.severitas) }
    var currentWalkability by remember(classification) { mutableIntStateOf(classification.walkability) }
    var currentRasional by remember(classification) { mutableStateOf(classification.rasional) }

    // BUKAN_TROTOAR and LAINNYA have no sidewalk to rate — zero walkability
    // as soon as the user selects one of those.
    LaunchedEffect(currentKategori) {
        val primary = currentKategori.primary
        if (primary == Kategori.BUKAN_TROTOAR || primary == Kategori.LAINNYA) {
            currentWalkability = 0
        }
    }

    val corrected = remember(currentKategori, currentSeveritas, currentWalkability, currentRasional, classification) {
        classification.copy(
            kategori = currentKategori,
            severitas = currentSeveritas,
            walkability = currentWalkability,
            rasional = currentRasional,
        )
    }
    val userCorrected = corrected.kategori != classification.kategori ||
        corrected.severitas != classification.severitas ||
        corrected.walkability != classification.walkability ||
        corrected.rasional != classification.rasional

    val primary = corrected.kategori.primary
    val ratingPossible = primary != Kategori.BUKAN_TROTOAR && primary != Kategori.LAINNYA

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = PaperHi,
        contentColor = Ink,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
        ) {
            PhotoHero(
                bitmap = bitmap,
                bbox = classification.bbox,
                showBbox = corrected.kategori.any { it.isViolation },
            )
            Spacer(Modifier.height(14.dp))
            AuditTimeHeader()
            SharpDivider()
            Spacer(Modifier.height(14.dp))

            // Single top banner replacing per-section "AI menebak..." copy
            AiHelperBanner()
            Spacer(Modifier.height(18.dp))

            // 01 — Kondisi trotoar (always visible; disabled state when foto isn't a sidewalk)
            FieldLabel("01", "Kondisi trotoar")
            Spacer(Modifier.height(6.dp))
            SectionPrompt("Seberapa nyaman trotoar ini untuk pejalan?")
            Spacer(Modifier.height(14.dp))
            WalkabilityBar(
                score = currentWalkability,
                interactive = ratingPossible,
                onChange = { currentWalkability = it },
                showLabel = false,
            )
            Spacer(Modifier.height(10.dp))
            SelectedDescription(
                when {
                    !ratingPossible -> "Tidak berlaku — foto bukan trotoar."
                    currentWalkability == 0 -> "Belum dinilai. Ketuk segmen di atas."
                    else -> "$currentWalkability dari 5 — ${walkabilityCopy(currentWalkability)}"
                }
            )

            Spacer(Modifier.height(22.dp))
            SharpDivider()
            Spacer(Modifier.height(18.dp))

            // 02 — Klasifikasi masalah
            FieldLabel("02", "Klasifikasi masalah")
            Spacer(Modifier.height(6.dp))
            SectionPrompt("Apa yang Anda lihat di trotoar ini? Bisa lebih dari satu.")
            Spacer(Modifier.height(12.dp))
            DisplayHeadline(displayNameSet(corrected.kategori))
            Spacer(Modifier.height(14.dp))
            KategoriChips(
                selected = currentKategori,
                onSelect = { currentKategori = currentKategori.toggle(it) },
            )

            // AI's original confidence — keep visible even after the user overrides.
            if (classification.meter > 0) {
                Spacer(Modifier.height(12.dp))
                ConfidenceBlocks(
                    meter = classification.meter,
                    originalKategori = if (currentKategori != classification.kategori) classification.kategori else null,
                )
            }

            // 03 — Severitas (only when at least one violation is selected)
            if (corrected.kategori.isViolation) {
                Spacer(Modifier.height(22.dp))
                SharpDivider()
                Spacer(Modifier.height(18.dp))

                FieldLabel("03", "Severitas")
                Spacer(Modifier.height(6.dp))
                SectionPrompt("Seberapa mengganggu pelanggaran ini bagi pejalan?")
                Spacer(Modifier.height(12.dp))
                SeverityChips(
                    selected = currentSeveritas,
                    onSelect = { currentSeveritas = it },
                )
                Spacer(Modifier.height(10.dp))
                SelectedDescription(severityCopy(currentSeveritas))
            }

            // 04 — Deskripsi (rasional, editable)
            Spacer(Modifier.height(22.dp))
            SharpDivider()
            Spacer(Modifier.height(18.dp))
            FieldLabel(
                number = if (corrected.kategori.isViolation) "04" else "03",
                text = "Deskripsi trotoar & masalah",
            )
            Spacer(Modifier.height(6.dp))
            SectionPrompt("Apa yang Anda amati di lapangan?")
            Spacer(Modifier.height(12.dp))
            OutlinedTextField(
                value = currentRasional,
                onValueChange = { currentRasional = it },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3,
                maxLines = 8,
                placeholder = {
                    Text(
                        "Belum ada deskripsi. Tambahkan sendiri.",
                        color = Mute,
                    )
                },
                shape = RoundedCornerShape(2.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Ink,
                    unfocusedBorderColor = Ink.copy(alpha = 0.4f),
                    focusedTextColor = Ink,
                    unfocusedTextColor = Ink,
                    cursorColor = Ink,
                ),
            )

            Spacer(Modifier.height(24.dp))
            if (corrected.kategori.isViolation) {
                PrimaryButton("Lanjutkan", onClick = { onConfirm(corrected, userCorrected) })
            } else {
                PrimaryButton(
                    "Kembali ke kamera",
                    onClick = { onConfirm(corrected, userCorrected) },
                )
                Spacer(Modifier.height(4.dp))
                TextButton(
                    onClick = { onSaveAnyway(corrected, userCorrected) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        "Tetap simpan ke linimasa",
                        color = Mute,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}

// ───────────────────────────────────────────────────────────────────────────
// Components
// ───────────────────────────────────────────────────────────────────────────

@Composable
private fun AiHelperBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Ink.copy(alpha = 0.06f))
            .border(1.dp, Ink.copy(alpha = 0.4f))
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            "AI",
            fontFamily = FontFamily.Monospace,
            fontWeight = FontWeight.Bold,
            fontSize = 11.sp,
            color = Ink,
            letterSpacing = 1.4.sp,
            modifier = Modifier.padding(top = 2.dp),
        )
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "AI telah membantu mengisi audit di bawah",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = Ink,
            )
            Text(
                "Anda dapat mengubah kapan saja.",
                style = MaterialTheme.typography.bodySmall,
                color = Mute,
            )
        }
    }
}

@Composable
private fun PhotoHero(bitmap: Bitmap, bbox: BBox, showBbox: Boolean) {
    val isMeaningfulBbox = bbox.w * bbox.h < 0.95f && bbox.w > 0.01f && bbox.h > 0.01f
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(Color.Black),
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )
        if (showBbox && isMeaningfulBbox) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                drawRect(
                    color = HiVis,
                    topLeft = Offset(bbox.x * size.width, bbox.y * size.height),
                    size = Size(bbox.w * size.width, bbox.h * size.height),
                    style = Stroke(width = 3.dp.toPx()),
                )
            }
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(8.dp)
                    .background(HiVis)
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                Text(
                    "AI",
                    fontFamily = FontFamily.Monospace,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    color = Ink,
                    letterSpacing = 1.4.sp,
                )
            }
        }
    }
}

@Composable
private fun AuditTimeHeader() {
    val time = remember {
        SimpleDateFormat("HH:mm", Locale("id", "ID")).format(Date()) + " WIB"
    }
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "Foto baru",
            style = MaterialTheme.typography.labelMedium,
            color = Mute,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.weight(1f))
        Text(
            time,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Mute,
        )
    }
}

@Composable
private fun FieldLabel(number: String, text: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            number,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Mute,
            letterSpacing = 1.4.sp,
        )
        Spacer(Modifier.width(10.dp))
        Text(
            text,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = FontWeight.SemiBold,
                color = Mute,
                letterSpacing = 0.8.sp,
            ),
        )
    }
}

@Composable
private fun DisplayHeadline(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        fontSize = 34.sp,
        lineHeight = 36.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-1.0).sp,
        color = Ink,
    )
}

@Composable
private fun ConfidenceBlocks(meter: Int, originalKategori: Set<Kategori>? = null) {
    val clamped = meter.coerceIn(0, 5)
    val color = confidenceColor(clamped)
    Column {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                repeat(5) { i ->
                    val filled = i < clamped
                    Box(
                        modifier = Modifier
                            .size(width = 20.dp, height = 8.dp)
                            .border(1.dp, color)
                            .background(if (filled) color else Color.Transparent),
                    )
                }
            }
            Spacer(Modifier.width(10.dp))
            Text(
                "AI ${confidenceLabel(clamped)}",
                style = MaterialTheme.typography.labelSmall,
                color = color,
                fontWeight = FontWeight.SemiBold,
                fontStyle = FontStyle.Italic,
            )
        }
        if (originalKategori != null) {
            Spacer(Modifier.height(2.dp))
            Text(
                "(tebakan asli AI: ${originalKategori.joinToString(", ") { it.label.lowercase() }})",
                style = MaterialTheme.typography.labelSmall,
                color = Mute,
                fontStyle = FontStyle.Italic,
            )
        }
    }
}

@Composable
private fun SectionPrompt(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.bodyMedium,
        color = Mute,
    )
}

@Composable
private fun SelectedDescription(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.bodyMedium,
        fontStyle = FontStyle.Italic,
        color = Ink.copy(alpha = 0.75f),
        fontWeight = FontWeight.Medium,
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun KategoriChips(selected: Set<Kategori>, onSelect: (Kategori) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Kategori.entries.forEach { k ->
            ChoiceChip(
                label = k.label.lowercase(),
                selected = k in selected,
                accent = Ink,
                onClick = { onSelect(k) },
            )
        }
    }
}

@Composable
private fun SeverityChips(selected: Severitas, onSelect: (Severitas) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
        Severitas.entries.forEach { sev ->
            val accent = severityColor(sev)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .background(if (sev == selected) accent else PaperHi)
                    .border(1.5.dp, accent)
                    .clickable { onSelect(sev) }
                    .padding(vertical = 12.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    sev.label,
                    color = if (sev == selected) Color.White else accent,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}

@Composable
private fun ChoiceChip(
    label: String,
    selected: Boolean,
    accent: Color,
    onClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(vertical = 3.dp)
            .background(if (selected) accent else PaperHi)
            .border(1.4.dp, accent)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        Text(
            label,
            color = if (selected) PaperHi else accent,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
        )
    }
}

@Composable
private fun PrimaryButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(54.dp),
        shape = RoundedCornerShape(2.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Ink,
            contentColor = PaperHi,
        ),
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.5.sp,
        )
    }
}

@Composable
private fun SharpDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(1.5.dp)
            .background(Ink),
    )
}

// ───────────────────────────────────────────────────────────────────────────
// Helpers
// ───────────────────────────────────────────────────────────────────────────

private fun displayName(kategori: Kategori): String = when (kategori) {
    Kategori.PARKIR_LIAR -> "parkir liar."
    Kategori.TROTOAR_RUSAK -> "trotoar rusak."
    Kategori.HALANGAN_PERMANEN -> "halangan permanen."
    Kategori.UBIN_DIFABEL_BERMASALAH -> "ubin difabel bermasalah."
    Kategori.TROTOAR_ABSEN -> "trotoar absen."
    Kategori.DRAINASE -> "drainase."
    Kategori.NIHIL -> "tidak ada pelanggaran."
    Kategori.BUKAN_TROTOAR -> "bukan trotoar."
    Kategori.LAINNYA -> "lainnya."
}

/** Renders one or more kategori stacked on separate lines, in enum declaration order. */
private fun displayNameSet(kategori: Set<Kategori>): String {
    val ordered = Kategori.entries.filter { it in kategori }
    return ordered.joinToString("\n") { displayName(it) }
}

private fun severityColor(s: Severitas): Color = when (s) {
    Severitas.RENDAH -> SevRendah
    Severitas.SEDANG -> SevSedang
    Severitas.TINGGI -> SevTinggi
}

private fun severityCopy(s: Severitas): String = when (s) {
    Severitas.RENDAH -> "Pejalan masih bisa lewat dengan mudah."
    Severitas.SEDANG -> "Pejalan harus menghindar atau memperlambat langkah."
    Severitas.TINGGI -> "Pejalan terpaksa turun ke jalan raya."
}

private fun walkabilityCopy(score: Int): String = when (score) {
    1 -> "Tidak dapat dilalui pejalan kaki."
    2 -> "Sangat sulit, banyak halangan."
    3 -> "Bisa dilalui dengan susah payah."
    4 -> "Cukup nyaman untuk berjalan."
    5 -> "Sangat baik, ramah pejalan dan difabel."
    else -> "—"
}

private fun confidenceColor(meter: Int): Color = when (meter) {
    5, 4 -> SevRendah
    3, 2 -> SevSedang
    1 -> SevTinggi
    else -> Mute
}

private fun confidenceLabel(meter: Int): String = when (meter) {
    5 -> "sangat yakin"
    4 -> "yakin"
    3 -> "cukup yakin"
    2 -> "kurang yakin"
    1 -> "tidak yakin"
    else -> "—"
}

@Composable
internal fun WalkabilityRow(score: Int, modifier: Modifier = Modifier) {
    // Retained for SavedScreen import compatibility.
    Column(modifier = modifier) {
        Text("Kondisi trotoar", style = MaterialTheme.typography.labelMedium, color = Mute)
        Spacer(Modifier.size(6.dp))
        WalkabilityBar(score = score, showLabel = true)
    }
}
