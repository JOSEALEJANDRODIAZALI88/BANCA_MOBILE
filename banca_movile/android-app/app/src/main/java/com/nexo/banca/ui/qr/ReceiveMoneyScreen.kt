package com.nexo.banca.ui.qr

import android.content.Intent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.nexo.banca.data.local.AccountEntity
import com.nexo.banca.domain.QrTransferCodec
import com.nexo.banca.domain.DemoData
import com.nexo.banca.ui.components.NexoCard
import com.nexo.banca.ui.components.NexoHeader
import com.nexo.banca.ui.components.NexoPrimaryButton
import com.nexo.banca.ui.components.NexoReceiptLine
import com.nexo.banca.ui.theme.Muted

private val QrInk = Color(0xFF10271F)

@Composable
fun ReceiveMoneyScreen(account: AccountEntity?, onBack: () -> Unit) {
    val context = LocalContext.current
    val holder = account?.holder ?: DemoData.ACCOUNT_HOLDER
    val accountNumber = account?.number ?: DemoData.ACCOUNT_NUMBER
    val payload = remember(holder, accountNumber) { QrTransferCodec.encode(holder, accountNumber) }
    val qr = remember(payload) { QRCodeWriter().encode(payload, BarcodeFormat.QR_CODE, 33, 33) }
    Column(Modifier.fillMaxSize()) {
        NexoHeader("Recibir", "QR DE DEMOSTRACION", "Volver", onBack)
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            item {
                Text("Comparte este codigo", fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                Text("Completa nombre y cuenta al escanearlo desde Nexo.", color = Muted, textAlign = TextAlign.Center, modifier = Modifier.padding(top = 6.dp, bottom = 18.dp))
            }
            item {
                NexoCard {
                    Canvas(Modifier.padding(18.dp).size(220.dp).semantics { contentDescription = "QR de demostracion para la cuenta terminada en ${accountNumber.takeLast(4)}" }) {
                        drawRect(Color.White)
                        val module = size.width / qr.width
                        for (x in 0 until qr.width) for (y in 0 until qr.height) if (qr[x, y]) drawRect(QrInk, Offset(x * module, y * module), Size(module + .5f, module + .5f))
                    }
                }
            }
            item {
                Spacer(Modifier.height(16.dp))
                NexoCard(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        NexoReceiptLine("Titular", holder)
                        NexoReceiptLine("Cuenta", accountNumber)
                    }
                }
            }
            item {
                Spacer(Modifier.height(16.dp))
                NexoPrimaryButton("Compartir datos") {
                    val text = "Nexo demo\nTitular: $holder\nCuenta: $accountNumber\nQR: $payload\nNo mueve dinero real."
                    context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply { type = "text/plain"; putExtra(Intent.EXTRA_TEXT, text) }, "Compartir datos de demostracion"))
                }
            }
        }
    }
}
