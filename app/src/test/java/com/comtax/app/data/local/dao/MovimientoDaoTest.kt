package com.comtax.app.data.local.dao

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.comtax.app.data.local.ComtaxDatabase
import com.comtax.app.data.local.entity.CuentaEntity
import com.comtax.app.data.local.entity.MovimientoEntity
import com.comtax.app.data.local.entity.TipoMovimiento
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.Instant
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
class MovimientoDaoTest {

    private lateinit var db: ComtaxDatabase
    private lateinit var cuentaDao: CuentaDao
    private lateinit var movimientoDao: MovimientoDao
    private var cuentaId: Long = 0

    @Before
    fun crearBaseDeDatos() = runBlocking {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), ComtaxDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        cuentaDao = db.cuentaDao()
        movimientoDao = db.movimientoDao()
        cuentaId = cuentaDao.insertar(CuentaEntity(nombre = "Guadiamar"))
    }

    @After
    fun cerrarBaseDeDatos() {
        db.close()
    }

    private fun movimiento(tipo: TipoMovimiento, centimos: Long, anulado: Boolean = false) = MovimientoEntity(
        tipo = tipo,
        cuentaId = cuentaId,
        sobreId = null,
        importeCentimos = centimos,
        concepto = "Concepto",
        fecha = LocalDate.of(2026, 1, 1),
        fechaCreacion = Instant.now(),
        fechaUltimaEdicion = Instant.now(),
        anulado = anulado
    )

    @Test
    fun `saldoActualCuenta suma ingresos y resta gastos`() = runBlocking {
        movimientoDao.insertar(movimiento(TipoMovimiento.INGRESO, 1000))
        movimientoDao.insertar(movimiento(TipoMovimiento.GASTO, 300))

        assertEquals(700L, movimientoDao.saldoActualCuenta(cuentaId))
    }

    @Test
    fun `saldoActualCuenta ignora movimientos anulados`() = runBlocking {
        movimientoDao.insertar(movimiento(TipoMovimiento.INGRESO, 1000))
        movimientoDao.insertar(movimiento(TipoMovimiento.GASTO, 300, anulado = true))

        assertEquals(1000L, movimientoDao.saldoActualCuenta(cuentaId))
    }

    @Test
    fun `listadoFiltrado respeta el rango de fechas`() = runBlocking {
        movimientoDao.insertar(movimiento(TipoMovimiento.INGRESO, 100).copy(fecha = LocalDate.of(2026, 1, 1)))
        movimientoDao.insertar(movimiento(TipoMovimiento.INGRESO, 200).copy(fecha = LocalDate.of(2026, 2, 1)))
        movimientoDao.insertar(movimiento(TipoMovimiento.INGRESO, 300).copy(fecha = LocalDate.of(2026, 3, 1)))

        val resultado = movimientoDao.listadoFiltrado(
            cuentaId = null,
            sobreId = null,
            fechaDesde = LocalDate.of(2026, 1, 15),
            fechaHasta = LocalDate.of(2026, 2, 28)
        ).first()

        assertEquals(1, resultado.size)
        assertEquals(200L, resultado.first().importeCentimos)
    }

    @Test
    fun `listadoFiltrado excluye anulados`() = runBlocking {
        movimientoDao.insertar(movimiento(TipoMovimiento.GASTO, 100, anulado = true))
        movimientoDao.insertar(movimiento(TipoMovimiento.GASTO, 200))

        val resultado = movimientoDao.listadoFiltrado(null, null, null, null).first()

        assertEquals(1, resultado.size)
        assertEquals(200L, resultado.first().importeCentimos)
    }
}
