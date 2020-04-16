package com.food.ordering.zinger.ui.home

import androidx.lifecycle.*
import com.food.ordering.zinger.data.local.Resource
import com.food.ordering.zinger.data.model.Shop
import com.food.ordering.zinger.data.model.ShopsResponseData
import com.food.ordering.zinger.data.retrofit.ShopRepository
import com.food.ordering.zinger.data.retrofit.UserRepository
import kotlinx.coroutines.launch

import java.net.UnknownHostException
import java.util.ArrayList


class HomeViewModel(private val shopRepository: ShopRepository) : ViewModel() {

    //Fetch total stats
    private val performFetchShops = MutableLiveData<Resource<List<ShopsResponseData>>>()
    val performFetchShopsStatus: LiveData<Resource<List<ShopsResponseData>>>
        get() = performFetchShops

    fun getShops(placeId: String) {
        viewModelScope.launch {
            try {
                performFetchShops.value = Resource.loading()
                val response = shopRepository.getShops(placeId)
                if(!response.data.isNullOrEmpty()){
                    response.data.let {
                        performFetchShops.value = Resource.success(it)
                    }
                }else{
                    performFetchShops.value = Resource.empty()
                }
            } catch (e: Exception) {
                println("fetch stats failed ${e.message}")
                if (e is UnknownHostException) {
                    performFetchShops.value = Resource.offlineError()
                } else {
                    performFetchShops.value = Resource.error(e)
                }
            }
        }
    }

}