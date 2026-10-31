package ui.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = Red,
    onPrimary = White,
    primaryContainer = LightRed,
    onPrimaryContainer = Red,
    background = LightRed,
    onBackground = Red,
    surface = White,
    onSurface = Black,
    surfaceVariant = LightRed,
    onSurfaceVariant = Red,
    outline = Red,
    error = Red
)

private val DarkColors = darkColorScheme(
    primary = Red,
    onPrimary = White,
    primaryContainer = DarkGray,
    onPrimaryContainer = White,
    background = DarkGray,
    onBackground = White,
    surface = Gris,
    onSurface = White,
    surfaceVariant = Gris,
    onSurfaceVariant = White,
    outline = Red,
    error = Red
)

@Composable
fun SecureVaultTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
