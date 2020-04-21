package com.food.ordering.zinger.data.retrofit

import retrofit2.Retrofit

class PlaceRepository(retrofit: Retrofit) {
    private val services = retrofit.create(CustomApi::class.java)
    suspend fun getPlaces() = services.getPlaceList()
}