package com.food.ordering.zinger.data.model

import com.google.gson.annotations.SerializedName

data class Response<out T>(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: T?,
    @SerializedName("message")
    val message: String
)