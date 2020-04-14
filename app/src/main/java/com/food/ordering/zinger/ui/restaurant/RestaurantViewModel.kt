package com.food.ordering.zinger.ui.restaurant

import android.os.Handler
import androidx.lifecycle.*
import com.food.ordering.zinger.data.local.Resource
import com.food.ordering.zinger.data.model.FoodItem
import com.food.ordering.zinger.data.model.Shop
import com.food.ordering.zinger.data.retrofit.CustomAppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

import java.net.UnknownHostException
import java.util.ArrayList


class RestaurantViewModel(private val productRepository: CustomAppRepository) : ViewModel() {

    //Fetch total stats
    private val performFetchShops = MutableLiveData<Resource<List<FoodItem>>>()
    val performFetchShopsStatus: LiveData<Resource<List<FoodItem>>>
        get() = performFetchShops

    fun getMenu(shop: Shop) {
        viewModelScope.launch {
            try {
                performFetchShops.value = Resource.loading()
                //val response = productRepository.getStats()
                val response = loadMenu(shop)
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

    private fun loadMenu(shop: Shop): List<FoodItem> {
        val foodItems: MutableList<FoodItem> = ArrayList()
        var foodItem = FoodItem("Plain Dosa", "South Indian", 30, "https://i2.wp.com/www.vegrecipesofindia.com/wp-content/uploads/2018/11/plain-dosa-recipe-1a.jpg", true)
        foodItem.id = "1"
        foodItem.shopId = shop.id
        foodItem.shopName = shop.name
        foodItems.add(foodItem)
        foodItem = FoodItem("Pongal", "South Indian", 25, "https://recipes.timesofindia.com/thumb/67488780.cms?width=1200&height=1200", true)
        foodItem.id = "2"
        foodItem.shopId = shop.id
        foodItem.shopName = shop.name
        foodItems.add(foodItem)
        foodItem = FoodItem("Veg Noodles", "Chinese", 55, "https://i.ndtvimg.com/i/2016-06/noodles-625_625x350_41466064269.jpg", true)
        foodItem.id = "3"
        foodItem.shopId = shop.id
        foodItem.shopName = shop.name
        foodItems.add(foodItem)
        foodItem = FoodItem("Chicken biriyani", "South Indian", 80, "https://food.fnr.sndimg.com/content/dam/images/food/fullset/2019/9/9/0/FNK_the-best-biryani_H.JPG.rend.hgtvcom.616.462.suffix/1568143107638.jpeg", false)
        foodItem.id = "4"
        foodItem.shopId = shop.id
        foodItem.shopName = shop.name
        foodItems.add(foodItem)
        foodItem = FoodItem("Chappathi (2 pcs)", "South Indian", 30, "https://1.bp.blogspot.com/-zmtVTuvvEIM/VtPUnCv42ZI/AAAAAAAAFYE/uvwEYpxz7ns/s1600/Home%2Bmade%2Bchapathi.JPG", true)
        foodItem.id = "5"
        foodItem.shopId = shop.id
        foodItem.shopName = shop.name
        foodItems.add(foodItem)
        foodItem = FoodItem("Parotta (2 pcs)", "South Indian", 30, "https://d3tfnts8u422oi.cloudfront.net/386x386/nilanjana-bhattacharjee-mitra20170314080156704.jpg", true)
        foodItem.id = "6"
        foodItem.shopId = shop.id
        foodItem.shopName = shop.name
        foodItems.add(foodItem)
        foodItem = FoodItem("Iced Tea", "Beverages", 25, "https://cdn.apartmenttherapy.info/image/fetch/f_jpg,q_auto:eco,c_fill,g_auto,w_1500,ar_1:1/https%3A%2F%2Fstorage.googleapis.com%2Fgen-atmedia%2F3%2F2017%2F04%2Fe1f4d85bbf9b958a04d5b93eaf279e4cdb2a6504.jpeg", true)
        foodItem.id = "7"
        foodItem.shopId = shop.id
        foodItem.shopName = shop.name
        foodItems.add(foodItem)
        foodItem = FoodItem("Mint Lime", "Beverages", 25, "https://www.simplyrecipes.com/wp-content/uploads/2017/06/mojito-vertical-a-1800.jpg", true)
        foodItem.id = "8"
        foodItem.shopId = shop.id
        foodItem.shopName = shop.name
        foodItems.add(foodItem)
        return foodItems
    }

}