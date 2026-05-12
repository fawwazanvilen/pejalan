package id.pejalan.ui.saving

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class SavingPhase {
    Fetching,
    PermissionDenied,
    LocationTimeout,
}

@Composable
fun SavingScreen(
    phase: SavingPhase,
    onRetry: () -> Unit,
    onCancel: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier.fillMaxSize().padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
            Text(
                "Mengambil lokasi…",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(top = 24.dp),
            )
        }
    }

    when (phase) {
        SavingPhase.Fetching -> Unit
        SavingPhase.PermissionDenied -> AlertDialog(
            onDismissRequest = onCancel,
            title = { Text("Lokasi diperlukan") },
            text = {
                Text(
                    "Pejalan butuh akses lokasi untuk membuat audit yang akurat. " +
                        "Tanpa lokasi, laporan tidak bisa dipetakan."
                )
            },
            confirmButton = {
                TextButton(onClick = onRetry) { Text("Coba lagi") }
            },
            dismissButton = {
                TextButton(onClick = onCancel) { Text("Batal") }
            },
        )
        SavingPhase.LocationTimeout -> AlertDialog(
            onDismissRequest = onCancel,
            title = { Text("Lokasi tidak tersedia") },
            text = {
                Text(
                    "Tidak bisa menemukan lokasi dalam 5 detik. Coba di area " +
                        "yang lebih terbuka dengan sinyal GPS yang baik."
                )
            },
            confirmButton = {
                TextButton(onClick = onRetry) { Text("Coba lagi") }
            },
            dismissButton = {
                TextButton(onClick = onCancel) { Text("Batal") }
            },
        )
    }
}
