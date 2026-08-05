package com.comtax.app.di

import android.content.Context
import androidx.room.Room
import com.comtax.app.data.local.ComtaxDatabase
import com.comtax.app.data.local.MIGRATION_1_2
import com.comtax.app.data.local.dao.CuentaDao
import com.comtax.app.data.local.dao.MovimientoDao
import com.comtax.app.data.local.dao.SobreDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): ComtaxDatabase =
        Room.databaseBuilder(context, ComtaxDatabase::class.java, ComtaxDatabase.NOMBRE_ARCHIVO)
            .addMigrations(MIGRATION_1_2)
            .build()

    @Provides
    fun provideCuentaDao(db: ComtaxDatabase): CuentaDao = db.cuentaDao()

    @Provides
    fun provideSobreDao(db: ComtaxDatabase): SobreDao = db.sobreDao()

    @Provides
    fun provideMovimientoDao(db: ComtaxDatabase): MovimientoDao = db.movimientoDao()
}
