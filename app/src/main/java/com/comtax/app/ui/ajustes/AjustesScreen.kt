package com.comtax.app.ui.ajustes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.comtax.app.ui.common.BotonGrandeSecundario
import com.comtax.app.ui.theme.Dimens

@Composable
fun AjustesScreen(
    onCuentasSobres: () -> Unit,
    onFusionSobres: () -> Unit,
    onPinSettings: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.espacioPantalla)
    ) {
        Text(text = "Ajustes", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.padding(top = 24.dp))
        Column(verticalArrangement = Arrangement.spacedBy(Dimens.espacioEntreBotones)) {
            BotonGrandeSecundario(texto = "Cuentas y sobres", onClick = onCuentasSobres)
            BotonGrandeSecundario(texto = "Fusionar sobres", onClick = onFusionSobres)
            BotonGrandeSecundario(texto = "Cambiar PIN", onClick = onPinSettings)
        }
    }
}
