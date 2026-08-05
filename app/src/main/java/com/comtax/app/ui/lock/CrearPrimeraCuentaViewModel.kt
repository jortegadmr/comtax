package com.comtax.app.ui.lock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comtax.app.data.repository.CuentaRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CrearPrimeraCuentaUiState(
    val nombre: String = "",
    val error: String? = null
)

sealed interface CrearPrimeraCuentaEvento {
    data object CuentaCreada : CrearPrimeraCuentaEvento
}

@HiltViewModel
class CrearPrimeraCuentaViewModel @Inject constructor(
    private val cuentaRepository: CuentaRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CrearPrimeraCuentaUiState())
    val uiState: StateFlow<CrearPrimeraCuentaUiState> = _uiState

    private val _eventos = Channel<CrearPrimeraCuentaEvento>()
    val eventos = _eventos.receiveAsFlow()

    fun onNombreCambia(texto: String) {
        _uiState.value = _uiState.value.copy(nombre = texto, error = null)
    }

    fun crear() {
        val nombre = _uiState.value.nombre.trim()
        if (nombre.isBlank()) {
            _uiState.value = _uiState.value.copy(error = "Ponle un nombre a la cuenta")
            return
        }
        viewModelScope.launch {
            cuentaRepository.crear(nombre)
            _eventos.send(CrearPrimeraCuentaEvento.CuentaCreada)
        }
    }
}
