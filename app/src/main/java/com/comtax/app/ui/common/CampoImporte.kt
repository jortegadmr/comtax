package com.comtax.app.ui.common

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import com.comtax.app.ui.theme.comoCifra

@Composable
fun CampoImporte(
    valor: String,
    onValorCambia: (String) -> Unit,
    modifier: Modifier = Modifier,
    etiqueta: String = "Importe (€)"
) {
    OutlinedTextField(
        value = valor,
        onValueChange = { nuevo ->
            if (nuevo.length <= 10 && nuevo.all { it.isDigit() || it == ',' || it == '.' }) {
                onValorCambia(nuevo)
            }
        },
        label = { Text(etiqueta, style = MaterialTheme.typography.bodyLarge) },
        textStyle = MaterialTheme.typography.headlineMedium.comoCifra().copy(textAlign = TextAlign.Center),
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
        singleLine = true,
        modifier = modifier.fillMaxWidth()
    )
}
