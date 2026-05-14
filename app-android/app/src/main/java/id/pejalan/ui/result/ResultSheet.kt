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
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.pejalan.ml.BBox
import id.pejalan.ml.Classification
import id.pejalan.ml.Kategori
import id.pejalan.ml.Severitas
import id.pejalan.ui.common.WalkabilityBar
import id.pejalan.ui.theme.HiVis
import id.pejalan.ui.theme.Ink
import id.pejalan.ui.theme.Mute
import id.pejalan.ui.theme.PaperHi
import id.pejalan.ui.theme.SevRendah
import id.pejalan.ui.theme.SevSedang
import id.pejalan.ui.theme.SevSedangTint
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

    var current by remember(classification) { mutableStateOf(classification.kategori) }
    val corrected = remember(current, classification) {
        classification.copy(kategori = current)
    }
    val userCorrected = current != classification.kategori

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
            PhotoHero(bitmap = bitmap, bbox = classification.bbox, showBbox = corrected.kategori.isViolation)
            Spacer(Modifier.height(14.dp))

            AuditTimeHeader()
            SharpDivider()
            Spacer(Modifier.height(14.dp))

            // 01 Kategori terdeteksi
            FieldLabel("01", "Kategori terdeteksi")
            Spacer(Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.Bottom) {
                DisplayHeadline(
                    displayName(corrected.kategori),
                    modifier = Modifier.weight(1f),
                )
                if (corrected.kategori.isViolation) {
                    SeverityStamp(corrected.severitas)
                }
            }

            Spacer(Modifier.height(16.dp))
            ConfidenceBlocks(corrected.meter)

            if (corrected.walkability > 0) {
                Spacer(Modifier.height(16.dp))
                FieldLabel("·", "Kelayakan pejalan kaki")
                Spacer(Modifier.height(6.dp))
                WalkabilityBar(score = corrected.walkability, showLabel = true)
            }

            if (corrected.meter in 1..3) {
                Spacer(Modifier.height(14.dp))
                AgakRaguBanner()
            }

            if (corrected.rasional.isNotBlank()) {
                Spacer(Modifier.height(16.dp))
                SharpDivider()
                Spacer(Modifier.height(12.dp))
                FieldLabel("02", "Apa yang dilihat")
                Spacer(Modifier.height(6.dp))
                Text(
                    corrected.rasional,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Medium,
                        color = Ink,
                    ),
                )
            }

            Spacer(Modifier.height(16.dp))
            SharpDivider()
            Spacer(Modifier.height(12.dp))
            FieldLabel("03", "Bukan ini? Ganti kategori")
            Spacer(Modifier.height(8.dp))
            CorrectionChips(selected = current, onSelect = { current = it })

            Spacer(Modifier.height(20.dp))
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
                        "Simpan ke linimasa tetap",
                        color = Mute,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}

// ───────────────────────────────────────────────────────────────────────────
// Components — the visual identity from the midfi
// ───────────────────────────────────────────────────────────────────────────

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
                val left = bbox.x * size.width
                val top = bbox.y * size.height
                val w = bbox.w * size.width
                val h = bbox.h * size.height
                drawRect(
                    color = HiVis,
                    topLeft = Offset(left, top),
                    size = Size(w, h),
                    style = Stroke(width = 3.dp.toPx()),
                )
            }
            // Tiny corner label
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
            "PJ-BARU",
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Mute,
            letterSpacing = 1.6.sp,
        )
        Spacer(Modifier.weight(1f))
        Text(
            time,
            fontFamily = FontFamily.Monospace,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium,
            color = Mute,
            letterSpacing = 1.2.sp,
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
        Spacer(Modifier.width(8.dp))
        Text(
            text.uppercase(),
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = Mute,
                letterSpacing = 1.4.sp,
            ),
        )
    }
}

@Composable
private fun DisplayHeadline(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        modifier = modifier,
        fontSize = 36.sp,
        lineHeight = 36.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-1.2).sp,
        color = Ink,
    )
}

@Composable
private fun SeverityStamp(severity: Severitas) {
    val color = severityColor(severity)
    Box(
        modifier = Modifier
            .rotate(-2f)
            .border(width = 2.5.dp, color = color)
            .padding(horizontal = 12.dp, vertical = 5.dp),
    ) {
        Text(
            severity.label.uppercase(),
            color = color,
            fontFamily = FontFamily.Monospace,
            fontSize = 13.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 2.sp,
        )
    }
}

@Composable
private fun ConfidenceBlocks(meter: Int) {
    val clamped = meter.coerceIn(0, 5)
    val color = confidenceColor(clamped)
    Column {
        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            repeat(5) { i ->
                val filled = i < clamped
                Box(
                    modifier = Modifier
                        .size(width = 28.dp, height = 12.dp)
                        .border(1.5.dp, color)
                        .background(if (filled) color else Color.Transparent),
                )
            }
        }
        Spacer(Modifier.height(6.dp))
        Text(
            confidenceLabel(clamped).uppercase(),
            fontFamily = FontFamily.Monospace,
            fontSize = 10.5.sp,
            fontWeight = FontWeight.Bold,
            color = color,
            letterSpacing = 1.8.sp,
        )
    }
}

@Composable
private fun AgakRaguBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(SevSedangTint)
            .border(1.5.dp, SevSedang)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            "!",
            color = SevSedang,
            fontWeight = FontWeight.ExtraBold,
            fontFamily = FontFamily.Monospace,
            fontSize = 16.sp,
        )
        Spacer(Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                "Agak ragu.",
                color = SevSedang,
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                "Periksa kategori — Anda bisa memperbaiki di bawah.",
                color = Ink,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CorrectionChips(selected: Kategori, onSelect: (Kategori) -> Unit) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        Kategori.entries.forEach { k ->
            val isSelected = k == selected
            Box(
                modifier = Modifier
                    .padding(vertical = 3.dp)
                    .background(if (isSelected) Ink else PaperHi)
                    .border(1.4.dp, Ink)
                    .clickable { onSelect(k) }
                    .padding(horizontal = 10.dp, vertical = 6.dp),
            ) {
                Text(
                    k.label.lowercase(),
                    color = if (isSelected) PaperHi else Ink,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Medium,
                )
            }
        }
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
    Kategori.PARKIR_LIAR -> "parkir\nliar."
    Kategori.TROTOAR_RUSAK -> "trotoar\nrusak."
    Kategori.HALANGAN_PERMANEN -> "halangan\npermanen."
    Kategori.UBIN_DIFABEL_BERMASALAH -> "ubin difabel\nbermasalah."
    Kategori.TROTOAR_ABSEN -> "trotoar\nabsen."
    Kategori.DRAINASE -> "drainase."
    Kategori.NIHIL -> "tidak ada\npelanggaran."
    Kategori.BUKAN_TROTOAR -> "bukan\ntrotoar."
    Kategori.LAINNYA -> "lainnya."
}

private fun severityColor(s: Severitas): Color = when (s) {
    Severitas.RENDAH -> SevRendah
    Severitas.SEDANG -> SevSedang
    Severitas.TINGGI -> SevTinggi
}

private fun confidenceColor(meter: Int): Color = when (meter) {
    5 -> SevRendah
    4 -> SevRendah
    3 -> SevSedang
    2 -> SevSedang
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
        Text(
            "Kelayakan",
            style = MaterialTheme.typography.labelMedium,
            color = Mute,
        )
        Spacer(Modifier.size(6.dp))
        WalkabilityBar(score = score, showLabel = true)
    }
}
