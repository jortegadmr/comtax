package com.comtax.app.util

import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.time.Duration
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

// ON_STOP/ON_START de ProcessLifecycleOwner distinguen "app en segundo plano" de un
// simple cambio de pantalla interno, a diferencia de Activity.onPause/onResume.
@Singleton
class AppLockController @Inject constructor() : DefaultLifecycleObserver {

    private var instanteEnSegundoPlano: Instant? = null

    private val _debeBloquear = MutableStateFlow(false)
    val debeBloquear: StateFlow<Boolean> = _debeBloquear

    override fun onStop(owner: LifecycleOwner) {
        instanteEnSegundoPlano = Instant.now()
    }

    override fun onStart(owner: LifecycleOwner) {
        val entrada = instanteEnSegundoPlano ?: return
        if (Duration.between(entrada, Instant.now()) >= UMBRAL_INACTIVIDAD) {
            _debeBloquear.value = true
        }
        instanteEnSegundoPlano = null
    }

    fun notificarDesbloqueado() {
        _debeBloquear.value = false
    }

    companion object {
        private val UMBRAL_INACTIVIDAD = Duration.ofMinutes(2)
    }
}
