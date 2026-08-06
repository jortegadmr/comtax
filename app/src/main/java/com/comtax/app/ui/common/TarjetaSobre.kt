package com.comtax.app.ui.common

import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp
import com.comtax.app.ui.theme.AccentIndigo
import com.comtax.app.ui.theme.BlancoSobrePrimario
import com.comtax.app.ui.theme.SuperficieAlt
import com.comtax.app.ui.theme.TextoPrincipal

// Sobre = solo una etiqueta de categoria, sin cifras ni barra de progreso/presupuesto:
// llevar la cuenta de cuanto queda no es el objetivo de esta app (ver decision de
// producto tras validar el primer mockup — la persona registra en el momento, no
// consulta analitica mientras paga).
@Composable
fun TarjetaSobre(
    nombre: String,
    seleccionado: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    forma: Shape = FormaSobre()
) {
    FilterChip(
        selected = seleccionado,
        onClick = onClick,
        label = { Text(nombre, style = MaterialTheme.typography.bodyLarge) },
        shape = forma,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = SuperficieAlt,
            labelColor = TextoPrincipal,
            selectedContainerColor = AccentIndigo,
            selectedLabelColor = BlancoSobrePrimario
        ),
        modifier = modifier.heightIn(min = 52.dp)
    )
}
