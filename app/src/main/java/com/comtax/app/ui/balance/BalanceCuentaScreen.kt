package com.comtax.app.ui.balance

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.comtax.app.data.local.entity.EstadoSobre
import com.comtax.app.ui.common.BotonGrandeSecundario
import com.comtax.app.ui.common.EstadoVacio
import com.comtax.app.ui.common.FilaMovimiento
import com.comtax.app.ui.common.ScaffoldConVolver
import com.comtax.app.ui.common.TarjetaSaldoHero
import com.comtax.app.ui.common.TarjetaSobre
import com.comtax.app.ui.theme.BlancoSobrePrimario
import com.comtax.app.ui.theme.Dimens
import com.comtax.app.ui.theme.SuperficieAlt
import com.comtax.app.ui.theme.TextoSecundario
import com.comtax.app.ui.theme.colorDesdeHex
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BalanceCuentaScreen(
    onVolver: () -> Unit,
    onMovimientoClick: (Long) -> Unit,
    viewModel: BalanceCuentaViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    val formatoFecha = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }
    var mostrarDatePickerDesde by remember { mutableStateOf(false) }
    var mostrarDatePickerHasta by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.eventos.collect { evento ->
            if (evento is BalanceCuentaEvento.CompartirCsv) {
                val intent = Intent(Intent.ACTION_SEND).apply {
                    type = "text/csv"
                    putExtra(Intent.EXTRA_STREAM, evento.uri)
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                }
                context.startActivity(Intent.createChooser(intent, "Compartir CSV"))
            }
        }
    }

    val colorCuenta = estado.cuenta?.let { colorDesdeHex(it.colorHex) }
    val coloresTopBar = if (colorCuenta != null) {
        TopAppBarDefaults.topAppBarColors(containerColor = colorCuenta, titleContentColor = BlancoSobrePrimario)
    } else {
        TopAppBarDefaults.topAppBarColors()
    }

    ScaffoldConVolver(
        titulo = estado.cuenta?.nombre ?: "",
        onVolver = onVolver,
        colors = coloresTopBar
    ) { padding ->
        Column(modifier = Modifier.fillMaxWidth().padding(padding).padding(Dimens.espacioPantalla)) {

            TarjetaSaldoHero(etiqueta = "Saldo actual", saldoCentimos = estado.saldoCuenta)
            Spacer(modifier = Modifier.padding(top = Dimens.espacioPantalla))

            if (estado.sobres.isNotEmpty()) {
                Text(text = "Sobres", style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.padding(top = Dimens.espacioPequeno))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(Dimens.espacioPequeno)) {
                    items(estado.sobres) { sobre ->
                        val sufijo = if (sobre.estado == EstadoSobre.ARCHIVADO) " (archivado)" else ""
                        TarjetaSobre(
                            nombre = "${sobre.nombre}$sufijo",
                            seleccionado = estado.sobreSeleccionadoId == sobre.id,
                            onClick = { viewModel.seleccionarSobre(sobre.id) }
                        )
                    }
                }
                Spacer(modifier = Modifier.padding(top = Dimens.espacioPantalla))
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = SuperficieAlt)
            ) {
                Column(modifier = Modifier.fillMaxWidth().padding(Dimens.espacioEstandar)) {
                    Text(text = "Rango de fechas", style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
                    Spacer(modifier = Modifier.padding(top = Dimens.espacioMinimo))
                    Row(horizontalArrangement = Arrangement.spacedBy(Dimens.espacioEstandar)) {
                        Column {
                            Text(text = "Desde", style = MaterialTheme.typography.bodyMedium)
                            TextButton(onClick = { mostrarDatePickerDesde = true }) {
                                Text(estado.rangoFechas.desde?.format(formatoFecha) ?: "Sin límite")
                            }
                        }
                        Column {
                            Text(text = "Hasta", style = MaterialTheme.typography.bodyMedium)
                            TextButton(onClick = { mostrarDatePickerHasta = true }) {
                                Text(estado.rangoFechas.hasta?.format(formatoFecha) ?: "Sin límite")
                            }
                        }
                    }
                    if (estado.rangoFechas.desde != null || estado.rangoFechas.hasta != null) {
                        TextButton(onClick = { viewModel.limpiarFiltroFechas() }) { Text("Quitar filtro de fechas") }
                    }
                }
            }

            Spacer(modifier = Modifier.padding(top = Dimens.espacioEstandar))

            Text(text = "Historial", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.padding(top = Dimens.espacioPequeno))

            if (estado.movimientos.isEmpty()) {
                EstadoVacio(mensaje = "No hay movimientos con estos filtros.")
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(Dimens.espacioPequeno)) {
                    items(estado.movimientos) { mov ->
                        FilaMovimiento(
                            fechaTexto = mov.fecha.format(formatoFecha),
                            concepto = mov.concepto,
                            importeCentimos = mov.importeCentimos,
                            tipo = mov.tipo,
                            sobreNombre = mov.sobreNombre,
                            pendienteCuadre = mov.pendienteCuadre,
                            onClick = { onMovimientoClick(mov.id) }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.padding(top = Dimens.espacioPantalla))
            BotonGrandeSecundario(texto = "Exportar CSV", onClick = viewModel::exportarCsv)
        }
    }

    if (mostrarDatePickerDesde) {
        val estadoPicker = rememberDatePickerState(
            initialSelectedDateMillis = estado.rangoFechas.desde
                ?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { mostrarDatePickerDesde = false },
            confirmButton = {
                TextButton(onClick = {
                    val fecha = estadoPicker.selectedDateMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
                    }
                    viewModel.filtrarPorFechas(fecha, estado.rangoFechas.hasta)
                    mostrarDatePickerDesde = false
                }) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { mostrarDatePickerDesde = false }) { Text("Cancelar") } }
        ) { DatePicker(state = estadoPicker) }
    }

    if (mostrarDatePickerHasta) {
        val estadoPicker = rememberDatePickerState(
            initialSelectedDateMillis = estado.rangoFechas.hasta
                ?.atStartOfDay(ZoneOffset.UTC)?.toInstant()?.toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { mostrarDatePickerHasta = false },
            confirmButton = {
                TextButton(onClick = {
                    val fecha = estadoPicker.selectedDateMillis?.let {
                        Instant.ofEpochMilli(it).atZone(ZoneOffset.UTC).toLocalDate()
                    }
                    viewModel.filtrarPorFechas(estado.rangoFechas.desde, fecha)
                    mostrarDatePickerHasta = false
                }) { Text("Aceptar") }
            },
            dismissButton = { TextButton(onClick = { mostrarDatePickerHasta = false }) { Text("Cancelar") } }
        ) { DatePicker(state = estadoPicker) }
    }
}
