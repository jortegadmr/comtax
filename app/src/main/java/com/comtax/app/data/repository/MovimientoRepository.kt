package com.comtax.app.data.repository

import com.comtax.app.data.local.dao.MovimientoDao
import com.comtax.app.data.local.entity.MovimientoEntity
import com.comtax.app.data.local.entity.TipoMovimiento
import com.comtax.app.data.local.query.MovimientoListItem
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

class MovimientoRepository @Inject constructor(
    private val movimientoDao: MovimientoDao
) {
    suspend fun saldoActualCuenta(cuentaId: Long): Long = movimientoDao.saldoActualCuenta(cuentaId)

    suspend fun saldoActualSobre(sobreId: Long): Long = movimientoDao.saldoActualSobre(sobreId)

    suspend fun registrar(
        tipo: TipoMovimiento,
        cuentaId: Long,
        sobreId: Long?,
        importeCentimos: Long,
        concepto: String,
        fecha: LocalDate,
        pendienteCuadre: Boolean
    ): Long {
        val ahora = Instant.now()
        return movimientoDao.insertar(
            MovimientoEntity(
                tipo = tipo,
                cuentaId = cuentaId,
                sobreId = sobreId,
                importeCentimos = importeCentimos,
                concepto = concepto,
                fecha = fecha,
                fechaCreacion = ahora,
                fechaUltimaEdicion = ahora,
                pendienteCuadre = pendienteCuadre
            )
        )
    }

    fun listadoFiltrado(
        cuentaId: Long?,
        sobreId: Long?,
        fechaDesde: LocalDate?,
        fechaHasta: LocalDate?
    ): Flow<List<MovimientoListItem>> =
        movimientoDao.listadoFiltrado(cuentaId, sobreId, fechaDesde, fechaHasta)

    suspend fun listadoFiltradoSnapshot(
        cuentaId: Long?,
        sobreId: Long?,
        fechaDesde: LocalDate?,
        fechaHasta: LocalDate?
    ): List<MovimientoListItem> =
        movimientoDao.listadoFiltradoSnapshot(cuentaId, sobreId, fechaDesde, fechaHasta)

    fun contarPendientesCuadre(): Flow<Int> = movimientoDao.contarPendientesCuadre()

    suspend fun obtenerPorId(id: Long): MovimientoEntity? = movimientoDao.obtenerPorId(id)

    suspend fun editar(
        movimiento: MovimientoEntity,
        sobreId: Long?,
        importeCentimos: Long,
        concepto: String,
        fecha: LocalDate
    ) = movimientoDao.actualizar(
        movimiento.copy(
            sobreId = sobreId,
            importeCentimos = importeCentimos,
            concepto = concepto,
            fecha = fecha,
            fechaUltimaEdicion = Instant.now()
        )
    )

    suspend fun marcarCuadrado(id: Long) = movimientoDao.marcarCuadrado(id, Instant.now())

    suspend fun anular(id: Long) = movimientoDao.anular(id, Instant.now())
}
