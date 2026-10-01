package com.nexo.banca.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexo.banca.data.local.CardSettingsEntity
import com.nexo.banca.data.local.AccountEntity
import com.nexo.banca.domain.DemoData
import com.nexo.banca.ui.components.NexoBrand
import com.nexo.banca.ui.components.NexoCard
import com.nexo.banca.ui.components.NexoHeader
import com.nexo.banca.ui.theme.AccentSoft
import com.nexo.banca.ui.theme.Danger
import com.nexo.banca.ui.theme.Ink
import com.nexo.banca.ui.theme.Muted
import com.nexo.banca.ui.theme.Outline
import com.nexo.banca.ui.theme.Surface

@Composable
fun ProfileScreen(account: AccountEntity?, onCards: () -> Unit, onLogout: () -> Unit) {
    val holder = account?.holder ?: DemoData.ACCOUNT_HOLDER
    val initials = holder.split(' ').take(2).mapNotNull { it.firstOrNull()?.toString() }.joinToString("")
    Column(Modifier.fillMaxSize()) {
        NexoHeader("Mi cuenta", "PERFIL FICTICIO")
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                NexoCard {
                    Row(Modifier.fillMaxWidth().padding(20.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(Modifier.size(44.dp).background(AccentSoft, CircleShape), contentAlignment = Alignment.Center) { Text(initials, fontSize = 14.sp, fontWeight = FontWeight.SemiBold) }
                        Column(Modifier.padding(start = 14.dp)) {
                            Text(holder, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                            Text("Cuenta terminada en ${account?.number?.takeLast(4) ?: DemoData.ACCOUNT_NUMBER.takeLast(4)}", color = Muted, fontSize = 13.sp)
                        }
                    }
                }
            }
            item { SettingsRow("Mis tarjetas", "Controles locales", onCards) }
            item { SettingsRow("Cerrar sesion", "Volver al acceso", onLogout, danger = true) }
            item { Text("Nexo demo 2.0 . SQLite local", modifier = Modifier.fillMaxWidth().padding(20.dp), textAlign = TextAlign.Center, color = Muted, fontSize = 12.sp) }
        }
    }
}

@Composable
fun CardsScreen(
    settings: CardSettingsEntity,
    account: AccountEntity?,
    onBack: () -> Unit,
    onLockedChange: (Boolean) -> Unit,
    onInternationalChange: (Boolean) -> Unit,
    onContactlessChange: (Boolean) -> Unit
) {
    val holder = account?.holder ?: DemoData.ACCOUNT_HOLDER
    val lastFour = account?.number?.takeLast(4) ?: DemoData.ACCOUNT_NUMBER.takeLast(4)
    Column(Modifier.fillMaxSize()) {
        NexoHeader("Mis tarjetas", "CONTROLES LOCALES", "Volver", onBack)
        LazyColumn(Modifier.weight(1f), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            item {
                Card(colors = CardDefaults.cardColors(containerColor = Ink), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(20.dp)) {
                        NexoBrand(light = true)
                        Spacer(Modifier.height(30.dp))
                        Text("----  ----  ----  $lastFour", color = Surface, fontSize = 18.sp, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(18.dp))
                        Text("${holder.uppercase()}   DEBITO DEMO", color = Surface.copy(alpha = .75f), fontSize = 12.sp)
                    }
                }
            }
            item { CardControl("Bloqueo local", "No bloquea una tarjeta real", settings.locked, onLockedChange) }
            item { CardControl("Compras internacionales", "Preferencia guardada localmente", settings.international, onInternationalChange) }
            item { CardControl("Pagos sin contacto", "Preferencia guardada localmente", settings.contactless, onContactlessChange) }
        }
    }
}

@Composable
private fun CardControl(title: String, subtitle: String, checked: Boolean, onChange: (Boolean) -> Unit) {
    NexoCard {
        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = Muted, fontSize = 12.sp)
            }
            Switch(checked = checked, onCheckedChange = onChange)
        }
    }
}

@Composable
private fun SettingsRow(title: String, subtitle: String, onClick: () -> Unit, danger: Boolean = false) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = Surface),
        border = BorderStroke(1.dp, Outline),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(title, color = if (danger) Danger else Ink, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = Muted, fontSize = 12.sp)
            }
            Text(">", color = if (danger) Danger else Muted)
        }
    }
}
