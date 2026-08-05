package com.comtax.app.ui.balance

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.comtax.app.data.export.CsvExporter
import com.comtax.app.data.local.entity.CuentaEntity
import com.comtax.app.data.repository.BalanceRepository
import com.comtax.app.data.repository.MovimientoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

data class BalanceUiState(
    val cuentas: List<CuentaEntity> = emptyList(),
    val saldoPorCuenta: Map<Long, Long> = emptyMap(),
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

    private val _eventos = Channel<BalanceEvento>()
    val eventos = _eventos.receiveAsFlow()

    val uiState: StateFlow<BalanceUiState> = combine(
        balanceRepository.observarBalance(),
        movimientoRepository.contarPendientesCuadre()
    ) { balance, pendientes ->
        BalanceUiState(
            cuentas = balance.cuentas,
            saldoPorCuenta = balance.saldoPorCuenta,
            pendientesCuadre = pendientes
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), BalanceUiState())

    fun exportarTodoCsv() = viewModelScope.launch {
        val filas = movimientoRepository.listadoFiltradoSnapshot(null, null, null, null)
        val uri = csvExporter.exportar(filas)
        _eventos.send(BalanceEvento.CompartirCsv(uri))
    }
}
