package com.comtax.app.data.security

import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PinHasherTest {

    @Test
    fun `coincide es verdadero con el mismo pin y sal`() {
        val salt = PinHasher.generarSalt()
        val hash = PinHasher.hash("1234", salt)
        assertTrue(PinHasher.coincide("1234", salt, hash))
    }

    @Test
    fun `coincide es falso con un pin distinto`() {
        val salt = PinHasher.generarSalt()
        val hash = PinHasher.hash("1234", salt)
        assertFalse(PinHasher.coincide("9999", salt, hash))
    }

    @Test
    fun `cada sal generada es distinta`() {
        val salt1 = PinHasher.generarSalt()
        val salt2 = PinHasher.generarSalt()
        assertNotEquals(salt1.toList(), salt2.toList())
    }

    @Test
    fun `el mismo pin con distinta sal produce hashes distintos`() {
        val hash1 = PinHasher.hash("1234", PinHasher.generarSalt())
        val hash2 = PinHasher.hash("1234", PinHasher.generarSalt())
        assertNotEquals(hash1.toList(), hash2.toList())
    }
}
