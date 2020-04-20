package com.food.ordering.zinger.data.retrofit

import com.food.ordering.zinger.data.model.LoginRequest
import retrofit2.Retrofit

class ItemRepository(retrofit: Retrofit) {
    private val services = retrofit.create(CustomApi::class.java)
    suspend fun searchItems(placeId: String, query: String) = services.searchItems(placeId, query)
    suspend fun getMenu(shopId: String) = services.getMenu(shopId)
}