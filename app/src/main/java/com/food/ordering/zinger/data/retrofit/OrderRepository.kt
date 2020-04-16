package com.food.ordering.zinger.data.retrofit

import com.food.ordering.zinger.data.model.LoginRequest
import retrofit2.Retrofit

class OrderRepository(private val retrofit: Retrofit) {
    suspend fun login(loginRequest: LoginRequest) = retrofit.create(CustomApi::class.java).login(loginRequest)
}