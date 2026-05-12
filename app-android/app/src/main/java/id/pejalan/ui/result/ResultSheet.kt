package id.pejalan.ui.result

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import id.pejalan.ml.Classification
import id.pejalan.ml.Kategori
import id.pejalan.ml.Severitas
import id.pejalan.ui.theme.Mute
import id.pejalan.ui.theme.SevRendah
import id.pejalan.ui.theme.SevSedang
import id.pejalan.ui.theme.SevTinggi

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultSheet(
    classification: Classification,
    onDismiss: () -> Unit,
    onConfirm: () -> Unit,
    onSaveAnyway: () -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp),
        ) {
            if (classification.kategori.isViolation) {
                ViolationBody(classification)
                Spacer(Modifier.height(28.dp))
                PrimaryButton("Lanjutkan", onConfirm)
            } else {
                NonViolationBody(classification)
                Spacer(Modifier.height(28.dp))
                PrimaryButton("Kembali ke kamera", onConfirm)
                Spacer(Modifier.height(4.dp))
                TextButton(
                    onClick = onSaveAnyway,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text(
                        "Simpan ke linimasa tetap",
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        style = MaterialTheme.typography.labelLarge,
                    )
                }
            }
        }
    }
}

@Composable
private fun ViolationBody(classification: Classification) {
    Text(
        classification.kategori.label,
        style = MaterialTheme.typography.headlineSmall,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
    )

    Spacer(Modifier.height(12.dp))

    Row(verticalAlignment = Alignment.CenterVertically) {
        SeverityChip(classification.severitas)
        Spacer(Modifier.size(12.dp))
        KeyakinanMeter(classification.meter)
    }

    if (classification.walkability > 0) {
        Spacer(Modifier.height(16.dp))
        WalkabilityRow(classification.walkability)
    }

    if (classification.rasional.isNotBlank()) {
        Spacer(Modifier.height(20.dp))
        Text(
            classification.rasional,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
internal fun WalkabilityRow(score: Int, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Text(
            "Kelayakan",
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.size(8.dp))
        WalkabilityStars(score)
    }
}

@Composable
internal fun WalkabilityStars(score: Int) {
    val clamped = score.coerceIn(0, 5)
    Row {
        repeat(5) { i ->
            val filled = i < clamped
            Icon(
                imageVector = if (filled) Icons.Filled.Star else Icons.Filled.StarBorder,
                contentDescription = null,
                tint = if (filled) MaterialTheme.colorScheme.primary
                       else MaterialTheme.colorScheme.outlineVariant,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}

@Composable
private fun NonViolationBody(classification: Classification) {
    val (badgeColor, icon) = when (classification.kategori) {
        Kategori.NIHIL -> SevRendah to "✓"
        Kategori.BUKAN_TROTOAR -> Mute to "?"
        else -> Mute to "—"  // LAINNYA fallback
    }

    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(badgeColor),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                icon,
                fontSize = 30.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold,
            )
        }
        Spacer(Modifier.size(16.dp))
        Text(
            classification.kategori.label,
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.weight(1f),
        )
    }

    if (classification.walkability > 0) {
        Spacer(Modifier.height(16.dp))
        WalkabilityRow(classification.walkability)
    }

    if (classification.rasional.isNotBlank()) {
        Spacer(Modifier.height(20.dp))
        Text(
            classification.rasional,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }

}

@Composable
private fun SeverityChip(severity: Severitas) {
    val bg = when (severity) {
        Severitas.RENDAH -> SevRendah
        Severitas.SEDANG -> SevSedang
        Severitas.TINGGI -> SevTinggi
    }
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(bg)
            .padding(horizontal = 12.dp, vertical = 6.dp),
    ) {
        Text(
            severity.label,
            color = Color.White,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun KeyakinanMeter(meter: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(5) { i ->
            val filled = i < meter
            Box(
                modifier = Modifier
                    .size(width = 12.dp, height = 16.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        if (filled) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.outlineVariant
                    ),
            )
        }
    }
}

@Composable
private fun PrimaryButton(label: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
        ),
    ) {
        Text(label, style = MaterialTheme.typography.titleMedium)
    }
}
