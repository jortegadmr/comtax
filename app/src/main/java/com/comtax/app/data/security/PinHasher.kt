package com.comtax.app.data.security

import java.security.SecureRandom
import java.security.spec.KeySpec
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

object PinHasher {
    private const val ALGORITMO = "PBKDF2WithHmacSHA256"
    private const val ITERACIONES = 120_000
    private const val LONGITUD_CLAVE_BITS = 256

    fun generarSalt(): ByteArray = ByteArray(16).also { SecureRandom().nextBytes(it) }

    fun hash(pin: String, salt: ByteArray): ByteArray {
        val spec: KeySpec = PBEKeySpec(pin.toCharArray(), salt, ITERACIONES, LONGITUD_CLAVE_BITS)
        val factory = SecretKeyFactory.getInstance(ALGORITMO)
        return factory.generateSecret(spec).encoded
    }

    fun coincide(pin: String, salt: ByteArray, hashEsperado: ByteArray): Boolean =
        hash(pin, salt).contentEquals(hashEsperado)
}
