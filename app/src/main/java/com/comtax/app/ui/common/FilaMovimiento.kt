package com.comtax.app.ui.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.comtax.app.data.local.entity.TipoMovimiento
import com.comtax.app.ui.theme.AvisoContenedor
import com.comtax.app.ui.theme.Aviso
import com.comtax.app.ui.theme.Dimens
import com.comtax.app.ui.theme.Gasto
import com.comtax.app.ui.theme.Ingreso
import com.comtax.app.ui.theme.Superficie
import com.comtax.app.ui.theme.SuperficieAlt
import com.comtax.app.ui.theme.TextoSecundario
import com.comtax.app.ui.theme.comoCifra
import com.comtax.app.util.centimosATexto

@Composable
fun FilaMovimiento(
    fechaTexto: String,
    concepto: String,
    importeCentimos: Long,
    tipo: TipoMovimiento,
    modifier: Modifier = Modifier,
    sobreNombre: String? = null,
    pendienteCuadre: Boolean = false,
    onClick: (() -> Unit)? = null
) {
    val clickable = if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier
    Card(
        modifier = modifier.fillMaxWidth().then(clickable),
        shape = RoundedCornerShape(Dimens.radioBorde),
        colors = CardDefaults.cardColors(containerColor = Superficie),
        elevation = CardDefaults.cardElevation(defaultElevation = Dimens.elevacionTarjeta)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(Dimens.espacioMedio),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(text = fechaTexto, style = MaterialTheme.typography.bodyMedium, color = TextoSecundario)
                    val color = if (tipo == TipoMovimiento.INGRESO) Ingreso else Gasto
                    val signo = if (tipo == TipoMovimiento.INGRESO) "+" else "-"
                    Text(
                        text = "$signo${centimosATexto(importeCentimos)} €",
                        color = color,
                        style = MaterialTheme.typography.titleMedium.comoCifra()
                    )
                }
                Spacer(modifier = Modifier.padding(top = Dimens.espacioMinimo))
                Text(text = concepto, style = MaterialTheme.typography.bodyLarge)
                if (sobreNombre != null || pendienteCuadre) {
                    Spacer(modifier = Modifier.padding(top = Dimens.espacioMinimo))
                    Row(horizontalArrangement = Arrangement.spacedBy(Dimens.espacioPequeno)) {
                        sobreNombre?.let {
                            EtiquetaBadge(texto = it, color = TextoSecundario, fondo = SuperficieAlt)
                        }
                        if (pendienteCuadre) {
                            EtiquetaBadge(texto = "Pendiente de cuadre", color = Aviso, fondo = AvisoContenedor)
                        }
                    }
                }
            }
            if (onClick != null) {
                Spacer(modifier = Modifier.padding(start = Dimens.espacioMinimo))
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Ver detalle",
                    tint = TextoSecundario
                )
            }
        }
    }
}
