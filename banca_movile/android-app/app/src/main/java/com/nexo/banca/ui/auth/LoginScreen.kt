package com.nexo.banca.ui.auth

import android.widget.Toast
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.nexo.banca.domain.BankingRules
import com.nexo.banca.ui.components.NexoBrand
import com.nexo.banca.ui.components.NexoPrimaryButton
import com.nexo.banca.ui.theme.Accent
import com.nexo.banca.ui.theme.Ink
import com.nexo.banca.ui.theme.Muted
import com.nexo.banca.ui.theme.Paper
import com.nexo.banca.ui.theme.Surface

@Composable
fun LoginScreen(
    onLogin: () -> Unit,
    biometricOnLaunch: Boolean = true,
    preferencesLoaded: Boolean = true
) {
    val context = LocalContext.current
    val activity = context as? FragmentActivity
    val currentOnLogin by rememberUpdatedState(onLogin)
    var user by rememberSaveable { mutableStateOf(BankingRules.DEMO_USER) }
    var password by rememberSaveable { mutableStateOf(BankingRules.DEMO_PASSWORD) }
    var showPassword by rememberSaveable { mutableStateOf(false) }
    var attempted by rememberSaveable { mutableStateOf(false) }
    var automaticPromptShown by rememberSaveable { mutableStateOf(false) }
    val biometricAvailable = remember(context) {
        BiometricManager.from(context).canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK) == BiometricManager.BIOMETRIC_SUCCESS
    }
    val biometricPrompt = remember(activity) {
        activity?.let {
            BiometricPrompt(
                it,
                ContextCompat.getMainExecutor(context),
                object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        currentOnLogin()
                    }

                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        if (errorCode != BiometricPrompt.ERROR_NEGATIVE_BUTTON && errorCode != BiometricPrompt.ERROR_USER_CANCELED) {
                            Toast.makeText(context, errString, Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
        }
    }
    val promptInfo = remember {
        BiometricPrompt.PromptInfo.Builder()
            .setTitle("Entrar a Nexo Demo")
            .setSubtitle("Confirma tu identidad con biometria")
            .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_WEAK)
            .setNegativeButtonText("Usar clave")
            .build()
    }
    LaunchedEffect(preferencesLoaded, biometricOnLaunch, biometricAvailable, biometricPrompt) {
        if (preferencesLoaded && biometricOnLaunch && biometricAvailable && biometricPrompt != null && !automaticPromptShown) {
            automaticPromptShown = true
            biometricPrompt.authenticate(promptInfo)
        }
    }
    val submit = {
        attempted = true
        if (BankingRules.validCredentials(user, password)) onLogin()
        else Toast.makeText(context, "Usa las credenciales de demostracion indicadas", Toast.LENGTH_LONG).show()
    }

    LazyColumn(modifier = Modifier.fillMaxSize().background(Paper), contentPadding = PaddingValues(bottom = 32.dp)) {
        item {
            Box(Modifier.fillMaxWidth().heightIn(min = 190.dp).statusBarsPadding().padding(24.dp)) {
                Column {
                    NexoBrand()
                    Spacer(Modifier.height(34.dp))
                    Text("DEMO LOCAL", color = Accent, fontSize = 11.sp, letterSpacing = 1.1.sp, fontWeight = FontWeight.Bold)
                    Text("Tus finanzas,\nsin ruido.", color = Ink, fontSize = 29.sp, lineHeight = 34.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        item {
            Column(Modifier.padding(horizontal = 24.dp, vertical = 20.dp)) {
                Text("Acceso", color = Ink, fontSize = 22.sp, fontWeight = FontWeight.SemiBold)
                Text("Usa biometria o los datos ficticios de la demostracion.", color = Muted, fontSize = 13.sp)
                Spacer(Modifier.height(18.dp))
                if (biometricAvailable && biometricPrompt != null) {
                    OutlinedButton(
                        onClick = { biometricPrompt.authenticate(promptInfo) },
                        modifier = Modifier.fillMaxWidth().heightIn(min = 50.dp)
                    ) { Text("Entrar con biometria") }
                    Spacer(Modifier.height(14.dp))
                    Text("O usa las credenciales", color = Muted, fontSize = 12.sp)
                    Spacer(Modifier.height(8.dp))
                }
                OutlinedTextField(
                    value = user,
                    onValueChange = { user = it },
                    label = { Text("Correo") },
                    supportingText = if (attempted && !user.equals(BankingRules.DEMO_USER, true)) ({ Text("Usa ${BankingRules.DEMO_USER}") }) else null,
                    isError = attempted && !user.equals(BankingRules.DEMO_USER, true),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email, imeAction = ImeAction.Next),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(8.dp))
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Clave") },
                    visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = { TextButton(onClick = { showPassword = !showPassword }) { Text(if (showPassword) "Ocultar" else "Ver") } },
                    supportingText = if (attempted && password != BankingRules.DEMO_PASSWORD) ({ Text("Usa la clave de la demo") }) else null,
                    isError = attempted && password != BankingRules.DEMO_PASSWORD,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password, imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { submit() }),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(12.dp))
                NexoPrimaryButton("Entrar", submit)
                Text(
                    "${BankingRules.DEMO_USER}  /  ${BankingRules.DEMO_PASSWORD}",
                    modifier = Modifier.fillMaxWidth().padding(top = 14.dp).background(Surface, RoundedCornerShape(7.dp)).padding(12.dp),
                    color = Muted,
                    fontSize = 12.sp
                )
            }
        }
    }
}
