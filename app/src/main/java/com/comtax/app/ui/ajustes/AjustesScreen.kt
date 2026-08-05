package com.comtax.app.ui.ajustes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.comtax.app.ui.common.BotonGrandeSecundario
import com.comtax.app.ui.common.ScaffoldConVolver
import com.comtax.app.ui.theme.Dimens

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjustesScreen(
    onVolver: () -> Unit,
    onCuentasSobres: () -> Unit,
    onFusionSobres: () -> Unit,
    onPinSettings: () -> Unit
) {
    ScaffoldConVolver(titulo = "Ajustes", onVolver = onVolver) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Dimens.espacioPantalla),
            verticalArrangement = Arrangement.spacedBy(Dimens.espacioEntreBotones)
        ) {
            BotonGrandeSecundario(texto = "Cuentas y sobres", onClick = onCuentasSobres)
            BotonGrandeSecundario(texto = "Fusionar sobres", onClick = onFusionSobres)
            BotonGrandeSecundario(texto = "Cambiar PIN", onClick = onPinSettings)
        }
    }
}
