package com.nexo.banca.ui.components

import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.shadow
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexo.banca.ui.theme.Ink
import com.nexo.banca.ui.theme.Accent
import com.nexo.banca.ui.theme.Paper
import com.nexo.banca.ui.theme.AccentSoft
import com.nexo.banca.ui.theme.Surface

@Composable
fun NexoHeader(title: String, subtitle: String, action: String? = null, onAction: () -> Unit = {}) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .background(Brush.horizontalGradient(listOf(Surface.copy(alpha = .95f), AccentSoft.copy(alpha = .82f))), RoundedCornerShape(bottomStart = 24.dp, bottomEnd = 24.dp))
            .statusBarsPadding()
            .padding(horizontal = 20.dp, vertical = 18.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(subtitle, color = Accent, fontSize = 11.sp, letterSpacing = 1.1.sp, fontWeight = FontWeight.Bold)
            Text(title, color = Ink, style = MaterialTheme.typography.headlineMedium)
        }
        action?.let { TextButton(onClick = onAction) { Text(it, color = Accent) } }
    }
}
