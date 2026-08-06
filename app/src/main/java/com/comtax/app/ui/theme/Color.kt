package com.comtax.app.ui.theme

import androidx.compose.ui.graphics.Color

// Paleta fija de alto contraste (WCAG AA/AAA). No se usa dynamicColor: el contraste
// no puede depender del fondo de pantalla del usuario en una app para baja visión.
val VerdePrimario = Color(0xFF0B5D3B)
val VerdePrimarioOscuro = Color(0xFF07341F)
val BlancoSobrePrimario = Color(0xFFFFFFFF)

val RojoGasto = Color(0xFFB3261E)
val VerdeIngreso = Color(0xFF0B5D3B)

val AmbarAviso = Color(0xFF8A5300)
val AmbarAvisoFondo = Color(0xFFFFF3E0)

val FondoApp = Color(0xFFFFFFFF)
val SuperficieApp = Color(0xFFF5F5F5)
val TextoPrincipal = Color(0xFF1A1A1A)
val TextoSecundario = Color(0xFF444444)
val BordeFuerte = Color(0xFF767680)

// Gradiente de marca (igual que el icono adaptativo de la app). Solo para superficies
// grandes (cabecera de saldo, CTA principal) — a esta saturacion NO cumple contraste
// AA como color de texto, por eso los textos siguen usando VerdePrimario.
val VerdeMarcaInicio = Color(0xFF0EA265)
val VerdeMarcaFin = Color(0xFF1AC1B6)
