package com.comtax.app.util

import java.math.BigDecimal
import java.math.RoundingMode
import java.text.DecimalFormatSymbols
import java.util.Locale

private val LOCALE_ES = Locale.forLanguageTag("es-ES")

fun centimosATexto(centimos: Long): String {
    val simbolos = DecimalFormatSymbols(LOCALE_ES)
    val euros = BigDecimal(centimos).movePointLeft(2).setScale(2, RoundingMode.HALF_UP)
    return euros.toPlainString().replace('.', simbolos.decimalSeparator)
}

/**
 * Acepta tanto coma como punto decimal (teclado numerico del sistema varia por dispositivo).
 * Devuelve null si el texto no representa un importe positivo valido.
 */
fun textoACentimos(texto: String): Long? {
    val normalizado = texto.trim().replace(',', '.')
    if (normalizado.isEmpty()) return null
    val valor = normalizado.toBigDecimalOrNull() ?: return null
    if (valor <= BigDecimal.ZERO) return null
    return valor.movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact()
}

private fun String.toBigDecimalOrNull(): BigDecimal? = try {
    BigDecimal(this)
} catch (e: NumberFormatException) {
    null
}
