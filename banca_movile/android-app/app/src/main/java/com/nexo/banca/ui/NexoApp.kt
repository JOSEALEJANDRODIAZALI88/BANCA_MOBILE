package com.nexo.banca.ui

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import com.google.zxing.BarcodeFormat
import com.google.zxing.qrcode.QRCodeWriter
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.nexo.banca.data.OperationResult
import com.nexo.banca.data.local.BillEntity
import com.nexo.banca.data.local.CardSettingsEntity
import com.nexo.banca.data.local.MovementEntity
import com.nexo.banca.domain.BankingRules
import com.nexo.banca.domain.QrTransfer
import com.nexo.banca.domain.QrTransferCodec
import com.nexo.banca.ui.theme.Coral
import com.nexo.banca.ui.theme.Ink
import com.nexo.banca.ui.theme.InkLight
import com.nexo.banca.ui.theme.Lime
import com.nexo.banca.ui.theme.Muted
import com.nexo.banca.ui.theme.NexoBlue
import com.nexo.banca.ui.theme.Outline
import com.nexo.banca.ui.theme.Paper
import com.nexo.banca.ui.theme.Purple
import com.nexo.banca.ui.theme.Success
import java.text.DateFormat
import java.text.NumberFormat
import java.util.Date
import java.util.Locale

private enum class Screen(val label: String, val symbol: String) {
    HOME("Inicio", "I"), ACTIVITY("Actividad", "A"), TRANSFER("Transferir", "T"), PAYMENTS("Pagos", "P"), PROFILE("Perfil", "AM"), CARDS("Tarjetas", "C"), RECEIVE("Recibir", "QR")
}

private val tabs = listOf(Screen.HOME, Screen.ACTIVITY, Screen.TRANSFER, Screen.PAYMENTS, Screen.PROFILE)

private fun currency(value: Long): String = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"))
    .format(kotlin.math.abs(value)).replace(",00", "")

@Composable
fun NexoRoot(state: NexoUiState, viewModel: NexoViewModel) {
    var loggedIn by rememberSaveable { mutableStateOf(false) }
    if (!loggedIn) {
        LoginScreen(onLogin = { loggedIn = true })
        return
    }
    NexoApp(state = state, viewModel = viewModel, onLogout = { loggedIn = false })
}

@Composable
private fun LoginScreen(onLogin: () -> Unit) {
    val context = LocalContext.current
    var user by rememberSaveable { mutableStateOf(BankingRules.DEMO_USER) }
    var password by rememberSaveable { mutableStateOf(BankingRules.DEMO_PASSWORD) }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var attempted by rememberSaveable { mutableStateOf(false) }
    val submit = {
        attempted = true
        if (BankingRules.validCredentials(user, password)) onLogin()
        else Toast.makeText(context, "Usa las credenciales de demostracion indicadas", Toast.LENGTH_LONG).show()
    }

    LazyColumn(modifier = Modifier.fillMaxSize().background(Color.White), contentPadding = PaddingValues(bottom = 32.dp)) {
        item {
            Box(Modifier.fillMaxWidth().heightIn(min = 245.dp).background(Brush.linearGradient(listOf(InkLight, Ink))).statusBarsPadding().padding(24.dp)) {
                Column {
                    Brand(light = true)
                    Spacer(Modifier.height(36.dp))
                    Eyebrow("BANCA SIN RUIDO", Lime)
                    Text("Tus finanzas,\ncon mas claridad.", color = Color.White, fontSize = 30.sp, lineHeight = 35.sp, fontWeight = FontWeight.ExtraBold)
                }
            }
        }
        item {
            Column(Modifier.padding(horizontal = 24.dp, vertical = 26.dp)) {
                Text("Acceso a la demostracion", style = androidx.compose.material3.MaterialTheme.typography.headlineMedium, color = Ink)
                Text("No introduzcas datos personales o bancarios reales.", color = Coral, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(20.dp))
                OutlinedTextField(
                    value = user,
                    onValueChange = { user = it },
                    label = { Text("Correo de demostracion") },
                    supportingText = if (attempted && !user.equals(BankingRules.DEMO_USER, true)) ({
                        Text("Usa ${BankingRules.DEMO_USER}")
                    }) else null,
                    isError = attempted && !user.equals(BankingRules.DEMO_USER, true),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Clave de demostracion") },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = { TextButton(onClick = { showPassword = !showPassword }) { Text(if (showPassword) "Ocultar" else "Ver") } },
                    supportingText = if (attempted && password != BankingRules.DEMO_PASSWORD) ({
                        Text("Usa la clave mostrada en la guia")
                    }) else null,
                    isError = attempted && password != BankingRules.DEMO_PASSWORD,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { submit() }),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(14.dp))
                PrimaryButton("Entrar a la demo", submit)
                Text("Usuario: ${BankingRules.DEMO_USER}\nClave: ${BankingRules.DEMO_PASSWORD}", modifier = Modifier.fillMaxWidth().padding(top = 16.dp).background(Lime.copy(alpha = .35f), RoundedCornerShape(12.dp)).padding(14.dp), color = Ink, fontSize = 14.sp)
            }
        }
    }
}

