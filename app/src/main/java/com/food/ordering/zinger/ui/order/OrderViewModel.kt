package com.food.ordering.zinger.ui.order

import androidx.lifecycle.*
import com.food.ordering.zinger.data.local.Resource
import com.food.ordering.zinger.data.model.MenuItem
import com.food.ordering.zinger.data.model.OrderData
import com.food.ordering.zinger.data.retrofit.OrderRepository
import kotlinx.coroutines.launch

import java.net.UnknownHostException


class OrderViewModel(private val orderRepository: OrderRepository) : ViewModel() {

    //fetch order items
    private val performFetchOrders = MutableLiveData<Resource<List<OrderData>>>()
    val performFetchOrdersStatus: LiveData<Resource<List<OrderData>>>
        get() = performFetchOrders

    fun getMenu(mobile: String, pageNum: Int, pageCount: Int) {
        viewModelScope.launch {
            try {
                performFetchOrders.value = Resource.loading()
                val response = orderRepository.getOrders(mobile, pageNum, pageCount)
                if (response != null) {
                    if (!response.data.isNullOrEmpty()) {
                        performFetchOrders.value = Resource.success(response.data)
                    } else {
                        performFetchOrders.value = Resource.empty()
                    }
                }
            } catch (e: Exception) {
                println("fetch orders failed ${e.message}")
                if (e is UnknownHostException) {
                    performFetchOrders.value = Resource.offlineError()
                } else {
                    performFetchOrders.value = Resource.error(e)
                }
            }
        }
    }

}