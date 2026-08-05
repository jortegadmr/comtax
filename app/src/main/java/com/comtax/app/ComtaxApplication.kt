package com.comtax.app

import android.app.Application
import androidx.lifecycle.ProcessLifecycleOwner
import com.comtax.app.util.AppLockController
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class ComtaxApplication : Application() {

    @Inject lateinit var appLockController: AppLockController

    override fun onCreate() {
        super.onCreate()
        ProcessLifecycleOwner.get().lifecycle.addObserver(appLockController)
    }
}
