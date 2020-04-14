package com.food.ordering.zinger.di

import com.food.ordering.zinger.ui.home.HomeViewModel
import com.food.ordering.zinger.ui.restaurant.RestaurantViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
    viewModel { RestaurantViewModel(get()) }
}