@Composable
private fun NexoApp(state: NexoUiState, viewModel: NexoViewModel, onLogout: () -> Unit) {
    val context = LocalContext.current
    var screenName by rememberSaveable { mutableStateOf(Screen.HOME.name) }
    var scannedTransfer by remember { mutableStateOf<QrTransfer?>(null) }
    val screen = Screen.entries.firstOrNull { it.name == screenName } ?: Screen.HOME
    val notify: (String) -> Unit = { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    val scanner = rememberLauncherForActivityResult(ScanContract()) { result ->
        val transfer = result.contents?.let(QrTransferCodec::decode)
        if (transfer == null) notify(if (result.contents == null) "Lectura cancelada" else "El QR no pertenece a Nexo o contiene datos invalidos")
        else {
            scannedTransfer = transfer
            screenName = Screen.TRANSFER.name
            notify("Datos del QR cargados. Revisa antes de confirmar")
        }
    }
    val scanQr = {
        scanner.launch(ScanOptions().setDesiredBarcodeFormats(ScanOptions.QR_CODE).setPrompt("Enfoca un QR de Nexo").setBeepEnabled(false).setOrientationLocked(false))
    }

    BackHandler(screen !in tabs || screen != Screen.HOME) {
        screenName = when (screen) {
            Screen.CARDS -> Screen.PROFILE.name
            else -> Screen.HOME.name
        }
    }

    Scaffold(
        containerColor = Paper,
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 0.dp) {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = screen == tab,
                        onClick = { screenName = tab.name },
                        icon = { NavSymbol(tab.symbol, screen == tab, tab == Screen.TRANSFER) },
                        label = { Text(tab.label, fontSize = 12.sp) }
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (screen) {
                Screen.HOME -> HomeScreen(state, onNavigate = { screenName = it.name }, onAddSavings = { viewModel.addSavings { notify(it.message()) } })
                Screen.ACTIVITY -> ActivityScreen(state.movements)
                Screen.TRANSFER -> TransferScreen(state, scannedTransfer, scanQr) { recipient, account, amount, concept, done -> viewModel.transfer(recipient, account, amount, concept, done) }
                Screen.PAYMENTS -> PaymentsScreen(state.bills) { id -> viewModel.payBill(id) { notify(it.message()) } }
                Screen.PROFILE -> ProfileScreen(onCards = { screenName = Screen.CARDS.name }, onLogout = onLogout)
                Screen.CARDS -> CardsScreen(
                    state.card,
                    onBack = { screenName = Screen.PROFILE.name },
                    onLockedChange = viewModel::updateCardLocked,
                    onInternationalChange = viewModel::updateCardInternational,
                    onContactlessChange = viewModel::updateCardContactless
                )
                Screen.RECEIVE -> ReceiveMoneyScreen(state, onBack = { screenName = Screen.HOME.name })
            }
        }
    }
}

private fun OperationResult.message(): String = when (this) {
    is OperationResult.Success -> "Simulacion guardada en SQLite. Referencia: $reference"
    is OperationResult.Error -> message
}

