package com.food.ordering.zinger.ui.payment

import androidx.lifecycle.*
import com.food.ordering.zinger.data.local.Resource
import com.food.ordering.zinger.data.model.PlaceOrderRequest
import com.food.ordering.zinger.data.model.Response
import com.food.ordering.zinger.data.model.VerifyOrderResponse
import com.food.ordering.zinger.data.retrofit.OrderRepository
import kotlinx.coroutines.launch
import java.net.UnknownHostException

class PaymentViewModel(private val orderRepository: OrderRepository) : ViewModel() {

    //verify order
    private val insertOrder = MutableLiveData<Resource<Response<VerifyOrderResponse>>>()
    val insertOrderStatus: LiveData<Resource<Response<VerifyOrderResponse>>>
        get() = insertOrder

    fun placeOrder(placeOrderRequest: PlaceOrderRequest) {
        viewModelScope.launch {
            try {
                insertOrder.value = Resource.loading()
                val response = orderRepository.insertOrder(placeOrderRequest)
                if(response!=null){
                    if(response.code==1){
                        insertOrder.value = Resource.success(response)
                    }else{
                        insertOrder.value = Resource.error(null,response.message)
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