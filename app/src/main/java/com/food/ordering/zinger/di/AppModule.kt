package com.food.ordering.zinger.di

import com.google.gson.Gson
import com.food.ordering.zinger.data.local.PreferencesHelper
import org.koin.dsl.module

val appModule = module {

    single {
        Gson()
    }

    single {
        PreferencesHelper(get())
    }

}