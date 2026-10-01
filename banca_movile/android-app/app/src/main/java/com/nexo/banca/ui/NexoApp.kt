package com.nexo.banca.ui

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.journeyapps.barcodescanner.ScanContract
import com.journeyapps.barcodescanner.ScanOptions
import com.nexo.banca.data.OperationResult
import com.nexo.banca.domain.QrTransfer
import com.nexo.banca.domain.QrTransferCodec
import com.nexo.banca.ui.auth.LoginScreen
import com.nexo.banca.ui.home.ActivityScreen
import com.nexo.banca.ui.home.HomeScreen
import com.nexo.banca.ui.more.MoreScreen
import com.nexo.banca.ui.payments.PaymentsScreen
import com.nexo.banca.ui.profile.CardsScreen
import com.nexo.banca.ui.profile.ProfileScreen
import com.nexo.banca.ui.qr.ReceiveMoneyScreen
import com.nexo.banca.ui.settings.SettingsScreen
import com.nexo.banca.ui.theme.Accent
import com.nexo.banca.ui.theme.AccentSoft
import com.nexo.banca.ui.theme.Muted
import com.nexo.banca.ui.theme.Paper
import com.nexo.banca.ui.theme.Surface
import com.nexo.banca.ui.transfer.TransferScreen

private enum class Screen(val label: String, val symbol: String) {
    HOME("Inicio", "I"),
    ACTIVITY("Movimientos", "M"),
    MORE("Mas", "+"),
    TRANSFER("Transferir", "T"),
    PAYMENTS("Pagos", "P"),
    PROFILE("Perfil", "AM"),
    CARDS("Tarjetas", "C"),
    RECEIVE("Recibir", "QR"),
    SETTINGS("Configuracion", "AJ")
}

private val tabs = listOf(Screen.HOME, Screen.ACTIVITY, Screen.MORE)

@Composable
fun NexoRoot(state: NexoUiState, viewModel: NexoViewModel) {
    var loggedIn by rememberSaveable { mutableStateOf(false) }
    if (!loggedIn) {
        LoginScreen(
            onLogin = { loggedIn = true },
            biometricOnLaunch = state.preferences.biometricOnLaunch,
            preferencesLoaded = state.preferences.loaded
        )
        return
    }
    NexoApp(state = state, viewModel = viewModel, onLogout = { loggedIn = false })
}

@Composable
private fun NexoApp(state: NexoUiState, viewModel: NexoViewModel, onLogout: () -> Unit) {
    val context = LocalContext.current
    var screenName by rememberSaveable { mutableStateOf(Screen.HOME.name) }
    var returnScreenName by rememberSaveable { mutableStateOf(Screen.HOME.name) }
    var scannedTransfer by remember { mutableStateOf<QrTransfer?>(null) }
    val screen = Screen.entries.firstOrNull { it.name == screenName } ?: Screen.HOME
    val notify: (String) -> Unit = { Toast.makeText(context, it, Toast.LENGTH_LONG).show() }
    val navigate: (Screen) -> Unit = { target ->
        returnScreenName = screenName
        screenName = target.name
    }
    val scanner = rememberLauncherForActivityResult(ScanContract()) { result ->
        val transfer = result.contents?.let(QrTransferCodec::decode)
        if (transfer == null) notify(if (result.contents == null) "Lectura cancelada" else "El QR no pertenece a Nexo o contiene datos invalidos")
        else {
            scannedTransfer = transfer
            returnScreenName = Screen.HOME.name
            screenName = Screen.TRANSFER.name
            notify("Datos cargados. Revisa antes de confirmar")
        }
    }
    val scanQr = {
        scanner.launch(ScanOptions().setDesiredBarcodeFormats(ScanOptions.QR_CODE).setPrompt("Enfoca un QR de Nexo").setBeepEnabled(false).setOrientationLocked(false))
    }

    BackHandler(screen !in tabs || screen != Screen.HOME) {
        screenName = if (screen in tabs) Screen.HOME.name else returnScreenName
    }

    Scaffold(
        containerColor = Paper,
        bottomBar = {
            NavigationBar(
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp).clip(RoundedCornerShape(26.dp)),
                containerColor = Surface.copy(alpha = .96f),
                tonalElevation = 3.dp
            ) {
                tabs.forEach { tab ->
                    NavigationBarItem(
                        selected = screen == tab,
                        onClick = { screenName = tab.name },
                        icon = { NavSymbol(tab.symbol, screen == tab) },
                        label = { Text(tab.label, fontSize = 12.sp) }
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.padding(padding).fillMaxSize()) {
            when (screen) {
                Screen.HOME -> HomeScreen(
                    state = state,
                    onTransfer = { navigate(Screen.TRANSFER) },
                    onReceive = { navigate(Screen.RECEIVE) },
                    onPayments = { navigate(Screen.PAYMENTS) },
                    onActivity = { screenName = Screen.ACTIVITY.name },
                    onMore = { screenName = Screen.MORE.name },
                    onAddSavings = { viewModel.addSavings { notify(it.message()) } },
                    showBalanceInitially = state.preferences.showBalanceOnHome
                )
                Screen.ACTIVITY -> ActivityScreen(state.movements)
                Screen.MORE -> MoreScreen(
                    onPayments = { navigate(Screen.PAYMENTS) },
                    onTransfer = { navigate(Screen.TRANSFER) },
                    onReceive = { navigate(Screen.RECEIVE) },
                    onCards = { navigate(Screen.CARDS) },
                    onProfile = { navigate(Screen.PROFILE) },
                    onSettings = { navigate(Screen.SETTINGS) }
                )
                Screen.TRANSFER -> TransferScreen(state.account?.balance ?: 0, scannedTransfer, scanQr) { recipient, account, amount, concept, done -> viewModel.transfer(recipient, account, amount, concept, done) }
                Screen.PAYMENTS -> PaymentsScreen(state.payments, onPay = { id -> viewModel.payBill(id) { notify(it.message()) } })
                Screen.PROFILE -> ProfileScreen(state.account, onCards = { navigate(Screen.CARDS) }, onLogout = onLogout)
                Screen.CARDS -> CardsScreen(state.card, state.account, onBack = { screenName = returnScreenName }, onLockedChange = viewModel::updateCardLocked, onInternationalChange = viewModel::updateCardInternational, onContactlessChange = viewModel::updateCardContactless)
                Screen.RECEIVE -> ReceiveMoneyScreen(state.account, onBack = { screenName = returnScreenName })
                Screen.SETTINGS -> SettingsScreen(
                    biometricOnLaunch = state.preferences.biometricOnLaunch,
                    themeMode = state.preferences.themeMode,
                    showBalanceOnHome = state.preferences.showBalanceOnHome,
                    onBiometricOnLaunchChange = viewModel::setBiometricOnLaunch,
                    onThemeModeChange = viewModel::setThemeMode,
                    onShowBalanceOnHomeChange = viewModel::setShowBalanceOnHome,
                    onBack = { screenName = returnScreenName }
                )
            }
        }
    }
}

private fun OperationResult.message(): String = when (this) {
    is OperationResult.Success -> "Simulacion guardada. Referencia: $reference"
    is OperationResult.Error -> message
}

@Composable
private fun NavSymbol(text: String, selected: Boolean) {
    Box(
        Modifier.size(38.dp).background(if (selected) AccentSoft else Color.Transparent, RoundedCornerShape(9.dp)).semantics { contentDescription = text },
        contentAlignment = Alignment.Center
    ) {
        Text(text, color = if (selected) Accent else Muted, fontWeight = FontWeight.SemiBold, fontSize = if (text == "AM") 10.sp else 15.sp)
    }
}
