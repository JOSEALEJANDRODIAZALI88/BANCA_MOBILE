package com.nexo.banca.ui.payments

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexo.banca.domain.PaymentType
import com.nexo.banca.ui.components.NexoCard
import com.nexo.banca.ui.components.NexoHeader
import com.nexo.banca.ui.theme.Accent
import com.nexo.banca.ui.theme.AccentSoft
import com.nexo.banca.ui.theme.Danger
import com.nexo.banca.ui.theme.Ink
import com.nexo.banca.ui.theme.Muted
import com.nexo.banca.ui.theme.Success
import java.text.NumberFormat
import java.util.Locale

private enum class PaymentTab(val label: String, val type: PaymentType? = null) {
    SERVICES("Servicios", PaymentType.SERVICE),
    CREDITS("Creditos", PaymentType.CREDIT),
    CALCULATOR("Calculadora")
}

enum class PaymentDestination {
    OVERVIEW,
    SERVICES,
    CREDITS,
    CALCULATOR
}

@Composable
internal fun PaymentsScreen(
    payments: List<PaymentUiModel>,
    destination: PaymentDestination,
    onPay: (String) -> Unit
) {
    val initialTab = when (destination) {
        PaymentDestination.OVERVIEW, PaymentDestination.SERVICES -> PaymentTab.SERVICES
        PaymentDestination.CREDITS -> PaymentTab.CREDITS
        PaymentDestination.CALCULATOR -> PaymentTab.CALCULATOR
    }
    var selectedTabName by rememberSaveable(destination.name) { mutableStateOf(initialTab.name) }
    var pending by remember { mutableStateOf<PaymentUiModel?>(null) }
    val selectedTab = PaymentTab.valueOf(selectedTabName)
    val visiblePayments = payments.filter { it.type == selectedTab.type }
    val title = when (destination) {
        PaymentDestination.OVERVIEW -> "Pagos"
        PaymentDestination.SERVICES -> "Servicios"
        PaymentDestination.CREDITS -> "Creditos"
        PaymentDestination.CALCULATOR -> "Calculadora"
    }

    Column(Modifier.fillMaxSize()) {
        NexoHeader(title, if (destination == PaymentDestination.OVERVIEW) "SERVICIOS, CREDITOS Y PRESTAMOS" else "ACCESO DIRECTO")
        if (destination == PaymentDestination.OVERVIEW) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp, vertical = 10.dp).background(AccentSoft.copy(alpha = .85f), RoundedCornerShape(20.dp)).padding(5.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                PaymentTab.entries.forEach { tab ->
                    TextButton(
                        onClick = { selectedTabName = tab.name },
                        modifier = Modifier.weight(1f).background(
                            if (selectedTab == tab) Accent else Color.Transparent,
                            RoundedCornerShape(16.dp)
                        )
                    ) {
                        Text(tab.label, color = if (selectedTab == tab) com.nexo.banca.ui.theme.Surface else Ink, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
        Box(Modifier.weight(1f)) {
            if (selectedTab == PaymentTab.CALCULATOR) {
                LoanCalculatorContent(Modifier.fillMaxSize())
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    items(visiblePayments, key = { it.id }) { payment ->
                        PaymentRow(payment = payment, onPay = { pending = payment })
                    }
                    if (visiblePayments.isEmpty()) {
                        item { Text("No hay pagos en esta categoria.", color = Muted, modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp), textAlign = TextAlign.Center) }
                    }
                }
            }
        }
    }

    pending?.let { payment ->
        AlertDialog(
            onDismissRequest = { pending = null },
            title = { Text("Confirmar pago") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Esta operacion solo afecta los datos de la demo.", color = Danger)
                    PaymentAmountLine(if (payment.type == PaymentType.CREDIT) "Credito" else "Servicio", payment.name)
                    PaymentAmountLine("Valor", paymentCurrency(payment.amount))
                }
            },
            confirmButton = { Button(onClick = { onPay(payment.id); pending = null }) { Text("Confirmar") } },
            dismissButton = { TextButton(onClick = { pending = null }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun PaymentRow(payment: PaymentUiModel, onPay: () -> Unit) {
    NexoCard(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(payment.name, fontWeight = FontWeight.SemiBold, color = Ink)
                Text(
                    if (payment.type == PaymentType.CREDIT) payment.status else "${payment.status} . ${paymentCurrency(payment.amount)}",
                    color = Muted,
                    fontSize = 13.sp
                )
                if (payment.type == PaymentType.CREDIT) Text(paymentCurrency(payment.amount), color = Ink, fontSize = 14.sp)
            }
            Spacer(Modifier.width(12.dp))
            if (payment.paid) Text("Pagado", color = Success, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            else TextButton(onClick = onPay) { Text("Pagar") }
        }
    }
}

@Composable
internal fun PaymentAmountLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth()) {
        Text(label, color = Muted, fontSize = 13.sp)
        Spacer(Modifier.weight(1f))
        Text(value, color = Ink, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
    }
}

internal fun paymentCurrency(value: Long): String = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"))
    .format(kotlin.math.abs(value)).replace(",00", "")