@Composable
private fun HomeScreen(state: NexoUiState, onNavigate: (Screen) -> Unit, onAddSavings: () -> Unit) {
    var balanceVisible by rememberSaveable { mutableStateOf(true) }
    val account = state.account
    LazyColumn(Modifier.fillMaxSize(), contentPadding = PaddingValues(bottom = 24.dp)) {
        item { ScreenHeader("Buenos dias, Alex", "DEMO LOCAL - DATOS FICTICIOS") }
        item {
            Column(Modifier.padding(16.dp)) {
                Card(colors = CardDefaults.cardColors(containerColor = Ink), shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.fillMaxWidth().heightIn(min = 205.dp).background(Brush.linearGradient(listOf(InkLight, Ink))).padding(22.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Saldo disponible", color = Color(0xFFD7ECE8), fontSize = 14.sp)
                            Spacer(Modifier.weight(1f))
                            TextButton(onClick = { balanceVisible = !balanceVisible }) { Text(if (balanceVisible) "Ocultar" else "Mostrar", color = Lime) }
                        }
                        Text(if (balanceVisible) currency(account?.balance ?: 0) else "$ ---------", color = Color.White, fontSize = 31.sp, fontWeight = FontWeight.ExtraBold)
                        Text("Cuenta Nexo ---- ${account?.number?.takeLast(4) ?: "----"}", color = Color(0xFFD7ECE8), fontSize = 14.sp)
                        Spacer(Modifier.height(28.dp))
                        Text("Los cambios de la demo se guardan en SQLite en este dispositivo.", color = Lime, fontSize = 13.sp)
                    }
                }
                Spacer(Modifier.height(24.dp))
                SectionTitle("ACCIONES", "Que quieres hacer?")
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    QuickAction("T", "Transferir", Coral, Modifier.weight(1f)) { onNavigate(Screen.TRANSFER) }
                    QuickAction("QR", "Recibir", Lime, Modifier.weight(1f)) { onNavigate(Screen.RECEIVE) }
                    QuickAction("P", "Pagar", NexoBlue, Modifier.weight(1f)) { onNavigate(Screen.PAYMENTS) }
                }
                Spacer(Modifier.height(24.dp))
                SectionTitle("ACTIVIDAD", "Movimientos recientes", "Ver todos") { onNavigate(Screen.ACTIVITY) }
                Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
                    Column(Modifier.padding(horizontal = 16.dp)) { state.movements.take(4).forEach { MovementRow(it) } }
                }
                Spacer(Modifier.height(20.dp))
                SavingsCard(account?.savings ?: 0, onAddSavings)
            }
        }
    }
}

@Composable
private fun ActivityScreen(movements: List<MovementEntity>) {
    var search by rememberSaveable { mutableStateOf("") }
    var filter by rememberSaveable { mutableStateOf("Todos") }
    val visible = movements.filter { movement ->
        (search.isBlank() || movement.name.contains(search, true) || movement.category.contains(search, true)) &&
            (filter == "Todos" || (filter == "Ingresos" && movement.amount > 0) || (filter == "Gastos" && movement.amount < 0))
    }
    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Movimientos", "HISTORIAL GUARDADO EN SQLITE")
        OutlinedTextField(search, { search = it }, placeholder = { Text("Buscar nombre o categoria") }, singleLine = true, modifier = Modifier.fillMaxWidth().padding(16.dp))
        Row(Modifier.padding(horizontal = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf("Todos", "Ingresos", "Gastos").forEach { option -> FilterChip(selected = filter == option, onClick = { filter = option }, label = { Text(option) }) }
        }
        LazyColumn(Modifier.weight(1f).padding(16.dp)) {
            items(visible, key = { it.id }) { MovementRow(it) }
            if (visible.isEmpty()) item { Text("No hay resultados.", modifier = Modifier.fillMaxWidth().padding(32.dp), textAlign = TextAlign.Center, color = Muted) }
        }
    }
}

