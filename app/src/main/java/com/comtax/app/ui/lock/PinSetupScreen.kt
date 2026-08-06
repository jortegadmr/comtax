package com.comtax.app.ui.lock

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.comtax.app.ui.common.BotonGrande
import com.comtax.app.ui.theme.Dimens
import com.comtax.app.ui.theme.Gasto

@Composable
fun PinSetupScreen(
    onPinConfigurado: () -> Unit,
    viewModel: PinSetupViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.eventos.collect { evento ->
            if (evento is PinSetupEvento.PinConfigurado) onPinConfigurado()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(Dimens.espacioPantalla),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (!estado.pidiendoConfirmacion) {
            Text(text = "Crea un PIN de acceso", style = MaterialTheme.typography.headlineMedium)
            Text(
                text = "Se te pedirá cada vez que abras la app",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = Dimens.espacioPequeno)
            )
            Spacer(modifier = Modifier.padding(top = Dimens.espacioExtra))
            OutlinedTextField(
                value = estado.pin,
                onValueChange = viewModel::onPinCambia,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                visualTransformation = PasswordVisualTransformation(),
                textStyle = MaterialTheme.typography.headlineMedium.copy(textAlign = TextAlign.Center),
                singleLine = true,
                isError = estado.error != null
            )
            if (estado.error != null) {
                Spacer(modifier = Modifier.padding(top = Dimens.espacioMedio))
                Text(text = estado.error!!, color = Gasto, style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.padding(top = Dimens.espacioExtra))
            BotonGrande(
                texto = "Continuar",
                onClick = viewModel::continuarAConfirmacion,
                habilitado = estado.pin.length >= 4,
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Text(text = "Repite el PIN", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.padding(top = Dimens.espacioExtra))
            OutlinedTextField(
                value = estado.confirmacion,
                onValueChange = viewModel::onConfirmacionCambia,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                visualTransformation = PasswordVisualTransformation(),
                textStyle = MaterialTheme.typography.headlineMedium.copy(textAlign = TextAlign.Center),
                singleLine = true,
                isError = estado.error != null
            )
            if (estado.error != null) {
                Spacer(modifier = Modifier.padding(top = Dimens.espacioMedio))
                Text(text = estado.error!!, color = Gasto, style = MaterialTheme.typography.bodyLarge)
            }
            Spacer(modifier = Modifier.padding(top = Dimens.espacioExtra))
            BotonGrande(
                texto = "Confirmar PIN",
                onClick = viewModel::confirmarPin,
                habilitado = estado.confirmacion.length >= 4,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
