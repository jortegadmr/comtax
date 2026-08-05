package com.comtax.app.ui.balance

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.comtax.app.data.local.entity.MovimientoEntity
import com.comtax.app.data.local.entity.SobreEntity
import com.comtax.app.data.repository.MovimientoRepository
import com.comtax.app.data.repository.SobreRepository
import com.comtax.app.ui.navigation.MovimientoDetalle
import com.comtax.app.util.centimosATexto
import com.comtax.app.util.textoACentimos
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class MovimientoDetalleUiState(
    val movimiento: MovimientoEntity? = null,
    val sobresActivos: List<SobreEntity> = emptyList(),
    val sobreSeleccionado: SobreEntity? = null,
    val importeTexto: String = "",
    val concepto: String = "",
    val fecha: LocalDate = LocalDate.now(),
    val errorFormulario: String? = null,
    val cargando: Boolean = true
)

sealed interface MovimientoDetalleEvento {
    data object Guardado : MovimientoDetalleEvento
    data object Anulado : MovimientoDetalleEvento
}

@HiltViewModel
class MovimientoDetalleViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val movimientoRepository: MovimientoRepository,
    private val sobreRepository: SobreRepository
) : ViewModel() {

    private val movimientoId: Long = savedStateHandle.toRoute<MovimientoDetalle>().id

    private val _uiState = MutableStateFlow(MovimientoDetalleUiState())
    val uiState: StateFlow<MovimientoDetalleUiState> = _uiState

    private val _eventos = Channel<MovimientoDetalleEvento>()
    val eventos = _eventos.receiveAsFlow()

    init {
        viewModelScope.launch {
            val movimiento = movimientoRepository.obtenerPorId(movimientoId) ?: return@launch
            val sobres = sobreRepository.observarActivosDeCuenta(movimiento.cuentaId)
            _uiState.value = _uiState.value.copy(
                movimiento = movimiento,
                importeTexto = centimosATexto(movimiento.importeCentimos),
                concepto = movimiento.concepto,
                fecha = movimiento.fecha,
                cargando = false
            )
            sobres.collect { lista ->
                val actual = _uiState.value
                _uiState.value = actual.copy(
                    sobresActivos = lista,
                    sobreSeleccionado = actual.sobreSeleccionado
                        ?: lista.firstOrNull { it.id == movimiento.sobreId }
                )
            }
        }
    }

    fun onImporteCambia(texto: String) {
        if (texto.length <= 10 && texto.all { it.isDigit() || it == ',' || it == '.' }) {
            _uiState.value = _uiState.value.copy(importeTexto = texto, errorFormulario = null)
        }
    }

    fun onConceptoCambia(texto: String) {
        _uiState.value = _uiState.value.copy(concepto = texto, errorFormulario = null)
    }

    fun onFechaCambia(fecha: LocalDate) {
        _uiState.value = _uiState.value.copy(fecha = fecha)
    }

    fun onSobreSeleccionado(sobre: SobreEntity?) {
        _uiState.value = _uiState.value.copy(sobreSeleccionado = sobre)
    }

    fun guardarEdicion() {
        val estado = _uiState.value
        val movimiento = estado.movimiento ?: return
        val importe = textoACentimos(estado.importeTexto)
        if (importe == null || estado.concepto.isBlank()) {
            _uiState.value = estado.copy(errorFormulario = "Revisa el importe y el concepto")
            return
        }
        viewModelScope.launch {
            movimientoRepository.editar(
                movimiento = movimiento,
                sobreId = estado.sobreSeleccionado?.id,
                importeCentimos = importe,
                concepto = estado.concepto.trim(),
                fecha = estado.fecha
            )
            _eventos.send(MovimientoDetalleEvento.Guardado)
        }
    }

    fun anular() {
        viewModelScope.launch {
            movimientoRepository.anular(movimientoId)
            _eventos.send(MovimientoDetalleEvento.Anulado)
        }
    }

    fun marcarCuadrado() {
        viewModelScope.launch {
            movimientoRepository.marcarCuadrado(movimientoId)
            val actual = _uiState.value
            actual.movimiento?.let {
                _uiState.value = actual.copy(movimiento = it.copy(pendienteCuadre = false))
            }
        }
    }
}
