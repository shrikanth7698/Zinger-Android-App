package com.food.ordering.zinger.data.retrofit

import com.food.ordering.zinger.data.model.*
import retrofit2.http.*

interface CustomApi {

    //USER REPO
    @POST("/user/customer")
    suspend fun login(@Body loginRequest: LoginRequest): Response<UserPlaceModel>
    @PATCH("/user/place") //This can be used for both sign-up and updating profile
    suspend fun updateUser(@Body updateUserRequest: UpdateUserRequest): Response<String>
    @PATCH("/user/notif")
    suspend fun updateFcmToken(@Body notificationTokenUpdateModel: NotificationTokenUpdate): Response<String>

    //SHOP REPO
    @GET("/shop/place/{placeId}")
    suspend fun getShops(@Path("placeId") placeId: String): Response<List<ShopConfigurationModel>>

    //PLACE REPO
    @GET("/place")
    suspend fun getPlaceList(): Response<List<PlaceModel>>

    //ITEM REPO
    @GET("/menu/{placeId}/{query}")
    suspend fun searchItems(@Path("placeId") placeId: String, @Path("query") query: String): Response<List<MenuItemModel>>
    @GET("/menu/shop/{shopId}")
    suspend fun getMenu(@Path("shopId") shopId: String): Response<List<MenuItemModel>>

    //ORDER REPO
    @GET("/order/{orderId}")
    suspend fun getOrderById(@Path("orderId") orderId: Int): Response<OrderItemListModel>
    @GET("/order/customer/{userId}/{pageNum}/{pageCount}")
    suspend fun getOrders(
            @Path("userId") id: String,
            @Path("pageNum") pageNum: Int,
            @Path("pageCount") pageCount: Int): Response<List<OrderItemListModel>>
    @POST("/order")
    suspend fun insertOrder(@Body placeOrderRequest: PlaceOrderRequest): Response<VerifyOrderResponse>
    @POST("/order/place/{orderId}")
    suspend fun placeOrder(@Path("orderId") orderId: String): Response<String>
    @PATCH("/order/rating")
    suspend fun rateOrder(@Body ratingRequest: RatingRequest): Response<String>
    @PATCH("/order/status")
    suspend fun cancelOrder(@Body orderStatusRequest: OrderStatusRequest): Response<String>


}