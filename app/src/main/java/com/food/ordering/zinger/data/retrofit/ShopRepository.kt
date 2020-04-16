package com.food.ordering.zinger.data.retrofit

import com.food.ordering.zinger.data.model.LoginRequest
import retrofit2.Retrofit

class ShopRepository(private val retrofit: Retrofit) {

    suspend fun getShops(placeId: String) = retrofit.create(CustomApi::class.java).getShops(placeId)

}