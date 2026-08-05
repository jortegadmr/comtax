package com.comtax.app.ui.ajustes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comtax.app.data.local.entity.CuentaEntity
import com.comtax.app.data.local.entity.SobreEntity
import com.comtax.app.data.repository.CuentaRepository
import com.comtax.app.data.repository.SobreRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FusionSobresUiState(
    val cuentas: List<CuentaEntity> = emptyList(),
    val cuentaSeleccionadaId: Long? = null,
    val sobresDeCuenta: List<SobreEntity> = emptyList(),
    val origenId: Long? = null,
    val destinoId: Long? = null,
    val error: String? = null,
    val exito: Boolean = false
)

@HiltViewModel
class FusionSobresViewModel @Inject constructor(
    private val cuentaRepository: CuentaRepository,
    private val sobreRepository: SobreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FusionSobresUiState())
    val uiState: StateFlow<FusionSobresUiState> = _uiState

    private var jobSobres: Job? = null

    init {
        cuentaRepository.observarActivas()
            .onEach { cuentas -> _uiState.value = _uiState.value.copy(cuentas = cuentas) }
            .launchIn(viewModelScope)
    }

    fun seleccionarCuenta(id: Long) {
        _uiState.value = _uiState.value.copy(
            cuentaSeleccionadaId = id,
            origenId = null,
            destinoId = null,
            error = null
        )
        jobSobres?.cancel()
        jobSobres = sobreRepository.observarActivosDeCuenta(id)
            .onEach { sobres -> _uiState.value = _uiState.value.copy(sobresDeCuenta = sobres) }
            .launchIn(viewModelScope)
    }

    fun seleccionarOrigen(id: Long) {
        _uiState.value = _uiState.value.copy(origenId = id, error = null)
    }

    fun seleccionarDestino(id: Long) {
        _uiState.value = _uiState.value.copy(destinoId = id, error = null)
    }

    fun fusionar() {
        val estado = _uiState.value
        val origen = estado.origenId
        val destino = estado.destinoId
        if (origen == null || destino == null) {
            _uiState.value = estado.copy(error = "Elige el sobre origen y el sobre destino")
            return
        }
        if (origen == destino) {
            _uiState.value = estado.copy(error = "Elige dos sobres distintos")
            return
        }
        viewModelScope.launch {
            try {
                sobreRepository.fusionarSobres(origen, destino)
                _uiState.value = FusionSobresUiState(
                    cuentas = estado.cuentas,
                    cuentaSeleccionadaId = estado.cuentaSeleccionadaId,
                    exito = true
                )
                estado.cuentaSeleccionadaId?.let { seleccionarCuenta(it) }
            } catch (e: RuntimeException) {
                // fusionarSobres valida con require()/error(): IllegalArgumentException
                // o IllegalStateException segun el caso, ambas con mensaje para el usuario.
                _uiState.value = estado.copy(error = e.message)
            }
        }
    }
}
