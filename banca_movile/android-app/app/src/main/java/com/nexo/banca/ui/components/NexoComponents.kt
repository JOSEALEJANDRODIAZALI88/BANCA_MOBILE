package com.nexo.banca.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexo.banca.ui.theme.Accent
import com.nexo.banca.ui.theme.AccentSoft
import com.nexo.banca.ui.theme.Ink
import com.nexo.banca.ui.theme.Muted
import com.nexo.banca.ui.theme.Outline
import com.nexo.banca.ui.theme.Surface
import java.text.NumberFormat
import java.util.Locale

fun formatCurrency(value: Long): String = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"))
    .format(kotlin.math.abs(value)).replace(",00", "")

@Composable
fun NexoCard(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    val shape = RoundedCornerShape(22.dp)
    Card(
        modifier = modifier.shadow(3.dp, shape),
        shape = shape,
        colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = .92f)),
        border = BorderStroke(1.dp, AccentSoft),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        content = { content() }
    )
}

@Composable
fun NexoPrimaryButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp),
        shape = RoundedCornerShape(18.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Ink)
    ) { Text(text, fontWeight = FontWeight.SemiBold) }
}

@Composable
fun NexoReceiptLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp)) {
        Text(label, color = Muted, fontSize = 13.sp)
        Spacer(Modifier.weight(1f))
        Text(value, color = Ink, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}

@Composable
fun NexoBrand(light: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier.size(30.dp).background(if (light) Surface.copy(alpha = .14f) else Ink, RoundedCornerShape(7.dp)),
            contentAlignment = Alignment.Center
        ) { Text("N", color = Surface, fontWeight = FontWeight.Bold, fontSize = 16.sp) }
        Spacer(Modifier.width(9.dp))
        Text("Nexo", color = if (light) Surface else Ink, fontWeight = FontWeight.SemiBold, fontSize = 20.sp)
    }
}

@Composable
fun NexoSection(label: String, title: String, action: String? = null, onAction: () -> Unit = {}) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
        Column(Modifier.weight(1f)) {
            Text(label, color = Accent, fontSize = 11.sp, letterSpacing = 1.1.sp, fontWeight = FontWeight.Bold)
            Text(title, color = Ink, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
        }
        action?.let { TextButton(onClick = onAction) { Text(it) } }
    }
}
