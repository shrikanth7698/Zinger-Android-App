package com.food.ordering.zinger.data.retrofit

import com.food.ordering.zinger.data.model.PlaceOrderRequest
import retrofit2.Retrofit

class OrderRepository(private val retrofit: Retrofit) {
    suspend fun getOrders(mobile: String, pageNum: Int, pageCount: Int) = retrofit.create(CustomApi::class.java).getOrders(
            mobile,
            pageNum,
            pageCount
    )

    suspend fun insertOrder(placeOrderRequest: PlaceOrderRequest) = retrofit.create(CustomApi::class.java).insertOrder(placeOrderRequest)
    suspend fun placeOrder(orderId: String) = retrofit.create(CustomApi::class.java).placeOrder(orderId)
}