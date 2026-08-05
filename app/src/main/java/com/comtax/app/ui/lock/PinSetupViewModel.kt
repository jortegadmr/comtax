package com.comtax.app.ui.lock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comtax.app.data.security.PinManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PinSetupUiState(
    val pin: String = "",
    val confirmacion: String = "",
    val pidiendoConfirmacion: Boolean = false,
    val error: String? = null
)

sealed interface PinSetupEvento {
    data object PinConfigurado : PinSetupEvento
}

@HiltViewModel
class PinSetupViewModel @Inject constructor(
    private val pinManager: PinManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PinSetupUiState())
    val uiState: StateFlow<PinSetupUiState> = _uiState

    private val _eventos = Channel<PinSetupEvento>()
    val eventos = _eventos.receiveAsFlow()

    fun onPinCambia(valor: String) {
        if (valor.length <= 6 && valor.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(pin = valor, error = null)
        }
    }

    fun onConfirmacionCambia(valor: String) {
        if (valor.length <= 6 && valor.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(confirmacion = valor, error = null)
        }
    }

    fun continuarAConfirmacion() {
        val estado = _uiState.value
        if (estado.pin.length < 4) {
            _uiState.value = estado.copy(error = "El PIN debe tener al menos 4 dígitos")
            return
        }
        _uiState.value = estado.copy(pidiendoConfirmacion = true)
    }

    fun confirmarPin() = viewModelScope.launch {
        val estado = _uiState.value
        if (estado.pin != estado.confirmacion) {
            _uiState.value = estado.copy(
                confirmacion = "",
                error = "Los PIN no coinciden, inténtalo de nuevo"
            )
            return@launch
        }
        pinManager.configurarPin(estado.pin)
        _eventos.send(PinSetupEvento.PinConfigurado)
    }
}
