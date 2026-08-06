package com.comtax.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
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

// Unico radio de esquina que la app sobreescribe: el de Card ("medium" en M3),
// para que todas las tarjetas compartan el mismo radio sin que cada pantalla
// tenga que declararlo. El resto de familias (chips, dialogos, campos de texto)
// ya son uniformes con los valores por defecto de Material 3.
private val ComtaxShapes = Shapes(
    medium = RoundedCornerShape(Dimens.radioBorde)
)

// Tema unico, fijo en claro: predictibilidad y contraste garantizado priman sobre
// seguir el modo oscuro del sistema, dado el perfil de usuario (persona mayor).
@Composable
fun ComtaxTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ComtaxColorScheme,
        typography = ComtaxTypography,
        shapes = ComtaxShapes,
        content = content
    )
}
