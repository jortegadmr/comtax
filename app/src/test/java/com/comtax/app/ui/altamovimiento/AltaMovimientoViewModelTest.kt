package com.comtax.app.ui.altamovimiento

import androidx.lifecycle.SavedStateHandle
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.comtax.app.MainDispatcherRule
import com.comtax.app.data.local.ComtaxDatabase
import com.comtax.app.data.local.entity.CuentaEntity
import com.comtax.app.data.local.entity.SobreEntity
import com.comtax.app.data.local.entity.TipoMovimiento
import com.comtax.app.data.repository.CuentaRepository
import com.comtax.app.data.repository.MovimientoRepository
import com.comtax.app.data.repository.SobreRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AltaMovimientoViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var db: ComtaxDatabase
    private lateinit var cuentaRepository: CuentaRepository
    private lateinit var sobreRepository: SobreRepository
    private lateinit var movimientoRepository: MovimientoRepository
    private var cuentaId: Long = 0
    private var sobreId: Long = 0

    @Before
    fun preparar(): Unit = runBlocking {
        db = Room.inMemoryDatabaseBuilder(ApplicationProvider.getApplicationContext(), ComtaxDatabase::class.java)
            .allowMainThreadQueries()
            .build()
        cuentaRepository = CuentaRepository(db.cuentaDao())
        sobreRepository = SobreRepository(db.sobreDao(), db.movimientoDao(), db)
        movimientoRepository = MovimientoRepository(db.movimientoDao())

        cuentaId = db.cuentaDao().insertar(CuentaEntity(nombre = "Guadiamar"))
        sobreId = db.sobreDao().insertar(SobreEntity(cuentaId = cuentaId, nombre = "Sobre A"))
        movimientoRepository.registrar(
            tipo = TipoMovimiento.INGRESO,
            cuentaId = cuentaId,
            sobreId = sobreId,
            importeCentimos = 1000,
            concepto = "Ingreso inicial",
            fecha = java.time.LocalDate.of(2026, 1, 1),
            pendienteCuadre = false
        )
    }

    @After
    fun cerrar() {
        db.close()
    }

    private fun crearViewModel(tipo: TipoMovimiento) = AltaMovimientoViewModel(
        savedStateHandle = SavedStateHandle(mapOf("tipo" to tipo)),
        cuentaRepository = cuentaRepository,
        sobreRepository = sobreRepository,
        movimientoRepository = movimientoRepository
    )

    @Test
    fun `validarFormulario falla sin importe ni concepto`() = runBlocking {
        val viewModel = crearViewModel(TipoMovimiento.GASTO)

        val valido = viewModel.validarFormulario()

        assertEquals(false, valido)
        assertEquals("Introduce un importe válido", viewModel.uiState.value.errorFormulario)
    }

    @Test
    fun `validarFormulario falla con importe valido pero sin concepto`() = runBlocking {
        val viewModel = crearViewModel(TipoMovimiento.GASTO)
        viewModel.onImporteCambia("50")

        val valido = viewModel.validarFormulario()

        assertEquals(false, valido)
        assertEquals("El concepto es obligatorio", viewModel.uiState.value.errorFormulario)
    }

    @Test
    fun `validarFormulario pasa con importe y concepto`() = runBlocking {
        val viewModel = crearViewModel(TipoMovimiento.GASTO)
        viewModel.onImporteCambia("50")
        viewModel.onConceptoCambia("Compra")

        val valido = viewModel.validarFormulario()

        assertTrue(valido)
        assertNull(viewModel.uiState.value.errorFormulario)
    }

    @Test
    fun `prepararConfirmacion detecta aviso negativo`() = runBlocking {
        val viewModel = crearViewModel(TipoMovimiento.GASTO)
        viewModel.onCuentaSeleccionada(CuentaEntity(id = cuentaId, nombre = "Guadiamar"))
        viewModel.onSobreSeleccionado(SobreEntity(id = sobreId, cuentaId = cuentaId, nombre = "Sobre A"))
        // Sobre A: +1000. Un gasto de 1500 lo deja en -500.
        viewModel.onImporteCambia("15,00")

        viewModel.prepararConfirmacion()

        val aviso = viewModel.uiState.value.avisoNegativo
        assertNotNull(aviso)
        assertEquals(true, aviso?.sobreQuedaNegativo)
        assertEquals(-500L, aviso?.saldoResultanteSobreCentimos)
    }

    @Test
    fun `confirmar registra el movimiento y emite Guardado`() = runBlocking {
        val viewModel = crearViewModel(TipoMovimiento.GASTO)
        viewModel.onCuentaSeleccionada(CuentaEntity(id = cuentaId, nombre = "Guadiamar"))
        viewModel.onSobreSeleccionado(SobreEntity(id = sobreId, cuentaId = cuentaId, nombre = "Sobre A"))
        viewModel.onImporteCambia("3,00")
        viewModel.onConceptoCambia("Compra test")

        viewModel.confirmar()
        withTimeout(5_000) { viewModel.eventos.first() }

        val saldo = movimientoRepository.saldoActualSobre(sobreId)
        assertEquals(700L, saldo)
    }

    @Test
    fun `crearYSeleccionarSobre crea el sobre y lo deja seleccionado`() = runBlocking {
        val viewModel = crearViewModel(TipoMovimiento.INGRESO)
        viewModel.onCuentaSeleccionada(CuentaEntity(id = cuentaId, nombre = "Guadiamar"))

        viewModel.crearYSeleccionarSobre("Sobre B")

        val seleccionado = withTimeout(5_000) {
            viewModel.uiState.first { it.sobreSeleccionado?.nombre == "Sobre B" }.sobreSeleccionado
        }
        assertEquals(cuentaId, seleccionado?.cuentaId)

        val sobresActivos = withTimeout(5_000) {
            viewModel.uiState.first { it.sobresActivos.any { sobre -> sobre.nombre == "Sobre B" } }.sobresActivos
        }
        assertTrue(sobresActivos.any { it.nombre == "Sobre B" })
    }

    @Test
    fun `crearYSeleccionarSobre ignora nombre en blanco`() = runBlocking {
        val viewModel = crearViewModel(TipoMovimiento.INGRESO)
        viewModel.onCuentaSeleccionada(CuentaEntity(id = cuentaId, nombre = "Guadiamar"))
        val sobresAntes = viewModel.uiState.value.sobresActivos.size

        viewModel.crearYSeleccionarSobre("   ")

        assertNull(viewModel.uiState.value.sobreSeleccionado)
        assertEquals(sobresAntes, viewModel.uiState.value.sobresActivos.size)
    }
}
