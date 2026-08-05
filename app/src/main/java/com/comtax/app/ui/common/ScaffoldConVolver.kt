package com.comtax.app.ui.common

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable

// Todas las pantallas secundarias comparten esta barra con flecha de volver: sin
// ella, la unica forma de retroceder era el gesto o boton del sistema, poco fiable
// para el usuario objetivo de esta app (persona mayor).
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScaffoldConVolver(
    titulo: String,
    onVolver: () -> Unit,
    colors: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    contenido: @Composable (PaddingValues) -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(titulo, style = MaterialTheme.typography.titleLarge) },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = colors
            )
        },
        content = contenido
    )
}
