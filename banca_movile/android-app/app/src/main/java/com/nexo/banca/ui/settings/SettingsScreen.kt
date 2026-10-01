package com.nexo.banca.ui.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexo.banca.data.preferences.ThemeMode
import com.nexo.banca.ui.components.NexoHeader
import com.nexo.banca.ui.components.NexoSection
import com.nexo.banca.ui.theme.Accent
import com.nexo.banca.ui.theme.AccentSoft
import com.nexo.banca.ui.theme.Ink
import com.nexo.banca.ui.theme.Muted
import com.nexo.banca.ui.theme.Surface

@Composable
fun SettingsScreen(
    biometricOnLaunch: Boolean,
    themeMode: ThemeMode,
    showBalanceOnHome: Boolean,
    onBiometricOnLaunchChange: (Boolean) -> Unit,
    onThemeModeChange: (ThemeMode) -> Unit,
    onShowBalanceOnHomeChange: (Boolean) -> Unit,
    onBack: () -> Unit
) {
    Column(Modifier.fillMaxSize()) {
        NexoHeader("Configuracion", "PREFERENCIAS", "Volver", onBack)
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    NexoSection("ACCESO", "Biometria")
                    SettingSwitch(
                        title = "Solicitar al iniciar",
                        description = "Abre el dialogo de huella o rostro al entrar. La clave demo siempre queda disponible.",
                        checked = biometricOnLaunch,
                        onCheckedChange = onBiometricOnLaunchChange
                    )
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    NexoSection("APARIENCIA", "Tema de la aplicacion")
                    SettingsCard {
                        Text("Selecciona como se muestran fondos, paneles y textos.", color = Muted, fontSize = 13.sp)
                        Spacer(Modifier.height(12.dp))
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            ThemeChoice("Sistema", ThemeMode.SYSTEM, themeMode, onThemeModeChange, Modifier.weight(1f))
                            ThemeChoice("Claro", ThemeMode.LIGHT, themeMode, onThemeModeChange, Modifier.weight(1f))
                            ThemeChoice("Oscuro", ThemeMode.DARK, themeMode, onThemeModeChange, Modifier.weight(1f))
                        }
                    }
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    NexoSection("PRIVACIDAD", "Informacion visible")
                    SettingSwitch(
                        title = "Mostrar saldo al iniciar",
                        description = "Si lo desactivas, el saldo aparecera oculto hasta que pulses Mostrar.",
                        checked = showBalanceOnHome,
                        onCheckedChange = onShowBalanceOnHomeChange
                    )
                }
            }
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    NexoSection("PROTECCION LOCAL", "Estado de seguridad")
                    SettingsCard {
                        SecurityLine("Capturas de pantalla", "Bloqueadas")
                        SecurityLine("Copias de seguridad", "Desactivadas")
                        SecurityLine("Datos de la demo", "Solo en este dispositivo")
                        Text(
                            "La biometria es una puerta local de demostracion. Nexo no accede ni almacena tu huella o rostro.",
                            color = Muted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 10.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun SettingSwitch(title: String, description: String, checked: Boolean, onCheckedChange: (Boolean) -> Unit) {
    SettingsCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f).padding(end = 12.dp)) {
                Text(title, color = Ink, fontWeight = FontWeight.SemiBold)
                Text(description, color = Muted, fontSize = 12.sp, lineHeight = 17.sp)
            }
            Switch(checked = checked, onCheckedChange = onCheckedChange)
        }
    }
}

@Composable
private fun SettingsCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Surface.copy(alpha = .94f)),
        border = BorderStroke(1.dp, AccentSoft),
        elevation = CardDefaults.cardElevation(3.dp),
        shape = RoundedCornerShape(22.dp)
    ) {
        Column(Modifier.fillMaxWidth().padding(18.dp), content = content)
    }
}

@Composable
private fun ThemeChoice(label: String, mode: ThemeMode, selectedMode: ThemeMode, onSelect: (ThemeMode) -> Unit, modifier: Modifier) {
    FilterChip(
        selected = selectedMode == mode,
        onClick = { onSelect(mode) },
        label = { Text(label, fontSize = 12.sp) },
        modifier = modifier
    )
}

@Composable
private fun SecurityLine(label: String, value: String) {
    Row(Modifier.fillMaxWidth().padding(vertical = 5.dp), verticalAlignment = Alignment.CenterVertically) {
        Text(label, color = Ink, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Text(value, color = Accent, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
    }
}
