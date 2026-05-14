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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
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
import id.pejalan.ml.Severitas
import id.pejalan.ui.common.WalkabilityBar
import id.pejalan.ui.theme.Ink
import id.pejalan.ui.theme.Mute
import id.pejalan.ui.theme.MuteLo
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
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Ink,
                            letterSpacing = (-0.5).sp,
                        )
                        Text(
                            "${all.size} LAPORAN · JAKARTA",
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Mute,
                            letterSpacing = 1.6.sp,
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
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
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
            .padding(vertical = 6.dp)
            .background(PaperHi)
            .border(1.4.dp, Ink)
            .clickable { onClick() }
            .padding(14.dp),
    ) {
        // Header row: audit code · time · severity stamp (if violation)
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                laporan.id,
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                color = Mute,
                letterSpacing = 1.4.sp,
            )
            Spacer(Modifier.width(8.dp))
            Text(
                "·",
                fontFamily = FontFamily.Monospace,
                fontSize = 11.sp,
                color = MuteLo,
            )
            Spacer(Modifier.width(8.dp))
            Text(
                relativeTime(laporan.createdAt).uppercase(),
                fontFamily = FontFamily.Monospace,
                fontSize = 10.sp,
                fontWeight = FontWeight.Medium,
                color = Mute,
                letterSpacing = 1.2.sp,
            )
            Spacer(Modifier.weight(1f))
            if (laporan.status == LaporanStatus.CLASSIFIED && laporan.kategori.isViolation) {
                SeverityStampSmall(laporan.severitas)
            }
        }

        Spacer(Modifier.height(10.dp))
        Box(modifier = Modifier.fillMaxWidth().height(1.2.dp).background(Ink))
        Spacer(Modifier.height(12.dp))

        // Body row: thumbnail + content
        Row(verticalAlignment = Alignment.Top) {
            Thumbnail(
                photoPath = laporan.photoPath,
                tintWhenMissing = thumbTint(laporan),
                label = laporan.kategori.label,
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                when (laporan.status) {
                    LaporanStatus.PENDING -> PendingBody()
                    LaporanStatus.FAILED -> FailedBody()
                    LaporanStatus.CLASSIFIED -> ClassifiedBody(laporan)
                }
            }
        }
    }
}

@Composable
private fun ClassifiedBody(laporan: Laporan) {
    Text(
        laporan.kategori.label.lowercase() + ".",
        fontSize = 22.sp,
        lineHeight = 24.sp,
        fontWeight = FontWeight.ExtraBold,
        letterSpacing = (-0.6).sp,
        color = Ink,
    )
    if (laporan.rasional.isNotBlank()) {
        Spacer(Modifier.height(6.dp))
        Text(
            laporan.rasional,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            maxLines = 2,
        )
    }
    if (laporan.walkability > 0) {
        Spacer(Modifier.height(8.dp))
        WalkabilityBar(score = laporan.walkability, compact = true)
    }
}

@Composable
private fun PendingBody() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        CircularProgressIndicator(
            modifier = Modifier.size(14.dp),
            strokeWidth = 2.dp,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.width(10.dp))
        Text(
            "menganalisis…",
            fontSize = 18.sp,
            fontWeight = FontWeight.SemiBold,
            color = Mute,
        )
    }
    Spacer(Modifier.height(4.dp))
    Text(
        "MASUK ANTRIAN — TUNGGU SEBENTAR",
        fontFamily = FontFamily.Monospace,
        fontSize = 9.5.sp,
        fontWeight = FontWeight.Bold,
        color = MuteLo,
        letterSpacing = 1.4.sp,
    )
}

@Composable
private fun FailedBody() {
    Text(
        "gagal menganalisis.",
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = SevTinggi,
    )
    Spacer(Modifier.height(4.dp))
    Text(
        "TAP UNTUK COBA ULANG",
        fontFamily = FontFamily.Monospace,
        fontSize = 9.5.sp,
        fontWeight = FontWeight.Bold,
        color = SevTinggi,
        letterSpacing = 1.4.sp,
    )
}

@Composable
private fun Thumbnail(
    photoPath: String,
    tintWhenMissing: Color,
    label: String,
) {
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
                modifier = Modifier.fillMaxWidth().fillMaxSize(),
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
private fun SeverityStampSmall(severity: Severitas) {
    val color = severityColor(severity)
    Box(
        modifier = Modifier
            .rotate(-1.5f)
            .border(1.8.dp, color)
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            severity.label.uppercase(),
            color = color,
            fontFamily = FontFamily.Monospace,
            fontSize = 10.sp,
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.6.sp,
        )
    }
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
        minutes < 1 -> "Baru saja"
        minutes < 60 -> "$minutes menit lalu"
        hours < 24 -> "$hours jam lalu"
        days < 2 -> "Kemarin"
        days < 7 -> "$days hari lalu"
        else -> "${days / 7} minggu lalu"
    }
}
