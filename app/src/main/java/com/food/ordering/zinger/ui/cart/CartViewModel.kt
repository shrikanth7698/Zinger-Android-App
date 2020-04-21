package com.food.ordering.zinger.ui.cart

import androidx.lifecycle.*
import com.food.ordering.zinger.data.local.Resource
import com.food.ordering.zinger.data.model.PlaceOrderRequest
import com.food.ordering.zinger.data.model.Response
import com.food.ordering.zinger.data.model.VerifyOrderResponse
import com.food.ordering.zinger.data.retrofit.OrderRepository
import kotlinx.coroutines.launch
import java.net.UnknownHostException


class CartViewModel(private val orderRepository: OrderRepository) : ViewModel() {

    //verify cart items
    private val insertOrder = MutableLiveData<Resource<Response<VerifyOrderResponse>>>()
    val insertOrderStatus: LiveData<Resource<Response<VerifyOrderResponse>>>
        get() = insertOrder

    fun verifyOrder(placeOrderRequest: PlaceOrderRequest) {
        viewModelScope.launch {
            try {
                insertOrder.value = Resource.loading()
                val response = orderRepository.insertOrder(placeOrderRequest)
                if (response != null) {
                    if (response.code == 1) {
                        if (response.data!=null) {
                            insertOrder.value = Resource.success(response)
                        } else {
                            insertOrder.value = Resource.error(null, message = response.message)
                        }
                    } else {
                        insertOrder.value = Resource.error(null, response.message)
                    }
                }
            } catch (e: Exception) {
                println("verify order failed ${e.message}")
                if (e is UnknownHostException) {
                    insertOrder.value = Resource.offlineError()
                } else {
                    insertOrder.value = Resource.error(e)
                }
            }
        }
    }

}