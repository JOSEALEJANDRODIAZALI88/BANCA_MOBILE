package com.nexo.banca.ui.more

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexo.banca.ui.components.NexoHeader
import com.nexo.banca.ui.components.NexoSection
import com.nexo.banca.ui.theme.Accent
import com.nexo.banca.ui.theme.Ink
import com.nexo.banca.ui.theme.Outline
import com.nexo.banca.ui.theme.Surface

@Composable
fun MoreScreen(
    onPayments: () -> Unit,
    onCredits: () -> Unit,
    onServices: () -> Unit,
    onCalculator: () -> Unit,
    onTransfer: () -> Unit,
    onReceive: () -> Unit,
    onCards: () -> Unit,
    onProfile: () -> Unit,
    onSettings: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        NexoHeader("Mas", "TODAS LAS FUNCIONES")
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    NexoSection("PAGOS Y COBROS", "Operaciones")
                    FeatureRow(
                        Triple("P", "Pagos", onPayments),
                        Triple("C", "Creditos", onCredits),
                        Triple("QR", "Cobrar", onReceive)
                    )
                    FeatureRow(
                        Triple("T", "Transferir", onTransfer),
                        Triple("S", "Servicios", onServices),
                        Triple("$", "Calculadora", onCalculator)
                    )
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    NexoSection("MI CUENTA", "Herramientas")
                    FeatureRow(
                        Triple("TC", "Tarjetas", onCards),
                        Triple("AM", "Perfil", onProfile),
                        Triple("AJ", "Config.", onSettings)
                    )
                }
            }
        }
    }
}

@Composable
private fun FeatureRow(vararg features: Triple<String, String, () -> Unit>) {
    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        features.forEach { (symbol, label, action) ->
            Card(
                onClick = action,
                modifier = Modifier.weight(1f),
                colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = .92f)),
                border = BorderStroke(1.dp, com.nexo.banca.ui.theme.AccentSoft),
                elevation = CardDefaults.cardElevation(3.dp),
                shape = RoundedCornerShape(22.dp)
            ) {
                Column(
                    Modifier.fillMaxWidth().padding(horizontal = 6.dp, vertical = 18.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(symbol, color = Accent, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
                    Text(label, color = Ink, fontSize = 12.sp, fontWeight = FontWeight.Medium, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
    }
}
