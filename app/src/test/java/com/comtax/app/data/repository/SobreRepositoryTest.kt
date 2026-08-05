package com.comtax.app.data.repository

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.comtax.app.data.local.ComtaxDatabase
import com.comtax.app.data.local.entity.CuentaEntity
import com.comtax.app.data.local.entity.EstadoSobre
import com.comtax.app.data.local.entity.MovimientoEntity
import com.comtax.app.data.local.entity.SobreEntity
import com.comtax.app.data.local.entity.TipoMovimiento
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.Instant
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
class SobreRepositoryTest {

    private lateinit var db: ComtaxDatabase
    private lateinit var sobreRepository: SobreRepository
    private var cuentaAId: Long = 0
    private var cuentaBId: Long = 0
    private var sobreOrigenId: Long = 0
    private var sobreDestinoId: Long = 0

    @Before
    fun preparar(): Unit = runBlocking {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), ComtaxDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        sobreRepository = SobreRepository(db.sobreDao(), db.movimientoDao(), db)

        cuentaAId = db.cuentaDao().insertar(CuentaEntity(nombre = "Cuenta A"))
        cuentaBId = db.cuentaDao().insertar(CuentaEntity(nombre = "Cuenta B"))
        sobreOrigenId = db.sobreDao().insertar(SobreEntity(cuentaId = cuentaAId, nombre = "Origen"))
        sobreDestinoId = db.sobreDao().insertar(SobreEntity(cuentaId = cuentaAId, nombre = "Destino"))

        db.movimientoDao().insertar(
            MovimientoEntity(
                tipo = TipoMovimiento.INGRESO,
                cuentaId = cuentaAId,
                sobreId = sobreOrigenId,
                importeCentimos = 1000,
                concepto = "Ingreso en origen",
                fecha = LocalDate.of(2026, 1, 1),
                fechaCreacion = Instant.now(),
                fechaUltimaEdicion = Instant.now()
            )
        )
        db.movimientoDao().insertar(
            MovimientoEntity(
                tipo = TipoMovimiento.INGRESO,
                cuentaId = cuentaAId,
                sobreId = sobreDestinoId,
                importeCentimos = 500,
                concepto = "Ingreso en destino",
                fecha = LocalDate.of(2026, 1, 2),
                fechaCreacion = Instant.now(),
                fechaUltimaEdicion = Instant.now()
            )
        )
    }

    @After
    fun cerrar() {
        db.close()
    }

    @Test
    fun `fusionar reasigna el historico y suma los saldos sin dejarlo huerfano`() = runBlocking {
        sobreRepository.fusionarSobres(sobreOrigenId, sobreDestinoId)

        assertEquals(1500L, db.movimientoDao().saldoActualSobre(sobreDestinoId))
        assertEquals(0L, db.movimientoDao().saldoActualSobre(sobreOrigenId))
    }

    @Test
    fun `fusionar archiva el sobre origen`() = runBlocking {
        sobreRepository.fusionarSobres(sobreOrigenId, sobreDestinoId)

        val origen = db.sobreDao().obtenerPorId(sobreOrigenId)
        assertEquals(EstadoSobre.ARCHIVADO, origen?.estado)
    }

    @Test
    fun `fusionar rechaza sobres de cuentas distintas`() {
        val sobreDeOtraCuenta = runBlocking {
            db.sobreDao().insertar(SobreEntity(cuentaId = cuentaBId, nombre = "De otra cuenta"))
        }

        assertThrows(IllegalArgumentException::class.java) {
            runBlocking { sobreRepository.fusionarSobres(sobreOrigenId, sobreDeOtraCuenta) }
        }
    }
}
