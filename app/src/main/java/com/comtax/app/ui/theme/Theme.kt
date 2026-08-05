package com.comtax.app.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ComtaxColorScheme = lightColorScheme(
    primary = VerdePrimario,
    onPrimary = BlancoSobrePrimario,
    primaryContainer = VerdePrimarioOscuro,
    onPrimaryContainer = BlancoSobrePrimario,
    error = RojoGasto,
    onError = BlancoSobrePrimario,
    background = FondoApp,
    onBackground = TextoPrincipal,
    surface = SuperficieApp,
    onSurface = TextoPrincipal,
    outline = BordeFuerte
)

// Tema unico, fijo en claro: predictibilidad y contraste garantizado priman sobre
// seguir el modo oscuro del sistema, dado el perfil de usuario (persona mayor).
@Composable
fun ComtaxTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ComtaxColorScheme,
        typography = ComtaxTypography,
        content = content
    )
}
