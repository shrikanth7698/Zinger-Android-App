package com.food.ordering.zinger.data.model
import com.google.gson.annotations.SerializedName


data class User(
    @SerializedName("email")
    val email: String,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("oauthId")
    val oauthId: String,
    @SerializedName("role")
    val role: String
)