package com.food.ordering.zinger.data.retrofit

import com.food.ordering.zinger.data.model.LoginRequest
import com.food.ordering.zinger.data.model.NotificationTokenUpdate
import com.food.ordering.zinger.data.model.UpdateUserRequest
import retrofit2.Retrofit

class UserRepository(retrofit: Retrofit) {
    private val services = retrofit.create(CustomApi::class.java)
    suspend fun login(loginRequest: LoginRequest) = services.login(loginRequest)
    suspend fun updateUser(updateUserRequest: UpdateUserRequest) = services.updateUser(updateUserRequest)
    suspend fun updateFcmToken(notificationTokenUpdate: NotificationTokenUpdate) = services.updateFcmToken(notificationTokenUpdate)
}