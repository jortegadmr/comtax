package com.comtax.app.ui.altamovimiento

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.comtax.app.data.local.entity.TipoMovimiento
import com.comtax.app.ui.common.BotonGrande
import com.comtax.app.ui.common.EstadoVacio
import com.comtax.app.ui.common.ScaffoldConVolver
import com.comtax.app.ui.theme.BlancoSobrePrimario
import com.comtax.app.ui.theme.Dimens
import com.comtax.app.ui.theme.colorDesdeHex

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SeleccionCuentaScreen(
    viewModel: AltaMovimientoViewModel,
    onVolver: () -> Unit,
    onIrAAjustes: () -> Unit,
    onCuentaElegida: () -> Unit
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val tituloTipo = if (estado.tipo == TipoMovimiento.INGRESO) "Ingreso" else "Gasto"

    ScaffoldConVolver(titulo = "$tituloTipo — elige la cuenta", onVolver = onVolver) { padding ->
        if (estado.cuentas.isEmpty()) {
            EstadoVacio(
                mensaje = "No hay cuentas creadas todavía.\nCréalas desde Ajustes.",
                modifier = Modifier.padding(padding),
                accion = { BotonGrande(texto = "Ir a Ajustes", onClick = onIrAAjustes) }
            )
            return@ScaffoldConVolver
        }

        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(Dimens.espacioPantalla)) {
            Spacer(modifier = Modifier.padding(top = Dimens.espacioMinimo))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(Dimens.espacioEntreBotones)) {
                items(estado.cuentas) { cuenta ->
                    BotonGrande(
                        texto = cuenta.nombre,
                        onClick = {
                            viewModel.onCuentaSeleccionada(cuenta)
                            onCuentaElegida()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = colorDesdeHex(cuenta.colorHex),
                            contentColor = BlancoSobrePrimario
                        )
                    )
                }
            }
        }
    }
}
