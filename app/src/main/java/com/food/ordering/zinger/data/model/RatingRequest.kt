package com.food.ordering.zinger.data.model
import com.google.gson.annotations.SerializedName


data class RatingRequest(
    @SerializedName("id")
    val id: Int?,
    @SerializedName("rating")
    val rating: Double,
    @SerializedName("feedback")
    val feedback: String?,
    @SerializedName("shopModel")
    val shopModel: RatingShopModel?
)

data class RatingShopModel(
    @SerializedName("id")
    val id: Int?
)