package com.comtax.app.ui.ajustes

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.comtax.app.ui.common.BotonGrande
import com.comtax.app.ui.common.ScaffoldConVolver
import com.comtax.app.ui.theme.Dimens
import com.comtax.app.ui.theme.Gasto
import com.comtax.app.ui.theme.Ingreso

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PinSettingsScreen(
    onVolver: () -> Unit,
    viewModel: PinSettingsViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val opcionesTeclado = KeyboardOptions(keyboardType = KeyboardType.NumberPassword)

    ScaffoldConVolver(titulo = "Cambiar PIN", onVolver = onVolver) { padding ->
    Column(modifier = Modifier.fillMaxSize().padding(padding).padding(Dimens.espacioPantalla)) {
        OutlinedTextField(
            value = estado.pinActual,
            onValueChange = viewModel::onPinActualCambia,
            label = { Text("PIN actual") },
            keyboardOptions = opcionesTeclado,
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )
        Spacer(modifier = Modifier.padding(top = Dimens.espacioEstandar))
        OutlinedTextField(
            value = estado.pinNuevo,
            onValueChange = viewModel::onPinNuevoCambia,
            label = { Text("PIN nuevo") },
            keyboardOptions = opcionesTeclado,
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )
        Spacer(modifier = Modifier.padding(top = Dimens.espacioEstandar))
        OutlinedTextField(
            value = estado.pinNuevoConfirmacion,
            onValueChange = viewModel::onPinNuevoConfirmacionCambia,
            label = { Text("Repite el PIN nuevo") },
            keyboardOptions = opcionesTeclado,
            visualTransformation = PasswordVisualTransformation(),
            singleLine = true
        )

        if (estado.error != null) {
            Spacer(modifier = Modifier.padding(top = Dimens.espacioEstandar))
            Text(text = estado.error!!, color = Gasto, style = MaterialTheme.typography.bodyLarge)
        }
        if (estado.exito) {
            Spacer(modifier = Modifier.padding(top = Dimens.espacioEstandar))
            Text(text = "PIN actualizado correctamente", color = Ingreso, style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.padding(top = Dimens.espacioExtra))
        BotonGrande(texto = "Guardar nuevo PIN", onClick = viewModel::cambiarPin)
    }
    }
}
