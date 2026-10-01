package com.nexo.banca.ui.transfer

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.nexo.banca.data.OperationResult
import com.nexo.banca.domain.BankingRules
import com.nexo.banca.domain.QrTransfer
import com.nexo.banca.ui.components.NexoCard
import com.nexo.banca.ui.components.NexoHeader
import com.nexo.banca.ui.components.NexoPrimaryButton
import com.nexo.banca.ui.components.NexoReceiptLine
import com.nexo.banca.ui.components.formatCurrency
import com.nexo.banca.ui.theme.Muted

@Composable
fun TransferScreen(
    balance: Long,
    scanned: QrTransfer?,
    onScan: () -> Unit,
    onTransfer: (String, String, Long, String, (OperationResult) -> Unit) -> Unit
) {
    var recipient by rememberSaveable { mutableStateOf("Sofia Ramirez") }
    var account by rememberSaveable { mutableStateOf("48290213") }
    var amount by rememberSaveable { mutableStateOf("") }
    var concept by rememberSaveable { mutableStateOf("") }
    var attempted by rememberSaveable { mutableStateOf(false) }
    var confirm by remember { mutableStateOf(false) }
    var isProcessing by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<OperationResult.Success?>(null) }
    var operationError by remember { mutableStateOf<String?>(null) }
    val amountValue = amount.toLongOrNull() ?: 0

    LaunchedEffect(scanned) {
        scanned?.let {
            recipient = it.name
            account = it.account
            if (it.amount != null) amount = it.amount.toString()
        }
    }

    Column(Modifier.fillMaxSize()) {
        NexoHeader("Transferir", "SIMULACION LOCAL")
        LazyColumn(Modifier.weight(1f).imePadding(), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { OutlinedButton(onClick = onScan, modifier = Modifier.fillMaxWidth()) { Text("Leer QR") } }
            item { OutlinedTextField(recipient, { recipient = it.take(60) }, label = { Text("Destinatario") }, isError = attempted && recipient.isBlank(), singleLine = true, modifier = Modifier.fillMaxWidth()) }
            item { OutlinedTextField(account, { account = it.filter(Char::isDigit).take(20) }, label = { Text("Cuenta") }, supportingText = { if (attempted && !BankingRules.validAccount(account)) Text("Usa entre 6 y 20 digitos") }, isError = attempted && !BankingRules.validAccount(account), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.fillMaxWidth()) }
            item { OutlinedTextField(amount, { amount = it.filter(Char::isDigit).take(9) }, label = { Text("Valor") }, prefix = { Text("$ ") }, supportingText = { Text("Disponible: ${formatCurrency(balance)}") }, isError = attempted && !BankingRules.validAmount(amountValue, balance), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.fillMaxWidth()) }
            item { OutlinedTextField(concept, { concept = it.take(45) }, label = { Text("Concepto opcional") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
            operationError?.let { error -> item { Text(error, color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.SemiBold) } }
            item { NexoCard { Text("No mueve dinero real. Los cambios se guardan solo en este dispositivo.", modifier = Modifier.padding(16.dp), color = Muted) } }
            item {
                NexoPrimaryButton("Revisar") {
                    attempted = true
                    operationError = null
                    if (recipient.isNotBlank() && BankingRules.validAccount(account) && BankingRules.validAmount(amountValue, balance)) confirm = true
                }
            }
        }
    }
    if (confirm) {
        AlertDialog(
            onDismissRequest = { if (result == null) confirm = false },
            title = { Text(if (result == null) "Revisa los datos" else "Simulacion completada") },
            text = {
                Column {
                    Text(if (result == null) "No se movera dinero real." else "Movimiento guardado localmente.", color = Muted)
                    Spacer(Modifier.height(12.dp))
                    NexoReceiptLine("Destinatario", recipient)
                    NexoReceiptLine("Cuenta", "---- ${account.takeLast(4)}")
                    NexoReceiptLine("Total", formatCurrency(amountValue))
                    result?.let { NexoReceiptLine("Referencia", it.reference) }
                }
            },
            confirmButton = {
                Button(enabled = !isProcessing, onClick = {
                    if (result != null) {
                        confirm = false; result = null; amount = ""; concept = ""; attempted = false
                    } else {
                        isProcessing = true
                        onTransfer(recipient, account, amountValue, concept) { operation ->
                            isProcessing = false
                            when (operation) {
                                is OperationResult.Success -> result = operation
                                is OperationResult.Error -> { confirm = false; attempted = true; operationError = operation.message }
                            }
                        }
                    }
                }) { Text(if (result == null) "Confirmar" else "Finalizar") }
            },
            dismissButton = { if (result == null) TextButton(onClick = { confirm = false }) { Text("Corregir") } }
        )
    }
}
