package com.food.ordering.zinger.di

import com.food.ordering.zinger.ui.cart.CartViewModel
import com.food.ordering.zinger.ui.contributors.ContributorViewModel
import com.food.ordering.zinger.ui.home.HomeViewModel
import com.food.ordering.zinger.ui.login.LoginViewModel
import com.food.ordering.zinger.ui.order.OrderViewModel
import com.food.ordering.zinger.ui.otp.OtpViewModel
import com.food.ordering.zinger.ui.payment.PaymentViewModel
import com.food.ordering.zinger.ui.placeorder.PlaceOrderViewModel
import com.food.ordering.zinger.ui.profile.ProfileViewModel
import com.food.ordering.zinger.ui.restaurant.RestaurantViewModel
import com.food.ordering.zinger.ui.search.SearchViewModel
import com.food.ordering.zinger.ui.signup.SignUpViewModel
import org.koin.android.viewmodel.dsl.viewModel
import org.koin.dsl.module

val viewModelModule = module {
    viewModel { HomeViewModel(get()) }
    viewModel { LoginViewModel(get()) }
    viewModel { OtpViewModel(get()) }
    viewModel { RestaurantViewModel(get()) }
    viewModel { SignUpViewModel(get(), get()) }
    viewModel { ProfileViewModel(get(), get(),get()) }
    viewModel { SearchViewModel(get(), get()) }
    viewModel { OrderViewModel(get()) }
    viewModel { CartViewModel(get()) }
    viewModel { PlaceOrderViewModel(get()) }
    viewModel { PaymentViewModel(get()) }
    viewModel { ContributorViewModel() }
}