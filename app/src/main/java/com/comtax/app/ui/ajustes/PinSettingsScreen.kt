package com.comtax.app.ui.ajustes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.comtax.app.ui.common.BotonGrande
import com.comtax.app.ui.theme.Dimens
import com.comtax.app.ui.theme.RojoGasto
import com.comtax.app.ui.theme.VerdeIngreso

@Composable
fun PinSettingsScreen(
    viewModel: PinSettingsViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val opcionesTeclado = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)

    Column(modifier = Modifier.fillMaxSize().padding(Dimens.espacioPantalla)) {
        Text(text = "Cambiar PIN", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.padding(top = 24.dp))

        OutlinedTextField(
            value = estado.pinActual,
            onValueChange = viewModel::onPinActualCambia,
            label = { Text("PIN actual") },
            keyboardOptions = opcionesTeclado,
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )
        Spacer(modifier = Modifier.padding(top = 16.dp))
        OutlinedTextField(
            value = estado.pinNuevo,
            onValueChange = viewModel::onPinNuevoCambia,
            label = { Text("PIN nuevo") },
            keyboardOptions = opcionesTeclado,
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )
        Spacer(modifier = Modifier.padding(top = 16.dp))
        OutlinedTextField(
            value = estado.pinNuevoConfirmacion,
            onValueChange = viewModel::onPinNuevoConfirmacionCambia,
            label = { Text("Repite el PIN nuevo") },
            keyboardOptions = opcionesTeclado,
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        if (estado.error != null) {
            Spacer(modifier = Modifier.padding(top = 16.dp))
            Text(text = estado.error!!, color = RojoGasto, style = MaterialTheme.typography.bodyLarge)
        }
        if (estado.exito) {
            Spacer(modifier = Modifier.padding(top = 16.dp))
            Text(text = "PIN actualizado correctamente", color = VerdeIngreso, style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.padding(top = 32.dp))
        BotonGrande(texto = "Guardar nuevo PIN", onClick = viewModel::cambiarPin)
    }
}
