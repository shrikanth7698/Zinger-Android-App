package com.food.ordering.zinger.data.retrofit

import com.food.ordering.zinger.data.model.LoginRequest
import com.food.ordering.zinger.data.model.UpdateUserRequest
import retrofit2.Retrofit

class UserRepository(private val retrofit: Retrofit) {

    suspend fun login(loginRequest: LoginRequest) = retrofit.create(CustomApi::class.java).login(loginRequest)
    suspend fun updateUser(updateUserRequest: UpdateUserRequest) = retrofit.create(CustomApi::class.java).updateUser(updateUserRequest)

}