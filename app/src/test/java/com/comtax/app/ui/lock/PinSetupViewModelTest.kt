package com.comtax.app.ui.lock

import com.comtax.app.MainDispatcherRule
import com.comtax.app.data.security.FakePinManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PinSetupViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `continuarAConfirmacion con menos de 4 digitos muestra error`() {
        val viewModel = PinSetupViewModel(FakePinManager())

        viewModel.onPinCambia("12")
        viewModel.continuarAConfirmacion()

        assertEquals("El PIN debe tener al menos 4 dígitos", viewModel.uiState.value.error)
        assertEquals(false, viewModel.uiState.value.pidiendoConfirmacion)
    }

    @Test
    fun `continuarAConfirmacion con 4 o mas digitos pasa a pedir confirmacion`() {
        val viewModel = PinSetupViewModel(FakePinManager())

        viewModel.onPinCambia("1234")
        viewModel.continuarAConfirmacion()

        assertTrue(viewModel.uiState.value.pidiendoConfirmacion)
    }

    @Test
    fun `confirmarPin con PIN distinto muestra error y limpia confirmacion`() = runTest {
        val viewModel = PinSetupViewModel(FakePinManager())
        viewModel.onPinCambia("1234")
        viewModel.continuarAConfirmacion()
        viewModel.onConfirmacionCambia("4321")

        viewModel.confirmarPin()
        advanceUntilIdle()

        assertEquals("", viewModel.uiState.value.confirmacion)
        assertEquals("Los PIN no coinciden, inténtalo de nuevo", viewModel.uiState.value.error)
    }

    @Test
    fun `confirmarPin con PIN igual configura el pin y emite PinConfigurado`() = runTest {
        val pinManager = FakePinManager()
        val viewModel = PinSetupViewModel(pinManager)
        viewModel.onPinCambia("1234")
        viewModel.continuarAConfirmacion()
        viewModel.onConfirmacionCambia("1234")

        val eventos = mutableListOf<PinSetupEvento>()
        val job = launch { viewModel.eventos.collect { eventos.add(it) } }

        viewModel.confirmarPin()
        advanceUntilIdle()

        assertEquals(listOf(PinSetupEvento.PinConfigurado), eventos)
        assertTrue(pinManager.verificarPin("1234"))
        job.cancel()
    }
}
