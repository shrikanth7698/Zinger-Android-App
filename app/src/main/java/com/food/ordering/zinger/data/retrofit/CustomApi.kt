package com.food.ordering.zinger.data.retrofit

import com.food.ordering.zinger.data.model.*
import retrofit2.http.*

interface CustomApi {

    //User Repo
    @POST("/user/customer")
    suspend fun login(@Body loginRequest: LoginRequest): LoginResponse
    //This can be used for both sign-up and updating profile
    @PATCH("/user/place")
    suspend fun updateUser(@Body updateUserRequest: UpdateUserRequest): UpdateUserResponse

    //Shop Repo
    @GET("/shop/place/{placeId}")
    suspend fun getShops(@Path("placeId") placeId: String): ShopsResponse

    //Place Repo
    @GET("/place")
    suspend fun getPlaceList(): PlacesResponse

    //Item Repo
    @GET("/menu/{placeId}/{query}")
    suspend fun searchItems(@Path("placeId") placeId: String, @Path("query") query: String): SearchResponse
    @GET("/menu/shop/{shopId}")
    suspend fun getMenu(@Path("shopId") shopId: String): MenuResponse


}