package com.comtax.app.di

import com.comtax.app.data.security.PinManager
import com.comtax.app.data.security.PinManagerImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SecurityModule {

    @Binds
    @Singleton
    abstract fun bindPinManager(impl: PinManagerImpl): PinManager
}
