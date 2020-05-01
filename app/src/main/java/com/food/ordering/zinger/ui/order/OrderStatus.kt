package com.food.ordering.zinger.ui.order

import com.food.ordering.zinger.data.model.OrderStatusModel

data class OrderStatus(
        var isDone: Boolean = false,
        var isCurrent: Boolean = false,
        var name: String,
        var orderStatusList: List<OrderStatusModel> = listOf()
)
