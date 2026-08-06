package com.comtax.app.ui.altamovimiento

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.comtax.app.ui.common.BotonGrande
import com.comtax.app.ui.common.CampoImporte
import com.comtax.app.ui.common.ScaffoldConVolver
import com.comtax.app.ui.theme.Dimens
import com.comtax.app.ui.theme.RojoGasto
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FormularioMovimientoScreen(
    viewModel: AltaMovimientoViewModel,
    onVolver: () -> Unit,
    onContinuar: () -> Unit
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    var mostrarDatePicker by remember { mutableStateOf(false) }
    val formatoFecha = remember { DateTimeFormatter.ofPattern("dd/MM/yyyy") }

    ScaffoldConVolver(titulo = estado.cuentaSeleccionada?.nombre.orEmpty(), onVolver = onVolver) { padding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(Dimens.espacioPantalla)
    ) {
        CampoImporte(valor = estado.importeTexto, onValorCambia = viewModel::onImporteCambia)
        Spacer(modifier = Modifier.padding(top = Dimens.espacioEstandar))

        OutlinedTextField(
            value = estado.concepto,
            onValueChange = viewModel::onConceptoCambia,
            label = { Text("Concepto", style = MaterialTheme.typography.bodyLarge) },
            textStyle = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.padding(top = Dimens.espacioEstandar))

        Text(text = "Fecha", style = MaterialTheme.typography.bodyLarge)
        Spacer(modifier = Modifier.padding(top = Dimens.espacioMinimo))
        TextButton(onClick = { mostrarDatePicker = true }) {
            Text(text = estado.fecha.format(formatoFecha), style = MaterialTheme.typography.titleMedium)
        }

        if (estado.sobresActivos.isNotEmpty()) {
            Spacer(modifier = Modifier.padding(top = Dimens.espacioEstandar))
            Text(text = "Sobre (opcional)", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.padding(top = Dimens.espacioPequeno))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(Dimens.espacioPequeno)) {
                item {
                    FilterChip(
                        selected = estado.sobreSeleccionado == null,
                        onClick = { viewModel.onSobreSeleccionado(null) },
                        label = { Text("Sin sobre", style = MaterialTheme.typography.bodyLarge) },
                        modifier = Modifier.heightIn(min = 52.dp)
                    )
                }
                items(estado.sobresActivos) { sobre ->
                    FilterChip(
                        selected = estado.sobreSeleccionado?.id == sobre.id,
                        onClick = { viewModel.onSobreSeleccionado(sobre) },
                        label = { Text(sobre.nombre, style = MaterialTheme.typography.bodyLarge) },
                        modifier = Modifier.heightIn(min = 52.dp)
                    )
                }
            }
        }

        if (estado.errorFormulario != null) {
            Spacer(modifier = Modifier.padding(top = Dimens.espacioEstandar))
            Text(text = estado.errorFormulario!!, color = RojoGasto, style = MaterialTheme.typography.bodyLarge)
        }

        Spacer(modifier = Modifier.padding(top = Dimens.espacioExtra))
        BotonGrande(
            texto = "Continuar",
            onClick = {
                if (viewModel.validarFormulario()) {
                    scope.launch {
                        viewModel.prepararConfirmacion()
                        onContinuar()
                    }
                }
            }
        )
    }
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
