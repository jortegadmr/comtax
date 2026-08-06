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
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.comtax.app.ui.common.AvisoNegativoCard
import com.comtax.app.ui.common.BotonGrande
import com.comtax.app.ui.common.BotonGrandeSecundario
import com.comtax.app.ui.common.CampoImporte
import com.comtax.app.ui.common.ScaffoldConVolver
import com.comtax.app.ui.common.TarjetaSobre
import com.comtax.app.ui.theme.Aviso
import com.comtax.app.ui.theme.BlancoSobrePrimario
import com.comtax.app.ui.theme.Dimens
import com.comtax.app.ui.theme.Gasto
import com.comtax.app.util.centimosATexto
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

    ScaffoldConVolver(titulo = "Detalle del movimiento", onVolver = onVolver) { padding ->
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .verticalScroll(rememberScrollState())
            .padding(Dimens.espacioPantalla)
    ) {
        if (movimiento.pendienteCuadre) {
            Text(text = "Pendiente de cuadre", color = Aviso, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.padding(top = Dimens.espacioPequeno))
            BotonGrandeSecundario(texto = "Marcar como cuadrado", onClick = viewModel::marcarCuadrado)
            Spacer(modifier = Modifier.padding(top = Dimens.espacioEstandar))
        }

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
        TextButton(onClick = { mostrarDatePicker = true }) {
            Text(text = estado.fecha.format(formatoFecha), style = MaterialTheme.typography.titleMedium)
        }

        if (estado.sobresActivos.isNotEmpty()) {
            Spacer(modifier = Modifier.padding(top = Dimens.espacioPequeno))
            Text(text = "Sobre", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.padding(top = Dimens.espacioPequeno))
            LazyRow(horizontalArrangement = Arrangement.spacedBy(Dimens.espacioPequeno)) {
                item {
                    TarjetaSobre(
                        nombre = "Sin sobre",
                        seleccionado = estado.sobreSeleccionado == null,
                        onClick = { viewModel.onSobreSeleccionado(null) },
                        forma = MaterialTheme.shapes.small
                    )
                }
                items(estado.sobresActivos) { sobre ->
                    TarjetaSobre(
                        nombre = sobre.nombre,
                        seleccionado = estado.sobreSeleccionado?.id == sobre.id,
                        onClick = { viewModel.onSobreSeleccionado(sobre) }
                    )
                }
            }
        }

        if (estado.errorFormulario != null) {
            Spacer(modifier = Modifier.padding(top = Dimens.espacioEstandar))
            Text(text = estado.errorFormulario!!, color = Gasto, style = MaterialTheme.typography.bodyLarge)
        }

        val aviso = estado.avisoNegativo
        if (aviso != null) {
            Spacer(modifier = Modifier.padding(top = Dimens.espacioEstandar))
            val mensaje = buildString {
                if (aviso.sobreQuedaNegativo && aviso.saldoResultanteSobreCentimos != null) {
                    append("Este cambio dejará el sobre en ${centimosATexto(aviso.saldoResultanteSobreCentimos)} €. ")
                }
                if (aviso.cuentaQuedaNegativa) {
                    append("Este cambio dejará la cuenta en ${centimosATexto(aviso.saldoResultanteCuentaCentimos)} €.")
                }
            }
            AvisoNegativoCard(mensaje = mensaje.trim())
        }

        Spacer(modifier = Modifier.padding(top = Dimens.espacioExtra))
        BotonGrande(
            texto = if (aviso != null) "Guardar de todas formas" else "Guardar cambios",
            onClick = viewModel::onGuardarClick,
            colors = if (aviso != null) {
                ButtonDefaults.buttonColors(containerColor = Aviso, contentColor = BlancoSobrePrimario)
            } else {
                ButtonDefaults.buttonColors()
            }
        )
        Spacer(modifier = Modifier.padding(top = Dimens.espacioMedio))
        BotonGrandeSecundario(
            texto = "Anular movimiento",
            onClick = viewModel::anular,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Gasto)
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
