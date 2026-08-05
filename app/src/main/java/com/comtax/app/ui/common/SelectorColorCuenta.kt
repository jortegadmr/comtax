package com.comtax.app.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.comtax.app.ui.theme.colorDesdeHex
import com.comtax.app.ui.theme.paletaCuentas

@Composable
fun PuntoColor(colorHex: String, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(16.dp)
            .clip(CircleShape)
            .background(colorDesdeHex(colorHex))
    )
}

@Composable
fun SelectorColorCuenta(
    colorSeleccionado: String,
    onColorSeleccionado: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
        paletaCuentas.forEach { hex ->
            val seleccionado = hex == colorSeleccionado
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(CircleShape)
                    .background(colorDesdeHex(hex))
                    .clickable { onColorSeleccionado(hex) },
                contentAlignment = Alignment.Center
            ) {
                if (seleccionado) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = "Seleccionado",
                        tint = Color.White
                    )
                }
            }
        }
    }
}
