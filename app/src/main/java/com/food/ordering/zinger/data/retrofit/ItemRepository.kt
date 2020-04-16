package com.food.ordering.zinger.data.retrofit

import com.food.ordering.zinger.data.model.LoginRequest
import retrofit2.Retrofit

class ItemRepository(private val retrofit: Retrofit) {
    suspend fun searchItems(placeId: String, query: String) = retrofit.create(CustomApi::class.java).searchItems(placeId, query)
    suspend fun getMenu(shopId: String) = retrofit.create(CustomApi::class.java).getMenu(shopId)
}