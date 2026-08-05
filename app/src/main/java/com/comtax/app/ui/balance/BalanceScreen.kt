package com.comtax.app.ui.balance

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.comtax.app.data.local.entity.TipoMovimiento
import com.comtax.app.ui.common.BotonGrande
import com.comtax.app.ui.theme.Dimens
import com.comtax.app.ui.theme.RojoGasto
import com.comtax.app.ui.theme.VerdeIngreso
import com.comtax.app.util.centimosATexto
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BalanceScreen(
    onVolver: () -> Unit,
    onMovimientoClick: (Long) -> Unit,
    viewModel: BalanceViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val formatoFecha = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }

    LaunchedEffect(Unit) {
        viewModel.eventos.collect { evento ->
            if (evento is BalanceEvento.CompartirCsv) {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/csv"
                    putExtra(Intent.EXTRA_STREAM, evento.uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(intent, "Compartir CSV"))
            }
        }
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Balance y registro", style = MaterialTheme.typography.titleLarge) }) }
    ) { padding ->
        Column(modifier = Modifier.fillMaxWidth().padding(padding).padding(Dimens.espacioPantalla)) {

            val saldoTotal = estado.saldoPorCuenta.values.sum()
            Card(colors = CardDefaults.cardColors()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(
                        text = "Saldo total: ${centimosATexto(saldoTotal)} €",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (saldoTotal < 0) RojoGasto else VerdeIngreso
                    )
                    estado.cuentas.forEach { cuenta ->
                        val saldo = estado.saldoPorCuenta[cuenta.id] ?: 0L
                        Text(
                            text = "${cuenta.nombre}: ${centimosATexto(saldo)} €",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (saldo < 0) RojoGasto else MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.padding(top = 12.dp))

            if (estado.pendientesCuadre > 0) {
                Card(colors = CardDefaults.cardColors()) {
                    Text(
                        text = "${estado.pendientesCuadre} movimiento(s) pendiente(s) de cuadre",
                        style = MaterialTheme.typography.bodyLarge,
                        color = RojoGasto,
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.padding(top = 12.dp))
            }

            SelectorCuenta(
                cuentas = estado.cuentas.map { it.id to it.nombre },
                seleccionId = estado.filtros.cuentaId,
                onSeleccion = viewModel::filtrarPorCuenta
            )
            Spacer(modifier = Modifier.padding(top = 8.dp))
            SelectorSobre(
                sobres = estado.sobres
                    .filter { estado.filtros.cuentaId == null || it.cuentaId == estado.filtros.cuentaId }
                    .map { it.id to it.nombre },
                seleccionId = estado.filtros.sobreId,
                onSeleccion = viewModel::filtrarPorSobre
            )
            Spacer(modifier = Modifier.padding(top = 8.dp))
            TextButton(onClick = viewModel::limpiarFiltros) { Text("Limpiar filtros") }

            Spacer(modifier = Modifier.padding(top = 8.dp))
            BotonGrande(texto = "Exportar CSV", onClick = viewModel::exportarCsv)

            Spacer(modifier = Modifier.padding(top = 16.dp))
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(estado.movimientos) { mov ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors()
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp)
                        ) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = mov.fecha.format(formatoFecha), style = MaterialTheme.typography.bodyMedium)
                                val color = if (mov.tipo == TipoMovimiento.INGRESO) VerdeIngreso else RojoGasto
                                val signo = if (mov.tipo == TipoMovimiento.INGRESO) "+" else "-"
                                Text(
                                    text = "$signo${centimosATexto(mov.importeCentimos)} €",
                                    color = color,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            }
                            Text(text = mov.concepto, style = MaterialTheme.typography.bodyLarge)
                            Text(
                                text = listOfNotNull(mov.cuentaNombre, mov.sobreNombre).joinToString(" · "),
                                style = MaterialTheme.typography.bodyMedium
                            )
                            if (mov.pendienteCuadre) {
                                Text(
                                    text = "Pendiente de cuadre",
                                    color = RojoGasto,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            TextButton(onClick = { onMovimientoClick(mov.id) }) {
                                Text("Ver detalle")
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectorCuenta(
    cuentas: List<Pair<Long, String>>,
    seleccionId: Long?,
    onSeleccion: (Long?) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }
    val texto = cuentas.firstOrNull { it.first == seleccionId }?.second ?: "Todas las cuentas"
    ExposedDropdownMenuBox(expanded = expandido, onExpandedChange = { expandido = it }) {
        OutlinedTextField(
            value = texto,
            onValueChange = {},
            readOnly = true,
            label = { Text("Cuenta") },
            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) }
        )
        androidx.compose.material3.DropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false },
            modifier = Modifier.exposedDropdownSize()
        ) {
            DropdownMenuItem(text = { Text("Todas las cuentas") }, onClick = { onSeleccion(null); expandido = false })
            cuentas.forEach { (id, nombre) ->
                DropdownMenuItem(text = { Text(nombre) }, onClick = { onSeleccion(id); expandido = false })
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SelectorSobre(
    sobres: List<Pair<Long, String>>,
    seleccionId: Long?,
    onSeleccion: (Long?) -> Unit
) {
    var expandido by remember { mutableStateOf(false) }
    val texto = sobres.firstOrNull { it.first == seleccionId }?.second ?: "Todos los sobres"
    ExposedDropdownMenuBox(expanded = expandido, onExpandedChange = { expandido = it }) {
        OutlinedTextField(
            value = texto,
            onValueChange = {},
            readOnly = true,
            label = { Text("Sobre") },
            modifier = Modifier.fillMaxWidth().menuAnchor(MenuAnchorType.PrimaryNotEditable, enabled = true),
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandido) }
        )
        androidx.compose.material3.DropdownMenu(
            expanded = expandido,
            onDismissRequest = { expandido = false },
            modifier = Modifier.exposedDropdownSize()
        ) {
            DropdownMenuItem(text = { Text("Todos los sobres") }, onClick = { onSeleccion(null); expandido = false })
            sobres.forEach { (id, nombre) ->
                DropdownMenuItem(text = { Text(nombre) }, onClick = { onSeleccion(id); expandido = false })
            }
        }
    }
}
