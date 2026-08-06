package com.comtax.app.ui.ajustes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.comtax.app.ui.common.BotonGrande
import com.comtax.app.ui.common.TarjetaSobre
import com.comtax.app.ui.common.ScaffoldConVolver
import com.comtax.app.ui.theme.Dimens
import com.comtax.app.ui.theme.Gasto
import com.comtax.app.ui.theme.Ingreso

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FusionSobresScreen(
    onVolver: () -> Unit,
    viewModel: FusionSobresViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    var expandidoCuenta by remember { mutableStateOf(false) }

    ScaffoldConVolver(titulo = "Fusionar sobres", onVolver = onVolver) { padding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(Dimens.espacioPantalla)
    ) {
        Text(
            text = "El sobre origen se archiva y todo su historial pasa al sobre destino.",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.padding(top = Dimens.espacioPantalla))

        val textoCuenta = estado.cuentas.firstOrNull { it.id == estado.cuentaSeleccionadaId }?.nombre
            ?: "Elige una cuenta"
        ExposedDropdownMenuBox(expanded = expandidoCuenta, onExpandedChange = { expandidoCuenta = it }) {
            OutlinedTextField(
                value = textoCuenta,
                onValueChange = {},
                readOnly = true,
                label = { Text("Cuenta") },
                modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandidoCuenta) }
            )
            androidx.compose.material3.DropdownMenu(
                expanded = expandidoCuenta,
                onDismissRequest = { expandidoCuenta = false },
                modifier = Modifier.exposedDropdownSize()
            ) {
                estado.cuentas.forEach { cuenta ->
                    DropdownMenuItem(
                        text = { Text(cuenta.nombre) },
                        onClick = { viewModel.seleccionarCuenta(cuenta.id); expandidoCuenta = false }
                    )
                }
            }
        }

        if (estado.sobresDeCuenta.size >= 2) {
            Spacer(modifier = Modifier.padding(top = Dimens.espacioGrande))
            Text(text = "Sobre origen (se archivará)", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.padding(top = Dimens.espacioPequeno))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(Dimens.espacioPequeno)) {
                items(estado.sobresDeCuenta) { sobre ->
                    TarjetaSobre(
                        nombre = sobre.nombre,
                        seleccionado = estado.origenId == sobre.id,
                        onClick = { viewModel.seleccionarOrigen(sobre.id) }
                    )
                }
            }

            Spacer(modifier = Modifier.padding(top = Dimens.espacioPantalla))
            Text(text = "Sobre destino (recibe el historial)", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.padding(top = Dimens.espacioPequeno))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(Dimens.espacioPequeno)) {
                items(estado.sobresDeCuenta) { sobre ->
                    TarjetaSobre(
                        nombre = sobre.nombre,
                        seleccionado = estado.destinoId == sobre.id,
                        onClick = { viewModel.seleccionarDestino(sobre.id) }
                    )
                }
            }

            Spacer(modifier = Modifier.padding(top = Dimens.espacioExtra))
            BotonGrande(texto = "Fusionar sobres", onClick = viewModel::fusionar)
        } else if (estado.cuentaSeleccionadaId != null) {
            Spacer(modifier = Modifier.padding(top = Dimens.espacioPantalla))
            Text(text = "Esta cuenta necesita al menos dos sobres activos para fusionar.")
        }

        if (estado.error != null) {
            Spacer(modifier = Modifier.padding(top = Dimens.espacioEstandar))
            Text(text = estado.error!!, color = Gasto, style = MaterialTheme.typography.bodyLarge)
        }
        if (estado.exito) {
            Spacer(modifier = Modifier.padding(top = Dimens.espacioEstandar))
            Text(text = "Sobres fusionados correctamente", color = Ingreso, style = MaterialTheme.typography.bodyLarge)
        }
    }
    }
}
