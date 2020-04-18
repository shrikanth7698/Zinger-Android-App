package com.food.ordering.zinger.data.model
import com.google.gson.annotations.SerializedName

data class PlaceOrderRequest(
    @SerializedName("orderItemsList")
    val orderItemsList: List<CartOrderItems>,
    @SerializedName("transactionModel")
    val transactionModel: CartTransactionModel
)

data class CartOrderItems(
    @SerializedName("itemModel")
    val itemModel: FoodItem,
    @SerializedName("price")
    val price: Int,
    @SerializedName("quantity")
    val quantity: Int
)

data class CartTransactionModel(
    @SerializedName("orderModel")
    val orderModel: CartOrderModel
)

data class FoodItem(
    @SerializedName("id")
    val id: Int
)

data class CartOrderModel(
    @SerializedName("cookingInfo")
    val cookingInfo: String?,
    @SerializedName("deliveryLocation")
    val deliveryLocation: String?,
    @SerializedName("deliveryPrice")
    val deliveryPrice: Int?,
    @SerializedName("price")
    val price: Int,
    @SerializedName("shopModel")
    val shopModel: CartShopModel,
    @SerializedName("userModel")
    val userModel: CartUserModel
)

data class CartShopModel(
    @SerializedName("id")
    val id: Int?
)

data class CartUserModel(
    @SerializedName("id")
    val id: Int?
)