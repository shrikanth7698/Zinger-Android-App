package com.food.ordering.zinger.data.model
import com.google.gson.annotations.SerializedName


data class MenuResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: List<MenuItem>,
    @SerializedName("message")
    val message: String
)
