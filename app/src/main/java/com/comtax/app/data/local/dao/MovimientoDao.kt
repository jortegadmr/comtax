package com.comtax.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.comtax.app.data.local.entity.MovimientoEntity
import com.comtax.app.data.local.query.MovimientoListItem
import com.comtax.app.data.local.query.SaldoCuenta
import com.comtax.app.data.local.query.SaldoSobre
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import java.time.LocalDate

@Dao
interface MovimientoDao {

    @Insert
    suspend fun insertar(movimiento: MovimientoEntity): Long

    @Update
    suspend fun actualizar(movimiento: MovimientoEntity)

    @Query(
        """
        SELECT COALESCE(SUM(CASE WHEN tipo = 'INGRESO' THEN importeCentimos ELSE -importeCentimos END), 0)
        FROM movimientos WHERE cuentaId = :cuentaId AND anulado = 0
        """
    )
    suspend fun saldoActualCuenta(cuentaId: Long): Long

    @Query(
        """
        SELECT COALESCE(SUM(CASE WHEN tipo = 'INGRESO' THEN importeCentimos ELSE -importeCentimos END), 0)
        FROM movimientos WHERE sobreId = :sobreId AND anulado = 0
        """
    )
    suspend fun saldoActualSobre(sobreId: Long): Long

    @Query(
        """
        SELECT cuentaId, SUM(CASE WHEN tipo = 'INGRESO' THEN importeCentimos ELSE -importeCentimos END) AS saldo
        FROM movimientos WHERE anulado = 0 GROUP BY cuentaId
        """
    )
    fun saldosPorCuenta(): Flow<List<SaldoCuenta>>

    @Query(
        """
        SELECT sobreId, SUM(CASE WHEN tipo = 'INGRESO' THEN importeCentimos ELSE -importeCentimos END) AS saldo
        FROM movimientos WHERE anulado = 0 AND sobreId IS NOT NULL GROUP BY sobreId
        """
    )
    fun saldosPorSobre(): Flow<List<SaldoSobre>>

    @Query(
        """
        SELECT COUNT(*) FROM movimientos WHERE anulado = 0 AND pendienteCuadre = 1
        """
    )
    fun contarPendientesCuadre(): Flow<Int>

    @Query(
        """
        SELECT m.id, m.tipo, m.importeCentimos, m.concepto, m.fecha, m.pendienteCuadre,
               c.nombre AS cuentaNombre, s.nombre AS sobreNombre
        FROM movimientos m
        INNER JOIN cuentas c ON c.id = m.cuentaId
        LEFT JOIN sobres s ON s.id = m.sobreId
        WHERE m.anulado = 0
          AND (:cuentaId IS NULL OR m.cuentaId = :cuentaId)
          AND (:sobreId IS NULL OR m.sobreId = :sobreId)
          AND (:fechaDesde IS NULL OR m.fecha >= :fechaDesde)
          AND (:fechaHasta IS NULL OR m.fecha <= :fechaHasta)
        ORDER BY m.fecha DESC, m.id DESC
        """
    )
    fun listadoFiltrado(
        cuentaId: Long?,
        sobreId: Long?,
        fechaDesde: LocalDate?,
        fechaHasta: LocalDate?
    ): Flow<List<MovimientoListItem>>

    @Query(
        """
        SELECT m.id, m.tipo, m.importeCentimos, m.concepto, m.fecha, m.pendienteCuadre,
               c.nombre AS cuentaNombre, s.nombre AS sobreNombre
        FROM movimientos m
        INNER JOIN cuentas c ON c.id = m.cuentaId
        LEFT JOIN sobres s ON s.id = m.sobreId
        WHERE m.anulado = 0
          AND (:cuentaId IS NULL OR m.cuentaId = :cuentaId)
          AND (:sobreId IS NULL OR m.sobreId = :sobreId)
          AND (:fechaDesde IS NULL OR m.fecha >= :fechaDesde)
          AND (:fechaHasta IS NULL OR m.fecha <= :fechaHasta)
        ORDER BY m.fecha DESC, m.id DESC
        """
    )
    suspend fun listadoFiltradoSnapshot(
        cuentaId: Long?,
        sobreId: Long?,
        fechaDesde: LocalDate?,
        fechaHasta: LocalDate?
    ): List<MovimientoListItem>

    @Query("SELECT * FROM movimientos WHERE id = :id")
    suspend fun obtenerPorId(id: Long): MovimientoEntity?

    @Query("UPDATE movimientos SET sobreId = :destinoId, fechaUltimaEdicion = :ahora WHERE sobreId = :origenId AND anulado = 0")
    suspend fun reasignarSobre(origenId: Long, destinoId: Long, ahora: Instant): Int

    @Query("UPDATE movimientos SET pendienteCuadre = 0, fechaUltimaEdicion = :ahora WHERE id = :id")
    suspend fun marcarCuadrado(id: Long, ahora: Instant)

    @Query("UPDATE movimientos SET anulado = 1, fechaUltimaEdicion = :ahora WHERE id = :id")
    suspend fun anular(id: Long, ahora: Instant)
}
