package com.comtax.app.ui.lock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.comtax.app.ui.common.BotonGrande
import com.comtax.app.ui.theme.Dimens
import com.comtax.app.ui.theme.RojoGasto

@Composable
fun CrearPrimeraCuentaScreen(
    onCuentaCreada: () -> Unit,
    viewModel: CrearPrimeraCuentaViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.eventos.collect { evento ->
            if (evento is CrearPrimeraCuentaEvento.CuentaCreada) onCuentaCreada()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.espacioPantalla),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Crea tu primera cuenta", style = MaterialTheme.typography.headlineMedium)
        Text(
            text = "Por ejemplo: Guadiamar o El Laurel",
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.padding(top = 8.dp)
        )
        Spacer(modifier = Modifier.padding(top = 32.dp))
        OutlinedTextField(
            value = estado.nombre,
            onValueChange = viewModel::onNombreCambia,
            label = { Text("Nombre de la cuenta") },
            modifier = Modifier.fillMaxWidth()
        )
        if (estado.error != null) {
            Spacer(modifier = Modifier.padding(top = 12.dp))
            Text(text = estado.error!!, color = RojoGasto, style = MaterialTheme.typography.bodyLarge)
        }
        Spacer(modifier = Modifier.padding(top = 32.dp))
        BotonGrande(texto = "Crear cuenta", onClick = viewModel::crear)
    }
}
