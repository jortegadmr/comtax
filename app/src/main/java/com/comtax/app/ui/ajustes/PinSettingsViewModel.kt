package com.comtax.app.ui.ajustes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comtax.app.data.security.PinManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PinSettingsUiState(
    val pinActual: String = "",
    val pinNuevo: String = "",
    val pinNuevoConfirmacion: String = "",
    val error: String? = null,
    val exito: Boolean = false
)

@HiltViewModel
class PinSettingsViewModel @Inject constructor(
    private val pinManager: PinManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(PinSettingsUiState())
    val uiState: StateFlow<PinSettingsUiState> = _uiState

    private fun soloDigitos(valor: String) = valor.length <= 6 && valor.all { it.isDigit() }

    fun onPinActualCambia(valor: String) {
        if (soloDigitos(valor)) _uiState.value = _uiState.value.copy(pinActual = valor, error = null, exito = false)
    }

    fun onPinNuevoCambia(valor: String) {
        if (soloDigitos(valor)) _uiState.value = _uiState.value.copy(pinNuevo = valor, error = null, exito = false)
    }

    fun onPinNuevoConfirmacionCambia(valor: String) {
        if (soloDigitos(valor)) _uiState.value = _uiState.value.copy(pinNuevoConfirmacion = valor, error = null, exito = false)
    }

    fun cambiarPin() {
        val estado = _uiState.value
        if (estado.pinNuevo.length < 4) {
            _uiState.value = estado.copy(error = "El nuevo PIN debe tener al menos 4 dígitos")
            return
        }
        if (estado.pinNuevo != estado.pinNuevoConfirmacion) {
            _uiState.value = estado.copy(error = "Los PIN nuevos no coinciden")
            return
        }
        viewModelScope.launch {
            val ok = pinManager.cambiarPin(estado.pinActual, estado.pinNuevo)
            _uiState.value = if (ok) {
                PinSettingsUiState(exito = true)
            } else {
                estado.copy(error = "El PIN actual no es correcto")
            }
        }
    }
}