@Composable
private fun TransferScreen(
    state: NexoUiState,
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
    val balance = state.account?.balance ?: 0

    LaunchedEffect(scanned) {
        scanned?.let {
            recipient = it.name
            account = it.account
            if (it.amount != null) amount = it.amount.toString()
        }
    }

    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Nueva transferencia", "SIMULACION - NO MUEVE DINERO")
        LazyColumn(Modifier.weight(1f).imePadding(), contentPadding = PaddingValues(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            item { OutlinedButton(onClick = onScan, modifier = Modifier.fillMaxWidth()) { Text("Leer QR de transferencia") } }
            item { Text("Tambien puedes completar los datos manualmente.", color = Muted, fontSize = 14.sp) }
            item { OutlinedTextField(recipient, { recipient = it.take(60) }, label = { Text("Nombre del destinatario") }, isError = attempted && recipient.isBlank(), singleLine = true, modifier = Modifier.fillMaxWidth()) }
            item { OutlinedTextField(account, { account = it.filter(Char::isDigit).take(20) }, label = { Text("Numero de cuenta") }, supportingText = { if (attempted && !BankingRules.validAccount(account)) Text("Ingresa entre 6 y 20 digitos") }, isError = attempted && !BankingRules.validAccount(account), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.fillMaxWidth()) }
            item { OutlinedTextField(amount, { amount = it.filter(Char::isDigit).take(9) }, label = { Text("Valor en COP") }, prefix = { Text("$ ") }, supportingText = { Text("Maximo: ${currency(BankingRules.MAX_TRANSFER)}. Saldo: ${currency(balance)}") }, isError = attempted && !BankingRules.validAmount(amountValue, balance), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number), singleLine = true, modifier = Modifier.fillMaxWidth()) }
            item { OutlinedTextField(concept, { concept = it.take(45) }, label = { Text("Concepto opcional") }, singleLine = true, modifier = Modifier.fillMaxWidth()) }
            operationError?.let { error -> item { Text(error, color = androidx.compose.material3.MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold) } }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Lime.copy(alpha = .35f))) {
                    Text("Esta operacion es una simulacion local. Al confirmar se actualizan el saldo y los movimientos guardados en SQLite.", modifier = Modifier.padding(16.dp), color = Ink, fontSize = 14.sp)
                }
            }
            item {
                PrimaryButton("Revisar simulacion") {
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
            text = { Column { Text(if (result == null) "No se movera dinero real." else "Se guardo el movimiento localmente.", color = Muted); Spacer(Modifier.height(12.dp)); ReceiptLine("Destinatario", recipient); ReceiptLine("Cuenta", "---- ${account.takeLast(4)}"); ReceiptLine("Total", currency(amountValue)); result?.let { ReceiptLine("Referencia", it.reference) } } },
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
                }) { Text(if (result == null) "Confirmar simulacion" else "Finalizar") }
            },
            dismissButton = { if (result == null) TextButton(onClick = { confirm = false }) { Text("Corregir") } }
        )
    }
}

@Composable
private fun PaymentsScreen(bills: List<BillEntity>, onPay: (String) -> Unit) {
    var pending by remember { mutableStateOf<BillEntity?>(null) }
    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Pagos y servicios", "SIMULACIONES GUARDADAS")
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { SectionTitle("FACTURAS", "Proximos pagos") }
            items(bills, key = { it.id }) { bill -> BillCard(bill) { pending = bill } }
        }
    }
    pending?.let { bill ->
        AlertDialog(
            onDismissRequest = { pending = null },
            title = { Text("Confirmar pago simulado") },
            text = { Column { Text("No se pagara una factura real.", color = Coral); ReceiptLine("Servicio", bill.name); ReceiptLine("Valor", currency(bill.amount)) } },
            confirmButton = { Button(onClick = { onPay(bill.id); pending = null }) { Text("Guardar simulacion") } },
            dismissButton = { TextButton(onClick = { pending = null }) { Text("Cancelar") } }
        )
    }
}

