package com.comtax.app.data.repository

import com.comtax.app.data.local.dao.CuentaDao
import com.comtax.app.data.local.entity.CuentaEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CuentaRepository @Inject constructor(
    private val cuentaDao: CuentaDao
) {
    fun observarTodas(): Flow<List<CuentaEntity>> = cuentaDao.observarTodas()

    fun observarActivas(): Flow<List<CuentaEntity>> = cuentaDao.observarActivas()

    suspend fun obtenerPorId(id: Long): CuentaEntity? = cuentaDao.obtenerPorId(id)

    suspend fun contarCuentas(): Int = cuentaDao.contarCuentas()

    suspend fun crear(nombre: String, colorHex: String): Long =
        cuentaDao.insertar(CuentaEntity(nombre = nombre, colorHex = colorHex))

    suspend fun renombrar(id: Long, nombre: String) = cuentaDao.renombrar(id, nombre)

    suspend fun cambiarColor(id: Long, colorHex: String) = cuentaDao.cambiarColor(id, colorHex)

    suspend fun archivar(id: Long) = cuentaDao.cambiarArchivada(id, true)

    suspend fun desarchivar(id: Long) = cuentaDao.cambiarArchivada(id, false)
}
