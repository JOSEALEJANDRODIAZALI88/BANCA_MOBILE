package com.nexo.banca.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexo.banca.data.local.MovementEntity
import com.nexo.banca.ui.NexoUiState
import com.nexo.banca.ui.components.NexoCard
import com.nexo.banca.ui.components.NexoHeader
import com.nexo.banca.ui.components.NexoSection
import com.nexo.banca.ui.components.formatCurrency
import com.nexo.banca.ui.theme.Accent
import com.nexo.banca.ui.theme.AccentSoft
import com.nexo.banca.ui.theme.Ink
import com.nexo.banca.ui.theme.Muted
import com.nexo.banca.ui.theme.Outline
import com.nexo.banca.ui.theme.Paper
import com.nexo.banca.ui.theme.Success
import com.nexo.banca.ui.theme.Surface
import java.text.DateFormat
import java.util.Date

@Composable
fun HomeScreen(
    state: NexoUiState,
    onTransfer: () -> Unit,
    onReceive: () -> Unit,
    onPayments: () -> Unit,
    onServices: () -> Unit,
    onActivity: () -> Unit,
    onMore: () -> Unit,
    onSettings: () -> Unit,
    onAddSavings: () -> Unit,
    showBalanceInitially: Boolean = true
) {
    var balanceVisible by rememberSaveable(showBalanceInitially) { mutableStateOf(showBalanceInitially) }
    val account = state.account
    val firstName = account?.holder?.substringBefore(' ') ?: "Alex"
    LazyColumn(
        Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Paper, AccentSoft.copy(alpha = .5f), Paper))),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            Row(
                Modifier.fillMaxWidth().statusBarsPadding().padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Menu", color = Accent, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                Text("Hola $firstName", modifier = Modifier.weight(1f), color = Ink, fontSize = 23.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
                TextButton(
                    onClick = onSettings,
                    modifier = Modifier.semantics { contentDescription = "Abrir configuracion" }
                ) {
                    Text("\u2699", color = Accent, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        item {
            Column(Modifier.padding(horizontal = 16.dp), verticalArrangement = Arrangement.spacedBy(22.dp)) {
                NexoCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Saldo disponible", color = Muted, fontSize = 13.sp)
                            Spacer(Modifier.weight(1f))
                            TextButton(onClick = { balanceVisible = !balanceVisible }) { Text(if (balanceVisible) "Ocultar" else "Mostrar") }
                        }
                        Text(
                            if (balanceVisible) formatCurrency(account?.balance ?: 0) else "$ ---------",
                            color = Ink,
                            fontSize = 31.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text("Cuenta terminada en ${account?.number?.takeLast(4) ?: "----"}", color = Muted, fontSize = 13.sp)
                    }
                }

                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        ProminentAction("Cobrar", Modifier.weight(1f), onReceive)
                        ProminentAction("Pagar", Modifier.weight(1f), onPayments)
                        ProminentAction("Transferir", Modifier.weight(1f), onTransfer)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        MinimalAction("QR", Modifier.weight(1f), onReceive)
                        MinimalAction("Servicios", Modifier.weight(1f), onServices)
                        MinimalAction("Enviar", Modifier.weight(1f), onTransfer)
                        MinimalAction("Mas", Modifier.weight(1f), onMore)
                    }
                }

                Column {
                    NexoSection("ACTIVIDAD", "Recientes", "Ver todos", onActivity)
                    Spacer(Modifier.height(8.dp))
                    NexoCard {
                        Column(Modifier.padding(horizontal = 16.dp)) {
                            state.movements.take(4).forEach { MovementRow(it) }
                        }
                    }
                }
                SavingsCard(account?.savings ?: 0, onAddSavings)
            }
        }
    }
}

@Composable
fun ActivityScreen(movements: List<MovementEntity>) {
    var search by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableStateOf("Todos") }
    val visible = movements.filter { movement ->
        (search.isBlank() || movement.name.contains(search, true) || movement.category.contains(search, true)) &&
            (filter == "Todos" || (filter == "Ingresos" && movement.amount > 0) || (filter == "Gastos" && movement.amount < 0))
    }
    val income = movements.filter { it.amount > 0 }.sumOf { it.amount }
    val expenses = movements.filter { it.amount < 0 }.sumOf { -it.amount }
    Column(Modifier.fillMaxSize()) {
        NexoHeader("Movimientos", "HISTORIAL LOCAL")
        Row(
            Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            NexoCard(Modifier.weight(1f)) {
                Column(Modifier.padding(16.dp)) {
                    Text("+", color = Success, fontSize = 22.sp)
                    Text("Ingresos", color = Muted, fontSize = 12.sp)
                    Text(formatCurrency(income), color = Ink, fontWeight = FontWeight.SemiBold)
                }
            }
            NexoCard(Modifier.weight(1f)) {
                Column(Modifier.padding(16.dp)) {
                    Text("-", color = com.nexo.banca.ui.theme.Danger, fontSize = 22.sp)
                    Text("Gastos", color = Muted, fontSize = 12.sp)
                    Text(formatCurrency(expenses), color = Ink, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        OutlinedTextField(
            value = search,
            onValueChange = { search = it },
            placeholder = { Text("Buscar") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)
        )
        Row(Modifier.padding(16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Todos", "Ingresos", "Gastos").forEach { option ->
                FilterChip(selected = filter == option, onClick = { filter = option }, label = { Text(option) })
            }
        }
        LazyColumn(Modifier.weight(1f).padding(horizontal = 16.dp)) {
            items(visible, key = { it.id }) { MovementRow(it) }
            if (visible.isEmpty()) item { Text("No hay resultados.", modifier = Modifier.fillMaxWidth().padding(32.dp), textAlign = TextAlign.Center, color = Muted) }
        }
    }
}

@Composable
private fun MinimalAction(title: String, modifier: Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Outline),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(18.dp)
    ) { Text(title, modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp), textAlign = TextAlign.Center, color = Ink, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
}

@Composable
private fun ProminentAction(title: String, modifier: Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(
            Modifier.fillMaxWidth().background(Brush.linearGradient(listOf(Ink, Accent))).padding(vertical = 24.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title.take(1), color = Surface, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            Text(title, color = Surface, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun SavingsCard(savings: Long, onAdd: () -> Unit) {
    val progress = (savings / 5_000_000f).coerceIn(0f, 1f)
    NexoCard {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            NexoSection("META", "Viaje a la costa")
            Text(formatCurrency(savings), color = Ink, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            LinearProgressIndicator(progress = { progress }, modifier = Modifier.fillMaxWidth(), color = Accent, trackColor = Outline)
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("${(progress * 100).toInt()}% de ${formatCurrency(5_000_000)}", color = Muted, fontSize = 13.sp)
                Spacer(Modifier.weight(1f))
                TextButton(onClick = onAdd) { Text("Aportar $ 100.000") }
            }
        }
    }
}

@Composable
private fun MovementRow(movement: MovementEntity) {
    Row(Modifier.fillMaxWidth().padding(vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f)) {
            Text(movement.name, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
            Text("${movement.category} . ${DateFormat.getDateInstance(DateFormat.SHORT).format(Date(movement.timestamp))}", color = Muted, fontSize = 12.sp)
        }
        Text(
            (if (movement.amount > 0) "+" else "-") + formatCurrency(movement.amount),
            color = if (movement.amount > 0) Success else Ink,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
        )
    }
    HorizontalDivider(color = Outline)
}
