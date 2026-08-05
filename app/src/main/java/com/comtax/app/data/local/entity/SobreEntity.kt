package com.comtax.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

enum class EstadoSobre { ACTIVO, ARCHIVADO }

@Entity(
    tableName = "sobres",
    foreignKeys = [
        ForeignKey(
            entity = CuentaEntity::class,
            parentColumns = ["id"],
            childColumns = ["cuentaId"]
        )
    ],
    indices = [Index("cuentaId")]
)
data class SobreEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val cuentaId: Long,
    val nombre: String,
    val estado: EstadoSobre = EstadoSobre.ACTIVO
)