@Composable
private fun ReceiveMoneyScreen(state: NexoUiState, onBack: () -> Unit) {
    val context = LocalContext.current
    val account = state.account
    val holder = account?.holder ?: "Alex Morgan"
    val accountNumber = account?.number ?: "104800004821"
    val payload = remember(holder, accountNumber) { QrTransferCodec.encode(holder, accountNumber) }
    val qr = remember(payload) { QRCodeWriter().encode(payload, BarcodeFormat.QR_CODE, 33, 33) }
    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Recibir por QR", "DATOS FICTICIOS", "Volver", onBack)
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            item { Text("Comparte este codigo", fontSize = 24.sp, fontWeight = FontWeight.ExtraBold); Text("Al escanearlo desde Nexo se completan el nombre y la cuenta.", color = Muted, textAlign = TextAlign.Center, modifier = Modifier.padding(8.dp, 8.dp, 8.dp, 20.dp)) }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Color.White), shape = RoundedCornerShape(24.dp)) {
                    Canvas(Modifier.padding(20.dp).size(232.dp).semantics { contentDescription = "Codigo QR de demostracion para recibir en la cuenta terminada en ${account?.number?.takeLast(4) ?: "4821"}" }) {
                        drawRect(Color.White)
                        val module = size.width / qr.width
                        for (x in 0 until qr.width) for (y in 0 until qr.height) if (qr[x, y]) drawRect(Ink, Offset(x * module, y * module), Size(module + .5f, module + .5f))
                    }
                }
            }
            item { Card(colors = CardDefaults.cardColors(containerColor = Lime.copy(alpha = .3f)), modifier = Modifier.fillMaxWidth().padding(top = 18.dp)) { Column(Modifier.padding(16.dp)) { ReceiptLine("Titular", account?.holder ?: "Alex Morgan"); ReceiptLine("Cuenta", account?.number ?: "104800004821") } } }
            item {
                Spacer(Modifier.height(16.dp))
                PrimaryButton("Compartir datos") {
                    val text = "Nexo demo\nTitular: $holder\nCuenta: $accountNumber\nQR: $payload\nNo mueve dinero real."
                    context.startActivity(Intent.createChooser(Intent(Intent.ACTION_SEND).apply { type = "text/plain"; putExtra(Intent.EXTRA_TEXT, text) }, "Compartir datos de demostracion"))
                }
            }
        }
    }
}

@Composable
private fun CardsScreen(
    settings: CardSettingsEntity,
    onBack: () -> Unit,
    onLockedChange: (Boolean) -> Unit,
    onInternationalChange: (Boolean) -> Unit,
    onContactlessChange: (Boolean) -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Mis tarjetas", "CONTROLES DE DEMOSTRACION", "Volver", onBack)
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { Card(colors = CardDefaults.cardColors(containerColor = Ink), modifier = Modifier.fillMaxWidth()) { Column(Modifier.padding(22.dp)) { Brand(true); Spacer(Modifier.height(36.dp)); Text("----  ----  ----  4821", color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Bold); Spacer(Modifier.height(24.dp)); Text("ALEX MORGAN    DEBITO DEMO", color = Lime, fontSize = 13.sp) } } }
            item { CardControl("Bloqueo local", "No bloquea una tarjeta real", settings.locked, onLockedChange) }
            item { CardControl("Compras internacionales", "Preferencia guardada en SQLite", settings.international, onInternationalChange) }
            item { CardControl("Pagos sin contacto", "Preferencia guardada en SQLite", settings.contactless, onContactlessChange) }
        }
    }
}

@Composable
private fun ProfileScreen(onCards: () -> Unit, onLogout: () -> Unit) {
    Column(Modifier.fillMaxSize()) {
        ScreenHeader("Mi cuenta", "PERFIL FICTICIO")
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { Card(colors = CardDefaults.cardColors(containerColor = Color.White)) { Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) { Box(Modifier.size(70.dp).background(Lime, RoundedCornerShape(22.dp)), contentAlignment = Alignment.Center) { Text("AM", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold) }; Spacer(Modifier.height(12.dp)); Text("Alex Morgan", fontSize = 20.sp, fontWeight = FontWeight.ExtraBold); Text("Perfil exclusivo de demostracion", color = Muted) } } }
            item { SettingsRow("Mis tarjetas", "Controles locales", onCards) }
            item { SettingsRow("Cerrar sesion", "Volver al acceso", onLogout, danger = true) }
            item { Text("Nexo demo 2.0 - SQLite local", modifier = Modifier.fillMaxWidth().padding(20.dp), textAlign = TextAlign.Center, color = Muted, fontSize = 13.sp) }
        }
    }
}

