package com.nexo.banca.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.nexo.banca.data.preferences.ThemeMode

private val LightColors = lightColorScheme(
    primary = Color(0xFF356B55),
    onPrimary = Color(0xFFEDF4EF),
    primaryContainer = Color(0xFFCFE3D6),
    onPrimaryContainer = Color(0xFF214137),
    secondary = Color(0xFF3D7657),
    onSecondary = Color(0xFFEDF4EF),
    tertiary = Color(0xFF7C7480),
    background = Color(0xFFE4EFE7),
    onBackground = Color(0xFF214137),
    surface = Color(0xFFEDF4EF),
    onSurface = Color(0xFF214137),
    surfaceVariant = Color(0xFFD8E8DD),
    onSurfaceVariant = Color(0xFF53695F),
    outline = Color(0xFFB9CDBF),
    error = Color(0xFF956A61),
    onError = Color(0xFFEDF4EF)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF91C5A7),
    onPrimary = Color(0xFF10271F),
    primaryContainer = Color(0xFF294B3C),
    onPrimaryContainer = Color(0xFFD7EADF),
    secondary = Color(0xFF8FC09F),
    onSecondary = Color(0xFF10271F),
    tertiary = Color(0xFFB8A9C1),
    background = Color(0xFF0F1D18),
    onBackground = Color(0xFFDDEBE3),
    surface = Color(0xFF182A23),
    onSurface = Color(0xFFE4F0E8),
    surfaceVariant = Color(0xFF233A31),
    onSurfaceVariant = Color(0xFFA9BDB2),
    outline = Color(0xFF526D60),
    error = Color(0xFFD99A91),
    onError = Color(0xFF301713)
)

val Ink: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onBackground
val Accent: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primary
val AccentSoft: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.primaryContainer
val Danger: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.error
val Purple: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.tertiary
val Paper: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.background
val Surface: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.surface
val Muted: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.onSurfaceVariant
val Success: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.secondary
val Outline: Color
    @Composable @ReadOnlyComposable get() = MaterialTheme.colorScheme.outline

private val typography = Typography(
    headlineLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 30.sp, lineHeight = 36.sp),
    headlineMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 24.sp, lineHeight = 30.sp),
    titleLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 20.sp, lineHeight = 26.sp),
    titleMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.Bold, fontSize = 16.sp, lineHeight = 22.sp),
    bodyLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 16.sp, lineHeight = 24.sp),
    bodyMedium = TextStyle(fontFamily = FontFamily.SansSerif, fontSize = 14.sp, lineHeight = 20.sp),
    labelLarge = TextStyle(fontFamily = FontFamily.SansSerif, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
)

private val shapes = Shapes(
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(18.dp),
    large = RoundedCornerShape(26.dp)
)

@Composable
fun NexoTheme(themeMode: ThemeMode = ThemeMode.SYSTEM, content: @Composable () -> Unit) {
    val darkTheme = when (themeMode) {
        ThemeMode.SYSTEM -> isSystemInDarkTheme()
        ThemeMode.LIGHT -> false
        ThemeMode.DARK -> true
    }
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}
