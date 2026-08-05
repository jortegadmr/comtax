package com.comtax.app.ui.balance

import android.net.Uri
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.comtax.app.data.export.CsvExporter
import com.comtax.app.data.local.entity.CuentaEntity
import com.comtax.app.data.local.entity.SobreEntity
import com.comtax.app.data.local.query.MovimientoListItem
import com.comtax.app.data.repository.BalanceRepository
import com.comtax.app.data.repository.MovimientoRepository
import com.comtax.app.ui.navigation.BalanceCuenta
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject

data class RangoFechas(
    val desde: LocalDate? = null,
    val hasta: LocalDate? = null
)

data class BalanceCuentaUiState(
    val cuenta: CuentaEntity? = null,
    val saldoCuenta: Long = 0,
    val sobres: List<SobreEntity> = emptyList(),
    val saldoPorSobre: Map<Long, Long> = emptyMap(),
    val sobreSeleccionadoId: Long? = null,
    val rangoFechas: RangoFechas = RangoFechas(),
    val movimientos: List<MovimientoListItem> = emptyList()
)

sealed interface BalanceCuentaEvento {
    data class CompartirCsv(val uri: Uri) : BalanceCuentaEvento
}

@HiltViewModel
class BalanceCuentaViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val balanceRepository: BalanceRepository,
    private val movimientoRepository: MovimientoRepository,
    private val csvExporter: CsvExporter
) : ViewModel() {

    private val cuentaId: Long = savedStateHandle.toRoute<BalanceCuenta>().cuentaId

    private val _sobreSeleccionadoId = MutableStateFlow<Long?>(null)
    private val _rangoFechas = MutableStateFlow(RangoFechas())

    private val _eventos = Channel<BalanceCuentaEvento>()
    val eventos = _eventos.receiveAsFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<BalanceCuentaUiState> = combine(
        balanceRepository.observarBalance(),
        _sobreSeleccionadoId,
        _rangoFechas,
        combine(_sobreSeleccionadoId, _rangoFechas) { sobreId, rango -> sobreId to rango }
            .flatMapLatest { (sobreId, rango) ->
                movimientoRepository.listadoFiltrado(cuentaId, sobreId, rango.desde, rango.hasta)
            }
    ) { balance, sobreSeleccionadoId, rango, movimientos ->
        BalanceCuentaUiState(
            cuenta = balance.cuentas.firstOrNull { it.id == cuentaId },
            saldoCuenta = balance.saldoPorCuenta[cuentaId] ?: 0,
            sobres = balance.sobres.filter { it.cuentaId == cuentaId },
            saldoPorSobre = balance.saldoPorSobre,
            sobreSeleccionadoId = sobreSeleccionadoId,
            rangoFechas = rango,
            movimientos = movimientos
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BalanceCuentaUiState())

    fun seleccionarSobre(sobreId: Long?) {
        _sobreSeleccionadoId.value = if (_sobreSeleccionadoId.value == sobreId) null else sobreId
    }

    fun filtrarPorFechas(desde: LocalDate?, hasta: LocalDate?) {
        _rangoFechas.value = RangoFechas(desde, hasta)
    }

    fun limpiarFiltroFechas() {
        _rangoFechas.value = RangoFechas()
    }

    fun exportarCsv() = viewModelScope.launch {
        val rango = _rangoFechas.value
        val filas = movimientoRepository.listadoFiltradoSnapshot(
            cuentaId, _sobreSeleccionadoId.value, rango.desde, rango.hasta
        )
        val uri = csvExporter.exportar(filas)
        _eventos.send(BalanceCuentaEvento.CompartirCsv(uri))
    }
}
