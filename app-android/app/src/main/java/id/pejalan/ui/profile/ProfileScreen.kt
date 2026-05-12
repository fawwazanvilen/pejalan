package id.pejalan.ui.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.pejalan.data.LaporanDb
import id.pejalan.ui.theme.Indigo
import id.pejalan.ui.theme.IndigoTint
import id.pejalan.ui.theme.SevRendah
import id.pejalan.ui.theme.SevSedang
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(db: LaporanDb) {
    val total by db.laporanDao().observeTotal().collectAsState(initial = 0)
    val today by db.laporanDao().observeCountSince(startOfToday()).collectAsState(initial = 0)
    val nihil by db.laporanDao().observeNihilCount().collectAsState(initial = 0)

    val badges = computeBadges(total = total, nihil = nihil)

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(modifier = Modifier.fillMaxSize()) {
            TopAppBar(
                title = {
                    Text(
                        "Profil",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.onBackground,
                ),
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
            ) {
                Header()

                Spacer(Modifier.height(28.dp))
                StatsRow(total = total, today = today, badgesCount = badges.size)

                Spacer(Modifier.height(28.dp))
                AdvocacyCard()

                Spacer(Modifier.height(28.dp))
                Text(
                    "Lencana",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                )
                Spacer(Modifier.height(12.dp))
                BadgesRow(badges)

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

@Composable
private fun Header() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Indigo),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                "P",
                color = Color.White,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.size(16.dp))
        Column {
            Text(
                "Warga Pejalan",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground,
            )
            Text(
                "Audit trotoar Jakarta",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun StatsRow(total: Int, today: Int, badgesCount: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
    ) {
        Stat(value = total.toString(), label = "Total laporan")
        Divider()
        Stat(value = today.toString(), label = "Hari ini")
        Divider()
        Stat(value = badgesCount.toString(), label = "Lencana")
    }
}

@Composable
private fun Stat(value: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            value,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.height(2.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun Divider() {
    Box(
        modifier = Modifier
            .size(width = 1.dp, height = 36.dp)
            .background(MaterialTheme.colorScheme.outlineVariant),
    )
}

@Composable
private fun AdvocacyCard() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(IndigoTint)
            .padding(20.dp),
    ) {
        Text(
            "Dampak Anda",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = Indigo,
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "Audit Anda telah dimasukkan ke 3 dokumen advokasi KPK 2026.",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Medium,
        )
        Spacer(Modifier.height(4.dp))
        Text(
            "Tim ITDP dan komunitas pejalan Bekasi turut menggunakan data ini.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private data class Badge(val emoji: String, val name: String, val description: String, val tint: Color)

private fun computeBadges(total: Int, nihil: Int): List<Badge> {
    val out = mutableListOf<Badge>()
    if (total >= 1) out += Badge("◆", "Pemula", "Audit pertama selesai", Indigo)
    if (total >= 5) out += Badge("◆◆", "Konsisten", "5+ audit", Indigo)
    if (total >= 20) out += Badge("◆◆◆", "Tekun", "20+ audit", Indigo)
    if (nihil >= 1) out += Badge("✓", "Apresiator", "Mengakui trotoar baik", SevRendah)
    return out
}

@Composable
private fun BadgesRow(badges: List<Badge>) {
    if (badges.isEmpty()) {
        Text(
            "Belum ada lencana. Mulai audit pertama Anda di tab Capture.",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        return
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        badges.forEach { badge ->
            BadgeChip(badge)
        }
    }
}

@Composable
private fun BadgeChip(badge: Badge) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(badge.tint),
            contentAlignment = Alignment.Center,
        ) {
            Text(badge.emoji, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
        }
        Spacer(Modifier.height(8.dp))
        Text(
            badge.name,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Text(
            badge.description,
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private fun startOfToday(): Long {
    val cal = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return cal.timeInMillis
}
