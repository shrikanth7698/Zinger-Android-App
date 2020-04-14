package com.food.ordering.zinger

import android.app.Application
import com.food.ordering.zinger.di.appModule
import com.food.ordering.zinger.di.networkModule
import com.food.ordering.zinger.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class zingerapp : Application() {

    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@zingerapp)
            modules(listOf(appModule, networkModule, viewModelModule))
        }
    }

}