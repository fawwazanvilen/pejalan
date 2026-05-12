package id.pejalan.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val PejalanColorScheme = lightColorScheme(
    primary       = Indigo,
    onPrimary     = PaperHi,
    primaryContainer   = IndigoTint,
    onPrimaryContainer = IndigoInk,

    secondary     = SevSedang,
    onSecondary   = PaperHi,
    secondaryContainer   = SevSedangTint,
    onSecondaryContainer = InkSoft,

    tertiary      = SevRendah,
    onTertiary    = PaperHi,
    tertiaryContainer   = SevRendahTint,
    onTertiaryContainer = InkSoft,

    background    = Paper,
    onBackground  = Ink,
    surface       = PaperHi,
    onSurface     = Ink,
    surfaceVariant      = PaperLo,
    onSurfaceVariant    = InkSoft,

    error         = SevTinggi,
    onError       = PaperHi,
    errorContainer      = SevTinggiTint,
    onErrorContainer    = InkSoft,

    outline       = Mute,
    outlineVariant = MuteLo,
)

@Composable
fun PejalanTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = PejalanColorScheme,
        typography  = Typography,
        content     = content,
    )
}
