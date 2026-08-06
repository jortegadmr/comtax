package com.comtax.app.ui.common

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.comtax.app.ui.theme.Aviso
import com.comtax.app.ui.theme.AvisoContenedor
import com.comtax.app.ui.theme.Dimens

@Composable
fun AvisoNegativoCard(
    mensaje: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = AvisoContenedor)
    ) {
        Row(modifier = Modifier.padding(Dimens.espacioEstandar)) {
            Icon(
                imageVector = Icons.Filled.Warning,
                contentDescription = "Aviso",
                tint = Aviso
            )
            Spacer(modifier = Modifier.width(Dimens.espacioMedio))
            Text(
                text = mensaje,
                style = MaterialTheme.typography.bodyLarge,
                color = Aviso
            )
        }
    }
}
