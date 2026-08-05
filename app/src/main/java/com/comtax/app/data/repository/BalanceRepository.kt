package com.comtax.app.data.repository

import com.comtax.app.data.local.dao.CuentaDao
import com.comtax.app.data.local.dao.MovimientoDao
import com.comtax.app.data.local.dao.SobreDao
import com.comtax.app.data.local.entity.CuentaEntity
import com.comtax.app.data.local.entity.SobreEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

data class BalanceCompleto(
    val cuentas: List<CuentaEntity>,
    val sobres: List<SobreEntity>,
    val saldoPorCuenta: Map<Long, Long>,
    val saldoPorSobre: Map<Long, Long>
)

class BalanceRepository @Inject constructor(
    private val cuentaDao: CuentaDao,
    private val sobreDao: SobreDao,
    private val movimientoDao: MovimientoDao
) {
    fun observarBalance(): Flow<BalanceCompleto> = combine(
        cuentaDao.observarTodas(),
        sobreDao.observarTodos(),
        movimientoDao.saldosPorCuenta(),
        movimientoDao.saldosPorSobre()
    ) { cuentas, sobres, saldosCuenta, saldosSobre ->
        BalanceCompleto(
            cuentas = cuentas,
            sobres = sobres,
            saldoPorCuenta = saldosCuenta.associate { it.cuentaId to it.saldo },
            saldoPorSobre = saldosSobre.associate { it.sobreId to it.saldo }
        )
    }
}
