package id.pejalan.ui.feed

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import id.pejalan.data.Laporan
import id.pejalan.data.LaporanDb
import id.pejalan.data.SeedData
import id.pejalan.ml.Severitas
import id.pejalan.ui.theme.Mute
import id.pejalan.ui.theme.SevRendah
import id.pejalan.ui.theme.SevSedang
import id.pejalan.ui.theme.SevTinggi
import java.io.File

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FeedScreen(db: LaporanDb) {
    val real by db.laporanDao().observeAll().collectAsState(initial = emptyList())
    val now = System.currentTimeMillis()
    val seed = remember { SeedData.entries(now) }
    val all = remember(real) { (real + seed).sortedByDescending { it.createdAt } }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        "Linimasa",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
            )

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp),
            ) {
                items(all, key = { it.id }) { laporan ->
                    LaporanCard(laporan)
                }
            }
        }
    }
}

@Composable
private fun LaporanCard(laporan: Laporan) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Thumbnail(
            photoPath = laporan.photoPath,
            tintWhenMissing = thumbTint(laporan),
            label = laporan.kategori.label,
        )

        Spacer(Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    laporan.kategori.label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier.weight(1f),
                )
                if (laporan.kategori.isViolation) {
                    SeverityChipSmall(laporan.severitas)
                }
            }
            Spacer(Modifier.height(6.dp))
            Text(
                laporan.rasional,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 2,
            )
            Spacer(Modifier.height(8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    laporan.id,
                    style = MaterialTheme.typography.labelSmall,
                    fontFamily = FontFamily.Monospace,
                    color = MaterialTheme.colorScheme.outline,
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    "· ${relativeTime(laporan.createdAt)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline,
                )
            }
        }
    }
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
            .size(72.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(tintWhenMissing),
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
            // Initial letter as placeholder
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
private fun SeverityChipSmall(severity: Severitas) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(severityColor(severity))
            .padding(horizontal = 8.dp, vertical = 3.dp),
    ) {
        Text(
            severity.label,
            color = Color.White,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
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
