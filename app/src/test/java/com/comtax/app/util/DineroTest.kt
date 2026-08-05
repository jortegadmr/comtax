package com.comtax.app.util

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DineroTest {

    @Test
    fun `centimosATexto formatea con coma decimal`() {
        assertEquals("5,00", centimosATexto(500))
        assertEquals("1234,56", centimosATexto(123456))
        assertEquals("0,00", centimosATexto(0))
    }

    @Test
    fun `textoACentimos acepta coma y punto`() {
        assertEquals(500L, textoACentimos("5,00"))
        assertEquals(500L, textoACentimos("5.00"))
        assertEquals(150L, textoACentimos("1,5"))
    }

    @Test
    fun `textoACentimos rechaza importes no positivos o invalidos`() {
        assertNull(textoACentimos("0"))
        assertNull(textoACentimos("-5"))
        assertNull(textoACentimos(""))
        assertNull(textoACentimos("abc"))
    }

    @Test
    fun `textoACentimos redondea al centimo mas cercano`() {
        assertEquals(501L, textoACentimos("5,005"))
    }
}
