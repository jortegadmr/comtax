package com.comtax.app.data.repository

import com.comtax.app.data.local.dao.MovimientoDao
import com.comtax.app.data.local.entity.MovimientoEntity
import com.comtax.app.data.local.entity.TipoMovimiento
import com.comtax.app.data.local.query.MovimientoListItem
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.LocalDate
import javax.inject.Inject

data class AvisoNegativo(
    val sobreQuedaNegativo: Boolean,
    val cuentaQuedaNegativa: Boolean,
    val saldoResultanteSobreCentimos: Long?,
    val saldoResultanteCuentaCentimos: Long
)

class MovimientoRepository @Inject constructor(
    private val movimientoDao: MovimientoDao
) {
    suspend fun saldoActualCuenta(cuentaId: Long): Long = movimientoDao.saldoActualCuenta(cuentaId)

    suspend fun saldoActualSobre(sobreId: Long): Long = movimientoDao.saldoActualSobre(sobreId)

    /**
     * Calcula si un gasto (alta o edicion) deja la cuenta o el sobre en negativo.
     * [movimientoOriginal] solo se pasa al editar un movimiento ya existente: su
     * aporte actual se resta primero del saldo leido (que ya lo incluye, porque
     * sigue persistido) para no contarlo dos veces al aplicar el importe nuevo.
     */
    suspend fun calcularAvisoNegativo(
        tipo: TipoMovimiento,
        cuentaId: Long,
        sobreId: Long?,
        importeCentimos: Long,
        movimientoOriginal: MovimientoEntity? = null
    ): AvisoNegativo? {
        if (tipo == TipoMovimiento.INGRESO) return null

        fun aporteOriginal(coincideCuentaOSobre: Boolean): Long = when {
            movimientoOriginal == null || !coincideCuentaOSobre -> 0L
            movimientoOriginal.tipo == TipoMovimiento.INGRESO -> movimientoOriginal.importeCentimos
            else -> -movimientoOriginal.importeCentimos
        }

        val saldoCuentaResultante = saldoActualCuenta(cuentaId) -
            aporteOriginal(movimientoOriginal?.cuentaId == cuentaId) - importeCentimos

        val saldoSobreResultante = sobreId?.let { id ->
            saldoActualSobre(id) - aporteOriginal(movimientoOriginal?.sobreId == id) - importeCentimos
        }

        val cuentaNegativa = saldoCuentaResultante < 0
        val sobreNegativo = saldoSobreResultante != null && saldoSobreResultante < 0

        return if (cuentaNegativa || sobreNegativo) {
            AvisoNegativo(
                sobreQuedaNegativo = sobreNegativo,
                cuentaQuedaNegativa = cuentaNegativa,
                saldoResultanteSobreCentimos = saldoSobreResultante,
                saldoResultanteCuentaCentimos = saldoCuentaResultante
            )
        } else null
    }

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
        fecha: LocalDate,
        pendienteCuadre: Boolean
    ) = movimientoDao.actualizar(
        movimiento.copy(
            sobreId = sobreId,
            importeCentimos = importeCentimos,
            concepto = concepto,
            fecha = fecha,
            pendienteCuadre = pendienteCuadre,
            fechaUltimaEdicion = Instant.now()
        )
    )

    suspend fun marcarCuadrado(id: Long) = movimientoDao.marcarCuadrado(id, Instant.now())

    suspend fun anular(id: Long) = movimientoDao.anular(id, Instant.now())
}
