package com.food.ordering.zinger.data.model
import com.google.gson.annotations.SerializedName

data class ShopConfigurationModel(
    @SerializedName("configurationModel")
    val configurationModel: ConfigurationModel,
    @SerializedName("ratingModel")
    val ratingModel: RatingModel,
    @SerializedName("shopModel")
    val shopModel: ShopModel
)

data class ConfigurationModel(
    @SerializedName("deliveryPrice")
    val deliveryPrice: Double?,
    @SerializedName("isDeliveryAvailable")
    val isDeliveryAvailable: Int,
    @SerializedName("isOrderTaken")
    val isOrderTaken: Int,
    @SerializedName("merchantId")
    val merchantId: String,
    @SerializedName("shopModel")
    val shopModel: ShopModel?
)

data class RatingModel(
    @SerializedName("rating")
    val rating: Double,
    @SerializedName("shopModel")
    val shopModel: ShopModel?,
    @SerializedName("userCount")
    val userCount: Int
)

data class ShopModel(
    @SerializedName("closingTime")
    val closingTime: String,
    @SerializedName("coverUrls")
    val coverUrls: List<String>,
    @SerializedName("id")
    val id: Int,
    @SerializedName("mobile")
    val mobile: String,
    @SerializedName("name")
    var name: String,
    @SerializedName("openingTime")
    val openingTime: String,
    @SerializedName("photoUrl")
    val photoUrl: String,
    @SerializedName("placeModel")
    val placeModel: PlaceModel?
)