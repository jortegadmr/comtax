package com.comtax.app.ui.balance

import androidx.lifecycle.SavedStateHandle
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.comtax.app.MainDispatcherRule
import com.comtax.app.data.export.CsvExporter
import com.comtax.app.data.local.ComtaxDatabase
import com.comtax.app.data.local.entity.CuentaEntity
import com.comtax.app.data.local.entity.SobreEntity
import com.comtax.app.data.local.entity.TipoMovimiento
import com.comtax.app.data.repository.BalanceRepository
import com.comtax.app.data.repository.MovimientoRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
class BalanceCuentaViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var db: ComtaxDatabase
    private lateinit var balanceRepository: BalanceRepository
    private lateinit var movimientoRepository: MovimientoRepository
    private var cuentaId: Long = 0
    private var sobreId: Long = 0

    @Before
    fun preparar(): Unit = runBlocking {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), ComtaxDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        balanceRepository = BalanceRepository(db.cuentaDao(), db.sobreDao(), db.movimientoDao())
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

    private fun crearViewModel(): BalanceCuentaViewModel = BalanceCuentaViewModel(
        savedStateHandle = SavedStateHandle(mapOf("cuentaId" to cuentaId)),
        balanceRepository = balanceRepository,
        movimientoRepository = movimientoRepository,
        csvExporter = CsvExporter(ApplicationProvider.getApplicationContext())
    )

    @Test
    fun `carga el saldo y el historial de la cuenta`() = runBlocking {
        val viewModel = crearViewModel()

        val estado = withTimeout(5_000) { viewModel.uiState.first { it.cuenta != null } }

        assertEquals("Guadiamar", estado.cuenta?.nombre)
        assertEquals(1000L, estado.saldoCuenta)
    }

    @Test
    fun `seleccionarSobre alterna la seleccion`() = runBlocking {
        val viewModel = crearViewModel()
        withTimeout(5_000) { viewModel.uiState.first { it.cuenta != null } }

        viewModel.seleccionarSobre(sobreId)
        val conSeleccion = withTimeout(5_000) {
            viewModel.uiState.first { it.sobreSeleccionadoId == sobreId }
        }
        assertEquals(sobreId, conSeleccion.sobreSeleccionadoId)

        viewModel.seleccionarSobre(sobreId)
        val sinSeleccion = withTimeout(5_000) {
            viewModel.uiState.first { it.sobreSeleccionadoId == null }
        }
        assertNull(sinSeleccion.sobreSeleccionadoId)
    }

    @Test
    fun `filtrarPorFechas y limpiarFiltroFechas actualizan el rango`() = runBlocking {
        val viewModel = crearViewModel()
        withTimeout(5_000) { viewModel.uiState.first { it.cuenta != null } }

        val desde = LocalDate.of(2026, 1, 1)
        val hasta = LocalDate.of(2026, 1, 31)
        viewModel.filtrarPorFechas(desde, hasta)
        val conFiltro = withTimeout(5_000) {
            viewModel.uiState.first { it.rangoFechas.desde == desde }
        }
        assertEquals(desde, conFiltro.rangoFechas.desde)
        assertEquals(hasta, conFiltro.rangoFechas.hasta)

        viewModel.limpiarFiltroFechas()
        val sinFiltro = withTimeout(5_000) {
            viewModel.uiState.first { it.rangoFechas.desde == null }
        }
        assertNull(sinFiltro.rangoFechas.desde)
        assertNull(sinFiltro.rangoFechas.hasta)
    }
}
