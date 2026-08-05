package com.comtax.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.comtax.app.data.local.entity.CuentaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CuentaDao {

    @Insert
    suspend fun insertar(cuenta: CuentaEntity): Long

    @Update
    suspend fun actualizar(cuenta: CuentaEntity)

    @Query("SELECT * FROM cuentas ORDER BY nombre")
    fun observarTodas(): Flow<List<CuentaEntity>>

    @Query("SELECT * FROM cuentas WHERE archivada = 0 ORDER BY nombre")
    fun observarActivas(): Flow<List<CuentaEntity>>

    @Query("SELECT * FROM cuentas WHERE id = :id")
    suspend fun obtenerPorId(id: Long): CuentaEntity?

    @Query("SELECT COUNT(*) FROM cuentas")
    suspend fun contarCuentas(): Int

    @Query("UPDATE cuentas SET archivada = :archivada WHERE id = :id")
    suspend fun cambiarArchivada(id: Long, archivada: Boolean)

    @Query("UPDATE cuentas SET nombre = :nombre WHERE id = :id")
    suspend fun renombrar(id: Long, nombre: String)
}
