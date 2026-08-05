package com.comtax.app.data.repository

import androidx.room.withTransaction
import com.comtax.app.data.local.ComtaxDatabase
import com.comtax.app.data.local.dao.MovimientoDao
import com.comtax.app.data.local.dao.SobreDao
import com.comtax.app.data.local.entity.EstadoSobre
import com.comtax.app.data.local.entity.SobreEntity
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import javax.inject.Inject

class SobreRepository @Inject constructor(
    private val sobreDao: SobreDao,
    private val movimientoDao: MovimientoDao,
    private val db: ComtaxDatabase
) {
    fun observarTodos(): Flow<List<SobreEntity>> = sobreDao.observarTodos()

    fun observarActivosDeCuenta(cuentaId: Long): Flow<List<SobreEntity>> =
        sobreDao.observarActivosDeCuenta(cuentaId)

    fun observarTodosDeCuenta(cuentaId: Long): Flow<List<SobreEntity>> =
        sobreDao.observarTodosDeCuenta(cuentaId)

    suspend fun obtenerPorId(id: Long): SobreEntity? = sobreDao.obtenerPorId(id)

    suspend fun crear(cuentaId: Long, nombre: String): Long =
        sobreDao.insertar(SobreEntity(cuentaId = cuentaId, nombre = nombre))

    suspend fun renombrar(id: Long, nombre: String) = sobreDao.renombrar(id, nombre)

    suspend fun archivar(id: Long) = sobreDao.cambiarEstado(id, EstadoSobre.ARCHIVADO)

    suspend fun desarchivar(id: Long) = sobreDao.cambiarEstado(id, EstadoSobre.ACTIVO)

    /**
     * Fusiona [origenId] en [destinoId]: reasigna todo el historico de movimientos del
     * sobre origen al destino y archiva el origen. El saldo nunca se almacena (siempre
     * se calcula por SUM), asi que el destino "hereda" el saldo del origen sin tocar
     * ningun campo numerico y sin dejar historico huerfano.
     */
    suspend fun fusionarSobres(origenId: Long, destinoId: Long) {
        require(origenId != destinoId) { "No se puede fusionar un sobre consigo mismo" }
        val origen = sobreDao.obtenerPorId(origenId) ?: error("Sobre origen no existe")
        val destino = sobreDao.obtenerPorId(destinoId) ?: error("Sobre destino no existe")
        require(origen.cuentaId == destino.cuentaId) { "Solo se pueden fusionar sobres de la misma cuenta" }

        db.withTransaction {
            movimientoDao.reasignarSobre(origenId, destinoId, Instant.now())
            sobreDao.cambiarEstado(origenId, EstadoSobre.ARCHIVADO)
        }
    }
}
