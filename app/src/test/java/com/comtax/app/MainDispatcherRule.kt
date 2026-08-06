package com.comtax.app

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

// viewModelScope usa Dispatchers.Main; esta regla lo sustituye por un dispatcher de test.
// Dispatchers.Unconfined (real, no un TestDispatcher de tiempo virtual) para que las
// corrutinas del ViewModel avancen solas y sin interferir con el tiempo real que
// necesitan las consultas Room/PBKDF2, que corren en hilos reales.
@OptIn(ExperimentalCoroutinesApi::class)
class MainDispatcherRule(
    private val dispatcher: CoroutineDispatcher = Dispatchers.Unconfined
) : TestWatcher() {

    override fun starting(description: Description) {
        Dispatchers.setMain(dispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }
}
