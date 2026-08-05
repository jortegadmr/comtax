package com.comtax.app.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.comtax.app.data.local.ComtaxDatabase
import com.comtax.app.data.local.entity.CuentaEntity
import com.comtax.app.data.local.entity.SobreEntity
import com.comtax.app.data.local.entity.TipoMovimiento
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
class MovimientoRepositoryTest {

    private lateinit var db: ComtaxDatabase
    private lateinit var movimientoRepository: MovimientoRepository
    private var cuentaId: Long = 0
    private var sobreId: Long = 0

    @Before
    fun preparar(): Unit = runBlocking {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), ComtaxDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        movimientoRepository = MovimientoRepository(db.movimientoDao())

        cuentaId = db.cuentaDao().insertar(CuentaEntity(nombre = "Guadiamar"))
        sobreId = db.sobreDao().insertar(SobreEntity(cuentaId = cuentaId, nombre = "Sobre A"))
        movimientoRepository.registrar(
            tipo = TipoMovimiento.INGRESO,
            cuentaId = cuentaId,
            sobreId = sobreId,
            importeCentimos = 1000,
            concepto = "Ingreso inicial",
            fecha = LocalDate.of(2026, 1, 1),
            pendienteCuadre = false
        )
    }

    @After
    fun cerrar() {
        db.close()
    }

    @Test
    fun `editar un gasto que deja el sobre en negativo genera aviso`() = runBlocking {
        val id = movimientoRepository.registrar(
            tipo = TipoMovimiento.GASTO,
            cuentaId = cuentaId,
            sobreId = sobreId,
            importeCentimos = 300,
            concepto = "Gasto original",
            fecha = LocalDate.of(2026, 1, 2),
            pendienteCuadre = false
        )
        val movimiento = movimientoRepository.obtenerPorId(id)!!

        // Sobre A: +1000 -300 = 700. Editar el gasto a 1500 lo dejaria en -500.
        val aviso = movimientoRepository.calcularAvisoNegativo(
            tipo = movimiento.tipo,
            cuentaId = cuentaId,
            sobreId = sobreId,
            importeCentimos = 1500,
            movimientoOriginal = movimiento
        )

        assertEquals(true, aviso?.sobreQuedaNegativo)
        assertEquals(-500L, aviso?.saldoResultanteSobreCentimos)
    }

    @Test
    fun `editar sin dejar el sobre en negativo no genera aviso`() = runBlocking {
        val id = movimientoRepository.registrar(
            tipo = TipoMovimiento.GASTO,
            cuentaId = cuentaId,
            sobreId = sobreId,
            importeCentimos = 300,
            concepto = "Gasto original",
            fecha = LocalDate.of(2026, 1, 2),
            pendienteCuadre = false
        )
        val movimiento = movimientoRepository.obtenerPorId(id)!!

        // Sobre A: +1000 -300 = 700. Editar el gasto a 400 deja el sobre en 600, sigue positivo.
        val aviso = movimientoRepository.calcularAvisoNegativo(
            tipo = movimiento.tipo,
            cuentaId = cuentaId,
            sobreId = sobreId,
            importeCentimos = 400,
            movimientoOriginal = movimiento
        )

        assertNull(aviso)
    }
}
