package com.food.ordering.zinger.ui.home

import androidx.lifecycle.*
import com.food.ordering.zinger.data.local.Resource
import com.food.ordering.zinger.data.model.ShopConfigurationModel
import com.food.ordering.zinger.data.retrofit.ShopRepository
import kotlinx.coroutines.launch

import java.net.UnknownHostException


class HomeViewModel(private val shopRepository: ShopRepository) : ViewModel() {

    //Fetch shops
    private val performFetchShops = MutableLiveData<Resource<List<ShopConfigurationModel>>>()
    val performFetchShopsStatus: LiveData<Resource<List<ShopConfigurationModel>>>
        get() = performFetchShops

    fun getShops(placeId: String) {
        viewModelScope.launch {
            try {
                performFetchShops.value = Resource.loading()
                val response = shopRepository.getShops(placeId)
                if (!response.data.isNullOrEmpty()) {
                    response.data.let { list ->
                        val shops: ArrayList<ShopConfigurationModel> = ArrayList()
                        shops.addAll(list)
                        shops.sortByDescending {
                            it.configurationModel.isOrderTaken
                        }
                        performFetchShops.value = Resource.success(shops)
                    }
                } else {
                    performFetchShops.value = Resource.empty()
                }
            } catch (e: Exception) {
                println("fetch shops failed ${e.message}")
                if (e is UnknownHostException) {
                    performFetchShops.value = Resource.offlineError()
                } else {
                    performFetchShops.value = Resource.error(e)
                }
            }
        }
    }

}