package com.comtax.app.data.security

import android.content.Context
import android.util.Base64
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

// Interfaz para poder sustituir la implementacion (EncryptedSharedPreferences/Android
// Keystore, no fiable bajo Robolectric) por un fake en tests de ViewModel.
interface PinManager {
    fun tienePinConfigurado(): Boolean
    suspend fun configurarPin(pin: String)
    suspend fun verificarPin(pin: String): Boolean
    suspend fun cambiarPin(pinActual: String, pinNuevo: String): Boolean
}

@Singleton
class PinManagerImpl @Inject constructor(
    @ApplicationContext context: Context
) : PinManager {
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

    override fun tienePinConfigurado(): Boolean =
        prefs.contains(CLAVE_SALT) && prefs.contains(CLAVE_HASH)

    // PBKDF2 (120.000 iteraciones) tarda decenas de ms: se calcula fuera del hilo principal
    // para no congelar la UI mientras se escribe o cambia el PIN.
    override suspend fun configurarPin(pin: String) = withContext(Dispatchers.Default) {
        val salt = PinHasher.generarSalt()
        val hash = PinHasher.hash(pin, salt)
        prefs.edit()
            .putString(CLAVE_SALT, Base64.encodeToString(salt, Base64.NO_WRAP))
            .putString(CLAVE_HASH, Base64.encodeToString(hash, Base64.NO_WRAP))
            .apply()
    }

    override suspend fun verificarPin(pin: String): Boolean = withContext(Dispatchers.Default) {
        val saltTexto = prefs.getString(CLAVE_SALT, null) ?: return@withContext false
        val hashTexto = prefs.getString(CLAVE_HASH, null) ?: return@withContext false
        val salt = Base64.decode(saltTexto, Base64.NO_WRAP)
        val hashEsperado = Base64.decode(hashTexto, Base64.NO_WRAP)
        PinHasher.coincide(pin, salt, hashEsperado)
    }

    override suspend fun cambiarPin(pinActual: String, pinNuevo: String): Boolean {
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
