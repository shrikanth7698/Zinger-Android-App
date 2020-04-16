package com.food.ordering.zinger.data.retrofit

import com.food.ordering.zinger.data.model.LoginRequest
import retrofit2.Retrofit

class OrderRepository(private val retrofit: Retrofit) {
    suspend fun getOrders(mobile: String, pageNum: Int, pageCount: Int) = retrofit.create(CustomApi::class.java).getOrders(
            mobile,
            pageNum,
            pageCount
    )
}