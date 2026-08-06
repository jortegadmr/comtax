package com.comtax.app.ui.altamovimiento

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.comtax.app.data.local.entity.TipoMovimiento
import com.comtax.app.ui.common.AvisoNegativoCard
import com.comtax.app.ui.common.BotonGrande
import com.comtax.app.ui.common.BotonGrandeSecundario
import com.comtax.app.ui.common.PuntoColor
import com.comtax.app.ui.theme.AmbarAviso
import com.comtax.app.ui.theme.BlancoSobrePrimario
import com.comtax.app.ui.theme.Dimens
import com.comtax.app.ui.theme.comoCifra
import com.comtax.app.util.centimosATexto
import com.comtax.app.util.textoACentimos
import java.time.format.DateTimeFormatter

@Composable
fun ConfirmacionMovimientoScreen(
    viewModel: AltaMovimientoViewModel,
    onConfirmado: () -> Unit,
    onCancelar: () -> Unit
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.eventos.collect { evento ->
            if (evento is AltaMovimientoEvento.Guardado) onConfirmado()
        }
    }

    val importeCentimos = textoACentimos(estado.importeTexto) ?: 0
    val tituloTipo = if (estado.tipo == TipoMovimiento.INGRESO) "Ingreso" else "Gasto"
    val aviso = estado.avisoNegativo

    Column(modifier = Modifier.fillMaxSize().padding(Dimens.espacioPantalla)) {
        Text(text = "Vas a registrar", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.padding(top = Dimens.espacioPantalla))

        Card(colors = CardDefaults.cardColors()) {
            Column(modifier = Modifier.padding(Dimens.espacioPantalla)) {
                FilaResumen("Tipo", tituloTipo)
                FilaResumen(
                    etiqueta = "Importe",
                    valor = "${centimosATexto(importeCentimos)} €",
                    valorStyle = MaterialTheme.typography.bodyLarge.comoCifra()
                )
                FilaResumen("Cuenta", estado.cuentaSeleccionada?.nombre.orEmpty(), estado.cuentaSeleccionada?.colorHex)
                estado.sobreSeleccionado?.let { FilaResumen("Sobre", it.nombre) }
                FilaResumen("Fecha", estado.fecha.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")))
                FilaResumen("Concepto", estado.concepto)
            }
        }

        if (aviso != null) {
            Spacer(modifier = Modifier.padding(top = Dimens.espacioPantalla))
            val mensaje = buildString {
                if (aviso.sobreQuedaNegativo && aviso.saldoResultanteSobreCentimos != null) {
                    append("Este gasto dejará el sobre en ${centimosATexto(aviso.saldoResultanteSobreCentimos)} €. ")
                }
                if (aviso.cuentaQuedaNegativa) {
                    append("Este gasto dejará la cuenta en ${centimosATexto(aviso.saldoResultanteCuentaCentimos)} €.")
                }
            }
            AvisoNegativoCard(mensaje = mensaje.trim())
        }

        Spacer(modifier = Modifier.weight(1f))

        BotonGrande(
            texto = if (aviso != null) "Continuar de todas formas" else "Confirmar",
            onClick = viewModel::confirmar,
            habilitado = !estado.guardando,
            colors = if (aviso != null) {
                ButtonDefaults.buttonColors(containerColor = AmbarAviso, contentColor = BlancoSobrePrimario)
            } else {
                ButtonDefaults.buttonColors()
            }
        )
        Spacer(modifier = Modifier.padding(top = Dimens.espacioMedio))
        BotonGrandeSecundario(
            texto = "Cancelar",
            onClick = onCancelar,
            habilitado = !estado.guardando
        )
    }
}

@Composable
private fun FilaResumen(
    etiqueta: String,
    valor: String,
    colorHex: String? = null,
    valorStyle: TextStyle = MaterialTheme.typography.bodyLarge
) {
    Row(
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = Dimens.espacioMinimo)
    ) {
        colorHex?.let {
            PuntoColor(it)
            Spacer(modifier = Modifier.padding(start = Dimens.espacioPequeno))
        }
        Text(text = "$etiqueta: ", style = MaterialTheme.typography.bodyLarge)
        Text(text = valor, style = valorStyle)
    }
}
