package com.comtax.app.ui.balance

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
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.comtax.app.ui.common.BotonGrande
import com.comtax.app.ui.common.BotonGrandeSecundario
import com.comtax.app.ui.common.CampoImporte
import com.comtax.app.ui.theme.Dimens
import com.comtax.app.ui.theme.RojoGasto
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovimientoDetalleScreen(
    onVolver: () -> Unit,
    viewModel: MovimientoDetalleViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    var mostrarDatePicker by remember { mutableStateOf(false) }
    val formatoFecha = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }

    LaunchedEffect(Unit) {
        viewModel.eventos.collect { evento ->
            if (evento is MovimientoDetalleEvento.Guardado || evento is MovimientoDetalleEvento.Anulado) {
                onVolver()
            }
        }
    }

    if (estado.cargando || estado.movimiento == null) {
        return
    }
    val movimiento = estado.movimiento!!

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(Dimens.espacioPantalla)
    ) {
        Text(text = "Detalle del movimiento", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.padding(top = 20.dp))

        if (movimiento.pendienteCuadre) {
            Text(text = "Pendiente de cuadre", color = RojoGasto, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.padding(top = 8.dp))
            BotonGrandeSecundario(texto = "Marcar como cuadrado", onClick = viewModel::marcarCuadrado)
            Spacer(modifier = Modifier.padding(top = 16.dp))
        }

        CampoImporte(valor = estado.importeTexto, onValorCambia = viewModel::onImporteCambia)
        Spacer(modifier = Modifier.padding(top = 16.dp))

        OutlinedTextField(
            value = estado.concepto,
            onValueChange = viewModel::onConceptoCambia,
            label = { Text("Concepto", style = MaterialTheme.typography.bodyLarge) },
            textStyle = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.padding(top = 16.dp))

        Text(text = "Fecha", style = MaterialTheme.typography.bodyLarge)
        TextButton(onClick = { mostrarDatePicker = true }) {
            Text(text = estado.fecha.format(formatoFecha), style = MaterialTheme.typography.titleMedium)
        }

        if (estado.sobresActivos.isNotEmpty()) {
            Spacer(modifier = Modifier.padding(top = 8.dp))
            Text(text = "Sobre", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.padding(top = 8.dp))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                item {
                    FilterChip(
                        selected = estado.sobreSeleccionado == null,
                        onClick = { viewModel.onSobreSeleccionado(null) },
                        label = { Text("Sin sobre") }
                    )
                }
                items(estado.sobresActivos) { sobre ->
                    FilterChip(
                        selected = estado.sobreSeleccionado?.id == sobre.id,
                        onClick = { viewModel.onSobreSeleccionado(sobre) },
                        label = { Text(sobre.nombre) }
                    )
                }
            }
        }

        if (estado.errorFormulario != null) {
            Spacer(modifier = Modifier.padding(top = 16.dp))
            Text(text = estado.errorFormulario!!, color = RojoGasto, style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.padding(top = 32.dp))
        BotonGrande(texto = "Guardar cambios", onClick = viewModel::guardarEdicion)
        Spacer(modifier = Modifier.padding(top = 12.dp))
        BotonGrandeSecundario(texto = "Anular movimiento", onClick = viewModel::anular)
    }

    if (mostrarDatePicker) {
        val estadoPicker = rememberDatePickerState(
            initialSelectedDateMillis = estado.fecha.atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        )
        DatePickerDialog(
            onDismissRequest = { mostrarDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    estadoPicker.selectedDateMillis?.let { millis ->
                        val fecha = Instant.ofEpochMilli(millis).atZone(ZoneOffset.UTC).toLocalDate()
                        viewModel.onFechaCambia(fecha)
                    }
                    mostrarDatePicker = false
                }) { Text("Aceptar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDatePicker = false }) { Text("Cancelar") }
            }
        ) {
            DatePicker(state = estadoPicker)
        }
    }
}
