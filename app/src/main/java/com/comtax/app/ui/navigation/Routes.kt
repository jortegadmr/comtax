package com.comtax.app.ui.navigation

import com.comtax.app.data.local.entity.TipoMovimiento
import kotlinx.serialization.Serializable

@Serializable data object Bloqueo
@Serializable data object PinSetup
@Serializable data object CrearPrimeraCuenta
@Serializable data object Inicio

@Serializable data class AltaMovimientoGraph(val tipo: TipoMovimiento)
@Serializable data object SeleccionCuenta
@Serializable data object FormularioMovimiento
@Serializable data object ConfirmacionMovimiento

@Serializable data object Balance
@Serializable data class BalanceCuenta(val cuentaId: Long)
@Serializable data class MovimientoDetalle(val id: Long)

@Serializable data object Ajustes
@Serializable data object CuentasSobres
@Serializable data object FusionSobres
@Serializable data object PinSettings
