package com.food.ordering.zinger.data.model

import com.google.gson.annotations.SerializedName


data class VerifyOrderResponse(
        @SerializedName("code")
        val code: Int,
        @SerializedName("data")
        val `data`: VerifyOrderData,
        @SerializedName("message")
        val message: String
)

data class VerifyOrderData(
        @SerializedName("orderId")
        val orderId: Int,
        @SerializedName("transactionToken")
        val transactionToken: String
)