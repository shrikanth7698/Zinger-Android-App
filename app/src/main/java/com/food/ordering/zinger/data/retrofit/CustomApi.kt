package com.food.ordering.zinger.data.retrofit

import com.food.ordering.zinger.data.model.*
import retrofit2.http.*

interface CustomApi {

    //USER REPO
    @POST("/user/customer")
    suspend fun login(@Body loginRequest: LoginRequest): Response<UserPlaceModel>
    @PATCH("/user/place") //This can be used for both sign-up and updating profile
    suspend fun updateUser(@Body updateUserRequest: UpdateUserRequest): Response<String>

    //SHOP REPO
    @GET("/shop/place/{placeId}")
    suspend fun getShops(@Path("placeId") placeId: String): Response<List<ShopConfigurationModel>>

    //PLACE REPO
    @GET("/place")
    suspend fun getPlaceList(): Response<List<PlaceModel>>

    //ITEM REPO
    @GET("/menu/{placeId}/{query}")
    suspend fun searchItems(@Path("placeId") placeId: String, @Path("query") query: String): SearchResponse
    @GET("/menu/shop/{shopId}")
    suspend fun getMenu(@Path("shopId") shopId: String): MenuResponse

    //ORDER REPO
    @GET("/order/customer/{userId}/{pageNum}/{pageCount}")
    suspend fun getOrders(
            @Path("userId") id: String,
            @Path("pageNum") pageNum: Int,
            @Path("pageCount") pageCount: Int): OrdersResponse
    @POST("/order")
    suspend fun insertOrder(@Body placeOrderRequest: PlaceOrderRequest): VerifyOrderResponse
    @POST("/order/place/{orderId}")
    suspend fun placeOrder(@Path("orderId") orderId: String): PlaceOrderResponse
    @PATCH("/order/rating")
    suspend fun rateOrder(@Body ratingRequest: RatingRequest): RatingResponse
    @PATCH("/order/status")
    suspend fun cancelOrder(@Body orderStatusRequest: OrderStatusRequest): OrderStatusResponse


}