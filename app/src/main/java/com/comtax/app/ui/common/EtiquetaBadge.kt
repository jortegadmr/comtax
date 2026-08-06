package com.comtax.app.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.comtax.app.ui.theme.Dimens

@Composable
fun EtiquetaBadge(texto: String, color: Color, fondo: Color, modifier: Modifier = Modifier) {
    Text(
        text = texto,
        style = MaterialTheme.typography.bodySmall,
        color = color,
        modifier = modifier
            .background(color = fondo, shape = MaterialTheme.shapes.small)
            .padding(horizontal = Dimens.espacioPequeno, vertical = Dimens.espacioMinimo)
    )
}
