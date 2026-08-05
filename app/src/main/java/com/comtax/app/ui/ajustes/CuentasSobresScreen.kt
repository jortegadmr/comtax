package com.comtax.app.ui.ajustes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.comtax.app.data.local.entity.CuentaEntity
import com.comtax.app.data.local.entity.EstadoSobre
import com.comtax.app.data.local.entity.SobreEntity
import com.comtax.app.ui.common.BotonGrande
import com.comtax.app.ui.common.PuntoColor
import com.comtax.app.ui.common.ScaffoldConVolver
import com.comtax.app.ui.common.SelectorColorCuenta
import com.comtax.app.ui.theme.Dimens
import androidx.compose.material3.ExperimentalMaterial3Api

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CuentasSobresScreen(
    onVolver: () -> Unit,
    viewModel: CuentasSobresViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    var cuentaARenombrar by remember { mutableStateOf<CuentaEntity?>(null) }
    var cuentaAColorear by remember { mutableStateOf<CuentaEntity?>(null) }
    var sobreARenombrar by remember { mutableStateOf<SobreEntity?>(null) }

    ScaffoldConVolver(titulo = "Cuentas y sobres", onVolver = onVolver) { padding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(Dimens.espacioPantalla)
    ) {
        estado.cuentas.forEach { cuenta ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .clickable { viewModel.seleccionarCuenta(cuenta.id) },
                colors = CardDefaults.cardColors()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
                        PuntoColor(cuenta.colorHex)
                        Spacer(modifier = Modifier.padding(start = 8.dp))
                        Text(
                            text = cuenta.nombre + if (cuenta.archivada) " (archivada)" else "",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                    Row {
                        IconButton(onClick = { cuentaAColorear = cuenta }) {
                            Icon(Icons.Filled.Palette, contentDescription = "Cambiar color")
                        }
                        IconButton(onClick = { cuentaARenombrar = cuenta }) {
                            Icon(Icons.Filled.Edit, contentDescription = "Renombrar")
                        }
                        TextButton(onClick = {
                            if (cuenta.archivada) viewModel.desarchivarCuenta(cuenta.id)
                            else viewModel.archivarCuenta(cuenta.id)
                        }) {
                            Text(if (cuenta.archivada) "Desarchivar" else "Archivar")
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.padding(top = 12.dp))
        OutlinedTextField(
            value = estado.nombreNuevaCuenta,
            onValueChange = viewModel::onNombreNuevaCuentaCambia,
            label = { Text("Nombre de la nueva cuenta") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.padding(top = 12.dp))
        SelectorColorCuenta(
            colorSeleccionado = estado.colorNuevaCuenta,
            onColorSeleccionado = viewModel::onColorNuevaCuentaCambia
        )
        Spacer(modifier = Modifier.padding(top = 12.dp))
        BotonGrande(texto = "Crear cuenta", onClick = viewModel::crearCuenta)

        val cuentaSeleccionada = estado.cuentas.firstOrNull { it.id == estado.cuentaSeleccionadaId }
        if (cuentaSeleccionada != null) {
            Spacer(modifier = Modifier.padding(top = 32.dp))
            Text(text = "Sobres de ${cuentaSeleccionada.nombre}", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.padding(top = 12.dp))

            estado.sobresDeCuenta.forEach { sobre ->
                Card(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), colors = CardDefaults.cardColors()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = sobre.nombre + if (sobre.estado == EstadoSobre.ARCHIVADO) " (archivado)" else "",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Row {
                            IconButton(onClick = { sobreARenombrar = sobre }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Renombrar")
                            }
                            TextButton(onClick = { viewModel.cambiarEstadoSobre(sobre) }) {
                                Text(if (sobre.estado == EstadoSobre.ARCHIVADO) "Desarchivar" else "Archivar")
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.padding(top = 8.dp))
            OutlinedTextField(
                value = estado.nombreNuevoSobre,
                onValueChange = viewModel::onNombreNuevoSobreCambia,
                label = { Text("Nombre del nuevo sobre") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.padding(top = 8.dp))
            BotonGrande(texto = "Crear sobre", onClick = viewModel::crearSobre)
        }
    }
    }

    cuentaARenombrar?.let { cuenta ->
        DialogoRenombrar(
            valorInicial = cuenta.nombre,
            onConfirmar = { nuevoNombre ->
                viewModel.renombrarCuenta(cuenta.id, nuevoNombre)
                cuentaARenombrar = null
            },
            onCancelar = { cuentaARenombrar = null }
        )
    }

    sobreARenombrar?.let { sobre ->
        DialogoRenombrar(
            valorInicial = sobre.nombre,
            onConfirmar = { nuevoNombre ->
                viewModel.renombrarSobre(sobre.id, nuevoNombre)
                sobreARenombrar = null
            },
            onCancelar = { sobreARenombrar = null }
        )
    }

    cuentaAColorear?.let { cuenta ->
        AlertDialog(
            onDismissRequest = { cuentaAColorear = null },
            title = { Text("Color de ${cuenta.nombre}") },
            text = {
                SelectorColorCuenta(
                    colorSeleccionado = cuenta.colorHex,
                    onColorSeleccionado = { hex ->
                        viewModel.cambiarColorCuenta(cuenta.id, hex)
                        cuentaAColorear = null
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = { cuentaAColorear = null }) { Text("Cerrar") }
            }
        )
    }
}

@Composable
private fun DialogoRenombrar(
    valorInicial: String,
    onConfirmar: (String) -> Unit,
    onCancelar: () -> Unit
) {
    var texto by remember { mutableStateOf(valorInicial) }
    AlertDialog(
        onDismissRequest = onCancelar,
        title = { Text("Renombrar") },
        text = {
            OutlinedTextField(value = texto, onValueChange = { texto = it }, singleLine = true)
        },
        confirmButton = {
            TextButton(onClick = { onConfirmar(texto) }) { Text("Guardar") }
        },
        dismissButton = {
            TextButton(onClick = onCancelar) { Text("Cancelar") }
        }
    )
}
