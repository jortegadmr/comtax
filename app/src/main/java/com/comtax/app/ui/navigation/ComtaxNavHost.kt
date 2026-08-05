package com.comtax.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navigation
import com.comtax.app.data.local.entity.TipoMovimiento
import com.comtax.app.ui.ajustes.AjustesScreen
import com.comtax.app.ui.ajustes.CuentasSobresScreen
import com.comtax.app.ui.ajustes.FusionSobresScreen
import com.comtax.app.ui.ajustes.PinSettingsScreen
import com.comtax.app.ui.altamovimiento.AltaMovimientoViewModel
import com.comtax.app.ui.altamovimiento.ConfirmacionMovimientoScreen
import com.comtax.app.ui.altamovimiento.FormularioMovimientoScreen
import com.comtax.app.ui.altamovimiento.SeleccionCuentaScreen
import com.comtax.app.ui.balance.BalanceScreen
import com.comtax.app.ui.balance.MovimientoDetalleScreen
import com.comtax.app.ui.home.HomeScreen
import com.comtax.app.ui.lock.CrearPrimeraCuentaScreen
import com.comtax.app.ui.lock.PinLockScreen
import com.comtax.app.ui.lock.PinSetupScreen
import kotlinx.coroutines.launch

private fun NavHostController.navegarLimpiando(destino: Any) {
    navigate(destino) {
        popUpTo(graph.id) { inclusive = true }
    }
}

@Composable
fun ComtaxNavHost() {
    val navController = rememberNavController()
    val vm: ComtaxNavHostViewModel = hiltViewModel()
    val scope = rememberCoroutineScope()
    val debeBloquear by vm.appLockController.debeBloquear.collectAsStateWithLifecycle()

    LaunchedEffect(debeBloquear) {
        if (debeBloquear && vm.tienePinConfigurado()) {
            navController.navegarLimpiando(Bloqueo)
        }
    }

    val inicio = remember { if (vm.tienePinConfigurado()) Bloqueo else PinSetup }

    NavHost(navController = navController, startDestination = inicio) {

        composable<Bloqueo> {
            PinLockScreen(onDesbloqueado = {
                vm.appLockController.notificarDesbloqueado()
                scope.launch {
                    val destino = if (vm.hayCuentas()) Inicio else CrearPrimeraCuenta
                    navController.navegarLimpiando(destino)
                }
            })
        }

        composable<PinSetup> {
            PinSetupScreen(onPinConfigurado = {
                navController.navegarLimpiando(CrearPrimeraCuenta)
            })
        }

        composable<CrearPrimeraCuenta> {
            CrearPrimeraCuentaScreen(onCuentaCreada = {
                navController.navegarLimpiando(Inicio)
            })
        }

        composable<Inicio> {
            HomeScreen(
                onIngreso = { navController.navigate(AltaMovimientoGraph(TipoMovimiento.INGRESO)) },
                onGasto = { navController.navigate(AltaMovimientoGraph(TipoMovimiento.GASTO)) },
                onBalance = { navController.navigate(Balance) },
                onAjustes = { navController.navigate(Ajustes) }
            )
        }

        navigation<AltaMovimientoGraph>(startDestination = SeleccionCuenta) {
            composable<SeleccionCuenta> { entry ->
                val parent = remember(entry) { navController.getBackStackEntry<AltaMovimientoGraph>() }
                val altaVm: AltaMovimientoViewModel = hiltViewModel(parent)
                SeleccionCuentaScreen(
                    viewModel = altaVm,
                    onCuentaElegida = { navController.navigate(FormularioMovimiento) }
                )
            }
            composable<FormularioMovimiento> { entry ->
                val parent = remember(entry) { navController.getBackStackEntry<AltaMovimientoGraph>() }
                val altaVm: AltaMovimientoViewModel = hiltViewModel(parent)
                FormularioMovimientoScreen(
                    viewModel = altaVm,
                    onContinuar = { navController.navigate(ConfirmacionMovimiento) }
                )
            }
            composable<ConfirmacionMovimiento> { entry ->
                val parent = remember(entry) { navController.getBackStackEntry<AltaMovimientoGraph>() }
                val altaVm: AltaMovimientoViewModel = hiltViewModel(parent)
                ConfirmacionMovimientoScreen(
                    viewModel = altaVm,
                    onConfirmado = { navController.popBackStack(Inicio, inclusive = false) },
                    onCancelar = { navController.popBackStack(Inicio, inclusive = false) }
                )
            }
        }

        composable<Balance> {
            BalanceScreen(
                onVolver = { navController.popBackStack() },
                onMovimientoClick = { id -> navController.navigate(MovimientoDetalle(id)) }
            )
        }
        composable<MovimientoDetalle> {
            MovimientoDetalleScreen(onVolver = { navController.popBackStack() })
        }

        composable<Ajustes> {
            AjustesScreen(
                onCuentasSobres = { navController.navigate(CuentasSobres) },
                onFusionSobres = { navController.navigate(FusionSobres) },
                onPinSettings = { navController.navigate(PinSettings) }
            )
        }
        composable<CuentasSobres> { CuentasSobresScreen() }
        composable<FusionSobres> { FusionSobresScreen() }
        composable<PinSettings> { PinSettingsScreen() }
    }
}
