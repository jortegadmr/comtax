package com.comtax.app.data.local

import androidx.room.TypeConverter
import com.comtax.app.data.local.entity.EstadoSobre
import com.comtax.app.data.local.entity.TipoMovimiento
import java.time.Instant
import java.time.LocalDate

class Converters {

    @TypeConverter
    fun fromEpochDay(value: Long?): LocalDate? = value?.let { LocalDate.ofEpochDay(it) }

    @TypeConverter
    fun localDateToEpochDay(date: LocalDate?): Long? = date?.toEpochDay()

    @TypeConverter
    fun fromEpochMilli(value: Long?): Instant? = value?.let { Instant.ofEpochMilli(it) }

    @TypeConverter
    fun instantToEpochMilli(instant: Instant?): Long? = instant?.toEpochMilli()

    @TypeConverter
    fun fromTipoMovimiento(value: TipoMovimiento?): String? = value?.name

    @TypeConverter
    fun toTipoMovimiento(value: String?): TipoMovimiento? = value?.let { TipoMovimiento.valueOf(it) }

    @TypeConverter
    fun fromEstadoSobre(value: EstadoSobre?): String? = value?.name

    @TypeConverter
    fun toEstadoSobre(value: String?): EstadoSobre? = value?.let { EstadoSobre.valueOf(it) }
}
