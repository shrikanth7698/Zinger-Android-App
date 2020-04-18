package com.food.ordering.zinger.data.retrofit

import com.food.ordering.zinger.data.model.OrderStatusRequest
import com.food.ordering.zinger.data.model.PlaceOrderRequest
import com.food.ordering.zinger.data.model.RatingRequest
import retrofit2.Retrofit

class OrderRepository(private val retrofit: Retrofit) {
    suspend fun getOrders(id: String, pageNum: Int, pageCount: Int) = retrofit.create(CustomApi::class.java).getOrders(
            id,
            pageNum,
            pageCount
    )

    suspend fun insertOrder(placeOrderRequest: PlaceOrderRequest) = retrofit.create(CustomApi::class.java).insertOrder(placeOrderRequest)

    suspend fun placeOrder(orderId: String) = retrofit.create(CustomApi::class.java).placeOrder(orderId)

    suspend fun rateOrder(ratingRequest: RatingRequest) = retrofit.create(CustomApi::class.java).rateOrder(ratingRequest)

    suspend fun cancelOrder(orderStatusRequest: OrderStatusRequest) = retrofit.create(CustomApi::class.java).cancelOrder(orderStatusRequest)
}