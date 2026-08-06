package com.comtax.app.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush

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

// Gradiente de marca para superficies grandes SIN texto encima (p.ej. la franja
// decorativa sobre "Saldo actual" en BalanceCuentaScreen). Deliberadamente NO se usa
// como fondo de BotonGrande/CTA: el label blanco de esos botones no llega a contraste
// AA sobre este gradiente a plena saturacion, y ese riesgo pesa mas que la coherencia
// de marca en botones. Ver comentario junto a VerdeMarcaInicio.
fun brandGradient(): Brush = Brush.linearGradient(listOf(VerdeMarcaInicio, VerdeMarcaFin))

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
