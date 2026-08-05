package com.comtax.app.data.export

import android.content.Context
import androidx.core.content.FileProvider
import android.net.Uri
import com.comtax.app.data.local.entity.TipoMovimiento
import com.comtax.app.data.local.query.MovimientoListItem
import com.comtax.app.util.centimosATexto
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import javax.inject.Inject

class CsvExporter @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val formatoFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy")

    suspend fun exportar(movimientos: List<MovimientoListItem>): Uri = withContext(Dispatchers.IO) {
        val dir = File(context.cacheDir, "exports").apply { mkdirs() }
        val archivo = File(dir, "comtax_${LocalDate.now()}.csv")
        archivo.bufferedWriter().use { writer ->
            writer.appendLine("Fecha;Tipo;Cuenta;Sobre;Concepto;Importe (EUR);Pendiente de cuadre")
            movimientos.forEach { writer.appendLine(it.aFilaCsv()) }
        }
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", archivo)
    }

    private fun MovimientoListItem.aFilaCsv(): String {
        val tipoTexto = if (tipo == TipoMovimiento.INGRESO) "Ingreso" else "Gasto"
        return listOf(
            fecha.format(formatoFecha),
            tipoTexto,
            cuentaNombre,
            sobreNombre.orEmpty(),
            concepto,
            centimosATexto(importeCentimos),
            if (pendienteCuadre) "Si" else "No"
        ).joinToString(";") { escapar(it) }
    }

    private fun escapar(campo: String): String =
        if (campo.contains(';') || campo.contains('"') || campo.contains('\n')) {
            "\"${campo.replace("\"", "\"\"")}\""
        } else {
            campo
        }
}
