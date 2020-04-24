package com.food.ordering.zinger.ui.order

data class OrderStatus(
        var isDone: Boolean = false,
        var isCurrent: Boolean = false,
        var name: String,
        var statusImage: Int = -1
)
