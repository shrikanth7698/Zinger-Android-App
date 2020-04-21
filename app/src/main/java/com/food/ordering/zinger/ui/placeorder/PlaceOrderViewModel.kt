package com.food.ordering.zinger.ui.placeorder

import androidx.lifecycle.*
import com.food.ordering.zinger.data.local.Resource
import com.food.ordering.zinger.data.model.*
import com.food.ordering.zinger.data.retrofit.OrderRepository
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class PlaceOrderViewModel(private val orderRepository: OrderRepository) : ViewModel() {

    //place order
    private val placeOrder = MutableLiveData<Resource<Response<String>>>()
    val placeOrderStatus: LiveData<Resource<Response<String>>>
        get() = placeOrder

    fun placeOrder(orderId: String) {
        viewModelScope.launch {
            try {
                placeOrder.value = Resource.loading()
                val response = orderRepository.placeOrder(orderId)
                if(response!=null){
                    if(response.code==1){
                        placeOrder.value = Resource.success(response)
                    }else{
                        placeOrder.value = Resource.error(null,response.message)
                    }
                }
            } catch (e: Exception) {
                println("place order failed ${e.message}")
                if (e is UnknownHostException) {
                    placeOrder.value = Resource.offlineError()
                } else {
                    placeOrder.value = Resource.error(e)
                }
            }
        }
    }

}