@Composable
private fun SavingsCard(savings: Long, onAdd: () -> Unit) {
    val progress = (savings / 5_000_000f).coerceIn(0f, 1f)
    Card(colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(20.dp)) {
            SectionTitle("META", "Viaje a la costa")
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(Modifier.size(78.dp).semantics { progressBarRangeInfo = ProgressBarRangeInfo(progress, 0f..1f) }, contentAlignment = Alignment.Center) {
                    Canvas(Modifier.fillMaxSize()) { drawArc(Color(0xFFDCE9E6), -90f, 360f, false, style = Stroke(9.dp.toPx(), cap = StrokeCap.Round)); drawArc(NexoBlue, -90f, progress * 360f, false, style = Stroke(9.dp.toPx(), cap = StrokeCap.Round)) }
                    Text("${(progress * 100).toInt()}%", fontWeight = FontWeight.ExtraBold)
                }
                Spacer(Modifier.width(18.dp))
                Column { Text(currency(savings), fontSize = 19.sp, fontWeight = FontWeight.ExtraBold); Text("de ${currency(5_000_000)}", color = Muted); TextButton(onClick = onAdd, contentPadding = PaddingValues(0.dp)) { Text("Simular aporte de $ 100.000") } }
            }
        }
    }
}

@Composable
private fun BillCard(bill: BillEntity, onPay: () -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(Modifier.size(44.dp).background(accent(bill.accent).copy(alpha = .2f), RoundedCornerShape(13.dp)), contentAlignment = Alignment.Center) { Text(bill.name.take(1), fontWeight = FontWeight.ExtraBold) }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) { Text(bill.name, fontWeight = FontWeight.Bold); Text(currency(bill.amount), color = Ink); Text(bill.dueLabel, color = if (bill.paid) Success else Coral, fontSize = 13.sp) }
            if (bill.paid) Text("PAGADO", color = Success, fontWeight = FontWeight.Bold, fontSize = 12.sp) else OutlinedButton(onClick = onPay) { Text("Pagar") }
        }
    }
}

@Composable
private fun CardControl(title: String, subtitle: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    Card(colors = CardDefaults.cardColors(containerColor = Color.White)) { Row(Modifier.fillMaxWidth().padding(17.dp), verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Text(title, fontWeight = FontWeight.Bold); Text(subtitle, color = Muted, fontSize = 13.sp) }; Switch(checked, onChange) } }
}

@Composable
private fun MovementRow(movement: MovementEntity) {
    Row(Modifier.fillMaxWidth().padding(vertical = 13.dp), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(42.dp).background(accent(movement.accent).copy(alpha = .2f), RoundedCornerShape(13.dp)), contentAlignment = Alignment.Center) { Text(movement.name.take(1), fontWeight = FontWeight.ExtraBold) }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) { Text(movement.name, fontWeight = FontWeight.Bold, fontSize = 14.sp); Text("${movement.category} - ${DateFormat.getDateInstance(DateFormat.SHORT).format(Date(movement.timestamp))}", color = Muted, fontSize = 13.sp) }
        Text((if (movement.amount > 0) "+" else "-") + currency(movement.amount), color = if (movement.amount > 0) Success else Ink, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
    HorizontalDivider(color = Outline)
}

private fun accent(name: String): Color = when (name) { "coral" -> Coral; "blue" -> NexoBlue; "purple" -> Purple; else -> Lime }

