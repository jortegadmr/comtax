package com.comtax.app.ui.ajustes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comtax.app.data.local.entity.CuentaEntity
import com.comtax.app.data.local.entity.EstadoSobre
import com.comtax.app.data.local.entity.SobreEntity
import com.comtax.app.data.repository.CuentaRepository
import com.comtax.app.data.repository.SobreRepository
import com.comtax.app.ui.theme.siguienteColorLibre
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CuentasSobresUiState(
    val cuentas: List<CuentaEntity> = emptyList(),
    val cuentaSeleccionadaId: Long? = null,
    val sobresDeCuenta: List<SobreEntity> = emptyList(),
    val nombreNuevaCuenta: String = "",
    val colorNuevaCuenta: String = siguienteColorLibre(0),
    val nombreNuevoSobre: String = ""
)

@HiltViewModel
class CuentasSobresViewModel @Inject constructor(
    private val cuentaRepository: CuentaRepository,
    private val sobreRepository: SobreRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(CuentasSobresUiState())
    val uiState: StateFlow<CuentasSobresUiState> = _uiState

    private var jobSobres: Job? = null

    init {
        cuentaRepository.observarTodas()
            .onEach { cuentas ->
                _uiState.value = _uiState.value.copy(
                    cuentas = cuentas,
                    colorNuevaCuenta = siguienteColorLibre(cuentas.size)
                )
            }
            .launchIn(viewModelScope)
    }

    fun onNombreNuevaCuentaCambia(texto: String) {
        _uiState.value = _uiState.value.copy(nombreNuevaCuenta = texto)
    }

    fun onColorNuevaCuentaCambia(colorHex: String) {
        _uiState.value = _uiState.value.copy(colorNuevaCuenta = colorHex)
    }

    fun crearCuenta() = viewModelScope.launch {
        val estado = _uiState.value
        val nombre = estado.nombreNuevaCuenta.trim()
        if (nombre.isBlank()) return@launch
        cuentaRepository.crear(nombre, estado.colorNuevaCuenta)
        _uiState.value = _uiState.value.copy(nombreNuevaCuenta = "")
    }

    fun renombrarCuenta(id: Long, nombre: String) = viewModelScope.launch {
        if (nombre.isBlank()) return@launch
        cuentaRepository.renombrar(id, nombre.trim())
    }

    fun cambiarColorCuenta(id: Long, colorHex: String) = viewModelScope.launch {
        cuentaRepository.cambiarColor(id, colorHex)
    }

    fun archivarCuenta(id: Long) = viewModelScope.launch { cuentaRepository.archivar(id) }

    fun desarchivarCuenta(id: Long) = viewModelScope.launch { cuentaRepository.desarchivar(id) }

    fun seleccionarCuenta(id: Long?) {
        _uiState.value = _uiState.value.copy(cuentaSeleccionadaId = id, sobresDeCuenta = emptyList())
        jobSobres?.cancel()
        if (id == null) return
        jobSobres = sobreRepository.observarTodosDeCuenta(id)
            .onEach { sobres -> _uiState.value = _uiState.value.copy(sobresDeCuenta = sobres) }
            .launchIn(viewModelScope)
    }

    fun onNombreNuevoSobreCambia(texto: String) {
        _uiState.value = _uiState.value.copy(nombreNuevoSobre = texto)
    }

    fun crearSobre() = viewModelScope.launch {
        val cuentaId = _uiState.value.cuentaSeleccionadaId ?: return@launch
        val nombre = _uiState.value.nombreNuevoSobre.trim()
        if (nombre.isBlank()) return@launch
        sobreRepository.crear(cuentaId, nombre)
        _uiState.value = _uiState.value.copy(nombreNuevoSobre = "")
    }

    fun renombrarSobre(id: Long, nombre: String) = viewModelScope.launch {
        if (nombre.isBlank()) return@launch
        sobreRepository.renombrar(id, nombre.trim())
    }

    fun cambiarEstadoSobre(sobre: SobreEntity) = viewModelScope.launch {
        if (sobre.estado == EstadoSobre.ACTIVO) sobreRepository.archivar(sobre.id) else sobreRepository.desarchivar(sobre.id)
    }
}
