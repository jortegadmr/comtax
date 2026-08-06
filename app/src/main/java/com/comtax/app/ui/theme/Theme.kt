package com.comtax.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val ComtaxColorScheme = lightColorScheme(
    primary = AccentIndigo,
    onPrimary = BlancoSobrePrimario,
    primaryContainer = AccentIndigoContenedor,
    onPrimaryContainer = AccentIndigoOscuro,
    secondary = TextoSecundario,
    onSecondary = BlancoSobrePrimario,
    secondaryContainer = SuperficieAlt,
    onSecondaryContainer = TextoPrincipal,
    tertiary = AccentIndigoOscuro,
    onTertiary = BlancoSobrePrimario,
    tertiaryContainer = AccentIndigoContenedor,
    onTertiaryContainer = AccentIndigoOscuro,
    error = Gasto,
    onError = BlancoSobrePrimario,
    errorContainer = GastoContenedor,
    onErrorContainer = Gasto,
    background = Fondo,
    onBackground = TextoPrincipal,
    surface = Superficie,
    onSurface = TextoPrincipal,
    surfaceVariant = SuperficieAlt,
    onSurfaceVariant = TextoSecundario,
    outline = BordeFuncional,
    outlineVariant = BordeDecorativo
)

// Unico radio de esquina que la app sobreescribe: el de Card ("medium" en M3),
// para que todas las tarjetas compartan el mismo radio sin que cada pantalla
// tenga que declararlo. El resto de familias (chips, dialogos, campos de texto)
// ya son uniformes con los valores por defecto de Material 3.
private val ComtaxShapes = Shapes(
    medium = RoundedCornerShape(Dimens.radioBorde)
)

// Tema unico, fijo en claro — sin alternativa oscura por decision explicita de producto.
@Composable
fun ComtaxTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ComtaxColorScheme,
        typography = ComtaxTypography,
        shapes = ComtaxShapes,
        content = content
    )
}
