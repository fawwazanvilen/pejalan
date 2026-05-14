package id.pejalan.ui.detail

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import android.widget.Toast
import coil.compose.AsyncImage
import id.pejalan.data.Laporan
import id.pejalan.data.LaporanDb
import id.pejalan.data.LaporanStatus
import id.pejalan.data.SeedData
import id.pejalan.ml.ClassificationQueue
import id.pejalan.ml.Kategori
import id.pejalan.ml.Severitas
import id.pejalan.ml.isViolation
import id.pejalan.ml.primary
import id.pejalan.ml.toggle
import id.pejalan.ui.common.WalkabilityBar
import id.pejalan.ui.theme.Ink
import id.pejalan.ui.theme.Mute
import id.pejalan.ui.theme.PaperHi
import id.pejalan.ui.theme.SevRendah
import id.pejalan.ui.theme.SevSedang
import id.pejalan.ui.theme.SevTinggi
import java.io.File
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun DetailScreen(
    laporanId: String,
    db: LaporanDb,
    queue: ClassificationQueue,
    onBack: () -> Unit,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val seedMatch = remember(laporanId) { SeedData.entries().firstOrNull { it.id == laporanId } }
    val realFlow = remember(laporanId) {
        if (seedMatch != null) flowOf(seedMatch) else db.laporanDao().observeById(laporanId)
    }
    val laporan by realFlow.collectAsState(initial = null)

    if (laporan == null) {
        Surface(modifier = Modifier.fillMaxSize(), color = PaperHi) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Ink)
            }
        }
        return
    }

    val current = laporan!!
    val isSeed = seedMatch != null

    var kategori by remember(current.id) { mutableStateOf(current.kategori) }
    var severitas by remember(current.id) { mutableStateOf(current.severitas) }
    var walkability by remember(current.id) { mutableIntStateOf(current.walkability.coerceIn(0, 5)) }
    var rasional by remember(current.id) { mutableStateOf(current.rasional) }

    // BUKAN_TROTOAR / LAINNYA → zero walkability automatically
    LaunchedEffect(kategori) {
        val primary = kategori.primary
        if (primary == Kategori.BUKAN_TROTOAR || primary == Kategori.LAINNYA) {
            walkability = 0
        }
    }

    var showDeleteDialog by remember { mutableStateOf(false) }

    val dirty = kategori != current.kategori ||
        severitas != current.severitas ||
        walkability != current.walkability ||
        rasional != current.rasional

    val primaryKategori = kategori.primary
    val ratingPossible = primaryKategori != Kategori.BUKAN_TROTOAR && primaryKategori != Kategori.LAINNYA

    Surface(modifier = Modifier.fillMaxSize(), color = PaperHi) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        current.id,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Ink,
                        letterSpacing = 0.8.sp,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali", tint = Ink)
                    }
                },
                actions = {
                    if (!isSeed) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Hapus", tint = Ink)
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PaperHi,
                    titleContentColor = Ink,
                ),
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
            ) {
                Hero(current.photoPath)

                if (current.status == LaporanStatus.FAILED) {
                    Spacer(Modifier.height(16.dp))
                    RetryBanner(onRetry = {
                        scope.launch {
                            db.laporanDao().updateStatus(current.id, LaporanStatus.PENDING)
                            queue.enqueue()
                            Toast.makeText(context, "Diantri ulang", Toast.LENGTH_SHORT).show()
                            onBack()
                        }
                    })
                }

                Spacer(Modifier.height(14.dp))
                AuditMeta(current)
                Spacer(Modifier.height(14.dp))
                SharpDivider()
                Spacer(Modifier.height(18.dp))

                // 01 — Kondisi
                FieldLabel("01", "Kondisi trotoar")
                Spacer(Modifier.height(6.dp))
                SectionPrompt("Seberapa nyaman trotoar ini untuk pejalan?")
                Spacer(Modifier.height(12.dp))
                WalkabilityBar(
                    score = walkability,
                    interactive = ratingPossible && !isSeed,
                    onChange = { walkability = it },
                    showLabel = false,
                )
                Spacer(Modifier.height(10.dp))
                SelectedDescription(
                    when {
                        !ratingPossible -> "Tidak berlaku — foto bukan trotoar."
                        walkability == 0 -> "Belum dinilai. Ketuk segmen di atas."
                        else -> "$walkability dari 5 — ${walkabilityCopy(walkability)}"
                    }
                )

                Spacer(Modifier.height(22.dp))
                SharpDivider()
                Spacer(Modifier.height(18.dp))

                // 02 — Klasifikasi
                FieldLabel("02", "Klasifikasi masalah")
                Spacer(Modifier.height(6.dp))
                SectionPrompt("Apa yang Anda lihat di trotoar ini? Bisa lebih dari satu.")
                Spacer(Modifier.height(12.dp))
                DisplayHeadline(displayNameSet(kategori))
                Spacer(Modifier.height(14.dp))
                if (!isSeed) {
                    KategoriChips(
                        selected = kategori,
                        onSelect = { kategori = kategori.toggle(it) },
                    )
                }

                if (kategori.isViolation) {
                    Spacer(Modifier.height(22.dp))
                    SharpDivider()
                    Spacer(Modifier.height(18.dp))

                    // 03 — Severitas
                    FieldLabel("03", "Severitas")
                    Spacer(Modifier.height(6.dp))
                    SectionPrompt("Seberapa mengganggu pelanggaran ini bagi pejalan?")
                    Spacer(Modifier.height(12.dp))
                    SeverityChips(
                        selected = severitas,
                        onSelect = { if (!isSeed) severitas = it },
                    )
                    Spacer(Modifier.height(10.dp))
                    SelectedDescription(severityCopy(severitas))
                }

                // 04 — Deskripsi
                Spacer(Modifier.height(22.dp))
                SharpDivider()
                Spacer(Modifier.height(18.dp))
                FieldLabel(
                    number = if (kategori.isViolation) "04" else "03",
                    text = "Deskripsi trotoar & masalah",
                )
                Spacer(Modifier.height(6.dp))
                SectionPrompt("Apa yang Anda amati di lapangan?")
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = rasional,
                    onValueChange = { if (!isSeed) rasional = it },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 8,
                    enabled = !isSeed,
                    shape = RoundedCornerShape(2.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Ink,
                        unfocusedBorderColor = Ink.copy(alpha = 0.4f),
                        focusedTextColor = Ink,
                        unfocusedTextColor = Ink,
                        disabledTextColor = Ink,
                        disabledBorderColor = Ink.copy(alpha = 0.3f),
                        cursorColor = Ink,
                    ),
                )

                Spacer(Modifier.height(28.dp))
            }

            // Save bar
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = PaperHi,
                shadowElevation = 4.dp,
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            db.laporanDao().updateUserContent(
                                id = current.id,
                                kategori = kategori,
                                severitas = severitas,
                                walkability = walkability,
                                rasional = rasional,
                            )
                            onBack()
                        }
                    },
                    enabled = dirty && !isSeed,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp)
                        .height(54.dp),
                    shape = RoundedCornerShape(2.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Ink,
                        contentColor = PaperHi,
                        disabledContainerColor = Ink.copy(alpha = 0.3f),
                        disabledContentColor = PaperHi.copy(alpha = 0.7f),
                    ),
                ) {
                    Text(
                        when {
                            isSeed -> "Contoh — tidak bisa disunting"
                            dirty -> "Simpan perubahan"
                            else -> "Tidak ada perubahan"
                        },
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus laporan?") },
            text = { Text("Laporan ${current.id} akan dihapus dari linimasa dan peta.") },
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    scope.launch {
                        if (current.photoPath.isNotEmpty()) {
                            runCatching { File(current.photoPath).delete() }
                        }
                        db.laporanDao().deleteById(current.id)
                        onBack()
                    }
                }) {
                    Text("Hapus", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            },
        )
    }
}

