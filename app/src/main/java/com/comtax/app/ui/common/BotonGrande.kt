package com.comtax.app.ui.common

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import com.comtax.app.ui.theme.Dimens

@Composable
fun BotonGrande(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    colors: ButtonColors = ButtonDefaults.buttonColors(),
    icono: ImageVector? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val presionado by interactionSource.collectIsPressedAsState()
    val escala by animateFloatAsState(if (presionado) 0.97f else 1f, label = "escalaBotonGrande")

    Button(
        onClick = onClick,
        enabled = habilitado,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(Dimens.radioBordeGrande),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = Dimens.elevacionTarjetaProminente),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = Dimens.alturaBotonGrande)
            .scale(escala),
        colors = colors,
        contentPadding = PaddingValues(Dimens.espacioEstandar)
    ) {
        if (icono != null) {
            Icon(imageVector = icono, contentDescription = null)
            Spacer(modifier = Modifier.padding(start = Dimens.espacioMedio))
        }
        Text(text = texto, style = MaterialTheme.typography.titleLarge)
    }
}

@Composable
fun BotonGrandeSecundario(
    texto: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    habilitado: Boolean = true,
    colors: ButtonColors = ButtonDefaults.outlinedButtonColors()
) {
    val interactionSource = remember { MutableInteractionSource() }
    val presionado by interactionSource.collectIsPressedAsState()
    val escala by animateFloatAsState(if (presionado) 0.98f else 1f, label = "escalaBotonSecundario")

    OutlinedButton(
        onClick = onClick,
        enabled = habilitado,
        interactionSource = interactionSource,
        shape = RoundedCornerShape(Dimens.radioBorde),
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = Dimens.alturaBotonGrande)
            .scale(escala),
        colors = colors
    ) {
        Text(text = texto, style = MaterialTheme.typography.titleMedium)
    }
}
