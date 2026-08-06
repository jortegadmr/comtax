package com.comtax.app.ui.theme

import androidx.compose.ui.unit.dp

object Dimens {
    val alturaBotonGrande = 64.dp
    val espacioPantalla = 20.dp
    val espacioEntreBotones = 16.dp
    val radioBorde = 16.dp
    val radioBordeGrande = 24.dp

    // Escala de espaciado: nombra los valores que ya se repetian sueltos por
    // las pantallas, para tener un unico vocabulario de separacion vertical/horizontal.
    val espacioMinimo = 4.dp
    val espacioPequeno = 8.dp
    val espacioMedio = 12.dp
    val espacioEstandar = 16.dp
    val espacioGrande = 24.dp
    val espacioExtra = 32.dp
    val espacioSeccion = 48.dp

    // Escala de elevacion (sombra): solo Superficie (fondo claro) hace visible la
    // sombra "shadowElevation" de Material 3 — la tonal es practicamente invisible
    // aqui, asi que la app no la usa.
    val elevacionTarjeta = 2.dp
    val elevacionTarjetaProminente = 3.dp
    val elevacionHero = 8.dp
    val elevacionPresionada = 1.dp
}
