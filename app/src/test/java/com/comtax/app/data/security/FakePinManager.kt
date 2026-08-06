package com.comtax.app.data.security

// Almacena el hash en memoria en vez de EncryptedSharedPreferences/Android Keystore
// (no fiable bajo Robolectric), pero reutiliza PinHasher real para que la
// verificacion sea representativa del comportamiento en produccion.
class FakePinManager : PinManager {
    private var salt: ByteArray? = null
    private var hash: ByteArray? = null

    override fun tienePinConfigurado(): Boolean = salt != null && hash != null

    override suspend fun configurarPin(pin: String) {
        val nuevoSalt = PinHasher.generarSalt()
        salt = nuevoSalt
        hash = PinHasher.hash(pin, nuevoSalt)
    }

    override suspend fun verificarPin(pin: String): Boolean {
        val saltActual = salt ?: return false
        val hashActual = hash ?: return false
        return PinHasher.coincide(pin, saltActual, hashActual)
    }

    override suspend fun cambiarPin(pinActual: String, pinNuevo: String): Boolean {
        if (!verificarPin(pinActual)) return false
        configurarPin(pinNuevo)
        return true
    }
}