@Composable
private fun QuickAction(symbol: String, title: String, color: Color, modifier: Modifier, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = modifier, colors = CardDefaults.cardColors(containerColor = Color.White)) { Column(Modifier.fillMaxWidth().padding(14.dp), horizontalAlignment = Alignment.CenterHorizontally) { Box(Modifier.size(40.dp).background(color.copy(alpha = .22f), RoundedCornerShape(12.dp)), contentAlignment = Alignment.Center) { Text(symbol, fontWeight = FontWeight.ExtraBold) }; Spacer(Modifier.height(8.dp)); Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp) } }
}

@Composable
private fun SettingsRow(title: String, subtitle: String, onClick: () -> Unit, danger: Boolean = false) {
    Card(onClick = onClick, colors = CardDefaults.cardColors(containerColor = Color.White), modifier = Modifier.fillMaxWidth()) { Row(Modifier.padding(17.dp), verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(38.dp).background((if (danger) Coral else NexoBlue).copy(alpha = .15f), RoundedCornerShape(11.dp)), contentAlignment = Alignment.Center) { Text(title.take(1), color = if (danger) Coral else NexoBlue, fontWeight = FontWeight.ExtraBold) }; Spacer(Modifier.width(13.dp)); Column { Text(title, color = if (danger) Coral else Ink, fontWeight = FontWeight.Bold); Text(subtitle, color = Muted, fontSize = 13.sp) } } }
}

@Composable
private fun NavSymbol(text: String, selected: Boolean, primary: Boolean) {
    val background = if (primary) Ink else if (selected) Lime.copy(alpha = .45f) else Color.Transparent
    Box(Modifier.size(if (primary) 42.dp else 34.dp).background(background, RoundedCornerShape(13.dp)).semantics { contentDescription = text }, contentAlignment = Alignment.Center) { Text(text, color = if (primary) Lime else Ink, fontWeight = FontWeight.ExtraBold, fontSize = if (text == "AM") 11.sp else 15.sp) }
}

@Composable
private fun ScreenHeader(title: String, subtitle: String, action: String? = null, onAction: () -> Unit = {}) {
    Row(Modifier.fillMaxWidth().background(Ink).statusBarsPadding().padding(horizontal = 20.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically) { Column(Modifier.weight(1f)) { Eyebrow(subtitle, Lime); Text(title, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold) }; action?.let { TextButton(onClick = onAction) { Text(it, color = Lime) } } }
}

@Composable
private fun SectionTitle(eyebrow: String, title: String, action: String? = null, onAction: () -> Unit = {}) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) { Column(Modifier.weight(1f)) { Eyebrow(eyebrow); Text(title, fontSize = 19.sp, fontWeight = FontWeight.ExtraBold) }; action?.let { TextButton(onClick = onAction) { Text(it) } } }
    Spacer(Modifier.height(10.dp))
}

@Composable
private fun Brand(light: Boolean = false) {
    Row(verticalAlignment = Alignment.CenterVertically) { Box(Modifier.size(36.dp).background(Lime, RoundedCornerShape(11.dp)), contentAlignment = Alignment.Center) { Text("N", color = Ink, fontWeight = FontWeight.ExtraBold, fontSize = 19.sp) }; Spacer(Modifier.width(9.dp)); Text("Nexo", color = if (light) Color.White else Ink, fontWeight = FontWeight.ExtraBold, fontSize = 21.sp) }
}

@Composable
private fun Eyebrow(text: String, color: Color = Muted) { Text(text, color = color, fontSize = 12.sp, letterSpacing = 1.2.sp, fontWeight = FontWeight.ExtraBold) }

@Composable
private fun PrimaryButton(text: String, onClick: () -> Unit) { Button(onClick = onClick, modifier = Modifier.fillMaxWidth().heightIn(min = 52.dp), colors = ButtonDefaults.buttonColors(containerColor = Ink)) { Text(text, fontWeight = FontWeight.Bold) } }

@Composable
private fun ReceiptLine(label: String, value: String) { Row(Modifier.fillMaxWidth().padding(vertical = 5.dp)) { Text(label, color = Muted, fontSize = 13.sp); Spacer(Modifier.weight(1f)); Text(value, color = Ink, fontWeight = FontWeight.Bold, fontSize = 13.sp) } }
