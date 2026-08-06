package com.comtax.app.ui.balance

import android.content.Intent
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.comtax.app.ui.common.BotonGrandeSecundario
import com.comtax.app.ui.common.EstadoVacio
import com.comtax.app.ui.common.PuntoColor
import com.comtax.app.ui.common.ScaffoldConVolver
import com.comtax.app.ui.theme.Dimens
import com.comtax.app.ui.theme.RojoGasto
import com.comtax.app.ui.theme.VerdeIngreso
import com.comtax.app.ui.theme.comoCifra
import com.comtax.app.util.centimosATexto

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BalanceScreen(
    onVolver: () -> Unit,
    onCuentaClick: (Long) -> Unit,
    viewModel: BalanceViewModel = hiltViewModel()
) {
    val estado by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

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

    ScaffoldConVolver(titulo = "Balance y registro", onVolver = onVolver) { padding ->
        if (estado.cuentas.isEmpty()) {
            EstadoVacio(
                mensaje = "No hay cuentas creadas todavía.\nCréalas desde Ajustes.",
                modifier = Modifier.padding(padding)
            )
            return@ScaffoldConVolver
        }

        Column(modifier = Modifier.fillMaxWidth().padding(padding).padding(Dimens.espacioPantalla)) {

            if (estado.pendientesCuadre > 0) {
                Card(colors = CardDefaults.cardColors()) {
                    Text(
                        text = "${estado.pendientesCuadre} movimiento(s) pendiente(s) de cuadre",
                        style = MaterialTheme.typography.bodyLarge,
                        color = RojoGasto,
                        modifier = Modifier.padding(Dimens.espacioMedio)
                    )
                }
                Spacer(modifier = Modifier.padding(top = Dimens.espacioMedio))
            }

            Text(text = "Elige una cuenta", style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.padding(top = Dimens.espacioMedio))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(Dimens.espacioPequeno)) {
                items(estado.cuentas) { cuenta ->
                    val saldo = estado.saldoPorCuenta[cuenta.id] ?: 0L
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onCuentaClick(cuenta.id) },
                        colors = CardDefaults.cardColors()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Dimens.espacioPantalla),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                            ) {
                                PuntoColor(cuenta.colorHex)
                                Spacer(modifier = Modifier.padding(start = Dimens.espacioMedio))
                                Text(
                                    text = cuenta.nombre + if (cuenta.archivada) " (archivada)" else "",
                                    style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Spacer(modifier = Modifier.padding(start = Dimens.espacioPequeno))
                            Text(
                                text = "${centimosATexto(saldo)} €",
                                style = MaterialTheme.typography.titleMedium.comoCifra(),
                                color = if (saldo < 0) RojoGasto else VerdeIngreso
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.padding(top = Dimens.espacioPantalla))
            BotonGrandeSecundario(texto = "Exportar todo (CSV)", onClick = viewModel::exportarTodoCsv)
        }
    }
}
