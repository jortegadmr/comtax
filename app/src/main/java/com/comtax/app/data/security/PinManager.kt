package com.comtax.app.data.security

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PinManager @Inject constructor(
    @ApplicationContext context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val prefs = EncryptedSharedPreferences.create(
        context,
        NOMBRE_ARCHIVO,
        masterKey,
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )

    fun tienePinConfigurado(): Boolean =
        prefs.contains(CLAVE_SALT) && prefs.contains(CLAVE_HASH)

    fun configurarPin(pin: String) {
        val salt = PinHasher.generarSalt()
        val hash = PinHasher.hash(pin, salt)
        prefs.edit()
            .putString(CLAVE_SALT, Base64.encodeToString(salt, Base64.NO_WRAP))
            .putString(CLAVE_HASH, Base64.encodeToString(hash, Base64.NO_WRAP))
            .apply()
    }

    fun verificarPin(pin: String): Boolean {
        val saltTexto = prefs.getString(CLAVE_SALT, null) ?: return false
        val hashTexto = prefs.getString(CLAVE_HASH, null) ?: return false
        val salt = Base64.decode(saltTexto, Base64.NO_WRAP)
        val hashEsperado = Base64.decode(hashTexto, Base64.NO_WRAP)
        return PinHasher.coincide(pin, salt, hashEsperado)
    }

    fun cambiarPin(pinActual: String, pinNuevo: String): Boolean {
        if (!verificarPin(pinActual)) return false
        configurarPin(pinNuevo)
        return true
    }

    companion object {
        private const val NOMBRE_ARCHIVO = "comtax_secure_prefs"
        private const val CLAVE_SALT = "pin_salt"
        private const val CLAVE_HASH = "pin_hash"
    }
}
