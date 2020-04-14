package com.food.ordering.zinger.data.model

import java.io.Serializable

class FoodItem(var name: String, var desc: String, var price: Int, var imageUrl: String, var isVeg: Boolean) : Serializable {
    var id: String? = null
    var shopId: String? = null
    var shopName: String? = null
    var quantity = 0
}