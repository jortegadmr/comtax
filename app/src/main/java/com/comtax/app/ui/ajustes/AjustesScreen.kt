package com.comtax.app.ui.ajustes

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.CallMerge
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.rounded.AccountBalanceWallet
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.comtax.app.ui.common.ScaffoldConVolver
import com.comtax.app.ui.theme.AccentIndigo
import com.comtax.app.ui.theme.AccentIndigoContenedor
import com.comtax.app.ui.theme.Dimens
import com.comtax.app.ui.theme.Superficie
import com.comtax.app.ui.theme.TextoSecundario

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AjustesScreen(
    onVolver: () -> Unit,
    onCuentasSobres: () -> Unit,
    onFusionSobres: () -> Unit,
    onPinSettings: () -> Unit
) {
    ScaffoldConVolver(titulo = "Ajustes", onVolver = onVolver) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Dimens.espacioPantalla)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Superficie),
                elevation = CardDefaults.cardElevation(defaultElevation = Dimens.elevacionTarjeta)
            ) {
                Column {
                    FilaAjuste(
                        icono = Icons.Rounded.AccountBalanceWallet,
                        texto = "Cuentas y sobres",
                        onClick = onCuentasSobres
                    )
                    FilaAjuste(
                        icono = Icons.AutoMirrored.Rounded.CallMerge,
                        texto = "Fusionar sobres",
                        onClick = onFusionSobres
                    )
                    FilaAjuste(
                        icono = Icons.Rounded.Lock,
                        texto = "Cambiar PIN",
                        onClick = onPinSettings,
                        esUltima = true
                    )
                }
            }
        }
    }
}

@Composable
private fun FilaAjuste(
    icono: ImageVector,
    texto: String,
    onClick: () -> Unit,
    esUltima: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(Dimens.espacioEstandar),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.espacioMedio)
    ) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(AccentIndigoContenedor, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icono, contentDescription = null, tint = AccentIndigo)
        }
        Text(
            text = texto,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Icon(
            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
            contentDescription = null,
            tint = TextoSecundario
        )
    }
    if (!esUltima) {
        HorizontalDivider(
            modifier = Modifier.padding(start = 72.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )
    }
}