// ───────────────────────────────────────────────────────────────────────────
// Components — mirror ResultSheet's visual vocabulary
// ───────────────────────────────────────────────────────────────────────────

@Composable
private fun Hero(photoPath: String) {
    val hasFile = photoPath.isNotEmpty() && File(photoPath).exists()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(2.dp))
            .background(Ink.copy(alpha = 0.1f)),
        contentAlignment = Alignment.Center,
    ) {
        if (hasFile) {
            AsyncImage(
                model = File(photoPath),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Text(
                "Foto contoh",
                style = MaterialTheme.typography.bodyMedium,
                color = Mute,
                fontStyle = FontStyle.Italic,
            )
        }
    }
}

@Composable
private fun AuditMeta(laporan: Laporan) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(
            relativeTime(laporan.createdAt),
            style = MaterialTheme.typography.labelMedium,
            color = Mute,
        )
        Spacer(Modifier.width(8.dp))
        Text(
            "·",
            color = Mute,
        )
        Spacer(Modifier.width(8.dp))
        Text(
            "%.4f, %.4f".format(laporan.lat, laporan.lng),
            fontFamily = FontFamily.Monospace,
            style = MaterialTheme.typography.labelSmall,
            color = Mute,
        )
        Spacer(Modifier.weight(1f))
        if (laporan.userCorrected) {
            Box(
                modifier = Modifier
                    .background(Ink.copy(alpha = 0.08f))
                    .padding(horizontal = 6.dp, vertical = 2.dp),
            ) {
                Text(
                    "disunting",
                    style = MaterialTheme.typography.labelSmall,
                    color = Ink,
                    fontStyle = FontStyle.Italic,
                )
            }
        }
    }
}

@Composable
private fun RetryBanner(onRetry: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            "Klasifikasi gagal. Coba ulang?",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onErrorContainer,
            modifier = Modifier.weight(1f),
        )
        TextButton(onClick = onRetry) {
            Text("Coba lagi", color = MaterialTheme.colorScheme.onErrorContainer)
        }
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
private fun SectionPrompt(text: String) {
    Text(text, style = MaterialTheme.typography.bodyMedium, color = Mute)
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

@Composable
private fun DisplayHeadline(text: String) {
    Text(
        text = text,
        fontSize = 32.sp,
        lineHeight = 34.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.8).sp,
        color = Ink,
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
private fun SharpDivider() {
    Box(modifier = Modifier.fillMaxWidth().height(1.5.dp).background(Ink))
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

private fun relativeTime(ms: Long, now: Long = System.currentTimeMillis()): String {
    val diff = (now - ms).coerceAtLeast(0)
    val minutes = diff / 60_000
    val hours = diff / 3_600_000
    val days = diff / 86_400_000
    return when {
        minutes < 1 -> "baru saja"
        minutes < 60 -> "$minutes menit lalu"
        hours < 24 -> "$hours jam lalu"
        days < 2 -> "kemarin"
        days < 7 -> "$days hari lalu"
        else -> "${days / 7} minggu lalu"
    }
}
