package com.comtax.app.ui.theme

import androidx.compose.ui.graphics.Color

// Paleta propia para identificar cuentas, deliberadamente ajena a los colores
// semanticos de la app (verde = ingreso/primario, rojo = gasto) para que
// nunca se confunda el color de una cuenta con el de un movimiento.
val paletaCuentas = listOf(
    "#1565C0", // Azul
    "#6A1B9A", // Morado
    "#E65100", // Naranja quemado
    "#00838F", // Turquesa oscuro
    "#AD1457"  // Granate
)

fun colorDesdeHex(hex: String): Color = Color(android.graphics.Color.parseColor(hex))

fun siguienteColorLibre(cuentasExistentes: Int): String =
    paletaCuentas[cuentasExistentes % paletaCuentas.size]
