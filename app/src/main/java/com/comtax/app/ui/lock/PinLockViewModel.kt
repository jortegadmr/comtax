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

data class PinLockUiState(
    val pin: String = "",
    val error: String? = null
)

sealed interface PinLockEvento {
    data object Desbloqueado : PinLockEvento
}

@HiltViewModel
class PinLockViewModel @Inject constructor(
    private val pinManager: PinManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PinLockUiState())
    val uiState: StateFlow<PinLockUiState> = _uiState

    private val _eventos = Channel<PinLockEvento>()
    val eventos = _eventos.receiveAsFlow()

    fun onPinCambia(valor: String) {
        if (valor.length <= 6 && valor.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(pin = valor, error = null)
            if (valor.length >= 4) verificar(valor)
        }
    }

    private fun verificar(pin: String) = viewModelScope.launch {
        if (pinManager.verificarPin(pin)) {
            _eventos.send(PinLockEvento.Desbloqueado)
        } else if (pin.length == 6) {
            _uiState.value = _uiState.value.copy(pin = "", error = "PIN incorrecto")
        }
    }
}
