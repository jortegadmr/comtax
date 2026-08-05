package com.comtax.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import java.time.Instant
import java.time.LocalDate

@Serializable
enum class TipoMovimiento { INGRESO, GASTO }

@Entity(
    tableName = "movimientos",
    foreignKeys = [
        ForeignKey(entity = CuentaEntity::class, parentColumns = ["id"], childColumns = ["cuentaId"]),
        ForeignKey(entity = SobreEntity::class, parentColumns = ["id"], childColumns = ["sobreId"])
    ],
    indices = [Index("cuentaId"), Index("sobreId"), Index("fecha")]
)
data class MovimientoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val tipo: TipoMovimiento,
    val cuentaId: Long,
    val sobreId: Long?,
    val importeCentimos: Long,
    val concepto: String,
    val fecha: LocalDate,
    val fechaCreacion: Instant,
    val fechaUltimaEdicion: Instant,
    val anulado: Boolean = false,
    val pendienteCuadre: Boolean = false
)
