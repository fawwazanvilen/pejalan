package id.pejalan.ui.camera

import android.graphics.Bitmap
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun ConfirmPhotoOverlay(
    bitmap: Bitmap,
    onUse: () -> Unit,
    onRetake: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.Black,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "Foto yang baru saja diambil",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Fit,
            )

            // Bottom action panel with scrim
            Column(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.75f))
                    .padding(horizontal = 24.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    "Pakai foto ini untuk audit?",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.White,
                    fontWeight = FontWeight.SemiBold,
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedButton(
                        onClick = onRetake,
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                    ) {
                        Text("Ulang", color = Color.White)
                    }
                    Button(
                        onClick = onUse,
                        modifier = Modifier.weight(1f).height(52.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                        ),
                    ) {
                        Text("Pakai")
                    }
                }
            }
        }
    }
}
