package com.food.ordering.zinger.ui.home

import android.os.Handler
import androidx.lifecycle.*
import com.food.ordering.zinger.data.local.Resource
import com.food.ordering.zinger.data.model.Shop
import com.food.ordering.zinger.data.retrofit.CustomAppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.net.UnknownHostException
import java.util.ArrayList


class HomeViewModel(private val productRepository: CustomAppRepository) : ViewModel() {

    //Fetch total stats
    private val performFetchShops = MutableLiveData<Resource<List<Shop>>>()
    val performFetchShopsStatus: LiveData<Resource<List<Shop>>>
        get() = performFetchShops

    fun getShops() {
        viewModelScope.launch {
            try {
                performFetchShops.value = Resource.loading()
                //val response = productRepository.getStats()
                val response = loadShops()
                performFetchShops.value = Resource.success(response)
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

    private fun loadShops():List<Shop> {
            val shops: MutableList<Shop> = ArrayList()
            var shop = Shop("1", "Sathyas Main Canteen", "Closes at 9pm", "4.2", "https://i.udemycdn.com/course/750x422/2729284_f9fa_3.jpg")
            shops.add(shop)
            shop = Shop("2", "Sathyas Mini Canteen", "Closes at 9pm", "4.0", "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcSfmlzlZEE15qncrWbPXRRgF8zXX4fllts4zQBeIXwONt3nmFXV")
            shops.add(shop)
            shop = Shop("3", "Snow Cube", "Closes at 10pm", "4.8", "https://www.seriouseats.com/2018/06/20180625-no-churn-vanilla-ice-cream-vicky-wasik-13-1500x1125.jpg")
            shops.add(shop)
            shop = Shop("4", "Thanjavur Thattu Kadai", "Closes at 8pm", "4.9", "https://encrypted-tbn0.gstatic.com/images?q=tbn%3AANd9GcR1tlxh62bkra0OXoVQPaogeF0Lc8tNqBGmW8dTO-Ekf13ExjsQ")
            shops.add(shop)
            return shops
    }

}