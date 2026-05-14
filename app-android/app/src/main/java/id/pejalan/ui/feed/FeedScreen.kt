package id.pejalan.ui.feed

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import id.pejalan.data.Laporan
import id.pejalan.data.LaporanDb
import id.pejalan.data.LaporanStatus
import id.pejalan.data.SeedData
import id.pejalan.ml.Kategori
import id.pejalan.ml.Severitas
import id.pejalan.ui.common.WalkabilityBar
import id.pejalan.ui.theme.Ink
import id.pejalan.ui.theme.Mute
import id.pejalan.ui.theme.PaperHi
import id.pejalan.ui.theme.SevRendah
import id.pejalan.ui.theme.SevSedang
import id.pejalan.ui.theme.SevTinggi
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(
    db: LaporanDb,
    onOpenDetail: (String) -> Unit = {},
) {
    val real by db.laporanDao().observeAll().collectAsState(initial = emptyList())
    val now = System.currentTimeMillis()
    val seed = remember { SeedData.entries(now) }
    val all = remember(real) { (real + seed).sortedByDescending { it.createdAt } }

    val listState = rememberLazyListState()
    val firstId = all.firstOrNull()?.id
    LaunchedEffect(firstId) {
        if (firstId != null) listState.scrollToItem(0)
    }

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Linimasa",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            "${all.size} laporan Jakarta",
                            style = MaterialTheme.typography.labelMedium,
                            color = Mute,
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
            )

            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                items(all, key = { it.id }) { laporan ->
                    LaporanCard(laporan, onClick = { onOpenDetail(laporan.id) })
                }
            }
        }
    }
}

@Composable
private fun LaporanCard(laporan: Laporan, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(PaperHi)
            .border(1.4.dp, Ink)
            .clickable { onClick() }
            .padding(14.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                laporan.id,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = Ink,
                letterSpacing = 0.8.sp,
            )
            Spacer(Modifier.width(8.dp))
            Text(
                relativeTime(laporan.createdAt),
                style = MaterialTheme.typography.labelSmall,
                color = Mute,
            )
            Spacer(Modifier.weight(1f))
            when {
                laporan.status == LaporanStatus.PENDING -> PendingPill()
                laporan.status == LaporanStatus.FAILED -> FailedPill()
                laporan.kategori.isViolation -> SeverityTag(laporan.severitas)
            }
        }

        Spacer(Modifier.height(10.dp))
        Box(modifier = Modifier.fillMaxWidth().height(1.dp).background(Ink.copy(alpha = 0.4f)))
        Spacer(Modifier.height(12.dp))

        Row {
            Thumbnail(
                photoPath = laporan.photoPath,
                tintWhenMissing = thumbTint(laporan),
                label = laporan.kategori.label,
            )
            Spacer(Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                when (laporan.status) {
                    LaporanStatus.PENDING -> {
                        Text(
                            "Menunggu klasifikasi…",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = Mute,
                        )
                    }
                    LaporanStatus.FAILED -> {
                        Text(
                            "Gagal menganalisis",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.error,
                        )
                        Spacer(Modifier.height(4.dp))
                        Text(
                            "Ketuk untuk mencoba ulang.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Mute,
                        )
                    }
                    LaporanStatus.CLASSIFIED -> {
                        Text(
                            displayName(laporan.kategori),
                            fontSize = 20.sp,
                            lineHeight = 22.sp,
                            fontWeight = FontWeight.ExtraBold,
                            letterSpacing = (-0.4).sp,
                            color = Ink,
                        )
                        if (laporan.walkability > 0) {
                            Spacer(Modifier.height(8.dp))
                            WalkabilityBar(score = laporan.walkability, compact = true)
                        }
                        if (laporan.rasional.isNotBlank()) {
                            Spacer(Modifier.height(8.dp))
                            Text(
                                laporan.rasional,
                                style = MaterialTheme.typography.bodySmall,
                                color = Mute,
                                maxLines = 2,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Thumbnail(photoPath: String, tintWhenMissing: Color, label: String) {
    val hasFile = photoPath.isNotEmpty() && File(photoPath).exists()
    Box(
        modifier = Modifier
            .size(76.dp)
            .background(tintWhenMissing)
            .border(1.2.dp, Ink),
        contentAlignment = Alignment.Center,
    ) {
        if (hasFile) {
            AsyncImage(
                model = File(photoPath),
                contentDescription = label,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
            )
        } else {
            Text(
                label.take(1).uppercase(),
                color = Color.White,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun SeverityTag(severity: Severitas) {
    val color = severityColor(severity)
    Box(
        modifier = Modifier
            .background(color)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            severity.label,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = 0.4.sp,
        )
    }
}

@Composable
private fun PendingPill() {
    Row(
        modifier = Modifier
            .background(Mute.copy(alpha = 0.15f))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CircularProgressIndicator(
            modifier = Modifier.size(10.dp),
            strokeWidth = 1.5.dp,
            color = Mute,
        )
        Spacer(Modifier.width(6.dp))
        Text(
            "Menganalisis",
            style = MaterialTheme.typography.labelSmall,
            color = Mute,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun FailedPill() {
    Box(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.errorContainer)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            "Gagal",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onErrorContainer,
            fontWeight = FontWeight.Medium,
        )
    }
}

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

private fun thumbTint(laporan: Laporan): Color =
    if (laporan.kategori.isViolation) severityColor(laporan.severitas) else Mute

private fun severityColor(s: Severitas) = when (s) {
    Severitas.RENDAH -> SevRendah
    Severitas.SEDANG -> SevSedang
    Severitas.TINGGI -> SevTinggi
}

private fun relativeTime(
    ms: Long,
    now: Long = System.currentTimeMillis(),
): String {
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
