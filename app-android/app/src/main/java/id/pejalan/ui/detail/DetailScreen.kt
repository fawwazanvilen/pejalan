package id.pejalan.ui.detail

import androidx.compose.foundation.background
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
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
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
import id.pejalan.ui.common.WalkabilityBar
import java.io.File
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

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

    // Real laporan come from Room. Seed entries are in-memory only — handle separately.
    val seedMatch = remember(laporanId) { SeedData.entries().firstOrNull { it.id == laporanId } }
    val realFlow = remember(laporanId) {
        if (seedMatch != null) flowOf(seedMatch) else db.laporanDao().observeById(laporanId)
    }
    val laporan by realFlow.collectAsState(initial = null)

    if (laporan == null) {
        Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
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

    var showDeleteDialog by remember { mutableStateOf(false) }

    val dirty = kategori != current.kategori ||
        severitas != current.severitas ||
        walkability != current.walkability ||
        rasional != current.rasional

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        current.id,
                        style = MaterialTheme.typography.titleMedium,
                        fontFamily = FontFamily.Monospace,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                actions = {
                    if (!isSeed) {
                        IconButton(onClick = { showDeleteDialog = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Hapus")
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
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

                Spacer(Modifier.height(20.dp))
                Label("Kategori")
                Spacer(Modifier.height(8.dp))
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Kategori.entries.forEach { k ->
                        FilterChip(
                            selected = kategori == k,
                            onClick = { kategori = k },
                            label = { Text(k.label) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            ),
                        )
                    }
                }

                if (kategori.isViolation) {
                    Spacer(Modifier.height(20.dp))
                    Label("Severitas")
                    Spacer(Modifier.height(8.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Severitas.entries.forEach { sev ->
                            FilterChip(
                                selected = severitas == sev,
                                onClick = { severitas = sev },
                                label = { Text(sev.label) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                    selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                                ),
                            )
                        }
                    }
                }

                Spacer(Modifier.height(20.dp))
                Label("Kelayakan pejalan kaki")
                Spacer(Modifier.height(4.dp))
                Text(
                    "Ketuk segmen untuk menilai (1–5). 0 = tidak berlaku.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(12.dp))
                WalkabilityBar(
                    score = walkability,
                    interactive = true,
                    onChange = { walkability = it },
                    showLabel = true,
                )

                Spacer(Modifier.height(20.dp))
                LabelItalic("rationale")
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = rasional,
                    onValueChange = { rasional = it },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3,
                    maxLines = 8,
                    placeholder = { Text("Jelaskan apa yang terlihat dan dampaknya.") },
                )

                Spacer(Modifier.height(24.dp))
                Metadata(current)
                Spacer(Modifier.height(28.dp))
            }

            // Save button at bottom
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.background,
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
                        .height(52.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    ),
                ) {
                    Text(
                        if (isSeed) "Contoh — tidak bisa disunting"
                        else if (dirty) "Simpan perubahan"
                        else "Tidak ada perubahan",
                        style = MaterialTheme.typography.titleMedium,
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
                        // Best-effort: delete the photo file too.
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

@Composable
private fun RetryBanner(onRetry: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
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
private fun Hero(photoPath: String) {
    val hasFile = photoPath.isNotEmpty() && File(photoPath).exists()
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(220.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant),
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
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun Label(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onBackground,
    )
}

@Composable
private fun LabelItalic(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.SemiBold,
        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
        color = MaterialTheme.colorScheme.onBackground,
    )
}

@Composable
private fun Metadata(laporan: Laporan) {
    Column {
        Label("Metadata")
        Spacer(Modifier.height(6.dp))
        MetaRow("Dibuat", relativeTime(laporan.createdAt))
        MetaRow("Lokasi", "%.5f, %.5f".format(laporan.lat, laporan.lng))
        if (laporan.accuracyM > 0) {
            MetaRow("Akurasi", "± %.0f m".format(laporan.accuracyM))
        }
        MetaRow("Status", laporan.status.name)
        if (laporan.userCorrected) {
            MetaRow("Disunting", "Ya")
        }
    }
}

@Composable
private fun MetaRow(key: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
    ) {
        Text(
            key,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.width(96.dp),
        )
        Text(
            value,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface,
            fontFamily = if (key == "Lokasi" || key == "Akurasi") FontFamily.Monospace else null,
        )
    }
}

private fun relativeTime(ms: Long, now: Long = System.currentTimeMillis()): String {
    val diff = (now - ms).coerceAtLeast(0)
    val minutes = diff / 60_000
    val hours = diff / 3_600_000
    val days = diff / 86_400_000
    return when {
        minutes < 1 -> "Baru saja"
        minutes < 60 -> "$minutes menit lalu"
        hours < 24 -> "$hours jam lalu"
        days < 2 -> "Kemarin"
        days < 7 -> "$days hari lalu"
        else -> "${days / 7} minggu lalu"
    }
}
