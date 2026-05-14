package id.pejalan.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import id.pejalan.ui.theme.Mute
import id.pejalan.ui.theme.SevRendah
import id.pejalan.ui.theme.SevSedang
import id.pejalan.ui.theme.SevTinggi

private val WalkabilityLabels = mapOf(
    1 to "Tidak dapat dilalui",
    2 to "Sangat sulit",
    3 to "Bisa dilalui",
    4 to "Cukup nyaman",
    5 to "Sangat baik",
)

fun walkabilityColor(score: Int): Color = when (score.coerceIn(0, 5)) {
    1 -> SevTinggi
    2 -> SevSedang
    3 -> Mute
    4 -> Color(0xFF7B8649) // light olive — between Sedang and Rendah
    5 -> SevRendah
    else -> Mute
}

fun walkabilityLabel(score: Int): String =
    WalkabilityLabels[score.coerceIn(0, 5)] ?: "—"

/**
 * Segmented horizontal bar showing walkability 1–5.
 *
 * @param compact a smaller version intended for inline list usage (no label, smaller segments).
 * @param interactive when true, tapping a segment sets the score; tapping the current top segment resets to 0.
 */
@Composable
fun WalkabilityBar(
    score: Int,
    modifier: Modifier = Modifier,
    compact: Boolean = false,
    interactive: Boolean = false,
    onChange: (Int) -> Unit = {},
    showLabel: Boolean = !compact,
) {
    val clamped = score.coerceIn(0, 5)
    val segWidth: Dp = if (compact) 14.dp else 56.dp
    val segHeight: Dp = if (compact) 4.dp else 32.dp
    val gap: Dp = if (compact) 2.dp else 6.dp
    val fillColor = if (clamped == 0) Mute else walkabilityColor(clamped)

    Column(modifier = modifier) {
        Row(horizontalArrangement = Arrangement.spacedBy(gap)) {
            repeat(5) { i ->
                val filled = i < clamped
                val segModifier = Modifier
                    .size(width = segWidth, height = segHeight)
                    .clip(RoundedCornerShape(if (compact) 3.dp else 2.dp))
                    .background(
                        if (filled) fillColor
                        else MaterialTheme.colorScheme.outlineVariant
                    )
                    .let { base ->
                        if (interactive && !compact) {
                            base.then(
                                Modifier.border(
                                    width = 1.dp,
                                    color = if (filled) fillColor
                                            else MaterialTheme.colorScheme.outline,
                                )
                            )
                        } else base
                    }
                    .let {
                        if (interactive) it.clickable {
                            val nextScore = i + 1
                            onChange(if (clamped == nextScore) 0 else nextScore)
                        } else it
                    }
                Box(
                    modifier = segModifier,
                    contentAlignment = androidx.compose.ui.Alignment.Center,
                ) {
                    if (interactive && !compact) {
                        Text(
                            (i + 1).toString(),
                            color = if (filled) androidx.compose.ui.graphics.Color.White
                                    else MaterialTheme.colorScheme.outline,
                            style = MaterialTheme.typography.labelLarge,
                            fontWeight = FontWeight.Bold,
                        )
                    }
                }
            }
        }
        if (showLabel) {
            Spacer(Modifier.height(6.dp))
            Text(
                text = if (clamped == 0) "—" else "$clamped/5 · ${walkabilityLabel(clamped)}",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium,
            )
        }
    }
}
