package com.food.ordering.zinger.data.model
import com.google.gson.annotations.SerializedName


data class PlacesResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: List<PlaceModel>?,
    @SerializedName("message")
    val message: String
)