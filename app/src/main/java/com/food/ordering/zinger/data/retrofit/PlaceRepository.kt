package com.food.ordering.zinger.data.retrofit

import com.food.ordering.zinger.data.model.LoginRequest
import retrofit2.Retrofit

class PlaceRepository(private val retrofit: Retrofit) {
    suspend fun getPlaces() = retrofit.create(CustomApi::class.java).getPlaceList()
}