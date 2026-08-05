package com.comtax.app.data.local.query

import com.comtax.app.data.local.entity.TipoMovimiento
import java.time.LocalDate

data class MovimientoListItem(
    val id: Long,
    val tipo: TipoMovimiento,
    val importeCentimos: Long,
    val concepto: String,
    val fecha: LocalDate,
    val pendienteCuadre: Boolean,
    val cuentaNombre: String,
    val sobreNombre: String?
)
