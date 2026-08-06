package com.comtax.app.ui.common

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import com.comtax.app.ui.theme.Dimens
import com.comtax.app.ui.theme.Gasto
import com.comtax.app.ui.theme.Superficie
import com.comtax.app.ui.theme.TextoPrincipal
import com.comtax.app.ui.theme.TextoSecundario
import com.comtax.app.ui.theme.comoCifra
import com.comtax.app.util.centimosATexto

// Tarjeta de saldo destacado. Uso exclusivo de pantallas de consulta (Balance):
// Home NO la usa — el registro de un movimiento es una accion, no una consulta,
// y mostrar cuanto dinero queda mientras alguien esta pagando no aporta.
@Composable
fun TarjetaSaldoHero(
    etiqueta: String,
    saldoCentimos: Long,
    modifier: Modifier = Modifier,
    tamano: TextStyle = MaterialTheme.typography.displayMedium
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(Dimens.radioBordeGrande),
        colors = CardDefaults.cardColors(containerColor = Superficie),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.elevacionHero)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(Dimens.espacioPantalla)) {
            Text(
                text = etiqueta.uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = TextoSecundario
            )
            Spacer(modifier = Modifier.padding(top = Dimens.espacioMinimo))
            Text(
                text = "${centimosATexto(saldoCentimos)} €",
                style = tamano.comoCifra(),
                color = if (saldoCentimos < 0) Gasto else TextoPrincipal
            )
        }
    }
}
