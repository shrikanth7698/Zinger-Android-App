package com.food.ordering.zinger.data.model

import com.google.gson.annotations.SerializedName


data class UserPlaceModel(
        @SerializedName("placeModel")
        val placeModel: PlaceModel,
        @SerializedName("userModel")
        val userModel: UserModel
)

data class PlaceModel(
        @SerializedName("address")
        val address: String,
        @SerializedName("iconUrl")
        val iconUrl: String,
        @SerializedName("id")
        val id: Int,
        @SerializedName("name")
        val name: String
)

data class UserModel(
        @SerializedName("id")
        val userId: Int? = null,
        @SerializedName("email")
        val email: String? = null,
        @SerializedName("mobile")
        val mobile: String? = null,
        @SerializedName("name")
        val name: String? = null,
        @SerializedName("oauthId")
        var oauthId: String? = null,
        @SerializedName("role")
        val role: String? = null
)