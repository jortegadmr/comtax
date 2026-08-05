package com.comtax.app.ui.balance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import android.net.Uri
import com.comtax.app.data.export.CsvExporter
import com.comtax.app.data.local.entity.CuentaEntity
import com.comtax.app.data.local.entity.SobreEntity
import com.comtax.app.data.local.query.MovimientoListItem
import com.comtax.app.data.repository.BalanceCompleto
import com.comtax.app.data.repository.BalanceRepository
import com.comtax.app.data.repository.MovimientoRepository
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

data class Filtros(
    val cuentaId: Long? = null,
    val sobreId: Long? = null,
    val fechaDesde: LocalDate? = null,
    val fechaHasta: LocalDate? = null
)

data class BalanceUiState(
    val cuentas: List<CuentaEntity> = emptyList(),
    val sobres: List<SobreEntity> = emptyList(),
    val saldoPorCuenta: Map<Long, Long> = emptyMap(),
    val saldoPorSobre: Map<Long, Long> = emptyMap(),
    val movimientos: List<MovimientoListItem> = emptyList(),
    val filtros: Filtros = Filtros(),
    val pendientesCuadre: Int = 0
)

sealed interface BalanceEvento {
    data class CompartirCsv(val uri: Uri) : BalanceEvento
}

@HiltViewModel
class BalanceViewModel @Inject constructor(
    private val balanceRepository: BalanceRepository,
    private val movimientoRepository: MovimientoRepository,
    private val csvExporter: CsvExporter
) : ViewModel() {

    private val _filtros = MutableStateFlow(Filtros())

    private val _eventos = Channel<BalanceEvento>()
    val eventos = _eventos.receiveAsFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState: StateFlow<BalanceUiState> = combine(
        balanceRepository.observarBalance(),
        _filtros,
        _filtros.flatMapLatest { f ->
            movimientoRepository.listadoFiltrado(f.cuentaId, f.sobreId, f.fechaDesde, f.fechaHasta)
        },
        movimientoRepository.contarPendientesCuadre()
    ) { balance: BalanceCompleto, filtros, movimientos, pendientes ->
        BalanceUiState(
            cuentas = balance.cuentas,
            sobres = balance.sobres,
            saldoPorCuenta = balance.saldoPorCuenta,
            saldoPorSobre = balance.saldoPorSobre,
            movimientos = movimientos,
            filtros = filtros,
            pendientesCuadre = pendientes
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BalanceUiState())

    fun filtrarPorCuenta(cuentaId: Long?) {
        _filtros.value = _filtros.value.copy(cuentaId = cuentaId, sobreId = null)
    }

    fun filtrarPorSobre(sobreId: Long?) {
        _filtros.value = _filtros.value.copy(sobreId = sobreId)
    }

    fun filtrarPorFechas(desde: LocalDate?, hasta: LocalDate?) {
        _filtros.value = _filtros.value.copy(fechaDesde = desde, fechaHasta = hasta)
    }

    fun limpiarFiltros() {
        _filtros.value = Filtros()
    }

    fun exportarCsv() = viewModelScope.launch {
        val f = _filtros.value
        val filas = movimientoRepository.listadoFiltradoSnapshot(f.cuentaId, f.sobreId, f.fechaDesde, f.fechaHasta)
        val uri = csvExporter.exportar(filas)
        _eventos.send(BalanceEvento.CompartirCsv(uri))
    }
}
