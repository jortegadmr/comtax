package com.comtax.app.ui.lock

import com.comtax.app.MainDispatcherRule
import com.comtax.app.data.security.FakePinManager
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class PinLockViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Test
    fun `pin correcto de 4 digitos emite Desbloqueado`() = runTest {
        val pinManager = FakePinManager().apply { configurarPin("1234") }
        val viewModel = PinLockViewModel(pinManager)
        val eventos = mutableListOf<PinLockEvento>()
        val job = launch { viewModel.eventos.collect { eventos.add(it) } }

        viewModel.onPinCambia("1234")
        advanceUntilIdle()

        assertEquals(listOf(PinLockEvento.Desbloqueado), eventos)
        job.cancel()
    }

    @Test
    fun `pin incorrecto de 6 digitos muestra error y limpia el campo`() = runTest {
        val pinManager = FakePinManager().apply { configurarPin("123456") }
        val viewModel = PinLockViewModel(pinManager)

        viewModel.onPinCambia("999999")
        advanceUntilIdle()

        assertEquals("", viewModel.uiState.value.pin)
        assertEquals("PIN incorrecto", viewModel.uiState.value.error)
    }

    @Test
    fun `pin incorrecto de menos de 6 digitos no muestra error todavia`() = runTest {
        val pinManager = FakePinManager().apply { configurarPin("123456") }
        val viewModel = PinLockViewModel(pinManager)

        viewModel.onPinCambia("999")
        advanceUntilIdle()

        assertNull(viewModel.uiState.value.error)
    }
}
