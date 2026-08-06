package com.comtax.app.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.comtax.app.ui.common.BotonGrande
import com.comtax.app.ui.common.BotonGrandeSecundario
import com.comtax.app.ui.theme.Dimens
import com.comtax.app.ui.theme.RojoGasto
import com.comtax.app.ui.theme.VerdeIngreso

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onIngreso: () -> Unit,
    onGasto: () -> Unit,
    onBalance: () -> Unit,
    onAjustes: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Comtax", style = MaterialTheme.typography.titleLarge) },
                actions = {
                    IconButton(onClick = onAjustes) {
                        Icon(imageVector = Icons.Filled.Settings, contentDescription = "Ajustes")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors()
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(Dimens.espacioPantalla),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            BotonGrande(
                texto = "Ingreso",
                onClick = onIngreso,
                colors = ButtonDefaults.buttonColors(containerColor = VerdeIngreso)
            )
            Spacer(modifier = Modifier.padding(top = Dimens.espacioEntreBotones))
            BotonGrande(
                texto = "Gasto",
                onClick = onGasto,
                colors = ButtonDefaults.buttonColors(containerColor = RojoGasto)
            )
            Spacer(modifier = Modifier.padding(top = Dimens.espacioSeccion))
            BotonGrandeSecundario(
                texto = "Balance y registro",
                onClick = onBalance
            )
        }
    }
}
