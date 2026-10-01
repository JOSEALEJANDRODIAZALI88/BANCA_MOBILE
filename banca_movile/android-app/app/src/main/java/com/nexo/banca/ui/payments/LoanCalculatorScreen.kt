package com.nexo.banca.ui.payments

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexo.banca.domain.LoanCalculator
import com.nexo.banca.domain.LoanEstimate
import com.nexo.banca.ui.theme.Accent
import com.nexo.banca.ui.theme.AccentSoft
import com.nexo.banca.ui.theme.Ink
import com.nexo.banca.ui.theme.Muted

@Composable
internal fun LoanCalculatorContent(modifier: Modifier = Modifier) {
    var principal by rememberSaveable { mutableStateOf("") }
    var annualRate by rememberSaveable { mutableStateOf("") }
    var months by rememberSaveable { mutableStateOf("") }
    var attempted by rememberSaveable { mutableStateOf(false) }
    var estimate by remember { mutableStateOf<LoanEstimate?>(null) }
    val principalValue = principal.toLongOrNull() ?: 0
    val rateValue = annualRate.replace(',', '.').toDoubleOrNull() ?: Double.NaN
    val monthsValue = months.toIntOrNull() ?: 0

    LazyColumn(
        modifier = modifier.imePadding(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item { Text("CALCULADORA EDUCATIVA", color = Accent, fontSize = 11.sp, letterSpacing = 1.1.sp, fontWeight = FontWeight.Bold) }
        item { Text("Cuota fija aproximada, sin seguros ni comisiones.", color = Muted) }
        item {
            OutlinedTextField(
                value = principal,
                onValueChange = { principal = it.filter(Char::isDigit).take(10); estimate = null },
                label = { Text("Valor del prestamo") },
                prefix = { Text("$ ") },
                isError = attempted && principalValue !in 1..LoanCalculator.MAX_PRINCIPAL,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = annualRate,
                onValueChange = { annualRate = it.filter { char -> char.isDigit() || char == ',' || char == '.' }.take(6); estimate = null },
                label = { Text("Tasa efectiva anual") },
                suffix = { Text("%") },
                isError = attempted && (!rateValue.isFinite() || rateValue !in 0.0..LoanCalculator.MAX_ANNUAL_EFFECTIVE_RATE),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            OutlinedTextField(
                value = months,
                onValueChange = { months = it.filter(Char::isDigit).take(3); estimate = null },
                label = { Text("Plazo en meses") },
                isError = attempted && monthsValue !in 1..LoanCalculator.MAX_MONTHS,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )
        }
        item {
            Button(
                onClick = {
                    attempted = true
                    estimate = LoanCalculator.estimate(principalValue, rateValue, monthsValue)
                },
                modifier = Modifier.fillMaxWidth()
            ) { Text("Calcular") }
        }
        estimate?.let { result ->
            item {
                Card(colors = CardDefaults.cardColors(containerColor = AccentSoft), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(18.dp)) {
                        Text("Cuota mensual", color = Muted, fontSize = 13.sp)
                        Text(paymentCurrency(result.monthlyPayment), color = Ink, fontSize = 28.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(12.dp))
                        PaymentAmountLine("Intereses", paymentCurrency(result.totalInterest))
                        PaymentAmountLine("Total", paymentCurrency(result.totalPayment))
                    }
                }
            }
        }
    }
}
