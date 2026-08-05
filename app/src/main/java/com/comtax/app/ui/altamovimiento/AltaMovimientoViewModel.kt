package com.comtax.app.ui.altamovimiento

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.comtax.app.data.local.entity.CuentaEntity
import com.comtax.app.data.local.entity.SobreEntity
import com.comtax.app.data.local.entity.TipoMovimiento
import com.comtax.app.data.repository.CuentaRepository
import com.comtax.app.data.repository.MovimientoRepository
import com.comtax.app.data.repository.SobreRepository
import com.comtax.app.ui.navigation.AltaMovimientoGraph
import com.comtax.app.util.textoACentimos
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class AvisoNegativo(
    val sobreQuedaNegativo: Boolean,
    val cuentaQuedaNegativa: Boolean,
    val saldoResultanteSobreCentimos: Long?,
    val saldoResultanteCuentaCentimos: Long
)

data class AltaMovimientoUiState(
    val tipo: TipoMovimiento,
    val cuentas: List<CuentaEntity> = emptyList(),
    val cuentaSeleccionada: CuentaEntity? = null,
    val sobresActivos: List<SobreEntity> = emptyList(),
    val sobreSeleccionado: SobreEntity? = null,
    val importeTexto: String = "",
    val concepto: String = "",
    val fecha: LocalDate = LocalDate.now(),
    val errorFormulario: String? = null,
    val avisoNegativo: AvisoNegativo? = null,
    val guardando: Boolean = false
)

sealed interface AltaMovimientoEvento {
    data object Guardado : AltaMovimientoEvento
}

@HiltViewModel
class AltaMovimientoViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val cuentaRepository: CuentaRepository,
    private val sobreRepository: SobreRepository,
    private val movimientoRepository: MovimientoRepository
) : ViewModel() {

    private val tipo: TipoMovimiento = savedStateHandle.toRoute<AltaMovimientoGraph>().tipo

    private val _uiState = MutableStateFlow(AltaMovimientoUiState(tipo = tipo))
    val uiState: StateFlow<AltaMovimientoUiState> = _uiState

    private val _eventos = Channel<AltaMovimientoEvento>()
    val eventos = _eventos.receiveAsFlow()

    private var jobSobres: Job? = null

    init {
        cuentaRepository.observarActivas()
            .onEach { cuentas -> _uiState.value = _uiState.value.copy(cuentas = cuentas) }
            .launchIn(viewModelScope)
    }

    fun onCuentaSeleccionada(cuenta: CuentaEntity) {
        _uiState.value = _uiState.value.copy(
            cuentaSeleccionada = cuenta,
            sobreSeleccionado = null,
            sobresActivos = emptyList()
        )
        jobSobres?.cancel()
        jobSobres = sobreRepository.observarActivosDeCuenta(cuenta.id)
            .onEach { sobres -> _uiState.value = _uiState.value.copy(sobresActivos = sobres) }
            .launchIn(viewModelScope)
    }

    fun onSobreSeleccionado(sobre: SobreEntity?) {
        _uiState.value = _uiState.value.copy(sobreSeleccionado = sobre)
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

    fun validarFormulario(): Boolean {
        val estado = _uiState.value
        val importe = textoACentimos(estado.importeTexto)
        val error = when {
            importe == null -> "Introduce un importe válido"
            estado.concepto.isBlank() -> "El concepto es obligatorio"
            else -> null
        }
        _uiState.value = estado.copy(errorFormulario = error)
        return error == null
    }

    suspend fun prepararConfirmacion() {
        val estado = _uiState.value
        val importe = textoACentimos(estado.importeTexto) ?: return
        if (estado.tipo == TipoMovimiento.INGRESO) {
            _uiState.value = estado.copy(avisoNegativo = null)
            return
        }
        val cuentaId = estado.cuentaSeleccionada?.id ?: return
        val saldoCuentaResultante = movimientoRepository.saldoActualCuenta(cuentaId) - importe
        val sobreId = estado.sobreSeleccionado?.id
        val saldoSobreResultante = sobreId?.let { movimientoRepository.saldoActualSobre(it) - importe }

        val cuentaNegativa = saldoCuentaResultante < 0
        val sobreNegativo = saldoSobreResultante != null && saldoSobreResultante < 0

        _uiState.value = _uiState.value.copy(
            avisoNegativo = if (cuentaNegativa || sobreNegativo) {
                AvisoNegativo(
                    sobreQuedaNegativo = sobreNegativo,
                    cuentaQuedaNegativa = cuentaNegativa,
                    saldoResultanteSobreCentimos = saldoSobreResultante,
                    saldoResultanteCuentaCentimos = saldoCuentaResultante
                )
            } else null
        )
    }

    fun confirmar() {
        val estado = _uiState.value
        val importe = textoACentimos(estado.importeTexto) ?: return
        val cuentaId = estado.cuentaSeleccionada?.id ?: return
        _uiState.value = estado.copy(guardando = true)
        viewModelScope.launch {
            movimientoRepository.registrar(
                tipo = estado.tipo,
                cuentaId = cuentaId,
                sobreId = estado.sobreSeleccionado?.id,
                importeCentimos = importe,
                concepto = estado.concepto.trim(),
                fecha = estado.fecha,
                pendienteCuadre = estado.avisoNegativo != null
            )
            _eventos.send(AltaMovimientoEvento.Guardado)
        }
    }
}
