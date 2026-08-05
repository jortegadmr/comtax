package com.comtax.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.comtax.app.data.local.entity.EstadoSobre
import com.comtax.app.data.local.entity.SobreEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SobreDao {

    @Insert
    suspend fun insertar(sobre: SobreEntity): Long

    @Query("SELECT * FROM sobres ORDER BY nombre")
    fun observarTodos(): Flow<List<SobreEntity>>

    @Query("SELECT * FROM sobres WHERE cuentaId = :cuentaId AND estado = 'ACTIVO' ORDER BY nombre")
    fun observarActivosDeCuenta(cuentaId: Long): Flow<List<SobreEntity>>

    @Query("SELECT * FROM sobres WHERE id = :id")
    suspend fun obtenerPorId(id: Long): SobreEntity?

    @Query("UPDATE sobres SET estado = :estado WHERE id = :id")
    suspend fun cambiarEstado(id: Long, estado: EstadoSobre)

    @Query("UPDATE sobres SET nombre = :nombre WHERE id = :id")
    suspend fun renombrar(id: Long, nombre: String)
}
