package com.food.ordering.zinger.data.model
import com.google.gson.annotations.SerializedName


data class SearchResponse(
    @SerializedName("code")
    val code: Int,
    @SerializedName("data")
    val `data`: List<MenuItem>,
    @SerializedName("message")
    val message: String
)

data class MenuItem(
    @SerializedName("category")
    val category: String,
    @SerializedName("id")
    val id: Int,
    @SerializedName("isAvailable")
    val isAvailable: Int,
    @SerializedName("isVeg")
    val isVeg: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("photoUrl")
    val photoUrl: String,
    @SerializedName("price")
    val price: Int,
    @SerializedName("shopModel")
    val shopModel: ShopModel
)
