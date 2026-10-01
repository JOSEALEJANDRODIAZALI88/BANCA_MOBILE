package com.nexo.banca.data.preferences

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UserPreferencesTest {
    @Test
    fun defaultsEnableBiometricAndFollowSystemTheme() {
        val preferences = UserPreferences()

        assertTrue(preferences.biometricOnLaunch)
        assertEquals(ThemeMode.SYSTEM, preferences.themeMode)
        assertTrue(preferences.showBalanceOnHome)
        assertFalse(preferences.loaded)
    }

    @Test
    fun themeModeFallsBackToSystemForUnknownStorageValue() {
        assertEquals(ThemeMode.DARK, ThemeMode.fromStorage("DARK"))
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromStorage("UNKNOWN"))
        assertEquals(ThemeMode.SYSTEM, ThemeMode.fromStorage(null))
    }
}
