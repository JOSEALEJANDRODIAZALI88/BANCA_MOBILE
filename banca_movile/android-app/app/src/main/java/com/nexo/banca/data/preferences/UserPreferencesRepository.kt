package com.nexo.banca.data.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.nexoPreferences by preferencesDataStore(name = "nexo_preferences")

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK;

    companion object {
        fun fromStorage(value: String?): ThemeMode = entries.firstOrNull { it.name == value } ?: SYSTEM
    }
}

data class UserPreferences(
    val biometricOnLaunch: Boolean = true,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val showBalanceOnHome: Boolean = true,
    val loaded: Boolean = false
)

class UserPreferencesRepository(private val context: Context) {
    private object Keys {
        val biometricOnLaunch = booleanPreferencesKey("biometric_on_launch")
        val themeMode = stringPreferencesKey("theme_mode")
        val showBalanceOnHome = booleanPreferencesKey("show_balance_on_home")
    }

    val preferences = context.nexoPreferences.data
        .catch { error ->
            if (error is IOException) emit(emptyPreferences()) else throw error
        }
        .map { values ->
            UserPreferences(
                biometricOnLaunch = values[Keys.biometricOnLaunch] ?: true,
                themeMode = ThemeMode.fromStorage(values[Keys.themeMode]),
                showBalanceOnHome = values[Keys.showBalanceOnHome] ?: true,
                loaded = true
            )
        }

    suspend fun setBiometricOnLaunch(enabled: Boolean) {
        context.nexoPreferences.edit { it[Keys.biometricOnLaunch] = enabled }
    }

    suspend fun setThemeMode(mode: ThemeMode) {
        context.nexoPreferences.edit { it[Keys.themeMode] = mode.name }
    }

    suspend fun setShowBalanceOnHome(enabled: Boolean) {
        context.nexoPreferences.edit { it[Keys.showBalanceOnHome] = enabled }
    }

    suspend fun reset() {
        context.nexoPreferences.edit { it.clear() }
    }
}
