package com.comtax.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.comtax.app.data.local.dao.CuentaDao
import com.comtax.app.data.local.dao.MovimientoDao
import com.comtax.app.data.local.dao.SobreDao
import com.comtax.app.data.local.entity.CuentaEntity
import com.comtax.app.data.local.entity.MovimientoEntity
import com.comtax.app.data.local.entity.SobreEntity

@Database(
    entities = [CuentaEntity::class, SobreEntity::class, MovimientoEntity::class],
    version = 1,
    exportSchema = true
)
@TypeConverters(Converters::class)
abstract class ComtaxDatabase : RoomDatabase() {
    abstract fun cuentaDao(): CuentaDao
    abstract fun sobreDao(): SobreDao
    abstract fun movimientoDao(): MovimientoDao

    companion object {
        const val NOMBRE_ARCHIVO = "comtax.db"
    }
}
