package com.comtax.app.ui.navigation

import androidx.lifecycle.ViewModel
import com.comtax.app.data.repository.CuentaRepository
import com.comtax.app.data.security.PinManager
import com.comtax.app.util.AppLockController
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class ComtaxNavHostViewModel @Inject constructor(
    private val pinManager: PinManager,
    private val cuentaRepository: CuentaRepository,
    val appLockController: AppLockController
) : ViewModel() {

    fun tienePinConfigurado(): Boolean = pinManager.tienePinConfigurado()

    suspend fun hayCuentas(): Boolean = cuentaRepository.contarCuentas() > 0
}
