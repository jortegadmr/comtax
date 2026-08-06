package com.comtax.app.ui.balance

import androidx.lifecycle.SavedStateHandle
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.comtax.app.MainDispatcherRule
import com.comtax.app.data.local.ComtaxDatabase
import com.comtax.app.data.local.entity.CuentaEntity
import com.comtax.app.data.local.entity.SobreEntity
import com.comtax.app.data.local.entity.TipoMovimiento
import com.comtax.app.data.repository.MovimientoRepository
import com.comtax.app.data.repository.SobreRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
class MovimientoDetalleViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var db: ComtaxDatabase
    private lateinit var movimientoRepository: MovimientoRepository
    private lateinit var sobreRepository: SobreRepository
    private var cuentaId: Long = 0
    private var sobreId: Long = 0
    private var movimientoId: Long = 0

    @Before
    fun preparar() = runBlocking {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), ComtaxDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        movimientoRepository = MovimientoRepository(db.movimientoDao())
        sobreRepository = SobreRepository(db.sobreDao(), db.movimientoDao(), db)

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
        movimientoId = movimientoRepository.registrar(
            tipo = TipoMovimiento.GASTO,
            cuentaId = cuentaId,
            sobreId = sobreId,
            importeCentimos = 300,
            concepto = "Gasto original",
            fecha = LocalDate.of(2026, 1, 2),
            pendienteCuadre = false
        )
    }

    @After
    fun cerrar() {
        db.close()
    }

    private suspend fun crearViewModelCargado(): MovimientoDetalleViewModel {
        val viewModel = MovimientoDetalleViewModel(
            savedStateHandle = SavedStateHandle(mapOf("id" to movimientoId)),
            movimientoRepository = movimientoRepository,
            sobreRepository = sobreRepository
        )
        withTimeout(5_000) { viewModel.uiState.first { !it.cargando && it.sobreSeleccionado != null } }
        return viewModel
    }

    @Test
    fun `carga el movimiento existente`() = runBlocking {
        val viewModel = crearViewModelCargado()

        assertEquals("Gasto original", viewModel.uiState.value.concepto)
    }

    @Test
    fun `onGuardarClick detecta aviso negativo en la primera pulsacion y no guarda todavia`() = runBlocking {
        val viewModel = crearViewModelCargado()
        // Sobre A: +1000 -300 = 700. Editar el gasto a 1500 lo dejaria en -500.
        viewModel.onImporteCambia("15,00")

        viewModel.onGuardarClick()
        val estadoConAviso = withTimeout(5_000) {
            viewModel.uiState.first { it.avisoNegativo != null }
        }

        assertNotNull(estadoConAviso.avisoNegativo)
        assertEquals(-500L, estadoConAviso.avisoNegativo?.saldoResultanteSobreCentimos)
        val movimientoSinCambios = movimientoRepository.obtenerPorId(movimientoId)!!
        assertEquals(300L, movimientoSinCambios.importeCentimos)
    }

    @Test
    fun `onGuardarClick en la segunda pulsacion guarda el cambio`() = runBlocking {
        val viewModel = crearViewModelCargado()
        viewModel.onImporteCambia("15,00")
        viewModel.onGuardarClick()
        withTimeout(5_000) { viewModel.uiState.first { it.avisoNegativo != null } }

        viewModel.onGuardarClick()
        withTimeout(5_000) { viewModel.eventos.first() }

        val movimientoActualizado = movimientoRepository.obtenerPorId(movimientoId)!!
        assertEquals(1500L, movimientoActualizado.importeCentimos)
        assertEquals(true, movimientoActualizado.pendienteCuadre)
    }

    @Test
    fun `anular marca el movimiento como anulado y emite Anulado`() = runBlocking {
        val viewModel = crearViewModelCargado()

        viewModel.anular()
        withTimeout(5_000) { viewModel.eventos.first() }

        val movimientoAnulado = movimientoRepository.obtenerPorId(movimientoId)!!
        assertEquals(true, movimientoAnulado.anulado)
    }
}
